package com.github.cm360.onegame.server.http.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import com.github.cm360.onegame.api.OneApi;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerApiCall implements HttpHandler {

	private OneApi api;
	
	public HandlerApiCall(OneApi api) {
		this.api = api;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			URI callURI = new URI("/api").relativize(exchange.getRequestURI());
			String resultJson = api.executeApiCall(callURI, exchange.getRemoteAddress().getAddress().getHostAddress());
			if (resultJson == null) {
				exchange.sendResponseHeaders(400, -1);
			} else {
				exchange.sendResponseHeaders(200, 0);
				PrintWriter responseWriter = new PrintWriter(exchange.getResponseBody());
				responseWriter.write(resultJson);
				responseWriter.flush();
			}
		} catch (Exception e) {
			exchange.sendResponseHeaders(500, -1);
			e.printStackTrace();
		}
		exchange.close();
	}
	


}
