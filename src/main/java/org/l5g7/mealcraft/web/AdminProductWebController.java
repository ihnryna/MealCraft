package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.units.UnitDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminProductWebController {

    private final RestClient internalApiClient;

    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String TITLE = "title";
    private static final String ADMIN_PAGE = "admin-page";
    private static final String UNITS_URI = "/units";
    private static final String UNITS_MODEL_ATTR = "units";
    private static final String PRODUCTS_URI = "/products";
    private static final String PRODUCT_ID_URI = "/products/{id}";
    private static final String PRODUCT_MODEL_ATTR = "product";
    private static final String PRODUCT_FORM_FRAGMENT = "fragments/product-form :: content";
    private static final String PRODUCTS_FRAGMENT = "fragments/products :: content";
    private static final String REDIRECT_PRODUCTS_URI = "redirect:/mealcraft/admin/product";


    public AdminProductWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/product")
    public String productsPage(Model model) {
        ResponseEntity<List<ProductDto>> response = internalApiClient.get()
                .uri(PRODUCTS_URI)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {
                });

        List<ProductDto> data = response.getBody();
        model.addAttribute("data", data);
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCTS_FRAGMENT);
        model.addAttribute(TITLE, "Products");
        return ADMIN_PAGE;
    }

    @GetMapping("/product/new")
    public String showCreateProductForm(Model model) {
        ProductDto product = new ProductDto();

        ResponseEntity<List<UnitDto>> unitsResponse = internalApiClient
                .get()
                .uri(UNITS_URI)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UnitDto>>() {
                });

        List<UnitDto> units = unitsResponse.getBody();

        model.addAttribute(PRODUCT_MODEL_ATTR, product);
        model.addAttribute(UNITS_MODEL_ATTR, units);
        model.addAttribute(TITLE, "Create product");
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCT_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @PostMapping("/product")
    public String saveProduct(@ModelAttribute("product") ProductDto productDto,
                              Model model) {

        try {
            if (productDto.getId() == null) {
                internalApiClient
                        .post()
                        .uri(PRODUCTS_URI)
                        .body(productDto)
                        .retrieve()
                        .toBodilessEntity();
            } else {
                internalApiClient
                        .put()
                        .uri(PRODUCT_ID_URI, productDto.getId())
                        .body(productDto)
                        .retrieve()
                        .toBodilessEntity();
            }
            return REDIRECT_PRODUCTS_URI;

        } catch (HttpClientErrorException e) {

            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                List<UnitDto> units = internalApiClient
                        .get()
                        .uri(UNITS_URI)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<UnitDto>>() {
                        });

                model.addAttribute(PRODUCT_MODEL_ATTR, productDto);
                model.addAttribute(UNITS_MODEL_ATTR, units);
                model.addAttribute(TITLE, productDto.getId() == null ? "Create product" : "Edit product");
                model.addAttribute(FRAGMENT_TO_LOAD, PRODUCT_FORM_FRAGMENT);

                model.addAttribute("errorMessage", "Product with this name already exists");

                return ADMIN_PAGE;
            }
            throw e;
        }
    }

    @GetMapping("/product/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {

        ProductDto product = internalApiClient
                .get()
                .uri(PRODUCT_ID_URI, id)
                .retrieve()
                .body(ProductDto.class);

        List<UnitDto> units = internalApiClient
                .get()
                .uri(UNITS_URI)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UnitDto>>() {
                });

        model.addAttribute(PRODUCT_MODEL_ATTR, product);
        model.addAttribute(UNITS_MODEL_ATTR, units);
        model.addAttribute(TITLE, "Edit product");
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCT_FORM_FRAGMENT);

        return ADMIN_PAGE;
    }

    @GetMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {
        try {
            internalApiClient
                    .delete()
                    .uri(PRODUCT_ID_URI, id)
                    .retrieve()
                    .toBodilessEntity();

            return REDIRECT_PRODUCTS_URI;

        } catch (HttpClientErrorException e) {
            String message = e.getResponseBodyAsString();
            ResponseEntity<List<ProductDto>> response = internalApiClient
                    .get()
                    .uri(PRODUCTS_URI)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {
                    });

            List<ProductDto> data = response.getBody();

            model.addAttribute("data", data);
            model.addAttribute("errorMessage", message);
            model.addAttribute(FRAGMENT_TO_LOAD, PRODUCTS_FRAGMENT);
            model.addAttribute(TITLE, "Products");

            return ADMIN_PAGE;
        }
    }
}
