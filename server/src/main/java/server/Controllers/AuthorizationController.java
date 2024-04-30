package server.Controllers;

import common.Exceptions.UsernameNotFoundException;
import common.Exceptions.WrongPasswordException;
import common.net.dataTransfer.UserInfo;
import common.utils.PasswordHasher;
import common.utils.RandomStringGenerator;
import server.DBQueries;

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

        PreparedStatement get_salt_query = DBQueries.GET_SALT();
        get_salt_query.setString(1, userInfo.userName());
        ResultSet saltResult = get_salt_query.executeQuery();
        saltResult.next();
        String salt = saltResult.getString(1);

        String password = new PasswordHasher().get_SHA_512_SecurePassword(userInfo.password() + salt);

        login_query.setString(2, password);
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

        String salt = new RandomStringGenerator().generate();
        String password = new PasswordHasher().get_SHA_512_SecurePassword(userInfo.password() + salt);

        add_user_query.setString(2, password);
        add_user_query.setString(3, salt);
        add_user_query.executeUpdate();
        add_user_query.close();
    }
}
