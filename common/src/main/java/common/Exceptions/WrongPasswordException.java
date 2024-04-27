package common.Exceptions;

public class WrongPasswordException extends AuthorizationException {
    public WrongPasswordException() {
        super("Wrong password!");
    }
}
