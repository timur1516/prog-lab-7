package client;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import client.Commands.*;
import common.Commands.HelpCommand;
import common.Constants;
import common.Controllers.CommandsController;
import client.Readers.WorkerReader;
import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;
import common.UI.CommandReader;
import common.UI.Console;
import common.Commands.UserCommand;
import common.UserInfo;
import common.net.requests.ClientRequest;
import common.net.requests.ServerResponse;
import common.net.requests.PackedCommand;

/**
 * Main app class
 * <p>Completes initialization of all controllers, sets default input stream for Console
 * <p>In the beginning loads data file (if it is wrong program stops), then calls interactiveMode method
 */
public class Main {
    private static final int TIMEOUT = 1000;
    private static WorkerReader workerReader;

    public static UserInfo user;
    /**
     * Controller of commands
     */
    private static CommandsController commandsController;
    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers and start handling user commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        Console.getInstance().setScanner(new Scanner(System.in));
        workerReader = new WorkerReader();

        try {
            UDPClient.getInstance().init(InetAddress.getLocalHost(), Constants.serverPort, TIMEOUT);
            UDPClient.getInstance().open();
        } catch (UnknownHostException e) {
            Console.getInstance().printError("Server host was not found!");
            System.exit(0);
        } catch (SocketException e) {
            Console.getInstance().printError("Error while starting client!");
            System.exit(0);
        }

        try {
            user = AuthorizationController.authorize();
        } catch (SendingDataException | ReceivingDataException e) {
            Console.getInstance().printError(e.getMessage());
            System.exit(0);
        }
        
        Console.getInstance().printLn(String.format("Hello %s!", user.userName()));

        ClientRequest.setUser(user);

        commandsController = new CommandsController();
        commandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new HelpCommand(commandsController),
                        new InfoCommand(),
                        new ShowCommand(),
                        new AddCommand(workerReader),
                        new UpdateByIdCommand(workerReader),
                        new RemoveByIdCommand(),
                        new ClearCommand(),
                        new ExecuteScriptCommand(),
                        new ExitCommand(),
                        new RemoveFirstCommand(),
                        new RemoveGreaterCommand(workerReader),
                        new RemoveLowerCommand(workerReader),
                        new MinBySalaryCommand(),
                        new FilterLessThanEndDateCommand(workerReader),
                        new PrintFieldDescendingSalaryCommand()
                ))
        );
        interactiveMode();
    }

    /**
     * method which is used to work with script file
     * @throws Exception if any error occurred in process of executing
     */
    public static void scriptMode() throws Exception {
        while(Console.getInstance().hasNextLine()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            Console.getInstance().printLn(packedCommand.commandName());

            UserCommand command = commandsController.launchCommand(packedCommand);
            ServerResponse response = command.execute();
            switch (response.state()){
                case SUCCESS:
                    Console.getInstance().printLn(response.data());
                    break;
                case EXCEPTION:
                    throw (Exception) response.data();
            }
        }
    }

    /**
     * Method to handle user input
     *
     * <p>Reads commands from user, gets their name and arguments, launch command and execute it
     * <p>If any error is occurred method prints error message and continues to read data
     */
    public static void interactiveMode(){
        while(Console.getInstance().hasNextLine()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            if(packedCommand == null) continue;
            UserCommand command;
            try {
                command = commandsController.launchCommand(packedCommand);
            }
            catch (Exception e){
                Console.getInstance().printError(e.getMessage());
                continue;
            }
            ServerResponse response = command.execute();
            switch (response.state()){
                case SUCCESS:
                    Console.getInstance().printLn(response.data());
                    break;
                case EXCEPTION:
                    Console.getInstance().printError(((Exception) response.data()).getMessage());
            }
        }
    }
}