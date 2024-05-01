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

public class AuthorizationController {
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

    public static boolean checkUsername(String username) throws SQLException {
        return DBQueriesExecutors.checkUsername(username);
    }

    public static void addUser(UserInfo userInfo) throws SQLException {
        String salt = new RandomStringGenerator().generate();
        String password = new PasswordHasher().get_SHA_512_SecurePassword(userInfo.password() + salt);

        DBQueriesExecutors.addUser(userInfo.userName(), password, salt);
    }
}
