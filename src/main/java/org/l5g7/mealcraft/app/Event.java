package org.l5g7.mealcraft.app;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Event {
    private LocalDate start;
    private LocalDate end;
    private String name;
    private String color;

    public Event(LocalDate localDates, LocalDate localDates1, String name, String color) {
        this.name = name;
        this.start = localDates;
        this.end = localDates1;
        this.color = color;
    }
}