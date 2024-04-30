package client.Commands;

import client.UDPClient;
import common.Commands.UserCommand;
import common.net.dataTransfer.PackedCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_first command for client
 * <p>This command is used to remove first element from collection
 * @see UserCommand
 */
public class RemoveFirstCommand extends UserCommand {
    /**
     * RemoveFirstCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public RemoveFirstCommand() {
        super("remove_first", "remove first element from collection");
    }

    /**
     * Method to complete remove_first command
     * <p>It removes first element from collection
     * <p>If collection is empty user is informed
     */
    @Override
    public ServerResponse execute() {
        try {
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(ClientRequest.getUser().userName());
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
            return (ServerResponse) UDPClient.getInstance().receiveObject();
        }
        catch (Exception e) {
            return new ServerResponse(ResultState.EXCEPTION, e);
        }
    }
}
