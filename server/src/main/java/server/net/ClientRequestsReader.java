package server.net;

import common.Exceptions.ReceivingDataException;
import server.utils.ServerLogger;

/**
 * {@link Runnable} task to read request from clients to server
 */
public class ClientRequestsReader implements Runnable{
    private final UDPServer server;
    private final ClientRequestHandler requestHandler;

    public ClientRequestsReader(UDPServer server, ClientRequestHandler requestHandler){
        this.server = server;
        this.requestHandler = requestHandler;
    }

    /**
     * Receive new request and send them to {@link ClientRequestsReader#requestHandler}
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                HandlingTask handlingTask = server.receiveObject();
                if (handlingTask == null) continue;
                this.requestHandler.handleTask(handlingTask);
            } catch (ReceivingDataException e) {
                ServerLogger.getInstance().error("Could not receive data from client", e);
            }
        }
    }
}
