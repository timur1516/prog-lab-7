package common.Exceptions;

/**
 * Exception which is sent to client if any error occurred on server
 */
public class ServerErrorException extends Exception{
    public ServerErrorException(){
        super("Error on server was occurred( Please try again later)");
    }
}
