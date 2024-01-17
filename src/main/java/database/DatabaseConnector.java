package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    public static Connection connect() throws SQLException {
        String url = "jdbc:oracle:thin:@149.156.138.232:1521:orcl";
        String user = "sbd28";
        String password = "sbd28";

        return DriverManager.getConnection(url, user, password);
    }
}
