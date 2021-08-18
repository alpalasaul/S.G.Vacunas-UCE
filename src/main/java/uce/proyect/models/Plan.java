package uce.proyect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    private String _id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String facultad;
    private String carrera;
    private int personasVacunadas;
    private String fase;
    private boolean completo;
}
