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
     * ShowCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ShowCommand() {
        super("show", "print all elements of collection");
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
        if(CollectionController.getInstance().isEmpty()){
            return new ServerResponse(ResultState.SUCCESS,
                    "Collection is empty");
        }
        return new ServerResponse(ResultState.SUCCESS, CollectionController.getInstance().getStringCollection());
    }
}
