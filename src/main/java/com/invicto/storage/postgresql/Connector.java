package com.invicto.storage.postgresql;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connector {
    private static final Logger logger = Logger.getLogger(Connector.class.getName());
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public Connector(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void getConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            logger.info("Connection OK");
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Connection failed", exception);
        }
    }

    public ResultSet executeQuery(String command) {
        ResultSet resultSet = null;
        try (Statement statement = connection.createStatement()) {
            resultSet = statement.executeQuery(command);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "SQLException", exception);
        }
        return resultSet;
    }

    public void executeUpdate(String command) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(command);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "SQLException", exception);
        }
    }
}
