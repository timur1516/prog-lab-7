package server.Controllers;

import common.Controllers.PropertiesFilesController;
import server.ServerLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBController {
    private static final String LOGIN_DATA_FILE = "db.properties";
    private Connection connection;
    private static DBController CONTROLLER = null;
    private DBController() {};

    public static DBController getInstance(){
        if(CONTROLLER == null){
            CONTROLLER = new DBController();
        }
        return CONTROLLER;
    }

    public void connect() throws SQLException, ClassNotFoundException, IOException {
        Class.forName("org.postgresql.Driver");
        Properties loginProperties = new PropertiesFilesController().readProperties(LOGIN_DATA_FILE);
        this.connection = DriverManager.
                getConnection(loginProperties.getProperty("jdbcUrl"),
                              loginProperties.getProperty("username"),
                              loginProperties.getProperty("password"));
    }

    public Connection getConnection(){
        return this.connection;
    }

    public void close() throws SQLException {
        this.connection.close();
        ServerLogger.getInstace().info("Database was disconnected");
    }
}