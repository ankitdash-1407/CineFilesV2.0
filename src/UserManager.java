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
    public static boolean registerUser(String username, String email, String plainTextPassword) {

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
            return true;

        } catch (SQLException e) {
            return false;
        }
    }

    // The method to check if a user exists and the password matches
    // 1. The Promise: Change 'boolean' to 'User'
    public static User verifyLogin(String inputUsername, String inputPassword) {

        // 2. The Updated Blueprint: Ask for ALL the groceries we need for the bucket!
        String sql = "SELECT user_id, username, email, password_hash FROM Users WHERE username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, inputUsername);
            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String savedHash = rs.getString("password_hash");
                String attemptHash = hashPassword(inputPassword);

                if (savedHash.equals(attemptHash)) {
                    // --- SUCCESS! ---
                    // Step A: Extract the data
                    int fetchedId = rs.getInt("user_id");
                    String fetchedUsername = rs.getString("username");
                    String fetchedEmail = rs.getString("email");

                    // Step B: Pack the User bucket (Make sure your User.java constructor matches this!)
                    User loggedInUserBucket = new User(fetchedId, fetchedUsername, fetchedEmail);

                    // Step C: Hand the bucket back to the Engine
                    return loggedInUserBucket;
                }
            }
        } catch (SQLException e) {
            // Keep error messages clean, but no UI messages!
            System.err.println("[CRITICAL] Login query failed: " + e.getMessage());
        }

        // 3. The Catch-All: If password fails, user isn't found, or database crashes, return empty hand.
        return null;
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

