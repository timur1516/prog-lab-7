package server.Commands;

import server.Exceptions.ExitingException;
import common.UI.YesNoQuestionAsker;
import common.Commands.UserCommand;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.PackedCommand;
import common.net.requests.ResultState;
import common.Controllers.CommandsController;
import server.Main;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class with realization of exit command
 * <p>This command is used to finish program
 * @see UserCommand
 */
public class ExitCommand extends UserCommand {
    private CommandsController commandsController;
    /**
     * ExitCommand constructor
     * <p> Firstly it initializes super constructor by command name, arguments and description
     */
    public ExitCommand(CommandsController commandsController) {
        super("exit", "stop program without saving collection");
        this.commandsController = commandsController;
    }

    /**
     * Method to complete exit command
     * <p>Firstly user is asked if he really wants to exit
     * <p>Than it tries to save the collection and if it was not successful exit is canceled
     * <p>Finally server is stopped and app is closed
     */
    @Override
    public ExecuteCommandResponse execute() {
        YesNoQuestionAsker questionAsker = new YesNoQuestionAsker("Do you want to exit?");
        if(questionAsker.ask()) {
            try {
                UserCommand saveCommad = this.commandsController
                        .launchCommand(new PackedCommand("save", new ArrayList<>()));
                ExecuteCommandResponse responce = saveCommad.execute();
                if(responce.state() == ResultState.EXCEPTION) throw (Exception) responce.data();
            } catch (Exception e) {
                String message = "Collection wasn't saved!\n" +
                        e.getMessage() + "\n Exit canceled!";
                return new ExecuteCommandResponse(ResultState.EXCEPTION, new ExitingException(message));
            }
            try {
                Main.server.stop();
            } catch (IOException e) {
                return new ExecuteCommandResponse(ResultState.EXCEPTION, new ExitingException("Could not stop server!"));
            }
            System.exit(0);
        }
        return new ExecuteCommandResponse(ResultState.SUCCESS, "Exit canceled");
    }
}
