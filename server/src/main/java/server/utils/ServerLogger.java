package server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Main;

/**
 * Singleton class with serve logger
 */
public class ServerLogger {
    private static Logger LOGGER = null;

    public static synchronized Logger getInstance(){
        if(LOGGER == null){
            LOGGER = LoggerFactory.getLogger(Main.class);
        }
        return LOGGER;
    }
}
