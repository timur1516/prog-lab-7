package client.Commands;

import client.UDPClient;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of info command for client
 * <p>This command is used to print information about collection
 * @see UserCommand
 */
public class InfoCommand extends UserCommand {
    /**
     * InfoCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public InfoCommand() {
        super("info", "print information about collection");
    }

    /**
     * Method to complete info command
     * <p>It prints info from collection controller
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), new ArrayList<>())));
            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        }
        catch (Exception e) {
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
        }
    }
}
