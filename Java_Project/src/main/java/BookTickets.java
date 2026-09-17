// Import all required Swing and AWT components for GUI
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// Import SQL components for database connectivity
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

// Import utility classes
import java.util.ArrayList;
import java.util.List;

// Main class for the ticket booking interface
// Uses CardLayout to show multiple steps in one window

/**
 * Ticket booking interface with multi-step flow.
 * Allows users to browse movies, select showtimes, and book seats.
 * 
 * @author Student
 * @version 1.0
 */
public class BookTickets extends JFrame {
    private static final Color DARK_BG = new Color(30, 30, 40);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color BUTTON_BLUE = new Color(70, 100, 180);
    private static final Color BUTTON_GREEN = new Color(80, 120, 80);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BG = new Color(50, 50, 60);
    
    // CardLayout manager - allows switching between different panels (steps)
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Variables to store user selections across all steps
    private int selectedMovieID = -1;           // ID of the selected movie
    private String selectedMovieName = "";      // Name of the selected movie
    private int selectedScreeningID = -1;      // ID of the selected showtime/screening
    private Timestamp selectedShowtime = null;  // Date/time of the showtime
    private List<Integer> selectedSeatIDs = new ArrayList<>();  // List of selected seat numbers
    
    // List models to hold data displayed in JLists
    private DefaultListModel<String> movieListModel;     // Movies from database
    private DefaultListModel<String> showtimeListModel;   // Showtimes for selected movie
    private DefaultListModel<String> seatListModel;       // Seat selection info
    
    // Stores showtime timestamps for reference when user selects a showtime
    private List<Timestamp> showtimeData = new ArrayList<>();
    
    // UI components that need to be accessed from multiple methods
    private JTextArea summaryArea;    // Booking summary text area
    private JButton showtimeButton;   // "Go to Showtimes" button
    private JButton seatsButton;      // "Go to Seats" button
    private JButton confirmButton;    // "Confirm Seats" button
    
    // Database connection settings
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";

