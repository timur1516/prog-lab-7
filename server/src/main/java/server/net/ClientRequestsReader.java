package server.net;

import common.Controllers.CommandsController;
import common.Exceptions.ReceivingDataException;
import server.utils.ServerLogger;

import java.util.concurrent.*;


public class ClientRequestsReader implements Runnable{
    private final UDPServer server;
    private final ClientRequestHandler requestHandler;

    public ClientRequestsReader(UDPServer server, ClientRequestHandler requestHandler){
        this.server = server;
        this.requestHandler = requestHandler;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                HandlingTask handlingTask = server.receiveObject();
                if (handlingTask == null) continue;
                this.requestHandler.handleTask(handlingTask);
            } catch (ReceivingDataException e) {
                ServerLogger.getInstace().error("Could not receive data from client", e);
            }
        }
    }
}
