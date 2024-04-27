package common.Exceptions;

public class DifferentPasswordsException extends AuthorizationException{
    public DifferentPasswordsException(){
        super("Passwords are different!");
    }
}
