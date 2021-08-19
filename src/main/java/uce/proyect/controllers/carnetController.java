package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Carnet;
import uce.proyect.models.CarnetResponse;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EstudianteService;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carnet")
@AllArgsConstructor
public class carnetController {

    private CarnetService carnetService;

    private EstudianteService estudianteService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getCarnets() {
        var listar = this.carnetService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Carnet carnet) {
        var cart = this.carnetService.agregarOActualizar(carnet);
        return new ResponseEntity<>(cart, ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Carnet carnet, @PathVariable("id") String id) {
        this.carnetService.buscarPorId(id); // importante para no crear registros al intentar actualizar
        carnet.set_id(id);
        var cart = this.carnetService.agregarOActualizar(carnet);
        return new ResponseEntity<>(cart, ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        var cart = this.carnetService.eliminar(id);
        return new ResponseEntity<>(cart, OK);
    }

    /*
    * El formato del PDF está hecho en JASPERReport y está en la carpeta resources
    * El nombre de user del estudiante se manda por la URI:
    * EJ: http://localhost:8080/carnet/ddlopezs52
    * */
    @GetMapping("/{estudiante}")
    public ResponseEntity<byte[]> generarCarnet(@PathVariable("estudiante") String estudiante) throws JRException, FileNotFoundException {
        byte[] export = this.carnetService.generarPdf(estudiante); // Genero mi pdf y lo guardo en una cadena de bytes
        var headers = new HttpHeaders(); // Mando la respuesta de manera intuitiva, lo mando por la cabecera
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=carnetVacunacion-".concat(estudiante).concat(".pdf")); // habilito la actividad de examen en línea (inline) para que el navegador me permita descargarlo y le pongo un nombre al pdf
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(export); // Devuelvo la respuesta
    }
}
