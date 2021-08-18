package uce.proyect.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarnetResponse {

    private String centroVacunacion;
    private String estudiante;
    private String cedula;
    private int fechaNacimiento;
    private String nombreVacuna;
    private LocalDate fechaPrimeraDosis;
    private LocalDate fechaSegundasDosis;
    private String vacunadorPrimeraDosis;
    private String vacunadorSegundaDosis;
    private String primeraDosis;
    private String segundaDosis;
    private String loteDosisUno;
    private String loteDosisDos;
}
