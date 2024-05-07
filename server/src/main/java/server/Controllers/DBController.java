package server.Controllers;

import common.Controllers.PropertiesFilesController;
import server.utils.ServerLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton Class to work with database
 */
public class DBController {
    /**
     * Name of properties file with login data for connecting to database
     */
    private static final String LOGIN_DATA_FILE = "db.properties";
    /**
     * Name of database drive
     */
    private static final String DATABASE_DRIVER = "org.postgresql.Driver";
    /**
     * Session in database
     */
    private Connection connection;

    private static DBController CONTROLLER = null;
    private DBController() {};

    public static synchronized DBController getInstance(){
        if(CONTROLLER == null){
            CONTROLLER = new DBController();
        }
        return CONTROLLER;
    }

    /**
     * Method to connect to database
     * @throws SQLException If an error occurred while working with database
     * @throws ClassNotFoundException If driver for database was not found
     * @throws IOException If an error occurred while reading login data from properties file
     */
    public void connect() throws SQLException, ClassNotFoundException, IOException {
        Class.forName(DATABASE_DRIVER);
        Properties loginProperties = new PropertiesFilesController().readProperties(LOGIN_DATA_FILE);
        this.connection = DriverManager.
                getConnection(loginProperties.getProperty("jdbcUrl"),
                              loginProperties.getProperty("username"),
                              loginProperties.getProperty("password"));
    }

    /**
     * Method to get object with database session
     * @return
     */
    public Connection getConnection(){
        return this.connection;
    }

    /**
     * Method to close session with database
     * @throws SQLException
     */
    public void close() throws SQLException {
        this.connection.close();
        ServerLogger.getInstance().info("Database was disconnected");
    }
}