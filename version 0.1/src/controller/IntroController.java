package controller;

import view.*;

import java.io.IOException;
import java.util.Iterator;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
 * This class updates all visual or value updates in the introScreen NOTE TO SELF, 
 * ADD A STATE FOR AFTER CHOOSING TO PLAY WITH HUMAN TO DO LOCAL OR ACROSS INTERNET.
 */

public class IntroController {

	private boolean soloActive = false;
	private boolean duoActive = false;
	private boolean ai = false;
	private boolean human = false;
	private String difficulty;
	private String userClass;
	private String state; // track state of progress, i.e start for choosing, or chooseAiHuman (which
							// returns to start) difficulty returns to start
	// lastly nameState returns to difficulty

	private String player1;
	private String player2;

	private Image closedDoor;
	private Image openDoor;

	// Panes
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private AnchorPane highPane;
	@FXML
	private AnchorPane playSoloPane;
	@FXML
	private AnchorPane playDuoPane;
	// Images, refrenced from FXML document
	@FXML
	private ImageView playSoloImage;
	@FXML
	private ImageView playDuoImage;
	@FXML
	private Pane blackOverlay;

	@FXML
	public void initialize() {
		// Load door images
		setState("Start");
		closedDoor = new Image(getClass().getResource("/images/doorClosed.png").toExternalForm());
		openDoor = new Image(getClass().getResource("/images/doorOpen.png").toExternalForm());
		highPane.setStyle(
				"-fx-background-image: url('/images/introbackground.jpg'); -fx-background-size: cover; -fx-background-repeat: no-repeat;");

		// Set initial door images
		playSoloImage.setImage(closedDoor);
		playDuoImage.setImage(closedDoor);

		blackOverlay.setStyle("-fx-background-color: black;");
		blackOverlay.setMouseTransparent(true);
		blackOverlay.setOpacity(0);
	}

	// this method will use a switch statement to determine the state, then call the
	// appropriate method to switch to that state
	// so if we're at names, it'll go back to difficulty, delete the stuff of names
	public void back(MouseEvent event) {

		System.out.println("Current State: " + getState());

		switch (getState()) {
		case "Difficulty":
			// Change as this only works for playing alone, but for playing duo,
			// difficulty should return to chooseAiHuman. Add another case or an if
			fadeIn(() -> {
				// If duo is active, go back to AI-human selection
				if (isDuoActive()) {
					for (Iterator<Node> iterator = anchorPane.getChildren().iterator(); iterator.hasNext();) {
						Node node = iterator.next();
						if (!"playSoloPane".equals(node.getId()) && !"playDuoPane".equals(node.getId())
								&& !"welcome".equals(node.getId())) {
							iterator.remove(); // Remove node if it doesn't match any of the specified IDs
						}
					}
					setSoloActive(false);
					setDuoActive(false);
					playSoloPane.getChildren().clear();
					playDuoPane.getChildren().clear();
					setState("chooseAiHuman");
					transitionAiHuman();

				} else if (isSoloActive()) {
					// If solo is active, return just to the start
					anchorPane.getChildren().clear();
					returnStart();
					setSoloActive(false);
					setState("Start");
				}
				fadeOut();
			});
			break;

		case "chooseAiHuman":
			fadeIn(() -> {
				anchorPane.getChildren().clear();
				returnStart();
				setState("Start");
				setDuoActive(false);
				fadeOut();
			});
			break;

		case "Name": // two different returns depending on duo or not or remove more depednig on ai
			fadeIn(() -> {
				System.out.println(anchorPane.getChildren());
				if (isSoloActive())
					anchorPane.getChildren().remove(3);
				else if (isDuoActive())
					anchorPane.getChildren().remove(4);
				anchorPane.getChildren().removeIf(node -> node.getId() != null
						&& ((isSoloActive() || isAi()) && "NamePane1".equals(node.getId()))
						|| (isDuoActive() && ("NamePane1".equals(node.getId()) || "NamePane2".equals(node.getId()))));

				transitionDifficulty(event);

				fadeOut();
			});
			break;
		}
	}

	// Switch to the actual maze, (tutorial will be incl in main screen)
	private void startGame(MouseEvent event) {
		GameScreen gameScreen = new GameScreen();
	}

	public void soloClick(MouseEvent event) {
		// Remove solo and duo buttons, transition directly to difficulty

		setSoloActive(true);

		fadeIn(() -> {
			transitionDifficulty(event);

			ImageView back = new ImageView();
			configureNode(back, 30, 700, 140, 90, "/images/back.png", e -> back(event));

			anchorPane.getChildren().add(back);

			fadeOut();
		});

	}

