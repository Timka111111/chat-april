package kz.timka.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {
    private Connection connection;
    private Statement statement;

    public Statement getStatement() {
        return statement;
    }

    public DbConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:chat.db");
            this.statement = connection.createStatement();
        }catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Невозможно подключаться к базе данных");
        }
    }

    public void disconnect() {
        if(statement != null) {
            try {
                statement.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(connection != null) {
            try {
                connection.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

}
