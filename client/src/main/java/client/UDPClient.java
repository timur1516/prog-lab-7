package client;

import common.Exceptions.ReceivingDataException;
import common.net.NetDataTransferringHandler;

import java.io.*;
import java.net.*;

/**
 * Singleton class for UPD client
 */
public class UDPClient extends NetDataTransferringHandler {
    private static UDPClient UDP_CLIENT = null;
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

    private UDPClient(){};
    public static UDPClient getInstance(){
        if(UDP_CLIENT == null){
            UDP_CLIENT = new UDPClient();
        }
        return UDP_CLIENT;
    }

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
    protected byte[] receive(int len) throws IOException {
        byte arr[] = new byte[len];
        DatagramPacket dp = new DatagramPacket(arr, len);
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
