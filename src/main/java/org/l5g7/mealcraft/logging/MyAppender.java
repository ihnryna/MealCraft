package org.l5g7.mealcraft.logging;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Setter
public class MyAppender extends AppenderBase<ILoggingEvent> {

    private String file;
    private FancyLayout layout;

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        if (!isStarted()) return;

        String logMessage = layout.doLayout(iLoggingEvent);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(LocalDateTime.now() + " " + logMessage);
        } catch (IOException e) {
            addError("Помилка запису логу у файл " + file, e);
        }
    }

    @Override
    public void start() {
        new File("logs").mkdirs();
        if (this.layout == null) {
            addError("Layout не встановлено!");
            return;
        }
        if (this.file == null) {
            addError("File не встановлено!");
            return;
        }
        super.start();
    }
}



