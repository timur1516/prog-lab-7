package server;

import common.Commands.HelpCommand;
import common.Constants;
import common.UI.Console;
import server.Commands.*;
import server.Controllers.CollectionController;
import common.Controllers.CommandsController;
import server.Controllers.DBController;

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

    private static final int N_SENDERS_THREADS = 10;
    private static final int N_HANDLERS_THREADS = 10;

    /**
     * Controller of collection
     */
    private static CollectionController collectionController;

    /**
     * Main method of program
     * <p>Calls methods to load data file, init all controllers, run server and start handling client commands
     * @param args (not used)
     */
    public static void main(String[] args) {
        ServerLogger.getInstace().info("Logger for server started");

        Console.getInstance().setScanner(new Scanner(System.in));
        ServerLogger.getInstace().info("Console handler was initialized successfully");

        server = new UDPServer(Constants.serverPort);
        try {
            server.open();
            ServerLogger.getInstace().info("Server started successfully");
        } catch (IOException e) {
            ServerLogger.getInstace().error("Error while starting server!", e);
            System.exit(0);
        }

        try {
            DBController.getInstance().connect();
            ServerLogger.getInstace().info("Database have been connected successfully!");
        } catch (SQLException e) {
            ServerLogger.getInstace().error("Error while connecting database!", e);
            System.exit(0);
        } catch (ClassNotFoundException e) {
            ServerLogger.getInstace().error("Database driver was not found!", e);
            System.exit(0);
        }

        try {
            CollectionController.getInstance().loadCollection();
        } catch (SQLException e) {
            ServerLogger.getInstace().error("Error while loading collection from database!", e);
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

        Thread consoleReaderThread = new Thread(new AdminRequestsReader(serverCommandsController));
        consoleReaderThread.start();

        BlockingQueue<SendingTask> sendingTasks = new LinkedBlockingQueue<>();
        BlockingQueue<HandlingTask> handlingTasks = new LinkedBlockingQueue<>();

        ExecutorService handlerExecutorService = Executors.newCachedThreadPool();
        ExecutorService senderExecutorService = Executors.newFixedThreadPool(N_SENDERS_THREADS);
        ForkJoinPool clientRequestsPool = ForkJoinPool.commonPool();

        for(int i = 0; i < N_HANDLERS_THREADS; i++) {
            handlerExecutorService.execute(new ClientRequestHandlerTask(clientCommandsController, handlingTasks, sendingTasks));
        }
        for(int i = 0; i < N_SENDERS_THREADS; i++) {
            senderExecutorService.execute(new ServerResponseSender(sendingTasks));
        }
        clientRequestsPool.execute(new ClientRequestsReaderTask(server, handlingTasks));
    }
}