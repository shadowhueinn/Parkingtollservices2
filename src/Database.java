import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Database {
    public Database() {
        createDatabaseAndTables();
        createDefaultAdmin();
        createDefaultParkingSlots();
    }

    private void createDatabaseAndTables() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:parking.db")) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS tbl_vehicle");
            stmt.executeUpdate("DROP TABLE IF EXISTS tbl_transaction");
            stmt.executeUpdate("DROP TABLE IF EXISTS tbl_user");
            stmt.executeUpdate("DROP TABLE IF EXISTS tbl_parking_slot");
            stmt.executeUpdate("DROP TABLE IF EXISTS tbl_payment");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tbl_vehicle (" +
                    "v_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "v_plate TEXT NOT NULL, " +
                    "v_type TEXT NOT NULL, " +
                    "v_owner TEXT NOT NULL, " +
                    "v_status TEXT NOT NULL)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tbl_transaction (" +
                    "t_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "v_id INTEGER NOT NULL, " +
                    "t_entry TIMESTAMP NOT NULL, " +
                    "t_exit TIMESTAMP, " +
                    "t_fee REAL NOT NULL DEFAULT 50.0, " + // Default fee set to 50 pesos
                    "t_status TEXT NOT NULL, " +
                    "s_id INTEGER, " +
                    "FOREIGN KEY (v_id) REFERENCES tbl_vehicle(v_id), " +
                    "FOREIGN KEY (s_id) REFERENCES tbl_parking_slot(s_id))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tbl_user (" +
                    "u_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "u_name TEXT NOT NULL UNIQUE, " +
                    "u_pass TEXT NOT NULL, " +
                    "u_type TEXT NOT NULL CHECK(u_type IN ('Admin', 'Customer')), " +
                    "u_status TEXT NOT NULL CHECK(u_status IN ('Pending', 'Active')))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tbl_parking_slot (" +
                    "s_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "s_number TEXT NOT NULL UNIQUE, " +
                    "s_status TEXT NOT NULL, " +
                    "v_id INTEGER, " +
                    "FOREIGN KEY (v_id) REFERENCES tbl_vehicle(v_id))");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tbl_payment (" +
                    "p_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "t_id INTEGER NOT NULL, " +
                    "p_amount REAL NOT NULL, " +
                    "p_method TEXT NOT NULL, " +
                    "p_date TIMESTAMP NOT NULL, " +
                    "FOREIGN KEY (t_id) REFERENCES tbl_transaction(t_id))");

            System.out.println("Database and tables created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating database and tables: " + e.getMessage());
        }
    }

    private void createDefaultAdmin() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:parking.db")) {
            String checkSql = "SELECT COUNT(*) FROM tbl_user WHERE u_name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, "admin");
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Default admin already exists.");
                return;
            }

            String hashedPass = hashPassword("admin123");
            if (hashedPass == null) {
                System.out.println("Failed to hash default admin password.");
                return;
            }

            String insertSql = "INSERT INTO tbl_user (u_name, u_pass, u_type, u_status) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, "admin");
            insertStmt.setString(2, hashedPass);
            insertStmt.setString(3, "Admin");
            insertStmt.setString(4, "Active");
            insertStmt.executeUpdate();

            System.out.println("Default admin created: Name = admin, Password = admin123");
        } catch (SQLException e) {
            System.out.println("Error creating default admin: " + e.getMessage());
        }
    }

    private void createDefaultParkingSlots() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:parking.db")) {
            String checkSql = "SELECT COUNT(*) FROM tbl_parking_slot";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) >= 50) {
                System.out.println("50 or more parking slots already exist.");
                return;
            }

            String insertSql = "INSERT INTO tbl_parking_slot (s_number, s_status, v_id) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            for (int i = 1; i <= 50; i++) {
                pstmt.setString(1, "Slot-" + String.format("%03d", i)); // e.g., Slot-001, Slot-002, ...
                pstmt.setString(2, "Available");
                pstmt.setObject(3, null); // No vehicle initially
                pstmt.executeUpdate();
            }
            System.out.println("50 default parking slots created successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating default parking slots: " + e.getMessage());
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error hashing password: " + e.getMessage());
            return null;
        }
    }
}