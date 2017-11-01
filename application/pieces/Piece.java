package application.pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import application.ChessGame;
import application.ChessMove;
import application.Player;
import application.Square;

/**
 *    Class that represents an abstract chess piece.
 *    Each piece has an owner and at most one {@link Square}, as
 *    well as a collection of squares it can move to.
 *
 * @version 2.0   24 April 2017
 *
 * @author  Claus Martinsen
 */

public abstract class Piece {

	protected Square square;
	protected final String name;
	protected final Player owner;
	protected final ChessGame game;
	protected King ownKing;
	
	protected Stack<Square> previousSquares = new Stack<>();
	protected Collection<ChessMove> pseudoLegalMoves = new ArrayList<>();
	protected Collection<ChessMove> legalMoves = new ArrayList<>();
	
	public Piece(String name, Player player, ChessGame game) {
		this.owner = player;
		this.name = name;
		this.game = game;
	}
	
	public Square getSquare() {
		return square;
	}
	
	/* One-to-one relation between a piece and a square */
	public void setSquare(Square square) {
		if (getSquare() == square) {
			return;
		}
		Square oldSquare = this.square;
		previousSquares.push(oldSquare);
		this.square = square;
		if (oldSquare != null && oldSquare.getPiece() == this) {	
			oldSquare.setPiece(null);
		}
		if (this.square != null) {
			this.square.setPiece(this);
		}
	}
	
	public void undoLastMove() throws IllegalStateException {
		if (previousSquares.size() == 0) {
			throw new IllegalStateException("No previous moves made");
		}
		Square previousSquare = previousSquares.pop();
		setSquare(previousSquare);
		previousSquares.pop(); //Remove the jump back to original square
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public boolean isOwnedBy(Player player) {
		return owner == player;
	}
	
	public boolean isOpponent(Player player) {
		return !isOwnedBy(player);
	}
	
	public String getName() {
		return name;
	}
	
	public King getOwnKing() {
		return ownKing;
	}
	
	public Square getLastSquare() {
		return previousSquares.peek();
	} 
	
	public void setOwnKing(King ownKing) throws IllegalStateException {
		if (this.ownKing != null) {
			throw new IllegalStateException("Own king cannot be changed.");
		}
		this.ownKing = ownKing;
	}
	
	public Collection<ChessMove> getPseudoLegalMoves() {
		return pseudoLegalMoves;
	}
	
	public Collection<ChessMove> getLegalMoves() {
		return legalMoves;
	}
	
	/** Alters the collection pseudoLegalMoves which
	 * is the moves the piece can move to without taking
	 * into account if its own King is in check.
	 * Each subclass specifies the rules for how
	 * the piece can move in this method. */
	public abstract void updatePseudoLegalMoves();
	
	public void updateLegalMoves() {
		updatePseudoLegalMoves();
		Collection<ChessMove> legalMoves = new ArrayList<>();
		
		for (ChessMove move : pseudoLegalMoves) {
			if (game.testIfLegalMove(move)) {
				legalMoves.add(move);
			}
		}
		this.legalMoves = legalMoves;
	}
	
	public void setLegalMoves(Collection<ChessMove> legalMoves) {
		this.legalMoves = legalMoves;
	}
	
	@Override
	public String toString() {
		String str = "Piece " + name + "\nLegalMoves:\n";
		for (ChessMove move : legalMoves) {
			str += move.getToSquare().toString() + "\n";
		}
		return str;
	}
}