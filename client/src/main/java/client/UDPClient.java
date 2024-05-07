package client;

import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;
import common.utils.Serializer;

import java.io.*;
import java.net.*;

import static common.utils.CommonConstants.PACKET_SIZE;

/**
 * Singleton class for UPD client
 */
public class UDPClient {
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
    public void open() throws SocketException {
        this.ds = new DatagramSocket();
        this.ds.setSoTimeout(this.timeout);
    }

    /**
     * Method to stop UPD client
     */
    public void stop() {
        this.ds.close();
    }

    /**
     * Method to receive Serializable object
     * @return Object which was received
     * @throws ReceivingDataException If any error while receiving data was occurred
     */
    public Serializable receiveObject() throws ReceivingDataException {
        try {
            byte arr[] = new byte[PACKET_SIZE];
            DatagramPacket dp = new DatagramPacket(arr, PACKET_SIZE);
            this.ds.receive(dp);
            return Serializer.deserialize(arr);
        }
        catch (Exception e){
            throw new ReceivingDataException("Server error occurred while receiving data!");
        }
    }

    /**
     * Method to send Serializable object
     * @param o Object to send
     * @throws SendingDataException If any error occurred while sending data
     */
    public void sendObject(Serializable o) throws SendingDataException {
        try {
            byte arr[] = Serializer.serialize(o);
            DatagramPacket dp = new DatagramPacket(arr, arr.length, this.host, this.port);
            this.ds.send(dp);
        }
        catch (Exception e){
            throw new SendingDataException("Error while sending data!");
        }
    }
}
