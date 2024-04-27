package common.Exceptions;

public class ServerErrorException extends Exception{
    public ServerErrorException(){
        super("Error on server was occurred( Please try again later)");
    }
}
