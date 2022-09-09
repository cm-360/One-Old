package com.github.cm360.onegame.api.auth;

import java.util.List;
import java.util.Map;

import com.github.cm360.onegame.api.ApiFunction;
import com.github.cm360.onegame.server.Session;
import com.github.cm360.onegame.utils.Hasher;
import com.github.cm360.onegame.utils.Logger;
import com.google.gson.JsonObject;

public class ApiFunctionLogin implements ApiFunction {

	private String[][] logins = {
			{"Player1", "0b14d501a594442a01c6859541bcb3e8164d183d32937b851835442f69d5c94e"}, // password1
			{"Player2", "6cf615d5bcaac778352a8f1f3360d23f02f34ec182e259897fd6ce485d7870d4"}  // password2
	};
	private List<Session> sessions;
	
	public ApiFunctionLogin(List<Session> sessions) {
		this.sessions = sessions;
	}

	public JsonObject run(Map<String, String> arguments, String callerID) {
		JsonObject response = new JsonObject();
		String username = arguments.get("username");
		String hash = arguments.get("hash").toLowerCase();
		Logger.log("OAPI_INFO", new String[] { "Login attempt", " - Username: " + username, " - Hash: " + hash });
		// Lookup username
		if (validateLogin(username, hash)) {
			response.addProperty("status", "ok");
			response.addProperty("reason", "Accepted");
			JsonObject login = new JsonObject();
			String token = Hasher.calculateSHA256(hash + Long.toHexString(System.currentTimeMillis()));
			updateSession(username, token);
			login.addProperty("token", token);
			response.add("login", login);
		} else {
			response.addProperty("status", "fail");
			response.addProperty("reason", "Invalid credentials");
		}
		return response;
	}
	
	private boolean validateLogin(String username, String hash) {
		for (String[] login : logins)
			if (username.equals(login[0]) && hash.equalsIgnoreCase(login[1]))
				return true;
		return false;
	}
	
	private void updateSession(String username, String token) {
		for (int i = 0; i < sessions.size(); i++) {
			Session session = sessions.get(i);
			if (session.getUsername().equals(username)) {
				Session oldSession = sessions.remove(i), newSession = new Session(token, username);
				sessions.add(newSession);
				Logger.log("OAPI_INFO", String.format("Invalidated old session '%s' for %s", oldSession.getToken(), oldSession.getUsername()));
				Logger.log("OAPI_INFO", String.format("Created new session '%s' for %s", newSession.getToken(), newSession.getUsername()));
				return;
			}
		}
		Session newSession = new Session(token, username);
		sessions.add(newSession);
		Logger.log("OAPI_INFO", String.format("Created new session '%s' for %s", newSession.getToken(), newSession.getUsername()));
	}

}
