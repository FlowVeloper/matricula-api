package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import pe.edu.cibertec.matricula_api.screenplay.questions.CodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.*;

import java.util.List;
import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class SeguridadStepDefinitions {

    private final String baseUrl = EnvironmentSpecificConfiguration
            .from(SystemEnvironmentVariables.currentEnvironmentVariables())
            .getProperty("restapi.baseurl");

    @Dado("que el alumno {string} inicia sesión con la contraseña {string} para verificar seguridad")
    public void elAlumnoIniciaSesionParaVerificarSeguridad(String alumno, String password) {
        theActorCalled(alumno).whoCan(CallAnApi.at(baseUrl));
        theActorInTheSpotlight().attemptsTo(IniciarSesion.conCredenciales(alumno, password));
    }

    @Cuando("renueva sus tokens con el refresh token")
    public void renuevaSusTokensConElRefreshToken() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.usandoRefreshTokenGuardado());
    }

    @Entonces("la renovación inicial responde 200 con tokens nuevos")
    public void laRenovacionInicialResponde200ConTokensNuevos() {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)));
        assertThat("El nuevo access token no debe ser nulo",
                theActorInTheSpotlight().recall("accessTokenNuevo"), notNullValue());
        assertThat("El nuevo refresh token no debe ser nulo",
                theActorInTheSpotlight().recall("refreshTokenNuevo"), notNullValue());
    }

    @Cuando("intenta usar otra vez el refresh token original")
    public void intentaUsarOtraVezElRefreshTokenOriginal() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.reintentandoConRefreshTokenOriginal());
    }

    @Entonces("el reuso es rechazado con 401 y código de error {string}")
    public void elReusoEsRechazadoCon401YCodigoDeError(String codigoEsperado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(401)));
        theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoEsperado)));
    }

    @Y("la respuesta cumple con el esquema de error")
    public void laRespuestaCumpleConElEsquemaDeError() {
        theActorInTheSpotlight().should(
                seeThatResponse("cumple el esquema schemas/error.json",
                        res -> res.body(matchesJsonSchemaInClasspath("schemas/error.json"))
                )
        );
    }

    @Cuando("intenta consultar su perfil con el nuevo access token")
    public void intentaConsultarSuPerfilConElNuevoAccessToken() {
        String nuevoAccessToken = theActorInTheSpotlight().recall("accessTokenNuevo");
        theActorInTheSpotlight().attemptsTo(ConsultarPerfil.conToken(nuevoAccessToken));
    }

    @Entonces("la consulta de perfil es rechazada con 401 y código de error {string}")
    public void laConsultaDePerfilEsRechazadaCon401YCodigoDeError(String codigoEsperado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(401)));
        theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoEsperado)));
    }

    @Cuando("cierra su sesión")
    public void cierraSuSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.deLaSesionActual());
    }

    @Entonces("el logout responde con código 204")
    public void elLogoutRespondeConCodigo204() {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(204)));
    }

    @Cuando("intenta consultar su perfil con el mismo access token")
    public void intentaConsultarSuPerfilConElMismoAccessToken() {
        theActorInTheSpotlight().attemptsTo(ConsultarPerfil.deLaSesionActual());
    }

    @Cuando("el alumno {string} ingresa credenciales fallidas 5 veces:")
    public void elAlumnoIngresaCredencialesFallidas5Veces(String alumno, DataTable dataTable) {
        theActorCalled(alumno).whoCan(CallAnApi.at(baseUrl));
        List<Map<String, String>> filas = dataTable.asMaps();

        for (Map<String, String> fila : filas) {
            String clave = fila.get("clave");
            int statusEsperado = Integer.parseInt(fila.get("status"));
            String codigoEsperado = fila.get("codigo");

            theActorInTheSpotlight().attemptsTo(IniciarSesion.conCredenciales(alumno, clave));
            theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(statusEsperado)));
            theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoEsperado)));
            theActorInTheSpotlight().should(
                    seeThatResponse("cumple el esquema schemas/error.json en intento fallido",
                            res -> res.body(matchesJsonSchemaInClasspath("schemas/error.json"))
                    )
            );
        }
    }

    @Cuando("intenta ingresar con la clave correcta {string}")
    public void intentaIngresarConLaClaveCorrecta(String claveCorrecta) {
        String alumno = theActorInTheSpotlight().getName();
        theActorInTheSpotlight().attemptsTo(IniciarSesion.conCredenciales(alumno, claveCorrecta));
    }

    @Entonces("el login es rechazado con 423 y código de error {string}")
    public void elLoginEsRechazadoCon423YCodigoDeError(String codigoEsperado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(423)));
        theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoEsperado)));
    }

    @Cuando("intenta listar todos los alumnos con GET {string}")
    public void intentaListarTodosLosAlumnosConGET(String recurso) {
        theActorInTheSpotlight().attemptsTo(ListarAlumnos.conTokenActual());
    }

    @Entonces("la consulta es rechazada con 403 y código de error {string}")
    public void laConsultaEsRechazadaCon403YCodigoDeError(String codigoEsperado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(403)));
        theActorInTheSpotlight().should(seeThat(CodigoDeError.delCuerpo(), equalTo(codigoEsperado)));
    }
}
