package com.github.cm360.onegame.server;

public class Session {

	private String token;
	private String username;

	public Session(String token, String username) {
		this.token = token;
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public String getUsername() {
		return username;
	}

}
