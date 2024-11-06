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
    private IPisteServices pisteServices;  // Mock the service layer

    @InjectMocks
    private PisteRestController pisteRestController;  // Inject the mocked service into the controller

    private MockMvc mockMvc;  // MockMvc instance to perform HTTP requests

    @BeforeEach
    void setUp() {
        // This is where the error occurred.
        // We need to make sure that `pisteServices` is properly mocked and injected into `pisteRestController`.
        mockMvc = MockMvcBuilders.standaloneSetup(pisteRestController).build();  // Setup MockMvc with controller
    }

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

    @Test
    void testGetPisteById() throws Exception {
        // Prepare a single piste object
        Piste piste = Piste.builder()
                .numPiste(1L)
                .namePiste("Advanced Slope")
                .color(Color.BLACK)
                .length(800)
                .slope(30)
                .build();

        // Mock the service call
        when(pisteServices.retrievePiste(1L)).thenReturn(piste);

        // Perform the HTTP GET request and assert the response
        mockMvc.perform(get("/piste/get/{id-piste}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numPiste").value(1L))
                .andExpect(jsonPath("$.namePiste").value("Advanced Slope"))
                .andExpect(jsonPath("$.color").value("BLACK"));
    }

    @Test
    void testDeletePisteById() throws Exception {
        // Mock the service call to do nothing when removePiste is called
        doNothing().when(pisteServices).removePiste(1L);

        // Perform the HTTP DELETE request and assert the response
        mockMvc.perform(delete("/piste/delete/{id-piste}", 1L))
                .andExpect(status().isOk());

        // Verify the service method was called exactly once
        verify(pisteServices, times(1)).removePiste(1L);
    }

    @Test
    void testGetPisteById_NotFound() throws Exception {
        // Mock the service call to return null when an invalid ID is passed
        when(pisteServices.retrievePiste(999L)).thenReturn(null);

        // Perform the HTTP GET request for an invalid piste ID and assert the response
        mockMvc.perform(get("/piste/get/{id-piste}", 999L))
                .andExpect(status().isNotFound());
    }
}
