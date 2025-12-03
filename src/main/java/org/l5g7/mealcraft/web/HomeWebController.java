package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpSession;
import org.l5g7.mealcraft.app.mealplan.EventCell;
import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.products.ProductDto;
import org.l5g7.mealcraft.app.recipes.RecipeDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.units.UnitDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.*;

@Controller
public class HomeWebController {

    private final RestClient internalApiClient;
    private final UserService userService;
    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String USERNAME = "username";
    private static final String TITLE = "title";
    private static final String RECIPE = "recipe";
    private static final String RECIPE_FORM_FRAGMENT = "fragments/recipe-form :: content";
    private static final String REDIRECT_HOME_PAGE = "redirect:/mealcraft/home";
    private static final String RECIPE_ID_URI = "/recipes/{id}";
    private static final String REDIRECT_RECIPE_PAGE = "redirect:/mealcraft/user/recipes";
    private static final String REDIRECT_PRODUCTS_URI = "redirect:/mealcraft/user/products";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String RECIPE_URI = "/recipes";
    private static final String PRODUCTS_URI = "/products";
    private static final String PRODUCT_ID_URI = "/products/{id}";
    private static final String PRODUCT_MODEL_ATTR = "product";
    private static final String PRODUCT_FORM_FRAGMENT = "fragments/product-form :: content";
    private static final String PRODUCTS_FRAGMENT = "fragments/products :: content";
    private static final String UNITS_URI = "/units";
    private static final String UNITS_MODEL_ATTR = "units";


    public HomeWebController(@Qualifier("internalApiClient") RestClient internalApiClient, UserService userService) {
        this.internalApiClient = internalApiClient;
        this.userService = userService;
    }

    @GetMapping("/mealcraft/home")
    public String showHome(@RequestParam(value = "month", required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                           LocalDate month,
                           Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/mealcraft/admin/home";
        }

        String username = auth.getName();
        model.addAttribute(USERNAME, username);

        addMonthCalendarToModel(model, month);
        addCalendarMealPlansToModel(model, username);
        addShoppingItemsToModel(model, username);

        model.addAttribute(TITLE, "MealCraft — Main Page");
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/calendar :: calendarFragment");
        return "home";
    }


    @GetMapping("/mealcraft/home/{day}")
    public String showDayPage(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = auth.getName();
        model.addAttribute(USERNAME, username);

        model.addAttribute(TITLE, "MealCraft — Main Page");
        addPageMealPlansToModel(model, username, day);
        addShoppingItemsToModel(model, username);
        model.addAttribute("day", day);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/day :: dayFragment");
        return "home";
    }

    @GetMapping("/mealcraft/home/recipes/create")
    public String showUserRecipeForm(@RequestParam(value = "month", required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                           LocalDate month,
                           Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        model.addAttribute(TITLE, "Create your own recipe");

        RecipeDto recipe = new RecipeDto();
        model.addAttribute(RECIPE, recipe);
        model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);

        return "home";
    }

