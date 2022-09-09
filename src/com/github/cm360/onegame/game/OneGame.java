package com.github.cm360.onegame.game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.github.cm360.onegame.game.objects.Card;
import com.github.cm360.onegame.game.objects.DeckBuilder;
import com.github.cm360.onegame.game.objects.Hand;
import com.github.cm360.onegame.game.objects.Pile;
import com.github.cm360.onegame.game.objects.Player;
import com.github.cm360.onegame.game.objects.actions.Action;
import com.github.cm360.onegame.game.objects.actions.ActionDrawCard;
import com.github.cm360.onegame.game.objects.actions.ActionPlayCard;
import com.github.cm360.onegame.game.objects.actions.ActionSetColor;
import com.github.cm360.onegame.game.objects.actions.ActionSkipTurn;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class OneGame {

	// https://en.wikipedia.org/wiki/Uno_(card_game)
	
	private List<Player> players;
	private int turnIndex;
	private boolean turnsReversed = false;
	private HashMap<Player, boolean[]> moveMap;
	private int amountToDraw = 0;
	private Pile discardPile;
	private Pile drawPile;
	
	private Runnable onBroadcast;
	
	public OneGame(List<Player> players, int startingCards, Runnable onBroadcast) {
		this.players = players;
		this.turnIndex = players.size() - 1;
		this.moveMap = new HashMap<Player, boolean[]>();
		this.onBroadcast = onBroadcast;
		// Create card piles
		discardPile = new Pile();
		drawPile = DeckBuilder.buildDeck(List.of());
		//drawPile = DeckBuilder.buildDrawDeck();
		drawPile.shuffle();
		// Give each player 7 cards
		for (Player player : players)
			player.getHand().add(drawPile.draw(startingCards));
		// Give last player (dealer) the top card and use them to play it
		Player dealer = players.get(players.size() - 1);
		Hand dealersHand = dealer.getHand();
		Card topCard = drawPile.draw(1)[0];
		dealersHand.add(topCard);
		doAction(dealer, new ActionPlayCard(Arrays.asList(dealersHand.getCards()).indexOf(topCard)));
	}
	
	public void doAction(Player player, Action action) {
		Class<? extends Action> actionClass = action.getClass();
		// Check for a double-draw attempt
		boolean skipAllowedLastMove = player.checkAllowSkipTurn();
		if (skipAllowedLastMove && actionClass == ActionDrawCard.class) {
			// TODO: Illegal move, cheat prevention
			return;
		}
		// Reset player moves
		resetMoves();
		if (actionClass == ActionDrawCard.class) {
			// Draw card and add to player's hand
			Card drawnCard = drawPile.draw(1)[0];
			Hand playerHand = player.getHand();
			playerHand.add(drawnCard);
			// Allow player to play if card can go on top, skip turn if not
			boolean canPlay = drawnCard.canPlaceOn(discardPile.getTopCard());
			if (canPlay) {
				// Set possible moves
				Card[] playerCards = playerHand.getCards();
				boolean[] moves = new boolean[playerCards.length];
				for (int i = 0; i < playerCards.length; i++) {
					if (playerCards[i] == drawnCard) {
						moves[i] = true;
						break;
					}
				}
				setMovesFor(player, moves);
				// Activate skip turn button
				player.setAllowSkipTurn(true);
			} else {
				nextTurn();
			}
			// Refill draw pile if needed
			if (drawPile.getCards().length == 0)
				refillDrawPile();
			// TODO: Check draw to match vs draw one
		} else if (actionClass == ActionPlayCard.class) {
			ActionPlayCard actionCast = (ActionPlayCard) action;
			int cardIndex = actionCast.getCardIndex();
			Card playedCard = player.getHand().getCards()[cardIndex];
			if (discardPile.getSize() == 0 || playedCard.canPlaceOn(discardPile.getTopCard())) {
				// Add to discard pile
				discardPile.add(player.getHand().remove(cardIndex));
				// Check for card actions
				playCard(player, playedCard);
			}
		} else if (!skipAllowedLastMove && actionClass == ActionSetColor.class) {
			ActionSetColor actionCast = (ActionSetColor) action;
			Card topCard = discardPile.getTopCard();
			// Set color of played card
			topCard.chooseColor(actionCast.getColor());
			// Check for card actions
			playCard(player, topCard);
		} else if (actionClass == ActionSkipTurn.class) {
			// Skip turn
			if (player.checkAllowSkipTurn()) {
				// Check if player chose to just draw cards
				if (amountToDraw > 0)
					drawCards(player.getHand());
				nextTurn();
			}
		} else {
			// TODO: Error handling, invalid class or illegal move
			return;
		}
		// Fill in remaining player moves
		fillOtherMoves();
		// Broadcast game update
		onBroadcast.run();
	}
	
	private void playCard(Player player, Card card) {
		String cardColor = card.getColor();
		String cardType = card.getType();
		// Do not advance turn for black card to allow player to choose color
		if (cardColor.equals("black")) {
			// Prevent playing card while choosing color
			setMovesFor(player, new boolean[player.getHand().getSize()]);
		} else {
			// Action cards
			if (cardType.equals("skip")) {
				nextTurn();
			} else if (cardType.equals("reverse")) {
				if (players.size() == 2)
					nextTurn();
				else
					turnsReversed = !turnsReversed;
			} else if (cardType.contains("draw")) {
				int lastIndex = turnIndex;
				// Get next player's hand
				nextTurn();
				Player drawingPlayer = players.get(turnIndex);
				Hand drawingHand = drawingPlayer.getHand();
				// Check if they can stack a draw card
				boolean canStack = false;
				boolean[] stackMoves = new boolean[drawingHand.getSize()];
				if (true) { // TODO: Check stacking rule
					Card[] cards = drawingHand.getCards();
					for (int i = 0; i < stackMoves.length; i++) {
						stackMoves[i] = cards[i].getType().equals(cardType);
						canStack = canStack || stackMoves[i];
					}
				}
				// Increase number of cards to draw
				amountToDraw += Integer.parseInt(cardType.replace("draw_", ""));
				if (canStack) {
					// Set stacking moves and activate skip turn button
					setMovesFor(drawingPlayer, stackMoves);
					drawingPlayer.setAllowSkipTurn(true);
					// Undo next turn
					turnIndex = lastIndex;
				} else {
					drawCards(drawingHand);
				}
			}
			// Advance turn
			nextTurn();
		}
	}
	
	private void drawCards(Hand drawingHand) {
		Card[] drawPileCards = drawPile.getCards();
		if (amountToDraw >= drawPileCards.length) {
			// Draw remaining card from draw pile
			drawingHand.add(drawPile.draw(drawPileCards.length));
			// Re-shuffle
			refillDrawPile();
			// Draw needed number of cards, or entire draw pile
			drawingHand.add(drawPile.draw(Math.max(0, amountToDraw - drawPileCards.length)));
		} else {
			drawingHand.add(drawPile.draw(amountToDraw));
		}
		amountToDraw = 0;
	}

	private void nextTurn() {
		// Change turn index
		if (turnsReversed)
			turnIndex--;
		else
			turnIndex++;
		// Loop around edges
		if (turnIndex < 0)
			turnIndex = players.size() - 1;
		else if (turnIndex >= players.size())
			turnIndex = 0;
	}
	
	private void setMovesFor(Player player, boolean[] moves) {
		moveMap.put(player, moves);
	}
	
	private void fillOtherMoves() {
		for (Player p : players)
			if (!moveMap.containsKey(p)) {
				Card[] playerCards = p.getHand().getCards();
				boolean[] moves = new boolean[playerCards.length];
				for (int i = 0; i < playerCards.length; i++)
					moves[i] = playerCards[i].canPlaceOn(discardPile.getTopCard());
				moveMap.put(p, moves);
			}
	}
	
	private void resetMoves() {
		for (Player p : players) {
			p.setAllowSkipTurn(false);
			moveMap.remove(p);
		}
	}
	
	private void refillDrawPile() {
		// Take top card from discard pile
		Card topCard = discardPile.draw(1)[0];
		// Transfer discard pile to draw pile and shuffle
		Card[] cards = discardPile.draw(discardPile.getCards().length);
		for (int i = 0; i < cards.length; i++) {
			Card card = cards[i];
			if (card.getValue() == -2)
				cards[i] = new Card(card.getType());
		}
		drawPile.add(cards);
		drawPile.shuffle();
		// Reset the state of any black cards
		
		// Return top card
		discardPile.add(topCard);
	}
	
	public JsonObject toJson(String username) {
		JsonObject gameJson = new JsonObject();
		// All player data
		JsonArray playersJson = new JsonArray();
		for (Player player : players) {
			JsonObject playerJson = new JsonObject();
			// Username
			String playerName = player.getName();
			playerJson.addProperty("username", playerName);
			// Hand
			Hand playerHand = player.getHand();
			JsonObject handJson = new JsonObject();
			handJson.addProperty("count", playerHand.getSize());
			if (playerName.equals(username)) {
				Card[] playerCards = playerHand.getCards();
				boolean[] playerMoves = moveMap.get(player);
				JsonArray handCardsJson = new JsonArray();
				JsonArray movesJson = new JsonArray();
				for (int i = 0; i < playerCards.length; i++) {
					Card card = playerCards[i];
					handCardsJson.add(card.toJson());
					movesJson.add(playerMoves[i]);
				}
				handJson.add("cards", handCardsJson);
				handJson.add("moves", movesJson);
				// Skip turn allowed
				playerJson.addProperty("skip_allowed", player.checkAllowSkipTurn());
			}
			playerJson.add("hand", handJson);
			// Finish single player's data
			playersJson.add(playerJson);
		}
		gameJson.add("players", playersJson);
		// Piles
		JsonArray discardPileJson = new JsonArray();
		for (Card card : discardPile.getCards()) {
			discardPileJson.add(card.toJson());
		}
		gameJson.add("discardPile", discardPileJson);
		gameJson.addProperty("drawPile", getDrawPileSize());
		// Turn data
		gameJson.addProperty("turn", getTurn());
		// Finished
		return gameJson;
	}
	
	public Player getPlayer(String name) {
		for (Player player : players)
			if (player.getName().equals(name))
				return player;
		return null;
	}
	
	public Player[] getPlayers() {
		return players.toArray(new Player[players.size()]);
	}
	
	public String getTurn() {
		return players.get(turnIndex).getName();
	}
	
	public Card[] getDiscardPileCards() {
		return discardPile.getCards();
	}
	
	public int getDrawPileSize() {
		return drawPile.getCards().length;
	}

}
