package uce.proyect.service.agreement;

import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Facultad;

public interface FacultadService extends CoreService<Facultad> {
    Facultad agregarCarrera(String facultad, String carrera) throws NoEncontradorException;
}
