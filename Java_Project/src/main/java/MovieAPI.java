import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

// This class provides methods to interact with the OMDb API to search for movies, parse movie data, and insert movies into the database using MovieCRUD.

/**
 * API client for OMDb movie database.
 * Provides methods to search and fetch movie data.
 * 
 * @author Student
 * @version 1.0
 */
public class MovieAPI {
    private static final String API_KEY = "e11fcbbf";
    private static final String BASE_URL = "https://www.omdbapi.com/";
    private static final String SEARCH_URL = BASE_URL + "?apikey=" + API_KEY;
// This method returns a predefined list of movie genres that can be used for searching movies by genre.
    public static String[] getGenres() {
        return new String[]{
            "Action", "Adventure", "Animation", "Biography", "Comedy",
            "Crime", "Documentary", "Drama", "Family", "Fantasy",
            "History", "Horror", "Music", "Mystery", "Romance",
            "Sci-Fi", "Sport", "Thriller", "War", "Western"
        };
    }
/**
 * Searches for a movie by title using the OMDb API and returns the raw JSON response as a string.
 * @param query
 * @return The raw JSON response from the OMDb API for the given movie title query, or null if an error occurs.
 */
    public static String searchMovieJSON(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            URL url = new URL(SEARCH_URL + "&t=" + encodedQuery);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
/**
 * Searches for movies by genre using the OMDb API and returns the raw JSON response as a string.
 * @param genre The genre to search for (e.g., "Action", "Comedy", etc.)
 * @param page The page number for paginated results (OMDb API returns 10 results per page)
 * @return The raw JSON response from the OMDb API for the given genre search query, or null if an error occurs.
 */
public static String searchMoviesByGenreJSON(String genre, int page) {
        try {
            String encodedGenre = URLEncoder.encode(genre, StandardCharsets.UTF_8.toString());
            URL url = new URL(SEARCH_URL + "&s=" + encodedGenre + "&type=movie&page=" + page);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
/**
 *  Parses the raw JSON response from the OMDb API for a single movie search and extracts relevant movie data such as title,
 * genre, duration, plot, and IMDb ID.
 * @param json The raw JSON response from the OMDb API for a single movie search query.
 * @return An array of strings containing the parsed movie data in the order: [imdbId, title, genre, duration, plot], or null if parsing fails.
 */
    public static String[] parseMovieData(String json) {
        if (json == null || !json.contains("\"Title\"")) {
            return null;
        }

        Pattern titlePattern = Pattern.compile("\"Title\":\\s*\"([^\"]+)\"");
        Pattern yearPattern = Pattern.compile("\"Year\":\\s*\"([^\"]+)\"");
        Pattern genrePattern = Pattern.compile("\"Genre\":\\s*\"([^\"]+)\"");
        Pattern runtimePattern = Pattern.compile("\"Runtime\":\\s*\"(\\d+)\"");
        Pattern plotPattern = Pattern.compile("\"Plot\":\\s*\"([^\"]+)\"");
        Pattern imdbPattern = Pattern.compile("\"imdbID\":\\s*\"([^\"]+)\"");

        Matcher titleMatcher = titlePattern.matcher(json);
        Matcher yearMatcher = yearPattern.matcher(json);
        Matcher genreMatcher = genrePattern.matcher(json);
        Matcher runtimeMatcher = runtimePattern.matcher(json);
        Matcher plotMatcher = plotPattern.matcher(json);
        Matcher imdbMatcher = imdbPattern.matcher(json);

        if (titleMatcher.find()) {
            String imdbId = imdbMatcher.find() ? imdbMatcher.group(1) : "0";
            String title = titleMatcher.group(1);
            String year = yearMatcher.find() ? yearMatcher.group(1) : "2024";
            String genre = genreMatcher.find() ? genreMatcher.group(1).trim() : "Drama";
            int duration = 120;
            if (runtimeMatcher.find()) {
                try {
                    String dur = runtimeMatcher.group(1).replace(" min", "").trim();
                    duration = Integer.parseInt(dur);
                } catch (Exception e) {
                    duration = 120;
                }
            }
            String plot = plotMatcher.find() ? plotMatcher.group(1) : "";

            return new String[]{imdbId, title, genre, String.valueOf(duration), plot};
        }
        return null;
    }
/**
 * Searches for movies by genre using the OMDb API and returns an array of movie titles that match the specified genre.
 * @param genre The genre to search for (e.g., "Action", "Comedy", etc.)
 * @return An array of movie titles that match the specified genre, or null if no movies are found or an error occurs.
 */
    public static String[] searchByGenre(String genre) {
        return searchByGenrePages(genre, 1);
    }

    public static String[] searchByGenrePages(String genre, int pages) {
        List<String> results = new ArrayList<>();
        
        for (int page = 1; page <= pages; page++) {
            String json = searchMoviesByGenreJSON(genre, page);
            if (json == null || !json.contains("\"Search\"")) {
                break;
            }

            Pattern titlePattern = Pattern.compile("\"Title\":\\s*\"([^\"]+)\"");
            Matcher titleMatcher = titlePattern.matcher(json);

            while (titleMatcher.find()) {
                results.add(titleMatcher.group(1));
            }
        }

        if (results.isEmpty()) {
            return null;
        }
        return results.toArray(new String[0]);
    }

    /**
     *  Searches for a movie by title using the OMDb API, parses the movie data, and inserts it into the database using the provided MovieCRUD instance.
     * @param crud An instance of MovieCRUD that provides the method for inserting movies into the database.
     * @param movieTitle The title of the movie to search for and add to the database.
     */
    public static void searchAndAddMovie(MovieCRUD crud, String movieTitle) {
        System.out.println("Fetching: " + movieTitle);
        String json = searchMovieJSON(movieTitle);
        if (json != null) {
            System.out.println("Got JSON: " + json.substring(0, Math.min(100, json.length())) + "...");
        }
        String[] data = parseMovieData(json);
        if (data != null) {
            try {
                System.out.println("Inserting: " + data[1] + " | " + data[2] + " | " + data[3]);
                int movieId = (int)(Math.random() * 10000);
                crud.movieInsert(
                    movieId,
                    data[1],
                    data[2],
                    Integer.parseInt(data[3]),
                    data[4]
                );
                System.out.println("Added: " + data[1]);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Movie not found: " + movieTitle);
        }
    }
// Main method for testing the MovieAPI class functionality by searching for a specific movie, parsing its data, and inserting it into the database.
    public static void main(String[] args) {
        System.out.println("OMDb Movie API - Testing...\n");
        String json = searchMovieJSON("Inception");
        if (json != null) {
            System.out.println("Raw response:\n" + json);
            String[] data = parseMovieData(json);
            if (data != null) {
                System.out.println("\nParsed data:");
                System.out.println("Title: " + data[1]);
                System.out.println("Genre: " + data[2]);
                System.out.println("Duration: " + data[3] + " min");
                System.out.println("Plot: " + data[4]);
                
                System.out.println("\nNow testing database insert...");
                MovieCRUD crud = new MovieCRUD();
                try {
                    int movieId = (int)(Math.random() * 10000);
                    crud.movieInsert(
                        movieId,
                        data[1],
                        data[2],
                        Integer.parseInt(data[3]),
                        data[4]
                    );
                    System.out.println("Movie inserted!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Failed to parse movie data");
            }
        } else {
            System.out.println("Failed to fetch from API - check internet connection");
        }
    }
}