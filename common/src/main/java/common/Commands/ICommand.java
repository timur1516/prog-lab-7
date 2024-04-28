package common.Commands;

import common.net.requests.ServerResponse;

import java.sql.SQLException;

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
    ServerResponse execute();
}
