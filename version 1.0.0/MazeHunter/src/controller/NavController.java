package controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import view.GameScreen;
import view.HomeScreen;
import view.IntroScreen;
import view.TutorialScreen;
import view.WinScreen;

public class NavController {

	private WinScreen winScreen;
	private HomeScreen homeScreen;
	private TutorialScreen tutorialScreen;

	private MediaPlayer player;

	private Image signImage;
	private Image signClickImage;
	

	@FXML
	public void mouseHover(MouseEvent event) {

		ImageView sign = (ImageView) event.getSource();
		sign.setImage(getSignClickImage());
	}

	@FXML
	public void mouseExit(MouseEvent event) {

		ImageView sign = (ImageView) event.getSource();
		sign.setImage(getSignImage());
	}

	//Set stage, stop music and restart screen
	@FXML
	public void returnHome(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		HomeScreen homeScreen = new HomeScreen();
		try {
			stopCurrentAudio();
			homeScreen.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void selectScreen(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		IntroScreen intro = new IntroScreen();
		try {
			stopCurrentAudio();
			intro.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void tutorialScreen(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		TutorialScreen tutorial = new TutorialScreen();
		try {
			stopCurrentAudio();
			tutorial.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopCurrentAudio() {
		if (winScreen != null && winScreen.getPlayer() != null) {
			winScreen.getPlayer().stop();
		} else if (homeScreen != null && homeScreen.getPlayer() != null) {
			homeScreen.getPlayer().stop();
		} else if (player != null) {
			player.stop();
		}
	}

	public void setWinScreen(WinScreen winScreen) {
		this.winScreen = winScreen;

	}
	

	public Image getSignImage() {
	    if (signImage == null) {
	        signImage = new Image(getClass().getResource("/images/sign.png").toExternalForm());
	    }
	    return signImage;
	}

	public Image getSignClickImage() {
	    if (signClickImage == null) {
	        signClickImage = new Image(getClass().getResource("/images/signClick.png").toExternalForm());
	    }
	    return signClickImage;
	}

	public WinScreen getWinscreen() {
		return this.winScreen;
	}

	public MediaPlayer getPlayer() {
		return player;
	}

	public void setPlayer(MediaPlayer player) {
		this.player = player;
	}

	public void setHomescreen(HomeScreen homeScreen) {
		this.homeScreen = homeScreen;

	}

	public HomeScreen getHomeScreen() {
		return this.homeScreen;
	}

}
