package uce.proyect.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uce.proyect.models.Estudiante;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.service.agreement.EstudianteService;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(estudianteController.class)
public class usuarioEstudianteTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EstudianteService estudianteService;

    @MockBean
    private EmailService emailService;

    private ObjectMapper objectMapper;

    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        this.testInfo = testInfo;
        this.testReporter = testReporter;

        this.objectMapper = new ObjectMapper();

        this.testReporter.publishEntry(new StringBuilder()
                .append("Ejecutando ").append(this.testInfo.getDisplayName()).append(" - del test ")
                .append(this.testInfo.getTestMethod().orElseThrow().getName()).toString());
    }

    @WithMockUser("admin")
    @Test
    void buscarEstudiantesPorCarrera() throws Exception {

        var estudiante = new Estudiante();
        estudiante.set_id("3939391122");
        estudiante.setCedula("12312312312");
        estudiante.setCorreo("erickdp@hotmail.com");
        estudiante.setTelefono("2626166");
        estudiante.setGenero("M");
        estudiante.setNombres("Erick Enrique");
        estudiante.setApellidos("Diaz Pastaz");
        estudiante.setCarrera("SISTEMAS DE INFOMRACION");
        estudiante.setSemestre(6);
        estudiante.setEsControlador(false);
        estudiante.setFechaNacimiento(LocalDate.of(1999, 4, 10));
        estudiante.setUsuario("eediazp1");

        var estudiante3 = new Estudiante();
        estudiante3.set_id("2343432432");
        estudiante3.setCedula("2832132");
        estudiante3.setCorreo("cmapana@hotmail.com");
        estudiante3.setTelefono("2343243243");
        estudiante3.setGenero("M");
        estudiante3.setNombres("Jose Esteban");
        estudiante3.setApellidos("Campana Mosquera");
        estudiante3.setCarrera("SISTEMAS DE INFOMRACION");
        estudiante3.setSemestre(6);
        estudiante3.setEsControlador(false);
        estudiante3.setFechaNacimiento(LocalDate.of(1998, 3, 22));
        estudiante3.setUsuario("jcampana");

        var estudiante1 = new Estudiante();
        estudiante1.set_id("29382921");
        estudiante1.setCedula("2392139");
        estudiante1.setCorreo("dlopez@hotmail.com");
        estudiante1.setTelefono("292382");
        estudiante1.setGenero("M");
        estudiante1.setNombres("Danny Gonzalo");
        estudiante1.setApellidos("Lopez Mena");
        estudiante1.setCarrera("SISTEMAS DE INFOMRACION");
        estudiante1.setSemestre(6);
        estudiante1.setEsControlador(false);
        estudiante1.setFechaNacimiento(LocalDate.of(1999, 7, 10));
        estudiante1.setUsuario("dlopez1");

        var estudiante2 = new Estudiante();
        estudiante2.set_id("234239993");
        estudiante2.setCorreo("dcorrea@hotmail.com");
        estudiante2.setCedula("93349238439");
        estudiante2.setTelefono("2938129");
        estudiante2.setGenero("M");
        estudiante2.setNombres("Dyllan Adrian");
        estudiante2.setApellidos("Correa Revelo");
        estudiante2.setCarrera("SISTEMAS DE INFOMRACION");
        estudiante2.setSemestre(6);
        estudiante2.setEsControlador(false);
        estudiante2.setFechaNacimiento(LocalDate.of(2000, 4, 10));
        estudiante2.setUsuario("dcorrea2");


        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        when(this.estudianteService.buscarEstudiantesPorFacultadYCarrera("SISTEMAS DE INFOMRACION")).thenReturn(
                Arrays.asList(estudiante, estudiante3, estudiante1, estudiante2)
        );

        this.mvc.perform(get("/estudiante/filtrarfc/SISTEMAS DE INFOMRACION"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]._id", is("3939391122")))

                .andExpect(jsonPath("$[0].nombres", is("Erick Enrique")))
                .andExpect(jsonPath("$[0].apellidos", is("Diaz Pastaz")))
                .andExpect(jsonPath("$[0].esControlador", is(false)))
                .andExpect(jsonPath("$[0].carrera", is("SISTEMAS DE INFOMRACION")))
                .andExpect(jsonPath("$[0].cedula", is("12312312312")))
                .andExpect(jsonPath("$[0].fechaNacimiento", is(LocalDate.of(1999, 4, 10).toString())))
                .andExpect(jsonPath("$[0].usuario", is("eediazp1")))

                .andExpect(jsonPath("$[1].nombres", is("Jose Esteban")))
                .andExpect(jsonPath("$[1].carrera", is("SISTEMAS DE INFOMRACION")))
                .andExpect(jsonPath("$[1].apellidos", is("Campana Mosquera")))
                .andExpect(jsonPath("$[1].esControlador", is(false)))
                .andExpect(jsonPath("$[1].cedula", is("2832132")))
                .andExpect(jsonPath("$[1].fechaNacimiento", is(LocalDate.of(1998, 3, 22).toString())))
                .andExpect(jsonPath("$[1].usuario", is("jcampana")))

                .andExpect(jsonPath("$[2].nombres", is("Danny Gonzalo")))
                .andExpect(jsonPath("$[2].carrera", is("SISTEMAS DE INFOMRACION")))
                .andExpect(jsonPath("$[2].apellidos", is("Lopez Mena")))
                .andExpect(jsonPath("$[2].esControlador", is(false)))
                .andExpect(jsonPath("$[2].cedula", is("2392139")))
                .andExpect(jsonPath("$[2].fechaNacimiento", is(LocalDate.of(1999, 7, 10).toString())))
                .andExpect(jsonPath("$[2].usuario", is("dlopez1")))

                .andExpect(jsonPath("$[3].nombres", is("Dyllan Adrian")))
                .andExpect(jsonPath("$[3].apellidos", is("Correa Revelo")))
                .andExpect(jsonPath("$[3].esControlador", is(false)))
                .andExpect(jsonPath("$[3].cedula", is("93349238439")))
                .andExpect(jsonPath("$[3].fechaNacimiento", is(LocalDate.of(2000, 4, 10).toString())))
                .andExpect(jsonPath("$[3].carrera", is("SISTEMAS DE INFOMRACION")))
                .andExpect(jsonPath("$[3].usuario", is("dcorrea2")));
    }

}
