import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
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
 * This class represents the UI for displaying a user's booked tickets.
 * 
 * @author Student
 * @version 1.0
 */
public class MyTicketsUI extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";
    private DefaultListModel<String> ticketListModel;
    private JLabel totalLabel;
    /**
     * Default constructor for MyTicketsUI, initializes with default userID and username
     */
    public MyTicketsUI() {
        this(1, "Guest");
    }
    
    public MyTicketsUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        setTitle("My Tickets");
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("My Booked Tickets", SwingConstants.CENTER));
        add(topPanel, BorderLayout.NORTH);
        
        ticketListModel = new DefaultListModel<>();
        JList<String> ticketList = new JList<>(ticketListModel);
        add(new JScrollPane(ticketList), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        buttonPanel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTickets());
        buttonPanel.add(refreshButton);
        
        totalLabel = new JLabel("Total Spent: $0");
        bottomPanel.add(buttonPanel);
        bottomPanel.add(totalLabel);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        loadTickets();
        
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(500, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    /**
     * Loads the user's booked tickets from the database and updates the ticket list.
     */
    private void loadTickets() {
        ticketListModel.clear();
        int totalSpent = 0;
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
                int price = rs.getInt("ticket.price");
                totalSpent += price;
                ticketListModel.addElement("Ticket #" + rs.getInt("ticket.ticketID") + 
                    " | " + rs.getString("movie.title") + 
                    " | Seat: " + rs.getInt("ticket.seatID") + 
                    " | $" + price + 
                    " | " + rs.getTimestamp("screening.startTime"));
            }
            if (ticketListModel.isEmpty()) {
                ticketListModel.addElement("No tickets purchased yet.");
            }
            totalLabel.setText("Total Spent: $" + totalSpent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new MyTicketsUI();
    }
}