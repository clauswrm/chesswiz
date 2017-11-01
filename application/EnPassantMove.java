package application;

import application.pieces.Piece;

public class EnPassantMove extends ChessMove {

	private Square targetSquare;
	
	public EnPassantMove(Piece movingPiece, Piece targetPiece, Square fromSquare,
			Square toSquare, Square targetSquare) {
		super(movingPiece, fromSquare, toSquare, targetPiece);
		this.targetSquare = targetSquare;
	}
	
	public Square getTargetSquare() {
		return targetSquare;
	}
	
	@Override
	public boolean capturesPiece() {
		return true;
	}
}