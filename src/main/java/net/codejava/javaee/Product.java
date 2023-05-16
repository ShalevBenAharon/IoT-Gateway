package net.codejava.javaee;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import org.json.JSONObject;


public class Product extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String dataBase = new String ("GenericIOTdataBase");
	DataBaseOperation sqlOp = null;

    public Product() throws SQLException {
        super();
        sqlOp = new SqlOperation(dataBase);
      
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject json = new JSONObject();
		json.put("Table", "Products");
		json.put("ID", request.getParameter("ID"));
		JSONObject retJson = sqlOp.get(json);
		
		response.getWriter().println(retJson.toString());
	}
	
	/**************************************************************/
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    JSONObject json = ServletUtils.CreateJsonObj(request);
	    sqlOp.insert(json);
	    request.setAttribute("jsonData", json.toString());
	    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gatewayServer");
	    dispatcher.forward(request, response);
		
	}
	
	/**************************************************************************************************************************/
}