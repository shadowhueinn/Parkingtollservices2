import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Config config = new Config();

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Register");
            System.out.println("2. Login as Admin");
            System.out.println("3. Park a Car");
            System.out.println("4. Exit a Car (Payment)");
            System.out.println("5. Exit Program");
            System.out.print("Choose option: ");

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("--- USER REGISTER ---");
                    System.out.print("Name: ");
                    String name = sc.nextLine();
                    System.out.print("Email: ");
                    String email = sc.nextLine();
                    System.out.print("Password: ");
                    String password = sc.nextLine();
                    if (config.registerUser(name, email, password)) {
                        System.out.println("User registered successfully! Waiting for admin approval.");
                    }
                    break;
                case 2:
                    System.out.println("--- ADMIN LOGIN ---");
                    System.out.print("Username: ");
                    String username = sc.nextLine();
                    System.out.print("Password: ");
                    String adminPass = sc.nextLine();
                    if (config.adminLogin(username, adminPass)) {
                        System.out.println("Admin logged in successfully!");
                        adminMenu(config, sc);
                    } else {
                        System.out.println("Invalid admin credentials!");
                    }
                    break;
                case 3:
                    int slot = config.parkCar();
                    if (slot == -1) {
                        System.out.println("No parking slots available!");
                    } else {
                        System.out.println("Car parked at slot: " + slot);
                    }
                    break;
                case 4:
                    System.out.print("Enter slot number to exit: ");
                    int exitSlot = sc.nextInt();
                    sc.nextLine();
                    config.exitCar(exitSlot);
                    break;
                case 5:
                    config.close();
                    System.out.println("Exiting program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private static void adminMenu(Config config, Scanner sc) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Approve Users");
            System.out.println("2. List Pending Users");
            System.out.println("3. Back to Main Menu");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    config.listPendingUsers();
                    System.out.print("Enter User ID to approve: ");
                    int userId = sc.nextInt();
                    sc.nextLine();
                    config.approveUser(userId);
                    System.out.println("User approved!");
                    break;
                case 2:
                    config.listPendingUsers();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}
