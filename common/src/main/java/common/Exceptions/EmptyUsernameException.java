package common.Exceptions;

public class EmptyUsernameException extends AuthorizationException{
    public EmptyUsernameException(){
        super("Username can't be empty!");
    }
}
