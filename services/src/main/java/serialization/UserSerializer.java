package serialization;

import java.lang.reflect.Type;

import model.User;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class UserSerializer implements JsonSerializer<User> {

	@Override
	public JsonElement serialize(User user, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("username", user.getUsername());
		json.addProperty("email", user.getEmail());
		json.addProperty("id", user.getId());
		json.addProperty("role", user.getRole().toString());
		return json;
	}

}
