package org.l5g7.mealcraft.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

public class ExceptionFileAppender extends FileAppender<ILoggingEvent> {

    public ExceptionFileAppender() {
        setFile("logs/exceptions.log");
        setAppend(false);

        MealcraftLayout layout = new MealcraftLayout();
        layout.setContext(getContext());
        layout.start();

        setLayout(layout);
    }
}
