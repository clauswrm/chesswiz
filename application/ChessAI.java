package application;

import java.util.ArrayList;
import java.util.Collection;

import application.pieces.Bishop;
import application.pieces.Knight;
import application.pieces.Pawn;
import application.pieces.Piece;
import application.pieces.Queen;
import application.pieces.Rook;


/**
 *    A chess engine AI that plays as an opponent.
 *    The AI will make the best possible {@link ChessMove}
 *    given how the game is at that point in time,
 *    and how many recursive steps forward it shall
 *    look for the best move. Implemented with a 
 *    miniMax algorithm which assumes best play from
 *    the opponent.
 *
 * @version 2.2   24 April 2017
 *
 * @author  Claus Martinsen
 */

public class ChessAI extends Player {

	/** The number of steps the AI will look ahead */
	public static final int RECURSION_DEPTH = 2;
	
	public static final double QUEEN_VALUE = 9.0, ROOK_VALUE = 5.0, BISHOP_VALUE = 3.0,
			KNIGHT_VALUE = 3.0, PAWN_VALUE = 1.0, LEGAL_MOVES_VALUE = 0.05,
			CHECK_VALUE = 5.0, CHECK_MATE_VALUE = 1000.0;

	private ChessMove move;
	private Player opponent;
	private ChessGame game;
	
	private int movesCalculated = 0;
	private int leafNodeMoves = 0;
	private StopWatch clock = new StopWatch();

	public ChessAI() {
		super("AI");
	}

	public void setup(ChessGame game) {
		this.game = game;
		opponent = (game.getWhite() == this) ? game.getBlack() : game.getWhite();
	}

	public void play() {
		findBestMove();
		move();
	}

	public void move() {
		game.movePiece(move);
	}

	private void findBestMove() throws IllegalStateException {
		//FOR DEBUGGING
		movesCalculated = 0;
		leafNodeMoves = 0;
		clock.start();
		
		move = null;
		System.out.println("Let me find a good move...");
		
		if (game.getCurrent() != this) {
			throw new IllegalStateException("It's not AIs turn, but trying to find best move.");
		}
		
		double max = Double.NEGATIVE_INFINITY;
		double score = 0;
		
		Collection<Piece> pieces = new ArrayList<>(getPieces(game));
		for (Piece piece : pieces) {
			for (ChessMove move : piece.getLegalMoves()) {
				game.movePiece(move);
				score = -negaMax(RECURSION_DEPTH);
				piece.undoLastMove();
				if (move.getMovingPiece() instanceof Pawn && move.getToSquare().getCoordinates()[1] % 7 == 0) {
					game.removePiece(move.getToSquare().getPiece());
				}
				if (move.capturesPiece()) {
					game.undoRemovePiece(move.getTargetPiece());
				} else if (move instanceof CastelingMove) {
					((CastelingMove) move).getRook().undoLastMove();
				}
				game.undo();
				
				if (score > max) {
					max = score;
					this.move = move;
				}
				movesCalculated++;
			}
		}
		
		if (move == null) { //Happens when the AI is checkmated in an amount of moves < rec. depth 
			for (Piece piece : pieces) {
				for (ChessMove move : piece.getLegalMoves()) {
					this.move = move;
					return;
				}
			}
		}
		//FOR DEBUGGING
		clock.stop();
		System.out.println("Moves calculated: " + movesCalculated);
		System.out.println("Leaf node moves: " + leafNodeMoves);
		Double time = clock.getElapsedTimeSecs();
		System.out.println("Tot calc. time: " + String.format("%.4f", time) + " sec.");
		System.out.println("Calc. time per move: " + String.format("%.4f", (time * 1000000 / movesCalculated)) + " µs.\n");
	}

	/** Finds the optimal move to make assuming the
	 *  opponent plays the best moves by depth first
	 *  searching through the possible move tree */
	private double negaMax(int recursionDepth) {
		if (recursionDepth == 0) {
			leafNodeMoves++;
			return evaluatePosition();
		}
		double max = Double.NEGATIVE_INFINITY;
		double score = 0;
		
		Collection<Piece> pieces = new ArrayList<>(game.getCurrent().getPieces(game));		
		for (Piece piece : pieces) {
			for (ChessMove move : piece.getLegalMoves()) {
				game.movePiece(move);
				score = -negaMax(recursionDepth - 1);
				piece.undoLastMove();
				if (move.getMovingPiece() instanceof Pawn && move.getToSquare().getCoordinates()[1] % 7 == 0) {
					game.removePiece(move.getToSquare().getPiece());
				}
				if (move.capturesPiece()) {
					game.undoRemovePiece(move.getTargetPiece());
				} else if (move instanceof CastelingMove) {
					((CastelingMove) move).getRook().undoLastMove();
				}
				game.undo();
				
				if (score > max) {
					max = score;
				}
				movesCalculated++;
			}
		}
		return max;
	}

	/** Evaluates the position for the AI vs the
	 * opponent, returning a score based on the
	 *  material and number of available moves it has */
	private double evaluatePosition() {
		int whoToMove = (game.getCurrent() == this) ? -1 : 1;
		double score = 0;
		
		if (game.isCheckMate(game.getCurrent())) {
			return -CHECK_MATE_VALUE;
		} else if (game.isCheckMate((game.getCurrent() == this) ? opponent : this)) {
			return CHECK_MATE_VALUE;
		} else if (game.isStalemate(game.getCurrent())) {
			return 0;
		}
		
		for (Piece piece : game.getCurrent().getPieces(game)) {
			if (piece instanceof Queen) {
				score += QUEEN_VALUE;
			} else if (piece instanceof Rook) {
				score += ROOK_VALUE;
			} else if (piece instanceof Bishop) {
				score += BISHOP_VALUE;
			} else if (piece instanceof Knight) {
				score += KNIGHT_VALUE;
			} else if (piece instanceof Pawn) {
				score += PAWN_VALUE;
			}

			score += LEGAL_MOVES_VALUE * piece.getPseudoLegalMoves().size();
		}
		
		Player other = (game.getCurrent() == this) ? opponent : this ;
		for (Piece piece : other.getPieces(game)) {
			if (piece instanceof Queen) {
				score -= QUEEN_VALUE;
			} else if (piece instanceof Rook) {
				score -= ROOK_VALUE;
			} else if (piece instanceof Bishop) {
				score -= BISHOP_VALUE;
			} else if (piece instanceof Knight) {
				score -= KNIGHT_VALUE;
			} else if (piece instanceof Pawn) {
				score -= PAWN_VALUE;
			}

			score -= LEGAL_MOVES_VALUE * piece.getPseudoLegalMoves().size();
		}
		
		score *= whoToMove;
		return score;
	}
}