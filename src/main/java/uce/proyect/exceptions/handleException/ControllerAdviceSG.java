package uce.proyect.exceptions.handleException;

import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uce.proyect.exceptions.CarnetException;
import uce.proyect.exceptions.NoEncontradorException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ControllerAdviceSG {

    @ExceptionHandler(NoEncontradorException.class)
    public ResponseEntity<Map> handleNoEncontradoEx(NoEncontradorException nee) {
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", nee.getMessage());
        jsonObject.put("fecha", LocalDateTime.now().toString());
        return new ResponseEntity<>(jsonObject.toMap(), NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map> handleDataAccessEx(DataAccessException dae) {
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", dae.getMessage());
        jsonObject.put("causa", dae.getMostSpecificCause().toString());
        jsonObject.put("fecha", LocalDateTime.now().toString());
        return new ResponseEntity<>(jsonObject.toMap(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CarnetException.class)
    public ResponseEntity<Map> handleCarnetEx(CarnetException dae) {
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", dae.getMessage());
        jsonObject.put("fecha_primera_dosis", dae.getFechaPrimeraDosis().toString());
        jsonObject.put("fecha_estimacion_segunda_dosis", dae.getEstimacionfechaSegundaDosis());
        jsonObject.put("tipoVacuna", dae.getTipoVacuna());
        return new ResponseEntity<>(jsonObject.toMap(), BAD_REQUEST);
    }
}
