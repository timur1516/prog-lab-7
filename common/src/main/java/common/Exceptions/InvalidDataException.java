package common.Exceptions;

/**
 * Signals that some data is not valid
 */
public class InvalidDataException extends Exception {
    /**
     * Constructs an {@code InvalidDataException} with the specified detail message.
     *
     * @param message
     *        The detail message
     */
    public InvalidDataException(String message){
        super(message);
    }
}
