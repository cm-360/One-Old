package com.github.cm360.onegame.game.objects;

import java.util.Arrays;

import com.google.gson.JsonObject;

public class Card implements Comparable<Card> {

	private String color; // red, yellow, green, blue, black
	private String type; // number, skip, reverse, wild, draw_2, draw_4, downpour_1, downpour_2
	private int value;
	
	
	public Card(String color, int value) {
		this.color = color;
		this.type = "number";
		this.value = value;
	}
	
	public Card(String color, String type) {
		this.color = color;
		this.type = type;
		this.value = -1;
	}
	
	public Card(String type) {
		this("black", type);
	}
	
	
	public void chooseColor(String chosenColor) {
		if (color.equals("black")) {
			if (chosenColor != "black") {
				color = chosenColor;
				value = -2;
			} else {
				// TODO: Error handling, black cannot be chosen as a card color
			}
		} else {
			// TODO: Error handling, this card cannot have a color chosen
		}
	}
	
	public boolean canPlaceOn(Card otherCard) {
		// TODO: Check rules +2 on +2, +4 on +4, etc
		return (color.equals("black") || (color.equals(otherCard.color) || (!type.equals("number") && type.equals(otherCard.type)) || (value != -1 && value == otherCard.value)));
	}
	
	public String getColor() {
		return color;
	}
	
	public String getType() {
		return type;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int compareTo(Card otherCard) {
		return getComparisonId() - otherCard.getComparisonId();
	}
	
	public int getComparisonId() {
		// Color index
		int colorIndex = Arrays.asList("red", "yellow", "green", "blue", "black").indexOf(color);
		if (colorIndex == -1)
			colorIndex = 15;
		// Type index
		int typeIndex = Arrays.asList("number", "skip", "reverse", "draw_2", "wild", "draw_4").indexOf(type);
		if (typeIndex == -1)
			typeIndex = 15;
		// Value
		int valueAdjusted = value;
		if (valueAdjusted < 0)
			valueAdjusted = 15;
		return (colorIndex << 8) + (typeIndex << 4) + value;
	}
	
	public JsonObject toJson() {
		JsonObject result = new JsonObject();
		result.addProperty("color", color);
		result.addProperty("type", type);
		result.addProperty("value", value);
		return result;
	}
	
}
