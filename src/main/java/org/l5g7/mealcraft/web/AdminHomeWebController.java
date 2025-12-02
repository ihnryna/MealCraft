package org.l5g7.mealcraft.web;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;

@Controller
@RequestMapping("/mealcraft/admin")
public class AdminHomeWebController {

    private final RestClient internalApiClient;

    public AdminHomeWebController(@Qualifier("internalApiClient") RestClient internalApiClient) {
        this.internalApiClient = internalApiClient;
    }

    @GetMapping("/home")
    public String showHome() {
        return "redirect:/mealcraft/admin/user";
    }
}
