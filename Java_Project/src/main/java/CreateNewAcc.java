import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import javax.swing.JPanel;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JTextField;
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

// This class represents the UI for creating a new account. 
// It allows users to enter a username, email, and password to create an account in the system. 
// The information is validated and then inserted into the database.

/**
 * UI for creating a new user account.
 * 
 * @author Student
 * @version 1.0
 */
public class CreateNewAcc extends JFrame {
    
    private static final Color DARK_BG = new Color(30, 30, 40);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color BUTTON_GREEN = new Color(80, 120, 80);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BG = new Color(50, 50, 60);
    
    private JPanel buttonJPanel;
    private JButton[] buttons;
    private JTextField username;
    private JTextField email;
    private JPasswordField password;
    private JLabel CreateusernameLabel;
    private JLabel CreateemailLabel;
    private JLabel CreatepasswordLabel;
    Connection con = null;
    
    public CreateNewAcc() {
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));
        // Top panel with title and styling
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        // Title label for the create account screen
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        // Set font, color, and alignment for the title label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(GOLD);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        // Center panel with form fields for username, email, and password
        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        centerPanel.setBackground(DARK_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        // Username label and text field
        CreateusernameLabel = new JLabel("Username");
        // Set font and color for the username label
        CreateusernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        CreateusernameLabel.setForeground(TEXT_COLOR);
        centerPanel.add(CreateusernameLabel);
        
        username = new JTextField(15);
        username.setFont(new Font("Arial", Font.PLAIN, 14));
        username.setBackground(FIELD_BG);
        username.setForeground(TEXT_COLOR);
        username.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        centerPanel.add(username);
        //  Email label and text field
        CreateemailLabel = new JLabel("Email");
        CreateemailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        CreateemailLabel.setForeground(TEXT_COLOR);
        centerPanel.add(CreateemailLabel);
        // Email text field with styling
        email = new JTextField(15);
        email.setFont(new Font("Arial", Font.PLAIN, 14));
        email.setBackground(FIELD_BG);
        email.setForeground(TEXT_COLOR);
        email.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        centerPanel.add(email);
        
        CreatepasswordLabel = new JLabel("Password");
        CreatepasswordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        CreatepasswordLabel.setForeground(TEXT_COLOR);
        centerPanel.add(CreatepasswordLabel);
        
        password = new JPasswordField(15);
        password.setFont(new Font("Arial", Font.PLAIN, 14));
        password.setBackground(FIELD_BG);
        password.setForeground(TEXT_COLOR);
        password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        centerPanel.add(password);
        
        buttons = new JButton[2];

        buttons[0] = new JButton("Create Account");
        buttons[0].setFont(new Font("Arial", Font.BOLD, 14));
        buttons[0].setForeground(TEXT_COLOR);
        buttons[0].setBackground(BUTTON_GREEN);
        buttons[0].setFocusPainted(false);
        buttons[0].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        buttons[0].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                buttons[0].setBackground(GOLD);
                buttons[0].setForeground(DARK_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                buttons[0].setBackground(BUTTON_GREEN);
                buttons[0].setForeground(TEXT_COLOR);
            }
        });
        
        centerPanel.add(buttons[0]);
        
        buttons[1] = new JButton("Back to Login");
        buttons[1].setFont(new Font("Arial", Font.BOLD, 14));
        buttons[1].setForeground(TEXT_COLOR);
        buttons[1].setBackground(new Color(80, 120, 80));
        buttons[1].setFocusPainted(false);
        buttons[1].setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        centerPanel.add(buttons[1]);

        add(centerPanel, BorderLayout.CENTER);
        
        TextFieldHandler Text_handler = new TextFieldHandler();
        username.addActionListener(Text_handler);
        email.addActionListener(Text_handler);
        password.addActionListener(Text_handler);
        
        ButtonHandler Button_handler = new ButtonHandler();
        buttons[0].addActionListener(Button_handler);
        buttons[1].addActionListener(Button_handler);
        
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(450, 200);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    private class TextFieldHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            buttons[0].doClick();
        }
    }
    
    private class ButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == buttons[0]) {
                String nameCreate = username.getText().trim();
                String emailCreate = email.getText().trim();
                String passCreate = new String(password.getPassword());
                
                if (nameCreate.isEmpty() || emailCreate.isEmpty() || passCreate.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    con = DriverManager.getConnection(
                        "jdbc:mysql://localhost/theater",
                        "root",
                        "OwenKhent098850"
                    );
                    
                    String sql = "INSERT INTO users (userName, email, password) VALUES (?, ?, ?)";
                    PreparedStatement statement = con.prepareStatement(sql);
                    statement.setString(1, nameCreate);
                    statement.setString(2, emailCreate);
                    statement.setString(3, passCreate);
                    
                    int rowsInserted = statement.executeUpdate();
                    
                    if (rowsInserted > 0) {
                        System.out.println("A new user was inserted successfully!");
                        JOptionPane.showMessageDialog(null, "Account created! Please login.");
                        dispose();
                        new MainMenu();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create account. Please try again.");
                    }
                    statement.close();
                    
                } catch (ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(null, "Database driver not found.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                } finally {
                    try {
                        if (con != null)
                            con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            if(event.getSource() == buttons[1]) {
                dispose();
                new MainMenu();
            }
        }
    }
}