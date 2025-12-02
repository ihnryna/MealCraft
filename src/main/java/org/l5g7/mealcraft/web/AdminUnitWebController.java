package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.units.UnitCreateDto;
import org.l5g7.mealcraft.app.units.UnitDto;
import org.l5g7.mealcraft.app.units.UnitUpdateDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminUnitWebController {

    private final RestClient internalApiClient;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";
    private static final String UNITS_URI = "/units";
    private static final String UNIT_ID_URI = "/units/{id}";
    private static final String REDIRECT_UNITS_URI = "redirect:/mealcraft/admin/unit";
    private static final String UNIT_FORM_FRAGMENT = "fragments/unit-form :: content";


    public AdminUnitWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/unit")
    public String unitsPage(Model model) {
        ResponseEntity<List<UnitDto>> response = internalApiClient.get()
                .uri(UNITS_URI)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {
                });

        List<UnitDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/units :: content");
        model.addAttribute(TITLE, "Units");
        return ADMIN_PAGE;
    }

    @GetMapping("/unit/new")
    public String showCreateUnitForm(Model model) {
        UnitDto unit = new UnitDto();

        model.addAttribute("unit", unit);
        model.addAttribute(TITLE, "Create unit");
        model.addAttribute(FRAGMENT_TO_LOAD, UNIT_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @PostMapping("/unit")
    public String saveUnit(@ModelAttribute("unit") UnitDto unitDto,
                           Model model) {

        try {
            if (unitDto.getId() == null) {
                UnitCreateDto createDto = UnitCreateDto.builder()
                        .name(unitDto.getName())
                        .build();

                internalApiClient
                        .post()
                        .uri(UNITS_URI)
                        .body(createDto)
                        .retrieve()
                        .toBodilessEntity();

            } else {
                UnitUpdateDto updateDto = UnitUpdateDto.builder()
                        .id(unitDto.getId())
                        .name(unitDto.getName())
                        .build();

                internalApiClient
                        .put()
                        .uri(UNIT_ID_URI, unitDto.getId())
                        .body(updateDto)
                        .retrieve()
                        .toBodilessEntity();
            }

            return REDIRECT_UNITS_URI;

        } catch (RestClientResponseException ex) {

            String body = ex.getResponseBodyAsString();
            String message;

            if (!body.isBlank()) {
                message = body;
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                message = "Unit with this name already exists";
            } else {
                message = "Failed to save unit: " + ex.getStatusCode();
            }

            model.addAttribute("unit", unitDto);
            model.addAttribute(TITLE, unitDto.getId() == null ? "Create unit" : "Edit unit");
            model.addAttribute(FRAGMENT_TO_LOAD, UNIT_FORM_FRAGMENT);
            model.addAttribute("errorMessage", message);

            return ADMIN_PAGE;
        }
    }

    @GetMapping("/unit/delete/{id}")
    public String deleteUnit(@PathVariable Long id, Model model) {
        try {
            internalApiClient
                    .delete()
                    .uri(UNIT_ID_URI, id)
                    .retrieve()
                    .toBodilessEntity();

            return REDIRECT_UNITS_URI;

        } catch (RestClientResponseException ex) {

            ResponseEntity<List<UnitDto>> response = internalApiClient.get()
                    .uri(UNITS_URI)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {
                    });

            List<UnitDto> data = response.getBody();

            String body = ex.getResponseBodyAsString();
            String message;

            if (!body.isBlank()) {
                message = body;
            } else if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                message = "This unit cannot be deleted because it is used by existing products.";
            } else {
                message = "Failed to delete unit: " + ex.getStatusCode();
            }

            model.addAttribute("data", data);
            model.addAttribute("errorMessage", message);
            model.addAttribute(FRAGMENT_TO_LOAD, "fragments/units :: content");
            model.addAttribute(TITLE, "Units");

            return ADMIN_PAGE;
        }
    }
}
