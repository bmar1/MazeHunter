package view;

import controller.IntroController;
import controller.NavController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class WinScreen extends Application {

	private NavController winController = new NavController();
	MediaPlayer player;

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("winscreen.fxml"));
			loader.setController(winController);
			Parent root = loader.load();

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);

			// primaryStage.setStyle("-fx-background-image:
			// url('/images/introbackground.jpg'); -fx-background-size: cover;
			// -fx-background-repeat: no-repeat;");

			Font.loadFont(getClass().getResourceAsStream("/resources/fnts/MorrisRoman.ttf"), 16);

			Image Icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
			primaryStage.setTitle("MazeHunter - Win Screen");
			primaryStage.getIcons().add(Icon);
			primaryStage.setResizable(true);

			Media bg = new Media(getClass().getResource("/sounds/winMusic.mp3").toExternalForm());
			MediaPlayer player = new MediaPlayer(bg);
			setPlayer(player);
			player.setCycleCount(4);
			player.setVolume(0.10);
			player.play();
			primaryStage.setResizable(false);
			winController.setWinScreen(this);

			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setPlayer(MediaPlayer player) {
		this.player = player;

	}

	public MediaPlayer getPlayer() {
		return player;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
