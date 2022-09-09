package com.github.cm360.onegame.api;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.cm360.onegame.api.auth.ApiFunctionLogin;
import com.github.cm360.onegame.api.auth.ApiFunctionTokenLookup;
import com.github.cm360.onegame.api.interact.ApiFunctionDoAction;
import com.github.cm360.onegame.api.interact.ApiFunctionGetGame;
import com.github.cm360.onegame.api.interact.ApiFunctionNewGame;
import com.github.cm360.onegame.game.OneGame;
import com.github.cm360.onegame.server.Session;
import com.github.cm360.onegame.utils.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OneApi {

	private Map<String, OneGame> gameList;
	private List<Session> sessions;
	
	private HashMap<String, ApiFunction> apiFunctions;
	
	public OneApi(Map<String, OneGame> gameList, List<Session> sessions) {
		this.gameList = gameList;
		this.sessions = sessions;
		registerApiFunctions();
	}
	
	public String executeApiCall(URI callURI, String callerID) {
		try {
			Logger.log("OAPI_CALL", callURI.toString());
			// Split into name and arguments
			String[] callSplit = callURI.toString().split("\\?", 2);
			String name = callSplit[0];
			Map<String, String> arguments = Stream.of(callSplit[1].split("\\&")).map(arg -> arg.split("=", 2)).collect(Collectors.toMap(arg -> arg[0], arg -> arg[1]));
			return executeApiCall(name, arguments, callerID);
		} catch (Exception e) {
			e.printStackTrace();
			return "{\"status\":\"fail\",\"reason\":\"Unrecognized API call\"}";
		}
	}
	
	/*
	 * auth
	 *   login
	 *   register
	 *   lookup
	 * game
	 *   action
	 *   get
	 */
	public String executeApiCall(String callName, Map<String, String> arguments, String callerID) {
		// Lookup function by name
		ApiFunction apiFunction = apiFunctions.get(callName);
		if (apiFunction == null) {
			Logger.log("OAPI_INFO", new String[] { "Response", " - Status: fail", " - Reason: Unrecognized API call" });
			return "{\"status\":\"fail\",\"reason\":\"Unrecognized API call\"}";
		} else {
			JsonObject response = apiFunction.run(arguments, callerID);
			Logger.log("OAPI_INFO", new String[] { "Response", " - Status: " + response.get("status").getAsString(), " - Reason: " + response.get("reason").getAsString() });
			return new Gson().toJson(response);
		}
	}
	
	public void registerApiFunctions() {
		apiFunctions = new HashMap<String, ApiFunction>();
		apiFunctions.put("auth/login", new ApiFunctionLogin(sessions));
		apiFunctions.put("auth/lookup", new ApiFunctionTokenLookup(sessions));
		apiFunctions.put("game/create", new ApiFunctionNewGame(gameList));
		apiFunctions.put("game/get", new ApiFunctionGetGame(gameList, sessions));
		apiFunctions.put("game/action", new ApiFunctionDoAction(gameList, sessions));
	}
	
	public Map<String, OneGame> getGames() {
		return gameList;
	}
	
	public List<Session> getSessions() {
		return sessions;
	}

}
