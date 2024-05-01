package server.net;

import common.Controllers.CommandsController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class to handle request from client
 * <p>It contains :
 * <p>{@link CommandsController} for client,
 * <p>{@link ExecutorService} to run execution threads for each request
 * <p>{@link BlockingQueue} to add responses from handled requests
 */
public class ClientRequestHandler {
    private final CommandsController clientCommandsController;
    private final ExecutorService handlerExecutorService;
    private final BlockingQueue<SendingTask> sendingTasks;

    public ClientRequestHandler(CommandsController clientCommandsController, BlockingQueue<SendingTask> sendingTasks){
        this.clientCommandsController = clientCommandsController;
        this.sendingTasks = sendingTasks;
        this.handlerExecutorService = Executors.newCachedThreadPool();
    }

    /**
     * Method to add new {@link HandlingTask} to executor
     * <p>{@link CommandsController} is cloned for each task
     * @param handlingTask
     */
    public void handleTask(HandlingTask handlingTask){
        this.handlerExecutorService.execute(new ClientRequestsHandlerTask(clientCommandsController.clone(), handlingTask, sendingTasks));
    }
}
