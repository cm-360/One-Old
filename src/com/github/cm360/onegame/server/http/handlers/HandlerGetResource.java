package com.github.cm360.onegame.server.http.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import com.github.cm360.onegame.server.ExchangeHelper;
import com.github.cm360.onegame.utils.FileUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerGetResource implements HttpHandler {

	private URI contextURI;
	
	public HandlerGetResource(URI contextURI) {
		this.contextURI = contextURI;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		URI requestURI = exchange.getRequestURI();
		// Check that requested resource is for the correct context
		if (contextURI.compareTo(requestURI) < 1) {
			// Relativize URI
			requestURI = contextURI.relativize(requestURI);
			// Retrieve and send resource
			
			
			
			
			
			// TODO: Make this secure!!!! like for real!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			
			
			
			
			try (FileInputStream fis = FileUtils.getResourceStream(requestURI.toString())) {
				exchange.sendResponseHeaders(200, 0);
				fis.transferTo(exchange.getResponseBody());
				fis.close();
			} catch (FileNotFoundException e) {
				ExchangeHelper.sendExceptionErrorPage(exchange, 404, e);
			}
		} else {
			ExchangeHelper.sendGenericErrorPage(exchange, 500, "", "Invalid URI!");
		}
		exchange.close();
	}

}
