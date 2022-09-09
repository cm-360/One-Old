package com.github.cm360.onegame.api.interact;

import java.util.List;
import java.util.Map;

import com.github.cm360.onegame.api.ApiFunction;
import com.github.cm360.onegame.game.OneGame;
import com.github.cm360.onegame.server.Session;
import com.google.gson.JsonObject;

public class ApiFunctionGetGame implements ApiFunction {

	private Map<String, OneGame> gameList;
	private List<Session> sessions;
	
	public ApiFunctionGetGame(Map<String, OneGame> gameList, List<Session> sessions) {
		this.gameList = gameList;
		this.sessions = sessions;
	}
	
	@Override
	public JsonObject run(Map<String, String> arguments, String callerID) {
		JsonObject response = new JsonObject();
		String gameId = arguments.get("gameid");
		String token = arguments.get("token");
		// Get username from session token
		String username = "";
		for (Session session : sessions)
			if (session.getToken().equalsIgnoreCase(token))
				username = session.getUsername();
		// Check for game
		if (gameList.containsKey(gameId)) {
			// Response status
			response.addProperty("status", "ok");
			response.addProperty("reason", "Accepted");
			response.add("game", gameList.get(gameId).toJson(username));
		} else {
			response.addProperty("status", "fail");
			response.addProperty("reason", "Unknown game ID");
		}
		return response;
	}

}
