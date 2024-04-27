package server;

import common.Exceptions.UsernameAlreadyExistsException;
import common.Exceptions.UsernameNotFoundException;
import common.Exceptions.WrongPasswordException;
import common.UI.Console;
import common.UserInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorizationController {
    public static void logIn(UserInfo userInfo) throws SQLException, WrongPasswordException, UsernameNotFoundException {
        if(!checkUsername(userInfo.userName())){
            throw new UsernameNotFoundException(userInfo.userName());
        }
        DBQueries.LOG_IN_USER.setString(1, userInfo.userName());
        DBQueries.LOG_IN_USER.setString(2, userInfo.password());
        ResultSet result = DBQueries.LOG_IN_USER.executeQuery();
        result.next();
        if(result.getBoolean("exists")) return;
        throw new WrongPasswordException();
    }

    public static boolean checkUsername(String username) throws SQLException {
        DBQueries.CHECK_USERNAME.setString(1, username);
        ResultSet result = DBQueries.CHECK_USERNAME.executeQuery();
        result.next();
        return result.getBoolean("exists");
    }

    public static void addUser(UserInfo userInfo) throws SQLException {
        DBQueries.ADD_USER.setString(1, userInfo.userName());
        DBQueries.ADD_USER.setString(2, userInfo.password());
        int val = DBQueries.ADD_USER.executeUpdate();
    }
}
