package tn.esprit.spring.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PisteRestControllerTest {

    @Mock
    private IPisteServices pisteServices;

    @InjectMocks
    private PisteRestController pisteRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pisteRestController).build();
    }

    @Test
    void testAddPiste() throws Exception {
        Piste newPiste = Piste.builder()
                .numPiste(1L)
                .namePiste("Beginner's Slope")
                .color(Color.GREEN)
                .length(500)
                .slope(15)
                .build();

        when(pisteServices.addPiste(any(Piste.class))).thenReturn(newPiste);

        mockMvc.perform(post("/piste/add")
                        .contentType("application/json")
                        .content("{\"namePiste\":\"Beginner's Slope\",\"color\":\"GREEN\",\"length\":500,\"slope\":15}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste").value(1L))
                .andExpect(jsonPath("$.namePiste").value("Beginner's Slope"))
                .andExpect(jsonPath("$.color").value("GREEN"))
                .andExpect(jsonPath("$.length").value(500));
    }

    @Test
    void testGetAllPistes() throws Exception {
        List<Piste> pistes = Arrays.asList(
                Piste.builder().numPiste(1L).namePiste("Slope A").color(Color.GREEN).build(),
                Piste.builder().numPiste(2L).namePiste("Slope B").color(Color.RED).build()
        );

        when(pisteServices.retrieveAllPistes()).thenReturn(pistes);

        mockMvc.perform(get("/piste/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numPiste").value(1L))
                .andExpect(jsonPath("$[1].namePiste").value("Slope B"));
    }

    @Test
    void testGetPisteById() throws Exception {
        Piste piste = Piste.builder()
                .numPiste(1L)
                .namePiste("Advanced Slope")
                .color(Color.BLACK)
                .length(800)
                .slope(30)
                .build();

        when(pisteServices.retrievePiste(1L)).thenReturn(piste);

        mockMvc.perform(get("/piste/get/{id-piste}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste").value(1L))
                .andExpect(jsonPath("$.namePiste").value("Advanced Slope"))
                .andExpect(jsonPath("$.color").value("BLACK"));
    }

    @Test
    void testDeletePisteById() throws Exception {
        doNothing().when(pisteServices).removePiste(1L);

        mockMvc.perform(delete("/piste/delete/{id-piste}", 1L))
                .andExpect(status().isOk());

        verify(pisteServices, times(1)).removePiste(1L);
    }

    @Test
    void testGetPisteById_NotFound() throws Exception {
        when(pisteServices.retrievePiste(999L)).thenReturn(null);

        mockMvc.perform(get("/piste/get/{id-piste}", 999L))
                .andExpect(status().isNotFound());
    }
}
