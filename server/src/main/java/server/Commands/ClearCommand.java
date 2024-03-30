package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of clear command
 * <p>This command is used to clear collection
 * @see UserCommand
 * @see ICommand
 */
public class ClearCommand extends UserCommand {
    /**
     * Controller of collection which is used to clear it
     */
    private CollectionController collectionController;

    /**
     * ClearCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public ClearCommand(CollectionController collectionController) {
        super("clear", "delete all element from collection");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete clear command
     * <p>It clears collection
     *
     * @return
     */
    @Override
    public ExecuteCommandResponse execute() {
        this.collectionController.clear();
        return new ExecuteCommandResponse(ResultState.SUCCESS, "Collection cleared successfully!");
    }
}
