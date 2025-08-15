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

public class HomeScreen extends Application {

	private NavController homeController = new NavController();
	MediaPlayer player;

	@Override
	public void start(Stage primaryStage) throws Exception {

		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("homescreen.fxml"));
			loader.setController(homeController);
			Parent root = loader.load();

			Scene scene = new Scene(root);

		
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			// primaryStage.setStyle("-fx-background-image:
			// url('/images/introbackground.jpg'); -fx-background-size: cover;
			// -fx-background-repeat: no-repeat;");

			Font.loadFont(getClass().getResourceAsStream("/resources/fnts/MorrisRoman.ttf"), 16);

			Image Icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
			primaryStage.setTitle("MazeHunter - Home");
			primaryStage.getIcons().add(Icon);
			primaryStage.setResizable(true);

			
			String musicResource = getClass().getResource("/sounds/winMusic.mp3").toExternalForm();
			Media media = new Media(musicResource);
			MediaPlayer mediaPlayer = new MediaPlayer(media);
			mediaPlayer.play();
			mediaPlayer.setCycleCount(4);
			mediaPlayer.setVolume(0.10);
			setPlayer(mediaPlayer);
			

			homeController.setHomescreen(this);

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
