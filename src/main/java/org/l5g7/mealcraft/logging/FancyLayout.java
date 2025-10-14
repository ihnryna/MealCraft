package org.l5g7.mealcraft.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;

public class FancyLayout extends LayoutBase<ILoggingEvent> {

    @Override
    public String doLayout(ILoggingEvent iLoggingEvent) {
        return (iLoggingEvent.getTimeStamp() - iLoggingEvent.getLoggerContextVO().getBirthTime()) +
                " " +
                iLoggingEvent.getLevel() +
                " [" +
                iLoggingEvent.getThreadName() +
                "] " +
                iLoggingEvent.getLoggerName() +
                " - " +
                iLoggingEvent.getFormattedMessage() +
                System.lineSeparator();
    }
}
