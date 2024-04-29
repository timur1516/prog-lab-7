package server.Controllers;

import server.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBController {
    private static String jdbcUrl = "jdbc:postgresql://localhost:5432/postgres";
    private static String username = "postgres";
    private static String password = "29082006";
    private Connection connection;
    private static DBController CONTROLLER = null;
    private DBController() {};

    public static DBController getInstance(){
        if(CONTROLLER == null){
            CONTROLLER = new DBController();
        }
        return CONTROLLER;
    }

    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        this.connection = DriverManager.getConnection(jdbcUrl, username, password);
    }
    public Connection getConnection(){
        return this.connection;
    }
    public void close() throws SQLException {
        this.connection.close();
        Main.logger.info("Database was disconnected");
    }
}