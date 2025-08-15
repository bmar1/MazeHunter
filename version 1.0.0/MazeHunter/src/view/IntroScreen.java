package view;


import model.*;
import controller.IntroController;

import java.io.InputStream;

import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class IntroScreen extends Application{
    

    private IntroController introController = new IntroController();
    MediaPlayer player;
    
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	
		try {
		
			FXMLLoader loader = new FXMLLoader(getClass().getResource("introscreen.fxml"));
	        loader.setController(introController);  // manually set the controller
	        Parent root = loader.load(); 	
	        
	       
			
		Scene scene = new Scene(root);
		
		
		scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		primaryStage.setScene(scene);
		
		Image Icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
		primaryStage.setTitle("MazeHunter - Select Stage");
		primaryStage.getIcons().add(Icon);
		primaryStage.setResizable(false);
		
		
		
		String musicResource = getClass().getResource("/sounds/intromusic.mp3").toExternalForm();
		Media media = new Media(musicResource);
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
		mediaPlayer.setCycleCount(4);
		mediaPlayer.setVolume(0.10);
		setPlayer(mediaPlayer);
		
		introController.initialize();
		introController.setIntroScreen(this);
		
		
		primaryStage.show();
		
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void setPlayer(MediaPlayer player) {
		this.player = player;
		
	}

	public MediaPlayer getPlayer() {
		return player;
	}
	
	
	
	public static void main (String[] args) {
		launch(args);
	}
}
