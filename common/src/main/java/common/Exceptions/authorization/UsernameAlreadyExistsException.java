package common.Exceptions.authorization;

/**
 * Exception which is thrown if user used name which is already used
 */
public class UsernameAlreadyExistsException extends AuthorizationException {
    public UsernameAlreadyExistsException(String username) {
        super(String.format("User with username %s already exists!", username));
    }
}
