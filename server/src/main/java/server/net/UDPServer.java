package server.net;

import common.Exceptions.ReceivingDataException;
import common.utils.Serializer;
import common.net.requests.ClientRequest;
import server.utils.ServerLogger;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import static common.utils.CommonConstants.PACKET_SIZE;

/**
 * Class to run UDP server
 * <p>Uses {@link DatagramChannel} in non-blocking mode
 */
public class UDPServer {
    /**
     * Datagram channel to communicate with clients
     */
    private DatagramChannel serverChannel;
    /**
     * Socket address with server port
     */
    private final SocketAddress serverAddress;

    public UDPServer(int serverPort) {
        serverAddress = new InetSocketAddress(serverPort);
    }

    public void open() throws IOException {
        this.serverChannel = DatagramChannel.open();
        this.serverChannel.bind(serverAddress);
        this.serverChannel.configureBlocking(false);
    }

    /**
     * Method to close server channel
     * @throws IOException If any I\O error occurred
     */
    public void stop() throws IOException {
        this.serverChannel.close();
        ServerLogger.getInstance().info("Server stopped");
    }

    /**
     * Method to receive object from client
     * @return Request with information about client address
     * @throws ReceivingDataException If an error occurred while receiving data from client
     */
    public HandlingTask receiveObject() throws ReceivingDataException {
        try {
            ByteBuffer buf = ByteBuffer.allocate(PACKET_SIZE);
            SocketAddress clientAddress = this.serverChannel.receive(buf);
            if(clientAddress == null) return null;
            ClientRequest clientRequest = (ClientRequest) Serializer.deserialize(buf.array());
            return new HandlingTask(clientRequest, clientAddress);
        }
        catch (Exception e){
            throw new ReceivingDataException("Server error occurred while receiving data!");
        }
    }
}