package common.net;

import common.Exceptions.ReceivingDataException;
import common.Exceptions.SendingDataException;

import java.io.*;

/**
 * Abstract class to handle net data transferring
 * <p>It has methods for serialization and deserialization of objects and methods to send and receive objects
 */
public abstract class NetDataTransferringHandler {
    /**
     * Method to init and start dataTransferring channel
     * @throws IOException If any error while opening occurred
     */
    public abstract void open() throws IOException;

    /**
     * Method to close dataTransferring channel
     * @throws IOException If any I\O error occurred while closing
     */
    public abstract void stop() throws IOException;

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

    /**
     * Method to deserialize object
     * @param arr Byte array
     * @return Serializable object
     * @throws IOException If any error occurred while deserialization
     * @throws ClassNotFoundException If object class was not found
     */
    private Serializable deserialize(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Serializable) ois.readObject();
    }

    /**
     * Abstract method to receive byte array with given length
     * @param len Length of receiving array
     * @return Byte array which was received
     * @throws IOException If any I\O error occurred while receiving data
     */
    protected abstract byte[] receive(int len) throws IOException;

    /**
     * Abstract class to send byte array
     * @param arr Byte array to send
     * @throws IOException If any I\O error occurred while sending data
     */
    protected abstract void send(byte[] arr) throws IOException;

    /**
     * Method to receive Serializable object
     * <p>Firstly it receives length of object and then object is read
     * @return Object which was received
     * @throws ReceivingDataException If any error while receiving data was occurred
     */
    public Serializable receiveObject() throws ReceivingDataException {
        try {
            Integer dataLen = (Integer) deserialize(receive(81));
            return deserialize(receive(dataLen));
        }
        catch (Exception e){
            throw new ReceivingDataException("Error while receiving data!");
        }
    }

    /**
     * Method to send Serializable object
     * @param o Object to send
     * @throws SendingDataException If any error occurred while sending data
     */
    public void sendObject(Serializable o) throws SendingDataException {
        try {
            byte arr[] = serialize(o);
            send(serialize(arr.length));
            send(arr);
        }
        catch (Exception e){
            throw new SendingDataException("Error while sending data!");
        }
    }
}
