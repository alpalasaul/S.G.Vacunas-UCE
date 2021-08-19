package uce.proyect.service.agreement;

import net.sf.jasperreports.engine.JRException;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;

import java.io.FileNotFoundException;

public interface CarnetService extends CoreService<Carnet> {

    Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException;

    byte[] generarPdf(String estudiante) throws FileNotFoundException, JRException;
}
