import java.util.Scanner;

public class MovieEngine {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // 1. THE GATEKEEPER
        // We use a String to hold their name. If it's null, they are locked out.
        String currentUser = null;

        // ==========================================
        // OUTER LOOP: AUTHENTICATION
        // ==========================================
        while (currentUser == null) {
            System.out.println("=================================");
            System.out.println("   Welcome to Cinefiles v3.0");
            System.out.println("=================================");
            System.out.println("1. Register a new account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose your protocol: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear the invisible Enter key

            switch (choice) {
                case 1:
                    System.out.print("Enter your new username: ");
                    String inputName = scanner.nextLine();
                    System.out.print("Enter your email address: ");
                    String inputEmail = scanner.nextLine();
                    System.out.print("Enter your password: ");
                    String inputPassword = scanner.nextLine();

                    UserManager.registerUser(inputName, inputEmail, inputPassword);
                    break;

                case 2:
                    System.out.print("Enter your username to login: ");
                    String loginName = scanner.nextLine();
                    System.out.print("Enter your password: ");
                    String password = scanner.nextLine();


                    // If it returns true, we assign the name and break the outer loop.
                    if (UserManager.verifyLogin(loginName,password)) {
                        currentUser = loginName;
                    } else {
                        System.out.println("Login Failed. User not found.");
                    }
                    break;

                case 3:
                    System.out.println("Shutting down...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input.");
            }
        }

        // ==========================================
        // INNER LOOP: THE MAIN MENU (ACCESS GRANTED)
        // ==========================================
        boolean isLoggedIn = true;
        System.out.println("\n>>> ACCESS GRANTED. Welcome to Cinefiles, " + currentUser + " <<<");

        while (isLoggedIn) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Search for a Movie");
            System.out.println("2. View Personal Watchlist");
            System.out.println("3. Logout");
            System.out.print("Awaiting command: ");

            String mainChoice = scanner.nextLine();

            if (mainChoice.equals("1")) {

                // --- THE SMART SEARCH ---
                System.out.print("Enter the title of the movie you want to find: ");
                String searchTitle = scanner.nextLine();

                // THE READ-THROUGH CACHE LOGIC
                // 1. Check local Vault first
                boolean foundLocally = MovieManager.searchLocalDatabase(searchTitle);

                // 2. If not found, fetch from Internet and cache it
                if (!foundLocally) {
                    ApiManager.fetchAndCacheMovie(searchTitle);
                }
                // --- UX: Prompt to add to watchlist right after searching ---
                System.out.println("-------------------------");
                System.out.print("Do you want to add '" + searchTitle + "' to your watchlist? (y/n): ");
                String addChoice = scanner.nextLine();

                if (addChoice.equalsIgnoreCase("y")) {
                    WatchlistManager.addMovieToWatchlist(currentUser, searchTitle);
                }

            } else if (mainChoice.equals("2")) {
                // View Watchlist
                WatchlistManager.viewWatchlist(currentUser);
                System.out.println("_______________________________");

            } else if (mainChoice.equals("3")) {
                System.out.println("Logging out " + currentUser + "...");
                currentUser = null;
                isLoggedIn = false;

            } else {
                System.out.println("Invalid command.");
            }
        }

        scanner.close();
    }
}