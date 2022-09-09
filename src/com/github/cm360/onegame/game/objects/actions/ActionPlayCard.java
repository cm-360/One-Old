package com.github.cm360.onegame.game.objects.actions;

public class ActionPlayCard implements Action {

	private int cardIndex;
	
	public ActionPlayCard(int cardIndex) {
		this.cardIndex = cardIndex;
	}
	
	public int getCardIndex() {
		return cardIndex;
	}

}
