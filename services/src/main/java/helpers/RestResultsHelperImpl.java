package helpers;

import javax.ejb.Stateless;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Stateless(name = "RestResultsHelper")
public class RestResultsHelperImpl implements RestResultsHelper {

	@Override
	public String resultWrapper(boolean success, String data) {
		String result = null;
		JsonParser parser = new JsonParser();
		if(success)
			result = "OK";
		else
			result = "KO";
		JsonObject response = new JsonObject();
		response.add("result", parser.parse(result));
		response.add("data", new JsonPrimitive(data));
		return response.toString();
	}

}
