package application;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 *    A complete JavaFX application in wich users
 *    can play chess against eachother, or against a
 *    {@link ChessAI}. It creates a GUI and connects
 *    input from the user to actions in a {@link ChessGame}.
 * 
 * @version 1.5   4 May 2017
 * 
 * @author Claus Martinsen
 */
public class Main extends Application {

	
	private Stage window;
	private Scene startScene, gameScene, endScene;
	private GridPane gameGrid = new GridPane();
	
	private ChessGame game;
	private ChessAI2 whiteAI, blackAI;
	
	private boolean isWhiteAI, isBlackAI = false;
	private boolean viewFromBlackPerspective = false;

	@Override
	public void start(Stage primaryStage) {
		try {
			window = primaryStage;
			createStartScene();
			window.setScene(startScene);
			window.setTitle("ChessFX by Claus");
			window.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createStartScene() {
		VBox startMenuLayout = new VBox(20);
		CheckBox whiteAICheckBox = new CheckBox("White player computer?"),
				 blackAICheckBox= new CheckBox("Black player computer?");
		Button startButton = new Button("Start game!");
		Label title = new Label("Chess beta 1.1"),
				startMenuLabel = new Label("Enter the players names below.");
		ImageView kingIcon1 = new ImageView(getImage("application/pics/WXV.png")),
				  kingIcon2 = new ImageView(getImage("application/pics/BXV.png"));
		TextArea inputField1 = new TextArea(),
				 inputField2 = new TextArea();
		
		inputField1.setPromptText("White players name");
		inputField2.setPromptText("Black players name");

		whiteAICheckBox.setOnAction(click -> {
			isWhiteAI = whiteAICheckBox.isSelected();		
			inputField1.setDisable(isWhiteAI);
		});
		
		blackAICheckBox.setOnAction(click -> {
			isBlackAI = blackAICheckBox.isSelected();		
			inputField2.setDisable(isBlackAI);
		});

		startButton.setOnAction(click -> startGame(inputField1.getText(), inputField2.getText()));
		
		startMenuLayout.getChildren().addAll(new HBox(kingIcon1, title, kingIcon2), startMenuLabel,
				new HBox(20, inputField1, whiteAICheckBox),
				new HBox(20, inputField2, blackAICheckBox), startButton);
		startScene = new Scene(startMenuLayout, 850, 600);
		
		startMenuLayout.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	}
	
	private void createGameScene() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				StackPane square = new StackPane();				
				square.setOnMouseClicked(click -> squareClicked(square));
				//TODO Mouse drag implementation
				square.setId(Integer.toString(col) + Integer.toString(row));
				gameGrid.add(square, col, row);
			}
		}
		gameScene = new Scene(gameGrid, 800, 800);
	}
	
	private void createEndScene() {
		Player winner = game.getWinner();
		String winnerStr = (winner == null) ? "Remis!" : winner.getNickname() + " won! Congratulations!";
		BorderPane endSceneLayout = new BorderPane();
		
		endSceneLayout.setTop(new Label("GAME OVER!"));
		endSceneLayout.setCenter(gameGrid);
		endSceneLayout.setBottom(new Label(winnerStr));
		
		endScene = new Scene(endSceneLayout, 800, 842);
	}
	
	private void startGame(String whiteName, String blackName) {
		if ((whiteName.length() > 0 || isWhiteAI) && (blackName.length() > 0 || isBlackAI)) {
			
			Player white = (isWhiteAI) ? new ChessAI2() : new Player(whiteName);
			Player black = (isBlackAI) ? new ChessAI2() : new Player(blackName);
			game = new ChessGame(white, black);
			
			if (isWhiteAI) {
				whiteAI = (ChessAI2) white;
				whiteAI.setup(game);
				
				if (!isBlackAI) { //When playing as black against the computer
					viewFromBlackPerspective = true;
				}
			}
			if (isBlackAI) {
				blackAI = (ChessAI2) black;
				blackAI.setup(game);
			}
			
			createGameScene();
			window.setScene(gameScene);
			updateGridImages();
			
		} else {
			System.out.println("Fill out all players");
		}
	}
	
	private void stopGame() {
		createEndScene();
		window.setScene(endScene);
		gameGrid.getChildren().forEach(pane -> pane.setOnMouseClicked(click -> System.out.println("Game over")));
	}
	
	private void squareClicked(StackPane pane) {
		int x = Character.getNumericValue(pane.getId().charAt(0));
		int y = 7 - Character.getNumericValue(pane.getId().charAt(1));
		Square square = game.getSquare(x, y);
		boolean pieceMoved = false;
		
		if (square.hasOwnPiece(game.getCurrent())) {
			game.selectPiece(x, y);
			System.out.println(square.getPiece()); //FOR DEBUGGING
			
		} else if (game.hasSelectedPiece() && game.isLegalMoveForPiece(game.getSelectedPiece(), square)) {
			game.moveSelectedPiece(square);
			pieceMoved = true;
			
		} else {
			System.out.println((square.hasPiece()) ? square.getPiece() : "Empty square\n"); //FOR DEBUGGING
			game.unselectPiece();
		}
		
		updateGrid(pieceMoved);
        
		if (game.isGameOver()) {
			stopGame();
			
		} else if (isWhiteAI && game.getCurrent() == whiteAI) {
			whiteAI.play();
			updateGrid(true);
			if (game.isGameOver()) {
				stopGame();
			}
			
		} else if (isBlackAI && game.getCurrent() == blackAI) {
            blackAI.play();
			updateGrid(true);
			if (game.isGameOver()) {
				stopGame();
			}
		}
	}
	
 	private StackPane getPane(Square square) {
 		int x = square.getCoordinates()[0];
 		int y = 7 - square.getCoordinates()[1];
 		return (StackPane) gameGrid.getChildren().get(x + y * 8);
 	}

	private Map<String, Image> images = new HashMap<String, Image>();

	private Image getImage(String imageDescription) {
		Image image = images.get(imageDescription);
		if (image == null) {
			image = new Image(imageDescription);
			if (image != null) {
				images.put(imageDescription, image);
			}
		}
		return image;
	}

	private String[] getImageNameFromSquare(Square square) {
		String[] str = null;
		if (square.hasPiece()) {
			str = new String[2];
			str[1] = square.getPiece().getName().substring(0, 2);
		} else {
			str = new String[1];
		}
		str[0] = ((square.getCoordinates()[0] + square.getCoordinates()[1]) % 2 == 1) ? "whiteSquare" : "blackSquare";
		return str;
	}

	private void updateGridImages() {
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				Square square = game.getSquare(x, 7 - y);
				String[] imageNames = getImageNameFromSquare(square);
				ObservableList<Node> pics = getPane(square).getChildren();
				
				pics.clear();
				for (int i = 0; i < imageNames.length; i++) {
					pics.add(new ImageView(getImage("application/pics/" + imageNames[i] + ".png")));
				}
			}
		}
	}
	
	private void updateGrid(boolean pieceMoved) {
		updateGridImages();
		
		if (game.hasSelectedPiece()) {
			//Show all legal moves for the piece
			for (ChessMove move : game.getSelectedPiece().getLegalMoves()) {
				String str = ((move.getToSquare().getCoordinates()[0] +
						move.getToSquare().getCoordinates()[1]) % 2 == 1) ? "whiteSquare" : "blackSquare";
				getPane(move.getToSquare()).getChildren().set(0, new ImageView(getImage("application/pics/" + str + "Target.png")));
			}
		} if (pieceMoved) {
			//Show where the piece was moved to
			Square square = game.getHistory().peek().getLastMoveDone().getToSquare();
			String str = ((square.getCoordinates()[0] + square.getCoordinates()[1]) % 2 == 1) ? "whiteSquare" : "blackSquare";
			getPane(square).getChildren().set(0, new ImageView(getImage("application/pics/" + str + "Moved.png")));
		}	
	}

	public static void main(String[] args) {
		launch(args);
	}
}