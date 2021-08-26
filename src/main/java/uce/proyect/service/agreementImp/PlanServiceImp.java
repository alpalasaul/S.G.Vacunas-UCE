package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.exceptions.PlanException;
import uce.proyect.models.Estudiante;
import uce.proyect.models.Plan;
import uce.proyect.repositories.CarnetRepository;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.FacultadRepository;
import uce.proyect.repositories.PlanRepository;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.service.agreement.PlanService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class PlanServiceImp implements PlanService {

    private PlanRepository planRepository;

    private FacultadRepository facultadRepository;

    private EstudianteRepository estudianteRepository;

    private EmailService emailService;

    private CarnetRepository carnetRepository;

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

        this.validarNuevoPlan(nuevoPlan);

        var estudiantes = this.validarEstudiantesEnFacultadYCarrera(nuevoPlan);

        this.cargarMensaje(estudiantes, nuevoPlan.getFechaInicio(), nuevoPlan.getFechaFin(), nuevoPlan.getCentroVacunacion());

        var jsonObject = new JSONObject();
        jsonObject.put("notificados", estudiantes.size());
        jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return jsonObject;
    }

    @Override
    public JSONObject obtenerEstudiantesAInocular() {
        return null;
    }

    //     Se envia las notificaciones a los mails de cada estudiante, para ver como se forma el JSON entra a la documentación de los ENDPOINT
    private void cargarMensaje(
            List<Estudiante> estudiantes,
            LocalDate fechaInicio,
            LocalDate fechaFinal,
            String centroVacunacion
    ) {
        estudiantes.forEach(estudiante -> this.emailService.enviarEmail(
                estudiante.getCorreo(),
                fechaInicio,
                fechaFinal,
                centroVacunacion));
    }

    private List<Estudiante> validarEstudiantesEnFacultadYCarrera(Plan nuevoPlan) {
        var facultadOptional = this.facultadRepository.findByNombre(nuevoPlan.getFacultad()); // Se busca si existe la facultad
        if (facultadOptional.isPresent()) {
            List<Estudiante> estudiantesFinal = new ArrayList<>();
            if (nuevoPlan.getCarrera() == null) { // Si es general no hay carrera y por tanto debo obtener todos lo estudiantes
                var estudiantes = new ArrayList<Estudiante>();
                facultadOptional.get().getCarreras().forEach(carreraString -> {
                    // Obtengo a cada estudiante de las carreras de la facultad, por eso no debe de haber la misma carrera en otra fac
                    estudiantes.addAll(this.estudianteRepository.findByCarrera(carreraString));
                });
                nuevoPlan.setGeneral(true); // Defino que es un plan genral
                estudiantesFinal = estudiantes;
            } else {
                estudiantesFinal = this.estudianteRepository.findByCarrera(nuevoPlan.getCarrera()); // Buscamos a todos los estudiantes de la facultad y carrera para notificarlos sobre el plan de vacunacion
                nuevoPlan.setGeneral(false);
            }
            if (estudiantesFinal.isEmpty()) { // Si no hay registros de estudiantes en esa carrera y facultad se lanza una excepcion
                throw new NoEncontradorException("No existen registros de estudiantes para: "
                        .concat(nuevoPlan.getCarrera() != null ? nuevoPlan.getCarrera() : "")
                        .concat(" - ")
                        .concat(nuevoPlan.getFacultad()));
            }
            nuevoPlan.setCompleto(false);
            nuevoPlan.setFacultad("PRIMERA");
            return estudiantesFinal;
        } else {  // Si no existe la carrera o la facultad se lanza una exepcion
            throw new NoEncontradorException("No existen registros para: "
                    .concat(nuevoPlan.getFacultad()));
        }
    }

    private void validarNuevoPlan(Plan nuevoPlan) throws PlanException {

        var planAntiguo = this.planRepository.findByFacultadAndGeneral(nuevoPlan.getFacultad(), true); // Se determina si esque existe el plan general

        if (planAntiguo.isPresent()) { // Si se encuentra el plan, se lanza una excepcion
            var stringBuilder =
                    new StringBuilder("Ya existe un plan de vacunacion para: ")
                            .append(nuevoPlan.getFacultad());
            if (nuevoPlan.getCarrera() != null) {
                stringBuilder
                        .append(" de ")
                        .append(nuevoPlan.getCarrera())
                        .append(" Y es un plan general.");
            }
            throw new PlanException(stringBuilder.toString(),
                    planAntiguo.get().getFechaInicio(),
                    planAntiguo.get().getFechaFin(),
                    planAntiguo.get().getPersonasVacunadas(),
                    planAntiguo.get().getFase());
        }
    }
}
