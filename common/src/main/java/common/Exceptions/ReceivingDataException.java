package common.Exceptions;

/**
 * Exception which is thrown if an error occurred in process of receiving data from
 */
public class ReceivingDataException extends Exception{
    public ReceivingDataException(String message){
        super(message);
    }
}
