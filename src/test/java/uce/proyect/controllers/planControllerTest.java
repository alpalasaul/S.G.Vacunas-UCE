package uce.proyect.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uce.proyect.models.Plan;
import uce.proyect.service.agreement.PlanService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(planController.class)
class planControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PlanService planService;

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
    @DisplayName("Endpoint que lista todos los planes")
    void getPlanes() throws Exception {
        // Given

        var plan = new Plan();
        plan.setPersonasVacunadas(0);
        plan.setCompleto(false);
        plan.setFacultad("FICFM");
        plan.setFase("PRIMERA");
        plan.setCentroVacunacion("COLISEO UCE");
        plan.setFechaInicio(LocalDate.now());
        plan.setFechaFin(LocalDate.now().plusDays(4L));

        var jsonObject = new JSONObject();
        jsonObject.put("notificados", 478);
        jsonObject.put("fecha_emision", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        when(this.planService.generarNotificacionVacuncacion(any(Plan.class))).thenReturn(jsonObject);

        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // When
        this.mvc.perform(post("/plan").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(plan)))
                // Then
                .andExpect(status().isAccepted());
        verify(this.planService).generarNotificacionVacuncacion(any(Plan.class));
    }


    @DisplayName("Actualizar plan")
    @WithMockUser("admin")
    @Test
    void actualizarPlan() throws Exception {
        // give
        var plan = new Plan();
        plan.set_id("6739kEifs2");
        plan.setPersonasVacunadas(0);
        plan.setCompleto(false);
        plan.setFacultad("FICFM");
        plan.setFase("PRIMERA");
        plan.setCentroVacunacion("COLISEO UCE");
        plan.setFechaInicio(LocalDate.now());
        plan.setFechaFin(LocalDate.now().plusDays(4L));

        when(this.planService.buscarPorId("6739kEifs2")).thenReturn(plan);
        when(this.planService.agregarOActualizar(any(Plan.class))).thenReturn(plan);

        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // when
        this.mvc.perform(put("/plan/6739kEifs2").contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(plan)))
        // then
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$._id").value("6739kEifs2"))
                .andExpect(jsonPath("$.fechaInicio").value(LocalDate.now().toString())) // Siempre pasarlo a string localdate para evitar fallas
                .andExpect(jsonPath("$.facultad").value("FICFM"));

        verify(this.planService).buscarPorId("6739kEifs2");
        verify(this.planService).agregarOActualizar(plan);
    }

    @Test
    void getTotalInoculados() {
    }
}