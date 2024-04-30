package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import client.Constants;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import client.Parsers.WorkerParsers;
import common.Commands.UserCommand;
import common.Validators.WorkerValidators;
import common.net.dataTransfer.PackedCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class with realization of update command for client
 * <p>This command is used to update value of collection element which id is equal to given
 * @see UserCommand
 */
public class UpdateByIdCommand extends UserCommand {
    /**
     * Worker reader which is used to read new element from user
     */
    private WorkerReader workerReader;
    /**
     * id of element to update
     */
    private long id;

    /**
     * UpdateByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public UpdateByIdCommand(WorkerReader workerReader) {
        super("update",
                "update value of collection element which id is equal to given",
                "id", "{element}");
        this.workerReader = workerReader;
    }

    /**
     * Method to complete update command
     * <p>It reads new element from user and then updates value of element with given id inside collection
     *
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ServerResponse execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND,
                    new PackedCommand("check_id",
                            new ArrayList<>(List.of(id, ClientRequest.getUser().userName())))));

            ServerResponse response = (ServerResponse) UDPClient.getInstance().receiveObject();

            if (response.state() == ResultState.EXCEPTION) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readWorker();
                }
                return response;
            }
            Worker worker = workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(id);
            arguments.add(worker);
            arguments.add(ClientRequest.getUser().userName());

            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
            return (ServerResponse) UDPClient.getInstance().receiveObject();
        } catch (Exception e){
            return new ServerResponse(ResultState.EXCEPTION, e);
        }
    }

    /**
     * Method checks if amount arguments is correct and validates id
     *
     * @param arguments String array with different arguments
     * @throws WrongAmountOfArgumentsException If number of arguments is not equal to one
     * @throws InvalidDataException            If given id is not valid
     */
    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws WrongAmountOfArgumentsException, InvalidDataException {
        super.initCommandArgs(arguments);
        this.id = WorkerParsers.longParser.parse((String) arguments.get(0));
        WorkerValidators.idValidator.validate(id);
    }
}
