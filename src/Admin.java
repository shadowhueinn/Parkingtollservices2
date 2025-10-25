import java.sql.Timestamp;
import java.util.*;

public class Admin {
    private Config config;

    public Admin() {
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
        if (!user.get("u_type").equals("Admin")) {
            System.out.println("Error: User is not an admin.");
            return false;
        }
        if (!user.get("u_status").equals("Active")) {
            System.out.println("Error: Admin account is not active.");
            return false;
        }

        System.out.println("Login successful! Welcome, Admin.");
        return true;
    }

    public void approveCustomer() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Customer Name to approve: ");
        String name = sc.nextLine();

        String sql = "UPDATE tbl_user SET u_status = 'Active' WHERE u_name = ? AND u_type = 'Customer' AND u_status = 'Pending'";
        config.updateRecord(sql, name);
    }

    public void viewPendingCustomers() {
        String sql = "SELECT u_name, u_type, u_status FROM tbl_user WHERE u_type = 'Customer' AND u_status = 'Pending'";
        String[] headers = {"Name", "Type", "Status"};
        String[] columns = {"u_name", "u_type", "u_status"};
        config.viewRecords(sql, headers, columns);
    }

    public void addVehicle() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Vehicle Plate Number: ");
        String plate = sc.nextLine();
        System.out.print("Vehicle Type (e.g., Car, Motorcycle): ");
        String type = sc.nextLine();
        System.out.print("Owner Name: ");
        String owner = sc.nextLine();
        System.out.print("Vehicle Status (e.g., Active, Inactive): ");
        String status = sc.nextLine();

        String sql = "INSERT INTO tbl_vehicle (v_plate, v_type, v_owner, v_status) VALUES (?, ?, ?, ?)";
        int vehicleId = config.addRecordAndReturnId(sql, plate, type, owner, status);
        if (vehicleId != -1) {
            System.out.println("Vehicle added with ID: " + vehicleId);
        }
    }

    public void addTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Vehicle ID: ");
        int vehicleId = sc.nextInt();
        sc.nextLine();
        System.out.print("Entry Time (yyyy-MM-dd HH:mm:ss): ");
        String entryStr = sc.nextLine();
        System.out.print("Exit Time (yyyy-MM-dd HH:mm:ss, or leave blank): ");
        String exitStr = sc.nextLine();
        System.out.print("Transaction Status (e.g., Ongoing, Completed): ");
        String status = sc.nextLine();
        System.out.print("Parking Slot ID: ");
        int slotId = sc.nextInt();
        sc.nextLine();

        Timestamp entryTime = Timestamp.valueOf(entryStr);
        Timestamp exitTime = exitStr.isEmpty() ? null : Timestamp.valueOf(exitStr);

        String sql = "INSERT INTO tbl_transaction (v_id, t_entry, t_exit, t_fee, t_status, s_id) VALUES (?, ?, ?, ?, ?, ?)";
        int transactionId = config.addRecordAndReturnId(sql, vehicleId, entryTime, 50.0, status, slotId); // Default 50 pesos
        if (transactionId != -1) {
            System.out.println("Transaction added with ID: " + transactionId);
        }
    }

    public void addParkingSlot() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Parking Slot Number: ");
        String slotNumber = sc.nextLine();
        System.out.print("Slot Status (e.g., Available, Occupied): ");
        String status = sc.nextLine();
        System.out.print("Vehicle ID (or leave blank if none): ");
        String vehicleIdStr = sc.nextLine();
        Integer vehicleId = vehicleIdStr.isEmpty() ? null : Integer.parseInt(vehicleIdStr);

        String sql = "INSERT INTO tbl_parking_slot (s_number, s_status, v_id) VALUES (?, ?, ?)";
        int slotId = config.addRecordAndReturnId(sql, slotNumber, status, vehicleId);
        if (slotId != -1) {
            System.out.println("Parking slot added with ID: " + slotId);
        }
    }

    public void addPayment() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Transaction ID: ");
        int transactionId = sc.nextInt();
        sc.nextLine();
        System.out.print("Payment Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Payment Method (e.g., Cash, Card): ");
        String method = sc.nextLine();
        System.out.print("Payment Date (yyyy-MM-dd HH:mm:ss): ");
        String dateStr = sc.nextLine();
        Timestamp paymentDate = Timestamp.valueOf(dateStr);

        String sql = "INSERT INTO tbl_payment (t_id, p_amount, p_method, p_date) VALUES (?, ?, ?, ?)";
        int paymentId = config.addRecordAndReturnId(sql, transactionId, amount, method, paymentDate);
        if (paymentId != -1) {
            System.out.println("Payment added with ID: " + paymentId);
        }
    }

    public void updateVehicle() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Vehicle ID to update: ");
        int vehicleId = sc.nextInt();
        sc.nextLine();
        System.out.print("New Plate Number: ");
        String plate = sc.nextLine();
        System.out.print("New Vehicle Type: ");
        String type = sc.nextLine();
        System.out.print("New Owner Name: ");
        String owner = sc.nextLine();
        System.out.print("New Status: ");
        String status = sc.nextLine();

        String sql = "UPDATE tbl_vehicle SET v_plate = ?, v_type = ?, v_owner = ?, v_status = ? WHERE v_id = ?";
        config.updateRecord(sql, plate, type, owner, status, vehicleId);
    }

    public void updateTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Transaction ID to update: ");
        int transactionId = sc.nextInt();
        sc.nextLine();
        System.out.print("New Exit Time (yyyy-MM-dd HH:mm:ss, or leave blank): ");
        String exitStr = sc.nextLine();
        System.out.print("New Parking Fee: ");
        double fee = sc.nextDouble();
        sc.nextLine();
        System.out.print("New Status: ");
        String status = sc.nextLine();
        System.out.print("New Parking Slot ID: ");
        int slotId = sc.nextInt();
        sc.nextLine();

        Timestamp exitTime = exitStr.isEmpty() ? null : Timestamp.valueOf(exitStr);
        String sql = "UPDATE tbl_transaction SET t_exit = ?, t_fee = ?, t_status = ?, s_id = ? WHERE t_id = ?";
        config.updateRecord(sql, exitTime, fee, status, slotId, transactionId);
    }

    public void updateParkingSlot() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Parking Slot ID to update: ");
        int slotId = sc.nextInt();
        sc.nextLine();
        System.out.print("New Slot Number: ");
        String slotNumber = sc.nextLine();
        System.out.print("New Slot Status: ");
        String status = sc.nextLine();
        System.out.print("New Vehicle ID (or leave blank): ");
        String vehicleIdStr = sc.nextLine();
        Integer vehicleId = vehicleIdStr.isEmpty() ? null : Integer.parseInt(vehicleIdStr);

        String sql = "UPDATE tbl_parking_slot SET s_number = ?, s_status = ?, v_id = ? WHERE s_id = ?";
        config.updateRecord(sql, slotNumber, status, vehicleId, slotId);
    }

    public void updatePayment() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Payment ID to update: ");
        int paymentId = sc.nextInt();
        sc.nextLine();
        System.out.print("New Payment Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("New Payment Method: ");
        String method = sc.nextLine();
        System.out.print("New Payment Date (yyyy-MM-dd HH:mm:ss): ");
        String dateStr = sc.nextLine();
        Timestamp paymentDate = Timestamp.valueOf(dateStr);

        String sql = "UPDATE tbl_payment SET p_amount = ?, p_method = ?, p_date = ? WHERE p_id = ?";
        config.updateRecord(sql, amount, method, paymentDate, paymentId);
    }

    public void deleteVehicle() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Vehicle ID to delete: ");
        int vehicleId = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_vehicle WHERE v_id = ?";
        config.deleteRecord(sql, vehicleId);
    }

    public void deleteTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Transaction ID to delete: ");
        int transactionId = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_transaction WHERE t_id = ?";
        config.deleteRecord(sql, transactionId);
    }

    public void deleteParkingSlot() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Parking Slot ID to delete: ");
        int slotId = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_parking_slot WHERE s_id = ?";
        config.deleteRecord(sql, slotId);
    }

    public void deletePayment() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Payment ID to delete: ");
        int paymentId = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM tbl_payment WHERE p_id = ?";
        config.deleteRecord(sql, paymentId);
    }

    public void viewVehicles() {
        String sql = "SELECT * FROM tbl_vehicle";
        String[] headers = {"ID", "Plate", "Type", "Owner", "Status"};
        String[] columns = {"v_id", "v_plate", "v_type", "v_owner", "v_status"};
        config.viewRecords(sql, headers, columns);
    }

    public void viewTransactions() {
        String sql = "SELECT * FROM tbl_transaction";
        String[] headers = {"ID", "Vehicle ID", "Entry", "Exit", "Fee", "Status", "Slot ID"};
        String[] columns = {"t_id", "v_id", "t_entry", "t_exit", "t_fee", "t_status", "s_id"};
        config.viewRecords(sql, headers, columns);
    }

    public void viewParkingSlots() {
        String sql = "SELECT * FROM tbl_parking_slot";
        String[] headers = {"ID", "Slot Number", "Status", "Vehicle ID"};
        String[] columns = {"s_id", "s_number", "s_status", "v_id"};
        config.viewRecords(sql, headers, columns);
    }

    public void viewPayments() {
        String sql = "SELECT * FROM tbl_payment";
        String[] headers = {"ID", "Transaction ID", "Amount", "Method", "Date"};
        String[] columns = {"p_id", "t_id", "p_amount", "p_method", "p_date"};
        config.viewRecords(sql, headers, columns);
    }
}