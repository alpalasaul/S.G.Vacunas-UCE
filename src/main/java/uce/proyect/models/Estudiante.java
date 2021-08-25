package uce.proyect.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Estudiante extends Persona {
    @JsonIgnore
    private int semestre;
    @JsonIgnore
    private String facultad; // Identificador del objeto facultad para obtener la carrera
}
