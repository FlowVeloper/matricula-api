package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ObtenerMatricula implements Task {

    private final String codigoCurso;

    public ObtenerMatricula(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }

    public static ObtenerMatricula delCurso(String codigoCurso) {
        return instrumented(ObtenerMatricula.class, codigoCurso);
    }

    @Override
    @Step("{0} obtiene la matrícula del curso #codigoCurso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );

        List<Map<String, Object>> matriculas = SerenityRest.lastResponse().jsonPath().getList("$");
        Map<String, Object> matriculaEncontrada = matriculas.stream()
                .filter(m -> codigoCurso.equals(m.get("codigoCurso")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró matrícula para el curso " + codigoCurso));

        Long idMatricula = Long.valueOf(matriculaEncontrada.get("id").toString());
        actor.remember("idMatricula", idMatricula);
        actor.remember("cursoActual", codigoCurso);
    }
}
