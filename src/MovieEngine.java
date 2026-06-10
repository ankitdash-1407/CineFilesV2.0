import java.util.Scanner;
import java.util.List;

public class MovieEngine {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        User currentUser = null; // 1. THE GATEKEEPER

        // ==========================================
        // THE MASTER LOOP: Keeps the app alive forever
        // ==========================================
        while (true) {

            // --- VIEW 1: AUTHENTICATION ---
            while (currentUser == null) {
                System.out.println("=================================");
                System.out.println("   Welcome to Cinefiles v4.0");
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

                        User loggedInUserBucket = UserManager.verifyLogin(loginName, password);

                        if (loggedInUserBucket != null) {
                            System.out.println("Login Successful! Welcome, " + loggedInUserBucket.getUsername());
                            currentUser = loggedInUserBucket;
                        } else {
                            System.out.println("Login Failed. User not found or incorrect password.");
                        }
                        break;

                    case 3:
                        System.out.println("Shutting down the server...");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid input.");
                }
            }

            // ==========================================
            // VIEW 2: THE MAIN MENU (ACCESS GRANTED)
            // ==========================================
            boolean isLoggedIn = true;
            System.out.println("\n>>> ACCESS GRANTED. Welcome to the Network, " + currentUser.getUsername() + " <<<");

            while (isLoggedIn) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Search for a Movie");
                System.out.println("2. View Personal Watchlist");
                System.out.println("3. Write a Post (V4)"); // <-- New Feature!
                System.out.println("4. View Global Content Feed");
                System.out.println("5. Log Out");
                System.out.print("Awaiting command: ");

                String mainChoice = scanner.nextLine();

                if (mainChoice.equals("1")) {
                    System.out.print("Enter the title of the movie you want to find: ");
                    String searchTitle = scanner.nextLine();

                    Movie foundMovie = MovieManager.searchLocalDatabase(searchTitle);

                    if (foundMovie == null) {
                        foundMovie = ApiManager.fetchAndCacheMovie(searchTitle);
                    }

                    // THE UNIFIED CHECK (No NullPointerExceptions!)
                    if (foundMovie != null) {
                        System.out.println("\n🎬 Title:  " + foundMovie.getTitle());
                        System.out.println("⭐ Rating: " + foundMovie.getRating() + "/10");
                        System.out.println("-------------------------");

                        System.out.print("Do you want to add '" + foundMovie.getTitle() + "' to your watchlist? (y/n): ");
                        String addChoice = scanner.nextLine();

                        if (addChoice.equalsIgnoreCase("y")) {
                            WatchlistManager.addMovieToWatchlist(currentUser, foundMovie.getTitle());
                        }
                    } else {
                        System.out.println("Sorry, we could not find that movie in our vault or on the internet.");
                    }

                } else if (mainChoice.equals("2")) {
                    List<Movie> userWatchlist = WatchlistManager.viewWatchlist(currentUser);

                    System.out.println("\n--- " + currentUser.getUsername().toUpperCase() + "'S WATCHLIST ---");
                    if (userWatchlist.isEmpty()) {
                        System.out.println("Your watchlist is completely empty.");
                    } else {
                        for (Movie m : userWatchlist) {
                            System.out.println("🎬 " + m.getTitle() + " | " + m.getGenre() + " | ⭐ " + m.getRating());
                        }
                    }
                    System.out.println("-------------------------");

                }
                    // --- V4 POST CREATION LOGIC GOES HERE ---
                    else if (mainChoice.equals("3")) {

                        // --- V4 POST CREATION LOGIC ---
                        System.out.println("\n--- COMPOSE NEW POST ---");
                        System.out.print("What's on your mind? ");
                        String postText = scanner.nextLine();

                        System.out.print("Attach an image/video URL? (Leave blank for text-only): ");
                        String mediaUrl = scanner.nextLine();

                        System.out.print("Do you want to tag a movie in this post? (y/n): ");
                        String tagChoice = scanner.nextLine();

                        int targetMovieId = -1; // Default to -1 (No movie attached)

                        if (tagChoice.equalsIgnoreCase("y")) {
                            System.out.print("Enter the movie title to tag: ");
                            String tagTitle = scanner.nextLine();

                            // Check if the movie exists in our vault
                            targetMovieId = MovieManager.getMovieId(tagTitle);

                            if (targetMovieId == -1) {
                                System.out.println("Movie not found in your vault. Posting without a tag...");
                            }
                        }

                        // Call the Manager to write it to the database!
                        boolean success = PostManager.createPost(currentUser, postText, mediaUrl, targetMovieId);

                        if (success) {
                            System.out.println("[SUCCESS] Your post has been published to the network!");
                        }
                        System.out.println("-------------------------");

                } else if (mainChoice.equals("4")) {

                    System.out.println("\n=========================================");
                    System.out.println("          GLOBAL CONTENT FEED            ");
                    System.out.println("=========================================\n");

                    List<Post> globalFeed = PostManager.getGlobalFeed();

                    if (globalFeed.isEmpty()) {
                        System.out.println("The feed is completely empty. Be the first to post!");
                    } else {
                        for (Post p : globalFeed) {
                            // Notice we now print the Post ID and the Like Count!
                            System.out.println("ID: [" + p.getPostId() + "] | 👤 @" + p.getAuthorName() + " | ❤️ " + p.getLikeCount() + " Likes | 🕒 " + p.getCreatedAt());

                            if (p.getMovieTitle() != null) {
                                System.out.println("🏷️  Tagged Movie: " + p.getMovieTitle());
                            }

                            System.out.println("\n   " + p.getText());

                            if (p.getMediaUrl() != null && !p.getMediaUrl().isEmpty()) {
                                System.out.println("   📎 Media: " + p.getMediaUrl());
                            }
                            System.out.println("\n-----------------------------------------");
                        }
                    }

                    // --- THE INTERACTIVE LIKE SYSTEM ---
                    System.out.println();
                    System.out.print("Enter a Post ID to like it, or press [Enter] to return to menu: ");
                    String likeInput = scanner.nextLine();

                    if (!likeInput.trim().isEmpty()) {
                        try {
                            int targetPostId = Integer.parseInt(likeInput);

                            // Call the exact method you just wrote!
                            boolean success = PostManager.likePost(currentUser.getId(), targetPostId);

                            if (success) {
                                System.out.println("[SUCCESS] You liked post #" + targetPostId + "!");
                            } else {
                                System.out.println("[INFO] You have already liked this post.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("[ERROR] Invalid Post ID.");
                        }
                    }
                }

                    else if (mainChoice.equals("5")) {
                    System.out.println("Logging out " + currentUser.getUsername() + "...");
                    currentUser = null; // Drop the bucket!
                    isLoggedIn = false; // Break the inner loop, returning to login screen

                } else {
                    System.out.println("Invalid command.");
                }
            }
        } // End of Master Loop
    }
}