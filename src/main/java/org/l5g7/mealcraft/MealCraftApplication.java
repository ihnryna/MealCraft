package org.l5g7.mealcraft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MealCraftApplication {

    private static final Logger log = LoggerFactory.getLogger(MealCraftApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MealCraftApplication.class, args);
        log.info("MEALCRAFT successfully started!");
        //log.warn("Something looks off...");
        //log.error("Oops! Critical error!");
    }

}
