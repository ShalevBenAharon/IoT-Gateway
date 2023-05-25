
import java.time.LocalDateTime;
import java.util.Arrays;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

public class MongodbOperations implements DataBaseOperation {
	static MongoDatabase database = null;
	public MongodbOperations(String dataBaseName) {
		
	    MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017/"));
	    database = mongoClient.getDatabase(dataBaseName);
	}

	@Override
	public JSONObject create(JSONObject json) {
	    return intsertIntoMongo(json,"Companies");
	}

	@Override
	public JSONObject insert(JSONObject json) {
	    return intsertIntoMongo(json,"Products");
	}

	@Override
	public JSONObject register(JSONObject json) {
	    return intsertIntoMongo(json,"IOTDevice");
	}

	@Override
	public JSONObject update(JSONObject json) {
		return updateDoc(json,"IOTDevice");
	}
	
	@Override
	public JSONObject get(JSONObject json) {
		return null;
	}
	
	private JSONObject intsertIntoMongo(JSONObject json,String Collection){
		
		MongoCollection<Document> collection = database.getCollection(Collection);
		Document doc = Document.parse(json.toString());
		String[] Data = {};
        
		doc.append("Data", Arrays.asList(Data));
		collection.insertOne(doc);
	    
		ObjectId id = doc.getObjectId("_id");
	    return(new JSONObject().put("id", id.toString()));
	}
	
	private JSONObject updateDoc(JSONObject json,String Collection){
		MongoCollection<Document> collection = database.getCollection(Collection);
		Document doc = Document.parse(json.toString());
		
		LocalDateTime currentDateTime = LocalDateTime.now();
		String dateTimeString = currentDateTime.toString();
		String curDateTime = dateTimeString.substring(0,dateTimeString.indexOf("."));
		String update = new String(curDateTime + " " + json.getString("Update"));
		  
		collection.updateOne(
				  Filters.eq("S/N", json.getString("S/N")),	
				  Updates.push("Data", update)
				);

		return json;	
	}

}
