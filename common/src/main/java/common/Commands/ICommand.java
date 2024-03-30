package common.Commands;

import common.net.requests.ExecuteCommandResponse;

/**
 * Interface of all commands
 */
public interface ICommand {
    /**
     * Method to get command name
     * @return String command name
     */
    String getName();

    /**
     * Method to execute command
     *
     * @return
     */
    ExecuteCommandResponse execute();
}
