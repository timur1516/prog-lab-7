package server.Exceptions;

/**
 * Exception for error in exit command
 */
public class ExitingException extends Exception{
    public ExitingException(String message){
        super(message);
    }
}
