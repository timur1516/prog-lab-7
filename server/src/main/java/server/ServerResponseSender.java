package server;

import common.Exceptions.SendingDataException;
import common.net.requests.ServerResponse;

import java.util.concurrent.BlockingQueue;

public class ServerResponseSender implements Runnable{
    private final UDPServer server;
    BlockingQueue<SendingTask> sendingTasks;

    public ServerResponseSender(UDPServer server, BlockingQueue<SendingTask> sendingTasks){
        this.server = server;
        this.sendingTasks = sendingTasks;
    }

    @Override
    public void run() {
        while (true){
            SendingTask task = null;
            try {
                task = sendingTasks.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(task == null) continue;
            if(task.response() == null) continue;
            try {
                server.sendObject(task.response(), task.address());
            } catch (SendingDataException e) {
                ServerLogger.getInstace().error("Could not send data to client!", e);
            }
        }
    }
}
