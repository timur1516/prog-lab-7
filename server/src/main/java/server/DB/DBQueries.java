package server.DB;

import server.Controllers.DBController;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Class which contain realization of all queries to database which are used in program
 * <>All queries are stored as {@link PreparedStatement}
 */
public class DBQueries {
    /**
     * Query to get all collection from database
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement GET_COLLECTION() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT Worker.id, name, x, y, creationDate, salary, startDate, endDate, status, height, eyeColor, nationality " +
                        "FROM Worker " +
                        "LEFT JOIN Coordinates ON Worker.coordinates_id = Coordinates.id " +
                        "LEFT JOIN Person ON Worker.person_id = Person.id");
    }

    /**
     * Query to check is username exist in database
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement CHECK_USERNAME() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ?)");
    }

    /**
     * Query to add new user into collection
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement ADD_USER() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "INSERT INTO User_info(username, password, salt) VALUES (?, ?, ?)");
    }

    /**
     * Query to check user's password and username for login
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement LOG_IN_USER() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS (SELECT 1 FROM User_info WHERE username = ? AND password = ?)");
    }

    /**
     * Query to get salt for user
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement GET_SALT() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT salt FROM User_info WHERE username = ?");
    }

    /**
     * Query to check if user can modify element with given id
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement CHECK_ACCESS() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "SELECT EXISTS " +
                        "(SELECT 1 FROM Worker JOIN User_info " +
                        "ON User_info.id = Worker.user_id " +
                        "WHERE Worker.id = ? AND username = ?)");
    }

    /**
     * Query to delete all elements created by given user
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement CLEAR_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareStatement(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?)");
    }

    /**
     * Query to add new element
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement ADD_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "CALL add_worker(?::text, ?::double precision, ?::double precision, ?::integer, ?::timestamp, ?::timestamp, ?::status, ?::bigint, ?::color, ?::country, ?::text)"
        );
    }

    /**
     * Query to remove element by its id
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement REMOVE_BY_ID_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "id = ?"
        );
    }

    /**
     * Query to remove the minimal element from collection which belongs to given user
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement REMOVE_FIRST_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "Worker.id IN (SELECT Worker.id FROM Worker ORDER BY name LIMIT 1);"
        );
    }

    /**
     * Query to remove elements which are greater than given and belongs to given user
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement REMOVE_GREATER_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "name > ?"
        );
    }

    /**
     * Query to remove elements which are lower than given and belongs to given user
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement REMOVE_LOWER_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "DELETE FROM Worker WHERE user_id IN (SELECT id FROM User_info WHERE username = ?) AND " +
                        "name < ?"
        );
    }

    /**
     * Query to update element with given id
     * @return New {@link PreparedStatement} which was got from current {@link java.sql.Connection} session
     * @throws SQLException If an error occurred while working with database
     */
    public static PreparedStatement UPDATE_COMMAND() throws SQLException {
        return DBController.getInstance().getConnection().prepareCall(
                "CALL update_worker(?::text, ?::double precision, ?::double precision, ?::integer, ?::timestamp, ?::timestamp, ?::status, ?::bigint, ?::color, ?::country, ?::text, ?::integer)"
        );
    }
}
