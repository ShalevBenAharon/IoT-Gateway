

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import threadPool.ThreadPool;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONObject;

public class GatewayServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataBaseOperation dataBaseOp = null;

	private SingletonCommandFactory factory = SingletonCommandFactory.getInstance();
	private ThreadPool<Object> threadPool = new ThreadPool<>(4);
	
    public GatewayServer() {
        super();
        factory.add("CreaeteCompany", this::createNewCompanyMongo);
        factory.add("InsertProduct", this::insertProductToMongo);
		factory.add("registerIOT", this::registerIOT);
		factory.add("updateIOT", this::updateIOT);
	
	    dataBaseOp = new MongodbOperations("IOTdatabase");
    }

	/*******************************************************************/
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonData = (String) request.getAttribute("jsonData");
	    JSONObject jsonObject = new JSONObject(jsonData);
    	String command = jsonObject.getString("command");
    	Future<JSONObject> retResult = threadPool.submit(CreateCallabeTask(command,jsonObject));
    	JSONObject result = null;
		try {
			result = retResult.get();
		} catch (InterruptedException | ExecutionException e) {
		    throw new RuntimeException("Task interrupted", e);
		}	
		String id = result.getString("id");
    	response.getWriter().println("id : " + id.toString());
	}

	/*******************************************************************/
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonObject = null;
	   	JSONObject result = null;
	 	String command = null;
    	try{
    		jsonObject = ServletUtils.CreateJsonObj(request); 
    	}catch (Exception e) {
			throw new RuntimeException("Filed to Create an JSON Object"); 
		}
    	command = jsonObject.getString("command");
    	Future<JSONObject> retResult =  threadPool.submit(CreateCallabeTask(command,jsonObject));
    	try {
			result  = retResult.get();
		}catch (InterruptedException | ExecutionException e) {
		    throw new RuntimeException("Task interrupted", e);
		}
	
    	String id = result.getString("S/N");
    	response.getWriter().println("S/N : " + id.toString());
		
	}
	/*******************************************************************/
	@SuppressWarnings("unchecked")
	public <v>Callable<v> CreateCallabeTask(String command ,JSONObject jsonObject){
	        return()->{
				return (v)factory.execute(command, jsonObject);
	        };
	    }
	 
	/******************************************************************/
	private JSONObject registerIOT(JSONObject jsonObject){
		return dataBaseOp.register(jsonObject);
	}
	
	/******************************************************************/
	private JSONObject createNewCompanyMongo(JSONObject jsonObject) {
		return dataBaseOp.create(jsonObject);
	}
	
	/*******************************************************************/
	private JSONObject insertProductToMongo(JSONObject jsonObject) {
		return dataBaseOp.insert(jsonObject);
	}
	
	/*******************************************************************/
	private JSONObject updateIOT(JSONObject jsonObject) {
		return dataBaseOp.update(jsonObject); // NEED TO IMPLEMENT THE UPDATE ITs doing INSERT AT THE MOMENT 
	}
	
}
