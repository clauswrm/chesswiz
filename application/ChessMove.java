package application;

import application.pieces.Piece;

public class ChessMove {

	protected final Piece movingPiece;
	protected final Piece targetPiece;
	protected final Square fromSquare;
	protected final Square toSquare;
	
	public ChessMove(Piece piece, Square fromSquare, Square toSquare) {
		this.movingPiece = piece;
		this.targetPiece = toSquare.getPiece();
		this.fromSquare = fromSquare;
		this.toSquare = toSquare;
	}
	
	public ChessMove(Piece movingPiece, Square fromSquare, Square toSquare, Piece targetPiece) {
		this.movingPiece = movingPiece;
		this.targetPiece = targetPiece;
		this.fromSquare = fromSquare;
		this.toSquare = toSquare;
	}

	public Square getToSquare() {
		return toSquare;
	}
	
	public Square getFromSquare() {
		return fromSquare;
	}
	
	public Piece getMovingPiece() {
		return movingPiece;
	}
	
	public Piece getTargetPiece() {
		return targetPiece;
	}
	
	public boolean capturesPiece() {
		return targetPiece != null;
	}
	
	@Override
	public String toString() {
		String str = "Move\n";
		str += "from: " + fromSquare.toString();
		str += "\nto: " + toSquare.toString();
		str += "\natk piece: " + movingPiece.getName();
		str += "\ntrgt piece: " + ((capturesPiece()) ? targetPiece.getName() : "none");
		str += "\nCaptures: " + capturesPiece();
		return str;
	}
}