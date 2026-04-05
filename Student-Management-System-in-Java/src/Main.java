import javax.swing.SwingUtilities;

/**
 * Main entry point for the Student Management System.
 * Launches the GUI on the Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // Run GUI creation on the Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> {
            AppGUI window = new AppGUI();
            window.setVisible(true);
        });
    }
}
