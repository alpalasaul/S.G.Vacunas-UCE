package uce.proyect.models;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document
@Data
public class User {
    @Id
    private String id;
    private String nombreUsuario;
    private String contrasena;
}
