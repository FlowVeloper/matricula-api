package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CerrarSesion implements Task {

    private final String token;

    public CerrarSesion(String token) {
        this.token = token;
    }

    public static CerrarSesion deLaSesionActual() {
        return instrumented(CerrarSesion.class, (String) null);
    }

    public static CerrarSesion conToken(String token) {
        return instrumented(CerrarSesion.class, token);
    }

    @Override
    @Step("{0} cierra sesion")
    public <T extends Actor> void performAs(T actor) {
        String tokenAUsar = this.token != null ? this.token : actor.recall("accessToken");
        actor.attemptsTo(
                Post.to("/api/auth/logout")
                        .with(req -> {
                            req.contentType(ContentType.JSON);
                            if (tokenAUsar != null) {
                                req.header("Authorization", "Bearer " + tokenAUsar);
                            }
                            return req;
                        })
        );
    }
}
