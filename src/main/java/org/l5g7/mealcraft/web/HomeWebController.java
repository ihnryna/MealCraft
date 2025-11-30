package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpSession;
import org.l5g7.mealcraft.app.mealplan.EventCell;
import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
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
import java.time.ZoneId;
import java.util.*;

@Controller
public class HomeWebController {

    private final RestClient internalApiClient;
    private final UserService userService;

    public HomeWebController(@Qualifier("internalApiClient") RestClient internalApiClient, UserService userService) {
        this.internalApiClient = internalApiClient;
        this.userService = userService;
    }

    @GetMapping("/mealcraft/home")
    public String showHome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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


        String username = auth.getName();
        model.addAttribute("username", username);


        ResponseEntity<List<MealPlanDto>> responseMeals = internalApiClient.get()
                .uri("/meal-plans/user/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<MealPlanDto>>() {
                });

        List<MealPlanDto> events = responseMeals.getBody();
        Map<LocalDate, ArrayList<EventCell>> dayEventMap = new HashMap<>();

        if (events != null) {
            for (MealPlanDto ev : events) {
                Date date = ev.getPlanDate();
                LocalDate start = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate end = start.plusDays(ev.getServings());
                int slot = 0;
                boolean foundSlot = false;
                for (LocalDate i = start; i.isBefore(end) || i.isEqual(end); i = i.plusDays(1)) {
                    if (!foundSlot) {
                        if (dayEventMap.containsKey(i)) {
                            slot = dayEventMap.get(i).size();
                            foundSlot = true;
                            putAtSlot(dayEventMap.get(i), slot, new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                            System.out.println(slot+" "+ev.getName());
                            //dayEventMap.get(i).add(new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                        } else {
                            foundSlot = true;
                            dayEventMap.put(i, new ArrayList<>());
                            putAtSlot(dayEventMap.get(i), slot, new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                            System.out.println(slot+" "+ev.getName());

                        }
                    } else {
                        if (dayEventMap.containsKey(i)) {
                            putAtSlot(dayEventMap.get(i), slot, new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                            System.out.println(slot+" "+ev.getName());

                            //dayEventMap.get(i).add(new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                        } else {
                            dayEventMap.put(i, new ArrayList<>());
                            putAtSlot(dayEventMap.get(i), slot, new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()));
                            System.out.println(slot+" "+ev.getName());


                            /*dayEventMap.put(i, new ArrayList<>(
                                    List.of(new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor())))
                            );*/
                        }
                        //dayEventMap.put(i, new ArrayList<>(List.of(new EventCell(i.isEqual(start), i.isEqual(end), ev.getName(), slot, ev.getColor()))));
                    }
                }
            }
        }

        model.addAttribute("dayEventMap", dayEventMap);


        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        model.addAttribute("month", monthNames[currentMonth.getMonthValue() - 1]);

        model.addAttribute("weeks", weeks);
        model.addAttribute("title", "MealCraft — Головна");

        ResponseEntity<List<ShoppingItemDto>> response = internalApiClient.get()
                .uri("/shopping-items/getUserShoppingItems/{id}", userService.getUserByUsername(username).id())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ShoppingItemDto>>() {
                });

        List<ShoppingItemDto> shoppingItems = response.getBody();

        model.addAttribute("shoppingItems", shoppingItems);

        return "home";
    }

    @PostMapping("/mealcraft/shopping/toggle")
    public String toggleChecked(@RequestParam Long id) {

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

    private void putAtSlot(List<EventCell> list, int slot, EventCell cell) {
        while (list.size() <= slot) {
            list.add(null); // додаємо пусті слоти
        }
        list.set(slot, cell);
    }

}
