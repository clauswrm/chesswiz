package application.pieces;

import java.util.ArrayList;
import java.util.Collection;

import application.ChessGame;
import application.ChessMove;
import application.Player;
import application.Square;

public class Knight extends Piece {
	
	private static final int[][] movePattern = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1}, 
				{2, 1}, {1, 2}, {-1, 2}, {-2, 1}};
	
	public Knight(String name, Player player, ChessGame game) {
		super(name, player, game);
	}

	@Override
	public void updatePseudoLegalMoves() {
		Collection<ChessMove> posibleMoves = new ArrayList<>();
		int x = getSquare().getCoordinates()[0];
		int y = getSquare().getCoordinates()[1];
		
		for (int[] i : movePattern) {
			int xMove = x + i[0];
			int yMove = y + i[1];
			if (xMove >= 0 && xMove < 8 && yMove >= 0 && yMove < 8) {
				Square posibleSquare = game.getSquare(xMove, yMove);
				if (!posibleSquare.hasOwnPiece(getOwner())) {
					posibleMoves.add(new ChessMove(this, getSquare(), posibleSquare));
				}
			}
		}
		pseudoLegalMoves = posibleMoves;
	}
}