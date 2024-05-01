package server.Controllers;

import common.Exceptions.authorization.UsernameNotFoundException;
import common.Exceptions.authorization.WrongPasswordException;
import common.net.dataTransfer.UserInfo;
import common.utils.PasswordHasher;
import common.utils.RandomStringGenerator;
import server.DB.DBQueries;
import server.DB.DBQueriesExecutors;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class to complete authorization of client on server
 */
public class AuthorizationController {
    /**
     * Method to login user
     * @param userInfo Information about user
     * @throws SQLException If an error occurred while working with database
     * @throws WrongPasswordException If user's password is wrong
     * @throws UsernameNotFoundException If user was not found
     */
    public static void logIn(UserInfo userInfo) throws SQLException, WrongPasswordException, UsernameNotFoundException {
        if(!checkUsername(userInfo.userName())){
            throw new UsernameNotFoundException(userInfo.userName());
        }

        String password = new PasswordHasher().
                get_SHA_512_SecurePassword(
                        userInfo.password() + DBQueriesExecutors.getSaltExecutor(userInfo.userName()));

        if(DBQueriesExecutors.logInUserExecutor(userInfo.userName(), password)) return;
        throw new WrongPasswordException();
    }

    /**
     * Method to check if user with given username is registered
     * @param username Username to check
     * @return Boolean value with result of check
     * @throws SQLException If an error occurred while working with database
     */
    public static boolean checkUsername(String username) throws SQLException {
        return DBQueriesExecutors.checkUsername(username);
    }

    /**
     * Method to add new user
     * @param userInfo Information about new user
     * @throws SQLException If an error occurred while working with database
     */
    public static void addUser(UserInfo userInfo) throws SQLException {
        String salt = new RandomStringGenerator().generate();
        String password = new PasswordHasher().get_SHA_512_SecurePassword(userInfo.password() + salt);

        DBQueriesExecutors.addUser(userInfo.userName(), password, salt);
    }
}
