package application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import application.pieces.*;

/**
 *    Class that represents a game of chess between
 *    two {@link Player}s. Includes the logic of each turn
 *    (making moves and switching players), as well
 *    as the total history of the game.
 *
 * @version 2.2   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class ChessGame {
	
	private Player white;
	private Player black;
	private Player current;
	
	private ArrayList<ArrayList<Square>> board;
	private Piece selectedPiece;
	
	private Stack<ChessGameState> history;
	
	/** New game with new players */
	public ChessGame(String p1, String p2) {	
		this(new Player(p1), new Player(p2));
	}
	
	/** New game with existing players */
	public ChessGame(Player white, Player black) {
		this.white = white;
		this.black = black;
		white.startNewGame(this);
		black.startNewGame(this);
		this.current = white;
		this.history = new Stack<>();
		initializeBoard();
	}
	
	/** Continue or load game */
	public ChessGame(Player white, Player black, Player current, ArrayList<ArrayList<Square>> board, 
			Stack<ChessGameState> history) {
		this.white = white;
		this.black = black;
		this.current = current;
		this.board = board;
		this.history = history;
	}
	
	/** Continue or load game */
	public ChessGame(Stack<ChessGameState> history) {	
		load(history);
	}

	/** Sets up the board as 64 squares and pieces
	 * in the standard formation */
	private void initializeBoard() {
		
		//Sets up the squares
		this.board = new ArrayList<>(8);
		for (int y = 0; y < 8; y++) {
			board.add(new ArrayList<>(8));
			for (int x = 0; x < 8; x++) {
				board.get(y).add(new Square(x, y));
			}
		}
		//Places the pieces on the board
		for (int m = 0; m < 2; m++) {
			
			Player player = (m == 0) ? white : black;
			String sign = (m == 0) ? "W" : "B";
			
			int row = (m == 0) ? 0 : 7;
			int pawnRow = (m == 0) ? 1 : 6;
			
			for (int i = 0; i < 8; i++) {
				Piece pawn = new Pawn(sign + "P" + i, player, this);
				board.get(pawnRow).get(i).setPiece(pawn);
				player.addPiece(this, pawn);
			}
			for (int j = 0; j < 8; j += 7) {
				Piece rook = new Rook(sign + "R" + (j % 6), player, this);
				board.get(row).get(j).setPiece(rook);
				player.addPiece(this, rook);
			}
			for (int k = 1; k < 8; k += 5) {
				Piece knight = new Knight(sign + "K" + ((k - 1) % 4), player, this);
				board.get(row).get(k).setPiece(knight);
				player.addPiece(this, knight);
			}
			for (int l = 2; l < 8; l += 3) {
				Piece bishop = new Bishop(sign + "B" + (l % 2), player, this);
				board.get(row).get(l).setPiece(bishop);
				player.addPiece(this, bishop);
			}
			Piece queen = new Queen(sign + "Q0", player, this);
			board.get(row).get(3).setPiece(queen);
			player.addPiece(this, queen);
			
			Piece king = new King(sign + "XX", player, this);
			board.get(row).get(4).setPiece(king);
			player.addPiece(this, king);
			
			for (Piece piece : player.getPieces(this)) {
				piece.setOwnKing((King) king);
				piece.updateLegalMoves();
			}
		}
	}
	
	public Player getWhite() {
		return white;
	}
	
	public Player getBlack() {
		return black;
	}

	/** Gets the square that corresponds to the given
	 * coordinates */
	public Square getSquare(int x, int y) {
		return board.get(y).get(x);
	}
	
	public Player getCurrent() {
		return current;
	}
	
	public Stack<ChessGameState> getHistory() {
		return history;
	}
	
	public Piece getSelectedPiece() {
		return selectedPiece;
	}
	
	public void unselectPiece() {
		this.selectedPiece = null;
	}
	
	public boolean hasSelectedPiece() {
		return selectedPiece != null;
	}
	
	/** Returns the specified players available pieces */
	public Collection<Piece> getAvailablePieces(Player player) {
		return player.getPieces(this);
	}
	
	/** Removes the piece from the game by setting its
	 * square to null and removing it from the players
	 * collection of usable pieces */
	public void removePiece(Piece piece) {
		piece.setSquare(null);
		piece.getOwner().removePiece(this, piece);
	}
	
	public void undoRemovePiece(Piece piece) {
		piece.undoLastMove();
		piece.getOwner().addPiece(this, piece);
	}
	
	/** Undoes the last move done by popping the last 
	 * ChessGameState of the history. Does nothing if the 
	 * history stack is empty */
	public void undo() {
		if (history.size() > 0) {
			ChessGameState previous = history.pop();
			this.white.setPieces(this, previous.getWhitePieces());
			this.black.setPieces(this, previous.getBlackPieces());
			this.current = previous.getCurrent();
			this.board = previous.getBoard();
			previous.setLegalMovesForPlayers();
		}
	}
	
	/** Pushes the current state to the history stack */
	public void addGameStateToHistory(ChessMove move) {
		history.push(new ChessGameState(board, white.getPieces(this),
				black.getPieces(this), white, black, current, move));
	}
	
	/** Overrides all parameters of the game with the ones
	 * specified in the given ChessGameState */
	public void load(ChessGameState state) {
		this.history = new Stack<>();
		this.white.setPieces(this, state.getWhitePieces());
		this.black.setPieces(this, state.getBlackPieces());
		this.current = state.getCurrent();
		this.board = state.getBoard();
		state.setLegalMovesForPlayers();
	}
	
	/** Loads the game from the topmost ChessGameState in
	 * the given stack and adds the rest as the history */
	public void load(Stack<ChessGameState> history) {
		load(history.pop());
		this.history = history;
	}
	
	/** Searches through the players pieces and findes their king */
	public King getKing(Player player) throws IllegalStateException {
		for (Piece piece : player.getPieces(this)) {
			if (piece instanceof King) {
				return (King) piece;
			}
		}
		throw new IllegalStateException("Player has no king!?");
	}
	
	/** Makes the piece at the specified position the
	 * selected piece.
	 * @return Whether or not the action was successful*/
	public void selectPiece(int x, int y) {
		this.selectedPiece = getSquare(x, y).getPiece();
	}
	
	/** Checks if the specified square is a legal move for
	 * the specified piece */
	public boolean isLegalMoveForPiece(Piece piece, Square square) {
		for (ChessMove move : piece.getLegalMoves()) {
			if (move.getToSquare() == square) {
				return true;
			}
		}
		return false;
	}
	
	/** Goes through the specified players pieces and 
	 * updates each pieces legalMoves according to
	 * the games current state */
	public void updateLegalMovesForPlayer(Player player) {
		for (Piece piece : player.getPieces(this)) {
			piece.updateLegalMoves();
		}
	}
	
	/** Assumes the move is legal, and moves the 
	 * piece to the specified target, taking out the
	 * target squares current piece if applicable */
	public void movePiece(Piece piece, Square target) {
		for (ChessMove move : piece.getLegalMoves()) {
			if (move.getToSquare() == target) {
				movePiece(move);
				System.out.println("Moved piece " + piece.getName() + " to " + target.toString() + "\n");
				return;
			}
		}
	}
	
	public void movePiece(ChessMove move) {
		addGameStateToHistory(move);
		
		if (move instanceof CastelingMove) {
			CastelingMove cMove = (CastelingMove) move;
			cMove.getKing().setSquare(cMove.getKingTargetSquare());
			cMove.getRook().setSquare(cMove.getRookTargetSquare());
			
		} else if (move instanceof EnPassantMove) {
			EnPassantMove eMove = (EnPassantMove) move;
			eMove.getMovingPiece().setSquare(eMove.getToSquare());
			removePiece(eMove.getTargetPiece());
			
		} else {
			if (move.capturesPiece()) {
				removePiece(move.getTargetPiece());
			}
			
			//Promotion of pawn to queen
			if (move.getMovingPiece() instanceof Pawn && move.getToSquare().getCoordinates()[1] % 7 == 0) {
				removePiece(move.getMovingPiece());
				int queenNumber = 1;
				Player owner = move.getMovingPiece().getOwner();
				for (Piece piece : owner.getPieces(this)) {
					if (piece instanceof Queen) {
						int existingNumber = Integer.parseInt(piece.getName().substring(2));
						if (existingNumber >= queenNumber) {
							queenNumber = existingNumber + 1;
						}
					}
				}
				Piece queen = new Queen(((owner == white) ? "W" : "B") + "Q" + queenNumber, owner, this);
				queen.setOwnKing(getKing(owner));
				move.getToSquare().setPiece(queen);
				owner.addPiece(this, queen);
			} else {
				move.getMovingPiece().setSquare(move.getToSquare());
			}
		}
		
		unselectPiece();
		updateLegalMovesForPlayer(current);
		switchCurrentPlayer();
		updateLegalMovesForPlayer(current);
	}
	
	/** Moves the selected piece to the taget square */
	public void moveSelectedPiece(Square target) {
		movePiece(selectedPiece, target);
	}
	
	/** Switches whose turn it is */
	public void switchCurrentPlayer() {
		current = (current.equals(white)) ? black : white;
	}
	
	/** The game is stalemate if the player has no 
	 * legal moves, but still isn't in check */
	public boolean isStalemate(Player player) {
		//TODO 3-repetition rule (compare each piece placement in earlier chessgamestates)
		for (Piece piece : player.getPieces(this)) {
			if (piece.getLegalMoves().size() > 0) {
				return false;
			}
		}
		return !(getKing(current).isChecked());
	}
	
	/** The game is checkmate if the player is in check
	 * and has no legal moves (no moves available that gets
	 * the player out of check) */
	public boolean isCheckMate(Player player) {
		for (Piece piece : player.getPieces(this)) {
			if (piece.getLegalMoves().size() > 0) {
				return false;
			}
		}
		return (getKing(current).isChecked());
	}
	
	/** The game is over if a player is checkmated or
	 * in case of stalemate */
	public boolean isGameOver() {
		return (isCheckMate(white) || isCheckMate(black) || isStalemate(current));
	}
	
	public Player getWinner() {
		if (!isGameOver() || isStalemate(current)) {
			return null;
		}
		return (isCheckMate(white)) ? black : white;
	}
	
	public boolean testIfLegalMove(ChessMove move) {
		boolean isLegalMove;
		if (move instanceof CastelingMove) {
			CastelingMove cMove = (CastelingMove) move;
			cMove.getKing().setSquare(cMove.getKingTargetSquare());
			cMove.getRook().setSquare(cMove.getRookTargetSquare());
			isLegalMove = !move.getMovingPiece().getOwnKing().isChecked();
			cMove.getKing().undoLastMove();
			cMove.getRook().undoLastMove();
			isLegalMove = !move.getMovingPiece().getOwnKing().isChecked();
			
		} else if (move instanceof EnPassantMove) {
			EnPassantMove eMove = (EnPassantMove) move;
			eMove.getMovingPiece().setSquare(eMove.getToSquare());
			removePiece(eMove.getTargetPiece());
			isLegalMove = !move.getMovingPiece().getOwnKing().isChecked();
			eMove.getMovingPiece().undoLastMove();
			undoRemovePiece(eMove.getTargetPiece());
			
		} else {
			if (move.capturesPiece()) {
				removePiece(move.getTargetPiece());
			}
			move.getMovingPiece().setSquare(move.getToSquare());
			isLegalMove = !move.getMovingPiece().getOwnKing().isChecked();
			move.getMovingPiece().undoLastMove();
			if (move.capturesPiece()) {
				undoRemovePiece(move.getTargetPiece());
			}
		}
		return isLegalMove;
	}
}