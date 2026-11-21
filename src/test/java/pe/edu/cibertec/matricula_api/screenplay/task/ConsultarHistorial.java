package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ConsultarHistorial implements Task {

    private final String codigoAlumno;

    public ConsultarHistorial(String codigoAlumno) {
        this.codigoAlumno = codigoAlumno;
    }

    public static ConsultarHistorial delAlumno(String codigoAlumno) {
        return instrumented(ConsultarHistorial.class, codigoAlumno);
    }

    public static ConsultarHistorial deSuHistorial() {
        return instrumented(ConsultarHistorial.class, (String) null);
    }

    @Override
    @Step("{0} consulta el historial académico")
    public <T extends Actor> void performAs(T actor) {
        String alumno = this.codigoAlumno != null ? this.codigoAlumno : actor.recall("username");
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/alumnos/" + alumno + "/historial")
                        .with(req -> {
                            if (token != null) {
                                req.header("Authorization", "Bearer " + token);
                            }
                            return req;
                        })
        );
    }
}
