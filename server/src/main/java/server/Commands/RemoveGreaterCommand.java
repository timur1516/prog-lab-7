package server.Commands;

import common.Collection.Worker;
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
 * Class with realization of remove_greater command
 * <p>This command is used to remove all elements which are greater than given
 * @see UserCommand
 * @see ICommand
 */
public class RemoveGreaterCommand extends UserCommand {
    private Worker worker;
    private String username;

    /**
     * RemoveGreaterCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveGreaterCommand() {
        super("remove_greater", "remove all elements which are greater than given", "element", "username");
    }

    /**
     * Method to complete remove_greater command
     * <p>It reads element to compare with and then removes elements which are greater that it
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
            elementsRemoved = CollectionController.getInstance().removeGreater(worker, username);
        } catch (SQLException e) {
            throw  new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS,
                String.format("Successfully removed %d elements!", elementsRemoved));
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException {
        super.initCommandArgs(arguments);
        this.worker = (Worker) arguments.get(0);
        this.username = (String) arguments.get(1);
    }
}
