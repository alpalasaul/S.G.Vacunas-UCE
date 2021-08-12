package uce.proyect.models;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
public class Administrador extends Persona {
    @Indexed(unique = true)
    private String identificadorAdmin;
}
