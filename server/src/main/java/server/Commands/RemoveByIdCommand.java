package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.AccessDeniedException;
import common.Exceptions.InvalidDataException;
import common.Exceptions.ServerErrorException;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Main;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Class with realization of remove_by_id command
 * <p>This command is used to remove element with given id from collection
 * @see UserCommand
 * @see ICommand
 */
public class RemoveByIdCommand extends UserCommand {
    /**
     * id of element to remove
     */
    private long id;

    private String username;

    /**
     * RemoveByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveByIdCommand() {
        super("remove_by_id", "remove element with given id from collection", "id", "username");
    }

    /**
     * Method to complete remove_by_id command
     * <p>It removes element by its id
     *
     * @return
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ServerResponse execute() {
        if(!CollectionController.getInstance().containsId(id)){
            return new ServerResponse(ResultState.EXCEPTION,
                    new NoSuchElementException("No element with such id!"));
        }
        try {
            if(!CollectionController.getInstance().checkAccess(id, username)){
                return new ServerResponse(ResultState.EXCEPTION, new AccessDeniedException("You can't delete this element!"));
            }
            CollectionController.getInstance().removeById(id, username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS, "Element removed successfully!");
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException {
        super.initCommandArgs(arguments);
        this.id = (long) arguments.get(0);
        this.username = (String) arguments.get(1);
    }
}
