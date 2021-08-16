package uce.proyect.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Carnet {

    @Id
    private String _id;

    private String centroVacunacion;
    private String estudiante;
    private String nombreVacuna;
    private Date fechaPrimeraDosis;
    private Date fechaSegundasDosis;
    private String vacunadorPrimeraDosis;
    private String vacunadorSegundaDosis;
    private boolean primeraDosis;
    private boolean segundaDosis;
    private String loteDosisUno;
    private String loteDosisDos;

}
