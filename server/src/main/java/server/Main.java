package server;

import common.Commands.HelpCommand;
import common.Constants;
import common.UI.Console;
import server.Commands.*;
import server.Controllers.CollectionController;
import common.Controllers.CommandsController;
import server.Controllers.DBController;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * Main class for server app
 */
public class Main {
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
     * Reader for checking if admin console input is ready
     */
    private static Reader reader;

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
            selector = Selector.open();
            server.registerSelector(selector, SelectionKey.OP_READ);
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

        collectionController = new CollectionController();
        try {
            collectionController.loadCollection();
        } catch (SQLException e) {
            ServerLogger.getInstace().error("Error while loading collection from database!", e);
        }

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
                        new PrintFieldDescendingSalaryCommand(collectionController),
                        new CheckIdCommand(collectionController),
                        new CheckEmptyCollectionCommand(collectionController)
                ))
        );
        serverCommandsController = new CommandsController();
        serverCommandsController.setCommandsList(
                new ArrayList<>(Arrays.asList(
                        new ExitCommand(serverCommandsController),
                        new HelpCommand(serverCommandsController)
                ))
        );

        Thread consoleReaderThread = new Thread(new AdminRequestsReader(serverCommandsController));
        consoleReaderThread.start();

        ForkJoinPool clientRequestsPool = ForkJoinPool.commonPool();
        clientRequestsPool.execute(new ClientRequestReader(server, collectionController, clientCommandsController));
    }
}