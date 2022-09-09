package com.github.cm360.onegame.server;

import java.io.PrintWriter;
import java.util.HashMap;

import com.github.cm360.onegame.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;

public class ExchangeHelper {

	private static HashMap<String, String> HTTP_CODES;
	
	public static String getMessageForCode(int errorCode) {
		// https://www.rfc-editor.org/info/rfc7231
		return HTTP_CODES.getOrDefault(Integer.toString(errorCode), "Unknown");
	}
	
	public static String escapeText(String input) {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char tmp = input.charAt(i);
			switch (tmp) {
			case '<':
				output.append("&lt;");
				break;
			case '>':
				output.append("&gt;");
				break;
			case '&':
				output.append("&amp;");
				break;
			case '"':
				output.append("&quot;");
				break;
			case '\'':
				output.append("&#x27;");
				break;
			case '/':
				output.append("&#x2F;");
				break;
			default:
				output.append(tmp);
			}
		}
		return output.toString();
	}
	
	public static void sendGenericErrorPage(HttpExchange exchange, int errorCode, String heading, String message) {
		try {
			// Send response headers with error code
			exchange.sendResponseHeaders(errorCode, 0);
			// Read error page template and get status message
			String template = FileUtils.getStringContent(FileUtils.getResourceStream("error.html"));
			String statusMessage = escapeText(getMessageForCode(errorCode));
			// Write filled-in template to response body
			PrintWriter responseWriter = new PrintWriter(exchange.getResponseBody());
			responseWriter.write(String.format(template, errorCode, statusMessage, statusMessage, heading, message));
			responseWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendExceptionErrorPage(HttpExchange exchange, int errorCode, Exception exception) {
		// Get stacktrace
		StringBuilder stacktrace = new StringBuilder(exception.getClass().getName() + "\n");
		for (StackTraceElement ste : exception.getStackTrace())
			stacktrace.append("&emsp;at " + escapeText(ste.toString()) + "\n");
		// Send
		sendGenericErrorPage(exchange, errorCode, escapeText(exception.getMessage()), stacktrace.toString());
	}
	
	static {
		try {
			HTTP_CODES = new Gson().fromJson(new String(FileUtils.getResourceStream("http_codes.json").readAllBytes()),
					new TypeToken<HashMap<String, String>>(){}.getType());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
