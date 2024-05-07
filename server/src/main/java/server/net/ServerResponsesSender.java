package server.net;

import common.utils.Serializer;
import server.utils.ServerLogger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;

import static common.utils.CommonConstants.PACKET_SIZE;

/**
 * {@link Runnable} task to send responses to clients
 */
public class ServerResponsesSender implements Runnable{
    BlockingQueue<SendingTask> sendingTasks;

    public ServerResponsesSender(BlockingQueue<SendingTask> sendingTasks){
        this.sendingTasks = sendingTasks;
    }

    /**
     * It reads responses from {@link BlockingQueue} and then send than
     * <p>For each client new channel is opened and then closed
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            SendingTask task;
            try {
                task = sendingTasks.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            if(task.response() == null) continue;
            try {
                DatagramChannel channel = DatagramChannel.open();
                channel.connect(task.address());
                ByteBuffer buf = ByteBuffer.allocate(PACKET_SIZE);
                buf.put(Serializer.serialize(task.response()));
                buf.flip();
                channel.write(buf);
                channel.close();
            } catch ( IOException e) {
                ServerLogger.getInstance().error("Could not send data to client!", e);
            }
        }
    }
}
