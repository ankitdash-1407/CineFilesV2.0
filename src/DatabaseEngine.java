import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseEngine {

    // Read variables dynamically from the system environment
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // The Bridge Method
    public static Connection connect() {
        // Safety guard: Check if the variables actually exist in IntelliJ's memory
        if (URL == null || USER == null || PASSWORD == null) {
            System.err.println("[CRITICAL] Database Bridge Setup Failed!");
            System.err.println("Error Details: Environment variables (DB_URL, DB_USER, or DB_PASSWORD) are missing.");
            return null;
        }

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
