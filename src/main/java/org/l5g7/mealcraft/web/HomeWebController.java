package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpSession;
import org.l5g7.mealcraft.app.shoppingItem.ShoppingItemDto;
import org.l5g7.mealcraft.app.user.User;
import org.l5g7.mealcraft.logging.LogUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.*;

@Controller
public class HomeWebController {

    private final RestClient internalApiClient;

    public HomeWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/mealcraft/home")
    public String showHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getAuthorities());
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/mealcraft/admin/home";
        }

        YearMonth currentMonth = YearMonth.now();
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


        //auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        model.addAttribute("month", monthNames[currentMonth.getMonthValue() - 1]);

        model.addAttribute("weeks", weeks);
        model.addAttribute("title", "MealCraft — Головна");

        //TODO: change to actual userId from db
        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items/getUserShoppingItems/{id}", 2L)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ShoppingItemDto>>() {
                });

        List<ShoppingItemDto> shoppingItems = response.getBody();

        model.addAttribute("shoppingItems", shoppingItems);

        return "home";
    }

    @PostMapping("/mealcraft/shopping/toggle")
    public String toggleChecked(@RequestParam Long id) {
        ShoppingItemDto dto = new ShoppingItemDto();
        dto.setStatus(true);
        internalApiClient.patch()
                .uri("/shopping-items/toggle/{id}", id)
                .retrieve()
                .toBodilessEntity();
        return "redirect:/mealcraft/home";
    }

    @PostMapping("/mealcraft/user/color")
    public String setUserColor(@RequestParam String color, HttpSession session) {
        session.setAttribute("themeColor", color);
        return "redirect:/mealcraft/home";
    }

}
