package server.net;

import common.Controllers.CommandsController;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientRequestHandler {
    private final CommandsController clientCommandsController;
    private final ExecutorService handlerExecutorService;
    private final BlockingQueue<SendingTask> sendingTasks;

    public ClientRequestHandler(CommandsController clientCommandsController, BlockingQueue<SendingTask> sendingTasks){
        this.clientCommandsController = clientCommandsController;
        this.sendingTasks = sendingTasks;
        this.handlerExecutorService = Executors.newCachedThreadPool();
    }

    public void handleTask(HandlingTask handlingTask){
        this.handlerExecutorService.submit(new ClientRequestsHandlerTask(clientCommandsController.clone(), handlingTask, sendingTasks));
    }
}
