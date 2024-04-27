package server.Commands;

import common.Collection.Worker;
import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;

/**
 * Class with realization of show command
 * <p>This command is used to print all elements of collection
 * @see UserCommand
 * @see ICommand
 */
public class ShowCommand extends UserCommand {
    /**
     * Controller of collection which is used to get collection
     */
    private CollectionController collectionController;

    /**
     * ShowCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public ShowCommand(CollectionController collectionController) {
        super("show", "print all elements of collection");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete show command
     * <p>It gets collection from collection controller and then prints it
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ServerResponse(ResultState.SUCCESS,
                    "Collection is empty");
        }
        StringBuilder result = new StringBuilder();
        for(Worker worker : this.collectionController.getCollection().stream().sorted().toList()) {
            result.append(worker.toString()).append("\n");
        }
        return new ServerResponse(ResultState.SUCCESS, result.toString());
    }
}
