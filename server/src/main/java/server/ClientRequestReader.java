package server;

import common.Controllers.CommandsController;
import common.Exceptions.ReceivingDataException;
import common.Exceptions.ServerErrorException;
import common.net.requests.ClientRequest;
import common.net.requests.ResultState;
import common.net.requests.ServerResponse;
import server.Controllers.CollectionController;

import java.net.SocketAddress;
import java.util.concurrent.*;


public class ClientRequestReader implements Runnable{
    private final CommandsController clientCommandsController;
    private final UDPServer server;
    private final CollectionController collectionController;
    private final ExecutorService handlerExecutorService;
    private final ExecutorService senderExecutorService;
    private BlockingQueue<SendingTask> sendingTasks;
    private static final int THREADS = 1;

    public ClientRequestReader(UDPServer server, CollectionController collectionController, CommandsController clientCommandsController){
        this.server = server;
        this.collectionController = collectionController;
        this.clientCommandsController = clientCommandsController;
        this.handlerExecutorService = Executors.newCachedThreadPool();
        this.senderExecutorService = Executors.newFixedThreadPool(THREADS);
        this.sendingTasks = new LinkedBlockingQueue<>();

        for(int i = 0; i < THREADS; i++){
            this.senderExecutorService.submit(new ServerResponseSender(server, sendingTasks));
        }
    }

    @Override
    public void run() {
        while (true) {
            ServerResponse response = null;
            SocketAddress addr = null;
            try {
                ClientRequest clientRequest = (ClientRequest) server.receiveObject();
                if(clientRequest == null) continue;
                addr = server.getAddr();
                ClientRequestHandler handlerTask = new ClientRequestHandler(server, clientRequest, clientCommandsController, collectionController);
                response = handlerExecutorService.submit(handlerTask).get();
            } catch (ReceivingDataException e) {
                ServerLogger.getInstace().error("Could not receive data from client", e);
            } catch (ExecutionException e) {
                ServerLogger.getInstace().error("Database error occurred", e);
                response = new ServerResponse(ResultState.EXCEPTION, new ServerErrorException());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                this.sendingTasks.put(new SendingTask(response, addr));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }
    }
}
