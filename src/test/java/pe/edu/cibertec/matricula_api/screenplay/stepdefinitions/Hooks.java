package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    @Before
    public void beforeScenario() {
        OnStage.setTheStage(new OnlineCast());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("==================================================");
        System.out.println("Alumno: Brayan Jair Quispe Paucar");
        System.out.println("Código: i202116198");
        System.out.println("Fecha y hora: " + LocalDateTime.now().format(dtf));
        System.out.println("==================================================");
    }
}
