package uce.proyect.exceptions;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarnetException extends RuntimeException {
    private LocalDate fechaPrimeraDosis;
    private LocalDate estimacionfechaSegundaDosis;
    private String tipoVacuna;

    public CarnetException(String mensaje) {
        super(mensaje);
    }

    public CarnetException(String message, LocalDate fechaPrimeraDosis, LocalDate estimacionfechaSegundaDosis, String tipoVacuna) {
        super(message);
        this.fechaPrimeraDosis = fechaPrimeraDosis;
        this.estimacionfechaSegundaDosis = estimacionfechaSegundaDosis;
        this.tipoVacuna = tipoVacuna;
    }
}
