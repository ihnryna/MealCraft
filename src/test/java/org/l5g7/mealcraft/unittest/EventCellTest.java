package org.l5g7.mealcraft.unittest;

import org.junit.jupiter.api.Test;
import org.l5g7.mealcraft.app.mealplan.EventCell;
import static org.junit.jupiter.api.Assertions.*;

class EventCellTest {

    @Test
    void constructorAndGetters_shouldInitializeFieldsCorrectly() {
        EventCell cell = new EventCell(true, false, "Test Event", 2, "#FFB347");

        assertTrue(cell.isStart());
        assertFalse(cell.isEnd());
        assertEquals("Test Event", cell.getName());
        assertEquals(2, cell.getSlot());
        assertEquals("#FFB347", cell.getColor());
    }

    @Test
    void setters_shouldUpdateFieldsCorrectly() {
        EventCell cell = new EventCell(false, false, "Old Event", 0, "#C2FF47");

        cell.setStart(true);
        cell.setEnd(true);
        cell.setName("New Event");
        cell.setSlot(3);
        cell.setColor("#4766FF");

        assertTrue(cell.isStart());
        assertTrue(cell.isEnd());
        assertEquals("New Event", cell.getName());
        assertEquals(3, cell.getSlot());
        assertEquals("#4766FF", cell.getColor());
    }
}

