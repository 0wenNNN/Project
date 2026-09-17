import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// This class represents the UI for ordering food at the concession stand.
// It allows users to select items, specify quantities, and apply loyalty points for discounts.

/**
 * UI for ordering food and beverages at the theater.
 * 
 * @author Student
 * @version 1.0
 */
public class FoodOrderUI extends JFrame {
    private static final Color DARK_BG = new Color(30, 30, 40);
    private static final Color GOLD = new Color(212, 175, 55);
    private static final Color BUTTON_BLUE = new Color(70, 100, 180);
    private static final Color BUTTON_GREEN = new Color(80, 120, 80);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color FIELD_BG = new Color(50, 50, 60);
    
    private static final String DATABASE_URL = "jdbc:mysql://localhost/theater";
    private static final String USER = "root";
    private static final String PASS = "OwenKhent098850";
    
    private int userID = 1;
    private String username = "Guest";
    private int loyaltyPoints = 0;
    
    private String[] items = {"Small Popcorn", "Large Popcorn", "Small Soda", "Large Soda", "Nachos", "Hot Dog", "Candy Bar", "Water"};
    private int[] prices = {8, 12, 5, 7, 10, 8, 4, 3};
    private JCheckBox[] itemCheckboxes;
    private JTextField[] quantityFields;
    private JLabel totalLabel;
    private JLabel pointsLabel;
    private JCheckBox usePointsCheckBox;
    
    public FoodOrderUI() {
        this(1, "Guest");
    }
    /**
     * @param userID The userID of the logged-in user
     * @param username The username of the logged-in user
    */
    public FoodOrderUI(int userID, String username) {
        this.userID = userID;
        this.username = username;
        getContentPane().setBackground(DARK_BG);
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.setBackground(DARK_BG);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("CONCESSION STAND");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(GOLD);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);
        
