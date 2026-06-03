import java.util.ArrayList;

public class User {
    // 1. Private Variables (The Vault - Encapsulation)
    private String username;
    private String password;

    // Here is your DSA repetition: An ArrayList living INSIDE the User object.
    // This is called "Composition". The User owns their own personal garage of movies.
    private ArrayList<String> watchlist;

    // 2. Constructor (The Assembly Line)
    public User(String inputUsername, String inputPassword) {
        this.username = inputUsername;
        this.password = inputPassword;

        // CRITICAL: We must physically build the empty garage when the user is created,
        // otherwise the program crashes with a NullPointerException when they add a movie.
        this.watchlist = new ArrayList<>();
    }

    // 3. Action Methods (What the user can DO)
    public void addToWatchlist(String movieTitle) {
        watchlist.add(movieTitle);
        System.out.println(movieTitle + " has been added to " + username + "'s watchlist.");
    }

    public void displayProfile() {
        System.out.println("\n--- PROFILE: " + username + " ---");
        System.out.println("Watchlist Size: " + watchlist.size() + " movies");

        if (watchlist.size() == 0) {
            System.out.println("  [Watchlist is empty]");
        } else {
            // Standard O(N) single-pass loop! Just like the Arena.
            for (int i = 0; i < watchlist.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + watchlist.get(i));
            }
        }
        System.out.println("-------------------------");
    }

    // 4. Getters (Secure Windows)
    public String getUsername() {
        return username;
    }

    // We need this so the engine can check if they typed the right password!
    public boolean checkPassword(String input) {
        // We use .equals() for Strings in Java, not ==
        return this.password.equals(input);
    }
}
