package uce.proyect.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import uce.proyect.models.Carnet;
import uce.proyect.service.agreement.CarnetService;
import uce.proyect.service.agreement.EmailService;
import uce.proyect.util.LocalDateAdapter;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(carnetController.class)
class carnetControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailService emailService;

    @MockBean
    private CarnetService carnetService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        this.gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
    }

    @WithMockUser("admin")
    @Test
    void deberiaActualizarCarnet() throws Exception {
        var carnet = new Carnet();
        carnet.setCentroVacunacion("COLISEO UCE");
        carnet.setVacunadorPrimeraDosis("JHON DOE");
        carnet.setVacunadorSegundaDosis("MARIO BROS");
        carnet.setLoteDosisDos("22227-FH");
        carnet.setLoteDosisUno("PNPATODOS");
        carnet.setEstudiante("LUIS GONZALES");
        carnet.setEstudiante("LUIS GONZALES");
        carnet.setNombreVacuna("PFIZER");
        carnet.setFechaPrimeraDosis(LocalDate.now());
        carnet.setFechaSegundasDosis(LocalDate.now().plusDays(28L));

        when(this.carnetService.agregarOActualizar(any(Carnet.class))).thenReturn(carnet);

        this.mvc.perform(put("/carnet").contentType(MediaType.APPLICATION_JSON)
                .content(this.gson.toJson(carnet)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.loteDosisDos", is("22227-FH")))
                .andExpect(jsonPath("$.loteDosisUno", is("PNPATODOS")))
                .andExpect(jsonPath("$.fechaPrimeraDosis", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$.fechaSegundasDosis", is(LocalDate.now().plusDays(28L).toString())))
                .andExpect(jsonPath("$.vacunadorPrimeraDosis", is("JHON DOE")))
                .andExpect(jsonPath("$.vacunadorSegundaDosis", is("MARIO BROS")));
    }

    @WithMockUser("admin")
    @Test
    void deberiaDevolverCarnets() throws Exception {
        var carnet = new Carnet();
        carnet.setCentroVacunacion("COLISEO UCE");
        carnet.setVacunadorPrimeraDosis("JHON DOE");
        carnet.setVacunadorSegundaDosis("MARIO BROS");
        carnet.setNombreVacuna("PFIZER");
        carnet.setLoteDosisUno("KDKFS-R");
        carnet.setLoteDosisDos("22227-FH");
        carnet.setEstudiante("LUIS GONZALES");
        carnet.setFechaPrimeraDosis(LocalDate.now().minusDays(28L));
        carnet.setFechaSegundasDosis(LocalDate.now());

        var carnet2 = new Carnet();
        carnet2.setCentroVacunacion("COLISEO UCE");
        carnet2.setVacunadorPrimeraDosis("JHON DOE");
        carnet2.setLoteDosisUno("22227-FH");
        carnet2.setEstudiante("ALVARO RUIZ");
        carnet2.setNombreVacuna("PFIZER");
        carnet2.setFechaPrimeraDosis(LocalDate.now());
        carnet2.setFechaSegundasDosis(LocalDate.now().plusDays(28L));

        var jsonElement = this.gson.toJsonTree(Arrays.asList(carnet, carnet2));

        when(this.carnetService.listar()).thenReturn(Arrays.asList(carnet, carnet2));

        this.mvc.perform(get("/carnet").contentType(MediaType.APPLICATION_JSON)
                        .content(this.gson.toJson(jsonElement)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].loteDosisUno", is("KDKFS-R")))
                .andExpect(jsonPath("$[0].loteDosisDos", is("22227-FH")))
                .andExpect(jsonPath("$[0].fechaPrimeraDosis", is(LocalDate.now().minusDays(28L).toString())))
                .andExpect(jsonPath("$[0].fechaSegundasDosis", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$[0].nombreVacuna", is("PFIZER")))
                .andExpect(jsonPath("$[0].vacunadorSegundaDosis", is("MARIO BROS")))
                .andExpect(jsonPath("$[1].loteDosisUno", is("22227-FH")))
                .andExpect(jsonPath("$[1].fechaPrimeraDosis", is(LocalDate.now().toString())))
                .andExpect(jsonPath("$[1].fechaSegundasDosis", is(LocalDate.now().plusDays(28L).toString())))
                .andExpect(jsonPath("$[1].vacunadorPrimeraDosis", is("JHON DOE")))
                .andExpect(jsonPath("$[1].nombreVacuna", is("PFIZER")));
    }
}