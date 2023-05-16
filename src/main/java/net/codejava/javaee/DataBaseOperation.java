package net.codejava.javaee;

import org.json.JSONObject;

public interface DataBaseOperation {
	JSONObject create(JSONObject json);
	JSONObject insert(JSONObject json);
	JSONObject register(JSONObject json);
	JSONObject update(JSONObject json);
	JSONObject get(JSONObject json);

}
