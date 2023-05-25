

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;


public class ServletUtils {

	public static JSONObject CreateJsonObj(HttpServletRequest request) throws IOException {
		
	 	InputStream inputStream = request.getInputStream();
	 	byte[] inputData = inputStream.readAllBytes();
	 	String jsonStr = new String(inputData);
	    return (new JSONObject(jsonStr));
	    
	}

}
