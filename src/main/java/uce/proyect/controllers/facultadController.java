package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Facultad;
import uce.proyect.service.agreement.FacultadService;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/facultad")
@AllArgsConstructor
@Slf4j
public class facultadController {

    private FacultadService facultadService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getFacultades() {
        var listar = this.facultadService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    // Solo enviar la nueva facultad, luego si se quieren agregar carreras usar el endpoint /{nombreFacultad}/{nombreCarrera}
    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Facultad facultad) {
        log.info(facultad.getNombre());
        var fac = this.facultadService.agregarOActualizar(facultad);
        return new ResponseEntity<>(fac, ACCEPTED);
    }

    // No se si se necesite algun metodo para actualizar el nombre de la fac o el nombre de una carrera
    @PutMapping("/{nombreFacultad}/{nombreCarrera}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(
            @PathVariable("nombreFacultad") String facultad,
            @PathVariable("nombreCarrera") String carrera) {
        var fac = this.facultadService.agregarCarrera(facultad, carrera);
        return new ResponseEntity<>(fac, ACCEPTED);
    }

//    No se si se requiera un metodo que elimine toda la facultad
    @DeleteMapping("/{nombreFacultad}/{nombreCarrera}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> delete(
            @PathVariable("nombreFacultad") String facultad,
            @PathVariable("nombreCarrera") String carrera) {
        var cadena = facultad.concat(" ").concat(carrera);
        var fac = this.facultadService.eliminar(cadena);
        return new ResponseEntity<>(fac.toMap(), OK);
    }

}
