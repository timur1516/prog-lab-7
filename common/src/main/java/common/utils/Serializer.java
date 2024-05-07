package common.utils;

import java.io.*;

/**
 * Class with methods for serialization and deserialization of objects
 */
public class Serializer {
    /**
     * Method to serialize object
     * <p>Object must be {@link Serializable}
     * @param o Object to send
     * @return byte array
     * @throws IOException If any I\O error occurred while serializing
     */
    public static byte[] serialize(Serializable o) throws IOException {
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
    public static Serializable deserialize(byte[] arr) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Serializable) ois.readObject();
    }
}
