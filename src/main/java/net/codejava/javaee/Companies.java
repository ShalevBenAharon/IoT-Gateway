package net.codejava.javaee;

import java.io.IOException;
import org.json.JSONObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class Companies extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String dataBase = new String ("GenericIOTdataBase");
	DataBaseOperation sqlOp = null;
    public Companies() {
        super();
        try {
        	sqlOp =  new SqlOperation(dataBase);
        }catch (Exception e) {
        	throw new RuntimeException("Connection Failed ");
        }
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("Table", "Companies");
		json.put("ID", request.getParameter("ID"));
		JSONObject retJson = sqlOp.get(json);
		response.getWriter().println(retJson.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    JSONObject json = ServletUtils.CreateJsonObj(request);
	    sqlOp.create(json);
	    request.setAttribute("jsonData", json.toString());
	    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gatewayServer");
	    dispatcher.forward(request, response);
	}
	

	 /**************************************************************************************************************/

}