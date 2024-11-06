package tn.esprit.spring.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.services.IPisteServices;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PisteRestController.class)
class PisteRestControllerTest {

    @MockBean
    private IPisteServices pisteServices; // Mock service layer

    @Autowired
    private MockMvc mockMvc; // Auto-configured MockMvc

    @Test
    void testAddPiste() throws Exception {
        // Prepare the expected piste object
        Piste newPiste = Piste.builder()
                .numPiste(1L)
                .namePiste("Beginner's Slope")
                .color(Color.GREEN)
                .length(500)
                .slope(15)
                .build();

        // Mock the service call
        when(pisteServices.addPiste(any(Piste.class))).thenReturn(newPiste);

        // Perform the HTTP POST request and assert the response
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
        // Prepare a list of pistes
        List<Piste> pistes = Arrays.asList(
                Piste.builder().numPiste(1L).namePiste("Slope A").color(Color.GREEN).build(),
                Piste.builder().numPiste(2L).namePiste("Slope B").color(Color.RED).build()
        );

        // Mock the service call to return the list
        when(pisteServices.retrieveAllPistes()).thenReturn(pistes);

        // Perform the HTTP GET request and assert the response
        mockMvc.perform(get("/piste/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numPiste").value(1L))
                .andExpect(jsonPath("$[1].namePiste").value("Slope B"));
    }
}
