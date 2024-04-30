package server;

import common.net.NetDataTransferringHandler;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

/**
 * Class to run UDP server
 * <p>Uses {@link DatagramChannel} in non-blocking mode
 */
public class UDPServer extends NetDataTransferringHandler{
    /**
     * Datagram channel to communicate with clients
     */
    DatagramChannel dc;
    /**
     * Socket address with server port
     * <p>After receiving first request it contains address of client
     */
    SocketAddress SERVER_ADRESS;

    UDPServer(int serverPort) {
        SERVER_ADRESS = new InetSocketAddress(serverPort);
    }

    @Override
    public void open() throws IOException {
        this.dc = DatagramChannel.open();
        this.dc.bind(SERVER_ADRESS);
        this.dc.configureBlocking(false);
    }

    /**
     * Method to close server channel
     * @throws IOException If any I\O error occurred
     */
    @Override
    public void stop() throws IOException {
        this.dc.close();
        ServerLogger.getInstace().info("Server stopped");
    }

    /**
     * Method to register selector for server channel
     * @param selector Selector
     * @param ops Selector mode
     * @throws ClosedChannelException If server channel is closed
     */
    public void registerSelector(Selector selector, int ops) throws ClosedChannelException {
        this.dc.register(selector, ops);
    }

    @Override
    protected byte[] receive() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(PACKET_SIZE);
        SERVER_ADRESS = this.dc.receive(buf);
        if(SERVER_ADRESS == null) return null;
        return buf.array();
    }

    @Override
    protected void send(byte[] arr) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(PACKET_SIZE);
        buf.put(arr);
        buf.flip();
        this.dc.send(buf, SERVER_ADRESS);
    }

    public SocketAddress getAddr(){
        return this.SERVER_ADRESS;
    }
}