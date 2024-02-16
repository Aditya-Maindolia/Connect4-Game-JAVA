package connect4game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

	private Controller controller;

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();
		controller = loader.getController();
		controller.createPlayground();

		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Connect 4");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	//MenuBar
	private MenuBar createMenu() {
		//File Menu
		Menu fileMenu = new Menu("File");

		MenuItem newGame = new MenuItem("New Game");
		newGame.setOnAction(event -> controller.resetGame());

		MenuItem resetGame = new MenuItem("Reset Game");
		resetGame.setOnAction(event -> controller.resetGame());

		SeparatorMenuItem separator = new SeparatorMenuItem();
		MenuItem exitGame = new MenuItem("Quit Game");
		exitGame.setOnAction(event -> exitGame());

		fileMenu.getItems().addAll(newGame, resetGame, separator, exitGame);


		//Help Menu
		Menu helpMenu = new Menu("Help");

		MenuItem aboutGame = new MenuItem("About Connect 4");
		aboutGame.setOnAction(event -> aboutConnect4());

		SeparatorMenuItem separatortwo = new SeparatorMenuItem();

		MenuItem aboutMe = new MenuItem("About Dev");
		aboutMe.setOnAction(event -> aboutDev());

		helpMenu.getItems().addAll(aboutGame, separatortwo, aboutMe);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		return menuBar;
	}

	private void aboutDev() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About the Developer");
		alert.setHeaderText("Aditya M");
		alert.setContentText("Hey there! Hope you are doing well.\nI am currently in 2nd year of Computer Science Engineering\n" +
				"I used to play this game in my childhood and decided to create one using Java as I am currently learning JAVA " +
				"Develpoment.\nHope you will have fun.");
		alert.show();
	}

	private void aboutConnect4() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Connect 4");
		alert.setHeaderText("How to play?");
		alert.setContentText("Connect Four is a two-player connection game in which the players first choose a " +
				"color and then take turns dropping colored discs from the top into a seven-column, six-row " +
				"vertically suspended grid. The pieces fall straight down, occupying the next available space " +
				"within the column. The objective of the game is to be the first to form a horizontal, vertical, " +
				"or diagonal line of four of one's own discs. Connect Four is a solved game. The first player " +
				"can always win by playing the right moves.");
		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
