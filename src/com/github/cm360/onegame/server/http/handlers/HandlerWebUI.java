package com.github.cm360.onegame.server.http.handlers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.cm360.onegame.server.ExchangeHelper;
import com.github.cm360.onegame.utils.FileUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HandlerWebUI implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			FileInputStream fis = FileUtils.getResourceStream("one.html");
			exchange.sendResponseHeaders(200, 0);
			fis.transferTo(exchange.getResponseBody());
			fis.close();
		} catch (FileNotFoundException e) {
			ExchangeHelper.sendExceptionErrorPage(exchange, 500, e);
		}
		exchange.close();
	}

}
