package uce.proyect.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uce.proyect.models.Facultad;
import uce.proyect.service.agreement.FacultadService;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(facultadController.class)
class facultadControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FacultadService planService;

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

    @WithMockUser(value = "admin")
    @Test
    @DisplayName("Endpoint que lista todos las facultades")
    void getFacultades() throws Exception {
        var facultad = new Facultad();
        facultad.setCarreras(Arrays.asList(
                "INFORMATICA",
                "SISTEMAS DE INFORMACION",
                "COMPUTACION GRAFICA",
                "CIVIL"));
        facultad.setNombre(
                "FICFM"
        );

        var facultad1 = new Facultad();
        facultad1.setCarreras(Arrays.asList(
                "CONTABILIDAD",
                "AUDITORIA"));
        facultad1.setNombre(
                "ADMINISTRACION"
        );

        var facultad2 = new Facultad();
        facultad2.setCarreras(Arrays.asList(
                "PARVULARIA",
                "INGLES",
                "BIOLOGIA",
                "MATEMATICAS",
                "LENGUAJE"));
        facultad2.setNombre(
                "FILOSOFIA"
        );

        when(this.planService.listar()).thenReturn(Arrays.asList(
                facultad,
                facultad1,
                facultad2
        ));

        this.mvc.perform(get("/facultad"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].nombre", is("FICFM")))
                .andExpect(jsonPath("$[1].nombre", is("ADMINISTRACION")))
                .andExpect(jsonPath("$[2].nombre", is("FILOSOFIA")))
                .andExpect(jsonPath("$[0].carreras", hasSize(4)))
                .andExpect(jsonPath("$[1].carreras", hasSize(2)))
                .andExpect(jsonPath("$[2].carreras", hasSize(5)))
                .andExpect(jsonPath("$[0].carreras[0]", is("INFORMATICA")))
                .andExpect(jsonPath("$[0].carreras[1]", is("SISTEMAS DE INFORMACION")))
                .andExpect(jsonPath("$[0].carreras[2]", is("COMPUTACION GRAFICA")))
                .andExpect(jsonPath("$[0].carreras[3]", is("CIVIL")))
                .andExpect(jsonPath("$[1].carreras[0]", is("CONTABILIDAD")))
                .andExpect(jsonPath("$[1].carreras[1]", is("AUDITORIA")));
    }

    @WithMockUser(value = "admin")
    @Test
    @DisplayName("Endpoint que persiste una facultad")
    void agregarFacultade() throws Exception {
        var facultad = new Facultad();
        facultad.setCarreras(Arrays.asList(
                "INFORMATICA",
                "SISTEMAS DE INFORMACION",
                "COMPUTACION GRAFICA",
                "CIVIL"));
        facultad.setNombre(
                "FICFM"
        );

        when(this.planService.agregarOActualizar(any(Facultad.class))).thenReturn(
                facultad
        );

        this.mvc.perform(post("/facultad")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(facultad)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombre", is("FICFM")))
                .andExpect(jsonPath("$.carreras[0]", is("INFORMATICA")))
                .andExpect(jsonPath("$.carreras[1]", is("SISTEMAS DE INFORMACION")))
                .andExpect(jsonPath("$.carreras[2]", is("COMPUTACION GRAFICA")))
                .andExpect(jsonPath("$.carreras[3]", is("CIVIL")));
    }
}
