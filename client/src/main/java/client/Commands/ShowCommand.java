package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of show command for client
 * <p>This command is used to print all elements of collection
 * @see UserCommand
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
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), new ArrayList<>())));
            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        } catch (Exception e) {
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
        }
    }
}
