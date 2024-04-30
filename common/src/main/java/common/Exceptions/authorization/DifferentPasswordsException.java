package common.Exceptions.authorization;

public class DifferentPasswordsException extends AuthorizationException{
    public DifferentPasswordsException(){
        super("Passwords are different!");
    }
}
