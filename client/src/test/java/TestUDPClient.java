
import client.UDPClient;
import common.Exceptions.ReceivingDataException;
import common.net.NetDataTransferringHandler;

import java.io.*;
import java.net.*;

/**
 * Singleton class for UPD client
 */
public class TestUDPClient extends NetDataTransferringHandler {
    /**
     * Datagram socket for client
     */
    DatagramSocket ds;
    /**
     * Servers host
     */
    InetAddress host;
    /**
     * Servers port
     */
    int port;
    /**
     * Timeout for waiting for server response
     */
    int timeout;

    /**
     * Method to init client
     * @param host
     * @param port
     * @param timeout
     */
    public void init(InetAddress host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    /**
     * Method to start UDP client
     * @throws SocketException If any error occurred
     */
    @Override
    public void open() throws SocketException {
        this.ds = new DatagramSocket();
        this.ds.setSoTimeout(this.timeout);
    }

    /**
     * Method to stop UPD client
     */
    @Override
    public void stop() {
        this.ds.close();
    }

    @Override
    protected byte[] receive() throws IOException {
        byte arr[] = new byte[PACKET_SIZE];
        DatagramPacket dp = new DatagramPacket(arr, PACKET_SIZE);
        this.ds.receive(dp);
        return arr;
    }

    @Override
    public Serializable receiveObject() throws ReceivingDataException {
        try {
            return super.receiveObject();
        }catch (ReceivingDataException e){
            throw new ReceivingDataException("Server unavailable!");
        }

    }

    @Override
    protected void send(byte[] arr) throws IOException {
        DatagramPacket dp = new DatagramPacket(arr, arr.length, this.host, this.port);
        this.ds.send(dp);
    }
}
