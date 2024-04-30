package server.Commands;

import common.Commands.UserCommand;
import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Command which check if collection contains given id
 */
public class CheckIdCommand extends UserCommand {
    long id;

    public CheckIdCommand(){
        super("check_id", "command to check if collection contains given id");
    }

    @Override
    public ServerResponse execute() {
        return new ServerResponse(ResultState.SUCCESS, CollectionController.getInstance().containsId(id));
    }

    @Override
    public void initCommandArgs(ArrayList<Serializable> arguments) throws InvalidDataException, WrongAmountOfArgumentsException {
        super.initCommandArgs(arguments);
        this.id = (long) arguments.get(0);
    }
}
