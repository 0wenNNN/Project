import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
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

/**
 * UI for customer service support requests.
 * 
 * @author Student
 * @version 1.0
 */
public class CustomerServiceUI extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";
    private DefaultListModel<String> requestListModel;
    private JTextArea messageArea;
    // Default constructor for CustomerServiceUI, initializes with default userID and username
    public CustomerServiceUI() {
        this(1, "Guest");
    }
    // Constructor for CustomerServiceUI when userID is provided, initializes with default username
     /**
     * @param userID The userID of the logged-in user
     */
    public CustomerServiceUI(int userID) {
        this(userID, "Guest");
    }
    // Constructor for CustomerServiceUI when userID and username are provided
    /**
     * @param userID The userID of the logged-in user
     * @param username The username of the logged-in user
     */
    public CustomerServiceUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("Customer Service");
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Submit a Support Request", SwingConstants.CENTER));
        add(topPanel, BorderLayout.NORTH);
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(new JLabel("Describe your issue:"), BorderLayout.NORTH);
        messageArea = new JTextArea(5, 30);
        inputPanel.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit Request");
        submitButton.addActionListener(e -> submitRequest());
        buttonPanel.add(submitButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(new JLabel("My Requests:"), BorderLayout.NORTH);
        requestListModel = new DefaultListModel<>();
        JList<String> requestList = new JList<>(requestListModel);
        bottomPanel.add(new JScrollPane(requestList), BorderLayout.CENTER);
        
        JPanel backPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        backPanel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadRequests());
        backPanel.add(refreshButton);
        
        bottomPanel.add(backPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadRequests();
        
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // This method is used to submit a new support request to the database
    private void submitRequest() {
        String message = messageArea.getText().trim();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please describe your issue.");
            return;
        }
        
        String query = "INSERT INTO customerservice (userID, issue, status, created) VALUES (?, ?, 0, NOW())";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ps.setString(2, message);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Request submitted!");
            messageArea.setText("");
            loadRequests();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    // This method loads the user's existing support requests from the database and displays them in the request list
    private void loadRequests() {
        requestListModel.clear();
        String query = "SELECT supportID, issue, status, created FROM customerservice WHERE userID = ? ORDER BY created DESC";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getInt("status") == 0 ? "Pending" : "Resolved";
                requestListModel.addElement("#" + rs.getInt("supportID") + 
                    " [" + status + "] - " + rs.getString("issue") + 
                    " (" + rs.getTimestamp("created") + ")");
            }
            if (requestListModel.isEmpty()) {
                requestListModel.addElement("No requests submitted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Main method to launch the CustomerServiceUI
    public static void main(String[] args) {
        new CustomerServiceUI();
    }
}