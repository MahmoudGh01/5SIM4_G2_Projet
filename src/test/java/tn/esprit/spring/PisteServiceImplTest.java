package tn.esprit.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.entities.Color;
import tn.esprit.spring.entities.Piste;
import tn.esprit.spring.repositories.IPisteRepository;
import tn.esprit.spring.services.PisteServicesImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PisteServiceImplTest {  // Updated class name to match the file name

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPiste() {
        Piste newPiste = Piste.builder()
                .namePiste("Beginner's Slope")
                .color(Color.GREEN)
                .length(500)
                .slope(15)
                .build();

        Piste savedPiste = Piste.builder()
                .numPiste(1L)
                .namePiste("Beginner's Slope")
                .color(Color.GREEN)
                .length(500)
                .slope(15)
                .build();

        when(pisteRepository.save(newPiste)).thenReturn(savedPiste);

        Piste result = pisteServiceImpl.addPiste(newPiste);

        assertNotNull(result.getNumPiste());
        assertEquals("Beginner's Slope", result.getNamePiste());
        assertEquals(Color.GREEN, result.getColor());
    }

    @Test
    void testRetrievePiste() {
        Long pisteId = 1L;

        Piste retrievedPiste = Piste.builder()
                .numPiste(pisteId)
                .namePiste("Advanced Slope")
                .color(Color.BLACK)
                .length(800)
                .slope(30)
                .build();

        when(pisteRepository.findById(pisteId)).thenReturn(Optional.of(retrievedPiste));

        Piste result = pisteServiceImpl.retrievePiste(pisteId);

        assertNotNull(result);
        assertEquals(pisteId, result.getNumPiste());
        assertEquals("Advanced Slope", result.getNamePiste());
        assertEquals(Color.BLACK, result.getColor());
    }

    @Test
    void testRetrievePiste_NotFound() {
        Long pisteId = 2L; // Assume this ID does not exist

        when(pisteRepository.findById(pisteId)).thenReturn(Optional.empty());

        Piste result = pisteServiceImpl.retrievePiste(pisteId);

        assertNull(result); // Expecting null since the piste does not exist
        verify(pisteRepository, times(1)).findById(pisteId);
    }

    @Test
    void testAddPiste_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            pisteServiceImpl.addPiste(null);
        });
    }

    @Test
    void testRemovePiste() {
        Long pisteId = 1L;

        doNothing().when(pisteRepository).deleteById(pisteId);

        pisteServiceImpl.removePiste(pisteId);

        verify(pisteRepository, times(1)).deleteById(pisteId);
    }

    @Test
    void testRemovePiste_NotFound() {
        Long pisteId = 2L; // Assume this ID does not exist

        doNothing().when(pisteRepository).deleteById(pisteId);

        pisteServiceImpl.removePiste(pisteId);

        verify(pisteRepository, times(1)).deleteById(pisteId);
    }

    @Test
    void testRetrieveAllPistes() {
        List<Piste> pistes = Arrays.asList(new Piste(), new Piste()); // Mock data
        when(pisteRepository.findAll()).thenReturn(pistes);

        List<Piste> result = pisteServiceImpl.retrieveAllPistes();

        assertEquals(2, result.size());
        verify(pisteRepository, times(1)).findAll();
    }
}
