import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WatchlistManager {

    public static void addMovieToWatchlist(String username, String movieTitle) {

        // 1. Translate the Strings into IDs!
        int userId = UserManager.getUserId(username);
        int movieId = MovieManager.getMovieId(movieTitle);

        // 2. Safety Check: Did we actually find them?
        if (userId == -1 || movieId == -1) {
            System.out.println("[FAILED] User or Movie not found in the system.");
            return;
        }

        // 3. The Blueprint for the Junction Table
        String sql = "INSERT INTO Watchlists (user_id, movie_id) VALUES (?, ?)";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);

            pstmt.executeUpdate();
            System.out.println("[SUCCESS] '" + movieTitle + "' added to your personal watchlist!");

        } catch (SQLException e) {
            // Here is where our Composite Primary Key shines!
            // If the combination already exists, MySQL throws an error. We catch it gracefully.
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("[SYSTEM] This movie is already on your watchlist.");
            } else {
                System.err.println("[FAILED] Database error: " + e.getMessage());
            }
        }
    }

    // --- THE VIEWER: Show a user's complete watchlist ---
    public static void viewWatchlist(String username) {

        // THE JOIN: Stitch the Users, Watchlists, and Movies tables together
        String sql = "SELECT m.title, m.attribute, m.rating " +
                "FROM Movies m " +
                "JOIN Watchlists w ON m.movie_id = w.movie_id " +
                "JOIN Users u ON w.user_id = u.user_id " +
                "WHERE u.username = ?";

        try (Connection conn = DatabaseEngine.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Put the username into the envelope
            pstmt.setString(1, username);

            // Execute the Read command
            java.sql.ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- " + username.toUpperCase() + "'S WATCHLIST ---");
            boolean hasMovies = false;

            // Loop through all the rows we found
            while (rs.next()) {
                hasMovies = true;
                String title = rs.getString("title");
                String genre = rs.getString("attribute");
                double rating = rs.getDouble("rating");

                System.out.println("🎬 " + title + " | " + genre + " | ⭐ " + rating);
            }

            if (!hasMovies) {
                System.out.println("Your watchlist is completely empty.");
            }
            System.out.println("-------------------------");

        } catch (SQLException e) {
            System.err.println("[CRITICAL] Could not load watchlist: " + e.getMessage());
        }
    }
}
