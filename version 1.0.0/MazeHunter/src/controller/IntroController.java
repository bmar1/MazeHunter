package controller;

import view.*;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
	private Pane classInfoPane = null;
	private IntroScreen introScreen;

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

	public void soloClick(MouseEvent event) {
		// Remove solo and duo buttons, transition directly to difficulty

		setSoloActive(true);

		fadeIn(() -> {
			clickNoise();
			transitionDifficulty(event);

			ImageView back = new ImageView();
			back.setId("back");
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
			clickNoise();
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
		ImageView AI = createDoor("AI", 0, 10, 150, 200, this::transitionDifficulty);
		AI.setId("AI");

		// Add new buttons to the scene
		playSoloPane.getChildren().add(human);
		playDuoPane.getChildren().add(AI);

	}

	public void difficultyClick(MouseEvent e) {

		clickNoise();

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

			anchorPane.getChildren().removeIf(node -> "back".equals(node.getId()));
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

		ImageView back = createDoor("Back", 0, 0, 250, 430, this::back);

		ImageView cont = createDoor("Continue", 0, 10, 250, 430, this::selectClass);

		textField1.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); " + "-fx-text-fill: white; "
				+ "-fx-font-size: 16px; " + "-fx-prompt-text-fill: #cccccc; " + "-fx-border-color: #888; "
				+ "-fx-border-radius: 15; " + "-fx-bakground-radius: 5;" + "-fx-font-family: 'Morris Roman Black'");

		textField2.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); " + "-fx-text-fill: white; "
				+ "-fx-font-size: 16px; " + "-fx-prompt-text-fill: #cccccc; " + "-fx-border-color: #888; "
				+ "-fx-border-radius: 15; " + "-fx-bakground-radius: 5;" + " \"-fx-font-family: 'Morris Roman Black'");

		configureNode(textField1, 340, 400, 450, 30, null, null);
		configureNode(textField2, 340, 500, 450, 30, null, null);
		if (isDuoActive() && isAi() == false) {
			anchorPane.getChildren().add(textField1);
			anchorPane.getChildren().add(textField2);
		} else if (!isDuoActive() || isAi())
			anchorPane.getChildren().add(textField1);

		playSoloPane.getChildren().add(back);
		playDuoPane.getChildren().add(cont);
		// add back
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
		configureNode(instruct, 450, 750, 0, 0, null, null);
		instruct.setFont(loadCustomFont(40));

		// Add the instruction text to the scene
		anchorPane.getChildren().add(instruct);
		ImageView easy = createDoor("Easy", 30, 0, 250, 430, this::difficultyClick);
		easy.setId("Easy");
		ImageView medium = createDoor("Medium", 500, 0, 250, 430, this::difficultyClick);
		medium.setId("Medium");
		ImageView hard = createDoor("Hard", 0, 0, 250, 430, this::difficultyClick);
		hard.setId("Hard");

		// Add new buttons to the scene
		playSoloPane.getChildren().addAll(easy, medium);
		playDuoPane.getChildren().add(hard);
		System.out.println(anchorPane.getChildren());
	}

	private void selectClass(MouseEvent event) { // generate 5 doors to select class
		playSoloPane.getChildren().clear();
		anchorPane.getChildren().clear();
		playDuoPane.getChildren().clear();

		setState("Class");
		
		Text text = new Text("Select Class");
		configureNode(text, 430, 75, 202, 202, null, null);

		text.setFont(loadCustomFont(50));
	

		highPane.setStyle(
				"-fx-background-image: url('/images/class_select.png'); -fx-background-size: cover; -fx-background-repeat: no-repeat;");

		Text instruct = new Text("Select A Class");
		configureNode(instruct, 420, 350, 0, 0, null, null);
		instruct.setFont(loadCustomFont(40));

		// place all 4 statues (placeholder imgs for now)
		// each should have an event handler for on hover, as well as on select to
		// confirm
		ImageView vis = new ImageView();
		vis.setId("Visionary");
		configureNode(vis, 730, 350, 202, 202, "/images/vis_statue.png", e -> confirmClass(e));

		ImageView run = new ImageView();
		run.setId("Runner");
		configureNode(run, 190, 380, 202, 202, "/images/runner_statue.png", e -> confirmClass(e));

		ImageView psy = new ImageView();
		psy.setId("Psychic");
		configureNode(psy, 360, 330, 202, 202, "/images/psychic_statue.png", e -> confirmClass(e));

		ImageView nomad = new ImageView();
		nomad.setId("Nomad");
		configureNode(nomad, 880, 410, 202, 202, "/images/statue.png", e -> confirmClass(e));

		anchorPane.getChildren().addAll(vis, run, psy, nomad, text);

	}

	private void confirmClass(MouseEvent e) {

		ImageView imgView = (ImageView) e.getSource();
		String name = imgView.getId();
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setTitle("Confirm Class");
		confirm.setContentText("Are you sure you want to select the " + name + " class?");

		// Proper styling via DialogPane
		DialogPane dialogPane = confirm.getDialogPane();
		dialogPane.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
		dialogPane.getStyleClass().add("dialog-pane");
		dialogPane.getStyleClass().add("dialog-pane.content");
		dialogPane.getStyleClass().add(".dialog-pane .header-panel");
		dialogPane.getStyleClass().add("dialog-pane.button-bar.button");
		dialogPane.getStyleClass().add("dialog-pane.button-bar.button:hover");

		Optional<ButtonType> result = confirm.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			setUserClass(name);
			startGame(e);
		}
	}

	// remove a custom pane that display's each class's info
	private void removeClassInfo(MouseEvent e, ImageView imageView, Pane classInfoPane) {
		anchorPane.getChildren().remove((classInfoPane));
	}

	// add a custom pane that displays class-specific info on hover of a class
	private void displayClassInfo(ImageView imageView, String imagePath) {
		// If an info pane already exists, remove it first
		if (classInfoPane != null || anchorPane.getChildren().contains(classInfoPane)) {
			anchorPane.getChildren().remove(classInfoPane);
		}

		// Create a new pane for the class info overlay
		classInfoPane = new Pane();
		classInfoPane.setPrefSize(500, 300); // Medium size pane
		classInfoPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75); -fx-background-radius: 10;");
		classInfoPane.setLayoutX(250); // Adjust to center or near imageView if desired
		classInfoPane.setLayoutY(100);

		// Create Text to display class name and description
		Text classTitle = new Text();
		classTitle.setFont(loadCustomFont(28));
		classTitle.setFill(Color.WHITE);

		Text classDescription = new Text();
		classDescription.setFont(Font.font("Arial", 16));
		classDescription.setFill(Color.LIGHTGRAY);
		classDescription.setWrappingWidth(380);

		String id = imageView.getId();
		switch (id) {
		case "Visionary":
			classTitle.setText("Visionary");
			classDescription.setText(
					"The visionary is renound for her knowledge of magical abilites, as a powerful sorcess, shes strong in utility, and strategy."
							+ " She uses her light magic powers to combat the darkness in the dungeon, and if used strategically, can be groundbreaking.");
			break;
		case "Runner":
			classTitle.setText("Runner");
			classDescription.setText(
					"The runner is known for their expertise in exploration, wether it be high or low, slow or fast, "
							+ "they'll move through anyting easy as long as you know the way.");
			break;
		case "Psychic":
			classTitle.setText("Psychic");
			classDescription.setText(
					"Though the psychic indeed has magical powers it was honed in a different way, her output of them is different, through arcana magic she is"
							+ " incredibly talented in providing utility, while being creative.");
			break;
		case "Nomad":
			classTitle.setText("Nomad");
			classDescription.setText("The nomad's expert skills in exploration have been tested time and time "
					+ "again, some say they just know the way through any place, when tested, the nomad always knows the way.");
			break;
		default:
			classTitle.setText("Unknown Class");
			classDescription.setText("No information available.");
			break;
		}

		classTitle.setLayoutX(10);
		classTitle.setLayoutY(30);

		classDescription.setLayoutX(10);
		classDescription.setLayoutY(70);

		// Optionally add an icon or image of the class (using imagePath)
		if (imagePath != null) {
			ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
			icon.setFitWidth(64);
			icon.setFitHeight(64);
			icon.setLayoutX(420);
			icon.setLayoutY(10);
			classInfoPane.getChildren().add(icon);
		}

		Button exit = new Button();

		configureNode(exit, 105, 10, 0, 0, null, e -> removeClassInfo(e, imageView, classInfoPane));
		exit.setText("x");
		exit.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
		exit.setTextFill(Color.WHITE);

		Button cont = new Button();
		configureNode(cont, 130, 10, 0, 0, null, e -> displayClassAbility(e, imageView, classInfoPane, id));
		cont.setText("->");
		cont.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
		cont.setTextFill(Color.WHITE);

		classInfoPane.getChildren().addAll(classTitle, classDescription, exit, cont);

		// Add the pane to the anchorPane (or your root pane)
		anchorPane.getChildren().add(classInfoPane);
	}

	private void displayClassAbility(MouseEvent e, ImageView imageView, Pane classInfoPane, String id) {
		classInfoPane.getChildren().clear();

		Button exit = new Button();

		configureNode(exit, 80, 10, 0, 0, null, event -> removeClassInfo(event, imageView, classInfoPane));
		exit.setText("x");
		exit.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
		exit.setTextFill(Color.WHITE);

		Text classTitle = new Text();
		classTitle.setFont(loadCustomFont(28));
		classTitle.setFill(Color.WHITE);

		Text classDescription = new Text();
		classDescription.setFont(Font.font("Arial", 16));
		classDescription.setFill(Color.LIGHTGRAY);
		classDescription.setWrappingWidth(380);

		switch (id) {
		case "Visionary":
			classTitle.setText("Visionary");
			classDescription.setText(
					"The visionary can cast a light beam, to dissapate the darkness of the dungeon termporarily. Cooldown of 40/80/130s depeding on diffculty. "
							+ "Her magic abilites also allow her to teleport, but only to her last few steps, with a cooldown of 20/60/110s depending on diffculty");
			break;
		case "Runner":
			classTitle.setText("Runner");
			classDescription
					.setText("The runner has the ability to channel a burst of energy, being able to move faster than "
							+ "other characters temporarily. Using this multiple times creates fatigue, and their "
							+ "sprint time decreases over-time. Starting at 8/7/6 seconds and decreasing .5 seconds each use.");
			break;
		case "Psychic":
			classTitle.setText("Psychic");
			classDescription.setText(
					"The psychic has the ability to traverse through walls, for a period of time, at the cost of exhaustion from this strong power, After multiple uses she will have a "
							+ "lock-out and struggle to move or see. 6s to move, at the cooldown of 40/80/110s.");
			break;
		case "Nomad":
			classTitle.setText("Nomad");
			classDescription.setText("The nomad can sense the breeze of the exit from its "
					+ "faint trace and smell, 60/200/250s cooldown. The cooldown for this ability increases by 20/30/40s each use.");
			break;
		default:
			classTitle.setText("idk");
			classDescription.setText("No information available.");
			break;
		}

		classDescription.setLayoutX(10);
		classDescription.setLayoutY(70);

		classTitle.setLayoutX(10);
		classTitle.setLayoutY(30);

		classInfoPane.getChildren().addAll(classDescription, exit, classTitle);

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
			if (imagePath.contains("statue")) {
				imageView.setOnMouseEntered(event -> {
					imageView.getStyleClass().add("image-hover-effect");
				});

				imageView.setOnMouseExited(event -> {
					imageView.getStyleClass().remove("image-hover-effect");
				});
			}
		} else if (node instanceof TextField textField) {
			textField.setPrefWidth(width);
			textField.setPrefHeight(height);
		} else if (node instanceof Text text) {
			text.setFill(javafx.scene.paint.Color.WHITE);
		}
		// Add more types as needed

		if (node instanceof ImageView imageView) {
			imageView.setOnMouseClicked(event -> {
				if (event.getButton() == MouseButton.PRIMARY) {
					if (onClick != null) {
						clickNoise();
						onClick.handle(event);
					}
				} else if (event.getButton() == MouseButton.SECONDARY) {
					// Right-click: Show class info
					displayClassInfo(imageView, imagePath);
				}
			});
		} else {
			if (onClick != null && node.getId() == null) {
				node.setOnMouseClicked(onClick);
			}
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
			display.setLayoutX(layoutX / 10 + 500);
		else if (text == "Continue")
			display.setLayoutX(layoutX / 10 + 950);
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
			// Create the FXMLLoader and set the location
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/introscreen.fxml"));

			// Optional: set a custom controller if needed
			loader.setController(new IntroController()); // if you need to pass something in the constructor

			// Load the root node from the FXML
			Parent root = loader.load();

			// Create a new scene with the loaded root
			Scene scene = new Scene(root);

			// Get the current stage and set the new scene
			Stage stage = (Stage) anchorPane.getScene().getWindow();
			stage.setScene(scene);
			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Switch to the actual maze, (tutorial will be incl in main screen)
	private void startGame(MouseEvent event) {

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		GameScreen gameScreen = new GameScreen(this);
		try {
			introScreen.getPlayer().stop();
			gameScreen.start(stage);
		} catch (Exception e) {
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

	private void clickNoise() {
		AudioClip sound = new AudioClip(getClass().getResource("/sounds/doorClick.mp3").toExternalForm());
		sound.setVolume(200);
		sound.play();
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

	public IntroScreen getIntroScreen() {
		return introScreen;
	}

	public void setIntroScreen(IntroScreen introScreen) {
		this.introScreen = introScreen;
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
