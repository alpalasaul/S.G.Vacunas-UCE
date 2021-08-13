package uce.proyect.exceptions.handleException;

import org.json.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uce.proyect.exceptions.NoEncontradorException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class ControllerAdviceSG {

    @ExceptionHandler(NoEncontradorException.class)
    public ResponseEntity<Map> handleNoEncontradoEx(NoEncontradorException nee) {
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", nee.getMessage());
        jsonObject.put("fecha", LocalDateTime.now().toString());
        return new ResponseEntity<Map>(jsonObject.toMap(), NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map> handleDataAccessEx(DataAccessException dae) {
        var jsonObject = new JSONObject();
        jsonObject.put("mensaje", dae.getMessage());
        jsonObject.put("causa", dae.getMostSpecificCause().toString());
        jsonObject.put("fecha", LocalDateTime.now().toString());
        return new ResponseEntity<Map>(jsonObject.toMap(), INTERNAL_SERVER_ERROR);
    }
}
