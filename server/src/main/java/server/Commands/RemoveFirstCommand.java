package server.Commands;

import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.InvalidDataException;
import common.Exceptions.ServerErrorException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Main;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Class with realization of remove_first command
 * <p>This command is used to remove first element from collection
 * @see UserCommand
 * @see ICommand
 */
public class RemoveFirstCommand extends UserCommand {
    /**
     * Controller of collection which is used to remove element
     */
    private CollectionController collectionController;

    private String username;

    /**
     * RemoveFirstCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public RemoveFirstCommand(CollectionController collectionController) {
        super("remove_first", "remove first element from collection", "username");
        this.collectionController = collectionController;
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException, WrongAmountOfArgumentsException {
        super.initCommandArgs(arguments);
        this.username = (String) arguments.get(0);
    }

    /**
     * Method to complete remove_first command
     * <p>It removes first element from collection
     * <p>If collection is empty user is informed
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        if(this.collectionController.getCollection().isEmpty()){
            return new ServerResponse(ResultState.SUCCESS, "Collection is empty!");
        }
        try {
            this.collectionController.removeFirst(username);
        } catch (SQLException e) {
            Main.logger.error("Database error occurred!", e);
            return new ServerResponse(ResultState.EXCEPTION, new ServerErrorException());
        }
        return new ServerResponse(ResultState.SUCCESS,
                "Element removed successfully!");
    }
}
