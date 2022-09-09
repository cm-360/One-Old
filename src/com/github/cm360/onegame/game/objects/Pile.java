package com.github.cm360.onegame.game.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Pile {

	private ArrayList<Card> cards;
	
	public Pile() {
		cards = new ArrayList<Card>();
	}
	
	public Card getTopCard() {
		return cards.get(cards.size() - 1);
	}
	
	public Card[] getCards() {
		return cards.toArray(new Card[cards.size()]);
	}
	
	public int getSize() {
		return cards.size();
	}
	
	public void shuffle() {
		Collections.shuffle(cards);
	}
	
	public void add(Card card) {
		cards.add(card);
	}
	
	public void add(Card[] cards) {
		this.cards.addAll(Arrays.asList(cards));
	}
	
	public Card[] draw(int amount) {
		int toDraw = Math.min(amount, cards.size());
		Card[] drawn = new Card[toDraw];
		for (int i = 0; i < toDraw; i++)
			drawn[i] = cards.remove(cards.size() - 1);
		return drawn;
	}

}
