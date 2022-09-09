package com.github.cm360.onegame.server.websocket;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.github.cm360.onegame.api.OneApi;
import com.github.cm360.onegame.utils.Logger;

public class OneWebSocketServer extends WebSocketServer {

	private OneApi api;
	private HashMap<WebSocket, String[]> connectionGameInfoCache;
	
	public OneWebSocketServer(OneApi api, InetSocketAddress address) {
		super(address);
		this.api = api;
		connectionGameInfoCache = new HashMap<WebSocket, String[]>();
	}
	
	public void broadcastGameUpdate() {
		for (WebSocket ws : this.getConnections()) {
			String[] cachedInfo = connectionGameInfoCache.get(ws);
			if (cachedInfo != null) {
				try {
					ws.send(api.executeApiCall(new URI(String.format("game/get?%s&%s", cachedInfo[0], cachedInfo[1])), null));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Logger.log("WSRV_INFO", String.format("Connected to '%s'", conn.getRemoteSocketAddress()));
		// Create cache entry
		connectionGameInfoCache.put(conn, new String[] {"", ""});
	}
	
	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Logger.log("WSRV_INFO", String.format("Disconnected from '%s', Code: %d, Reason: ''", conn.getRemoteSocketAddress(), code, reason));
		// Delete cache entry
		connectionGameInfoCache.remove(conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		// Split socket message
		String[] messageSplit = message.split("\n\n");
		String name = messageSplit[0];
		String[] argumentsSplit = messageSplit[1].split("\n");
		Map<String, String> arguments = Stream.of(argumentsSplit).map(arg -> arg.split("=", 2)).collect(Collectors.toMap(arg -> arg[0], arg -> arg[1]));
		// Log socket message
		Logger.log("WSRV_INFO", new String[] {
				String.format("API call from '%s'", conn.getRemoteSocketAddress()),
				"Call: " + name,
				"Arguments: "
		});
		String[] argumentsIndented = new String[argumentsSplit.length];
		for (int i = 0; i < argumentsSplit.length; i++) {
			String arg = argumentsSplit[i];
			argumentsIndented[i] = " - " + arg;
			// Update cache entries if possible
			String[] cachedInfo = connectionGameInfoCache.get(conn);
			if (arg.startsWith("token=")) {
				cachedInfo[0] = arg;
			} else if (arg.startsWith("gameid=")) {
				cachedInfo[1] = arg;
			}
		}
		Logger.log("WSRV_INFO", argumentsIndented);
		// Execute API call
		conn.send(api.executeApiCall(name, arguments, conn.getRemoteSocketAddress().getAddress().getHostAddress()));
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Logger.log("WSRV_WARN", String.format("Exception occured in connection to '%s'", conn.getRemoteSocketAddress()));
		ex.printStackTrace();
		conn.close();
	}
	
	@Override
	public void onStart() {
		Logger.log("WSRV_INFO", String.format("Started on '%s'", this.getAddress()));
	}

}
