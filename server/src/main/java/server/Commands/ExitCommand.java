package server.Commands;

import server.Controllers.DBController;
import server.Exceptions.ExitingException;
import common.UI.YesNoQuestionAsker;
import common.Commands.UserCommand;
import common.net.requests.ServerResponse;
import common.net.requests.ResultState;
import server.Main;

import java.io.IOException;
import java.sql.SQLException;

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
        super("exit", "stop server");
    }

    /**
     * Method to complete exit command
     * <p>Firstly user is asked if he really wants to exit
     * <p>Than it tries to save the collection and if it was not successful exit is canceled
     * <p>Finally server is stopped and app is closed
     */
    @Override
    public ServerResponse execute() {
        YesNoQuestionAsker questionAsker = new YesNoQuestionAsker("Do you want to exit?");
        if(questionAsker.ask()) {
            try {
                DBController.getInstance().close();
                Main.server.stop();
            } catch (IOException | SQLException e) {
                return new ServerResponse(ResultState.EXCEPTION, new ExitingException("Could not stop server!"));
            }
            System.exit(0);
        }
        return new ServerResponse(ResultState.SUCCESS, "Exit canceled");
    }
}
