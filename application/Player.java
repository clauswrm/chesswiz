package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import application.pieces.Piece;

/**
 *    Class that represents a chess player.
 *    Each player has a nickname, and can play 
 *    many games of chess, for each the player has
 *    a collection of pieces available.
 *
 * @version 1.1   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class Player {

	protected Map<ChessGame, Collection<Piece>> pieces;
	protected String nickname;
	
	public Player(String nickname) {
		this.pieces = new HashMap<>();
		this.nickname = nickname;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void startNewGame(ChessGame game) throws IllegalArgumentException {
		if (pieces.containsKey(game)) {
			throw new IllegalArgumentException("Game has allready been started");
		}
		this.pieces.put(game, new ArrayList<>());
	}
	
	public void removeGame(ChessGame game) throws IllegalArgumentException {
		if (!pieces.containsKey(game)) {
			throw new IllegalArgumentException("Game has not been started");
		}
		this.pieces.remove(game);
	}
	
	public void addPiece(ChessGame game, Piece piece) throws IllegalArgumentException {
		if (!pieces.containsKey(game)) {
			throw new IllegalArgumentException("Game has not been started");
		}
		if (!pieces.get(game).contains(piece)) {
			this.pieces.get(game).add(piece);
		}
	}
	
	public void removePiece(ChessGame game, Piece piece) throws IllegalArgumentException {
		if (!pieces.containsKey(game)) {
			throw new IllegalArgumentException("Game has not been started");
		}
		if (pieces.get(game).contains(piece)) {
			this.pieces.get(game).remove(piece);
		}
	}
	
	public Collection<Piece> getPieces(ChessGame game) {
		return pieces.get(game);
	}
	
	/* Replace the pieces for the specified game
	 * (used when undoing moves or loading a new state) */
	public void setPieces(ChessGame game, Collection<Piece> pieces) throws IllegalArgumentException {
		if (!this.pieces.containsKey(game)) {
			throw new IllegalArgumentException("Game has not been started");
		}
		this.pieces.put(game, pieces);
	}
}