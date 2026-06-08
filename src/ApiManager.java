import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiManager {

    // You will need to get a free API key from omdbapi.com
    private static final String API_KEY = "6b157274";

    public static void fetchAndCacheMovie(String title) {
        System.out.println("[🌐 CACHE MISS] Movie not found locally. Connecting to OMDb API...");

        // 1. Format the title for a URL (e.g., "The Dark Knight" -> "The+Dark+Knight")
        String formattedTitle = title.replace(" ", "+");
        String url = "http://www.omdbapi.com/?t=" + formattedTitle + "&apikey=" + API_KEY;

        try {
            // 2. Build the Internet Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // 3. Send the request and wait for the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();

            // 4. Did the API find the movie?
            if (jsonResponse.contains("\"Response\":\"False\"")) {
                System.out.println("[FAILED] Movie does not exist on the internet.");
                return;
            }

            // 5. Parse the data out of the JSON response (Basic String extraction)
            String fetchedTitle = extractJsonValue(jsonResponse, "Title");
            String fetchedGenre = extractJsonValue(jsonResponse, "Genre");
            String imdbRatingStr = extractJsonValue(jsonResponse, "imdbRating");

            // Convert rating to a decimal (Default to 0.0 if "N/A")
            double rating = imdbRatingStr.equals("N/A") ? 0.0 : Double.parseDouble(imdbRatingStr);

            System.out.println("\n[📡 LOADED FROM INTERNET - 500ms]");
            System.out.println("🎬 Title:  " + fetchedTitle);
            System.out.println("🎭 Genre:  " + fetchedGenre);
            System.out.println("⭐ Rating: " + rating + "/10");

            // 6. THE CRITICAL STEP: Save it to our local MySQL Vault for next time!
            MovieManager.addMovie(fetchedTitle, fetchedGenre, rating);
            System.out.println("[SYSTEM] Movie successfully cached in local database.");

        } catch (Exception e) {
            System.err.println("[CRITICAL] Internet connection failed: " + e.getMessage());
        }
    }

    // A helper method to extract values from JSON without needing external libraries
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey) + searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (startIndex < searchKey.length() || endIndex == -1) return "N/A";
        return json.substring(startIndex, endIndex);
    }
}
