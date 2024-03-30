package server;

import common.Commands.HelpCommand;
import common.Constants;
import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;
import common.FileLoader;
import common.UI.CommandReader;
import common.net.requests.ClientRequest;
import common.net.requests.ExecuteCommandResponse;
import common.net.requests.PackedCommand;
import common.net.requests.ResultState;
import common.Collection.*;
import common.Commands.UserCommand;
import common.UI.Console;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Commands.*;
import server.Controllers.CollectionController;
import common.Controllers.CommandsController;
import server.Controllers.DataFileController;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * Main class for server app
 */
public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    /**
     * Server object
     */
    public static UDPServer server;
    /**
     * Selector object for handling many clients
     */
    private static Selector selector;
    /**
     * Controller of collection
     */
    private static CollectionController collectionController;
    /**
     * Controller of commands
     */
    private static CommandsController clientCommandsController;

    private static CommandsController serverCommandsController;
    /**
     * Controller of data file
     */
    private static DataFileController dataFileController;
    /**
     * Reader for checking if admin console input is ready
     */
    private static Reader reader;

    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers, run server and start handling client commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        logger.info("Logger for server started");

        reader = new InputStreamReader(System.in);
        Console.getInstance().setScanner(new Scanner(reader));
        logger.info("Console handler was initialized successfully");
        server = new UDPServer(Constants.serverPort);
        try {
            server.open();
            selector = Selector.open();
            server.registerSelector(selector, SelectionKey.OP_READ);
            logger.info("Server started successfully");
        } catch (IOException e) {
            logger.error("Error while starting server!", e);
            System.exit(0);
        }

        collectionController = new CollectionController(loadData());

        clientCommandsController = new CommandsController();
        clientCommandsController.setCommandsList(
               new ArrayList<>(Arrays.asList(
                        new InfoCommand(collectionController),
                        new ShowCommand(collectionController),
                        new AddCommand(collectionController),
                        new UpdateByIdCommand(collectionController),
                        new RemoveByIdCommand(collectionController),
                        new ClearCommand(collectionController),
                        new RemoveFirstCommand(collectionController),
                        new RemoveGreaterCommand(collectionController),
                        new RemoveLowerCommand(collectionController),
                        new MinBySalaryCommand(collectionController),
                        new FilterLessThanEndDateCommand(collectionController),
                        new PrintFieldDescendingSalaryCommand(collectionController)
                ))
        );
        serverCommandsController = new CommandsController();
        serverCommandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new SaveCommand(collectionController, dataFileController),
                        new ExitCommand(serverCommandsController),
                        new HelpCommand(serverCommandsController)
                ))
        );
        interactiveMode();
    }

    /**
     * Method to handle server requests
     * <p>It checks two possible sourses of requests: admin console and client
     * <p>If any error is occurred method prints error message and continues to read data
     */
    public static void interactiveMode() {
        while(true) {
            try {
                askClient();
                askAdmin();
            } catch (IOException e) {
                logger.error("An error occurred while reading request!", e);
            }
        }
    }

    /**
     * Method to handle requests from all clients
     * <p>It gets list of all available clients and then call {@link #handleClientRequest(ClientRequest)} method
     * @throws IOException If any I\O error occurred
     * @throws ReceivingDataException If error occurred while receiving data from client
     */
    private static void askClient() throws IOException {
        if(selector.selectNow() == 0) return;
        Set<SelectionKey> keys = selector.selectedKeys();
        for (var iter = keys.iterator(); iter.hasNext();){
            SelectionKey key = iter.next(); iter.remove();
            if(key.isValid()){
                if(key.isReadable()){
                    ClientRequest clientRequest;
                    try {
                        clientRequest = (ClientRequest) server.receiveObject();
                        handleClientRequest(clientRequest);
                    } catch (SendingDataException e) {
                        logger.error("Could not send data to client", e);
                    } catch (ReceivingDataException e) {
                        logger.error("Could not receive data from client", e);
                    }
                }
            }
        }
    }

    /**
     * Method to check if any commands from admin were received
     * <p>If console input is ready, command is read using {@link CommandReader}
     * <p>Then {@link #handleAdminRequest(PackedCommand)} method is called
     * @throws IOException
     */
    private static void askAdmin() throws IOException {
        if(reader.ready()) {
            PackedCommand packedCommand = CommandReader.getInstance().readCommand();
            if(packedCommand != null) handleAdminRequest(packedCommand);
        }
    }

    /**
     * Method to handle commands from admin
     * <p>It creates {@link UserCommand} object, executes it and print result to console
     * @param packedCommand Object which contains all info about command
     */
    private static void handleAdminRequest(PackedCommand packedCommand) {
        UserCommand command;
        try {
            command = serverCommandsController.launchCommand(packedCommand);
        } catch (Exception e) {
            Console.getInstance().printError(e.getMessage());
            return;
        }
        ExecuteCommandResponse responce = command.execute();
        switch (responce.state()) {
            case SUCCESS:
                Console.getInstance().printLn(responce.data());
                break;
            case EXCEPTION:
                Console.getInstance().printError(((Exception) responce.data()).getMessage());
        }
    }

    /**
     * Method to handle requests from client
     * <p>It check what type of requests is received and then generate and rend answer to client
     * @param clientRequest Request from client
     * @throws IOException If any I\O error occurred while sending answer to client
     */
    private static void handleClientRequest(ClientRequest clientRequest) throws SendingDataException {
        switch (clientRequest.type()) {
            case EXECUTE_COMMAND:
                PackedCommand packedCommand = (PackedCommand) clientRequest.data();
                logger.info("Request for executing command {}", packedCommand.commandName());
                ExecuteCommandResponse executeCommandResponse = null;
                try {
                    UserCommand command = clientCommandsController.launchCommand(packedCommand);
                    executeCommandResponse = command.execute();
                } catch (Exception e) {
                    executeCommandResponse = new ExecuteCommandResponse(ResultState.EXCEPTION, e);
                } finally {
                    server.sendObject(executeCommandResponse);
                    logger.info("Command {} executed successfully", packedCommand.commandName());
                }
                break;
            case CHECK_ID:
                long id = (long) clientRequest.data();
                logger.info("Request for checking id {}",id);
                server.sendObject(collectionController.containsId(id));
                logger.info("Request handled successfully");
                break;
            case IS_COLLECTION_EMPTY:
                logger.info("Request for checking if collection is empty");
                server.sendObject(collectionController.getCollection().isEmpty());
                logger.info("Request handled successfully");
                break;
        }
    }

    /**
     * Method to read name of environmental variable from console
     * <p>If variable if is not valid it prints an error message and stops program
     * @return String path to data file
     */
    private static String readFileName(){
        Console.getInstance().print("Enter environmental variable name: ");
        String envName = Console.getInstance().readLine().trim();
        String dataFilePath = System.getenv(envName);
        if(dataFilePath == null){
            logger.error("Environmental variable is not defined!");
            System.exit(0);
        }
        return dataFilePath;
    }

    /**
     * Method to load collection from data file.
     * <p>Method also completes validation of filePath and collection inside dataFile
     * @return Collection of workers
     * @see DataFileController
     * @see CollectionController
     */
    private static PriorityQueue<Worker> loadData(){
        String dataFilePath = readFileName();

        PriorityQueue<Worker> data = null;
        File dataFile = null;

        try {
            dataFile = new FileLoader().loadFile(dataFilePath, "json", "rw", "data file");
        } catch (FileNotFoundException e) {
            Console.getInstance().printError(e.getMessage());
            System.exit(0);
        }

        dataFileController = new DataFileController(dataFile);

        try {
            data = dataFileController.readJSON();
        } catch (Exception e) {
            logger.error("Data file reading error!", e);
            System.exit(0);
        }
        if(data == null) data = new PriorityQueue<>();
        if(!CollectionController.isValid(data)){
            logger.error("Data file is not valid!");
            System.exit(0);
        }
        logger.info("Data was loaded successfully!");
        return data;
    }
}