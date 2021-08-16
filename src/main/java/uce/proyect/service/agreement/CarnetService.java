package uce.proyect.service.agreement;

import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;

public interface CarnetService extends CoreService<Carnet> {
    Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException;
}