	// transition to asking if they wanna play with ai or human, then call a method
	// for difficulty
	public void duoClick(MouseEvent event) {

		setDuoActive(true);
		playSoloPane.getChildren().clear();
		playDuoPane.getChildren().clear();

		fadeIn(() -> {
			transitionAiHuman();
			ImageView back = new ImageView();
			configureNode(back, 30, 700, 140, 90, "/images/back.png", e -> back(event));

			anchorPane.getChildren().add(back);
			fadeOut();
		});
	}

	private void transitionAiHuman() {
		// add two doors to determine if wanna play with or human
		setState("chooseAiHuman");
		Text instruct = new Text("Choose to play with Human or AI");
		configureNode(instruct, 340, 350, 0, 0, null, null);
		instruct.setFont(loadCustomFont(36));

		anchorPane.getChildren().add(instruct);
		instruct.setId("instructAI");

		ImageView human = createDoor("Human", 0, 0, 150, 200, this::transitionDifficulty);
		human.setId("human");
		ImageView AI = createDoor("AI", 0, 0, 150, 200, this::transitionDifficulty);
		AI.setId("AI");

		// Add new buttons to the scene
		playSoloPane.getChildren().add(human);
		playDuoPane.getChildren().add(AI);

	}

	public void difficultyClick(MouseEvent e) {

		Object source = e.getSource();
		if (source instanceof ImageView) {
			ImageView clickedDoor = (ImageView) source;

			switch (clickedDoor.getId()) {
			case "Easy":
				setDifficulty("Easy");
				break;

			case "Medium":
				setDifficulty("Medium");
				break;

			case "Hard":
				setDifficulty("Hard");
				break;

			default:
				System.out.println("Unknown door clicked");
				break;
			}
		}

		// Transition to name
		fadeIn(() -> {
			transitionName(e);
			fadeOut();
		});
	}

	public void transitionName(MouseEvent event) {
		setState("Name");

		ImageView clickedDoor = (ImageView) event.getSource();
		if (clickedDoor.getId() == "AI")
			setAi(true);
		else if (clickedDoor.getId() == "Human")
			setHuman(true);
		// delete all existing doors, (which are difficulty doors)
		// remove the panes to hold it and place the text fields on the anchorPane
		playSoloPane.getChildren().clear();

		playDuoPane.getChildren().clear();
		// anchorPane.getChildren().removeAll(playSoloPane, playDuoPane);
		Text instruct;
		if (isSoloActive())
			instruct = (Text) anchorPane.getChildren().get(3);

		else
			instruct = (Text) anchorPane.getChildren().get(4);

		instruct.setText("Enter name(s)");
		TextField textField1 = new TextField("Enter Player 1 Name...");
		textField1.setId("NamePane1");
		TextField textField2 = new TextField("Enter Player 2 Name...");
		textField2.setId("NamePane2");

		ImageView cont = new ImageView();
		configureNode(cont, 930, 750, 140, 90, "/images/cont.png", e -> selectClass(e));

		textField1.setStyle("-fx-border-radius: 30px;");
		textField2.setStyle("-fx-border-radius: 30px;");

		configureNode(textField1, 330, 400, 400, 30, null, null);
		configureNode(textField2, 330, 500, 400, 30, null, null);

		if (isDuoActive() && isAi() == false) {
			anchorPane.getChildren().add(textField1);
			anchorPane.getChildren().add(textField2);
		} else if (!isDuoActive() || isAi())
			anchorPane.getChildren().add(textField1);

		anchorPane.getChildren().add(cont);
	}

	//!!!
	private void selectClass(MouseEvent event) { // generate 5 doors to select class
		playSoloPane.getChildren().clear();
		anchorPane.getChildren().clear();
		playDuoPane.getChildren().clear();
		
		setState("Class");

		// set new background
		// include new statue img's of each class, on click set to img id (which is a
		// class)

		// then start game
		startGame(event);

	}

	// removes old doors and creates 3 new ones
	private void transitionDifficulty(MouseEvent event) {
		setState("Difficulty");

		playSoloPane.getChildren().clear();
		playDuoPane.getChildren().clear();
		if (duoActive)
			anchorPane.getChildren().remove(anchorPane.getChildren().get(3));

		if (event.getSource() instanceof ImageView) {
			ImageView clickedDoor = (ImageView) event.getSource();
			if (clickedDoor.getId() == "AI")
				setAi(true);
			else if (clickedDoor.getId() == "human")
				setHuman(true);

		}

		Text instruct = new Text("Select A Difficulty");
		configureNode(instruct, 420, 350, 0, 0, null, null);
		instruct.setFont(loadCustomFont(40)); 


		// Add the instruction text to the scene
		anchorPane.getChildren().add(instruct);
		ImageView easy = createDoor("Easy", 0, 0, 150, 200, this::difficultyClick);
		easy.setId("Easy");
		ImageView medium = createDoor("Medium", 150, 0, 150, 200, this::difficultyClick);
		medium.setId("Medium");
		ImageView hard = createDoor("Hard", 0, 0, 150, 200, this::difficultyClick);
		hard.setId("Hard");

		// Add new buttons to the scene
		playSoloPane.getChildren().addAll(easy, medium);
		playDuoPane.getChildren().add(hard);
		System.out.println(anchorPane.getChildren());
	}

