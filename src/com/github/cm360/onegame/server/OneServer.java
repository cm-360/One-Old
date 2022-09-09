package com.github.cm360.onegame.server;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.cm360.onegame.api.OneApi;
import com.github.cm360.onegame.game.OneGame;
import com.github.cm360.onegame.game.objects.Hand;
import com.github.cm360.onegame.game.objects.Player;
import com.github.cm360.onegame.server.http.handlers.HandlerApiCall;
import com.github.cm360.onegame.server.http.handlers.HandlerGetResource;
import com.github.cm360.onegame.server.http.handlers.HandlerWebUI;
import com.github.cm360.onegame.server.websocket.OneWebSocketServer;
import com.sun.net.httpserver.HttpServer;

public class OneServer {

	private static HashMap<String, OneGame> gameList = new HashMap<String, OneGame>();
	private static ArrayList<Session> sessions = new ArrayList<Session>();
	
	public static void main(String[] args) {
		try {
			// API
			OneApi api = new OneApi(gameList, sessions);
			
			// HTTP server
			HttpServer httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", 11180), 0);
			// Register contexts
			httpServer.createContext("/", new HandlerWebUI());
			httpServer.createContext("/resource",  new HandlerGetResource(new URI("/resource")));
			httpServer.createContext("/api", new HandlerApiCall(api));
			// Start HTTP server
			httpServer.start();
			
			// Start WebSocket server
			OneWebSocketServer webSocketServer = new OneWebSocketServer(api, new InetSocketAddress("0.0.0.0", 11111));
			
			// Game
			gameList.put("abcd1234", new OneGame(List.of(
						new Player("Player1", new Hand()),
						new Player("Player2", new Hand())),
					7, () -> webSocketServer.broadcastGameUpdate()));
			webSocketServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
