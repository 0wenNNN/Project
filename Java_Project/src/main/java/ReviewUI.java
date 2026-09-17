import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

// This class represents the UI for submitting and viewing movie reviews.
// It allows users to select a movie, view existing reviews, and submit their own review with a rating and comment.

/**
 * UI for submitting and viewing movie reviews.
 * 
 * @author Student
 * @version 1.0
 */
public class ReviewUI extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";
    private JComboBox<String> movieCombo;
    private DefaultListModel<String> reviewListModel;
    private JTextField ratingField;
    private JTextField commentField;
    private int selectedMovieID = -1;
    private List<Integer> movieIDs = new ArrayList<>();
    
    // Default constructor for ReviewUI, initializes with default userID and username
    public ReviewUI() {
        this(1);
    }
    //  Constructor for ReviewUI when userID is provided, initializes with default username
     /**
     * @param userID The userID of the logged-in user
     */
    public ReviewUI(int userID) {
        this(userID, "Guest");
    }
    // Constructor for ReviewUI when userID and username are provided
    /**
     * @param userID The userID of the logged-in user
     * @param username The username of the logged-in user
     */
    public ReviewUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("Movie Reviews");
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Movie:"));
        movieCombo = new JComboBox<>();
        loadMovies();
        movieCombo.addActionListener(e -> {
            int index = movieCombo.getSelectedIndex();
            if (index >= 0) {
                selectedMovieID = movieIDs.get(index);
                loadReviews();
            }
        });
        topPanel.add(movieCombo);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JLabel("Existing Reviews:"), BorderLayout.NORTH);
        reviewListModel = new DefaultListModel<>();
        JList<String> reviewList = new JList<>(reviewListModel);
        centerPanel.add(new JScrollPane(reviewList), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.add(new JLabel("Rating (1-5):"));
        ratingField = new JTextField();
        inputPanel.add(ratingField);
        
        inputPanel.add(new JLabel("Comment:"));
        commentField = new JTextField();
        inputPanel.add(commentField);
        
        JButton submitButton = new JButton("Submit Review");
        submitButton.addActionListener(e -> submitReview());
        inputPanel.add(submitButton);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        inputPanel.add(backButton);
        
        add(inputPanel, BorderLayout.SOUTH);
        
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // Method to load movies from the database and populate the movieCombo JComboBox with movie titles,
    // while keeping track of their corresponding movieIDs in a list.
    private void loadMovies() {
        movieCombo.removeAllItems();
        movieIDs.clear();
        String query = "SELECT movieID, title FROM movie ORDER BY title";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movieIDs.add(rs.getInt("movieID"));
                movieCombo.addItem(rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Method to load reviews for the selected movie from the database and populate the reviewListModel 
    // with formatted review information.
    private void loadReviews()
    {
        reviewListModel.clear();
        if (selectedMovieID <= 0) return;
        
        String query = "SELECT review.rating, review.comment, review.reviewDate, users.userName FROM review " +
                    "INNER JOIN users ON review.userID = users.userID WHERE review.movieID = ? ORDER BY review.reviewDate DESC";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, selectedMovieID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reviewListModel.addElement(rs.getInt("review.rating") + "/5 stars - " + rs.getString("review.comment") + 
                    " (by " + rs.getString("users.userName") + ")");
            }
            if (reviewListModel.isEmpty()) {
                reviewListModel.addElement("No reviews yet for this movie.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Method to submit a new review for the selected movie, validating the input and inserting the review into the database using the ReviewCRUD class.
    private void submitReview() {
        if (selectedMovieID <= 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.");
            return;
        }
        
        try {
            int rating = Integer.parseInt(ratingField.getText());
            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.");
                return;
            }
            String comment = commentField.getText();
            if (comment.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a comment.");
                return;
            }
            
            ReviewCRUD crud = new ReviewCRUD();
            crud.reviewInsert(userID, selectedMovieID, rating, comment);
            
            JOptionPane.showMessageDialog(this, "Review submitted successfully!");
            ratingField.setText("");
            commentField.setText("");
            loadReviews();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid rating (1-5).");
        }
    }
    
    public static void main(String[] args) {
        new ReviewUI();
    }
}
