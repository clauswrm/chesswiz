package application.pieces;

import java.util.ArrayList;
import java.util.Collection;

import application.CastelingMove;
import application.ChessGame;
import application.ChessMove;
import application.Player;
import application.Square;

/**
 *    Class that represents a chess King.
 *    (A subclass of the abstract Piece)
 *    Has methods for detecting if it is in
 *    check and if it has been moved.
 *
 * @version 1.1   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class King extends Piece {

	private static final int[][] movePattern = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}, 
			{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
	private final Rook queenSideRook;
	private final Rook kingSideRook;
	
	public King(String name, Player player, ChessGame game) {
		super(name, player, game);
		int row = (getName().charAt(0) == 'W') ? 0 : 7;
		queenSideRook = (Rook) game.getSquare(0, row).getPiece();
		kingSideRook = (Rook) game.getSquare(7, row).getPiece();
	}
	
	/** Goes through all squares in the board and checks
	 * for any enemy pieces that has its (the kings) square
	 * in its collection of legal moves */
	public boolean isChecked() {
		Player opponent = (getOwner() == game.getWhite()) ? game.getBlack() : game.getWhite();
		for (Piece piece : opponent.getPieces(game)) {
			piece.updatePseudoLegalMoves();
			for (ChessMove PLM : piece.getPseudoLegalMoves()) {
				if (PLM.getToSquare().equals(this.getSquare())) {
					return true;
				} 
			}
		}
		return false;
	}
	
	public boolean isMoved() {
		return previousSquares.size() > 1;
	}

	@Override
	public void updatePseudoLegalMoves() {
		Collection<ChessMove> posibleMoves = new ArrayList<>();
		int x = getSquare().getCoordinates()[0];
		int y = getSquare().getCoordinates()[1];
		
		for (int[] m : movePattern) {
			int xMove = x + m[0];
			int yMove = y + m[1];
			if (xMove >= 0 && xMove < 8 && yMove >= 0 && yMove < 8) {
				
				Square posibleSquare = game.getSquare(xMove, yMove);
				if (!posibleSquare.hasOwnPiece(getOwner())) {
					posibleMoves.add(new ChessMove(this, getSquare(), posibleSquare));
				}
			}
		}
		
		//Casteling
		if (!(isMoved() || canOpponentReachSquare(getSquare()))) {
			int row = (getName().charAt(0) == 'W') ? 0 : 7;
			//Castle kingside
			if (!(kingSideRook.isMoved() || game.getSquare(5, row).hasPiece()
					|| game.getSquare(6, row).hasPiece())) {
				if (!(canOpponentReachSquare(game.getSquare(5, row)) ||
						canOpponentReachSquare(game.getSquare(6, row)))) {
					posibleMoves.add(new CastelingMove(this, this, kingSideRook,
							game.getSquare(6, row), game.getSquare(5, row)));
				}
			}
			//Castle queenside
			if (!(queenSideRook.isMoved() || game.getSquare(1, row).hasPiece()
					|| game.getSquare(2, row).hasPiece() || game.getSquare(3, row).hasPiece())) {
				if (!(canOpponentReachSquare(game.getSquare(1, row)) ||
						canOpponentReachSquare(game.getSquare(2, row)))) {
					posibleMoves.add(new CastelingMove(this, this, queenSideRook,
							game.getSquare(2, row), game.getSquare(3, row)));
				}
			}
		}
		pseudoLegalMoves = posibleMoves;
	}
	
	private boolean canOpponentReachSquare(Square square) {
		Player opponent = (getOwner() == game.getWhite()) ? game.getBlack() : game.getWhite();
		 
		for (Piece piece : opponent.getPieces(game)) {
			for (ChessMove move : piece.getPseudoLegalMoves()) {
				if (move.getToSquare() == square) {
					return true;
				}
			}
		}
		return false;
	}
}