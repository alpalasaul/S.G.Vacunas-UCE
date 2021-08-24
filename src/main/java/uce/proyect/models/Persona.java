package uce.proyect.models;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.persistence.Id;
import java.time.LocalDate;

@Data
public class Persona {
    @Id
    private String _id;
    @Indexed(unique = true)
    private String cedula;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String telefono;
    @Indexed(unique = true)
    private String correo;
    private String usuario; // Como en el usuario ya no se repite este campo no es necesario volverlo a indexar
    private String genero;
    private boolean esControlador;
}
