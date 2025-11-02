package org.l5g7.mealcraft.web;

import org.l5g7.mealcraft.app.shoppingItem.ShoppingItemDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.*;

@Controller
public class HomeWebController {

    @GetMapping("/mealcraft/home")
    public String showHome(Model model) {
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


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        model.addAttribute("month", monthNames[currentMonth.getMonthValue()-1]);

        model.addAttribute("weeks", weeks);
        model.addAttribute("title", "MealCraft — Головна");

        //TODO: change to actual shoppingItems from db
        ShoppingItemDto[] shoppingItems = {new ShoppingItemDto(1L,1L,"egg",1L,1L,3,true),
                new ShoppingItemDto(2L,1L,"milk",2L,1L,3,true),
                new ShoppingItemDto(3L,1L,"bread",3L,1L,3,false)};


        model.addAttribute("shoppingItems", shoppingItems);

        return "home";
    }

    @PostMapping("/mealcraft/shopping/toggle")
    public String toggleChecked(@RequestParam Long id) {
        /*shoppingItemRepository.findById(id).ifPresent(item -> {
            item.setChecked(!item.isChecked());
            shoppingItemRepository.save(item);
        });*/
        return "redirect:/mealcraft/home";
    }
}
