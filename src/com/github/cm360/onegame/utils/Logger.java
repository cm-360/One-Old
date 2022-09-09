package com.github.cm360.onegame.utils;

public class Logger {
	
	public static void log(String source, String message) {
		System.out.printf("[DATETIME] %s: %s\n", source, message);
	}
	
	public static void log(String source, String[] messageLines) {
		for (String line : messageLines) 
			log(source, line);
	}

}
