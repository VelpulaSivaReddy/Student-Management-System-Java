import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Utility class that converts a SQL ResultSet into a Swing DefaultTableModel,
 * which can then be used directly to populate a JTable in the GUI.
 *
 * HOW IT WORKS:
 *   1. Reads column names from ResultSetMetaData
 *   2. Iterates over every row in the ResultSet
 *   3. Collects all data into Vectors (dynamic arrays)
 *   4. Packages everything into a DefaultTableModel
 *
 * The returned model is NON-EDITABLE — cells cannot be changed by clicking
 * in the popup table (data is only modified through the Modify button).
 */
public class Table {

    /**
     * Builds a read-only DefaultTableModel from the given ResultSet.
     *
     * @param rs the ResultSet returned by a SQL SELECT query
     * @return a DefaultTableModel ready to be passed to new JTable(model)
     * @throws SQLException if reading the ResultSet fails
     */
    public DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // ── Step 1: collect column names ──────────────────────────────────
        Vector<String> columnNames = new Vector<>();
        for (int col = 1; col <= columnCount; col++) {
            columnNames.add(metaData.getColumnName(col));
        }

        // ── Step 2: collect row data ──────────────────────────────────────
        Vector<Vector<Object>> rowData = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int col = 1; col <= columnCount; col++) {
                row.add(rs.getObject(col));
            }
            rowData.add(row);
        }

        // ── Step 3: return a non-editable model ───────────────────────────
        return new DefaultTableModel(rowData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // prevent accidental edits inside the popup table
            }
        };
    }
}
