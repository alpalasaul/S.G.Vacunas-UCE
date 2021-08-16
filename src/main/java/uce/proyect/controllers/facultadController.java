package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Estudiante;
import uce.proyect.models.Facultad;
import uce.proyect.service.agreement.FacultadService;

import java.util.Collection;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/facultad")
@AllArgsConstructor
public class facultadController {

    private FacultadService facultadService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getFacultades() {
        var listar = this.facultadService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Facultad facultad) {
        var fac = this.facultadService.agregarOActualizar(facultad);
        return new ResponseEntity<>(fac, ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Facultad facultad, @PathVariable("id") String id) {
        this.facultadService.buscarPorId(id); // importante para no crear registros al intentar actualizar
        facultad.set_id(id);
        var fac = this.facultadService.agregarOActualizar(facultad);
        return new ResponseEntity<>(fac, ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        var fac = this.facultadService.eliminar(id);
        return new ResponseEntity<>(fac, OK);
    }

}
