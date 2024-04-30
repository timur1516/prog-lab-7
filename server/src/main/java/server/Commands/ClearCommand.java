package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.InvalidDataException;
import common.Exceptions.ServerErrorException;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Main;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class with realization of clear command
 * <p>This command is used to clear collection
 * @see UserCommand
 * @see ICommand
 */
public class ClearCommand extends UserCommand {
    private String username;

    /**
     * ClearCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ClearCommand() {
        super("clear", "delete all element from collection", "username");
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException {
        super.initCommandArgs(arguments);
        this.username = (String) arguments.get(0);
    }

    /**
     * Method to complete clear command
     * <p>It clears collection
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        try {
            CollectionController.getInstance().clear(username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS, "Collection cleared successfully!");
    }
}
