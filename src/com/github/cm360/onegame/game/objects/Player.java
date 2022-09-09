package com.github.cm360.onegame.game.objects;

public class Player {

	private String name;
	private Hand hand;
	private boolean skipTurnAllowed;
	
	public Player(String name, Hand hand) {
		this.name = name;
		this.hand = hand;
		skipTurnAllowed = false;
	}
	
	public String getName() {
		return name;
	}
	
	public Hand getHand() {
		return hand;
	}
	
	public void setAllowSkipTurn(boolean allow) {
		skipTurnAllowed = allow;
	}
	
	public boolean checkAllowSkipTurn() {
		return skipTurnAllowed;
	}

}
