package application;

import application.pieces.Piece;

/**
 *    Class that represents a chess square.
 *    Each square has an at most one {@link Piece}, as
 *    well as its coordinates.
 *
 * @version 1.1   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class Square {

	private Piece piece;
	
	private int x;
	private int y;
	
	public Square(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/* One-to-one relation between a piece and a square */
	public void setPiece(Piece piece) {
		if (getPiece() == piece) {
			return;
		}
		Piece oldPiece = this.piece;
		this.piece = piece;
		if (oldPiece != null && oldPiece.getSquare() == this) {
			oldPiece.setSquare(null);
		}
		if (this.piece != null) {
			this.piece.setSquare(this);
		}
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public boolean hasPiece() {
		return getPiece() != null;
	}
	
	public boolean hasOwnPiece(Player player) {
		return (hasPiece()) ? getPiece().getOwner().equals(player) : false;
	}
	
	public int[] getCoordinates() {
		int[] coords = {x, y};
		return coords;
	}
	
	@Override
	public String toString() {
		return "Square (X:" + x + ",Y:" + y + ")";
	}
}