import java.sql.*;

public class Config {
    private static final String DB_URL = "jdbc:sqlite:parking.db";
    private Connection conn;

    public Config() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
            createDefaultAdmin();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // Close connection
    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        // Admin table
        stmt.execute("CREATE TABLE IF NOT EXISTS admin (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL)");

        // Users table
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "is_approved INTEGER DEFAULT 0)");

        // Parking slots table
        stmt.execute("CREATE TABLE IF NOT EXISTS parking_slots (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "slot_number INTEGER UNIQUE," +
                "is_occupied INTEGER DEFAULT 0)");

        // Initialize 50 parking slots if not exist
        for (int i = 1; i <= 50; i++) {
            stmt.execute("INSERT OR IGNORE INTO parking_slots(slot_number, is_occupied) VALUES(" + i + ", 0)");
        }

        stmt.close();
    }

    private void createDefaultAdmin() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO admin(username, password) VALUES(?, ?)"
        );
        pstmt.setString(1, "admin");  // default username
        pstmt.setString(2, "admin123"); // default password
        pstmt.executeUpdate();
        pstmt.close();
    }

    // Admin login
    public boolean adminLogin(String username, String password) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT * FROM admin WHERE username=? AND password=?"
            );
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            boolean success = rs.next();
            rs.close();
            pstmt.close();
            return success;
        } catch (SQLException e) {
            System.out.println("Admin login error: " + e.getMessage());
            return false;
        }
    }

    // Register user
    public boolean registerUser(String name, String email, String password) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO users(name, email, password) VALUES(?, ?, ?)"
            );
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            pstmt.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    // Approve user
    public void approveUser(int userId) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET is_approved=1 WHERE id=?"
            );
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
        }
    }

    // List unapproved users
    public void listPendingUsers() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE is_approved=0");
            System.out.println("--- Pending Users ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") +
                        " | Email: " + rs.getString("email"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error listing users: " + e.getMessage());
        }
    }

    // Park a car
    public int parkCar() {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT slot_number FROM parking_slots WHERE is_occupied=0 LIMIT 1"
            );
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int slot = rs.getInt("slot_number");
                PreparedStatement update = conn.prepareStatement(
                        "UPDATE parking_slots SET is_occupied=1 WHERE slot_number=?"
                );
                update.setInt(1, slot);
                update.executeUpdate();
                update.close();
                rs.close();
                pstmt.close();
                return slot;
            } else {
                rs.close();
                pstmt.close();
                return -1; // no slots available
            }
        } catch (SQLException e) {
            System.out.println("Error parking car: " + e.getMessage());
            return -1;
        }
    }

    // Exit car and pay
    public void exitCar(int slotNumber) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE parking_slots SET is_occupied=0 WHERE slot_number=?"
            );
            pstmt.setInt(1, slotNumber);
            pstmt.executeUpdate();
            pstmt.close();
            System.out.println("Payment processed for slot " + slotNumber + ". Thank you!");
        } catch (SQLException e) {
            System.out.println("Error exiting car: " + e.getMessage());
        }
    }
}
