package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Carnet;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EmailService;

import javax.mail.MessagingException;
import java.io.IOException;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carnet")
@AllArgsConstructor
public class carnetController {

    private CarnetService carnetService;

    private EmailService emailService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getCarnets() {
        var listar = this.carnetService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Carnet carnet) {
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
    @GetMapping("/descargarCarnert/{estudiante}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<byte[]> generarCarnet(@PathVariable("estudiante") String estudiante) throws JRException, IOException {
        var jsonObject = this.carnetService.generarPdfEnBytes(estudiante);
        var export = (byte[]) jsonObject.get("recurso");// Genero mi pdf y lo guardo en una cadena de bytes
        var headers = new HttpHeaders(); // Mando la respuesta de manera intuitiva, lo mando por la cabecera
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=carnetVacunacion-".concat(estudiante).concat(".pdf")); // habilito la actividad de examen en línea (inline) para que el navegador me permita descargarlo y le pongo un nombre al pdf
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(export); // Devuelvo la respuesta
    }

    @GetMapping("/enviarCarnet/{estudiante}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> generarCarnetAlmacenado(@PathVariable("estudiante") String estudiante) throws JRException, IOException, MessagingException {
        var recursos = this.carnetService.generarPdfEnBytes(estudiante);// Genero mi pdf y lo guardo en una cadena de bytes
        var respuesta = this.emailService.enviarComprobante(recursos);
        return new ResponseEntity<>(respuesta.toMap(), OK);
    }

    @GetMapping("/{estudiante}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getDatosCarnet(@PathVariable("estudiante") String estudiante) {
        var datosCarnet = this.carnetService.buscarPorId(estudiante);
        return new ResponseEntity<>(datosCarnet, OK);
    }
}
