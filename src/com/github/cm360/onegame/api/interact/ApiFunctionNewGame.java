package com.github.cm360.onegame.api.interact;

import java.util.Map;

import com.github.cm360.onegame.api.ApiFunction;
import com.github.cm360.onegame.game.OneGame;
import com.google.gson.JsonObject;

public class ApiFunctionNewGame implements ApiFunction {

	private Map<String, OneGame> gameList;
	private Map<String, Long> cooldowns;
	
	public ApiFunctionNewGame(Map<String, OneGame> gameList) {
		this.gameList = gameList;
	}
	
	@Override
	public JsonObject run(Map<String, String> arguments, String callerID) {
		if (callerID != null) {
			if (cooldowns.containsKey(callerID)) {
				if (cooldowns.get(callerID) > System.currentTimeMillis() + (60 * 1000)) {
					createGame(callerID);
				} else {
					
				}
			} else {
				createGame(callerID);
			}
		}
		
		// TODO craft response
		return null;
	}
	
	private void createGame(String creator) {
		
	}

}
