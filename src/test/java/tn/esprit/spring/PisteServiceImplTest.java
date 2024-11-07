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

    // Helper method to create Piste objects
    private Piste createPiste(Long id, String name, Color color, int length, int slope) {
        return Piste.builder()
                .numPiste(id)
                .namePiste(name)
                .color(color)
                .length(length)
                .slope(slope)
                .build();
    }

    // Helper method to assert Piste properties
    private void assertPiste(Piste piste, Long expectedId, String expectedName, Color expectedColor, int expectedLength, int expectedSlope) {
        assertNotNull(piste);
        assertEquals(expectedId, piste.getNumPiste());
        assertEquals(expectedName, piste.getNamePiste());
        assertEquals(expectedColor, piste.getColor());
        assertEquals(expectedLength, piste.getLength());
        assertEquals(expectedSlope, piste.getSlope());
    }

    @Test
    void testAddPiste_Valid() {
        Piste newPiste = createPiste(null, "Beginner's Slope", Color.GREEN, 500, 15);
        Piste savedPiste = createPiste(1L, "Beginner's Slope", Color.GREEN, 500, 15);

        when(pisteRepository.save(newPiste)).thenReturn(savedPiste);

        Piste result = pisteServiceImpl.addPiste(newPiste);

        // Using helper method for assertions
        assertPiste(result, 1L, "Beginner's Slope", Color.GREEN, 500, 15);
    }

    @Test
    void testAddPiste_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> pisteServiceImpl.addPiste(null));
    }

    @Test
    void testRetrievePiste_ExistingId() {
        Long pisteId = 1L;
        Piste retrievedPiste = createPiste(pisteId, "Advanced Slope", Color.BLACK, 800, 30);

        when(pisteRepository.findById(pisteId)).thenReturn(Optional.of(retrievedPiste));

        Piste result = pisteServiceImpl.retrievePiste(pisteId);

        // Using helper method for assertions
        assertPiste(result, pisteId, "Advanced Slope", Color.BLACK, 800, 30);
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
                createPiste(1L, "Slope A", Color.GREEN, 500, 20),
                createPiste(2L, "Slope B", Color.RED, 600, 25)
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
        Piste boundaryPiste = createPiste(2L, "Boundary Slope", Color.BLUE, 0, 90);

        when(pisteRepository.save(boundaryPiste)).thenReturn(boundaryPiste);

        Piste result = pisteServiceImpl.addPiste(boundaryPiste);

        // Using helper method for assertions
        assertPiste(result, 2L, "Boundary Slope", Color.BLUE, 0, 90);
    }
}
