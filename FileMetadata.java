package UserAuthentication;

import java.time.LocalDateTime;

public class FileMetadata 
{
	
	

	private int id;
	private int userId;
	private String filename;
	private String filepath;
	private int version;
	private LocalDateTime uploadTime;
	
	// defult constuctor
	public FileMetadata () 
	{
		
	}
	// parameterized Constructor
	public FileMetadata(int id, int userId, String filename, String filepath, int version, LocalDateTime uploadTime) 
	{
		super();
		this.id = id;
		this.userId = userId;
		this.filename = filename;
		this.filepath = filepath;
		this.version = version;
		this.uploadTime = uploadTime;
	}
	
	// getters and setters 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public LocalDateTime getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(LocalDateTime localDateTime) {
		this.uploadTime = localDateTime;
	}
	
	// override the to string method
	@Override
	public String toString() 
	{
		return "FileMetadata [id=" + id + ", userId=" + userId + ", filename=" + filename + ", filepath=" + filepath
				+ ", version=" + version + ", uploadTime=" + uploadTime + "]";
	}
	
	

	

}
