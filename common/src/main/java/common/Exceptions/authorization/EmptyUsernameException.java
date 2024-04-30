package common.Exceptions.authorization;

public class EmptyUsernameException extends AuthorizationException{
    public EmptyUsernameException(){
        super("Username can't be empty!");
    }
}
