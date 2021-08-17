package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.h2.engine.Mode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Carnet;
import uce.proyect.models.Facultad;
import uce.proyect.service.agreement.CarnetService;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carnet")
@AllArgsConstructor
public class carnetController {

    private CarnetService carnetService;

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


    @GetMapping("/{estudiante}")
    public String generarCarnet(@PathVariable String estudiante, Model model) {
//        var carnet = this.carnetService.buscarCarnetPorEstudiante(estudiante);
        model.addAttribute("carnet", "ddd");
        return "carnet/pdf";
    }
}
