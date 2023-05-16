package net.codejava.javaee;

import org.json.JSONObject;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


public class IotUpdate extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public IotUpdate() {
        super();
  
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gatewayServer");
	    dispatcher.forward(request, response);
    }
    
    /**************************************************************************************************************/
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		JSONObject json = ServletUtils.CreateJsonObj(request);
		request.setAttribute("jsonData", json.toString());
	    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/gatewayServer");
	    dispatcher.forward(request, response);
	}
	
	 /**************************************************************************************************************/
}
