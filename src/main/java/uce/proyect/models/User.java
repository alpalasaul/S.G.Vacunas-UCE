package uce.proyect.models;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;

@Document
@Data
public class User {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String nombreUsuario;
    private String contrasena;
    private List<String> roles;
}
