import java.sql.Timestamp;
import java.util.*;

public class Customer {
    private Config config;

    public Customer() {
        this.config = new Config();
    }

    public boolean login() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        String hashedPass = Config.hashPassword(pass);
        if (hashedPass == null) {
            System.out.println("Error: Password hashing failed.");
            return false;
        }

        String sql = "SELECT * FROM tbl_user WHERE u_name = ?";
        List<Map<String, Object>> result = config.fetchRecords(sql, name);

        if (result.isEmpty()) {
            System.out.println("Error: Name not found.");
            return false;
        }
        Map<String, Object> user = result.get(0);
        if (!user.get("u_pass").equals(hashedPass)) {
            System.out.println("Error: Incorrect password.");
            return false;
        }
        if (!user.get("u_type").equals("Customer")) {
            System.out.println("Error: User is not a customer.");
            return false;
        }
        if (!user.get("u_status").equals("Active")) {
            System.out.println("Error: Customer account is not active. Please wait for admin approval.");
            return false;
        }

        System.out.println("Login successful! Welcome, Customer.");
        return true;
    }

    public void parkVehicle() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Your Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Vehicle Plate Number: ");
        String plate = sc.nextLine();
        System.out.print("Enter Vehicle Type (e.g., Car, Motorcycle): ");
        String type = sc.nextLine();

        String sqlSlots = "SELECT s_id, s_number FROM tbl_parking_slot WHERE s_status = 'Available' AND v_id IS NULL";
        String[] headers = {"Slot ID", "Slot Number"};
        String[] columns = {"s_id", "s_number"};
        config.viewRecords(sqlSlots, headers, columns);

        System.out.print("Enter Slot ID to park in: ");
        int slotId = sc.nextInt();
        sc.nextLine();
        System.out.print("Entry Time (yyyy-MM-dd HH:mm:ss): ");
        String entryStr = sc.nextLine();
        System.out.print("Parking Fee: ");
        double fee = sc.nextDouble();
        sc.nextLine();

        Timestamp entryTime = Timestamp.valueOf(entryStr);

        String sqlVehicle = "INSERT INTO tbl_vehicle (v_plate, v_type, v_owner, v_status) VALUES (?, ?, ?, ?)";
        int vehicleId = config.addRecordAndReturnId(sqlVehicle, plate, type, name, "Active");
        if (vehicleId == -1) {
            System.out.println("Failed to add vehicle.");
            return;
        }

        String sqlTransaction = "INSERT INTO tbl_transaction (v_id, t_entry, t_fee, t_status, s_id) VALUES (?, ?, ?, ?, ?)";
        int transactionId = config.addRecordAndReturnId(sqlTransaction, vehicleId, entryTime, fee, "Ongoing", slotId);
        if (transactionId == -1) {
            System.out.println("Failed to add transaction.");
            return;
        }

        String sqlUpdateSlot = "UPDATE tbl_parking_slot SET s_status = 'Occupied', v_id = ? WHERE s_id = ?";
        config.updateRecord(sqlUpdateSlot, vehicleId, slotId);

        System.out.println("Vehicle parked successfully! Transaction ID: " + transactionId);
    }
}