package server.Commands;

import common.Commands.UserCommand;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

public class CheckEmptyCollectionCommand extends UserCommand {
    /**
     * Controller of collection which is used to clear it
     */
    private CollectionController collectionController;

    public CheckEmptyCollectionCommand(CollectionController collectionController){
        super("is_collection_empty", "command to check if collection is empty");
        this.collectionController = collectionController;
    }

    @Override
    public ServerResponse execute() {
        return new ServerResponse(ResultState.SUCCESS, collectionController.getCollection().isEmpty());
    }
}
