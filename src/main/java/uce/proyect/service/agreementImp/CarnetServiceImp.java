package uce.proyect.service.agreementImp;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import uce.proyect.exceptions.CarnetException;
import uce.proyect.exceptions.NoEncontradorException;
import uce.proyect.models.Carnet;
import uce.proyect.models.CarnetResponse;
import uce.proyect.repositories.CarnetRepository;
import uce.proyect.repositories.EstudianteRepository;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EstudianteService;

import java.io.FileNotFoundException;
import java.util.*;

@Service
@AllArgsConstructor
public class CarnetServiceImp implements CarnetService {

    private CarnetRepository carnetRepository;

    private EstudianteRepository estudianteRepository;

    private EstudianteService estudianteService;

    @Override
    public Carnet agregarOActualizar(Carnet pojo) {
        return this.carnetRepository.save(pojo);
    }

    @Override
    public Collection<Carnet> listar() throws RuntimeException {
        var list = this.carnetRepository.findAll();
        if (list.isEmpty()) {
            throw new RuntimeException("Sin registros");
        }
        return list;
    }

    @Override
    public Carnet buscarPorId(String identificador) {
        var carnet = this.carnetRepository.findById(identificador);
        if (carnet.isPresent()) {
            return carnet.get();
        }
        throw new NoEncontradorException("No existen registros para : ".concat(identificador));
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
    public Carnet buscarCarnetPorEstudiante(String estudiante) throws NoEncontradorException {
        var carnetOptional = this.carnetRepository.findByEstudiante(estudiante);
        if (carnetOptional.isPresent()) {
            var carnet = carnetOptional.get();
            if (!carnet.isSegundaDosis()) {
                var fechaPrimeraDosis = carnet.getFechaPrimeraDosis();
                var fechaEstimadaSegundaDosis = fechaPrimeraDosis.plusDays(28L);
                throw new CarnetException(
                        "No se ha suministrado la segunda dosis aún.",
                        fechaPrimeraDosis,
                        fechaEstimadaSegundaDosis,
                        carnet.getNombreVacuna());
            }
            return carnet;
        }
        throw new NoEncontradorException("No se ha encontrado ningun carnet para :".concat(estudiante));
    }

    @Override
    public byte[] generarPdf(String estudiante) throws FileNotFoundException, JRException, NoSuchElementException {

        var data = this.carnetRepository.findByEstudiante(estudiante); // Cargo los datos del carnet y estudiante(vacunado)
        var estu = this.estudianteRepository.findByUsuario(estudiante);
        var file = ResourceUtils.getFile("classpath:carnet.jrxml"); // Indico en que dirección está alojado el formato XML (está en resources)

        var cal = Calendar.getInstance();
        var year = cal.get(Calendar.YEAR);

        var test = CarnetResponse.builder() // Cargo los nuevos datos al objeto que voy a regresar (CarnetResponse)
                .centroVacunacion(data.orElseThrow().getCentroVacunacion())
                .estudiante(this.estudianteService.nombres(estudiante)) // Servicio que devuelve los nombres y apellidos en una sola cadena
                .fechaNacimiento(year - estu.orElseThrow().getFechaNacimiento().getYear())
                .fechaPrimeraDosis(data.get().getFechaPrimeraDosis())
                .fechaSegundasDosis(data.get().getFechaSegundasDosis())
                .loteDosisDos(data.get().getLoteDosisDos())
                .loteDosisUno(data.get().getLoteDosisUno())
                .cedula(estu.get().getCedula())
                .nombreVacuna(data.get().getNombreVacuna())
                .primeraDosis((data.get().isPrimeraDosis())?"Sí":"No") // Operador ternario
                .segundaDosis((data.get().isSegundaDosis())?"Sí":"No") // Operador ternario
                .vacunadorPrimeraDosis(this.estudianteService.nombres(data.get().getVacunadorPrimeraDosis()))
                .vacunadorSegundaDosis(this.estudianteService.nombres(data.get().getVacunadorSegundaDosis()))
                .build();

        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath()); // Mando a compilar el reporte que está en la ruta resources
        var dataSource = new JRBeanCollectionDataSource(Collections.singletonList(test)); // Cargo los datos que voy a llenar en el reporte en forma de colección
        Map<String, Object> map = new HashMap<>();
        map.put("createdBy", "sgvacunas"); //
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource); // Lleno el reporte que compilé con los datos que cague en la colección
        //JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\alpal\\Desktop\\carnet.pdf"); // Genera el PDF Físico en una ruta (Se sobreescribe) podrías usar esta línea para mandar por mail solo lo guardar en una ruta del proyecto y cada vez que lo pidan solo se va a sobreescribir (no debe estar abierto el pdf sino genera error al sobreescribir)
        return JasperExportManager.exportReportToPdf(jasperPrint); // Exporto mi pdf en una cadena de bytes

    }
}
