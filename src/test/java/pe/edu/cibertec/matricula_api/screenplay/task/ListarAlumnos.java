package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ListarAlumnos implements Task {

    private final String token;

    public ListarAlumnos(String token) {
        this.token = token;
    }

    public static ListarAlumnos conTokenActual() {
        return instrumented(ListarAlumnos.class, (String) null);
    }

    public static ListarAlumnos conToken(String token) {
        return instrumented(ListarAlumnos.class, token);
    }

    @Override
    @Step("{0} intenta listar todos los alumnos")
    public <T extends Actor> void performAs(T actor) {
        String tokenAUsar = this.token != null ? this.token : actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/alumnos")
                        .with(req -> {
                            if (tokenAUsar != null) {
                                req.header("Authorization", "Bearer " + tokenAUsar);
                            }
                            return req;
                        })
        );
    }
}
