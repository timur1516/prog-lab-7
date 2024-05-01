package server.Commands;

import common.Commands.UserCommand;
import common.Exceptions.AccessDeniedException;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.net.dataTransfer.UserInfo;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Command which check if collection contains given id
 * <p>It also checks if user has rights to modify element with this id
 */
public class CheckIdCommand extends UserCommand {
    private long id;
    private String username;

    /**
     * CheckIdCommand constructor
     * <> Firstly it initializes super constructor by command name, arguments and description
     */
    public CheckIdCommand(){
        super("check_id", "command to check if collection contains given id and user has rights to operate with it", "id", "user");
    }

    /**
     * Method to complete CheckEmptyCollectionCommand
     * <>Firstly it checks if collection contains given id
     * <>Then user rights to modify this element are checked
     * @return SeverResponse
     */
    @Override
    public ServerResponse execute() {
        if(!CollectionController.getInstance().containsId(id)){
            return new ServerResponse(ResultState.EXCEPTION, new NoSuchElementException("No element with such id!"));
        }
        try {
            if(!CollectionController.getInstance().checkAccess(id, username)){
                return new ServerResponse(ResultState.EXCEPTION, new AccessDeniedException("You can't modify this element!"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS, CollectionController.getInstance().containsId(id));
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException, WrongAmountOfArgumentsException {
        super.initCommandArgs(arguments);
        this.id = (long) arguments.get(0);
        this.username = (String) arguments.get(1);
    }
}
