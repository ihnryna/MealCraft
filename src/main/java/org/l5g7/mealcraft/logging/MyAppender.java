package org.l5g7.mealcraft.logging;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
public class MyAppender extends AppenderBase<ILoggingEvent> {

    private String file;
    private FancyLayout layout;
    private FileWriter writer;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (!isStarted()) return;

        String logMessage = layout.doLayout(iLoggingEvent);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(logMessage);
        } catch (IOException e) {
            addError(file, e);
        }
    }

    @Override
    public void start() {
        if (this.layout == null) {
            addError("No Layout!");
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        file = "logs/mealcraft-log-" + timestamp + ".log";
        new java.io.File("logs").mkdirs();
        try {
            writer = new FileWriter(file, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.start();
    }
}



