package server;

import common.Exceptions.SendingDataException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.BlockingQueue;

import static common.net.NetDataTransferringHandler.PACKET_SIZE;

public class ServerResponseSender implements Runnable{
    BlockingQueue<SendingTask> sendingTasks;

    public ServerResponseSender(BlockingQueue<SendingTask> sendingTasks){
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
                buf.put(serialize(task.response()));
                buf.flip();
                channel.write(buf);
            } catch ( IOException e) {
                ServerLogger.getInstace().error("Could not send data to client!", e);
            }
        }
    }
    /**
     * Method to serialize object
     * <p>Object must be {@link Serializable}
     * @param o Object to send
     * @return byte array
     * @throws IOException If any I\O error occurred while serializing
     */
    private byte[] serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.flush();
        return baos.toByteArray();
    }
}
