package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.exceptions.PlanException;
import uce.proyect.models.Plan;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.FacultadRepository;
import uce.proyect.repositories.PlanRepository;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.service.agreement.PlanService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@Service
@AllArgsConstructor
public class PlanServiceImp implements PlanService {

    private PlanRepository planRepository;

    private FacultadRepository facultadRepository;

    private EstudianteRepository estudianteRepository;

    private EmailService emailService;

    @Override
    public Plan agregarOActualizar(Plan pojo) {
        return this.planRepository.save(pojo);
    }

    @Override
    public Collection<Plan> listar() throws RuntimeException {
        var list = this.planRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Plan buscarPorId(String identificador) {
        var plan = this.planRepository.findById(identificador);
        if (plan.isPresent()) {
            return plan.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var plan = this.planRepository.findById(identificador);
        var jsonObject = new JSONObject();
        if (plan.isPresent()) {
            this.planRepository.delete(plan.get());
            jsonObject.put("Eliminado_P", "Se ha eliminado el plan: "
                    .concat(plan.get().get_id())
                    .concat(" con programación: ")
                    .concat("I: ".concat(plan.get().getFechaInicio().toString()))
                    .concat(" F: ".concat(plan.get().getFechaFin().toString())));
        }
        return jsonObject;
    }

    @Override
    @Transactional(readOnly = true)
    public JSONObject generarNotificacionVacuncacion(Plan nuevoPlan) throws PlanException { // Logica de negocio para agregar un plan de vacunación
        var planAntiguo = this.planRepository.findByFacultadAndCarrera(nuevoPlan.getFacultad(), nuevoPlan.getCarrera()); // Se determina si esque existe el plan
        var jsonObject = new JSONObject();
        if (planAntiguo.isPresent()) { // Si se encuentra el plan, se lanza una excepcion
            throw new PlanException("Ya existe un plan de vacunacion para: "
                    .concat(nuevoPlan.getCarrera())
                    .concat(" de ")
                    .concat(nuevoPlan.getFacultad()),
                    nuevoPlan.getFechaInicio(),
                    nuevoPlan.getFechaFin(),
                    nuevoPlan.getPersonasVacunadas(),
                    nuevoPlan.getFase());
        } else {
            var facultad = this.facultadRepository.findByNombreAndCarrera(nuevoPlan.getFacultad(), nuevoPlan.getCarrera()); // Se busca si existe la facultad y la carrera
            if (facultad.isPresent()) {
                var estudiantes = this.estudianteRepository.findByFacultad(facultad.get().get_id()); // Buscamos a todos los estudiantes de la facultad y carrera para notificarlos sobre el plan de vacunacion
                if (estudiantes.isEmpty()) { // Si no hay registros de estudiantes en esa carrera y facultad se lanza una excepcion
                    throw new NoEncontradorException("No existen registros de estudiantes para: "
                            .concat(nuevoPlan.getCarrera())
                            .concat(" de ")
                            .concat(nuevoPlan.getFacultad()));
                } else {
                    estudiantes.forEach(estudiante -> this.emailService.enviarEmail( // Se envia las notificaciones a los mails de cada estudiante, para ver como se forma el JSON entra a la documentación de los ENDPOINT
                            estudiante.getCorreo(),
                            nuevoPlan.getFechaInicio(),
                            nuevoPlan.getFechaFin(),
                            nuevoPlan.getFacultad()));
                    jsonObject.put("notificados", estudiantes.size());
                    jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            } else { // Si no existe la carrera o la facultad se lanza una exepcion
                throw new NoEncontradorException("No existen registros para: "
                        .concat(nuevoPlan.getCarrera())
                        .concat(" de ")
                        .concat(nuevoPlan.getFacultad()));
            }
        }
        return jsonObject;
    }
}
