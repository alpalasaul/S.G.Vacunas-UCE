package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.exceptions.PlanException;
import uce.proyect.models.Carnet;
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
import java.util.*;
import java.util.stream.Collectors;

import static uce.proyect.util.ValidarFechas.validarFechas;

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
            pojo2.setFechaInicio(pojo.getFechaInicio().plusDays(4));
            pojo2.setFechaFin(pojo.getFechaFin().plusDays(4));
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
                Optional<Plan> plan2Update = this.planRepository.findByFacultadAndFase(pojo.getFacultad(), "SEGUNDA"); // Obtengo el segundo plan para actualizarlo
                plan2Update.get().setFacultad(pojo.getFacultad());
                plan2Update.get().setFechaInicio(pojo.getFechaInicio().plusDays(4));
                plan2Update.get().setFechaFin(pojo.getFechaFin().plusDays(4));
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
    public Collection<Plan> listar() throws NoEncontradorException {
        var list = this.planRepository.findAll();
        if (list.isEmpty()) {
            throw new NoEncontradorException("Sin registros");
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
        var plan = this.planRepository.findByFacultad(identificador); // Obtengo los 2 planes de existir
        var jsonObject = new JSONObject();
        if (plan.isEmpty()) {
            throw new NoEncontradorException("No se han encontrado planes para ".concat(identificador));
        }
        this.planRepository.deleteAll(plan);
        jsonObject.put("Eliminado_P", "Planes eliminados para :".concat(plan.get(0).getFacultad()));
        return jsonObject;
    }

    @Override
    @Transactional
    public JSONObject generarNotificacionVacuncacion(Plan nuevoPlan) throws PlanException { // Logica de negocio para agregar un plan de vacunación

        this.validarNuevoPlan(nuevoPlan);

        var estudiantes = this.validarEstudiantesEnFacultadYCarrera(nuevoPlan);

        this.actualizarCarnetEstudiantes(estudiantes, nuevoPlan.getCentroVacunacion());

        this.cargarMensaje(estudiantes, nuevoPlan.getFechaInicio(), nuevoPlan.getFechaFin(), nuevoPlan.getCentroVacunacion(), nuevoPlan.getFase());

        var jsonObject = new JSONObject();
        jsonObject.put("notificados", estudiantes.size());
        jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        return jsonObject;
    }

    private void actualizarCarnetEstudiantes(List<Estudiante> estudiantes, String centroVacunacionActualizado) { // Al agregar el nuevo plan se actualiza el centro de vacunacion en los carnets, solo los que deseearon
        estudiantes.forEach(estudiante -> this.carnetRepository.findByEstudianteAndInoculacionVoluntaria(estudiante.getUsuario(), true).ifPresent(carnet -> {
            carnet.setCentroVacunacion(centroVacunacionActualizado);
            this.carnetRepository.save(carnet);
        }));
    }

    // Se debe de enviar la facultad del plan y la fase en la que se encuentra
    @Override
    public JSONObject obtenerEstudiantesAInocular(String facultadNombre, String fase) {
        var jsonObject = new JSONObject();
        var carnets = new ArrayList<Carnet>();

        var datos = new ArrayList<JSONObject>();
        this.facultadRepository.findByNombre(facultadNombre).ifPresent(facultad -> facultad.getCarreras().forEach(carrera -> this.estudianteRepository.findByCarrera(carrera).forEach(estudiante -> {

            carnets.addAll(this.carnetRepository.findByEstudianteAndInoculacionVoluntariaAndPrimeraDosis(
                    estudiante.getUsuario(),
                    true, // Inoculacion Voluntaria
                    fase.equalsIgnoreCase("SEGUNDA") // Si es la segunda dosis entonces la primera debe estar en true
            ).stream().collect(Collectors.toList())); // Ver si en las pruebas aquí no se desborda, sino usar ifPresent

        })));


        carnets.forEach(carnet -> {
            var estudiante = this.estudianteRepository.findByUsuario(carnet.getEstudiante());
            var datosUsuario = new JSONObject();
            datosUsuario.put("usuario", estudiante.get().getUsuario());
            datosUsuario.put("nombre", estudiante.get().getNombres());
            datosUsuario.put("apellido", estudiante.get().getApellidos());
            datosUsuario.put("cedula", estudiante.get().getCedula());
            datosUsuario.put("vacunadorSegundaDosis", carnet.getVacunadorSegundaDosis());
            datosUsuario.put("nombreVacuna", carnet.getNombreVacuna());
            datosUsuario.put("segundaDosis", carnet.isSegundaDosis());
            datosUsuario.put("inoculacionVoluntaria", carnet.isInoculacionVoluntaria());
            datosUsuario.put("centroVacunacion", carnet.getCentroVacunacion());
            datosUsuario.put("loteDosisUno", carnet.getLoteDosisUno());
            datosUsuario.put("vacunadorPrimeraDosis", carnet.getVacunadorPrimeraDosis());
            datosUsuario.put("loteDosisDos", carnet.getLoteDosisDos());
            datosUsuario.put("primeraDosis", carnet.isPrimeraDosis());
            datos.add(datosUsuario);
        });




//        jsonObject.put("carnets", carnets);
        jsonObject.put("data", datos);
        return jsonObject;
    }

    @Override
    public List<Plan> buscarPorFecha(LocalDate fechaInicio) {
        return this.planRepository.findByFechaInicio(fechaInicio);
    }

    //     Se envia las notificaciones a los mails de cada estudiante, para ver como se forma el JSON entra a la documentación de los ENDPOINT
    private void cargarMensaje(
            List<Estudiante> estudiantes,
            LocalDate fechaInicio,
            LocalDate fechaFinal,
            String centroVacunacion,
            String fase
    ) {
        estudiantes.forEach(estudiante -> this.emailService.enviarEmailPlan(
                estudiante.getCorreo(),
                fechaInicio,
                fechaFinal,
                centroVacunacion,
                fase
        ));
    }

    private List<Estudiante> validarEstudiantesEnFacultadYCarrera(Plan plan) {
        var facultadOptional = this.facultadRepository.findByNombre(plan.getFacultad()); // Se busca si existe la facultad
        if (facultadOptional.isPresent()) {
            var estudiantes = new ArrayList<Estudiante>();
            facultadOptional.get().getCarreras().forEach(carreraString -> {
//                 Obtengo a cada estudiante de las carreras de la facultad, por eso no debe de haber la misma carrera en otra facultad
                estudiantes.addAll(this.estudianteRepository.findByCarrera(carreraString));
            });
            if (estudiantes.isEmpty()) { // Si no hay registros de estudiantes en esa carrera y facultad se lanza una excepcion
                throw new NoEncontradorException("No existen registros de estudiantes para: "
                        .concat(plan.getFacultad()));
            }
            return estudiantes;
        } else {  // Si no existe la carrera o la facultad se lanza una exepcion
            throw new NoEncontradorException("No existen registros para: "
                    .concat(plan.getFacultad()));
        }
    }

    //    Agregar el segundo plan luego de 28 dias
    private void validarNuevoPlan(Plan nuevoPlan) throws PlanException {

        var planesAntiguos = this.planRepository.findByFacultad(nuevoPlan.getFacultad()); // Se determina si esque existe el plan general

        if (!planesAntiguos.isEmpty()) { // Si se encuentra el plan, se lanza una excepcion
            throw new PlanException(
                    "Ya existe un plan de vacunacion para: ".concat(nuevoPlan.getFacultad()),
                    planesAntiguos.get(0).getFechaInicio(),
                    planesAntiguos.get(0).getFechaFin(),
                    planesAntiguos.get(0).getPersonasVacunadas(),
                    planesAntiguos.get(0).getFase());
        }
    }

    @Override
    public JSONObject establecerPlanes() {
        var respuestaJson = new JSONObject();
        log.info("INICIANDO BUSQUEDA DE NUEVOS PLANES PARA ".concat(LocalDate.now().toString()));
        var planesActuales = this.planRepository.findByFaseAndCompletoAndFechaInicioLessThanEqual("PRIMERA", false, LocalDate.now());
        planesActuales.addAll(this.planRepository.findByFaseAndCompletoAndFechaInicioLessThanEqual("SEGUNDA", false, LocalDate.now()));
        respuestaJson.put("planes_actuales", "No existen planes disponibles.");
        if (planesActuales.isEmpty()) {
            planesActuales = this.planRepository.findByCompleto(false);
            respuestaJson.put("planes_para_proximas_fechas", planesActuales);
        } else {
            respuestaJson.put("planes_actuales", planesActuales);
        }
        return respuestaJson;
    }

    @Override
    public JSONObject porcentajeInoculados(String nombreFacultad) {
        var jsonObject = new JSONObject();

        var planes = this.planRepository.findByFacultad(nombreFacultad);

        if (planes.isEmpty()) { // Valido que primeramente exista el plan para la facultad
            throw new NoEncontradorException("No se han encontrado planes para ".concat(nombreFacultad));
        }

        planes.forEach(plan -> {
            if (plan.getFase().equalsIgnoreCase("PRIMERA")) {
                jsonObject.put("total_inoculados_primera_fase_".concat(nombreFacultad.toLowerCase()), plan.getPersonasVacunadas()); // total de primera fase
            } else {
                jsonObject.put("total_inoculados_segunda_fase_".concat(nombreFacultad.toLowerCase()), plan.getPersonasVacunadas()); // total de segunda fase
            }
        });

        this.facultadRepository.findByNombre(nombreFacultad).ifPresent(facultad -> {
            facultad.getCarreras().forEach(carrera -> {
                var totalCarnetsFacultad = this.estudianteRepository.findByCarrera(carrera).stream().map(estudiante -> {
                    return this.carnetRepository.findByEstudiante(estudiante.getUsuario()).orElseThrow();
                }).collect(Collectors.toList());

                jsonObject.put("total_estudiantes_".concat(nombreFacultad.toLowerCase()), totalCarnetsFacultad.size()); // Total de estudiantes de la facultad
                var voluntarios = totalCarnetsFacultad.stream().filter(Carnet::isInoculacionVoluntaria).count();
                var noVoluntarios = totalCarnetsFacultad.stream().filter(carnet -> !carnet.isInoculacionVoluntaria()).count();
                jsonObject.put("estudiantes_a_inocular_de_".concat(nombreFacultad.toLowerCase()), voluntarios); // total de estudiantes a inocular voluntarios
                jsonObject.put("estudiantes_no_voluntarios_de_".concat(nombreFacultad.toLowerCase()), noVoluntarios); // total de estudiantes no voluntarios

            });
        });
        return jsonObject;
    }

    //    @Scheduled(fixedRate = 12000L, initialDelay = 30000L)
    // Programo una tarea todos los dias a las 5 am para enviar mail 4 dias antes de la segunda dosis
    @Scheduled(cron = "0 37 13 * * ?")
    public void enviarNotificacionSegundaDosis() { // Envio un mail una semana antes de la segunda dosis, para presentar el proyecto se debe de hacer en menos tiempo
        log.info("----------------- INGRESANDO AL PROCESO PROGRAMADO ---------------");

        this.planRepository.findByFechaInicioAndFase(LocalDate.now().plusDays(4L), "SEGUNDA").forEach(plan -> { // Obtengo los planes proximos a 7 dias que sean de segunda dosis
            log.info("----------------- SEGUNDAS FASES ------------------");
            log.info("FECHA INICIO".concat(plan.getFechaInicio().toString()));

            this.facultadRepository.findByNombre(plan.getFacultad()).ifPresent(facultad -> { // Como al agregar el plan ya se valida que haya estudiantes y la facultad no me preocupo de eso
                log.info("FACULTAD ".concat(facultad.getNombre()));

                facultad.getCarreras().forEach(carrera -> {
                    log.info("CARRERA ".concat(carrera));

                    this.estudianteRepository.findByCarrera(carrera).forEach(estudiante -> { // Por cada carrera busco a los estudiantes
                        log.info("MAIL HACIA ".concat(estudiante.getCorreo()));

                        this.emailService.enviarEmailPlan( // Envio el email de segunda fase
                                estudiante.getCorreo(),
                                plan.getFechaInicio(),
                                plan.getFechaFin(),
                                plan.getCentroVacunacion(),
                                plan.getFase()
                        );
                    });
                });
            });
        });
    }
}