    // Constructor - sets up the main window and all booking panels
    public BookTickets() {
        this(1, "Guest");
    }
    /**
     *@param userID The userID of the logged-in user
     *@param username The username of the logged-in user
     */
    public BookTickets(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("Book Tickets - " + username);
        setLayout(new BorderLayout());
        
        // Initialize CardLayout for switching between steps
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add all four steps as cards in the CardLayout
        mainPanel.add(createMoviePanel(), "MOVIE");        // Step 1
        mainPanel.add(createShowtimePanel(), "SHOWTIME");  // Step 2
        mainPanel.add(createSeatsPanel(), "SEATS");       // Step 3
        mainPanel.add(createConfirmationPanel(), "CONFIRM"); // Step 4
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Set window properties
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    // Creates Step 1: Movie Selection Panel
    private JPanel createMoviePanel() 
    {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Genre selection components - declare before use
        String[] genres = MovieAPI.getGenres();
        JComboBox<String> genreCombo = new JComboBox<>(genres);
        JButton bFetchByGenre = new JButton("Fetch by Genre");
        
        JButton bFilter = new JButton("Filter by Genre");
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Step 1: Select Movie"));
        topPanel.add(genreCombo);
        topPanel.add(bFilter);
        topPanel.add(bFetchByGenre);
        panel.add(topPanel, BorderLayout.NORTH);
        movieListModel = new DefaultListModel<>();
        loadMovies(null);
        JList<String> movieList = new JList<>(movieListModel);
        panel.add(new JScrollPane(movieList), BorderLayout.CENTER);
        
        // Nice buttons with better layout
        JButton bSelect = new JButton("Select Movie");
        showtimeButton = new JButton("Go to Showtimes");
        showtimeButton.setEnabled(false);
        JButton bFetch = new JButton("Fetch Movies");
        JButton bBack = new JButton("Back to Home");
        
        // Button panel - 2 rows, 3 columns
        JPanel btnPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        
        bFilter.addActionListener(e -> {
            String selectedGenre = (String) genreCombo.getSelectedItem();
            if (selectedGenre == null || selectedGenre.equals("All")) {
                loadMovies(null);
            } else {
                loadMovies(selectedGenre);
            }
        });
        
        bFetchByGenre.addActionListener(e -> {
            String selectedGenre = (String) genreCombo.getSelectedItem();
            if (selectedGenre == null || selectedGenre.equals("All")) {
                JOptionPane.showMessageDialog(this, "Please select a specific genre to fetch.");
                return;
            }
            
            try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)) {
                conn.setAutoCommit(false);
                conn.prepareStatement("DELETE FROM ticket").executeUpdate();
                conn.prepareStatement("DELETE FROM screening").executeUpdate();
                conn.prepareStatement("DELETE FROM movie").executeUpdate();
                conn.prepareStatement("ALTER TABLE movie AUTO_INCREMENT = 1").executeUpdate();
                conn.commit();
            } catch (Exception ex) { ex.printStackTrace(); }
            
            String[] movieTitles = MovieAPI.searchByGenrePages(selectedGenre, 3);
            if (movieTitles != null) {
                MovieCRUD crud = new MovieCRUD();
                for (String m : movieTitles) {
                    MovieAPI.searchAndAddMovie(crud, m);
                }
                loadMovies(selectedGenre);
                JOptionPane.showMessageDialog(this, "Fetched " + movieTitles.length + " " + selectedGenre + " movies from API!");
            } else {
                JOptionPane.showMessageDialog(this, "No movies found for genre: " + selectedGenre);
            }
        });
        bFetch.addActionListener(e -> {
            // Clear old data and reset IDs first
            try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)) {
                conn.setAutoCommit(false);
                conn.prepareStatement("DELETE FROM ticket").executeUpdate();
                conn.prepareStatement("DELETE FROM screening").executeUpdate();
                conn.prepareStatement("DELETE FROM movie").executeUpdate();
                conn.prepareStatement("ALTER TABLE movie AUTO_INCREMENT = 1").executeUpdate();
                conn.commit();
            } catch (Exception ex) { ex.printStackTrace(); }
            
