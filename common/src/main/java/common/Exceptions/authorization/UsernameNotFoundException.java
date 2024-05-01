package common.Exceptions.authorization;

/**
 * Exception which is thrown if username was not found in server database
 */
public class UsernameNotFoundException extends AuthorizationException {
    public UsernameNotFoundException(String username) {
        super(String.format("User with username %s was not found!", username));
    }
}
