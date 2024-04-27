package server;

import server.Controllers.DBController;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBQueries {
    public static PreparedStatement GET_COLLECTION;
    public static PreparedStatement CHECK_USERNAME;
    public static PreparedStatement ADD_USER;
    public static PreparedStatement LOG_IN_USER;
    public static void initStatements() throws SQLException {
        GET_COLLECTION = DBController.getInstance().getConnection().prepareStatement(
                "SELECT Worker.id, name, x, y, creationDate, salary, startDate, endDate, status, height, eyeColor, nationality\n" +
                        "FROM Worker\n" +
                        "LEFT JOIN Coordinates ON Worker.coordinates_id = Coordinates.id\n" +
                        "LEFT JOIN Person ON Worker.person_id = Person.id");
        CHECK_USERNAME = DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ?)");
        ADD_USER = DBController.getInstance().getConnection().prepareStatement(
                "INSERT INTO User_info(username, password) VALUES (?, ?)");
        LOG_IN_USER = DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ? AND password = ?)");
    }
}
