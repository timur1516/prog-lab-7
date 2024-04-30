package server;

import common.Exceptions.ReceivingDataException;

import java.util.concurrent.*;


public class ClientRequestsReader implements Runnable{
    private final UDPServer server;
    private final BlockingQueue<HandlingTask> handlingTasks;

    public ClientRequestsReader(UDPServer server, BlockingQueue<HandlingTask> handlingTasks){
        this.server = server;
        this.handlingTasks = handlingTasks;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                HandlingTask handlingTask = server.receiveObject();
                if(handlingTask == null) continue;
                this.handlingTasks.put(handlingTask);
            } catch (ReceivingDataException e) {
                ServerLogger.getInstace().error("Could not receive data from client", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
