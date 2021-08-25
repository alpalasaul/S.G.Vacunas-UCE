package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Plan;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.service.agreement.PlanService;

import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/plan")
@AllArgsConstructor
public class planController {

    private PlanService planService;

    private EmailService emailService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getPlanes() {
        var listar = this.planService.listar();
        return new ResponseEntity<>(listar, OK);
    }

//         Este endpoint es para un plan por facultad y carrera
//        En el JSON se va a enviar la facultad y carrera, y notifica a todos los estudiantes de la facultad y carrera
    @PostMapping("/carrera")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> createC(@RequestBody Plan plan) {
        var jsonObject = this.planService.generarNotificacionVacuncacionPorCarrera(plan); // Solo al agregar el nuevo plan se realiza la notificación a los mails
        var pl = this.planService.agregarOActualizar(plan); // Si pasa el envio de mails se lo guarda
        jsonObject.put("nuevo_plan", pl);
        return new ResponseEntity<>(jsonObject.toMap(), ACCEPTED);
    }

//         Este endpoint es para un plan por facultad
//        En el JSON solo se va a enviar la facultad y no la carrera, y notifica a todos los estudiantes de la facultad
    @PostMapping("/facultad")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> createF(@RequestBody Plan plan) {
        var jsonObject = this.planService.generarNotificacionVacuncacionPorFacultad(plan);
        var pl = this.planService.agregarOActualizar(plan);
        jsonObject.put("nuevo_plan", pl);
        return new ResponseEntity<>(jsonObject.toMap(), ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Plan plan, @PathVariable("id") String id) { // Si se actualizan las fechas es importante notificar o no actualizar
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
