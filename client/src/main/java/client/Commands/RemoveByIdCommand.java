package client.Commands;

import client.UDPClient;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import client.Parsers.WorkerParsers;
import common.Commands.UserCommand;
import common.Validators.WorkerValidators;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Class with realization of remove_by_id command for client
 * <p>This command is used to remove element with given id from collection
 * @see UserCommand
 */
public class RemoveByIdCommand extends UserCommand {
    /**
     * id of element to remove
     */
    private long id;

    /**
     * RemoveByIdCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveByIdCommand() {
        super("remove_by_id", "remove element with given id from collection", "id");
    }

    /**
     * Method to complete remove_by_id command
     * <p>It removes element by its id
     *
     * @throws NoSuchElementException is element with given id was not found
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(id);
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND,
                    new PackedCommand(super.getName(), arguments)));
            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        }
        catch (Exception e) {
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
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
