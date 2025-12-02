package org.l5g7.mealcraft.unittest.units;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.l5g7.mealcraft.app.units.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

    @Mock
    private UnitServiceImpl unitServiceImpl;

    @InjectMocks
    private UnitController unitController;

    private UnitDto testUnitDto;
    private UnitCreateDto testCreateDto;
    private UnitUpdateDto testUpdateDto;

    @BeforeEach
    void setUp() {
        testUnitDto = new UnitDto(1L, "kilogram");
        testCreateDto = new UnitCreateDto();
        testCreateDto.setName("kilogram");
        testUpdateDto = new UnitUpdateDto();
        testUpdateDto.setName("gram");
    }

    @Test
    void getAllUnits_Success_ReturnsListOfUnits() {
        List<UnitDto> expectedUnits = Arrays.asList(
                new UnitDto(1L, "kilogram"),
                new UnitDto(2L, "liter"),
                new UnitDto(3L, "piece")
        );
        when(unitServiceImpl.getAllUnits()).thenReturn(expectedUnits);

        List<UnitDto> result = unitController.getAllUnits();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedUnits, result);
        verify(unitServiceImpl, times(1)).getAllUnits();
    }

    @Test
    void getAllUnits_EmptyList_ReturnsEmptyList() {
        when(unitServiceImpl.getAllUnits()).thenReturn(List.of());

        List<UnitDto> result = unitController.getAllUnits();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(unitServiceImpl, times(1)).getAllUnits();
    }

    @Test
    void getUnit_ValidId_ReturnsUnit() {
        long unitId = 1L;
        when(unitServiceImpl.getUnitById(unitId)).thenReturn(testUnitDto);

        UnitDto result = unitController.getUnit(unitId);

        assertNotNull(result);
        assertEquals(testUnitDto.getId(), result.getId());
        assertEquals(testUnitDto.getName(), result.getName());
        verify(unitServiceImpl, times(1)).getUnitById(unitId);
    }

    @Test
    void getUnit_NonExistentId_ThrowsException() {
        long nonExistentId = 999L;
        when(unitServiceImpl.getUnitById(nonExistentId))
                .thenThrow(new RuntimeException("Unit not found"));

        assertThrows(RuntimeException.class, () -> {
            unitController.getUnit(nonExistentId);
        });
        verify(unitServiceImpl, times(1)).getUnitById(nonExistentId);
    }

    @Test
    void createUnit_ValidUnit_ReturnsCreatedUnit() {
        when(unitServiceImpl.createUnit(any(UnitCreateDto.class))).thenReturn(testUnitDto);

        UnitDto result = unitController.createUnit(testCreateDto);

        assertNotNull(result);
        assertEquals(testUnitDto.getId(), result.getId());
        assertEquals(testUnitDto.getName(), result.getName());
        verify(unitServiceImpl, times(1)).createUnit(testCreateDto);
    }

    @Test
    void createUnit_DuplicateName_ThrowsException() {
        when(unitServiceImpl.createUnit(any(UnitCreateDto.class)))
                .thenThrow(new RuntimeException("Unit already exists"));

        assertThrows(RuntimeException.class, () -> {
            unitController.createUnit(testCreateDto);
        });
        verify(unitServiceImpl, times(1)).createUnit(testCreateDto);
    }

    @Test
    void updateUnit_ValidIdAndData_ReturnsUpdatedUnit() {
        long unitId = 1L;
        UnitDto updatedUnit = new UnitDto(unitId, "gram");
        when(unitServiceImpl.updateUnit(eq(unitId), any(UnitUpdateDto.class)))
                .thenReturn(updatedUnit);

        UnitDto result = unitController.updateUnit(unitId, testUpdateDto);

        assertNotNull(result);
        assertEquals(updatedUnit.getId(), result.getId());
        assertEquals(updatedUnit.getName(), result.getName());
        verify(unitServiceImpl, times(1)).updateUnit(unitId, testUpdateDto);
    }

    @Test
    void updateUnit_NonExistentId_ThrowsException() {
        long nonExistentId = 999L;
        when(unitServiceImpl.updateUnit(eq(nonExistentId), any(UnitUpdateDto.class)))
                .thenThrow(new RuntimeException("Unit not found"));

        assertThrows(RuntimeException.class, () -> {
            unitController.updateUnit(nonExistentId, testUpdateDto);
        });
        verify(unitServiceImpl, times(1)).updateUnit(nonExistentId, testUpdateDto);
    }

    @Test
    void patchUnit_ValidIdAndData_ReturnsUpdatedUnit() {
        long unitId = 1L;
        UnitDto patchedUnit = new UnitDto(unitId, "gram");
        when(unitServiceImpl.patchUnit(eq(unitId), any(UnitUpdateDto.class)))
                .thenReturn(patchedUnit);

        UnitDto result = unitController.patchUnit(unitId, testUpdateDto);

        assertNotNull(result);
        assertEquals(patchedUnit.getId(), result.getId());
        assertEquals(patchedUnit.getName(), result.getName());
        verify(unitServiceImpl, times(1)).patchUnit(unitId, testUpdateDto);
    }

    @Test
    void patchUnit_NonExistentId_ThrowsException() {
        long nonExistentId = 999L;
        when(unitServiceImpl.patchUnit(eq(nonExistentId), any(UnitUpdateDto.class)))
                .thenThrow(new RuntimeException("Unit not found"));

        assertThrows(RuntimeException.class, () -> {
            unitController.patchUnit(nonExistentId, testUpdateDto);
        });
        verify(unitServiceImpl, times(1)).patchUnit(nonExistentId, testUpdateDto);
    }

    @Test
    void deleteUnit_ValidId_DeletesSuccessfully() {
        long unitId = 1L;
        doNothing().when(unitServiceImpl).deleteUnit(unitId);

        unitController.deleteUnit(unitId);

        verify(unitServiceImpl, times(1)).deleteUnit(unitId);
    }

    @Test
    void deleteUnit_NonExistentId_ThrowsException() {
        long nonExistentId = 999L;
        doThrow(new RuntimeException("Unit not found"))
                .when(unitServiceImpl).deleteUnit(nonExistentId);

        assertThrows(RuntimeException.class, () -> {
            unitController.deleteUnit(nonExistentId);
        });
        verify(unitServiceImpl, times(1)).deleteUnit(nonExistentId);
    }

    @Test
    void deleteUnit_UnitInUse_ThrowsException() {
        long unitId = 1L;
        doThrow(new IllegalArgumentException("Cannot delete unit because it is used by some products"))
                .when(unitServiceImpl).deleteUnit(unitId);

        assertThrows(IllegalArgumentException.class, () -> {
            unitController.deleteUnit(unitId);
        });
        verify(unitServiceImpl, times(1)).deleteUnit(unitId);
    }
}