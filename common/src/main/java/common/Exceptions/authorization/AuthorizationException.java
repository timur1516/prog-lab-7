package common.Exceptions.authorization;

/**
 * Super class for all exception which can be caused while authorization
 */
public class AuthorizationException extends Exception{
    public AuthorizationException(String message){
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " Please try again.";
    }
}
