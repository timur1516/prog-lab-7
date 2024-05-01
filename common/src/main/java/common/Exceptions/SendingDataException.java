package common.Exceptions;

/**
 * Exception which is thrown if an error occurred in process of sending data from
 */
public class SendingDataException extends Exception{
    public SendingDataException(String message){
        super(message);
    }
}
