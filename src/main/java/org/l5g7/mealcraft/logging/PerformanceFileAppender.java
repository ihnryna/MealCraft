package org.l5g7.mealcraft.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class PerformanceFileAppender extends FileAppender<ILoggingEvent> {

    public PerformanceFileAppender() {
        setFile("logs/performance.log");
        setAppend(false);

        MealcraftLayout layout = new MealcraftLayout();
        layout.setContext(getContext());
        layout.start();

        setLayout(layout);
    }
}
