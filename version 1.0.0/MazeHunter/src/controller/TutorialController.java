package controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import view.TutorialScreen;

public class TutorialController {

	private TutorialScreen screen;

	@FXML
	private Label titleLabel;

	@FXML
	private ImageView tutorialImage;

	@FXML
	private TextArea descriptionArea;

	@FXML
	private Button step1Btn;

	@FXML
	private Button step2Btn;

	@FXML
	private Button step3Btn;
	@FXML
	private Button step4Btn;

	@FXML
	private BorderPane root1;

	@FXML
	private HBox content;

	private HBox buttonLayout;

	private static final Image WALKING_GIF = new Image(
			TutorialController.class.getResource("/animations/walking.gif").toExternalForm());
	private static final Image EXIT_GIF = new Image(
			TutorialController.class.getResource("/animations/exit.gif").toExternalForm());
	private static final Image ABILITY_GIF = new Image(
			TutorialController.class.getResource("/animations/ability.gif").toExternalForm());

	private NavController navController = new NavController();

	@FXML
	private void initialize() {
		showBasicsScreen();
	}

	private void showBasicsScreen() {

		String text = "\"Fog in MazeHunter is a crucial aspect of the game, you must manuever around knowing you can only see portions of the screen. \"\r\n"
				+ "				+ \"The radius in which is visible depends on difficulty. \"\r\n"
				+ "				+ \"The minimap gives a strong sense of guidance towards how big of a maze you are in, as well as a sense of surrounding.";

		String title = "Basics";

		setTutorialScreen(title, text, WALKING_GIF, step1Btn);

	}

	@FXML
	private void handleClick(MouseEvent event) {
		Button button = (Button) event.getSource();

		List<Button> stepButtons = List.of(step1Btn, step2Btn, step3Btn, step4Btn);

		for (Button stepBtn : stepButtons) {
			if (stepBtn.getStyleClass().contains("step-button-active"))
				stepBtn.getStyleClass().remove("step-button-active");
		}

		if (content.getChildren().contains(buttonLayout)) {
			content.getChildren().remove(buttonLayout);
		}

		switch (button.getText()) {

		case "1":
			showBasicsScreen();
			break;
		case "2":
			showFogScreen();
			break;
		case "3":
			showClassScreen();
			break;
		case "4":
			showCompleted();
			break;
		default:
			showBasicsScreen();
		}
	}

	private void showCompleted() {

		setTutorialScreen("Completed", null, null, step4Btn);
		descriptionArea.setVisible(false);

		Image image = new Image(getClass().getResourceAsStream("/images/sign.png"));

		// First image stack - "Play Game"
		ImageView imageView1 = new ImageView(image);
		imageView1.setFitWidth(300);
		imageView1.setFitHeight(300);

		Label label1 = new Label("Play Game");
		label1.setStyle(
				"-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Morris Roman Black';");

		navController.setPlayer(screen.getPlayer());

		StackPane stack1 = new StackPane(imageView1, label1);
		stack1.setLayoutX(0);
		imageView1.setCursor(Cursor.HAND); // Optional: hand cursor on hover
		imageView1.setLayoutX(0);
		imageView1.setOnMouseEntered(e -> navController.mouseHover(e));
		imageView1.setOnMouseExited(e -> navController.mouseExit(e));
		imageView1.setOnMouseClicked(e -> navController.selectScreen(e));

		// Second image stack - "Home"
		ImageView imageView2 = new ImageView(image);
		imageView2.setFitWidth(300);
		imageView2.setFitHeight(300);

		Label label2 = new Label("Home");
		label2.setStyle(
				"-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-font-family: 'Morris Roman Black';");

		StackPane stack2 = new StackPane(imageView2, label2);
		stack2.setCursor(Cursor.HAND);
		stack2.setLayoutX(0);
		imageView2.setLayoutX(200);
		imageView2.setOnMouseEntered(e -> navController.mouseHover(e));
		imageView2.setOnMouseExited(e -> navController.mouseExit(e));
		imageView2.setOnMouseClicked(e -> navController.returnHome(e));

		// Layout to hold both image-buttons
		buttonLayout = new HBox(20, stack1, stack2);
		buttonLayout.setAlignment(Pos.CENTER);
		buttonLayout.setLayoutX(50);

		// Add to root
		if (content.getChildren().contains(buttonLayout)) {
			return;
		}

		content.getChildren().add(buttonLayout);

	}

	private void showClassScreen() {

		String text = "Each class selected contains an ability, a passive, and a debuff of sorts. Each class is aimed to aid in elements of the game, such as maze traversal, fog etc. \\n\"\r\n"
				+ "				+ \"An ability can be used by pressing E, after which its cooldown will start. Passives start with P, and debuffs apply automatically.";

		setTutorialScreen("Abilites & Classes", text, ABILITY_GIF, step3Btn);

	}

	private void showFogScreen() {

		String text = "The main objective of the game is to navigate through the maze to search for exit doorways. Exit doorways will be on each of the very far walls. \\n\"\r\n"
				+ "						+ \"Depending on difficulty, you may need to go through multiple layers of maze's.";

		setTutorialScreen("Exit & Game Functionality", text, EXIT_GIF, step2Btn);

	}

	private void setTutorialScreen(String title, String description, Image image, Button activeButton) {
		titleLabel.setText(title);
		descriptionArea.setText(description);
		tutorialImage.setImage(image);
		activeButton.getStyleClass().add("step-button-active");
	}

	public void setScreen(TutorialScreen tutorialScreen) {
		this.screen = tutorialScreen;

	}

}
