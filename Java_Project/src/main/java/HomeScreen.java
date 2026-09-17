import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

/**
 * Home screen after login, provides navigation to all system features.
 * 
 * @author Student
 * @version 1.0
 */
public class HomeScreen extends JFrame {
    
    private static final Color DARK_BG = new Color(30, 30, 40);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color RED = new Color(180, 30, 30);
    private static final Color BUTTON_BLUE = new Color(70, 100, 180);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private String username;
    private int userID;
    private JPanel buttonJPanel;
    private JButton[] buttons;
    
    public HomeScreen() 
    {
        this("Guest", 1);
    }
    // Constructor for HomeScreen when username and userID are provided
     /**
     * @param username The username of the logged-in user
     * @param userID The userID of the logged-in user
     */
    public HomeScreen(String username, int userID) {
        this.username = username;
        this.userID = userID;
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("CINEMA BOOKING - Welcome, " + username);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(GOLD);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        try {
            ImageIcon logo = new ImageIcon("theater/lib/logo.png");
            JLabel logoLabel = new JLabel(logo);
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoLabel.setBackground(DARK_BG);
            add(logoLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel welcomeLabel = new JLabel("Welcome to Our Theater");
            welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            welcomeLabel.setForeground(TEXT_COLOR);
            welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
            welcomeLabel.setBackground(DARK_BG);
            welcomeLabel.setOpaque(true);
            add(welcomeLabel, BorderLayout.CENTER);
        }
        
        buttons = new JButton[8];
        buttonJPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        buttonJPanel.setBackground(DARK_BG);
        buttonJPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        String[] labels = {"Browse Movies", "Book Tickets", "View Profile", "My Tickets", 
                        "Order Food", "Reviews", "Customer Service", "Logout"};
        
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(labels[i]);
            buttons[i].setFont(new Font("Arial", Font.BOLD, 14));
            buttons[i].setForeground(TEXT_COLOR);
            buttons[i].setBackground(BUTTON_BLUE);
            buttons[i].setFocusPainted(false);
            buttons[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            ));
            
            if (i == 7) {
                buttons[i].setBackground(RED);
            }
            
            buttons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    ((JButton)evt.getSource()).setBackground(GOLD);
                    ((JButton)evt.getSource()).setForeground(DARK_BG);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    JButton btn = (JButton)evt.getSource();
                    btn.setBackground(btn == buttons[7] ? RED : BUTTON_BLUE);
                    btn.setForeground(TEXT_COLOR);
                }
            });
            
            buttonJPanel.add(buttons[i]);
        }
        add(buttonJPanel, BorderLayout.SOUTH);
        
        ButtonHandler Button_handler = new ButtonHandler();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].addActionListener(Button_handler);
        }
        
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(450, 150);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // Method to get userID from database based on username
    /**
     * @param username The username to look up in the database
     * @return The userID associated with the given username, or 1 if not found
     */
    private int getUserID(String username) {
        try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:mysql://localhost/theater", "root", "OwenKhent098850")) {
            var ps = conn.prepareStatement("SELECT userID FROM users WHERE userName = ?");
            ps.setString(1, username);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("userID");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 1;
    }
    /*
     * Handles button click events for the home screen buttons
     */
    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == buttons[0]) {
                dispose();
                new BrowseMoviesUI(userID, username);
            }
            else if (event.getSource() == buttons[1]) {
                dispose();
                BookTickets bookTickets = new BookTickets(userID, username);
                bookTickets.setSize(450, 400);
                bookTickets.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                bookTickets.setLocation(450, 150);
                bookTickets.setVisible(true);
            }
            else if (event.getSource() == buttons[2]) {
                dispose();
                new ProfileUI(userID, username);
            }
            else if (event.getSource() == buttons[3]) {
                dispose();
                new MyTicketsUI(userID, username);
            }
            else if (event.getSource() == buttons[4]) {
                dispose();
                new FoodOrderUI(userID, username);
            }
            else if (event.getSource() == buttons[5]) {
                dispose();
                new ReviewUI(userID, username);
            }
            else if (event.getSource() == buttons[6]) {
                dispose();
                new CustomerServiceUI(userID, username);
            }
            else if (event.getSource() == buttons[7]) {
                int choice = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to logout?", 
                    "Logout", 
                    JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    new MainMenu();
                }
            }
        }
    }
}