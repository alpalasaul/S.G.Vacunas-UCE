package uce.proyect.models;

import lombok.Data;

@Data
public class Estudiante extends Persona{
    private int semestre;
    private String facultad; // Identificador del objeto facultad para obtener la carrera
}
