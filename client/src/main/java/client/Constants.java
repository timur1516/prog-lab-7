package client;

import java.util.Stack;

/**
 * This class is made to store constants which are required in many parts of project
 */
public class Constants {
    /**
     * Flag of script mode
     */
    public static boolean SCRIPT_MODE = false;
    /**
     * Global stack for script file names
     * <p>It is used to track recursion of scripts
     */
    public static Stack<String> scriptStack = new Stack<>();
    /**
     * Default server port which is used if reading from property file was not successful
     */
    public static final int DEFAULT_PORT_NUMBER = 8081;
    /**
     * Timeout for udp client in milliseconds
     */
    public static final int CLIENT_TIMEOUT = 10000;
}
