package com.github.cm360.onegame.game.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Hand {

	private ArrayList<Card> cards = new ArrayList<Card>();
	
	public void add(Card card) {
		cards.add(card);
		sort();
	}
	
	public void add(Card[] cards) {
		this.cards.addAll(Arrays.asList(cards));
		sort();
	}
	
	public Card remove(int index) {
		return cards.remove(index);
	}
	
	public Card[] getCards() {
		return cards.toArray(new Card[getSize()]);
	}
	
	public int getSize() {
		return cards.size();
	}
	
	private void sort() {
		Collections.sort(cards);
	}

}
