package org.l5g7.mealcraft.app;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCell {
    private boolean isStart;
    private boolean isEnd;
    private String name;
    private int slot;
    private String color;

    public EventCell(boolean isStart, boolean isEnd, String name, int slot, String color) {
        this.isStart = isStart;
        this.isEnd = isEnd;
        this.name = name;
        this.slot = slot;
        this.color = color;
    }
}
