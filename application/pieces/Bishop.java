package application.pieces;

import java.util.ArrayList;
import java.util.Collection;

import application.ChessGame;
import application.ChessMove;
import application.Player;
import application.Square;

public class Bishop extends Piece {
	
	private static final int[][] movePattern = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}};
	
	public Bishop(String name, Player player, ChessGame game) {
		super(name, player, game);
	}

	@Override
	public void updatePseudoLegalMoves() {
		Collection<ChessMove> posibleMoves = new ArrayList<>();
		int x = getSquare().getCoordinates()[0];
		int y = getSquare().getCoordinates()[1];
		
		
		for (int[] m : movePattern) {
			for (int i = 1; i < 8; i++) {
				int xMove = x + i * (m[0]);
				int yMove = y + i * (m[1]);
				if (xMove >= 0 && xMove < 8 && yMove >= 0 && yMove < 8) {
					
					Square posibleSquare = game.getSquare(xMove, yMove);
					if (!posibleSquare.hasOwnPiece(getOwner())) {
						posibleMoves.add(new ChessMove(this, getSquare(), posibleSquare));
					}
					if (posibleSquare.hasPiece()) {
						break;
					}
				}
			}
		}
		pseudoLegalMoves = posibleMoves;
	}
}