            // Fetch from OMDB API dynamically
            String[] popularMovies = {"Inception", "The Dark Knight", "Avatar", "Titanic",
                "The Matrix", "Interstellar", "Avengers", "Frozen", "Spider-Man", "Joker"};
            MovieCRUD crud = new MovieCRUD();
            for (String m : popularMovies) 
            {
                MovieAPI.searchAndAddMovie(crud, m);
            }
            loadMovies(null);
            JOptionPane.showMessageDialog(this, "Fetched " + popularMovies.length + " movies from API!");
        });
        
        bSelect.addActionListener(e -> {
            String s = movieList.getSelectedValue();
            if (s != null) {
                selectedMovieID = Integer.parseInt(s.split(" - ")[0]);
                selectedMovieName = s.split(" - ")[1];
                showtimeButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Selected: " + selectedMovieName);
            }
        });
        
        showtimeButton.addActionListener(e -> {
            if (selectedMovieID > 0) { 
                generateShowtimes();  // Auto-generate showtimes if needed
                loadShowtimes(); 
                cardLayout.show(mainPanel, "SHOWTIME"); 
            }
        });
        
        bBack.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        
        btnPanel.add(bSelect);
        btnPanel.add(showtimeButton);
        btnPanel.add(bFetch);
        btnPanel.add(bBack);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Creates Step 2: Showtime Selection Panel
    // Displays available showtimes for the selected movie
    private JPanel createShowtimePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Title showing which movie's showtimes are displayed
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Step 2: Select Showtime - " + selectedMovieName, SwingConstants.CENTER));
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Showtime list - populated after selecting a movie
        showtimeListModel = new DefaultListModel<>();
        JList<String> showtimeList = new JList<>(showtimeListModel);
        panel.add(new JScrollPane(showtimeList), BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton selectButton = new JButton("Select Showtime");
        seatsButton = new JButton("Go to Seats");
        seatsButton.setEnabled(false);  // Disabled until a showtime is selected
        JButton backButton = new JButton("Back");
        
        // When "Select Showtime" is clicked, store the selected showtime
        selectButton.addActionListener(e -> {
            int index = showtimeList.getSelectedIndex();
            if (index >= 0) {
                String item = showtimeListModel.getElementAt(index);
                // Parse screening ID and timestamp
                selectedScreeningID = Integer.parseInt(item.split(" - ")[0]);
                selectedShowtime = showtimeData.get(index);
                // Enable the "Go to Seats" button
                seatsButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Showtime selected: " + selectedShowtime);
            }
        });
        
        // When "Go to Seats" is clicked, reload seat availability and switch to seat selection
        /**
         *@param e ActionEvent triggered when "Go to Seats" button is clicked
         *@return void
         */
        seatsButton.addActionListener(e -> {
            if (selectedScreeningID > 0) {
                // Just recreate the seat panel - CardLayout replaces old card with same name
                mainPanel.add(createSeatsPanel(), "SEATS");
                cardLayout.show(mainPanel, "SEATS");
            }
        });
        
        // Go back to movie selection
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MOVIE"));
        
        bottomPanel.add(selectButton);
        bottomPanel.add(seatsButton);
        bottomPanel.add(backButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Creates Step 3: Seat Selection Panel
    // Shows a 5x6 grid of seat buttons for the user to select
    private JPanel createSeatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Title
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Step 3: Select Seats", SwingConstants.CENTER));
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Info panel showing selected seats count
        seatListModel = new DefaultListModel<>();
        seatListModel.addElement("Click buttons to select/deselect seats");
        JList<String> seatList = new JList<>(seatListModel);
        panel.add(new JScrollPane(seatList), BorderLayout.CENTER);
        
        // Create seat button grid (5 rows x 6 columns = 30 seats)
        JPanel seatButtonsPanel = createSeatButtonsPanel();
        panel.add(seatButtonsPanel, BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        confirmButton = new JButton("Confirm Seats");
        confirmButton.setEnabled(false);
        JButton backButton = new JButton("Back");
        
        confirmButton.addActionListener(e -> {
            if (!selectedSeatIDs.isEmpty()) {
                updateSummary();
                cardLayout.show(mainPanel, "CONFIRM");
            }
        });
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SHOWTIME"));
        
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    // Helper method to create the grid of seat buttons, with availability from database
    private JPanel createSeatButtonsPanel() {
        JPanel seatButtonsPanel = new JPanel(new GridLayout(5, 6, 5, 5));
        JButton[] seatButtons = new JButton[30];
        boolean[] availability = loadSeatAvailability();
        
        // Create a button for each seat
        for (int i = 0; i < 30; i++) {
            final int seatNum = i + 1;
            seatButtons[i] = new JButton(String.valueOf(seatNum));
            
            if (availability[i]) {
                // Available seat - green background
                seatButtons[i].setBackground(new java.awt.Color(34, 139, 34));
                seatButtons[i].setForeground(java.awt.Color.WHITE);
                final int seatIndex = i;
                
                // Toggle seat selection on click
                seatButtons[i].addActionListener(e -> {
                    if (selectedSeatIDs.contains(seatNum)) {
                        // Deselect: remove from list, turn green again
                        selectedSeatIDs.remove(Integer.valueOf(seatNum));
                        seatButtons[seatIndex].setBackground(new java.awt.Color(34, 139, 34));
                    } else {
                        // Select: add to list, turn red
                        selectedSeatIDs.add(seatNum);
                        seatButtons[seatIndex].setBackground(java.awt.Color.RED);
                    }
                    updateSeatList();  // Enable/disable confirm button based on selection
                });
            } else {
                // Taken seat - red background, cannot be clicked
                seatButtons[i].setBackground(java.awt.Color.RED);
                seatButtons[i].setForeground(java.awt.Color.WHITE);
                seatButtons[i].setEnabled(false);
            }
            seatButtonsPanel.add(seatButtons[i]);
        }
        return seatButtonsPanel;
    }
    
    // Creates Step 4: Confirmation Panel
    // Shows booking summary and allows user to confirm or cancel
    private JPanel createConfirmationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Title
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Step 4: Confirm Booking", SwingConstants.CENTER));
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Summary text area - shows booking details
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);  // Read-only
        panel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        
        // Bottom buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton confirmButton = new JButton("Confirm Booking");
        JButton backButton = new JButton("Back");
        JButton homeButton = new JButton("Cancel");
        
        // Complete the booking - save to database
        confirmButton.addActionListener(e -> completeBooking());
        
        // Go back to seat selection
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SEATS"));
        
        // Cancel booking and return to home
        homeButton.addActionListener(e -> {
            dispose();
            new HomeScreen(username, userID);
        });
        
        bottomPanel.add(confirmButton);
        bottomPanel.add(backButton);
        bottomPanel.add(homeButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // Loads all movies from the database into the movie list
    private void loadMovies(String genre) {
        movieListModel.clear();  // Clear existing items first
        String query;
        if (genre == null || genre.equals("All")) {
            query = "SELECT movieID, title, genre FROM movie ORDER BY movieID";
        } else {
            query = "SELECT movieID, title, genre FROM movie WHERE genre LIKE ? ORDER BY movieID";
        }
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            if (genre != null && !genre.equals("All")) {
                ps.setString(1, "%" + genre + "%");
            }
            ResultSet rs = ps.executeQuery();
            // Iterate through all movies returned
            while (rs.next()) {
                int id = rs.getInt("movieID");
                String title = rs.getString("title");
                String movieGenre = rs.getString("genre");
                // Add to list in format: "ID - Title (Genre)"
                movieListModel.addElement(id + " - " + title + " (" + movieGenre + ")");
            }
            if (movieListModel.isEmpty()) {
                movieListModel.addElement("No movies found. Try fetching movies first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Loads showtimes for the selected movie from the database
    private void loadShowtimes() {
        showtimeListModel.clear();   // Clear previous showtimes
        showtimeData.clear();        // Clear previous timestamps
        
        System.out.println("Loading showtimes for movieID: " + selectedMovieID);
        
        String query = "SELECT screeningID, startTime FROM screening WHERE movieID = ? ORDER BY startTime";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, selectedMovieID);  // Filter by selected movie
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                int screeningID = rs.getInt("screeningID");
                Timestamp startTime = rs.getTimestamp("startTime");
                showtimeData.add(startTime);  // Store timestamp for reference
                showtimeListModel.addElement(screeningID + " - " + startTime.toString());
                count++;
            }
            System.out.println("Loaded " + count + " showtimes");
            if (count == 0) {
                JOptionPane.showMessageDialog(this, "No showtimes found for this movie!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Loads seat availability for the selected screening and updates the seat buttons accordingly
    private void generateShowtimes() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)) {
            var checkPs = conn.prepareStatement("SELECT COUNT(*) FROM screening WHERE movieID = ?");
            checkPs.setInt(1, selectedMovieID);
            var checkRs = checkPs.executeQuery();
            checkRs.next();
            if (checkRs.getInt(1) > 0) return;
            
            var cal = java.util.Calendar.getInstance();
            var ps = conn.prepareStatement("INSERT INTO screening (movieID, startTime) VALUES (?, ?)");
            ps.setInt(1, selectedMovieID);
            for (int i = 0; i < 3; i++) {
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                cal.set(java.util.Calendar.HOUR_OF_DAY, 10 + (i * 4));
                cal.set(java.util.Calendar.MINUTE, 0);
                ps.setTimestamp(2, new java.sql.Timestamp(cal.getTimeInMillis()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     *  Loads seat availability for the selected screening from the database.
     *  Returns a boolean array where true indicates an available seat and false indicates a booked seat
     * @return
     */
    private boolean[] loadSeatAvailability() {
        boolean[] avail = new boolean[30];
        // Initialize all seats as available
        for (int i = 0; i < 30; i++) avail[i] = true;
        
        // Query tickets table to find booked seats for this screening
        String query = "SELECT seatID FROM ticket WHERE screeningID = ?";
        System.out.println("Loading seats for screeningID: " + selectedScreeningID);
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, selectedScreeningID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int seatID = rs.getInt("seatID");
                System.out.println("Found booked seat: " + seatID);
                // Mark seat as unavailable (if within our 30 seat range)
                if (seatID >= 1 && seatID <= 30) {
                    avail[seatID - 1] = false;
                    System.out.println("Marking seat " + seatID + " as unavailable");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Availability array: " + java.util.Arrays.toString(avail));
        return avail;
    }
    
    /**
     * Enables or disables the "Confirm Seats" button based on whether any seats are currently selected.
     */
    private void updateSeatList() {
        if (selectedSeatIDs.isEmpty()) {
            confirmButton.setEnabled(false);
        } else {
            confirmButton.setEnabled(true);
        }
    }
    
    // Generates and displays the booking summary text
    private void updateSummary() {
        int totalPrice = selectedSeatIDs.size() * 15;  // $15 per ticket
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOOKING SUMMARY ===\n\n");
        sb.append("Movie: ").append(selectedMovieName).append("\n");
        sb.append("Showtime: ").append(selectedShowtime).append("\n");
        sb.append("Screening ID: ").append(selectedScreeningID).append("\n");
        sb.append("Selected Seats: ").append(selectedSeatIDs).append("\n");
        sb.append("Number of Tickets: ").append(selectedSeatIDs.size()).append("\n");
        sb.append("Price per Ticket: $15\n");
        sb.append("Total Price: $").append(totalPrice).append("\n");
        summaryArea.setText(sb.toString());
    }
    
    // Completes the booking by inserting records into the database
    // Creates a purchase record and ticket records for each selected seat
    private void completeBooking() {
        int totalPrice = selectedSeatIDs.size() * 15;
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)) {
            conn.setAutoCommit(false);  // Start transaction
            
            // Insert purchase record first
            String purchaseQuery = "INSERT INTO purchase (userID, totalPrice) VALUES (?, ?)";
            int purchaseID;
            try (PreparedStatement ps = conn.prepareStatement(purchaseQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userID);
                ps.setInt(2, totalPrice);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                purchaseID = rs.getInt(1);  // Get the auto-generated purchase ID
            }
            
            /* Insert a ticket record for each selected seat */
            String ticketQuery = "INSERT INTO ticket (seatID, screeningID, userID, purchaseID, price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(ticketQuery)) {
                for (int seatID : selectedSeatIDs) {
                    ps.setInt(1, seatID);          // Seat number
                    ps.setInt(2, selectedScreeningID);  // Screening ID
                    ps.setInt(3, userID);          // User ID
                    ps.setInt(4, purchaseID);     // Purchase ID (links ticket to purchase)
                    ps.setInt(5, 15);             // Price per ticket
                    ps.executeUpdate();
                }
            }
            
            conn.commit();  // Commit transaction
            
            // Add loyalty points (1 point per dollar spent)
            addLoyaltyPoints(userID, totalPrice);
            
            // Show success message with purchase ID
            JOptionPane.showMessageDialog(this, 
                "Booking confirmed!\nPurchase ID: " + purchaseID + "\nTotal: $" + totalPrice + "\nLoyalty Points Earned: " + totalPrice);
            dispose();
            new HomeScreen(username, userID);  // Return to home screen
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Booking failed: " + e.getMessage());
        }
    }
    // Helper method to add loyalty points to the user's account after a successful purchase
    /**
     * @param userID The ID of the user to whom loyalty points will be added
     * @param points The number of loyalty points to add (e.g., 1 point per dollar spent)
     */
    private void addLoyaltyPoints(int userID, int points) {
        String query = "UPDATE users SET loyaltyPoints = COALESCE(loyaltyPoints, 0) + ? WHERE userID = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, points);
            ps.setInt(2, userID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            new BookTickets();
        });
    }
}
