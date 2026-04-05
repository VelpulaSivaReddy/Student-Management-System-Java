import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

/**
 * AppGUI — the main window of the Student Management System.
 *
 * IMPROVEMENTS OVER THE ORIGINAL:
 *   1. Uses PreparedStatement everywhere → prevents SQL injection attacks
 *   2. Added DELETE student functionality
 *   3. Input validation before Add (no blank required fields)
 *   4. GPA range validation (0.0 – 10.0)
 *   5. Connections are closed after each operation (try-with-resources)
 *   6. Styled table with alternating row colors and a header
 *   7. GridBagLayout for a cleaner, aligned form
 *   8. Clear Fields button to reset the form quickly
 *   9. Status bar at the bottom showing feedback messages
 *  10. Proper Swing EDT launching (done in Main.java)
 *
 * ARCHITECTURE:
 *   AppGUI (this file)  →  dbConnect  →  MySQL
 *                       →  Table      ←  MySQL (ResultSet)
 */
public class AppGUI extends JFrame {

    // ── Form input fields ─────────────────────────────────────────────────
    private final JTextField studentIdField   = new JTextField(12);
    private final JTextField firstNameField   = new JTextField(12);
    private final JTextField lastNameField    = new JTextField(12);
    private final JTextField majorField       = new JTextField(12);
    private final JTextField phoneField       = new JTextField(12);
    private final JTextField gpaField         = new JTextField(12);
    private final JTextField dobField         = new JTextField(12);

    // ── Status bar label ──────────────────────────────────────────────────
    private final JLabel statusBar = new JLabel("  Ready");

    // ── Database helper ───────────────────────────────────────────────────
    private final dbConnect db = new dbConnect();

