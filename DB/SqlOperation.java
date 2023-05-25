

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

public class SqlOperation implements DataBaseOperation{
	
	static Connection connection = null;
	public SqlOperation(String database) throws SQLException {
		connection = GetConnection(database);
	}

	@Override
	public JSONObject create(JSONObject json) {
			 int rowsEffected = 0;
			 try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Companies (CID, CompanyName, TelephoneNumber) VALUES (?, ?, ?)")) {
	         preparedStatement.setObject(1, json.getInt("CID"));
	         preparedStatement.setString(2, json.getString("CompanyName"));
	         preparedStatement.setString(3, json.getString("TelephoneNumber"));
	         rowsEffected = preparedStatement.executeUpdate();
	          	
	     } catch (SQLException e) {
	         throw new RuntimeException(e);
	     }
			 return new JSONObject().put("rowsEffected", rowsEffected);
	}

	@Override
	public JSONObject insert(JSONObject json) {
		 int rowsAffected = 0;
		  try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Products (CID, ProductType, ProductModel) VALUES (?, ?, ?)")) {
	          preparedStatement.setObject(1, json.getInt("CID"));
	          preparedStatement.setString(2, json.getString("ProductType"));
	          preparedStatement.setString(3, json.getString("ProductModel"));
	          rowsAffected = preparedStatement.executeUpdate();
	      } catch (SQLException e) {
	          throw new RuntimeException(e);
	      }
	          return new JSONObject().put("rowsEffected", rowsAffected);
		}
	
	@Override
	public JSONObject get(JSONObject json) {
		 JSONObject retJson = new JSONObject();
		 String sql = new String ("SELECT * FROM " + json.getString("Table") + " WHERE CID = ?");
		 try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            preparedStatement.setObject(1, json.getInt("CID"));
	            ResultSet result = preparedStatement.executeQuery();
	            result.next();
	            retJson.put("CID",result.getString(1));
	            retJson.put("ProductType",result.getString(2));
	            retJson.put("ProductModel",result.getString(3));
	       
	        } catch (SQLException e) {
	            retJson.put("CID", "NULL");
	            retJson.put("ProductType", "NULL");
	            retJson.put("ProductModel", "NULL");
	        }
		 
		return retJson;
	}
	
	
	public Connection GetConnection(String dataBases) throws SQLException {
		
		String connectionName = "jdbc:mysql://localhost:3306/"+dataBases;
		String userName = "****";
		String password = "****";
		DriverManager.registerDriver(new com.mysql.jdbc.Driver());
		return( DriverManager.getConnection(connectionName,userName,password));
	}

	@Override
	public JSONObject register(JSONObject json) {
		return null;
	}

	@Override
	public JSONObject update(JSONObject json) {
		return null;
	}

	
	
}
	


