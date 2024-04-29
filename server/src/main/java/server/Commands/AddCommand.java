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

    private String username;

    /**
     * AddCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     */
    public AddCommand(CollectionController collectionController) {
        super("add", "add new element to collection", "element", "username");
        this.collectionController = collectionController;
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) {
        this.worker = (Worker) arguments.get(0);
        this.username = (String) arguments.get(1);
    }

    /**
     * Method to complete add command
     * <p>It reads new element and then adds it to collection
     *
     * @return
     */
    @Override
    public ServerResponse execute() {
        try {
            collectionController.add(worker, username);
        } catch (SQLException e) {
            Main.logger.error("Database error occurred!", e);
            return new ServerResponse(ResultState.EXCEPTION, new ServerErrorException());
        }
        return new ServerResponse(ResultState.SUCCESS, "Worker added successfully!");
    }
}
