package common.Exceptions.authorization;

/**
 * Exception which is thrown if user entered wrong password
 */
public class WrongPasswordException extends AuthorizationException {
    public WrongPasswordException() {
        super("Wrong password!");
    }
}
