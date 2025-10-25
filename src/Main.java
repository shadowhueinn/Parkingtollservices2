import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        new Database();
        Config config = new Config();
        Admin admin = new Admin();
        Customer customer = new Customer();

        while (true) {
            System.out.println("\n=== Parking Toll Services ===");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as Customer");
            System.out.println("3. Register Customer");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 4.");
                sc.nextLine(); // Clear invalid input
                continue;
            }

            if (choice == 1) {
                if (admin.login()) {
                    adminMenu(admin, sc);
                }
            } else if (choice == 2) {
                if (customer.login()) {
                    customerMenu(customer, sc);
                }
            } else if (choice == 3) {
                registerCustomer(config, sc);
            } else if (choice == 4) {
                System.out.println("Exiting system. Goodbye!");
                break;
            } else {
                System.out.println("Invalid option. Please enter a number between 1 and 4.");
            }
        }
        sc.close();
    }

    private static void registerCustomer(Config config, Scanner sc) {
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        String hashedPass = Config.hashPassword(pass);
        String sql = "INSERT INTO tbl_user (u_name, u_pass, u_type, u_status) VALUES (?, ?, ?, ?)";
        config.addRecord(sql, name, hashedPass, "Customer", "Pending");
        System.out.println("Customer registered successfully! Awaiting admin approval.");
    }

    private static void adminMenu(Admin admin, Scanner sc) {
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Approve Customer");
            System.out.println("2. View Pending Customers");
            System.out.println("3. Add Vehicle");
            System.out.println("4. Add Transaction");
            System.out.println("5. Add Parking Slot");
            System.out.println("6. Add Payment");
            System.out.println("7. Update Vehicle");
            System.out.println("8. Update Transaction");
            System.out.println("9. Update Parking Slot");
            System.out.println("10. Update Payment");
            System.out.println("11. Delete Vehicle");
            System.out.println("12. Delete Transaction");
            System.out.println("13. Delete Parking Slot");
            System.out.println("14. Delete Payment");
            System.out.println("15. View Vehicles");
            System.out.println("16. View Transactions");
            System.out.println("17. View Parking Slots");
            System.out.println("18. View Payments");
            System.out.println("19. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline
                System.out.println("Selected option: " + choice); // Debug output
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 19.");
                sc.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("Executing Approve Customer...");
                    admin.approveCustomer();
                    break;
                case 2:
                    System.out.println("Executing View Pending Customers...");
                    admin.viewPendingCustomers();
                    break;
                case 3:
                    System.out.println("Executing Add Vehicle...");
                    admin.addVehicle();
                    break;
                case 4:
                    System.out.println("Executing Add Transaction...");
                    admin.addTransaction();
                    break;
                case 5:
                    System.out.println("Executing Add Parking Slot...");
                    admin.addParkingSlot();
                    break;
                case 6:
                    System.out.println("Executing Add Payment...");
                    admin.addPayment();
                    break;
                case 7:
                    System.out.println("Executing Update Vehicle...");
                    admin.updateVehicle();
                    break;
                case 8:
                    System.out.println("Executing Update Transaction...");
                    admin.updateTransaction();
                    break;
                case 9:
                    System.out.println("Executing Update Parking Slot...");
                    admin.updateParkingSlot();
                    break;
                case 10:
                    System.out.println("Executing Update Payment...");
                    admin.updatePayment();
                    break;
                case 11:
                    System.out.println("Executing Delete Vehicle...");
                    admin.deleteVehicle();
                    break;
                case 12:
                    System.out.println("Executing Delete Transaction...");
                    admin.deleteTransaction();
                    break;
                case 13:
                    System.out.println("Executing Delete Parking Slot...");
                    admin.deleteParkingSlot();
                    break;
                case 14:
                    System.out.println("Executing Delete Payment...");
                    admin.deletePayment();
                    break;
                case 15:
                    System.out.println("Executing View Vehicles...");
                    admin.viewVehicles();
                    break;
                case 16:
                    System.out.println("Executing View Transactions...");
                    admin.viewTransactions();
                    break;
                case 17:
                    System.out.println("Executing View Parking Slots...");
                    admin.viewParkingSlots();
                    break;
                case 18:
                    System.out.println("Executing View Payments...");
                    admin.viewPayments();
                    break;
                case 19:
                    System.out.println("Executing Logout...");
                    System.out.println("Logged out successfully.");
                    return; // Exit back to main menu
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 19.");
            }
            // Pause to allow user to see output before redisplaying menu
            System.out.print("Press Enter to continue...");
            sc.nextLine();
        }
    }

    private static void customerMenu(Customer customer, Scanner sc) {
        while (true) {
            System.out.println("\n=== Customer Menu ===");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice;
            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consume newline

        } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 2.");
                sc.nextLine(); // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1:
                    customer.parkVehicle();
                    break;
                case 2:
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid option. Please enter a number between 1 and 2.");
            }
        }
    }
}