	private void configureNode(Node node, double x, double y, double width, double height, String imagePath,
			EventHandler<? super MouseEvent> onClick) {
		node.setLayoutX(x);
		node.setLayoutY(y);

		if (node instanceof ImageView imageView) {
			imageView.setFitWidth(width);
			imageView.setFitHeight(height);

			if (imagePath != null && !imagePath.isEmpty()) {
				Image img = new Image(getClass().getResource(imagePath).toExternalForm());
				imageView.setImage(img);
			}
		} else if (node instanceof TextField textField) {
			textField.setPrefWidth(width);
			textField.setPrefHeight(height);
		} else if (node instanceof Text text) {
			text.setFill(javafx.scene.paint.Color.WHITE);
		}
		// Add more types as needed

		if (onClick != null) {
			node.setOnMouseClicked(onClick);
		}
	}

	private ImageView createDoor(String text, double layoutX, double layoutY, double width, double height,
			EventHandler<MouseEvent> onClickHandler) {
		ImageView img = new ImageView();
		img.setImage(closedDoor);
		img.setLayoutX(layoutX);
		img.setLayoutY(layoutY);
		img.setFitWidth(width);
		img.setFitHeight(height);

		img.setOnMouseClicked(onClickHandler);
		img.setOnMouseEntered(event -> openDoor(event));
		img.setOnMouseExited(event -> closeDoor(event));
		// create the text slightly above
		Text display = new Text(text);
		display.setFont(loadCustomFont(28));
		if (text == "Medium")
			display.setLayoutX(layoutX / 10 + 190);
		else
			display.setLayoutX(layoutX / 10 + 30);
		display.setLayoutY(0);
		display.setFill(javafx.scene.paint.Color.WHITE);
		// set color to white

		if (text != "Hard" && text != "AI")
			playSoloPane.getChildren().add(display);
		else
			playDuoPane.getChildren().add(display);

		return img;
	}

	// Close and open door based on if the mouse is in bounds, triggered from FXML
	// if the mosuse is on/off target
	public void openDoor(MouseEvent event) {
		ImageView doorImage = (ImageView) event.getSource();
		doorImage.setImage(openDoor);
	}

	public void closeDoor(MouseEvent event) {
		ImageView doorImage = (ImageView) event.getSource();
		doorImage.setImage(closedDoor);
	}

	private void returnStart() {

		try {
			// reset the screen to the start

			Parent root = FXMLLoader.load(getClass().getResource("/view/introscreen.fxml"));
			Scene scene = new Scene(root);

			// Get the current stage from any node in the current scene
			Stage stage = (Stage) anchorPane.getScene().getWindow();

			// replace current screen with one from FXML
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void fadeIn(Runnable onFinished) {
		// Fade in the black overlay to cover everything
		FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), blackOverlay);
		fadeIn.setFromValue(0);
		fadeIn.setToValue(1);
		fadeIn.setOnFinished(e -> onFinished.run()); // play the animation
		fadeIn.play();
	}

	private void fadeOut() {
		// Fade out the black overlay to reveal everything
		FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), blackOverlay);
		fadeOut.setFromValue(1);
		fadeOut.setToValue(0);
		fadeOut.play();
	}

	// Helper method to load custom font
	private Font loadCustomFont(int fontSize) {
		try {
			// Load the font from the resources folder
			Font customFont = Font.loadFont(getClass().getResourceAsStream("/fonts/MorrisRoman-Black.ttf"), fontSize);
			if (customFont != null) {
				return customFont;
			} else {
				System.out.println("Font not found, using default.");
				return Font.font("Arial", fontSize); // Default to Arial if the font is not found
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Font.font("Arial", fontSize); // Fallback to Arial if any error occurs
		}
	}

	public boolean isSoloActive() {
		return soloActive;
	}

	public void setSoloActive(boolean soloActive) {
		this.soloActive = soloActive;
	}

	public boolean isDuoActive() {
		return duoActive;
	}

	public void setDuoActive(boolean duoActive) {
		this.duoActive = duoActive;
	}

	public boolean isAi() {
		return ai;
	}

	public void setAi(boolean ai) {
		this.ai = ai;
	}

	public boolean isHuman() {
		return human;
	}

	public void setHuman(boolean human) {
		this.human = human;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getPlayer1() {
		return player1;
	}

	public void setPlayer1(String player1) {
		this.player1 = player1;
	}

	public String getPlayer2() {
		return player2;
	}

	public void setPlayer2(String player2) {
		this.player2 = player2;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}

}
