import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the MySQL database connection for the Student Management System.
 *
 * HOW TO CONFIGURE:
 *   1. Set DB_NAME to your MySQL database name (default: studentdb)
 *   2. Set DB_USER to your MySQL username (default: root)
 *   3. Set DB_PASS to your MySQL password
 *
 * REQUIREMENTS:
 *   - MySQL server must be running on localhost:3306
 *   - The database must already exist (run student_data.sql first)
 *   - mysql-connector-j-9.5.0.jar must be on the classpath (it's in /lib)
 */
public class dbConnect {

    // ─── UPDATE THESE TO MATCH YOUR MYSQL SETUP ───────────────────────────
    private static final String DB_HOST = "localhost";
    private static final int    DB_PORT = 3306;
    private static final String DB_NAME = "studentdb";   // your database name
    private static final String DB_USER = "root";         // your MySQL username
    private static final String DB_PASS = "your_password_here"; // your MySQL password
    // ───────────────────────────────────────────────────────────────────────

    private static final String JDBC_URL =
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
        + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    /**
     * Opens and returns a new Connection to the MySQL database.
     *
     * @return a live java.sql.Connection
     * @throws ClassNotFoundException if the MySQL JDBC driver JAR is missing from /lib
     * @throws SQLException           if credentials are wrong or the database is unreachable
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
    }
}
