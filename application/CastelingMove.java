package application;

import application.pieces.King;
import application.pieces.Piece;
import application.pieces.Rook;

public class CastelingMove extends ChessMove {

	private King king;
	private Rook rook;
	private Square kingSquare;
	private Square rookSquare;
	private Square kingTargetSquare;
	private Square rookTargetSquare;
	
	public CastelingMove(Piece piece, King king, Rook rook,
			Square kingTargetSquare, Square rookTargetSquare) {
		super(piece, (piece == king) ? king.getSquare() : rook.getSquare(),
				(piece == king) ? kingTargetSquare : rookTargetSquare);
		this.king = king;
		this.rook = rook;
		this.kingSquare = king.getSquare();
		this.rookSquare = rook.getSquare();
		this.kingTargetSquare = kingTargetSquare;
		this.rookTargetSquare = rookTargetSquare;
	}
	
	public King getKing() {
		return king;
	}
	
	public Rook getRook() {
		return rook;
	}
	
	public Square getKingSquare() {
		return kingSquare;
	}
	
	public Square getRookSquare() {
		return rookSquare;
	}
	
	public Square getKingTargetSquare() {
		return kingTargetSquare;
	}
	
	public Square getRookTargetSquare() {
		return rookTargetSquare;
	}
	
	@Override
	public boolean capturesPiece() {
		return false;
	}
}