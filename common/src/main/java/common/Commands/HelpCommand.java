package common.Commands;

import common.Controllers.CommandsController;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.ResultState;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class with realization of help command
 * <p>This command is used to print description of available commands
 * @see UserCommand
 */
public class HelpCommand extends UserCommand {
    /**
     * Controller of command which is used to get list of all commands
     */
    private CommandsController commandsController;

    /**
     * HelpCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     * @param commandsController
     */
    public HelpCommand(CommandsController commandsController) {
        super("help", "print description of available commands");
        this.commandsController = commandsController;
    }

    /**
     * Method to complete help command
     * <p>It gets all commands from commandController and then prints their description
     */
    @Override
    public ExecuteCommandResponse execute() {
        String result = "";
        for(UserCommand command : this.commandsController.getCommandsList()){
            result += command.toString() + "\n";
        }
        return new ExecuteCommandResponse(ResultState.SUCCESS, result);
    }
}
