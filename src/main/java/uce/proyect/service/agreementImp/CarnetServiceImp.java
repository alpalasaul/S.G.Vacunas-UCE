package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.annotation.Transactional;
import uce.proyect.exceptions.CarnetException;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;
import uce.proyect.repositories.CarnetRepository;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.repositories.PlanRepository;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EstudianteService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class CarnetServiceImp implements CarnetService {

    private CarnetRepository carnetRepository;

    private EstudianteRepository estudianteRepository;

    private EstudianteService estudianteService;

    private PlanRepository planRepository;

    @Override
    public Carnet agregarOActualizar(Carnet pojo) {
        Optional<Carnet> carnet = this.carnetRepository.findByEstudiante(pojo.getEstudiante());
        if (pojo.isPrimeraDosis() && !pojo.isSegundaDosis()) { // true - false  (registra la primera dosis)
            return this.carnetRepository.save(pojo);
        }
        // buscar que primera dosis sea true
        if (pojo.isPrimeraDosis() && pojo.isSegundaDosis()) { // true - true (registra la segunda dosis)
            // buscar en la db que exista la fecha uno para poder crear la fecha 2 y evitar que cree ambos registros en el mismo día
            if (carnet.get().getFechaPrimeraDosis() != null) {
                return this.carnetRepository.save(pojo);
            } else {
                throw new CarnetException("No se puede registrar las 2 vacunas al mismo tiempo, fuera de plan");
            }
        }
        if (!pojo.isPrimeraDosis() && !pojo.isSegundaDosis()) { // false - false edita el registro voluntario
            return this.carnetRepository.save(pojo);
        }
        throw new CarnetException("No se puede registrar la segunda fecha si no tiene la primera");
    }

    @Override
    public Collection<Carnet> listar() throws NoEncontradorException {
        var list = this.carnetRepository.findAll();
        if (list.isEmpty()) {
            throw new NoEncontradorException("Sin registros");
        }
        return list;
    }

    @Override
    public Carnet buscarPorId(String identificador) {
        var carnet = this.carnetRepository.findByEstudiante(identificador);
        return carnet.orElseThrow();
    }

    @Override
    public JSONObject eliminar(String identificador) {
        var carnet = this.carnetRepository.findById(identificador);
        var jsonObject = new JSONObject();
        if (carnet.isPresent()) {
            this.carnetRepository.delete(carnet.get());
            jsonObject.put("Eliminado_C", "Se ha eliminado el carnet: "
                    .concat(carnet.get().get_id()));
        }
        return jsonObject;
    }

    //    Servicio que valida que el cerdo ya tenga las 2 dosis
    @Override
    @Transactional(readOnly = true)
    public Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException {
        var carnetOptional = this.carnetRepository.findByEstudiante(estudiante);
        if (carnetOptional.isPresent()) {
            var carnet = carnetOptional.get();
            if (!carnet.isSegundaDosis()) {
                var fechaPrimeraDosis = carnet.getFechaPrimeraDosis();
                if (fechaPrimeraDosis != null) {
                    var fechaEstimadaSegundaDosis = fechaPrimeraDosis.plusDays(28L);
                    throw new CarnetException(
                            "No se ha suministrado la segunda dosis aún.",
                            fechaPrimeraDosis,
                            fechaEstimadaSegundaDosis,
                            carnet.getNombreVacuna());
                } else {
                    throw new CarnetException("No se ha asigando aún un calendario de Vacunación.");
                }
            }
            return carnet;
        }
        throw new NoEncontradorException("No se ha encontrado ningun carnet para :".concat(estudiante));
    }

    @Override
    public JSONObject generarPdfEnBytes(String estudiante) throws IOException, JRException, NoSuchElementException {

        var data = this.buscarCarnetPorEstudiante(estudiante); // Cargo los datos del carnet y estudiante (vacunado), verifico que tenga las 2 dosis y que exista
        var estu = this.estudianteRepository.findByUsuario(estudiante).orElseThrow(); // Con la validacion anterior ya se define la existencia o no del usuario

        var resource = new ClassPathResource("carnet.jrxml").getInputStream(); // Habia un error al hacer referencia a la ruta absoluta del pdf al usar heroku - RESUELTO

        var dataJson = new JSONObject();

        dataJson.put("centroVacunacion", data.getCentroVacunacion());
        dataJson.put("estudiante", this.estudianteService.nombres(estudiante));
        dataJson.put("cedula", estu.getCedula());
        dataJson.put("fechaNacimiento", LocalDate.now().getYear() - estu.getFechaNacimiento().getYear()); // Solo es hilar mas fino
        dataJson.put("nombreVacuna", data.getNombreVacuna());
        dataJson.put("fechaPrimeraDosis", (data.getFechaPrimeraDosis() == null) ? "" : data.getFechaPrimeraDosis().toString());
        dataJson.put("fechaSegundasDosis", (data.getFechaSegundasDosis() == null ? "" : data.getFechaSegundasDosis().toString()));
        dataJson.put("vacunadorPrimeraDosis", data.getVacunadorPrimeraDosis());
        dataJson.put("vacunadorSegundaDosis", data.getVacunadorSegundaDosis());
        dataJson.put("primeraDosis", (data.isPrimeraDosis()) ? "Sí" : "No");
        dataJson.put("segundaDosis", (data.isSegundaDosis()) ? "Sí" : "No");
        dataJson.put("loteDosisUno", data.getLoteDosisUno());
        dataJson.put("loteDosisDos", data.getLoteDosisDos());

        var jsonDataStream = new ByteArrayInputStream(dataJson.toString().getBytes());
        var ds = new JsonDataSource(jsonDataStream);
        JasperReport jasperReport = JasperCompileManager.compileReport(resource); // Mando a compilar el reporte que está en la ruta resources
//        var dataSource = new JRBeanCollectionDataSource(Collections.singletonList(test)); // Cargo los datos que voy a llenar en el reporte en forma de colección
        Map<String, Object> map = new HashMap<>();
        map.put("createdBy", "sgvacunas"); //
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, ds); // Lleno el reporte que compilé con los datos que cague en la colección
//        JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\alpal\\Desktop\\carnet.pdf"); // Genera el PDF Físico en una ruta (Se sobreescribe) podrías usar esta línea para mandar por mail solo lo guardar en una ruta del proyecto y cada vez que lo pidan solo se va a sobreescribir (no debe estar abierto el pdf sino genera error al sobreescribir)
        var bytes = JasperExportManager.exportReportToPdf(jasperPrint);// Exporto mi pdf en una cadena de bytes - ERICK: Uso este mismo metodo para no guardar datos en otro lugar que no sea la DB
//         ERICK: Para no acoplar el servicio de mail aqui envio los recursos necesarios para tratarlo desde el controller
        var jsonObject = new JSONObject();
        jsonObject.put("recurso", bytes);
        jsonObject.put("mailDestinatario", estu.getCorreo());
        return jsonObject;
    }

    @Override
    @Transactional
    public void actualizarCarnetVacunado(String idPlan, Carnet carnet) {
        var jsonObject = new JSONObject();
        this.planRepository.findById(idPlan).ifPresent(plan -> {
            plan.setPersonasVacunadas(plan.getPersonasVacunadas() + 1);
            this.planRepository.save(plan);
            log.info("Se han aumentado en 1 el numero de vacunados para ".concat(plan.getFacultad()));
            this.carnetRepository.save(carnet);
            log.info("El carnet se ha actualizado correctamente.");
        });
    }
}