        loadLoyaltyPoints();
        
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(DARK_BG);
        pointsLabel = new JLabel("Your Points: " + loyaltyPoints);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pointsLabel.setForeground(GOLD);
        infoPanel.add(pointsLabel);
        add(infoPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        centerPanel.setBackground(DARK_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel hItem = new JLabel("Item");
        hItem.setFont(new Font("Arial", Font.BOLD, 14));
        hItem.setForeground(GOLD);
        centerPanel.add(hItem);
        
        JLabel hPrice = new JLabel("Price");
        hPrice.setFont(new Font("Arial", Font.BOLD, 14));
        hPrice.setForeground(GOLD);
        centerPanel.add(hPrice);
        
        JLabel hQty = new JLabel("Qty");
        hQty.setFont(new Font("Arial", Font.BOLD, 14));
        hQty.setForeground(GOLD);
        centerPanel.add(hQty);
        
        itemCheckboxes = new JCheckBox[items.length];
        quantityFields = new JTextField[items.length];
        
        for (int i = 0; i < items.length; i++) {
            itemCheckboxes[i] = new JCheckBox(items[i]);
            itemCheckboxes[i].setFont(new Font("Arial", Font.PLAIN, 13));
            itemCheckboxes[i].setForeground(TEXT_COLOR);
            itemCheckboxes[i].setBackground(DARK_BG);
            centerPanel.add(itemCheckboxes[i]);
            
            JLabel priceLabel = new JLabel("$" + prices[i]);
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            priceLabel.setForeground(TEXT_COLOR);
            centerPanel.add(priceLabel);
            
            quantityFields[i] = new JTextField("0", 5);
            quantityFields[i].setFont(new Font("Arial", Font.PLAIN, 13));
            quantityFields[i].setBackground(FIELD_BG);
            quantityFields[i].setForeground(TEXT_COLOR);
            quantityFields[i].setBorder(BorderFactory.createLineBorder(GOLD, 1));
            centerPanel.add(quantityFields[i]);
        }
        
        usePointsCheckBox = new JCheckBox("Use Loyalty Points (100 pts = $5 off)");
        usePointsCheckBox.setFont(new Font("Arial", Font.PLAIN, 12));
        usePointsCheckBox.setForeground(TEXT_COLOR);
        usePointsCheckBox.setBackground(DARK_BG);
        usePointsCheckBox.addActionListener(e -> updateTotal());
        centerPanel.add(new JLabel());
        centerPanel.add(usePointsCheckBox);
        centerPanel.add(new JLabel());
        
        totalLabel = new JLabel("Total: $0");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(GOLD);
        centerPanel.add(new JLabel());
        centerPanel.add(totalLabel);
        centerPanel.add(new JLabel());
        
        add(centerPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(DARK_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton calculateButton = createStyledButton("Calculate", BUTTON_BLUE);
        calculateButton.addActionListener(e -> updateTotal());
        bottomPanel.add(calculateButton);
        
        JButton orderButton = createStyledButton("Place Order", BUTTON_GREEN);
        orderButton.addActionListener(e -> placeOrder());
        bottomPanel.add(orderButton);
        
        JButton backButton = createStyledButton("Back", new Color(120, 80, 80));
        backButton.addActionListener(e -> { dispose(); new HomeScreen(username, userID); });
        bottomPanel.add(backButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        setSize(450, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocation(450, 150);
        setVisible(true);
    }
    // Helper method to create styled buttons with hover effects
    /**
     * @param text The text to display on the button
     * @param bgColor The background color of the button
     * @return A styled JButton with the specified text and background color, including hover effects
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(GOLD);
                btn.setForeground(DARK_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
                btn.setForeground(TEXT_COLOR);
            }
        });
        return btn;
    }
    // This method loads the user's loyalty points from the database and updates the pointsLabel.
    private void loadLoyaltyPoints() {
        String query = "SELECT loyaltyPoints FROM users WHERE userID = ?";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS);
            PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                loyaltyPoints = rs.getInt("loyaltyPoints");
                if (rs.wasNull()) loyaltyPoints = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // This method calculates the total price based on selected items, quantities, and applied loyalty points, then updates the totalLabel.
    private void updateTotal() {
        int total = 0;
        for (int i = 0; i < items.length; i++) {
            if (itemCheckboxes[i].isSelected()) {
                try {
                    int qty = Integer.parseInt(quantityFields[i].getText());
                    if (qty > 0) {
                        total += prices[i] * qty;
                    }
                } catch (NumberFormatException e) {
                    quantityFields[i].setText("0");
                }
            }
        }
        
        int discount = 0;
        if (usePointsCheckBox.isSelected() && loyaltyPoints >= 100) {
            discount = (loyaltyPoints / 100) * 5;
            if (discount > total) discount = total;
            total -= discount;
        }
        
        totalLabel.setText("Total: $" + total + (discount > 0 ? " (saved $" + discount + ")" : ""));
    }
    
    private void placeOrder() {
        int total = 0;
        int totalItems = 0;
        for (int i = 0; i < items.length; i++) {
            if (itemCheckboxes[i].isSelected()) {
                try {
                    int qty = Integer.parseInt(quantityFields[i].getText());
                    if (qty > 0) {
                        total += prices[i] * qty;
                        totalItems += qty;
                    }
                } catch (NumberFormatException e) {
                    quantityFields[i].setText("0");
                }
            }
        }
        
        if (totalItems == 0) {
            JOptionPane.showMessageDialog(this, "Please select items first.");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (itemCheckboxes[i].isSelected()) {
                try {
                    int qty = Integer.parseInt(quantityFields[i].getText());
                    if (qty > 0) {
                        if (details.length() > 0) details.append(", ");
                        details.append(qty).append("x ").append(items[i]);
                    }
                } catch (NumberFormatException e) {}
            }
        }
        
        int pointsUsed = 0;
        if (usePointsCheckBox.isSelected() && loyaltyPoints >= 100) {
            pointsUsed = (loyaltyPoints / 100) * 100;
            total -= pointsUsed / 20;
        }
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASS)) {
            conn.setAutoCommit(false);
            
            String purchaseQuery = "INSERT INTO purchase (userID, totalPrice, purchaseDate) VALUES (?, ?, NOW())";
            int purchaseID;
            try (PreparedStatement ps = conn.prepareStatement(purchaseQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userID);
                ps.setInt(2, total);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                purchaseID = rs.getInt(1);
            }
            
            if (pointsUsed > 0) {
                String updatePoints = "UPDATE users SET loyaltyPoints = COALESCE(loyaltyPoints, 0) - ? WHERE userID = ?";
                try (PreparedStatement ps = conn.prepareStatement(updatePoints)) {
                    ps.setInt(1, pointsUsed);
                    ps.setInt(2, userID);
                    ps.executeUpdate();
                }
            }
            
            conn.commit();
            
            JOptionPane.showMessageDialog(this, 
                "Order placed!\nItems: " + details + "\nTotal: $" + total + 
                (pointsUsed > 0 ? "\nPoints used: " + pointsUsed : ""));
            dispose();
            new HomeScreen(username, userID);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        new FoodOrderUI();
    }
}