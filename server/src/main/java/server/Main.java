package server;

import common.Commands.HelpCommand;
import common.Controllers.PropertiesFilesController;
import common.UI.Console;
import server.Commands.*;
import server.Controllers.CollectionController;
import common.Controllers.CommandsController;
import server.Controllers.DBController;
import server.UI.AdminRequestsHandler;
import server.net.*;
import server.utils.ServerLogger;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main class for server app
 */
public class Main {
    /**
     * Server object
     */
    public static UDPServer server;
    /**
     * Number of senders threads
     */
    private static final int N_SENDERS_THREADS = 100;
    /**
     * Default port for server
     */
    private static final int DEFAULT_SERVER_PORT = 8081;

    /**
     * Main method of program
     * <p>Init all controllers, run server and start handling client commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        ServerLogger.getInstance().info("Logger for server started");

        Console.getInstance().setScanner(new Scanner(System.in));
        ServerLogger.getInstance().info("Console handler was initialized successfully");

        int serverPort = DEFAULT_SERVER_PORT;
        try {
            serverPort = readServerPort();
        } catch (IOException | NumberFormatException e) {
            ServerLogger.getInstance().error("Error while reading server port from config file!", e);
            ServerLogger.getInstance().info(String.format("Server port set to default value: %d", DEFAULT_SERVER_PORT));
        }
        server = new UDPServer(serverPort);

        try {
            server.open();
            ServerLogger.getInstance().info("Server started successfully");
        } catch (IOException e) {
            ServerLogger.getInstance().error("Error while starting server!", e);
            System.exit(0);
        }

        try {
            DBController.getInstance().connect();
            ServerLogger.getInstance().info("Database have been connected successfully!");
        } catch (SQLException e) {
            ServerLogger.getInstance().error("Error while connecting database!", e);
            System.exit(0);
        } catch (ClassNotFoundException e) {
            ServerLogger.getInstance().error("Database driver was not found!", e);
            System.exit(0);
        } catch (IOException e) {
            ServerLogger.getInstance().error("Error while reading login data for database", e);
            ServerLogger.getInstance().info("Make sure that file dp.properties is created and located in the same directory with server program");
            System.exit(0);
        }

        try {
            CollectionController.getInstance().loadCollection();
        } catch (SQLException e) {
            ServerLogger.getInstance().error("Error while loading collection from database!", e);
            System.exit(0);
        }

        /**
         * Controller of commands
         */
        CommandsController clientCommandsController = new CommandsController();
        clientCommandsController.setCommandsList(
               new ArrayList<>(Arrays.asList(
                        new InfoCommand(),
                        new ShowCommand(),
                        new AddCommand(),
                        new UpdateByIdCommand(),
                        new RemoveByIdCommand(),
                        new ClearCommand(),
                        new RemoveFirstCommand(),
                        new RemoveGreaterCommand(),
                        new RemoveLowerCommand(),
                        new MinBySalaryCommand(),
                        new FilterLessThanEndDateCommand(),
                        new PrintFieldDescendingSalaryCommand(),
                        new CheckIdCommand(),
                        new CheckEmptyCollectionCommand()
                ))
        );
        CommandsController serverCommandsController = new CommandsController();
        serverCommandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new ExitCommand(),
                        new HelpCommand(serverCommandsController)
                ))
        );

        Thread consoleReaderThread = new Thread(new AdminRequestsHandler(serverCommandsController));
        consoleReaderThread.start();

        BlockingQueue<SendingTask> sendingTasks = new LinkedBlockingQueue<>();

        ClientRequestHandler requestHandler = new ClientRequestHandler(clientCommandsController, sendingTasks);
        ExecutorService senderExecutorService = Executors.newFixedThreadPool(N_SENDERS_THREADS);
        ForkJoinPool clientRequestsPool = ForkJoinPool.commonPool();

        for(int i = 0; i < N_SENDERS_THREADS; i++) {
            senderExecutorService.execute(new ServerResponsesSender(sendingTasks));
        }
        clientRequestsPool.execute(new ClientRequestsReader(server, requestHandler));
    }

    private static int readServerPort() throws IOException, NumberFormatException {
        Properties configProperties = new PropertiesFilesController().readProperties("serverConfig.properties");
        return Integer.parseInt(configProperties.getProperty("port"));
    }
}