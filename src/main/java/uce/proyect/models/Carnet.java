package uce.proyect.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
public class Carnet {
    @Id
    private String _id;

    private String centroVacunacion;
    @Indexed(unique = true)
    private String estudiante;
    private String nombreVacuna;
    private LocalDate fechaPrimeraDosis;
    private LocalDate fechaSegundasDosis;
    private String vacunadorPrimeraDosis;
    private String vacunadorSegundaDosis;
    private boolean primeraDosis;
    private boolean segundaDosis;
    private String loteDosisUno;
    private String loteDosisDos;
    private boolean inoculacionVoluntaria;
}
