import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//this class represents the UI for browsing movies.
//It displays a list of available movies and allows users to refresh the list or go back to the home screen.
// The BrowseMoviesUI class provides a user interface for browsing available movies in the theater application. It connects to the database to retrieve movie information and displays it in a list format. Users can refresh the movie list to see any updates or go back to the home screen.

/**
 * UI for browsing available movies in the theater.
 * 
 * @author Student
 * @version 1.0
 */
public class BrowseMoviesUI extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";
    private DefaultListModel<String> movieListModel;
    // Default constructor for BrowseMoviesUI, initializes with default userID and username
    public BrowseMoviesUI() 
    {
        this(1, "Guest");
    }
    // Constructor for BrowseMoviesUI when userID and username are provided
    /**
     * @param userID The userID of the logged-in user
     * @param username The username of the logged-in user
     */
    public BrowseMoviesUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("Browse Movies - " + username);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Available Movies", SwingConstants.CENTER));
        add(topPanel, BorderLayout.NORTH);
        
        movieListModel = new DefaultListModel<>();
        JList<String> movieList = new JList<>(movieListModel);
        add(new JScrollPane(movieList), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        bottomPanel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMovies());
        bottomPanel.add(refreshButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadMovies();
        
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // this method is used to load movies from the database and display them in the movie list
    private void loadMovies() {
        movieListModel.clear();
        String query = "SELECT movieID, title, genre, duration, description FROM movie ORDER BY title";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String info = rs.getString("title") + " (" + rs.getString("genre") + ") - " + 
                    rs.getInt("duration") + " min";
                movieListModel.addElement(info);
            }
            if (movieListModel.isEmpty()) {
                movieListModel.addElement("No movies available. Fetch movies first.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new BrowseMoviesUI();
    }
}