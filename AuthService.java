package UserAuthentication;

import java.sql.Date;
import java.util.Base64;

public class AuthService 
{
	public static String generateToken(String username) 
	{
		String token = username +";" + new Date(0).getTime();
		return Base64.getEncoder().encodeToString(token.getBytes());
	}
	
	public static boolean validateToken(String token)
	{
		String decodedToken = new String (Base64.getDecoder().decode(token));
		String parts[] = decodedToken.split(":");
		return parts.length ==2;
		
	}
	
	public static String getUsernameFromToken(String token) 
	{
		String decodedToken = new String (Base64.getDecoder().decode(token));
		return decodedToken.split(":")[0];
	}

}
