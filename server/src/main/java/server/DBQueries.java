package server;

import jdk.jfr.Percentage;
import server.Controllers.DBController;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBQueries {
    public static PreparedStatement GET_COLLECTION() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT Worker.id, name, x, y, creationDate, salary, startDate, endDate, status, height, eyeColor, nationality " +
                        "FROM Worker " +
                        "LEFT JOIN Coordinates ON Worker.coordinates_id = Coordinates.id " +
                        "LEFT JOIN Person ON Worker.person_id = Person.id");
    }

    public static PreparedStatement CHECK_USERNAME() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ?)");
    }

    public static PreparedStatement ADD_USER() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "INSERT INTO User_info(username, password, salt) VALUES (?, ?, ?)");
    }

    public static PreparedStatement LOG_IN_USER() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ? AND password = ?)");
    }

    public static PreparedStatement GET_SALT() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT salt FROM User_info WHERE username = ?");
    }

    public static PreparedStatement CLEAR_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?)");
    }

    public static PreparedStatement ADD_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "CALL add_worker(?::text, ?::double precision, ?::double precision, ?::integer, ?::timestamp, ?::timestamp, ?::status, ?::bigint, ?::color, ?::country, ?::text)"
        );
    }

    public static PreparedStatement REMOVE_BY_ID_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "id = ?"
        );
    }

    public static PreparedStatement REMOVE_FIRST_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "Worker.id IN (SELECT Worker.id FROM Worker ORDER BY name LIMIT 1);"
        );
    }

    public static PreparedStatement REMOVE_GREATER_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "name > ?"
        );
    }

    public static PreparedStatement REMOVE_LOWER_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "name < ?"
        );
    }

    public static PreparedStatement UPDATE_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "CALL update_worker(?::text, ?::double precision, ?::double precision, ?::integer, ?::timestamp, ?::timestamp, ?::status, ?::bigint, ?::color, ?::country, ?::text, ?::integer)"
        );
    }
}
