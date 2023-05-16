package net.codejava.javaee;

import java.util.HashMap;
import java.util.function.Function;

import org.json.JSONObject;

public class SingletonCommandFactory implements ISQLFactory {

    private HashMap<String, Function<JSONObject,JSONObject>> commandFactory = new HashMap<>();

    private SingletonCommandFactory() {}

    private static class SingletonFactory {
        static final SingletonCommandFactory Instance = new SingletonCommandFactory();
    }

    public static SingletonCommandFactory getInstance() {
        return SingletonFactory.Instance;
    }

	@Override
	public JSONObject execute(String key, JSONObject param) {
		return commandFactory.get(key).apply(param);
	}

	@Override
	public void add(String key, Function<JSONObject,JSONObject>method) {
		commandFactory.put(key, method);
		
	}

}

interface ISQLFactory{
 
    void add(String key, Function<JSONObject,JSONObject> method);
    JSONObject execute(String key, JSONObject param);
}

