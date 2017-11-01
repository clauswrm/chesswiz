package application.pieces;

import java.util.ArrayList;
import java.util.Collection;

import application.ChessGame;
import application.ChessMove;
import application.EnPassantMove;
import application.Player;
import application.Square;

public class Pawn extends Piece {
	
	private final int direction, startingRow;
	
	public Pawn(String name, Player player, ChessGame game) {
		super(name, player, game);
		direction = (getName().charAt(0) == 'W') ? 1 : -1;
		startingRow = (getName().charAt(0) == 'W') ? 1 : 6;
	}
	
	@Override
	public void updatePseudoLegalMoves() {
		Collection<ChessMove> posibleMoves = new ArrayList<>();
		
		int x = getSquare().getCoordinates()[0];
		int y = getSquare().getCoordinates()[1];
		
		if (y + direction < 8 && y + direction >= 0) {
			//Jump one tile forward
			Square possibleSquareOneJump = game.getSquare(x, y + direction);
			if (!possibleSquareOneJump.hasPiece()) {
				posibleMoves.add(new ChessMove(this, getSquare(), possibleSquareOneJump));
			}
			// Take the piece forward right
			if (x < 7) {
				Square possibleSquare = game.getSquare(x + 1, y + direction);
				if (possibleSquare.hasPiece()) {
					if (possibleSquare.getPiece().isOpponent(this.getOwner())) {
						posibleMoves.add(new ChessMove(this, getSquare(), possibleSquare));
					}
				}
			}
			
			// Take the piece forward left
			if (x > 0) {
				Square possibleSquare = game.getSquare(x - 1, y + direction);
				if (possibleSquare.hasPiece()) {
					if (possibleSquare.getPiece().isOpponent(this.getOwner())) {
						posibleMoves.add(new ChessMove(this, getSquare(), possibleSquare));
					}
				}
			}
			
			// Jump two tiles forward from starting position
			if (y == startingRow) {
				Square possibleSquareTwoJump = game.getSquare(x, y + direction * 2);
				if (!possibleSquareOneJump.hasPiece() && !possibleSquareTwoJump.hasPiece()) {
					posibleMoves.add(new ChessMove(this, getSquare(), game.getSquare(x, y + 2 * direction)));
				}
			}
			
			//Én passant
			if (y == startingRow + direction * 3) {
				if (x < 7 && isEnPassant(x + 1, y)) {
					posibleMoves.add(new EnPassantMove(this, game.getSquare(x + 1, y).getPiece(), 
							getSquare(), game.getSquare(x + 1, y + direction), game.getSquare(x + 1, y)));
					
				} else if (x > 0 && isEnPassant(x - 1, y)) {
					posibleMoves.add(new EnPassantMove(this, game.getSquare(x - 1, y).getPiece(), 
							getSquare(), game.getSquare(x - 1, y + direction), game.getSquare(x - 1, y)));
				}
			}
		}
		pseudoLegalMoves = posibleMoves;
	}
	
	private boolean isEnPassant(int x, int y) {
		Piece currentTargetPiece = game.getSquare(x, y).getPiece();
		if (currentTargetPiece != null) {
			if (currentTargetPiece.getLastSquare() == game.getSquare(x, y + direction * 2)
					&& currentTargetPiece == game.getHistory().peek().getLastMoveDone().getMovingPiece()) {
				return true;
			}
		}
		return false;
	}
}