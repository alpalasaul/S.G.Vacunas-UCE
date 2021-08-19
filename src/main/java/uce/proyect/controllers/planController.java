package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import org.aspectj.weaver.patterns.HasMemberTypePattern;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Plan;
import uce.proyect.service.agreement.PlanService;
import uce.proyect.service.agreementImp.EmailService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
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

    @GetMapping("/mail")
    public ResponseEntity<?> sendEmail() { // Metodo para verificar el funcionamiento del servicio email, no es el final
        try {
            this.emailService.enviarComprobante();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<Void>(OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Plan plan) {
        var jsonObject = this.planService.generarNotificacionVacuncacion(plan); // Solo al agregar el nuevo plan se realiza la notificación a los mails
        var pl = this.planService.agregarOActualizar(plan);
        jsonObject.put("nuevo_plan", pl);
        return new ResponseEntity<Map>(jsonObject.toMap(), ACCEPTED);
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
