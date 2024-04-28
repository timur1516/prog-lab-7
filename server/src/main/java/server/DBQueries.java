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
                "INSERT INTO User_info(username, password) VALUES (?, ?)");
    }

    public static PreparedStatement LOG_IN_USER() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ? AND password = ?)");
    }

    public static PreparedStatement CLEAR_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?)");
    }

    public static PreparedStatement ADD_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "CALL add_worker(?::text, ?::real, ?::real, ?::integer, ?::timestamp, ?::timestamp, ?::status, ?::integer, ?::color, ?::country, ?::text)");
    }
}
