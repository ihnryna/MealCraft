package org.l5g7.mealcraft.web;

import jakarta.servlet.http.HttpSession;
import org.l5g7.mealcraft.app.mealplan.EventCell;
import org.l5g7.mealcraft.app.mealplan.MealPlanDto;
import org.l5g7.mealcraft.app.shoppingitem.ShoppingItemDto;
import org.l5g7.mealcraft.app.user.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    private static final String USERNAME = "username";
    private static final String TITLE = "title";


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

    public void addShoppingItemsToModel(Model model, String username) {
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
                LocalDate end = start.plusDays(ev.getServings());
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
