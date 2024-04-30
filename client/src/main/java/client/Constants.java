package client;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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
     * Server info
     */
    public static final int serverPort = 8081;
}
