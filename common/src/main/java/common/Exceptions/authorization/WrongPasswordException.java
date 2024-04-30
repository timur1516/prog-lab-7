package common.Exceptions.authorization;

public class WrongPasswordException extends AuthorizationException {
    public WrongPasswordException() {
        super("Wrong password!");
    }
}
