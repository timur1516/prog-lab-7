package server.DB;

import common.Collection.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.PriorityQueue;

/**
 * Class which contains methods to execute queries from {@link DBQueries} with given parameters and receive result if it exists
 */
public class DBQueriesExecutors {
    public static PriorityQueue<Worker> getCollectionExecutor() throws SQLException {
        PreparedStatement get_collection_query = DBQueries.GET_COLLECTION();
        ResultSet resultSet = get_collection_query.executeQuery();

        PriorityQueue<Worker> data = new PriorityQueue<>();

        while(resultSet.next()){
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            double x = resultSet.getDouble("x");
            double y = resultSet.getDouble("y");
            ZonedDateTime creationDate = resultSet.getObject("creationDate", OffsetDateTime.class).toZonedDateTime();
            Integer salary = resultSet.getInt("salary");
            LocalDateTime startDate = resultSet.getTimestamp("startDate").toLocalDateTime();
            LocalDateTime endDate = resultSet.getTimestamp("endDate") == null ? null : resultSet.getTimestamp("endDate").toLocalDateTime();
            Status status = Status.valueOf(resultSet.getString("status"));
            Long height = resultSet.getLong("height");
            Color eyeColor = resultSet.getString("eyeColor") == null ? null : Color.valueOf(resultSet.getString("eyeColor"));
            Country nationality = resultSet.getString("nationality") == null ? null : Country.valueOf(resultSet.getString("nationality"));

            Coordinates coordinates = new Coordinates(x, y);
            Person person = height == 0 ? null : new Person(height, eyeColor, nationality);
            Worker worker = new Worker(id, name, coordinates, creationDate, salary, startDate, endDate, status, person);
            data.add(worker);
        }

        resultSet.close();
        get_collection_query.close();

        return data;
    }

    public static boolean checkUsername(String username) throws SQLException {
        PreparedStatement check_username_query = DBQueries.CHECK_USERNAME();

        check_username_query.setString(1, username);

        ResultSet resultSet = check_username_query.executeQuery();
        resultSet.next();

        boolean result = resultSet.getBoolean("exists");

        resultSet.close();
        check_username_query.close();
        return result;
    }

    public static void addUser(String username, String password, String salt) throws SQLException {
        PreparedStatement add_user_query = DBQueries.ADD_USER();

        add_user_query.setString(1,username);
        add_user_query.setString(2, password);
        add_user_query.setString(3, salt);

        add_user_query.executeUpdate();
        add_user_query.close();
    }

    public static boolean logInUserExecutor(String username, String password) throws SQLException {
        PreparedStatement login_query = DBQueries.LOG_IN_USER();

        login_query.setString(1, username);
        login_query.setString(2, password);

        ResultSet resultSet = login_query.executeQuery();
        resultSet.next();

        boolean result = resultSet.getBoolean("exists");

        resultSet.close();
        login_query.close();

        return result;
    }

    public static String getSaltExecutor(String username) throws SQLException {
        PreparedStatement get_salt_query = DBQueries.GET_SALT();

        get_salt_query.setString(1, username);

        ResultSet resultSet = get_salt_query.executeQuery();
        resultSet.next();

        String salt = resultSet.getString(1);

        resultSet.close();
        get_salt_query.close();
        return salt;
    }

    public static boolean checkAccessExecutor(long id, String username) throws SQLException {
        PreparedStatement check_access_query = DBQueries.CHECK_ACCESS();

        check_access_query.setLong(1, id);
        check_access_query.setString(2, username);

        ResultSet resultSet = check_access_query.executeQuery();

        resultSet.next();
        boolean result = resultSet.getBoolean(1);

        resultSet.close();
        check_access_query.close();
        return result;
    }

    public static void clearCommandExecutor(String username) throws SQLException {
        PreparedStatement clear_command_query = DBQueries.CLEAR_COMMAND();

        clear_command_query.setString(1, username);

        clear_command_query.execute();
        clear_command_query.close();
    }

    public static void addCommandExecutor(Worker newWorker, String username) throws SQLException {
        PreparedStatement add_command_query = DBQueries.ADD_COMMAND();

        loadWorkerToStatement(newWorker, add_command_query);
        add_command_query.setString(11, username);

        add_command_query.execute();
        add_command_query.close();
    }

    public static void removeByIdCommandExecutor(long id, String username) throws SQLException {
        PreparedStatement remove_by_id_query = DBQueries.REMOVE_BY_ID_COMMAND();

        remove_by_id_query.setString(1, username);
        remove_by_id_query.setLong(2, id);

        remove_by_id_query.execute();
        remove_by_id_query.close();
    }

    public static void removeFirstCommandExecutor(String username) throws SQLException {
        PreparedStatement remove_first_command_query = DBQueries.REMOVE_FIRST_COMMAND();

        remove_first_command_query.setString(1, username);

        remove_first_command_query.execute();
        remove_first_command_query.close();
    }

    public static void removeGreaterCommandExecutor(Worker worker, String username) throws SQLException {
        PreparedStatement remove_greater_command_query = DBQueries.REMOVE_GREATER_COMMAND();

        remove_greater_command_query.setString(1, username);
        remove_greater_command_query.setString(2, worker.getName());

        remove_greater_command_query.execute();
        remove_greater_command_query.close();
    }

    public static void removeLowerCommandExecutor(Worker worker, String  username) throws SQLException {
        PreparedStatement remove_lower_command_query = DBQueries.REMOVE_LOWER_COMMAND();

        remove_lower_command_query.setString(1, username);
        remove_lower_command_query.setString(2, worker.getName());

        remove_lower_command_query.execute();
        remove_lower_command_query.close();
    }

    public static void updateCommandExecutor(Worker newWorker, long id, String username) throws SQLException {
        PreparedStatement update_command_query = DBQueries.UPDATE_COMMAND();

        loadWorkerToStatement(newWorker, update_command_query);
        update_command_query.setString(11, username);
        update_command_query.setLong(12, id);

        update_command_query.execute();
        update_command_query.close();
    }

    private static void loadWorkerToStatement(Worker newWorker, PreparedStatement statement) throws SQLException {
        String name = newWorker.getName();
        double x = newWorker.getCoordinates().getX();
        double y = newWorker.getCoordinates().getY();
        Integer salary = newWorker.getSalary();
        Timestamp startDate = Timestamp.valueOf(newWorker.getStartDate());
        Timestamp endDate = newWorker.getEndDate() == null ? null : Timestamp.valueOf(newWorker.getEndDate());
        String status = String.valueOf(newWorker.getStatus());
        Long height = null;
        String eyeColor = null;
        String nationality = null;
        if(newWorker.getPerson() != null) {
            height = newWorker.getPerson().getHeight();
            eyeColor = newWorker.getPerson().getEyeColor() == null ? null : String.valueOf(newWorker.getPerson().getEyeColor());
            nationality = newWorker.getPerson().getNationality() == null ? null : String.valueOf(newWorker.getPerson().getNationality());
        }

        statement.setString(1, name);
        statement.setDouble(2, x);
        statement.setDouble(3, y);
        statement.setInt(4, salary);
        statement.setTimestamp(5, startDate);
        statement.setTimestamp(6, endDate);
        statement.setString(7, status);
        statement.setObject(8, height, Types.BIGINT);
        statement.setString(9, eyeColor);
        statement.setString(10, nationality);
    }
}
