package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static uce.proyect.util.ValidarFechas.validarFechas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static uce.proyect.util.FabricaCredenciales.PLANES_DIARIOS;

@Service
@AllArgsConstructor
@Slf4j
public class PlanServiceImp implements PlanService {

    private PlanRepository planRepository;

    private FacultadRepository facultadRepository;

    private EstudianteRepository estudianteRepository;

    private EmailService emailService;

    private CarnetRepository carnetRepository;

    @Override
    public Plan agregarOActualizar(Plan pojo) {
        List<Plan> lista = this.buscarPorFecha(pojo.getFechaInicio());
        // Mando a validar
        validarFechas(pojo, lista);

        // Creo la fase 2 después de 28 días (CREAR)
        if (pojo.get_id() == null) { // Si es diferente de null es una actualización
            var pojo2 = new Plan();
            pojo2.setFechaInicio(pojo.getFechaInicio().plusDays(28));
            pojo2.setFechaFin(pojo.getFechaFin().plusDays(28));
            pojo2.setFacultad(pojo.getFacultad());
            pojo2.setCompleto(false);
            pojo2.setCentroVacunacion(pojo.getCentroVacunacion());
            pojo2.setPersonasVacunadas(0);
            pojo2.setFase("SEGUNDA");
            this.planRepository.save(pojo2);
        } else {
            // Si actualiza voy a buscar cual plan quiere actualizar
            Optional<Plan> plan = this.planRepository.findById(pojo.get_id());
            if (plan.get().getFase().equals("PRIMERA")) { // Actualizar la primera y la segunda
                Optional<Plan> plan2Update = this.planRepository.findByFacultadAndFase(pojo.getFacultad(),"SEGUNDA"); // Obtengo el segundo plan para actualizarlo
                plan2Update.get().setFacultad(pojo.getFacultad());
                plan2Update.get().setFechaInicio(pojo.getFechaInicio().plusDays(28));
                plan2Update.get().setFechaFin(pojo.getFechaFin().plusDays(28));
                plan2Update.get().setCentroVacunacion(pojo.getCentroVacunacion());
                plan2Update.get().setCompleto(false);
                plan2Update.get().setPersonasVacunadas(0);
                pojo.setFase(plan.get().getFase()); // No se permite cambiar de primera a segunda fase
                this.planRepository.save(plan2Update.get());
            } else { // Actualizar solo la segunda fase
                pojo.setFase("SEGUNDA"); // NO se permite cambiar de segunda a primera fase
                // Opcional enviar exception
            }
            // Las validaciones de que no cambia ni la facultad ni la fase EN UN UPDATE se las va a bloquear en el front
        }

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

    @Override
    public List<Plan> buscarPorFecha(LocalDate fechaInicio) {
        List<Plan> lista = this.planRepository.findByFechaInicio(fechaInicio);
        return lista;
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
//            List<Estudiante> estudiantesFinal = new ArrayList<>();
//            if (nuevoPlan.getCarrera() == null) { // Si es general no hay carrera y por tanto debo obtener todos lo estudiantes
            var estudiantes = new ArrayList<Estudiante>();
            facultadOptional.get().getCarreras().forEach(carreraString -> {
                // Obtengo a cada estudiante de las carreras de la facultad, por eso no debe de haber la misma carrera en otra fac
                estudiantes.addAll(this.estudianteRepository.findByCarrera(carreraString));
            });
//                nuevoPlan.setGeneral(true); // Defino que es un plan genral
//                estudiantesFinal = estudiantes;
//            } else {
//                estudiantesFinal = this.estudianteRepository.findByCarrera(nuevoPlan.getCarrera()); // Buscamos a todos los estudiantes de la facultad y carrera para notificarlos sobre el plan de vacunacion
//                nuevoPlan.setGeneral(false);
//            }
            if (estudiantes.isEmpty()) { // Si no hay registros de estudiantes en esa carrera y facultad se lanza una excepcion
                throw new NoEncontradorException("No existen registros de estudiantes para: "
//                        .concat(nuevoPlan.getCarrera() != null ? nuevoPlan.getCarrera() : "")
//                        .concat(" - ")
                        .concat(nuevoPlan.getFacultad()));
            }
            nuevoPlan.setCompleto(false);
            nuevoPlan.setFase("PRIMERA");
            return estudiantes;
        } else {  // Si no existe la carrera o la facultad se lanza una exepcion
            throw new NoEncontradorException("No existen registros para: "
                    .concat(nuevoPlan.getFacultad()));
        }
    }

    //    Agregar el segundo plan luego de 28 dias
    private void validarNuevoPlan(Plan nuevoPlan) throws PlanException {

        var planAntiguo = this.planRepository.findByFacultad(nuevoPlan.getFacultad()); // Se determina si esque existe el plan general

        if (planAntiguo.isPresent()) { // Si se encuentra el plan, se lanza una excepcion
            var stringBuilder =
                    new StringBuilder("Ya existe un plan de vacunacion para: ")
                            .append(nuevoPlan.getFacultad());
//            if (nuevoPlan.getCarrera() != null) {
//                stringBuilder
//                        .append(" de ")
//                        .append(nuevoPlan.getCarrera())
//                        .append(" Y es un plan general.");
//            }
            throw new PlanException(stringBuilder.toString(),
                    planAntiguo.get().getFechaInicio(),
                    planAntiguo.get().getFechaFin(),
                    planAntiguo.get().getPersonasVacunadas(),
                    planAntiguo.get().getFase());
        }
    }

//    Programacion de tareas, es para determinar el numero de personas que se han vacunado en un plan y eso presentarlo en una grafica en el front