    // ═════════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR — builds and wires the entire UI
    // ═════════════════════════════════════════════════════════════════════
    public AppGUI() {
        super("🎓 Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(520, 500));

        // ── Root layout ───────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 246, 250));

        // ── Title banner ──────────────────────────────────────────────────
        JPanel banner = new JPanel();
        banner.setBackground(new Color(52, 73, 94));
        banner.setBorder(new EmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Student Management System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        banner.add(title);

        // ── Form panel ────────────────────────────────────────────────────
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(16, 16, 8, 16),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 230), 1, true),
                new EmptyBorder(16, 20, 16, 20)
            )
        ));

        GridBagConstraints lc = new GridBagConstraints(); // label constraints
        lc.anchor = GridBagConstraints.EAST;
        lc.insets = new Insets(6, 8, 6, 8);

        GridBagConstraints fc = new GridBagConstraints(); // field constraints
        fc.fill   = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = new Insets(6, 0, 6, 8);

        String[][] fields = {
            {"Student ID *",          "e.g.  S001"},
            {"First Name *",          "e.g.  Sunnyth"},
            {"Last Name *",           "e.g.  Sheelam"},
            {"Major *",               "e.g.  AIML"},
            {"Phone",                 "e.g.  9876543210"},
            {"GPA  (0.0 – 10.0)",     "e.g.  8.5"},
            {"Date of Birth",         "yyyy-mm-dd"}
        };
        JTextField[] inputs = {
            studentIdField, firstNameField, lastNameField,
            majorField, phoneField, gpaField, dobField
        };

        for (int i = 0; i < fields.length; i++) {
            lc.gridx = 0; lc.gridy = i;
            fc.gridx = 1; fc.gridy = i;

            JLabel lbl = new JLabel(fields[i][0]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            lbl.setForeground(new Color(60, 60, 80));
            formCard.add(lbl, lc);

            inputs[i].setFont(new Font("SansSerif", Font.PLAIN, 13));
            inputs[i].setToolTipText(fields[i][1]);
            inputs[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210), 1, true),
                new EmptyBorder(4, 8, 4, 8)
            ));
            formCard.add(inputs[i], fc);
        }

        // ── Button panel ──────────────────────────────────────────────────
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        buttonPanel.setBackground(new Color(245, 246, 250));

        String[][] buttons = {
            {"Add",     "#27ae60"},
            {"Display", "#2980b9"},
            {"Sort",    "#8e44ad"},
            {"Search",  "#e67e22"},
            {"Modify",  "#c0392b"},
            {"Delete",  "#7f8c8d"},
            {"Clear",   "#95a5a6"}
        };

        for (String[] btnDef : buttons) {
            JButton btn = makeButton(btnDef[0], btnDef[1]);
            buttonPanel.add(btn);

            switch (btnDef[0]) {
                case "Add"     -> btn.addActionListener(e -> handleAdd());
                case "Display" -> btn.addActionListener(e -> handleDisplay());
                case "Sort"    -> btn.addActionListener(e -> handleSort());
                case "Search"  -> btn.addActionListener(e -> handleSearch());
                case "Modify"  -> btn.addActionListener(e -> handleModify());
                case "Delete"  -> btn.addActionListener(e -> handleDelete());
                case "Clear"   -> btn.addActionListener(e -> clearFields());
            }
        }

        // ── Status bar ────────────────────────────────────────────────────
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setForeground(new Color(80, 80, 100));
        statusBar.setPreferredSize(new Dimension(0, 28));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 210, 220)),
            new EmptyBorder(0, 12, 0, 0)
        ));
        statusBar.setBackground(new Color(240, 241, 245));
        statusBar.setOpaque(true);

        // ── Assemble root ─────────────────────────────────────────────────
        root.add(banner,       BorderLayout.NORTH);
        root.add(formCard,     BorderLayout.CENTER);
        root.add(buttonPanel,  BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(root,      BorderLayout.CENTER);
        wrapper.add(statusBar, BorderLayout.SOUTH);
        setContentPane(wrapper);

        pack();
        setLocationRelativeTo(null); // center on screen
    }

    // ═════════════════════════════════════════════════════════════════════
    //  BUTTON HANDLERS
    // ═════════════════════════════════════════════════════════════════════

    /** INSERT a new student record. Uses PreparedStatement to prevent SQL injection. */
    private void handleAdd() {
        // ── Validate required fields ──────────────────────────────────────
        if (studentIdField.getText().isBlank() || firstNameField.getText().isBlank()
                || lastNameField.getText().isBlank() || majorField.getText().isBlank()) {
            setStatus("⚠  Please fill in all required fields (marked with *).", Color.RED);
            return;
        }

        // ── Validate GPA if provided ──────────────────────────────────────
        String gpaText = gpaField.getText().trim();
        if (!gpaText.isEmpty()) {
            try {
                double gpa = Double.parseDouble(gpaText);
                if (gpa < 0 || gpa > 10) {
                    setStatus("⚠  GPA must be between 0.0 and 10.0.", Color.RED);
                    return;
                }
            } catch (NumberFormatException ex) {
                setStatus("⚠  GPA must be a number (e.g. 8.5).", Color.RED);
                return;
            }
        }

        String sql = "INSERT INTO sdata (student_id, first_name, last_name, major, phone, gpa, dob) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentIdField.getText().trim());
            ps.setString(2, firstNameField.getText().trim());
            ps.setString(3, lastNameField.getText().trim());
            ps.setString(4, majorField.getText().trim());
            ps.setString(5, phoneField.getText().trim());
            ps.setString(6, gpaText.isEmpty() ? null : gpaText);
            ps.setString(7, dobField.getText().trim().isEmpty() ? null : dobField.getText().trim());

            ps.executeUpdate();
            setStatus("✓  Student added successfully.", new Color(30, 130, 60));
            clearFields();

        } catch (ClassNotFoundException ex) {
            showError("MySQL driver not found. Make sure mysql-connector JAR is in /lib.");
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) { // Duplicate entry
                setStatus("⚠  A student with this ID already exists.", Color.RED);
            } else {
                showError("Database error: " + ex.getMessage());
            }
        }
    }

    /** SELECT * to display all students. */
    private void handleDisplay() {
        String sql = "SELECT * FROM sdata ORDER BY student_id";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            showTable(rs, "All Students");
            setStatus("✓  Displaying all students.", new Color(30, 100, 160));

        } catch (Exception ex) {
            showError("Could not load students: " + ex.getMessage());
        }
    }

    /** Prompt user for a sort column, then SELECT with ORDER BY. */
    private void handleSort() {
        String[] options = {"First Name", "Last Name", "Major", "GPA (High→Low)", "Student ID"};
        int choice = JOptionPane.showOptionDialog(this, "Sort students by:", "Sort",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice < 0) return; // user cancelled

        String orderCol = switch (choice) {
            case 0 -> "first_name";
            case 1 -> "last_name";
            case 2 -> "major";
            case 3 -> "gpa DESC";
            case 4 -> "student_id";
            default -> "student_id";
        };

        String sql = "SELECT * FROM sdata ORDER BY " + orderCol;
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            showTable(rs, "Sorted by: " + options[choice]);
            setStatus("✓  Sorted by " + options[choice] + ".", new Color(100, 50, 160));

        } catch (Exception ex) {
            showError("Could not sort: " + ex.getMessage());
        }
    }

    /** Prompt for a search column and term, then SELECT with WHERE LIKE. */
    private void handleSearch() {
        String[] options = {"Student ID", "Last Name", "Major", "First Name"};
        int choice = JOptionPane.showOptionDialog(this, "Search by:", "Search",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice < 0) return;

        String searchTerm = JOptionPane.showInputDialog(this, "Enter search term:");
        if (searchTerm == null || searchTerm.isBlank()) return;

        String column = switch (choice) {
            case 0 -> "student_id";
            case 1 -> "last_name";
            case 2 -> "major";
            case 3 -> "first_name";
            default -> "student_id";
        };

        // PreparedStatement with LIKE — safe against SQL injection
        String sql = "SELECT * FROM sdata WHERE " + column + " LIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + searchTerm + "%");
            ResultSet rs = ps.executeQuery();

            showTable(rs, "Search results — " + options[choice] + " contains \"" + searchTerm + "\"");
            setStatus("✓  Search complete.", new Color(180, 90, 0));

        } catch (Exception ex) {
            showError("Search failed: " + ex.getMessage());
        }
    }

    /** Prompt for student ID, then let user pick a field to update via PreparedStatement. */
    private void handleModify() {
        String studentId = JOptionPane.showInputDialog(this, "Enter Student ID to modify:");
        if (studentId == null || studentId.isBlank()) return;

        // First check the student exists
        try (Connection conn = db.getConnection();
             PreparedStatement check = conn.prepareStatement(
                     "SELECT student_id FROM sdata WHERE student_id = ?")) {

            check.setString(1, studentId.trim());
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                setStatus("⚠  No student found with ID: " + studentId.trim(), Color.RED);
                return;
            }

        } catch (Exception ex) {
            showError("Could not look up student: " + ex.getMessage());
            return;
        }

        // Ask which field to modify
        String[] fieldOptions = {"First Name", "Last Name", "Major", "Phone", "GPA", "Date of Birth"};
        int choice = JOptionPane.showOptionDialog(this, "Select field to modify:", "Modify",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, fieldOptions, fieldOptions[0]);

        if (choice < 0) return;

        String newValue = JOptionPane.showInputDialog(this, "Enter new value for " + fieldOptions[choice] + ":");
        if (newValue == null || newValue.isBlank()) return;

        String column = switch (choice) {
            case 0 -> "first_name";
            case 1 -> "last_name";
            case 2 -> "major";
            case 3 -> "phone";
            case 4 -> "gpa";
            case 5 -> "dob";
            default -> "first_name";
        };

        String sql = "UPDATE sdata SET " + column + " = ? WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newValue.trim());
            ps.setString(2, studentId.trim());
            ps.executeUpdate();
            setStatus("✓  Student " + studentId.trim() + " updated successfully.", new Color(30, 130, 60));

        } catch (Exception ex) {
            showError("Update failed: " + ex.getMessage());
        }
    }

    /** Delete a student by ID — asks for confirmation first. */
    private void handleDelete() {
        String studentId = JOptionPane.showInputDialog(this, "Enter Student ID to delete:");
        if (studentId == null || studentId.isBlank()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete student ID: " + studentId.trim() + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM sdata WHERE student_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentId.trim());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                setStatus("✓  Student " + studentId.trim() + " deleted.", new Color(100, 100, 100));
            } else {
                setStatus("⚠  No student found with ID: " + studentId.trim(), Color.RED);
            }

        } catch (Exception ex) {
            showError("Delete failed: " + ex.getMessage());
        }
    }

    /** Clears all input fields and resets the status bar. */
    private void clearFields() {
        studentIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        majorField.setText("");
        phoneField.setText("");
        gpaField.setText("");
        dobField.setText("");
        setStatus("  Ready", new Color(80, 80, 100));
        studentIdField.requestFocus();
    }

    // ═════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ═════════════════════════════════════════════════════════════════════

    /** Displays a ResultSet in a styled popup JTable dialog. */
    private void showTable(ResultSet rs, String title) throws SQLException {
        Table tb = new Table();
        JTable table = new JTable(tb.buildTableModel(rs));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(26);
        table.setGridColor(new Color(220, 220, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Alternating row colours
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 252));
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // Style the header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(52, 73, 94));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(780, 340));

        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }

    /** Updates the status bar with a message and color. */
    private void setStatus(String message, Color color) {
        statusBar.setText("  " + message);
        statusBar.setForeground(color);
    }

    /** Shows an error dialog and updates the status bar. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        setStatus("✗  " + message, Color.RED);
    }

    /** Creates a styled button with the given label and hex background color. */
    private JButton makeButton(String label, String hexColor) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(hexColor));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 34));

        // Hover effect — slightly darken the button
        Color base  = Color.decode(hexColor);
        Color hover = base.darker();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(base);  }
        });

        return btn;
    }
}
