package client.Commands;

import client.Readers.WorkerReader;
import client.UDPClient;
import common.Constants;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Class with realization of filter_less_than_end_date command for client
 * <p>This command is used to print all elements whose endDate is less than given
 * @see UserCommand
 */
public class FilterLessThanEndDateCommand extends UserCommand {
    /**
     * Worker reader which is used to read endDate
     */
    private WorkerReader workerReader;

    /**
     * FilterLessThanEndDateCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param workerReader
     */
    public FilterLessThanEndDateCommand(WorkerReader workerReader) {
        super("filter_less_than_end_date", "print all elements whose endDate is less than given","endDate");
        this.workerReader = workerReader;
    }

    /**
     * Method to complete filter_less_than_end_date command
     * <p>It reads endDate from user and then print all elements whose endDate is less than given
     * <p>If collection is empty endDate is not read (except script mode)
     *
     */
    @Override
    public ExecuteCommandResponse execute() {
        try {
            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.IS_COLLECTION_EMPTY, null));
            if (((boolean)UDPClient.getInstance().receiveObject())) {
                if (Constants.SCRIPT_MODE) {
                    workerReader.readEndDate();
                }
                return new ExecuteCommandResponse(ResultState.SUCCESS, "Collection is empty!");
            }

            LocalDateTime endDate = workerReader.readEndDate();
            ArrayList<Serializable> arguments = new ArrayList<>();
            arguments.add(endDate);

            UDPClient.getInstance().sendObject(new ClientRequest(ClientRequestType.EXECUTE_COMMAND, new PackedCommand(super.getName(), arguments)));

            return (ExecuteCommandResponse) UDPClient.getInstance().receiveObject();
        } catch (Exception e){
            return new ExecuteCommandResponse(ResultState.EXCEPTION, e);
        }
    }
}
