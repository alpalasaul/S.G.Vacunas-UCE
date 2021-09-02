package uce.proyect.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import org.apache.commons.collections.map.HashedMap;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Administrador;
import uce.proyect.models.Estudiante;
import uce.proyect.models.Plan;
import uce.proyect.service.agreement.PlanService;

import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/plan")
@AllArgsConstructor
public class planController {

    private PlanService planService;

    @GetMapping
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getPlanes() {
        var listar = this.planService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    //    Obtener todos los planes diarios
    @GetMapping("/planesDiarios")
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getPlanesDiarios() {
        var respuesta = this.planService.establecerPlanes();
        return new ResponseEntity<>(respuesta.toMap(), OK);
    }

    @GetMapping("/{nombreFacultad}")
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getTotalInoculados(@PathVariable String nombreFacultad) {
        var respuesta = this.planService.porcentajeInoculados(nombreFacultad);
        return new ResponseEntity<>(respuesta.toMap(), OK);
    }

    @GetMapping("/gson")
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getTotalInoculados() throws JsonProcessingException {

//        var gson = new Gson(); // Usar para la prox GSON
//        var jsonElement = gson.toJsonTree(estudiante); // est es un objeto
//        jsonElement.getAsJsonObject().addProperty("saludo", "Holaaa");
//        jsonElement.getAsJsonObject().addProperty("numero", 8.4d);
//        jsonElement.getAsJsonObject().addProperty("numero", 84.2d);
//        jsonElement.getAsJsonObject().add("admin", gson.toJsonTree(administrador));

        return new ResponseEntity<>(OK);
    }

    //    Obtener todos los estudiantes del plan de inoculacion
    @GetMapping("{nombreFacultad}/{fase}")
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getEstudiantesInocular(
            @PathVariable("nombreFacultad") String nombreFacultad,
            @PathVariable("fase") String fase
    ) {
        var respuesta = this.planService.obtenerEstudiantesAInocular(nombreFacultad, fase);
        return new ResponseEntity<>(respuesta.toMap(), OK);
    }

    @PostMapping
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> createF(@RequestBody Plan plan) {
        var jsonObject = this.planService.generarNotificacionVacuncacion(plan);
        var pl = this.planService.agregarOActualizar(plan);
        jsonObject.put("nuevo_plan", pl);
        return new ResponseEntity<>(ACCEPTED); // Importante el toMap() para mostrar el json en el reponse sino devuelve {"empty": false}
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Plan plan, @PathVariable("id") String id) { // Si se actualizan las fechas es importante notificar o no actualizar
        this.planService.buscarPorId(id); // importante para no crear registros al intentar actualizar
        plan.set_id(id);
        var pl = this.planService.agregarOActualizar(plan);
        return new ResponseEntity<>(pl, ACCEPTED);
    }

    @DeleteMapping("/{nombreFacultad}") // Elimina los planes por el nombre de la facultad
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("nombreFacultad") String id) {
        var pl = this.planService.eliminar(id);
        return new ResponseEntity<>(OK);
    }


}
