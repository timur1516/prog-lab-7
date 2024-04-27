package common.Exceptions;

public class UsernameAlreadyExistsException extends AuthorizationException {
    public UsernameAlreadyExistsException(String username) {
        super(String.format("User with username %s already exists!", username));
    }
}
