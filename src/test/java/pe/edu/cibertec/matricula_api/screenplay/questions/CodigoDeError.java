package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class CodigoDeError implements Question<String> {

    public static CodigoDeError enLaRespuesta() {
        return new CodigoDeError();
    }

    public static Question<String> delCuerpo() {
        return Question.about("el código de error")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getString("codigo"));
    }

    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getString("codigo");
    }
}
