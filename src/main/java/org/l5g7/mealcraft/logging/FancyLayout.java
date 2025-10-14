package org.l5g7.mealcraft.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import org.slf4j.Marker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class FancyLayout extends LayoutBase<ILoggingEvent> {


    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    .withZone(ZoneId.systemDefault());
    @Override
    public String doLayout(ILoggingEvent iLoggingEvent) {

        String timestamp = TIMESTAMP_FORMATTER.format(Instant.ofEpochMilli(iLoggingEvent.getTimeStamp()));
        String thread = iLoggingEvent.getThreadName();
        String loggerName = iLoggingEvent.getLoggerName();
        String level = iLoggingEvent.getLevel().toString();

        String user = iLoggingEvent.getMDCPropertyMap().getOrDefault("user", " ");

        String markers = " ";
        String emoji = " ";
        if (iLoggingEvent.getMarkerList() != null && !iLoggingEvent.getMarkerList().isEmpty()) {
            markers = iLoggingEvent.getMarkerList().stream()
                    .map(Marker::getName)
                    .collect(Collectors.joining(","));

            for (LogMarker logMarker : LogMarker.values()) {
                if (markers.contains(logMarker.getMarkerName())) {
                    emoji = logMarker.getEmoji();
                    break;
                }
            }
        }

        String message = iLoggingEvent.getFormattedMessage();

        return String.format("%s %-5s %-10s %-10s %-3s %-15s %s %-40s%n",
                timestamp,
                level,
                user,
                markers,
                emoji,
                thread,
                message,
                loggerName
        );
    }
}
