package connect4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int Columns = 7;
	private static final int Rows = 6;
	private static final int Circle_Diamenter = 80;

	private static String PlayerOne = "Player One";
	private static String PlayerTwo = "Player Two";

	private static final String disc1Color = "#24303E";
	private static final String disc2Color = "#4CAA88";

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscsArray = new Disc[Rows][Columns];

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	private boolean isAllowedToInsert = true; //Flag to avoid same color disc to be inserted

	@FXML
	public TextField playerOneTextField, playerTwoTextField;

	@FXML
	public Button setNamesButton;


	public void createPlayground() {
		Shape rectangleWithHoles = createGameStructuralGrid();
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}

	private Shape createGameStructuralGrid() {
		Shape rectangleWithHoles = new Rectangle((Columns + 1) * Circle_Diamenter, (Rows + 1) * Circle_Diamenter);

		for (int i = 0; i < Rows; i++) {
			for (int j = 0; j < Columns; j++) {
				Circle circle = new Circle();
				circle.setRadius(Circle_Diamenter / 2);
				circle.setCenterX(Circle_Diamenter / 2);
				circle.setCenterY(Circle_Diamenter / 2);

				circle.setTranslateX(j * (Circle_Diamenter + 5) + Circle_Diamenter / 4);
				circle.setTranslateY(i * (Circle_Diamenter + 5) + Circle_Diamenter / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
			}
		}

		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < Columns; col++) {
			Rectangle rectangle = new Rectangle(Circle_Diamenter, (Rows + 1) * Circle_Diamenter);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (Circle_Diamenter + 5) + Circle_Diamenter / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if (isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisk(new Disc(isPlayerOneTurn), column);
				}

			});

			rectangleList.add(rectangle);
		}

		return rectangleList;
	}

	private void insertDisk(Disc disc, int column) {

		int row = Rows - 1;
		while (row >= 0) {
			if (getDiscIfPresent(row, column) == null)
				break;

			row--;
		}
		if (row < 0)
			return;

		insertedDiscsArray[row][column] = disc; // Structural changes
		insertedDiscsPane.getChildren().add(disc);

		disc.setTranslateX(column * (Circle_Diamenter + 5) + Circle_Diamenter / 4);

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (Circle_Diamenter + 5) + Circle_Diamenter / 4);
		translateTransition.setOnFinished(event -> {
			isAllowedToInsert = true;
			if (gameEnded(currentRow, column)) {
				gameOver();
			}
			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn ? PlayerOne : PlayerTwo);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row, int column) {
		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)
				.mapToObj(r -> new Point2D(r, column)).collect(Collectors.toList());
		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(col -> new Point2D(row, col)).collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint1.add(i, -i)).collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i)).collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) ||
				checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {
		int chain = 0;
		for (Point2D point : points) {
			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

			if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {
				chain++;
				if (chain == 4)
					return true;
			} else
				chain = 0;
		}
		return false;
	}

	private Disc getDiscIfPresent(int row, int column) { // TO Prevent ArrayOutOfBound Exception
		if (row >= Rows || row < 0 || column >= Columns || column < 0)
			return null;
		return insertedDiscsArray[row][column];
	}

	private void gameOver() {
		String winner = isPlayerOneTurn ? PlayerOne : PlayerTwo;
		System.out.println("Winner is: " + winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText(winner+" the winner!");
		alert.setContentText("Play Again?");

		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No,Exit");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> {
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
				resetGame();
			} else {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {
		insertedDiscsPane.getChildren().clear(); //Removes all inserted discs from the pane
		for (int row = 0; row < insertedDiscsArray.length; row++) { //Structurally sets all the disc elements to null
			for (int col = 0; col < insertedDiscsArray[row].length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true;
		playerNameLabel.setText(PlayerOne);
		createPlayground();
	}

	private static class Disc extends Circle {
		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove) {
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(Circle_Diamenter / 2);
			setFill(isPlayerOneMove ? Color.valueOf(disc1Color) : Color.valueOf(disc2Color));
			setCenterX(Circle_Diamenter / 2);
			setCenterY(Circle_Diamenter / 2);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setNamesButton.setOnAction(event -> {

			String input1 = playerOneTextField.getText();
			String input2 = playerTwoTextField.getText();

			PlayerOne = input1 + "`s";
			PlayerTwo = input2 + "`s";

			if (input1.isEmpty())
				PlayerOne = "Player One`s";

			if (input2.isEmpty())
				PlayerTwo = "Player Two`s";

			playerNameLabel.setText(isPlayerOneTurn ? PlayerOne : PlayerTwo);
		});
	}
}
