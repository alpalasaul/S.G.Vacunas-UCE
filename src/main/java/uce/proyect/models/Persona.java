package uce.proyect.models;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDate;

@Document
@Data
public class Persona {
    @Id
    private String id;
    private String cedula;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String telefono;
    @Indexed(unique = true)
    private String correo;
    private String usuario;
    private String genero;
    private boolean esControlado;
}
