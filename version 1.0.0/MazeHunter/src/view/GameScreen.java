package view;

import model.*;
import controller.GameController;
import controller.IntroController;
import controller.PlayerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class GameScreen extends Application {

	GameController gameController;
	PlayerController playerController;
	MediaPlayer player;
	private IntroController introController;

	public GameScreen(IntroController introController) {
		this.introController = introController;
	}

	public void start(Stage primaryStage) throws Exception {
		try {
			StackPane root = new StackPane();
			GridPane grid = new GridPane();
			Pane hudLayer = new Pane();

			root.getChildren().addAll(grid, hudLayer);

			grid.setAlignment(Pos.CENTER);
			grid.setPrefWidth(980);
			grid.setPrefHeight(980);
			grid.setHgap(0);
			grid.setVgap(0);
			grid.setPadding(Insets.EMPTY);

			Scene scene = new Scene(root, 980, 980);
			root.setStyle("-fx-background-color: black;");

			primaryStage.setScene(scene);

			Image Icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
			primaryStage.setTitle("MazeHunter");
			primaryStage.getIcons().add(Icon);
			primaryStage.setResizable(false);

			gameController = new GameController(root, grid, this, introController, hudLayer);
			gameController.setIntroController(introController);

			Media bg = new Media(getClass().getResource("/sounds/mazemusic.mp3").toExternalForm());
			player = new MediaPlayer(bg);
			player.setCycleCount(MediaPlayer.INDEFINITE);
			player.setVolume(0.30);
			player.play();

			setPlayer(player);

			playerController = new PlayerController(root, grid, gameController);

			gameController.setPlayerController(playerController);

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void setPlayer(MediaPlayer player) {
		this.player = player;

	}

	public MediaPlayer getPlayer() {
		return player;
	}
}