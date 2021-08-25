package uce.proyect.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Facultad {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String nombre;
    private List<String> carreras; // Toca aplicar la vieja confiable :'v
}
