package client.Commands;

import client.Constants;
import common.UI.YesNoQuestionAsker;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;

/**
 * Class with realization of exit command
 * <p>This command is used to finish program
 * @see UserCommand
 */
public class ExitCommand extends UserCommand {
    /**
     * ExitCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExitCommand() {
        super("exit", "stop program without saving collection");
    }

    /**
     * Method to complete exit command
     * <p>Firstly user is asked if he really wants to exit
     */
    @Override
    public ServerResponse execute() {
        if (Constants.SCRIPT_MODE) {
            System.exit(0);
        }
        YesNoQuestionAsker questionAsker = new YesNoQuestionAsker("Do you want to exit?");
        if(questionAsker.ask()) {
            System.exit(0);
        }
        return new ServerResponse(ResultState.SUCCESS, "Exit canceled");
    }
}
