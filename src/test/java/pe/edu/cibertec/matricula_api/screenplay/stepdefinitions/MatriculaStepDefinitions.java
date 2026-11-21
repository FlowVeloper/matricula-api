package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.model.environment.SystemEnvironmentVariables;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MatriculaStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Dado("que el alumno {string} inicia sesión con la contraseña {string}")
    public void alumnoIniciaSesion(String alumno, String password) {
        theActorCalled(alumno).whoCan(CallAnApi.at(baseUrl));

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("username", alumno, "password", password)))
        );

        String token = SerenityRest.lastResponse().jsonPath().getString("accessToken");
        theActorInTheSpotlight().remember("token", token);
        theActorInTheSpotlight().remember("codigoAlumno", alumno);
    }

    @Cuando("cotiza el curso {string}")
    public void cotizaElCurso(String curso) {
        String token = theActorInTheSpotlight().recall("token");
        String alumno = theActorInTheSpotlight().recall("codigoAlumno");

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/matriculas/cotizacion")
                        .with(req -> req.contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(Map.of("codigoAlumno", alumno, "cursos", List.of(curso))))
        );
    }

    @Y("^(?:Y )?el monto total cotizado es ([0-9]+(?:[.,][0-9]+)?)$")
    public void elMontoTotalCotizadoEs(String montoStr) {
        double totalEsperado = Double.parseDouble(montoStr.replace(',', '.'));
        double totalActual = SerenityRest.lastResponse().jsonPath().getDouble("total");
        assertThat("El total cotizado no coincide", totalActual, equalTo(totalEsperado));
    }

    @Cuando("se matricula en el curso {string}")
    public void seMatriculaEnElCurso(String curso) {
        String token = theActorInTheSpotlight().recall("token");
        String alumno = theActorInTheSpotlight().recall("codigoAlumno");

        theActorInTheSpotlight().attemptsTo(
                Post.to("/api/matriculas")
                        .with(req -> req.contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(Map.of("codigoAlumno", alumno, "codigoCurso", curso)))
        );

        Object idObj = SerenityRest.lastResponse().jsonPath().get("id");
        if (idObj != null) {
            theActorInTheSpotlight().remember("idMatricula", Long.valueOf(idObj.toString()));
        }
    }

    @Y("el estado de la matrícula es {string}")
    public void elEstadoDeLaMatriculaEs(String estadoEsperado) {
        String estadoActual = SerenityRest.lastResponse().jsonPath().getString("estado");
        assertThat("El estado de la matrícula no coincide", estadoActual, equalTo(estadoEsperado));
    }

    @Y("^(?:Y )?el monto de la matrícula es ([0-9]+(?:[.,][0-9]+)?)$")
    public void elMontoDeLaMatriculaEs(String montoStr) {
        double montoEsperado = Double.parseDouble(montoStr.replace(',', '.'));
        double montoActual = SerenityRest.lastResponse().jsonPath().getDouble("monto");
        assertThat("El monto de la matrícula no coincide", montoActual, equalTo(montoEsperado));
    }

    @Cuando("paga la matrícula con el método {string}")
    public void pagaLaMatriculaConElMetodo(String metodo) {
        String token = theActorInTheSpotlight().recall("token");
        Long idMatricula = theActorInTheSpotlight().recall("idMatricula");

        theActorInTheSpotlight().attemptsTo(
                Patch.to("/api/matriculas/" + idMatricula + "/pagar")
                        .with(req -> {
                            req.contentType(ContentType.JSON)
                               .header("Authorization", "Bearer " + token);
                            if (metodo != null && !metodo.equalsIgnoreCase("Sin cuerpo en la petición")) {
                                req.body(Map.of("metodo", metodo));
                            }
                            return req;
                        })
        );
    }

    @Y("la fecha de pago está presente")
    public void laFechaDePagoEstaPresente() {
        String fechaPago = SerenityRest.lastResponse().jsonPath().getString("fechaPago");
        assertThat("La fecha de pago debe tener valor", fechaPago, notNullValue());
    }

    @Y("en la lista de pagos existe el pago de la matrícula con método {string} y formato de operación válido")
    public void enLaListaDePagosExisteElPago(String metodoEsperado) {
        String token = theActorInTheSpotlight().recall("token");
        Long idMatricula = theActorInTheSpotlight().recall("idMatricula");

        theActorInTheSpotlight().attemptsTo(
                Get.resource("/api/pagos")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );

        List<Map<String, Object>> pagos = SerenityRest.lastResponse().jsonPath().getList("$");
        Map<String, Object> pagoEncontrado = pagos.stream()
                .filter(p -> {
                    Object idMat = p.get("idMatricula");
                    return idMat != null && Long.valueOf(idMat.toString()).equals(idMatricula);
                })
                .findFirst()
                .orElse(null);

        assertThat("No se encontró el pago para la matrícula " + idMatricula, pagoEncontrado, notNullValue());
        assertThat("El método de pago no coincide", pagoEncontrado.get("metodo").toString(), equalTo(metodoEsperado));

        String numeroOperacion = (String) pagoEncontrado.get("numeroOperacion");
        assertThat("El número de operación no cumple el formato OP-000000",
                numeroOperacion, matchesPattern("^OP-\\d{6}$"));
    }

    @Cuando("intenta consultar la constancia del alumno {string}")
    public void intentaConsultarLaConstanciaDelAlumno(String codigoAlumno) {
        String token = theActorInTheSpotlight().recall("token");

        theActorInTheSpotlight().attemptsTo(
                Get.resource("/api/alumnos/" + codigoAlumno + "/constancia")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }
}
