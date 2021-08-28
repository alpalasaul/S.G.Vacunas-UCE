package uce.proyect.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
public class Plan {
    @Id
    private String _id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String centroVacunacion;
    private String facultad;
    private int personasVacunadas;
    private String fase;
    private boolean completo;
}
