package com.invicto.storage.postgresql;

import java.sql.*;
import java.util.logging.Logger;

public class Connector {
    private static Logger log = Logger.getLogger(Connector.class.getName());
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public Connector(Builder builder) {
        this.url = builder.getUrl();
        this.user = builder.getUser();
        this.password = builder.getPassword();
    }

    public void getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                log.info("Connection OK");
            } else {
                log.info("Connection Failed");
            }
        } catch (ClassNotFoundException | SQLException e) {
            log.info(String.valueOf(e));
        }
    }

    public ResultSet executeStatement(String command) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(command);
            statement.close();
            return resultSet;
        } catch (SQLException ex) {
            return null;
        }
    }

    public static class Builder {
        private String url;
        private String user;
        private String password;

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getUrl() {
            return url;
        }


        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }
}
