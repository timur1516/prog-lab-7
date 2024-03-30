package client.Commands;

import client.UDPClient;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.util.ArrayList;

/**
 * Class with realization of clear command for client
 * <p>This command is used to send request for clear command to server
 * @see UserCommand
 */
public class ClearCommand extends UserCommand {
    /**
     * ClearCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ClearCommand() {
        super("clear", "delete all element from collection");
    }

    /**
     * Method to complete clear command
     * <p>It sends request to clear collection
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
