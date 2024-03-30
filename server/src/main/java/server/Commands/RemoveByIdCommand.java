package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.InvalidDataException;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

import java.io.Serializable;
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
     * Controller of collection which is used to remove element by its id
     */
    private CollectionController collectionController;

    /**
     * id of element to remove
     */
    private long id;

    /**
     * RemoveByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public RemoveByIdCommand(CollectionController collectionController) {
        super("remove_by_id", "remove element with given id from collection", "id");
        this.collectionController = collectionController;
        this.id = id;
    }

    /**
     * Method to complete remove_by_id command
     * <p>It removes element by its id
     *
     * @return
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ExecuteCommandResponse execute() {
        if(!collectionController.containsId(id)){
            return new ExecuteCommandResponse(ResultState.EXCEPTION,
                    new NoSuchElementException("No element with such id!"));
        }
        this.collectionController.removeById(id);
        return new ExecuteCommandResponse(ResultState.SUCCESS, "Element removed successfully!");
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException {
        super.initCommandArgs(arguments);
        this.id = (long) arguments.get(0);
    }
}
