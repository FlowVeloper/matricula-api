package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class ElHistorial {

    public static Question<Integer> creditosAprobados() {
        return Question.about("los créditos aprobados")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getInt("creditosAprobados"));
    }

    public static Question<Double> promedioPonderado() {
        return Question.about("el promedio ponderado")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getDouble("promedioPonderado"));
    }

    public static Question<String> condicionDelCurso(String codigoCurso) {
        return Question.about("la condición del curso " + codigoCurso)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getString("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.condicion"));
    }
}
