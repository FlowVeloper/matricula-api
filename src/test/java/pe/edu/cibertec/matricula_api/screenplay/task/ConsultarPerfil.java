package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ConsultarPerfil implements Task {

    private final String token;

    public ConsultarPerfil(String token) {
        this.token = token;
    }

    public static ConsultarPerfil conToken(String token) {
        return instrumented(ConsultarPerfil.class, token);
    }

    public static ConsultarPerfil deLaSesionActual() {
        return instrumented(ConsultarPerfil.class, (String) null);
    }

    @Override
    @Step("{0} consulta su perfil")
    public <T extends Actor> void performAs(T actor) {
        String tokenAUsar = this.token != null ? this.token : actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> {
                            if (tokenAUsar != null) {
                                req.header("Authorization", "Bearer " + tokenAUsar);
                            }
                            return req;
                        })
        );
    }
}
