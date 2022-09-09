package com.github.cm360.onegame.api.interact;

import java.util.List;
import java.util.Map;

import com.github.cm360.onegame.api.ApiFunction;
import com.github.cm360.onegame.game.OneGame;
import com.github.cm360.onegame.game.objects.Player;
import com.github.cm360.onegame.game.objects.actions.ActionDrawCard;
import com.github.cm360.onegame.game.objects.actions.ActionPlayCard;
import com.github.cm360.onegame.game.objects.actions.ActionSetColor;
import com.github.cm360.onegame.game.objects.actions.ActionSkipTurn;
import com.github.cm360.onegame.server.Session;
import com.google.gson.JsonObject;

public class ApiFunctionDoAction implements ApiFunction {

	private Map<String, OneGame> gameList;
	private List<Session> sessions;
	
	public ApiFunctionDoAction(Map<String, OneGame> gameList, List<Session> sessions) {
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
			OneGame game = gameList.get(gameId);
			Player player = game.getPlayer(username);
			if (player != null) {
				if (game.getTurn().equals(username)) {
					// Response status
					response.addProperty("status", "ok");
					response.addProperty("reason", "Accepted");
					// Do requested action
					String action = arguments.get("action");
					if (action.equals("draw")) {
						game.doAction(player, new ActionDrawCard());
					} else if (action.equals("play")) {
						game.doAction(player, new ActionPlayCard(Integer.parseInt(arguments.get("action_index"))));
					} else if (action.equals("color")) {
						String colorName = arguments.get("action_color");
						game.doAction(player, new ActionSetColor(colorName));
						// TODO Error handling, invalid color
					} else if (action.equals("skip")) {
						game.doAction(player, new ActionSkipTurn());
					} else {
						// TODO: Error handling, illegal action
					}
				} else {
					response.addProperty("status", "fail");
					response.addProperty("reason", "It is not this player's turn");
				}
			} else {
				response.addProperty("status", "fail");
				response.addProperty("reason", "Player is not in game");
			}
		} else {
			response.addProperty("status", "fail");
			response.addProperty("reason", "Unknown game ID");
		}
		return response;
	}

}