    @GetMapping("/mealcraft/home/products/create")
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
        model.addAttribute(TITLE, "Create your own product");
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCT_FORM_FRAGMENT);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        return "home";
    }

    @GetMapping("/mealcraft/user/recipes")
    public String showUserRecipePage(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        model.addAttribute(TITLE, "Your own recipes");

        ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                .uri(RECIPE_URI)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<RecipeDto>>() {});

        List<RecipeDto> data = response.getBody();
        List<RecipeDto> recipes = new ArrayList<>();
        if(data != null) {
             recipes = data.stream().filter(r -> r.getOwnerUserId()!=null && r.getOwnerUserId().equals(userService.getUserByUsername(username).id()))
                    .toList();
        }
        model.addAttribute("data", recipes);
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipes :: content");
        model.addAttribute(TITLE, "Your own recipes");
        return "home";
    }

    @GetMapping("/mealcraft/user/products")
    public String showUserProductsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);

        ResponseEntity<List<ProductDto>> response = internalApiClient.get()
                .uri(PRODUCTS_URI)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ProductDto>>() {});

        List<ProductDto> data = response.getBody();
        List<ProductDto> products = new ArrayList<>();
        if(data != null) {
            products = data.stream().filter(r -> r.getOwnerUserId()!=null && r.getOwnerUserId().equals(userService.getUserByUsername(username).id()))
                    .toList();
        }
        model.addAttribute("data", products);
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCTS_FRAGMENT);
        model.addAttribute(TITLE, "Your own products");
        return "home";
    }

    @GetMapping("/mealcraft/home/recipes/edit/{id}")
    public String showUserEditRecipeForm(@PathVariable Long id, Model model) {

        RecipeDto recipe = internalApiClient
                .get()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute(RECIPE, recipe);
        model.addAttribute(TITLE, "Edit your own recipe");
        model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        return "home";
    }

    @GetMapping("/mealcraft/home/products/edit/{id}")
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
        model.addAttribute(TITLE, "Edit your own product");
        model.addAttribute(FRAGMENT_TO_LOAD, PRODUCT_FORM_FRAGMENT);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        return "home";
    }

    @PostMapping("/mealcraft/user/recipe")
    public String saveRecipe(@ModelAttribute("recipe") RecipeDto recipeDto, Model model) {

        String title = (recipeDto.getId() == null)
                ? "Create your own recipe"
                : "Edit your own recipe";

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        recipeDto.setOwnerUserId(userService.getUserByUsername(username).id());

        try {
            if (recipeDto.getId() == null) {
                internalApiClient
                        .post()
                        .uri(RECIPE_URI)
                        .body(recipeDto)
                        .retrieve()
                        .toBodilessEntity();
            } else {
                internalApiClient
                        .put()
                        .uri(RECIPE_ID_URI, recipeDto.getId())
                        .body(recipeDto)
                        .retrieve()
                        .toBodilessEntity();
            }

            return REDIRECT_HOME_PAGE;

        } catch (RestClientResponseException ex) {

            String message;
            String body = ex.getResponseBodyAsString();

            if (!body.isBlank()) {
                message = body;
            } else {
                message = "Failed to save recipe: " + ex.getStatusCode();
            }

            model.addAttribute(RECIPE, recipeDto);
            model.addAttribute(TITLE, title);
            model.addAttribute(FRAGMENT_TO_LOAD, RECIPE_FORM_FRAGMENT);

            model.addAttribute(USERNAME, username);
            addShoppingItemsToModel(model, username);
            model.addAttribute(ERROR_MESSAGE, message);
            return "home";
        }
    }

    @PostMapping("/mealcraft/user/product")
    public String saveProduct(@ModelAttribute("product") ProductDto productDto,
                              Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        productDto.setOwnerUserId(userService.getUserByUsername(username).id());

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

                model.addAttribute(USERNAME, username);
                addShoppingItemsToModel(model, username);
                model.addAttribute(ERROR_MESSAGE, "Product with this name already exists");
                return "home";
            }
            throw e;
        }
    }

    @PostMapping("/mealcraft/home/recipes/delete/{id}")
    public String deleteRecipe(@PathVariable Long id, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        try {
            internalApiClient
                    .delete()
                    .uri(RECIPE_ID_URI, id)
                    .retrieve()
                    .toBodilessEntity();

            return REDIRECT_RECIPE_PAGE;

        } catch (RestClientResponseException ex) {
            String body = ex.getResponseBodyAsString();
            String message = !body.isBlank()
                    ? body
                    : "Failed to delete recipe: " + ex.getStatusCode();

            ResponseEntity<List<RecipeDto>> response = internalApiClient.get()
                    .uri(RECIPE_URI)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<RecipeDto>>() {});

            List<RecipeDto> data = response.getBody();
            List<RecipeDto> recipes = new ArrayList<>();
            if(data != null) {
                recipes = data.stream().filter(r -> r.getOwnerUserId()!=null && r.getOwnerUserId().equals(userService.getUserByUsername(username).id()))
                        .toList();
            }
            model.addAttribute("data", recipes);
            model.addAttribute(ERROR_MESSAGE, message);
            model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipes :: content");
            model.addAttribute(TITLE, "Recipes");


            model.addAttribute(USERNAME, username);
            addShoppingItemsToModel(model, username);
            model.addAttribute(ERROR_MESSAGE, message);
            return "home";
        }
    }

    @GetMapping("/mealcraft/home/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

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
            model.addAttribute(ERROR_MESSAGE, message);
            model.addAttribute(FRAGMENT_TO_LOAD, PRODUCTS_FRAGMENT);
            model.addAttribute(TITLE, "Products");

            model.addAttribute(USERNAME, username);
            addShoppingItemsToModel(model, username);
            model.addAttribute(ERROR_MESSAGE, message);
            return "home";
        }
    }

    @GetMapping("/mealcraft/home/recipes/view/{id}")
    public String viewRecipe(@PathVariable Long id, Model model) {
        RecipeDto recipe = internalApiClient
                .get()
                .uri(RECIPE_ID_URI, id)
                .retrieve()
                .body(RecipeDto.class);

        model.addAttribute(RECIPE, recipe);
        model.addAttribute(TITLE, "Recipe details");
        model.addAttribute(FRAGMENT_TO_LOAD, "fragments/recipe-details :: content");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        addShoppingItemsToModel(model, username);
        return "home";
    }


    @PostMapping("/mealcraft/shopping/toggle")
    public String toggleChecked(@RequestParam Long id) {

        internalApiClient.patch()
                .uri("/shopping-items/toggle/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return REDIRECT_HOME_PAGE;
    }

    @PostMapping("/mealcraft/user/color")
    public String setUserColor(@RequestParam String color, HttpSession session) {
        session.setAttribute("themeColor", color);
        return REDIRECT_HOME_PAGE;
    }

    @GetMapping("/mealcraft/craft")
    public String showCraftPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/mealcraft/admin/home";
        }

        String username = auth.getName();
        model.addAttribute(USERNAME, username);
        model.addAttribute(TITLE, "MealCraft — Recipe Craft");
        model.addAttribute(FRAGMENT_TO_LOAD, "craft-page :: content");

        return "home";
    }

    private void putAtSlot(List<EventCell> list, int slot, EventCell cell) {
        while (list.size() <= slot) {
            list.add(null);
        }
        list.set(slot, cell);
    }

    private void addMonthCalendarToModel(Model model, LocalDate month) {

        LocalDate now = (month != null) ? month : LocalDate.now();
        model.addAttribute("prevMonth", now.minusMonths(1));
        model.addAttribute("nextMonth", now.plusMonths(1));

        YearMonth currentMonth = (month != null) ? YearMonth.from(month) : YearMonth.now();
        LocalDate firstOfMonth = currentMonth.atDay(1);
        DayOfWeek firstDayOfWeek = firstOfMonth.getDayOfWeek();

        List<List<LocalDate>> weeks = new ArrayList<>();
        List<LocalDate> week = new ArrayList<>();

        int emptyDays = (firstDayOfWeek.getValue() + 6) % 7;
        for (int i = 0; i < emptyDays; i++) week.add(null);

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            week.add(LocalDate.of(currentMonth.getYear(), currentMonth.getMonth(), day));
            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }
        if (!week.isEmpty()) {
            while (week.size() < 7) week.add(null);
            weeks.add(week);
        }

        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        model.addAttribute("month", monthNames[currentMonth.getMonthValue() - 1]);

        model.addAttribute("weeks", weeks);
    }

    private void addShoppingItemsToModel(Model model, String username) {
        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items/getUserShoppingItems/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<ShoppingItemDto> shoppingItems = response.getBody();
        model.addAttribute("shoppingItems", shoppingItems);
    }

    private void addCalendarMealPlansToModel(Model model, String username) {

        ResponseEntity<List<MealPlanDto>> responseMeals = internalApiClient.get()
                .uri("/meal-plans/user/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
        List<MealPlanDto> events = responseMeals.getBody();
        Map<LocalDate, ArrayList<EventCell>> dayEventMap = new TreeMap<>();

        if (events != null) {
            events.sort(Comparator.comparing(MealPlanDto::getPlanDate));

            for (MealPlanDto ev : events) {
                Date date = ev.getPlanDate();
                LocalDate start = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = start.plusDays(ev.getServings() - 1L);
                int slot = 0;
                boolean foundSlot = false;
                for (LocalDate i = start; i.isBefore(end) || i.isEqual(end); i = i.plusDays(1)) {
                    dayEventMap.computeIfAbsent(i, k -> new ArrayList<>());
                    if (!foundSlot) {
                        slot = dayEventMap.get(i).size();
                        foundSlot = true;
                    }
                    putAtSlot(dayEventMap.get(i), slot, new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                }
            }
        }

        model.addAttribute("dayEventMap", dayEventMap);
    }

    private void addPageMealPlansToModel(Model model, String username, LocalDate day) {
        ResponseEntity<List<MealPlanDto>> responseMeals = internalApiClient.get()
                .uri("/meal-plans/user/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });

        List<MealPlanDto> events = responseMeals.getBody();
        Map<LocalDate, ArrayList<MealPlanDto>> dayEventMapForDay = new TreeMap<>();

        if (events != null) {
            events.sort(Comparator.comparing(MealPlanDto::getPlanDate));

            for (MealPlanDto ev : events) {
                Date date = ev.getPlanDate();
                LocalDate start = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = start.plusDays(ev.getServings()-1L);
                for (LocalDate i = start; i.isBefore(end) || i.isEqual(end); i = i.plusDays(1)) {
                    dayEventMapForDay.computeIfAbsent(i, k -> new ArrayList<>());
                    dayEventMapForDay.get(i).add(ev);
                }
            }
        }

        model.addAttribute("dayEventMapForDay", dayEventMapForDay);
        Date d = Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
        model.addAttribute("dayDate", d);
    }

}
