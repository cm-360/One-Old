package com.github.cm360.onegame.api.auth;

import java.util.List;
import java.util.Map;

import com.github.cm360.onegame.api.ApiFunction;
import com.github.cm360.onegame.server.Session;
import com.github.cm360.onegame.utils.Logger;
import com.google.gson.JsonObject;

public class ApiFunctionTokenLookup implements ApiFunction {

	private List<Session> sessions;
	
	public ApiFunctionTokenLookup(List<Session> sessions) {
		this.sessions = sessions;
	}
	
	public JsonObject run(Map<String, String> arguments, String callerID) {
		JsonObject response = new JsonObject();
		String token = arguments.get("token");
		Logger.log("OAPI_INFO", new String[] { "Token lookup", " - Token: " + token });
		for (Session session : sessions)
			if (session.getToken().equalsIgnoreCase(token)) {
				response.addProperty("status", "ok");
				response.addProperty("reason", "Accepted");
				JsonObject login = new JsonObject();
				login.addProperty("username", session.getUsername());
				response.add("login", login);
				return response;
			}
		response.addProperty("status", "fail");
		response.addProperty("reason", "Invalid token");
		return response;
	}

}
