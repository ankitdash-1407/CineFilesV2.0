import java.util.ArrayList;

public class Thread {
    // 1. Private Variables (The Vault)
    private String author;
    private String movieTitle;
    private String topic;
    private ArrayList<String> comments; // The dynamic list of replies

    // 2. Constructor (The Assembly Line)
    public Thread(String inputAuthor, String inputMovie, String inputTopic) {
        this.author = inputAuthor;
        this.movieTitle = inputMovie;
        this.topic = inputTopic;

        // We must build the empty list when the Thread is created,
        // otherwise it will crash when someone tries to add the first comment!
        this.comments = new ArrayList<>();
    }

    // 3. Action Methods (Things this object can DO)

    // Allows someone to drop a reply into this specific thread
    public void addComment(String newComment) {
        comments.add(newComment);
    }

    // Prints out the entire debate to the console
    public void displayThread() {
        System.out.println("\n====================================");
        System.out.println("MOVIE: " + movieTitle);
        System.out.println("DEBATE: " + topic + " (Started by: " + author + ")");
        System.out.println("====================================");

        if (comments.size() == 0) {
            System.out.println("  [No replies yet. Be the first to comment!]");
        } else {
            for (int i = 0; i < comments.size(); i++) {
                System.out.println("  -> " + comments.get(i));
            }
        }
        System.out.println("====================================\n");
    }

    // 4. Getters
    public String getMovieTitle() {
        return movieTitle;
    }

    public String getTopic() {
        return topic;
    }
}
