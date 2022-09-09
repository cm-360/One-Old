package com.github.cm360.onegame.game.objects;

import java.util.List;

public class DeckBuilder {

	public static Pile buildDeck(List<String> rules) {
		Pile deck = new Pile();
		// Wild cards
		for (int i = 0; i < 4; i++) {
			deck.add(new Card("wild"));
			deck.add(new Card("draw_4"));
			if (rules.contains("downpour")) {
				deck.add(new Card("downpour_1"));
				deck.add(new Card("downpour_2"));
			}
		}
		// 4 colors
		for (String color : new String[] {"red", "yellow", "green", "blue"}) {
			// 0
			deck.add(new Card(color, 0));
			// 1-9
			for (int i = 1; i <= 9; i++) {
				deck.add(new Card(color, i));
				deck.add(new Card(color, i));
			}
			// Color action cards
			for (String type : new String[] {"skip", "reverse", "draw_2"}) {
				deck.add(new Card(color, type));
				deck.add(new Card(color, type));
			}
		}
		return deck;
	}
	
	public static Pile buildDrawDeck() {
		Pile deck = new Pile();
		for (int i = 0; i < 7; i++) {
			deck.add(new Card("draw_4"));
			deck.add(new Card("red", 0));
			deck.add(new Card("red", 0));
		}
		return deck;
	}

}
