import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User profile management interface.
 * Displays user details and allows profile updates.
 * 
 * @author Student
 * @version 1.0
 */
public class ProfileUI extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID;
    private String username = "Guest";
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel pointsLabel;
    private DefaultListModel<String> ticketListModel;
    private DefaultListModel<String> purchaseListModel;
    private JLabel totalLabel;
    
    // Default constructor for ProfileUI, initializes with default userID and username
    public ProfileUI() {
        this(1);
    }
    /**
     * @param userID The userID of the logged-in user
     */
    public ProfileUI(int userID) {
        this(userID, "Guest");
    }
    /**
     * @param userID The userID of the logged-in user
     * @param username The username of the logged-in user
     */
    public ProfileUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("My Profile - " + username);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        topPanel.add(new JLabel("Username:"));
        nameLabel = new JLabel("-");
        topPanel.add(nameLabel);
        
        topPanel.add(new JLabel("Email:"));
        emailLabel = new JLabel("-");
        topPanel.add(emailLabel);
        
        topPanel.add(new JLabel("Loyalty Points:"));
        pointsLabel = new JLabel("0");
        topPanel.add(pointsLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JLabel("My Tickets:"), BorderLayout.NORTH);
        ticketListModel = new DefaultListModel<>();
        JList<String> ticketList = new JList<>(ticketListModel);
        centerPanel.add(new JScrollPane(ticketList), BorderLayout.CENTER);
        
        JPanel purchasePanel = new JPanel(new BorderLayout(5, 5));
        purchasePanel.add(new JLabel("Purchase History:"), BorderLayout.NORTH);
        purchaseListModel = new DefaultListModel<>();
        JList<String> purchaseList = new JList<>(purchaseListModel);
        purchasePanel.add(new JScrollPane(purchaseList), BorderLayout.CENTER);
        
        totalLabel = new JLabel("Total Spent: $0");
        purchasePanel.add(totalLabel, BorderLayout.SOUTH);
        
        centerPanel.add(purchasePanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        bottomPanel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadProfile());
        bottomPanel.add(refreshButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadProfile();
        
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // This method loads the user's profile information,
    // including username, email, loyalty points, tickets, and purchase history from the database and updates the UI components accordingly.
    private void loadProfile() {
        String query = "SELECT userName, email, loyaltyPoints FROM users WHERE userID = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameLabel.setText(rs.getString("userName"));
                emailLabel.setText(rs.getString("email"));
                Integer points = rs.getInt("loyaltyPoints");
                pointsLabel.setText(points != null ? points.toString() : "0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        loadTickets();
        loadPurchaseHistory();
    }
    // This method loads the user's purchase history from the database,
    // calculates the total amount spent, and updates the purchase list and total label in the UI.
    private void loadPurchaseHistory() {
        purchaseListModel.clear();
        int totalSpent = 0;
        String query = "SELECT purchaseID, totalPrice, purchaseDate FROM purchase WHERE userID = ? ORDER BY purchaseDate DESC";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int price = rs.getInt("totalPrice");
                totalSpent += price;
                purchaseListModel.addElement("Purchase #" + rs.getInt("purchaseID") + 
                    " - $" + price + " - " + rs.getTimestamp("purchaseDate"));
            }
            if (purchaseListModel.isEmpty())
                {
                purchaseListModel.addElement("No purchases yet.");
            }
            totalLabel.setText("Total Spent: $" + totalSpent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // This method loads the user's tickets from the database and updates the ticket list in the UI.
    private void loadTickets() {
        ticketListModel.clear();
        String query = "SELECT ticket.ticketID, movie.title, screening.startTime, ticket.seatID, ticket.price " +
                    "FROM ticket " +
                    "INNER JOIN screening ON ticket.screeningID = screening.screeningID " +
                    "INNER JOIN movie ON screening.movieID = movie.movieID " +
                    "WHERE ticket.userID = ? ORDER BY screening.startTime DESC";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ticketListModel.addElement("Ticket #" + rs.getInt("ticket.ticketID") + 
                    " - " + rs.getString("movie.title") + 
                    " | Seat: " + rs.getInt("ticket.seatID") + 
                    " | $" + rs.getInt("ticket.price") + 
                    " | " + rs.getTimestamp("screening.startTime"));
            }
            if (ticketListModel.isEmpty()) {
                ticketListModel.addElement("No tickets purchased yet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new ProfileUI();
    }
}
