package common.Controllers;

import common.Exceptions.InvalidDataException;
import common.Exceptions.WrongAmountOfArgumentsException;
import common.Commands.UserCommand;
import common.net.requests.PackedCommand;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Class which is used to work with UserCommand objects
 */
public class CommandsController {
    /**
     * List with all available commands
     */
    private ArrayList<UserCommand> commandsList;

    public void setCommandsList(ArrayList<UserCommand> commandsList) {
        this.commandsList = commandsList;
    }

    /**
     * Method to get list of commands
     * @return ArrayList of UserCommand
     */
    public ArrayList<UserCommand> getCommandsList() {
        return commandsList;
    }

    /**
     * Method to find command by its name and init its argument
     * @return UserCommand object
     * @throws WrongAmountOfArgumentsException If number of arguments is wrong for given command
     * @throws NoSuchElementException If command not found
     * @throws InvalidDataException if command argument are not valid
     */
    public UserCommand launchCommand(PackedCommand packedCommand) throws InvalidDataException, WrongAmountOfArgumentsException, NoSuchElementException {
        if(this.commandsList.stream().noneMatch(userCommand ->
                userCommand.getName().equals(packedCommand.commandName()))){
            throw new NoSuchElementException("Command '" + packedCommand.commandName() + "' not found!");
        }

        UserCommand command;

        command = this.commandsList
                .stream()
                .filter(userCommand -> userCommand.getName().equals(packedCommand.commandName()))
                .findFirst().get();

        command.initCommandArgs(packedCommand.arguments());

        return command;
    }
}
