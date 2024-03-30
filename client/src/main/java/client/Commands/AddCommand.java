package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of add command on client app
 * <p>This command is used to read data from user and send request to server
 * @see UserCommand
 */
public class AddCommand extends UserCommand {
    /**
     * Worker reader which is used to read new element from user
     */
    private WorkerReader workerReader;

    /**
     * AddCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public AddCommand(WorkerReader workerReader) {
        super("add", "add new element to collection", "{element}");
        this.workerReader = workerReader;
    }

    /**
     * Method to complete add command
     * <p>It reads new element and then send request for adding to server
     * @return
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            Worker worker = this.workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(worker);
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));
            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        }
        catch (Exception e) {
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
        }
    }
}
