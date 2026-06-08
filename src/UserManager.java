import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.util.Base64;

public class UserManager {

    // --- THE SECURE HASHING ALGORITHM (SHA-256) JAVA'S built-in---
    private static String hashPassword(String plainText) {
        try {
            // 1. Summon the Java meat grinder
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 2. Throw the password in and turn the grinder
            byte[] hashBytes = digest.digest(plainText.getBytes("UTF-8"));

            // 3. Convert the raw bytes into a readable string to save in MySQL
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            System.err.println("[CRITICAL] Hashing Engine Failed.");
            return null;
        }
    }

    // The method to push a new user across the bridge
    // Update the method to accept a password
    public static void registerUser(String username, String email, String plainTextPassword) {

        // 1. Hash the password IMMEDIATELY
        String securedHash = hashPassword(plainTextPassword);

        // 2. The Blueprint now includes the password_hash column
        String sql = "INSERT INTO Users (username, email, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, securedHash); // We send the ground beef, NOT the steak!

            pstmt.executeUpdate();
            System.out.println("[SUCCESS] New Cinephile Registered securely!");

        } catch (SQLException e) {
            System.err.println("[FAILED] Could not register user: " + e.getMessage());
        }
    }

    // The method to check if a user exists and the password matches
    public static boolean verifyLogin(String inputUsername, String inputPassword) {

        // 1. The Blueprint (We only need to fetch the saved hash)
        String sql = "SELECT password_hash FROM Users WHERE username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 2. Put the typed username into the envelope
            pstmt.setString(1, inputUsername);

            // 3. Execute the READ command
            java.sql.ResultSet rs = pstmt.executeQuery();

            // 4. Did we find the user?
            if (rs.next()) {
                // Yes! Grab the saved hash from the vault
                String savedHash = rs.getString("password_hash");

                // 5. Grind the new input password into a hash
                String attemptHash = hashPassword(inputPassword);

                // 6. Compare the two hashes
                if (savedHash.equals(attemptHash)) {
                    System.out.println("[SUCCESS] Identity verified. Access Granted.");
                    return true;
                } else {
                    System.out.println("[FAILED] Incorrect password.");
                    return false;
                }
            } else {
                System.out.println("[FAILED] User not found in the system.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Login query failed: " + e.getMessage());
            return false;
        }
    }

    // --- THE TRANSLATOR: Get User ID ---
    public static int getUserId(String username) {
        String sql = "SELECT user_id FROM Users WHERE username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id"); // Found the ID!
            }
        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not fetch User ID: " + e.getMessage());
        }
        return -1; // -1 means the user was not found
    }
}

