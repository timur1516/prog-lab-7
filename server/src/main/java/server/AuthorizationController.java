package server;

import common.Exceptions.UsernameAlreadyExistsException;
import common.Exceptions.UsernameNotFoundException;
import common.Exceptions.WrongPasswordException;
import common.UI.Console;
import common.UserInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorizationController {
    public static void logIn(UserInfo userInfo) throws SQLException, WrongPasswordException, UsernameNotFoundException {
        if(!checkUsername(userInfo.userName())){
            throw new UsernameNotFoundException(userInfo.userName());
        }
        PreparedStatement login_query = DBQueries.LOG_IN_USER();
        login_query.setString(1, userInfo.userName());
        login_query.setString(2, userInfo.password());
        ResultSet result = login_query.executeQuery();
        result.next();
        boolean flag = result.getBoolean("exists");
        result.close();
        login_query.close();
        if(flag) return;
        throw new WrongPasswordException();
    }

    public static boolean checkUsername(String username) throws SQLException {
        PreparedStatement check_username_query = DBQueries.CHECK_USERNAME();
        check_username_query.setString(1, username);
        ResultSet result = check_username_query.executeQuery();
        result.next();
        boolean flag = result.getBoolean("exists");
        result.close();
        check_username_query.close();
        return flag;
    }

    public static void addUser(UserInfo userInfo) throws SQLException {
        PreparedStatement add_user_query = DBQueries.ADD_USER();
        add_user_query.setString(1, userInfo.userName());
        add_user_query.setString(2, userInfo.password());
        add_user_query.executeUpdate();
        add_user_query.close();
    }
}
