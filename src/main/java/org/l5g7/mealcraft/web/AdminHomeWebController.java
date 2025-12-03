package org.l5g7.mealcraft.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminHomeWebController {

    @GetMapping("/home")
    public String showHome() {
        return "redirect:/mealcraft/admin/user";
    }
}
