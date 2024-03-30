package server.Commands;

import common.Collection.Worker;
import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.InvalidDataException;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of add command
 * <p>This command is used to add new element to collection
 * @see UserCommand
 * @see ICommand
 */
public class AddCommand extends UserCommand {
    /**
     * Controller of collection which is used to add new element
     */
    private CollectionController collectionController;
    /**
     * Worker object to add
     */
    private Worker worker;

    /**
     * AddCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public AddCommand(CollectionController collectionController) {
        super("add", "add new element to collection", "{element}");
        this.collectionController = collectionController;
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) {
        this.worker = (Worker) arguments.get(0);
    }

    /**
     * Method to complete add command
     * <p>It reads new element and then adds it to collection
     *
     * @return
     */
    @Override
    public ExecuteCommandResponse execute() {
        collectionController.add(worker);
        return new ExecuteCommandResponse(ResultState.SUCCESS, "Worker added successfully!");
    }
}
