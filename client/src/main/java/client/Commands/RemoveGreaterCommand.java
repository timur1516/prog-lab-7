package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Collection.Worker;
import common.Constants;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of remove_greater command for client
 * <p>This command is used to remove all elements which are greater than given
 * @see UserCommand
 */
public class RemoveGreaterCommand extends UserCommand {
    /**
     * Worker reader which is used to read element from user
     */
    private WorkerReader workerReader;

    /**
     * RemoveGreaterCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public RemoveGreaterCommand(WorkerReader workerReader) {
        super("remove_greater", "remove all elements which are greater than given", "{element}");
        this.workerReader = workerReader;
    }

    /**
     * Method to complete remove_greater command
     * <p>It reads element to compare with and then removes elements which are greater that it
     * <p>In the end it prints number of deleted elements
     * <p>If collection is empty element is not read (except script mode)
     *
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.IS_COLLECTION_EMPTY, null));
            if ((boolean)(UDPClient.getInstance().receiveObject())) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readWorker();
                }
                return new ExecuteCommandResponse(ResultState.SUCCESS, "Collection is empty!");
            }

            Worker worker = this.workerReader.readWorker();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(worker);

            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));

            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        } catch (Exception e){
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
        }
    }
}
