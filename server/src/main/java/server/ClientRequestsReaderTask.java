package server;

import common.Exceptions.ReceivingDataException;
import common.net.requests.ClientRequest;
import java.util.concurrent.*;


public class ClientRequestsReaderTask implements Runnable{
    private final UDPServer server;
    private final BlockingQueue<HandlingTask> handlingTasks;

    public ClientRequestsReaderTask(UDPServer server, BlockingQueue<HandlingTask> handlingTasks){
        this.server = server;
        this.handlingTasks = handlingTasks;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ClientRequest clientRequest = (ClientRequest) server.receiveObject();
                if(clientRequest == null) continue;
                this.handlingTasks.put(new HandlingTask(clientRequest, server.getAddr()));
            } catch (ReceivingDataException e) {
                ServerLogger.getInstace().error("Could not receive data from client", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
