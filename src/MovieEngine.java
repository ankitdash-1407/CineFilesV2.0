import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MovieEngine {

    // --- TOOL 1: THE LOAD MODULE (Movies) ---
    public static void loadDatabase(ArrayList<Movie> database) {
        try {
            File movieFile = new File("movie_database.txt");
            Scanner fileScanner = new Scanner(movieFile);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    database.add(new Movie(parts[0].trim(), parts[1].trim(), Double.parseDouble(parts[2].trim())));
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("[SYSTEM] No existing movie database found. Starting fresh.");
        }
    }

    // --- TOOL 2: THE SAVE MODULE (Movies) ---
    public static void saveDatabase(ArrayList<Movie> database) {
        try {
            File movieFile = new File("movie_database.txt");
            FileWriter writer = new FileWriter(movieFile, false);
            for (int i = 0; i < database.size(); i++) {
                Movie m = database.get(i);
                writer.write(m.getTitle() + "|" + m.getGenre() + "|" + m.getRating() + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("[SYSTEM] CRITICAL ERROR: Could not save database.");
        }
    }

    // --- THE MAYOR: MAIN METHOD ---
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);

        // 1. City Infrastructure
        ArrayList<Movie> movieDatabase = new ArrayList<>();
        ArrayList<User> communityBoard = new ArrayList<>();
        ArrayList<Thread> discussionBoard = new ArrayList<>();

        // Let's hardcode a few users so you don't have to create them every time you test
        communityBoard.add(new User("Ankit", "admin1407"));
        communityBoard.add(new User("Bismita", "adminkibiwi"));

        loadDatabase(movieDatabase);

        System.out.println("\n----_CineFiles Platform v3.0 Starting_----");
        boolean engineRunning = true;

        // --- OUTER LOOP: THE AUTH GATE ---
        while (engineRunning) {
            System.out.println("\n--- WELCOME GUEST ---");
            System.out.println("1. Login");
            System.out.println("2. Create Account");
            System.out.println("3. Shut Down Server");
            System.out.print("Command: ");

            String authChoice = keyboard.nextLine();
            User currentUser = null; // This variable tracks who is currently holding the controller

            if (authChoice.equals("1")) {
                System.out.print("Username: ");
                String typedUser = keyboard.nextLine();
                System.out.print("Password: ");
                String typedPass = keyboard.nextLine();

                // The O(N) Search Pattern! (Finding the user)
                for (int i = 0; i < communityBoard.size(); i++) {
                    User tempUser = communityBoard.get(i);
                    // Match the username AND check the password using the Object's secure method
                    if (tempUser.getUsername().equals(typedUser) && tempUser.checkPassword(typedPass)) {
                        currentUser = tempUser; // Hand them the controller!
                        break; // Stop searching
                    }
                }

                if (currentUser == null) {
                    System.out.println("[ERROR] Invalid credentials. Access Denied.");
                    continue; // Kick them back to the Auth Gate
                }

            } else if (authChoice.equals("2")) {
                System.out.print("Choose a Username: ");
                String newU = keyboard.nextLine();
                System.out.print("Choose a Password: ");
                String newP = keyboard.nextLine();

                communityBoard.add(new User(newU, newP));
                System.out.println("[SUCCESS] Account created. Please login.");
                continue; // Kick them back to the Auth Gate to actually log in

            } else if (authChoice.equals("3")) {
                System.out.println("Saving databases... Shutting down.");
                saveDatabase(movieDatabase);
                engineRunning = false;
                continue;
            } else {
                System.out.println("Invalid command.");
                continue;
            }

            // --- INNER LOOP: THE MAIN MENU (Only reaches here if currentUser is NOT null) ---
            boolean isLoggedIn = true;
            System.out.println("\n>>> ACCESS GRANTED. Welcome to Cinefiles, " + currentUser.getUsername() + " <<<");

            while (isLoggedIn) {
                System.out.println("\n--- MAIN MENU ---");
                System.out.println("1. Add a Movie to Global DB");
                System.out.println("2. Search Movies");
                System.out.println("3. Add a Movie to Personal Watchlist");
                System.out.println("4. View My Profile");
                System.out.println("5. Start a Debate (Create Thread)");
                System.out.println("6. View Community Board (Read/Reply)");
                System.out.println("7. Logout");
                System.out.print("Awaiting command: ");

                String mainChoice = keyboard.nextLine();

                if (mainChoice.equals("1")) {
                    System.out.print("Type the title: ");
                    String newTitle = keyboard.nextLine();
                    System.out.print("Type the genre: ");
                    String newGenre = keyboard.nextLine();
                    System.out.print("Type the rating (out of 10): ");
                    double newRating = keyboard.nextDouble();
                    keyboard.nextLine(); // Clear the invisible newline

                    movieDatabase.add(new Movie(newTitle, newGenre, newRating));
                    System.out.println("DATABASE UPDATED: " + newTitle + " added.");

                } else if (mainChoice.equals("2")) {
                    System.out.print("Enter the Title or Genre to search: ");
                    String searchQ = keyboard.nextLine();

                    System.out.print("Minimum rating preference (Type a number or press ENTER to skip): ");
                    String ratingPrefstr = keyboard.nextLine().trim();

                    double ratingPrefd = 0.0;
                    if (!ratingPrefstr.isEmpty()){
                        ratingPrefd = Double.parseDouble(ratingPrefstr);
                    }

                    boolean found = false;
                    System.out.println("\n--- Search Results ---");

                    // Note: We are searching the 'movieDatabase' list here!
                    for (int i = 0; i < movieDatabase.size(); i++) {
                        Movie currentMovie = movieDatabase.get(i);
                        boolean matchesText = searchQ.equalsIgnoreCase(currentMovie.getTitle()) || searchQ.equalsIgnoreCase(currentMovie.getGenre());
                        boolean meetsRating = currentMovie.getRating() >= ratingPrefd;

                        if (matchesText && meetsRating) {
                            System.out.println(currentMovie.getTitle() + " | " + currentMovie.getGenre() + " | Rating: " + currentMovie.getRating());
                            found = true;
                        }
                    }
                    if (!found) {
                        System.out.println("No movies found matching those criteria.");
                    }
                } else if (mainChoice.equals("3")) {
                    System.out.print("Enter the title to add to your watchlist: ");
                    String watchTitle = keyboard.nextLine();
                    // Here we use the Object's method!
                    currentUser.addToWatchlist(watchTitle);
                } else if (mainChoice.equals("4")) {
                    // Let the Object print its own data!
                    currentUser.displayProfile();
                } else if (mainChoice.equals("4")) {
                    currentUser.displayProfile();

                    // --- NEW CODE STARTS HERE ---
                } else if (mainChoice.equals("5")) {
                    System.out.println("\n--- START A DEBATE ---");
                    System.out.print("Target Movie: ");
                    String targetMovie = keyboard.nextLine();
                    System.out.print("Your Hot Take (Topic): ");
                    String topic = keyboard.nextLine();

                    // We build the Thread and park it in the discussion board
                    discussionBoard.add(new Thread(currentUser.getUsername(), targetMovie, topic));
                    System.out.println("Thread created successfully!");

                } else if (mainChoice.equals("6")) {
                    System.out.println("\n--- COMMUNITY BOARD ---");
                    if (discussionBoard.size() == 0) {
                        System.out.println("No threads yet. Be the first to start one!");
                    } else {
                        // 1. Print all available threads (O(N) Search)
                        for (int i = 0; i < discussionBoard.size(); i++) {
                            System.out.println((i + 1) + ". " + discussionBoard.get(i).getMovieTitle() + " - " + discussionBoard.get(i).getTopic());
                        }

                        System.out.print("\nEnter Thread Number to view (or 0 to cancel): ");
                        int threadChoice = keyboard.nextInt();
                        keyboard.nextLine(); // Clear the invisible newline

                        if (threadChoice > 0 && threadChoice <= discussionBoard.size()) {
                            // 2. Grab the specific thread object they chose
                            Thread selected = discussionBoard.get(threadChoice - 1);

                            // 3. Tell the object to print itself!
                            selected.displayThread();

                            // 4. The Reply Logic
                            System.out.print("Drop a reply? (y/n): ");
                            String reply = keyboard.nextLine();
                            if (reply.equalsIgnoreCase("y")) {
                                System.out.print("Your comment: ");
                                String commentText = keyboard.nextLine();

                                // Notice how we automatically tag the current user's name onto their comment!
                                selected.addComment(currentUser.getUsername() + ": " + commentText);
                                System.out.println("Reply posted.");
                            }
                        }
                    }
                } else if (mainChoice.equals("7")) {
                    System.out.println("Logging out " + currentUser.getUsername() + "...");
                    currentUser = null;
                    isLoggedIn = false;
                } else {
                    System.out.println("Invalid command. Please select 1-7.");
                }
            }
        }

        keyboard.close();
    }
}