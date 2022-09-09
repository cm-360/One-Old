package com.github.cm360.onegame.game.objects.actions;

public class ActionSetColor implements Action {

	private String color;

	public ActionSetColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

}
