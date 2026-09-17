import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import javax.swing.JPanel;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JTextField;
import java.sql.ResultSet;
import javax.swing.JPasswordField;
import java.sql.SQLException;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Main login menu for the theater booking system.
 * Provides user authentication and navigation to the home screen.
 */
public class MainMenu extends JFrame {
    
    private static final Color DARK_BG = new Color(30, 30, 40);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color BUTTON_BLUE = new Color(70, 100, 180);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    public static int loggedInUserID = -1;
    public static String loggedInUsername = "";
    
    private JPanel buttonJPanel;
    private JButton[] buttons;
    private JTextField username;
    private JPasswordField password;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    
    // constructor for the main menu used to login
    public MainMenu() 
    {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("CINEMA LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(GOLD);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 15, 15));
        centerPanel.setBackground(DARK_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));
        
        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        centerPanel.add(usernameLabel);
        
        username = new JTextField(15);
        username.setFont(new Font("Arial", Font.PLAIN, 14));
        username.setBackground(new Color(50, 50, 60));
        username.setForeground(TEXT_COLOR);
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        centerPanel.add(username);
        
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        centerPanel.add(passwordLabel);
        
        password = new JPasswordField(15);
        password.setFont(new Font("Arial", Font.PLAIN, 14));
        password.setBackground(new Color(50, 50, 60));
        password.setForeground(TEXT_COLOR);
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        centerPanel.add(password);
        
        add(centerPanel, BorderLayout.CENTER);
        
        buttons = new JButton[2];
        buttonJPanel = new JPanel(new GridLayout(1, 2, 15, 10));
        buttonJPanel.setBackground(DARK_BG);
        buttonJPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 30, 50));
        
        buttons[0] = new JButton("Login");
        buttons[0].setFont(new Font("Arial", Font.BOLD, 14));
        buttons[0].setForeground(TEXT_COLOR);
        buttons[0].setBackground(BUTTON_BLUE);
        buttons[0].setFocusPainted(false);
        buttons[0].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        buttons[1] = new JButton("Create Account");
        buttons[1].setFont(new Font("Arial", Font.BOLD, 14));
        buttons[1].setForeground(TEXT_COLOR);
        buttons[1].setBackground(new Color(80, 120, 80));
        buttons[1].setFocusPainted(false);
        buttons[1].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        for (JButton btn : buttons) {
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(GOLD);
                    btn.setForeground(DARK_BG);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (btn == buttons[0]) btn.setBackground(BUTTON_BLUE);
                    else btn.setBackground(new Color(80, 120, 80));
                    btn.setForeground(TEXT_COLOR);
                }
            });
        }
        
        buttonJPanel.add(buttons[0]);
        buttonJPanel.add(buttons[1]);
        add(buttonJPanel, BorderLayout.SOUTH);
        
        TextFieldHandler Text_handler = new TextFieldHandler();
        username.addActionListener(Text_handler);
        password.addActionListener(Text_handler);
        
        ButtonHandler Button_handler = new ButtonHandler();
        buttons[0].addActionListener(Button_handler);
        buttons[1].addActionListener(Button_handler);
        
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(450, 200);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    // Method to handle Enter key press in text fields to trigger login
    private class TextFieldHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            buttons[0].doClick();
        }
    }
    // Method to handle button click events for login and create account
    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == buttons[1]) {
                CreateNewAcc createAcc = new CreateNewAcc();
                createAcc.setSize(400, 350);
                createAcc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                createAcc.setLocation(450, 200);
                createAcc.setVisible(true);
                dispose();
            }
            else if (event.getSource() == buttons[0]) {
                String user = username.getText();
                String pass = new String(password.getPassword());
                
                if (Login(user, pass)) {
                    int userID = getUserID(user);
                    JOptionPane.showMessageDialog(MainMenu.this, "Login Successful!");
                    dispose();
                    new HomeScreen(user, userID);
                } else {
                    JOptionPane.showMessageDialog(MainMenu.this, "Invalid username or password.");
                }
            }
        }
    }
    
    public static void main(String[] args) {
        new MainMenu();
    }
    
    public boolean Login(String username, String password) {
        String url = "jdbc:mysql://localhost/theater";
        String dbUser = "root";
        String dbPass = "OwenKhent098850";
        
        String sql = "SELECT username, password FROM users WHERE userName = ?;";
        
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (password.equals(dbPassword) && username.equals(rs.getString("userName"))) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Login failed. Wrong username or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private int getUserID(String username) {
        String url = "jdbc:mysql://localhost/theater";
        String dbUser = "root";
        String dbPass = "OwenKhent098850";
        
        String sql = "SELECT userID FROM users WHERE userName = ?";
        
        try (Connection con = DriverManager.getConnection(url, dbUser, dbPass);
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("userID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}