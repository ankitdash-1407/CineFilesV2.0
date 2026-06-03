public class Movie {
    // 1. Private Variables (The Vault)
    private String title;
    private String genre;
    private double rating;

    // 2. Constructor (The Assembly Line)
    public Movie(String inputTitle, String inputGenre, double inputRating) {
        this.title = inputTitle;
        this.genre = inputGenre;
        this.rating = inputRating;
    }

    // 3. Getters (The Secure Windows)
    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public double getRating() {
        return rating;
    }
}
