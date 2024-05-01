package server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Main;

public class ServerLogger {
    private static Logger LOGGER = null;

    public static synchronized Logger getInstace(){
        if(LOGGER == null){
            LOGGER = LoggerFactory.getLogger(Main.class);
        }
        return LOGGER;
    }
}
