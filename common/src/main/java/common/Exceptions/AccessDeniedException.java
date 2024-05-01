package common.Exceptions;

/**
 * Exception which is thrown if client tried to modify element of collection which does not belong to him
 */
public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message){
        super(message);
    }

    @Override
    public String getMessage() {
        return "Access denied! " + super.getMessage();
    }
}
