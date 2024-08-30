package Main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import Service.AuditServ.AuditService;
import Service.FileService;
import UserAuthentication.AuthService;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Authenticate User
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        // Assuming a method to authenticate and get userId
        int userId = authenticateUser(username, password);
        if (userId == -1) {
            System.out.println("Invalid credentials!");
            return;
        }

        // Generate JWT token
        String token = AuthService.generateToken(username);
        System.out.println("Your token: " + token);

        // Main loop for file operations
        while (true) {
            System.out.println("Choose an option: 1) Upload 2) Delete 3) Download 4) Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    // Sample File Upload
                    System.out.print("Enter file path to upload: ");
                    String filepath = scanner.nextLine();
                    try (FileInputStream fis = new FileInputStream(filepath)) {
                        byte[] data = fis.readAllBytes();
                        String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
                        FileService.uploadFile(userId, filename, data);

                        System.out.println("File uploaded successfully!");
                        AuditService.logAction(userId, getFileId(filename), "UPLOAD");
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    deleteFile(scanner, userId);
                    break;
                case 3:
                    downloadFile(scanner, userId);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private static int authenticateUser(String username, String password) {
        // Dummy method to simulate authentication
        if ("admin".equals(username) && "password".equals(password)) {
            return 1;  // Example user ID
        }
        return -1;
    }

    private static int getFileId(String filename) {
        // Dummy method to simulate fetching file ID
        return 1;  // Example file ID
    }

    // Method to delete a file
    private static void deleteFile(Scanner scanner, int userId) throws IOException {
        System.out.print("Enter file name to delete: ");
        String filename = scanner.nextLine();
        try {
            FileService.deleteFile(userId, filename);
            System.out.println("File deleted successfully!");
            AuditService.logAction(userId, getFileId(filename), "DELETE");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to download a file
    private static void downloadFile(Scanner scanner, int userId) {
        System.out.print("Enter file name to download: ");
        String filename = scanner.nextLine();
        try {
            byte[] fileData = FileService.downloadFile(userId, filename);
            try (FileOutputStream fos = new FileOutputStream("downloaded_" + filename)) {
                fos.write(fileData);
                System.out.println("File downloaded successfully!");
            }
            AuditService.logAction(userId, getFileId(filename), "DOWNLOAD");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}