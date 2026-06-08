import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseEngine {

    // The exact GPS coordinates to your specific database vault
    private static final String URL = "jdbc:mysql://localhost:3306/cinefiles_v3";

    // The Master Keys
    private static final String USER = "root";
    private static final String PASSWORD = "SasmitaDash@1407";

    // The Bridge Method
    public static Connection connect() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[SYSTEM] Database Bridge Connected Successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println("[CRITICAL] Database Bridge Collapsed!");
            System.err.println("Error Details: " + e.getMessage());
            return null;
        }
    }
}
