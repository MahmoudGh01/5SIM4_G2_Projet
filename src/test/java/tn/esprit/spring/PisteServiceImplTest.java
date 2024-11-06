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
class PisteServiceImplTest {

    @Mock
    private IPisteRepository pisteRepository;

    @InjectMocks
    private PisteServicesImpl pisteServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPiste_Valid() {
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
    void testAddPiste_NullInput() {
        Piste newPiste = null;
        assertThrows(IllegalArgumentException.class, () -> pisteServiceImpl.addPiste(newPiste));
    }

    @Test
    void testRetrievePiste_ExistingId() {
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
    void testRetrievePiste_NonExistingId() {
        Long pisteId = 1L;

        when(pisteRepository.findById(pisteId)).thenReturn(Optional.empty());

        Piste result = pisteServiceImpl.retrievePiste(pisteId);

        assertNull(result, "Expected null when piste ID is not found");
    }

    @Test
    void testRemovePiste_ValidId() {
        Long pisteId = 1L;

        doNothing().when(pisteRepository).deleteById(pisteId);

        pisteServiceImpl.removePiste(pisteId);

        verify(pisteRepository, times(1)).deleteById(pisteId);
    }

    @Test
    void testRemovePiste_InvalidId() {
        Long pisteId = -1L;

        doThrow(new IllegalArgumentException("Invalid piste ID")).when(pisteRepository).deleteById(pisteId);

        assertThrows(IllegalArgumentException.class, () -> pisteServiceImpl.removePiste(pisteId));
    }

    @Test
    void testRetrieveAllPistes() {
        List<Piste> pistes = Arrays.asList(
                Piste.builder().numPiste(1L).namePiste("Slope A").color(Color.GREEN).build(),
                Piste.builder().numPiste(2L).namePiste("Slope B").color(Color.RED).build()
        );

        when(pisteRepository.findAll()).thenReturn(pistes);

        List<Piste> result = pisteServiceImpl.retrieveAllPistes();

        assertEquals(2, result.size());
        verify(pisteRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveAllPistes_NoPistes() {
        when(pisteRepository.findAll()).thenReturn(Arrays.asList());

        List<Piste> result = pisteServiceImpl.retrieveAllPistes();

        assertTrue(result.isEmpty(), "Expected an empty list when no pistes are available");
    }

    @Test
    void testRetrievePisteWithBoundaryValues() {
        Piste boundaryPiste = Piste.builder()
                .numPiste(2L)
                .namePiste("Boundary Slope")
                .color(Color.BLUE)
                .length(0) // testing lower boundary
                .slope(90) // testing upper boundary
                .build();

        when(pisteRepository.save(boundaryPiste)).thenReturn(boundaryPiste);

        Piste result = pisteServiceImpl.addPiste(boundaryPiste);

        assertNotNull(result);
        assertEquals(0, result.getLength(), "Length should match the boundary value 0");
        assertEquals(90, result.getSlope(), "Slope should match the boundary value 90");
    }
}
