package server.Commands;


import common.Collection.Worker;
import common.Exceptions.InvalidDataException;
import common.Exceptions.ServerErrorException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Main;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class with realization of remove_lower command
 * <p>This command is used to remove all elements which are lower than given
 * @see UserCommand
 * @see ICommand
 */
public class RemoveLowerCommand extends UserCommand {

    private Worker worker;
    private String username;

    /**
     * RemoveLowerCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveLowerCommand() {
        super("remove_lower", "remove all elements which are lower than given", "element", "username");
    }

    /**
     * Method to complete remove_lower command
     * <p>It reads element to compare with and then removes elements which are lower that it
     * <p>In the end it prints number of deleted elements
     * <p>If collection is empty element is not read (except script mode)
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        if(CollectionController.getInstance().isEmpty()){
            return new ServerResponse(ResultState.SUCCESS, "Collection is empty!");
        }
        int elementsRemoved = 0;
        try {
            elementsRemoved = CollectionController.getInstance().removeLower(worker, username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS,
                String.format("Successfully removed %d elements!", elementsRemoved));
    }

    /**
     * Method checks if amount arguments is correct
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to zero
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException {
        super.initCommandArgs(arguments);
        this.worker = (Worker) arguments.get(0);
        this.username = (String) arguments.get(1);
    }
}
