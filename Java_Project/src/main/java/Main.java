import javax.swing.JFrame;

/**
 * Main entry point for the theater booking application.
 * Initializes the main menu for user login.
 */
public class Main
{
    public static void main( String [] args)
    {
        MainMenu textFieldFrame = new MainMenu();
        textFieldFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
