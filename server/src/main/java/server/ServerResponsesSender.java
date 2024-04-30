package server;

import common.utils.Serializator;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;

import static common.utils.CommonConstants.PACKET_SIZE;

public class ServerResponsesSender implements Runnable{
    BlockingQueue<SendingTask> sendingTasks;

    public ServerResponsesSender(BlockingQueue<SendingTask> sendingTasks){
        this.sendingTasks = sendingTasks;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            SendingTask task = null;
            try {
                task = sendingTasks.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(task.response() == null) continue;
            try {
                DatagramChannel channel = DatagramChannel.open();
                channel.connect(task.address());
                ByteBuffer buf = ByteBuffer.allocate(PACKET_SIZE);
                buf.put(Serializator.serialize(task.response()));
                buf.flip();
                channel.write(buf);
                channel.close();
            } catch ( IOException e) {
                ServerLogger.getInstace().error("Could not send data to client!", e);
            }
        }
    }
}
