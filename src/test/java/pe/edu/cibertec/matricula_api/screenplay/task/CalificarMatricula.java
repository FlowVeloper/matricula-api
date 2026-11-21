package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CalificarMatricula implements Task {

    private final Long idMatricula;
    private final Double nota;

    public CalificarMatricula(Long idMatricula, Double nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conNota(Long idMatricula, Double nota) {
        return instrumented(CalificarMatricula.class, idMatricula, nota);
    }

    public static CalificarMatricula conNota(Double nota) {
        return instrumented(CalificarMatricula.class, (Long) null, nota);
    }

    @Override
    @Step("{0} califica la matrícula con nota #nota")
    public <T extends Actor> void performAs(T actor) {
        Long id = this.idMatricula != null ? this.idMatricula : actor.recall("idMatricula");
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Patch.to("/api/matriculas/" + id + "/nota")
                        .with(req -> {
                            req.contentType(ContentType.JSON);
                            if (token != null) {
                                req.header("Authorization", "Bearer " + token);
                            }
                            req.body(Map.of("nota", nota));
                            return req;
                        })
        );
    }
}
