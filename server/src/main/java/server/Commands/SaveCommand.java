package server.Commands;

import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;
import server.Controllers.CollectionController;
import server.Controllers.DataFileController;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of save command
 * <p>This command is used to save collection to data file
 * @see UserCommand
 */
public class SaveCommand extends UserCommand {
    /**
     * Controller of collection which is used to get collection
     */
    private CollectionController collectionController;
    /**
     * Controller of data file which is used to write data
     */
    private DataFileController dataFileController;

    /**
     * SaveCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param collectionController
     * @param dataFileController
     */
    public SaveCommand(CollectionController collectionController, DataFileController dataFileController) {
        super("save", "save collection to data file");
        this.collectionController = collectionController;
        this.dataFileController = dataFileController;
    }

    /**
     * Method to complete save command
     * <p>It gets current collection from collection controller and writes it to data file
     * <p>Also ChangeFlag is set to false
     * @return
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            this.dataFileController.writeToJSON(this.collectionController.getCollection());
            this.collectionController.removeChangeFlag();
            return new ExecuteCommandResponse(ResultState.SUCCESS,
                    "Collection saved successfully!");
        } catch (IOException e) {
            return new ExecuteCommandResponse(ResultState.EXCEPTION,
                    new IOException("An error occurred while writing to the file!"));
        }
    }
}
