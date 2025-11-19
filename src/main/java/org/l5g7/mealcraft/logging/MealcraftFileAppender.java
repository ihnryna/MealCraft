package org.l5g7.mealcraft.logging;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
public class MealcraftFileAppender extends AppenderBase<ILoggingEvent> {

    private String file;
    private MealcraftLayout layout;
    private FileWriter writer;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (!isStarted()) return;

        String logMessage = layout.doLayout(iLoggingEvent);
        try (FileWriter writerToFile = new FileWriter(file, true)) {
            writerToFile.write(logMessage);
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
            addError("Failed to initialize writer for logs in file: " + file, e);
            return;
        }
        super.start();
    }
}



