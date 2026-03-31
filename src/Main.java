import view.LoginFrame;

import javax.swing.*;

/**
 * Main entry point for the Vehicle Rental System.
 * Initializes the application and shows the login frame.
 */
public class Main {
    
    /**
     * Main method - Application entry point.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel to system default for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }
        
        // Create and show the login frame on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}

