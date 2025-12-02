package org.l5g7.mealcraft.unittest.units;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.products.ProductRepository;
import org.l5g7.mealcraft.app.units.*;
import org.l5g7.mealcraft.exception.EntityAlreadyExistsException;
import org.l5g7.mealcraft.exception.EntityDoesNotExistException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceImplTest {

    @Mock
    private UnitRepository repository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit testUnit;
    private UnitCreateDto testCreateDto;
    private UnitUpdateDto testUpdateDto;

    @BeforeEach
    void setUp() {
        testUnit = Unit.builder()
                .id(1L)
                .name("kilogram")
                .build();

        testCreateDto = new UnitCreateDto();
        testCreateDto.setName("kilogram");

        testUpdateDto = new UnitUpdateDto();
        testUpdateDto.setName("gram");

    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUnits_Success_ReturnsListOfUnitDtos() {
        List<Unit> units = Arrays.asList(
                Unit.builder().id(1L).name("kilogram").build(),
                Unit.builder().id(2L).name("liter").build(),
                Unit.builder().id(3L).name("piece").build()
        );
        when(repository.findAll()).thenReturn(units);

        List<UnitDto> result = unitService.getAllUnits();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("kilogram", result.get(0).getName());
        assertEquals("liter", result.get(1).getName());
        assertEquals("piece", result.get(2).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllUnits_EmptyRepository_ReturnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<UnitDto> result = unitService.getAllUnits();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getUnitById_ExistingId_ReturnsUnitDto() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));

        UnitDto result = unitService.getUnitById(1L);

        assertNotNull(result);
        assertEquals(testUnit.getId(), result.getId());
        assertEquals(testUnit.getName(), result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void getUnitById_NonExistentId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityDoesNotExistException.class, () -> {
            unitService.getUnitById(999L);
        });
        verify(repository, times(1)).findById(999L);
    }

    @Test
    void createUnit_ValidUnit_ReturnsCreatedUnitDto() {
        when(repository.existsByNameIgnoreCase("kilogram")).thenReturn(false);
        when(repository.save(any(Unit.class))).thenReturn(testUnit);

        UnitDto result = unitService.createUnit(testCreateDto);

        assertNotNull(result);
        assertEquals(testUnit.getId(), result.getId());
        assertEquals(testUnit.getName(), result.getName());
        verify(repository, times(1)).existsByNameIgnoreCase("kilogram");
        verify(repository, times(1)).save(any(Unit.class));
    }

    @Test
    void createUnit_NullName_ThrowsException() {
        UnitCreateDto invalidDto = new UnitCreateDto();
        invalidDto.setName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            unitService.createUnit(invalidDto);
        });
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void createUnit_BlankName_ThrowsException() {
        UnitCreateDto invalidDto = new UnitCreateDto();
        invalidDto.setName("   ");

        assertThrows(IllegalArgumentException.class, () -> {
            unitService.createUnit(invalidDto);
        });
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void createUnit_DuplicateName_ThrowsException() {
        when(repository.existsByNameIgnoreCase("kilogram")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            unitService.createUnit(testCreateDto);
        });
        verify(repository, times(1)).existsByNameIgnoreCase("kilogram");
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void createUnit_CaseInsensitiveDuplicateCheck_ThrowsException() {
        UnitCreateDto upperCaseDto = new UnitCreateDto();
        upperCaseDto.setName("KILOGRAM");
        when(repository.existsByNameIgnoreCase("KILOGRAM")).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            unitService.createUnit(upperCaseDto);
        });
        verify(repository, times(1)).existsByNameIgnoreCase("KILOGRAM");
    }

    @Test
    void updateUnit_ExistingUnit_ReturnsUpdatedUnitDto() {
        Unit updatedUnit = Unit.builder().id(1L).name("gram").build();
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(repository.existsByNameIgnoreCaseAndIdNot("gram", 1L)).thenReturn(false);
        when(repository.save(any(Unit.class))).thenReturn(updatedUnit);

        UnitDto result = unitService.updateUnit(1L, testUpdateDto);

        assertNotNull(result);
        assertEquals("gram", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNameIgnoreCaseAndIdNot("gram", 1L);
        verify(repository, times(1)).save(any(Unit.class));
    }

    @Test
    void updateUnit_NonExistentId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityDoesNotExistException.class, () -> {
            unitService.updateUnit(999L, testUpdateDto);
        });
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void updateUnit_DuplicateName_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(repository.existsByNameIgnoreCaseAndIdNot("gram", 1L)).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            unitService.updateUnit(1L, testUpdateDto);
        });
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).existsByNameIgnoreCaseAndIdNot("gram", 1L);
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void updateUnit_NullName_SavesWithoutChangingName() {
        UnitUpdateDto nullNameDto = new UnitUpdateDto();
        nullNameDto.setName(null);
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(repository.save(any(Unit.class))).thenReturn(testUnit);

        UnitDto result = unitService.updateUnit(1L, nullNameDto);

        assertNotNull(result);
        assertEquals("kilogram", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).existsByNameIgnoreCaseAndIdNot(anyString(), anyLong());
        verify(repository, times(1)).save(any(Unit.class));
    }

    @Test
    void patchUnit_ExistingUnit_ReturnsUpdatedUnitDto() {
        Unit patchedUnit = Unit.builder().id(1L).name("gram").build();
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(repository.existsByNameIgnoreCaseAndIdNot("gram", 1L)).thenReturn(false);
        when(repository.save(any(Unit.class))).thenReturn(patchedUnit);

        UnitDto result = unitService.patchUnit(1L, testUpdateDto);

        assertNotNull(result);
        assertEquals("gram", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Unit.class));
    }

    @Test
    void deleteUnit_ExistingUnitNotInUse_DeletesSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(productRepository.existsByDefaultUnit(testUnit)).thenReturn(false);

        unitService.deleteUnit(1L);

        verify(repository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsByDefaultUnit(testUnit);
        verify(repository, times(1)).delete(testUnit);
    }

    @Test
    void deleteUnit_NonExistentId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityDoesNotExistException.class, () -> {
            unitService.deleteUnit(999L);
        });
        verify(repository, times(1)).findById(999L);
        verify(repository, never()).delete(any(Unit.class));
    }

    @Test
    void deleteUnit_UnitInUse_ThrowsException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUnit));
        when(productRepository.existsByDefaultUnit(testUnit)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            unitService.deleteUnit(1L);
        });

        assertEquals("Cannot delete unit because it is used by some products", exception.getMessage());
        verify(repository, times(1)).findById(1L);
        verify(productRepository, times(1)).existsByDefaultUnit(testUnit);
        verify(repository, never()).delete(any(Unit.class));
    }
}