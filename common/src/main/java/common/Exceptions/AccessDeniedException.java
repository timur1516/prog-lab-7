package common.Exceptions;

public class AccessDeniedException extends Exception {
    public AccessDeniedException(String message){
        super(message);
    }

    @Override
    public String getMessage() {
        return "Access denied! " + super.getMessage();
    }
}
