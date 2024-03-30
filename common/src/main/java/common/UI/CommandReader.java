package common.UI;

import common.net.requests.PackedCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Singleton class to read commands from console
 */
public class CommandReader {
    private static CommandReader COMMAND_READER = null;
    private CommandReader(){}

    /**
     * Returns COMMAND_READER
     * <p>If COMMAND_READER is null it is initialized
     * @return COMMAND_READER
     */
    public static CommandReader getInstance(){
        if(COMMAND_READER == null){
            COMMAND_READER = new CommandReader();
        }
        return COMMAND_READER;
    }

    /**
     * Method to read command from console
     * <p>It read line from user, parse it, and create {@link PackedCommand} object
     * @return {@link PackedCommand} object with command
     */
    public PackedCommand readCommand(){
        String line = Console.getInstance().readLine();
        String[] input = (line.trim() + " ").split(" ");
        if (input.length == 0) return null;
        String commandName = input[0];
        String[] commandArgs = Arrays.copyOfRange(input, 1, input.length);
        return new PackedCommand(commandName, new ArrayList<>(List.of(commandArgs)));
    }
}
