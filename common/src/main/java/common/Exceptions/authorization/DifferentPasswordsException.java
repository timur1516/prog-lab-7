package common.Exceptions.authorization;

/**
 * Exception which is throw if user entered different password in process of registration
 */
public class DifferentPasswordsException extends AuthorizationException{
    public DifferentPasswordsException(){
        super("Passwords are different!");
    }
}
