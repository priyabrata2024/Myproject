package Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DatabaseConnection.Database;
import UserAuthentication.FileMetadata;

public class FileService 
{
	private static final String FILE_DIRECTORY = "files/";
	public static void uploadFile(int userId, String filename, byte[] data) throws SQLException, IOException {

	    int version = getCurrentVersion(userId, filename) + 1;
	

	    // Extract the file name from the full path provided by the user
	    String extractedFilename = new File(filename).getName();

	    // Construct the filepath properly
	    String filepath = FILE_DIRECTORY + "user_" + userId + "/" + extractedFilename + "_v" + version;
	    
	    File file = new File(filepath);
	    
	    file.getParentFile().mkdirs();
	    

	    try (FileOutputStream os = new FileOutputStream(file)) {
	        
	        os.write(data);
	        
	    }

	    
	    // file metadata saving
	    saveFileMetadata(userId, extractedFilename, filepath, version);
	}


	// Methods for saving the meta data
	private static void saveFileMetadata(int userId,String filename,String filepath, int version) throws SQLException
	{
		String sql = "INSERT INTO files(user_id,filename,filepath,version,uploaded_at) VALUES(?,?,?,?,NOW())";
		
		try(Connection conn = Database.getConnection(); 
				PreparedStatement stmt = conn.prepareStatement(sql))
		{
			stmt.setInt(1, userId);
			stmt.setString(2, filename);
			stmt.setString(3, filepath);
			stmt.setInt(4, version);
			stmt.executeUpdate();
		}
	}
	
	// Get the current version of the file
    private static int getCurrentVersion(int userId, String filename) throws SQLException {
        String sql = "SELECT MAX(version) AS version FROM files WHERE user_id = ? AND filename = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, filename);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("version");
            }
        }
        return 0;  // No previous versions
    }
	
    // List all files for a user
    public static List<FileMetadata> listFiles(int userId) throws SQLException {
        String sql = "SELECT * FROM files WHERE user_id = ?";
        List<FileMetadata> fileList = new ArrayList<>();
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                FileMetadata fileMetadata = new FileMetadata();
                fileMetadata.setId(rs.getInt("id"));
                fileMetadata.setUserId(rs.getInt("user_id"));
                fileMetadata.setFilename(rs.getString("filename"));
                fileMetadata.setFilepath(rs.getString("filepath"));
                fileMetadata.setVersion(rs.getInt("version"));
                fileMetadata.setUploadTime(rs.getTimestamp("uploaded_at").toLocalDateTime());
                fileList.add(fileMetadata);
            }
        }
        return fileList;
    }
    
 // Delete  of a file
    public static void deleteFile(int userId, String filename) throws SQLException, IOException {
        String sql = "SELECT filepath FROM files WHERE user_id = ? AND filename = ? ";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, filename);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String filepath = rs.getString("filepath");

                // Delete the file from the filesystem
                File file = new File(filepath);
                if (file.exists()) {
                    file.delete();
                }

                // Delete the file metadata from the database
                deleteFileMetadata(userId, filename);
            }
        }
    }

    // Delete file metadata from the database
    private static void deleteFileMetadata(int userId, String filename) throws SQLException {
        String sql = "DELETE FROM files WHERE user_id = ? AND filename = ? ";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, filename);
          
            stmt.executeUpdate();
        }
    }
    
    
    
    
    // method to Download the files 
    public static byte[] downloadFile(int userId, String filename) throws SQLException, IOException {
        String filePath;

        // Retrieve the file path from the database
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT filepath FROM files WHERE user_id = ? AND filename = ?")) {
            stmt.setInt(1, userId);
            stmt.setString(2, filename);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    filePath = rs.getString("filepath");
                } else {
                    throw new SQLException("File not found in the database: " + filename);
                }
            }
        }

        // Construct the file object
        File file = new File(filePath);
        
        // Check if the file exists
        if (!file.exists()) {
            throw new SQLException("File not found on the filesystem: " + filename);
        }

        // Read the file data from the filesystem
        return Files.readAllBytes(file.toPath());
    }
    
}
