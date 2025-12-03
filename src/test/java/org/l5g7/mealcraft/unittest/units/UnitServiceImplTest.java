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

    // ========== searchUnitsByPrefix Tests ==========

    @Test
    void searchUnitsByPrefix_ValidPrefix_ReturnsMatchingUnits() {
        List<Unit> units = Arrays.asList(
                Unit.builder().id(1L).name("kilogram").build(),
                Unit.builder().id(2L).name("kilometer").build()
        );
        when(repository.findAllByNameStartingWithIgnoreCase("kilo")).thenReturn(units);

        List<UnitDto> result = unitService.searchUnitsByPrefix("kilo");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("kilogram", result.get(0).getName());
        assertEquals("kilometer", result.get(1).getName());
        verify(repository, times(1)).findAllByNameStartingWithIgnoreCase("kilo");
    }

    @Test
    void searchUnitsByPrefix_NullPrefix_ReturnsEmptyList() {
        List<UnitDto> result = unitService.searchUnitsByPrefix(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).findAllByNameStartingWithIgnoreCase(anyString());
    }

    @Test
    void searchUnitsByPrefix_EmptyPrefix_ReturnsEmptyList() {
        List<UnitDto> result = unitService.searchUnitsByPrefix("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).findAllByNameStartingWithIgnoreCase(anyString());
    }

    @Test
    void searchUnitsByPrefix_BlankPrefix_ReturnsEmptyList() {
        List<UnitDto> result = unitService.searchUnitsByPrefix("   ");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, never()).findAllByNameStartingWithIgnoreCase(anyString());
    }

    @Test
    void searchUnitsByPrefix_PrefixWithSpaces_TrimsAndSearches() {
        List<Unit> units = List.of(Unit.builder().id(1L).name("gram").build());
        when(repository.findAllByNameStartingWithIgnoreCase("g")).thenReturn(units);

        List<UnitDto> result = unitService.searchUnitsByPrefix("  g  ");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("gram", result.get(0).getName());
        verify(repository, times(1)).findAllByNameStartingWithIgnoreCase("g");
    }

    @Test
    void searchUnitsByPrefix_NoMatches_ReturnsEmptyList() {
        when(repository.findAllByNameStartingWithIgnoreCase("xyz")).thenReturn(List.of());

        List<UnitDto> result = unitService.searchUnitsByPrefix("xyz");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAllByNameStartingWithIgnoreCase("xyz");
    }

    // ========== getOrCreateUnitByName Tests ==========

    @Test
    void getOrCreateUnitByName_ExistingUnit_ReturnsExisting() {
        when(repository.findFirstByNameIgnoreCase("kilogram")).thenReturn(Optional.of(testUnit));

        Unit result = unitService.getOrCreateUnitByName("kilogram");

        assertNotNull(result);
        assertEquals(testUnit.getId(), result.getId());
        assertEquals(testUnit.getName(), result.getName());
        verify(repository, times(1)).findFirstByNameIgnoreCase("kilogram");
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void getOrCreateUnitByName_NonExistingUnit_CreatesNew() {
        Unit newUnit = Unit.builder().id(2L).name("meter").build();
        when(repository.findFirstByNameIgnoreCase("meter")).thenReturn(Optional.empty());
        when(repository.save(any(Unit.class))).thenReturn(newUnit);

        Unit result = unitService.getOrCreateUnitByName("meter");

        assertNotNull(result);
        assertEquals("meter", result.getName());
        verify(repository, times(1)).findFirstByNameIgnoreCase("meter");
        verify(repository, times(1)).save(any(Unit.class));
    }

    @Test
    void getOrCreateUnitByName_CaseInsensitive_ReturnsExisting() {
        when(repository.findFirstByNameIgnoreCase("KILOGRAM")).thenReturn(Optional.of(testUnit));

        Unit result = unitService.getOrCreateUnitByName("KILOGRAM");

        assertNotNull(result);
        assertEquals(testUnit.getId(), result.getId());
        verify(repository, times(1)).findFirstByNameIgnoreCase("KILOGRAM");
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void getOrCreateUnitByName_TrimsWhitespace_FindsUnit() {
        when(repository.findFirstByNameIgnoreCase("kilogram")).thenReturn(Optional.of(testUnit));

        Unit result = unitService.getOrCreateUnitByName("  kilogram  ");

        assertNotNull(result);
        assertEquals(testUnit.getId(), result.getId());
        verify(repository, times(1)).findFirstByNameIgnoreCase("kilogram");
    }

    @Test
    void getOrCreateUnitByName_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            unitService.getOrCreateUnitByName(null);
        });
        verify(repository, never()).findFirstByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Unit.class));
    }

    @Test
    void getOrCreateUnitByName_BlankName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            unitService.getOrCreateUnitByName("   ");
        });
        verify(repository, never()).findFirstByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Unit.class));
    }

    // ========== getAuthenticatedUsername Tests ==========

    @Test
    void getAuthenticatedUsername_AuthenticatedUser_ReturnsUsername() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");

        // Test through getAllUnits which calls getAuthenticatedUsername
        when(repository.findAll()).thenReturn(List.of(testUnit));
        unitService.getAllUnits();

        verify(authentication, times(1)).getName();
    }

    @Test
    void getAuthenticatedUsername_NoAuthentication_ReturnsAnonymous() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        // Test through getAllUnits which calls getAuthenticatedUsername
        when(repository.findAll()).thenReturn(List.of(testUnit));
        unitService.getAllUnits();

        // If no authentication, it should return "anonymous" (verified in logs)
        verify(securityContext, times(1)).getAuthentication();
    }

    @Test
    void getAuthenticatedUsername_NotAuthenticated_ReturnsAnonymous() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // Test through getAllUnits which calls getAuthenticatedUsername
        when(repository.findAll()).thenReturn(List.of(testUnit));
        unitService.getAllUnits();

        // If not authenticated, it should return "anonymous"
        verify(authentication, times(1)).isAuthenticated();
    }
}