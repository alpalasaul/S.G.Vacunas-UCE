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
    public JSONObject generarNotificacionVacuncacionPorCarrera(Plan nuevoPlan) throws PlanException { // Logica de negocio para agregar un plan de vacunación

        this.validarNuevoPlan(nuevoPlan, true);

        var estudiantes = this.validarEstudiantesEnFacultadYCarrera(nuevoPlan.getFacultad(), nuevoPlan.getCarrera());

        this.cargarMensaje(estudiantes, nuevoPlan.getFechaInicio(), nuevoPlan.getFechaFin(), nuevoPlan.getCentroVacunacion());

        var jsonObject = new JSONObject();
        jsonObject.put("notificados", estudiantes.size());
        jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return jsonObject;
    }

    @Override
    @Transactional(readOnly = true)
    public JSONObject generarNotificacionVacuncacionPorFacultad(Plan nuevoPlan) throws NoEncontradorException {

        this.validarNuevoPlan(nuevoPlan, false);

        // No se valida con el metodo de la clase pues se debe de tomar a los estudiantes de toda las carreras de la facultad
        var facultad = this.facultadRepository.findByNombre(nuevoPlan.getFacultad()); // Se busca si existe la facultad
        if (facultad.isPresent()) {
            // Llamar a todos los estudiantes de todas las carreras
            var estudiantes = new ArrayList<Estudiante>();
            facultad.get().getCarreras().forEach(carrera -> {
                // Obtengo a cada estudiante de las carreras de la facultad, por eso no debe de haber la misma carrera en otra fac
                estudiantes.addAll(this.estudianteRepository.findByCarrera(carrera));
            });

            if (estudiantes.isEmpty()) { // Si no hay registros de estudiantes en esa facultad se lanza una excepcion
                throw new NoEncontradorException("No existen registros de estudiantes para: "
                        .concat(nuevoPlan.getFacultad()));
            } else {

                this.cargarMensaje(estudiantes, nuevoPlan.getFechaInicio(), nuevoPlan.getFechaFin(), nuevoPlan.getCentroVacunacion());
                var jsonObject = new JSONObject();

                jsonObject.put("notificados", estudiantes.size());
                jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                return jsonObject;
            }
        } else { // Si no existe la facultad se lanza una exepcion
            throw new NoEncontradorException("No existen registros para: ".concat(nuevoPlan.getFacultad()));
        }
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

    private List<Estudiante> validarEstudiantesEnFacultadYCarrera(String facultad, String carrera) {
        var facultadOptional = this.facultadRepository.findByNombre(facultad); // Se busca si existe la facultad
        if (facultadOptional.isPresent()) {
            var estudiantes = this.estudianteRepository.findByCarrera(carrera); // Buscamos a todos los estudiantes de la facultad y carrera para notificarlos sobre el plan de vacunacion
            if (estudiantes.isEmpty()) { // Si no hay registros de estudiantes en esa carrera y facultad se lanza una excepcion
                throw new NoEncontradorException("No existen registros de estudiantes para: "
                        .concat(carrera)
                        .concat(" de ")
                        .concat(facultad));
            }
            return estudiantes;
        } else {  // Si no existe la carrera o la facultad se lanza una exepcion
            throw new NoEncontradorException("No existen registros para: "
                    .concat(facultad));
        }
    }

    private void validarNuevoPlan(Plan nuevoPlan, boolean bandera) throws PlanException {
        var planAntiguo = this.planRepository.findByFacultadAndCarrera(nuevoPlan.getFacultad(), nuevoPlan.getCarrera()); // Se determina si esque existe el plan
        if (planAntiguo.isPresent()) { // Si se encuentra el plan, se lanza una excepcion
            var stringBuilder =
                    new StringBuilder("Ya existe un plan de vacunacion para: ")
                            .append(nuevoPlan.getFacultad());
            if (bandera) {
                stringBuilder
                        .append(" de ")
                        .append(nuevoPlan.getCarrera());
            }
            throw new PlanException(stringBuilder.toString(),
                    nuevoPlan.getFechaInicio(),
                    nuevoPlan.getFechaFin(),
                    nuevoPlan.getPersonasVacunadas(),
                    nuevoPlan.getFase());
        }
    }
}
