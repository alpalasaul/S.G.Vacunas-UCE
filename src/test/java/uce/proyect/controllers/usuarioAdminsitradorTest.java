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
import uce.proyect.models.Administrador;
import uce.proyect.service.agreement.AdministradorService;
import uce.proyect.service.agreement.EmailService;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(administradorController.class)
public class usuarioAdminsitradorTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AdministradorService administradorService;

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
    void agregarAdministradorPorUsuario() throws Exception {

        var administrador = new Administrador();
        administrador.set_id("3939391122");
        administrador.setCorreo("erickdp@hotmail.com");
        administrador.setTelefono("2626166");
        administrador.setGenero("M");
        administrador.setNombres("Erick Enrique");
        administrador.setApellidos("Diaz Pastaz");
        administrador.setEsControlador(true);
        administrador.setFechaNacimiento(LocalDate.of(1999, 4, 10));
        administrador.setUsuario("eediazp1");

        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        when(this.administradorService.buscarPorId("eediazp1")).thenReturn(administrador);

        this.mvc.perform(get("/administrador/eediazp1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._id", is("3939391122")))
                .andExpect(jsonPath("$.nombres", is("Erick Enrique")))
                .andExpect(jsonPath("$.apellidos", is("Diaz Pastaz")))
                .andExpect(jsonPath("$.esControlador", is(true)))
                .andExpect(jsonPath("$.fechaNacimiento", is(LocalDate.of(1999, 4, 10).toString())))
                .andExpect(jsonPath("$.usuario", is("eediazp1")));

    }

    @WithMockUser("admin")
    @Test
    void actualizarAdministrador() throws Exception {

        var administrador = new Administrador();
        administrador.set_id("3939391122");
        administrador.setCorreo("erickdp@hotmail.com");
        administrador.setTelefono("2626166");
        administrador.setGenero("M");
        administrador.setNombres("Erick Enrique");
        administrador.setApellidos("Diaz Pastaz");
        administrador.setCedula("172282373231");
        administrador.setEsControlador(true);
        administrador.setFechaNacimiento(LocalDate.of(1999, 4, 10));
        administrador.setUsuario("eediazp1");

        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        when(this.administradorService.agregarOActualizar(any(Administrador.class))).thenReturn(administrador);
//        doThrow(IllegalArgumentException.class).when(this.emailService).
//                enviarEmailCredenciales("erickdp@hotmail.com", "eediazp1", "erickdp041099");

        this.mvc.perform(put("/administrador").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(administrador)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._id", is("3939391122")))
                .andExpect(jsonPath("$.nombres", is("Erick Enrique")))
                .andExpect(jsonPath("$.apellidos", is("Diaz Pastaz")))
                .andExpect(jsonPath("$.esControlador", is(true)))
                .andExpect(jsonPath("$.cedula", is("172282373231")))
                .andExpect(jsonPath("$.fechaNacimiento", is(LocalDate.of(1999, 4, 10).toString())))
                .andExpect(jsonPath("$.usuario", is("eediazp1")));
    }
}
