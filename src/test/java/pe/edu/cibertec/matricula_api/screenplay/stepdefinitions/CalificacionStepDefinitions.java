package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.CodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.Matchers.equalTo;

public class CalificacionStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Dado("que la alumna {string} obtiene su matrícula del curso {string}")
    public void laAlumnaObtieneSuMatriculaDelCurso(String codigoAlumna, String codigoCurso) {
        Actor alumna = theActorCalled(codigoAlumna).whoCan(CallAnApi.at(baseUrl));
        alumna.attemptsTo(
                IniciarSesion.conCredenciales(codigoAlumna, "Cibertec2026"),
                ObtenerMatricula.delCurso(codigoCurso)
        );
    }

    @Y("el docente {string} inicia sesión con la clave {string}")
    public void elDocenteIniciaSesionConLaClave(String codigoDocente, String password) {
        Actor alumna = theActorCalled("i202610005");
        Long idMatricula = alumna.recall("idMatricula");

        Actor docente = theActorCalled(codigoDocente).whoCan(CallAnApi.at(baseUrl));
        docente.remember("idMatricula", idMatricula);
        docente.attemptsTo(
                IniciarSesion.conCredenciales(codigoDocente, password)
        );
    }

    @Cuando("el docente registra la nota {double} en la matrícula")
    public void elDocenteRegistraLaNotaEnLaMatricula(Double nota) {
        theActorInTheSpotlight().attemptsTo(
                CalificarMatricula.conNota(nota)
        );
    }

    @Cuando("el docente intenta registrar la nota {double} en la matrícula")
    public void elDocenteIntentaRegistrarLaNotaEnLaMatricula(Double nota) {
        theActorInTheSpotlight().attemptsTo(
                CalificarMatricula.conNota(nota)
        );
    }

    @Entonces("la nota es registrada con código 200")
    public void laNotaEsRegistradaConCodigo200() {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)));
    }

    @Cuando("la alumna consulta su historial académico")
    public void laAlumnaConsultaSuHistorialAcademico() {
        theActorCalled("i202610005").attemptsTo(
                ConsultarHistorial.delAlumno("i202610005")
        );
    }

    @Entonces("la condición del curso {string} es {string}")
    public void laCondicionDelCursoEs(String codigoCurso, String condicion) {
        theActorInTheSpotlight().should(seeThat(ElHistorial.condicionDelCurso(codigoCurso), equalTo(condicion)));
    }

    @Y("los créditos aprobados acumulados son {int}")
    public void losCreditosAprobadosAcumuladosSon(int creditosAprobados) {
        theActorInTheSpotlight().should(seeThat(ElHistorial.creditosAprobados(), equalTo(creditosAprobados)));
    }

    @Y("^(?:Y )?el promedio ponderado calculado es ([0-9]+(?:[.,][0-9]+)?)$")
    public void elPromedioPonderadoCalculadoEs(String promedioStr) {
        double esperado = Double.parseDouble(promedioStr.replace(',', '.'));
        theActorInTheSpotlight().should(seeThat(ElHistorial.promedioPonderado(), equalTo(esperado)));
    }

    @Y("la respuesta cumple con el esquema de historial")
    public void laRespuestaCumpleConElEsquemaDeHistorial() {
        theActorInTheSpotlight().should(
                seeThatResponse("cumple el esquema schemas/historial.json",
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/historial.json"))
                )
        );
    }

    @Entonces("la calificación es rechazada con código {int} y código de error {string}")
    public void laCalificacionEsRechazadaConCodigoYCodigoDeError(int statusEsperado, String codigoErrorEsperado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(statusEsperado)));
        theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoErrorEsperado)));
    }
}
