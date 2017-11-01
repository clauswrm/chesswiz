package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import application.pieces.Piece;

/**
 *    Class that represents a state of a chess game.
 *    In other words; ChessGameState X represents how
 *    the game looked at turn no. X.
 *    Containes all info to reproduce the scenario.
 *
 * @version 1.1   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class ChessGameState {

	private final ArrayList<ArrayList<Square>> board = new ArrayList<>();
	
	private final Collection<Piece> whitePieces = new ArrayList<>();
	private final Collection<Piece> blackPieces = new ArrayList<>();
	private final Map<Piece, Collection<ChessMove>> whiteLegalMoves = new HashMap<>();
	private final Map<Piece, Collection<ChessMove>> blackLegalMoves = new HashMap<>();
	
	private final Player white;
	private final Player black;
	private final Player current;
	
	private final ChessMove lastMoveDone;
	
	public ChessGameState(ArrayList<ArrayList<Square>> board, Collection<Piece> whitePieces, Collection<Piece> blackPieces,
			Player white, Player black, Player current, ChessMove move) {
		
		board.forEach(r -> this.board.add(new ArrayList<>(r)));
		this.whitePieces.addAll(whitePieces);
		this.blackPieces.addAll(blackPieces);
		whitePieces.forEach(p -> whiteLegalMoves.put(p, new ArrayList<>(p.getLegalMoves())));
		blackPieces.forEach(p -> blackLegalMoves.put(p, new ArrayList<>(p.getLegalMoves())));
		this.white = white;
		this.black = black;
		this.current = current;
		this.lastMoveDone = move;
	}
	
	public Collection<Piece> getWhitePieces() {
		return whitePieces;
	}
	
	public Collection<Piece> getBlackPieces() {
		return blackPieces;
	}
	
	public Player getWhite() {
		return white;
	}
	
	public Player getBlack() {
		return black;
	}
	
	public Collection<Piece> getPieces(Player player) throws IllegalArgumentException {
		if (player.equals(white)) {
			return getWhitePieces();
		} else if (player.equals(black)) {
			return getBlackPieces();
		} else {
			throw new IllegalArgumentException("Player not playing this game");
		}
	}
	
	public ArrayList<ArrayList<Square>> getBoard() {
		return board;
	}
	
	public Player getCurrent() {
		return current;
	}
	
	public ChessMove getLastMoveDone() {
		return lastMoveDone;
	}
	
	public void setLegalMovesForPlayers() {
		whitePieces.forEach(p -> p.setLegalMoves(whiteLegalMoves.get(p)));
		blackPieces.forEach(p -> p.setLegalMoves(blackLegalMoves.get(p)));
	}
}