package uce.proyect.controllers;

import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import uce.proyect.models.Carnet;
import uce.proyect.models.CarnetResponse;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EstudianteService;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/carnet")
@AllArgsConstructor
public class carnetController {

    private CarnetService carnetService;

    private EstudianteService estudianteService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> getCarnets() {
        var listar = this.carnetService.listar();
        return new ResponseEntity<>(listar, OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> create(@RequestBody Carnet carnet) {
        var cart = this.carnetService.agregarOActualizar(carnet);
        return new ResponseEntity<>(cart, ACCEPTED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> update(@RequestBody Carnet carnet, @PathVariable("id") String id) {
        this.carnetService.buscarPorId(id); // importante para no crear registros al intentar actualizar
        carnet.set_id(id);
        var cart = this.carnetService.agregarOActualizar(carnet);
        return new ResponseEntity<>(cart, ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HC')")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        var cart = this.carnetService.eliminar(id);
        return new ResponseEntity<>(cart, OK);
    }


    @GetMapping("/{estudiante}")
    public ResponseEntity<byte[]> generarCarnet(@PathVariable("estudiante") String estudiante) throws FileNotFoundException, JRException {
        var data = this.carnetService.buscarCarnetPorEstudiante(estudiante);
        var file = ResourceUtils.getFile("classpath:carnet.jrxml");


        // TODO: lo voy a implementar cuando regrese de las votaciones xd
        /*
        * va a regresar un nuevo objeto ya modificado para que salgan los datos completos
        * */


//        CarnetResponse test = CarnetResponse.builder()
//                .centroVacunacion()
//                .estudiante()
//                .cedula()
//                .fechaNacimiento()
//                .fechaPrimeraDosis()
//                .fechaSegundasDosis()
//                .loteDosisDos()
//                .loteDosisUno()
//                .nombreVacuna()
//                .primeraDosis()
//                .segundaDosis()
//                .vacunadorPrimeraDosis()
//                .vacunadorSegundaDosis()
//                .build();


        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath()); //
        var dataSource = new JRBeanCollectionDataSource(Collections.singletonList(data)); //
        Map<String, Object> map = new HashMap<>();
        map.put("createdBy", "sgvacunas");
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, dataSource);
        //JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Users\\alpal\\Desktop\\carnet.pdf");
        byte[] export = JasperExportManager.exportReportToPdf(jasperPrint);
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=carnet.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(export);
    }
}
