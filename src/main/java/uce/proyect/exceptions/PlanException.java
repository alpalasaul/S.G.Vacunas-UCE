package uce.proyect.exceptions;

import lombok.Data;

import java.time.LocalDate;

/*
Excepcion que permite manejar los errores debido a los planes de vacunación
* */
@Data
public class PlanException extends RuntimeException {
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private Integer personasVacunadas;
    private String fase;

    public PlanException(String mensaje) {
        super(mensaje);
    }

    public PlanException(String message, LocalDate fechaInicio, LocalDate fechaFinal, Integer personasVacunadas, String fase) {
        super(message);
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.personasVacunadas = personasVacunadas;
        this.fase = fase;
    }
}
