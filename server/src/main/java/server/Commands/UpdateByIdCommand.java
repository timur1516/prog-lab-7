package server.Commands;

import common.Collection.Worker;
import common.Commands.ICommand;
import common.Commands.UserCommand;
import common.Exceptions.ServerErrorException;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Main;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Class with realization of update command
 * <p>This command is used to update value of collection element which id is equal to given
 * @see UserCommand
 * @see ICommand
 */
public class UpdateByIdCommand extends UserCommand {
    /**
     * Controller of collection which is used to update element
     */
    private CollectionController collectionController;
    /**
     * id of element to update
     */
    private long id;
    Worker worker;
    String username;

    /**
     * UpdateByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public UpdateByIdCommand(CollectionController collectionController) {
        super("update",
                "update value of collection element which id is equal to given",
                "id", "element", "username");
        this.collectionController = collectionController;
    }

    /**
     * Method to complete update command
     * <p>It reads new element from user and then updates value of element with given id inside collection
     *
     * @return
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ServerResponse execute() {
        if(!this.collectionController.containsId(id)){
            return new ServerResponse(ResultState.EXCEPTION,
                    new NoSuchElementException("No element with such id!"));
        }
        try {
            this.collectionController.update(id, worker, username);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new ServerResponse(ResultState.SUCCESS,
                "Element updated successfully!");
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) {
        this.id = (long) arguments.get(0);
        this.worker = (Worker) arguments.get(1);
        this.username = (String) arguments.get(2);
    }
}
