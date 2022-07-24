package com.medis;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class GeneralLogger {
    static Logger LOGGER;
    public FileHandler file;
    public GeneralLogger() throws IOException{
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
        LOGGER =  Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        LOGGER.setUseParentHandlers(false);
        file = new FileHandler("./general_logger.log",true);
        SimpleFormatter formatter = new SimpleFormatter();

        file.setFormatter(formatter);
        LOGGER.addHandler(file);

    }

    private static Logger getLogger(){
        if(LOGGER == null){
            try {
                new GeneralLogger();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return LOGGER;
    }

    public static void log(Level level, String msg){
        getLogger().log(level, msg);
    }

}

