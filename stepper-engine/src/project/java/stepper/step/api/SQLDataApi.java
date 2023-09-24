package project.java.stepper.step.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDataApi {
    public final static String JBC_URL = "jdbc:mysql://localhost:3306/amitcheck";
    public final static String USERNAME = "root";
    public final static String PASSWORD = "amitrol120";
    public static Connection CONNECTION;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            CONNECTION = DriverManager.getConnection(JBC_URL, USERNAME, PASSWORD);
            System.out.println(
                    "Connection Established successfully");
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
