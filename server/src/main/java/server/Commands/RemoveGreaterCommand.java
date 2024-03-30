package server.Commands;

import common.Collection.Worker;
import common.Exceptions.InvalidDataException;
import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_greater command
 * <p>This command is used to remove all elements which are greater than given
 * @see UserCommand
 * @see ICommand
 */
public class RemoveGreaterCommand extends UserCommand {
    /**
     * Controller of collection which is used to remove elements
     */
    private CollectionController collectionController;

    private Worker worker;
    /**
     * RemoveGreaterCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public RemoveGreaterCommand(CollectionController collectionController) {
        super("remove_greater", "remove all elements which are greater than given", "{element}");
        this.collectionController = collectionController;
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
    public ExecuteCommandResponse execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ExecuteCommandResponse(ResultState.SUCCESS, "Collection is empty!");
        }
        int elementsRemoved = this.collectionController.removeGreater(worker);
        return new ExecuteCommandResponse(ResultState.SUCCESS,
                String.format("Successfully removed %d elements!", elementsRemoved));
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) {
        this.worker = (Worker) arguments.get(0);
    }
}
