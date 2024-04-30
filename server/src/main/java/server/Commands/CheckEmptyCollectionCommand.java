package server.Commands;

import common.Commands.UserCommand;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

public class CheckEmptyCollectionCommand extends UserCommand {

    public CheckEmptyCollectionCommand(){
        super("is_collection_empty", "command to check if collection is empty");
    }

    @Override
    public ServerResponse execute() {
        return new ServerResponse(ResultState.SUCCESS, CollectionController.getInstance().getCollection().isEmpty());
    }
}
