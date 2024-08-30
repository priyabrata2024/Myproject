package Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import DatabaseConnection.Database;

public class AuditServ 
{
	public class AuditService {
	    private static final String LOG_FILE = "audit_logs.txt";

	    public static void logAction(int userId, int fileId, String action) throws SQLException, IOException {
	        // Log to a database
	        String sql = "INSERT INTO audit_logs (user_id, file_id, activity, timestamp) VALUES (?, ?, ?, NOW())";
	        try (Connection conn = Database.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, userId);
	            stmt.setInt(2, fileId);
	            stmt.setString(3, action);
	            stmt.executeUpdate();
	        }

	        // Log to a file
	        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
	            out.printf("User %d performed %s on File %d at %s%n", userId, action, fileId, new java.util.Date());
	        }
	    }
	}

}
