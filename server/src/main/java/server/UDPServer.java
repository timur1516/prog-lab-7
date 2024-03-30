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
public class UDPServer extends NetDataTransferringHandler {
    /**
     * Datagram channel to communicate with clients
     */
    DatagramChannel dc;
    /**
     * Socket address with server port
     * <p>After receiving first request it contains address of client
     */
    SocketAddress addr;

    UDPServer(int serverPort) {
        addr = new InetSocketAddress(serverPort);
    }

    /**
     * Method to open server channel
     * <p>It also configure channel to non-blocking mode
     * @throws IOException If any I\O error occurred
     */
    @Override
    public void open() throws IOException {
        this.dc = DatagramChannel.open();
        this.dc.bind(addr);
        this.dc.configureBlocking(false);
    }

    /**
     * Method to close server channel
     * @throws IOException If any I\O error occurred
     */
    @Override
    public void stop() throws IOException {
        this.dc.close();
        Main.logger.info("Server stopped");
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
    protected byte[] receive(int len) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(len);
        addr = this.dc.receive(buf);
        return buf.array();
    }

    @Override
    protected void send(byte[] arr) throws IOException {
         ByteBuffer buf = ByteBuffer.wrap(arr);
         this.dc.send(buf, addr);
    }
}