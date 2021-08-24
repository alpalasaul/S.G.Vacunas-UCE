package uce.proyect.service.agreement;

import net.sf.jasperreports.engine.JRException;
import org.json.JSONObject;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;

import java.io.IOException;

public interface CarnetService extends CoreService<Carnet> {

    Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException;

    JSONObject generarPdfEnBytes(String estudiante) throws IOException, JRException;
}
