package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Plan;
import uce.proyect.service.agreement.PlanService;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/plan")
@AllArgsConstructor
public class planController {

    private PlanService planService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getPlanes() {
        var listar = this.planService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Plan plan) {
        var pl = this.planService.agregarOActualizar(plan);
        return new ResponseEntity<>(pl, ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Plan plan, @PathVariable("id") String id) {
        this.planService.buscarPorId(id); // importante para no crear registros al intentar actualizar
        plan.set_id(id);
        var pl = this.planService.agregarOActualizar(plan);
        return new ResponseEntity<>(pl, ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        var pl = this.planService.eliminar(id);
        return new ResponseEntity<>(pl, OK);
    }

}
