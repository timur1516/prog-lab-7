package server.Commands;

import common.Commands.UserCommand;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

/**
 * Command to check if collection is empty
 */
public class CheckEmptyCollectionCommand extends UserCommand {
    /**
     * CheckEmptyCollectionCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public CheckEmptyCollectionCommand(){
        super("is_collection_empty", "command to check if collection is empty");
    }

    /**
     * Method to complete CheckEmptyCollectionCommand
     * <>It returns result of calling {@link CollectionController#isEmpty()} method
     * @return SeverResponse
     */
    @Override
    public ServerResponse execute() {
        return new ServerResponse(ResultState.SUCCESS, CollectionController.getInstance().isEmpty());
    }
}