    //    @Scheduled(fixedRate = 300000L, initialDelay = 30000L)
    public void establecerPlanes() { // Cuando acaben con todos lo estudiantes debe haber un boton para que PLANES_DIARIOS regrese a nulo
        log.info("INICIANDO LA BUSQUEDA DE NUEVOS PLANES");
//        PLANES_DIARIOS = this.planRepository.findByFaseAndCompletoAndFechaFinLessThanEqual("PRIMERA", false, LocalDate.now());
//        PLANES_DIARIOS.addAll(this.planRepository.findByFaseAndCompletoAndFechaFinLessThanEqual("SEGUNDA", false, LocalDate.now()));
        var plan = new Plan();
        plan.setFase("PRIMERA");
        plan.setFacultad("FICFM");
        plan.setPersonasVacunadas(0);
//        plan.setGeneral(true);
        plan.setCompleto(false);
        var plan1 = new Plan();
        plan1.setFase("SEGUNDA");
        plan1.setFacultad("FIGEMPA");
//        plan1.setCarrera("PETROLEOS");
        plan1.setPersonasVacunadas(0);
//        plan1.setGeneral(false);
        plan1.setCompleto(false);
        var plan2 = new Plan();
        plan2.setFase("PRIMERA");
        plan2.setFacultad("MEDICINA");
//        plan2.setCarrera("OBSTETRICIA");
        plan2.setPersonasVacunadas(0);
//        plan2.setGeneral(false);
        plan2.setCompleto(false);
        PLANES_DIARIOS = Arrays.asList(
                plan, plan1, plan2
        );
    }

    //    @Scheduled(fixedRate = 120000L)
//    @Scheduled(cron = "00 05 * * * ?") // Programo una tarea todos los dias a las 5 am
    public void contarVacunados() { // Obtengo los planes que no esten completos ya sean segundas o primeras fases, y pueden haber varias
        // Tener en cuenta que no pueden haber fechas muy anteriores y no esten completas, una vez terminado el dia ese plan debe establecerse como completado
        var planFasePrimera = this.planRepository.findByFaseAndCompletoAndFechaFinLessThanEqual("PRIMERA", false, LocalDate.now());
        var planFaseSegunda = this.planRepository.findByFaseAndCompletoAndFechaFinLessThanEqual("SEGUNDA", false, LocalDate.now());
        log.info("---------------- PRIMERAS FASES --------");
        planFasePrimera.forEach(plan -> {
            log.info("-------");
            log.info(plan.getFacultad());
            log.info(plan.getFase());
            log.info(plan.getFechaInicio().toString());
            log.info(plan.getFechaFin().toString());
            log.info("-------");
        });
        log.info("----------------- SEGUNDAS FASES --------");
        planFaseSegunda.forEach(plan -> {
            log.info("-------");
            log.info(plan.getFacultad());
            log.info(plan.getFase());
            log.info(plan.getFechaInicio().toString());
            log.info(plan.getFechaFin().toString());
            log.info("-------");
        });
    }

    //    @Scheduled(fixedRate = 120000L)
    public void determinarVacunados() {

        if (PLANES_DIARIOS != null) { // Cuando ya este validado los planes o el plan a realizarse tomar el plan de ese dia

            log.info("** DEFINIENDO EL NUMERO DE VACUNADOS PARA LOS PLANES **");

            PLANES_DIARIOS.forEach(plan -> {

                log.info("---------------------------------");
                log.info("PLAN: ".concat(plan.getFacultad()).concat(" FASE ").concat(plan.getFase().concat(" PERSONAS VACUNADAS ").concat(String.valueOf(plan.getPersonasVacunadas()))));
                log.info("---------------------------------");


                List<Estudiante> estudiantesFinal = new ArrayList<>();

//                if (plan.isGeneral()) {
                var estudiantes = new ArrayList<Estudiante>();
                this.facultadRepository.findByNombre(plan.getFacultad()).ifPresent(facultad -> {
                    facultad.getCarreras().forEach(carrera -> {
                        estudiantes.addAll(this.estudianteRepository.findByCarrera(carrera));
                    });
                });
                estudiantesFinal = estudiantes;
//                } else {
//                    estudiantesFinal = this.estudianteRepository.findByCarrera(plan.getCarrera());
//                }

                estudiantesFinal.forEach(estudiante -> {
                    this.carnetRepository.findByEstudianteAndInoculacionVoluntaria(estudiante.getUsuario(), true).ifPresent(carnet -> {

                        if (plan.getFase().equalsIgnoreCase("PRIMERA") && carnet.isPrimeraDosis()) {

                            log.info(carnet.getEstudiante().concat(" PRIMERA DOSIS"));

                            plan.setPersonasVacunadas(plan.getPersonasVacunadas() + 1);

                            log.info("PLAN AGREGADO EN 1  PARA LA PRIMERA FASE");

                        } else if (carnet.isSegundaDosis()) {

                            log.info(carnet.getEstudiante().concat(" SEGUNDA DOSIS"));

                            plan.setPersonasVacunadas(plan.getPersonasVacunadas() + 1);

                        }

                        this.agregarOActualizar(plan);

                    });
                });
            });
        } else {
            log.info("NO EXISTEN PLANES ACTUALES AUN");
        }
    }

}
