package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class RenovarToken implements Task {

    private final String tokenARenovar;
    private final boolean guardarNuevos;

    public RenovarToken(String tokenARenovar, boolean guardarNuevos) {
        this.tokenARenovar = tokenARenovar;
        this.guardarNuevos = guardarNuevos;
    }

    public static RenovarToken usandoRefreshTokenGuardado() {
        return instrumented(RenovarToken.class, (String) null, true);
    }

    public static RenovarToken reintentandoConRefreshTokenOriginal() {
        return instrumented(RenovarToken.class, (String) null, false);
    }

    public static RenovarToken conRefreshToken(String refreshToken) {
        return instrumented(RenovarToken.class, refreshToken, true);
    }

    public static RenovarToken intentarConRefreshToken(String refreshToken) {
        return instrumented(RenovarToken.class, refreshToken, false);
    }

    @Override
    @Step("{0} renueva tokens con refresh token")
    public <T extends Actor> void performAs(T actor) {
        String token = this.tokenARenovar != null ? this.tokenARenovar : actor.recall("refreshToken");

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", token != null ? token : "")))
        );

        if (guardarNuevos && SerenityRest.lastResponse().statusCode() == 200) {
            String nuevoAccess = SerenityRest.lastResponse().jsonPath().getString("accessToken");
            String nuevoRefresh = SerenityRest.lastResponse().jsonPath().getString("refreshToken");
            actor.remember("accessTokenNuevo", nuevoAccess);
            actor.remember("refreshTokenNuevo", nuevoRefresh);
        }
    }
}
