package model;

import model.classInfo.*;
import view.CooldownIcon;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import controller.GameController;
import controller.PlayerController;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import model.Difficulty;

public class Userclass {

	private String userClassName;
	private Ability ability;
	private Passive passive;
	private Debuff debuff;
	private String classPathImg;
	private int abilityCount = 0;
	private boolean runnerMove = false;
	private boolean lockout;

	private boolean isLightBeamOnCooldown = false;
	private boolean isBeamActive = false;
	private long beamExpiryTime = 0;
	private List<Point2D> activeBeamPath = null;

	private PlayerController playerController;
	private GameController gameController;
	private CooldownIcon abilityIcon;
	private CooldownIcon passiveIcon;

	// Inital constructor call for abilites and class info to set
	public Userclass(String userClass, Ability ability, Passive passive, Debuff debuff, String classPathImg) {
		super();
		this.userClassName = userClass;
		this.ability = ability;
		this.passive = passive;
		this.debuff = debuff;
		this.classPathImg = classPathImg;

		setUserClass(userClass);
	}

	// Seperate constructor to identify other details
	public Userclass(String className, GameController gameController, PlayerController playerController) {
		this.userClassName = className;
		setUserClass(className);
		this.gameController = gameController;
		this.playerController = playerController;

		this.abilityIcon = playerController.getAbilityIcon();
		this.passiveIcon = playerController.getPassiveIcon();

	}

	/**
	 * Applies a cooldown for an ability and passive depending on the class
	 * 
	 * @param Difficulty d, the difficulty set
	 */
	public void applyDifficulty(Difficulty d) {
		switch (userClassName) {
		case "Nomad":
			ability.setCooldownTime(d == Difficulty.EASY ? 60 : d == Difficulty.MEDIUM ? 200 : 250);
			passive.setCooldown(d == Difficulty.EASY ? 70 : d == Difficulty.MEDIUM ? 90 : 130);
			break;

		case "Psychic":
			ability.setCooldownTime(d == Difficulty.EASY ? 40 : d == Difficulty.MEDIUM ? 3 : 140);
			passive.setCooldown(d == Difficulty.EASY ? 50 : d == Difficulty.MEDIUM ? 100 : 160);

			break;

		case "Runner":
			ability.setCooldownTime(d == Difficulty.EASY ? 40 : d == Difficulty.MEDIUM ? 80 : 140);
			break;

		case "Visionary":
			ability.setCooldownTime(d == Difficulty.EASY ? 60 : d == Difficulty.MEDIUM ? 90 : 140);
			passive.setCooldown(d == Difficulty.EASY ? 50 : d == Difficulty.MEDIUM ? 80 : 130);

			break;
		}
	}

	/**
	 * Instanties new abilities with descriptive information, ability use, and
	 * debuff, depedning on class, which sets cooldowns after
	 * 
	 * @param userClassName the class user chose
	 */
	public void setUserClass(String userclassName) {

		// get the user class based on the class name, all default values in cd
		switch (userClassName) {
		case "Nomad":
			ability = new Ability("Sense Exit", 0, "Reveals a faint directional hint toward the nearest exit.",
					"Nomad"); // !
			passive = new Passive("Warns the player before entering dead-end paths (1 min cooldown).", "Nomad", 0); // !
			debuff = new Debuff("Each use increases cooldown duration progressively.", "Nomad"); //
			classPathImg = "/images/nomad.png";
			break;

		case "Psychic":
			ability = new Ability("Phase Walk", 0, "Allows passing through walls for a short period (6s).", "Psychic");//
			passive = new Passive("Temporarily expands visibility range after staying still (2 min cooldown).",
					"Psychic", 0); // im go
			debuff = new Debuff("Using abilities too often causes temporary vision loss in fog.", "Psychic"); //
			classPathImg = "/images/psy.png";
			break;

		case "Runner":
			ability = new Ability("Sprint", 90, "Quickly dash forward twice during one movement window.", "Runner"); //
			passive = new Passive("Leaves a temporary trail showing recent movement.", "Runner", 0); // !
			debuff = new Debuff("Sprint gets shorter with repeated usage due to fatigue.", "Runner"); //
			classPathImg = "/images/runner.png";
			break;

		case "Visionary":
			ability = new Ability("Light Beam", 0, "Shoots a beam forward revealing hidden tiles briefly.",
					"Visionary"); // !
			passive = new Passive("Remembers a few recent steps and allows backtracking to them.", "Visionary", 0); // !
			debuff = new Debuff("Frequent usage disables abilities briefly for recovery.", "Visionary"); //
			classPathImg = "/images/vis.png";
			break;
		}
	}

	/**
	 * Runs the logic for the users passive, depending on class, after a duration
	 * sets ability cooldown, as well marking it inactive, and playing key ability
	 * indicators
	 * 
	 * @param userClassName the class user chose
	 * @param playerImg     to edit temporarily
	 */
	public void usePassive(Userclass userClass, ImageView playerImg) {

		if (!userClass.getPassive().isOnCooldown()) {

			userClass.getPassive().setLastUsedTime(System.currentTimeMillis());
			// start cooldown timer if not runner
			if (!userClass.getUserClass().equals("Runner")) {
				passiveIcon.startCooldown(userClass.getPassive().getCooldown());
			}

			switch (userClass.getUserClass()) {

			case "Psychic":
				System.out.println("INSIDE ABILITY LOCK PASSIVE");
				playPassive(false);
				changeRadius(1);

				PauseTransition pause = new PauseTransition(Duration.seconds(4));
				pause.setOnFinished(event -> {
					// increase fog radius temporarily

					changeRadius(-1);

				});
				pause.play();

				break;
			case "Nomad":

				int dir1 = 4;
				String dir = playerController.getDirection();
				switch (dir) {
				case "Right":
					dir1 = 2;
					break;
				case "Down":
					dir1 = 3;
					break;
				case "Left":
					dir1 = 0;
					break;
				case "Up":
					dir1 = 1;
					break;
				}
				boolean result = gameController.getMaze().isDeadEnd(playerController.getPlayer1X(),
						playerController.getPlayer1Y(), gameController.getMaze().getGrid(), dir1);
				System.out.println("Is dead end?: " + result);
				playPassive(result);
				break;

			case "Visionary":

				playPassive(false);
				returntoStep(playerController.getStepTrail(), playerController.getCellSize());
				break;

			}

		}

	}

	/**
	 * Returns the player to a designated midpoint of a step
	 * 
	 * @param stepTrail, the total trail of steps
	 * @param cellSize
	 */
	private void returntoStep(List<ImageView> stepTrail, int cellSize) {
		// Find which index gonna go back to
		if (stepTrail.size() < 2) {
			// Not enough steps to go back to, failed
			return;
		}

		// Access it's position
		int midPoint = (int) Math.floor(stepTrail.size() / 2.0);
		ImageView step = stepTrail.get(midPoint);
		int[] stepMazeCoords = (int[]) step.getUserData();

		// check if valid, if so
		if (gameController.getMaze().isPath(stepMazeCoords[0], stepMazeCoords[1], gameController.getMaze().getGrid())) {

			// remove all indexs ahead of the position we're going back to
			stepTrail.subList(midPoint, stepTrail.size()).clear();

			playerController.setPlayer1X(stepMazeCoords[0]);
			playerController.setPlayer1Y(stepMazeCoords[1]);

			// redraw maze, place player, redraw steps BEHIND the player
			playerController.redrawScene(playerController.getPlayer1X(), playerController.getPlayer1Y());

		}
	}

	/**
	 * Runs the logic for the users main ability, sets a cooldown after, calling the
	 * icon to set a specific cooldown, and after a duration removes the ability
	 * active indicator
	 * 
	 * @param userClassName the class user chose
	 * @param playerImg     to edit temporarily
	 */
	public void useAbility(Userclass userClass, ImageView playerImg) {

		if (!userClass.getAbility().isOnCooldown()) {

			setAbilityCount(getAbilityCount() + 1);
			userClass.getAbility().setLastUsedTime(System.currentTimeMillis());
			abilityIcon.startCooldown(userClass.getAbility().getCooldownTime());

			switch (userClass.getUserClass()) {

			case "Visionary":
				userClass.getAbility().setActive(true);
				setActive(5, userClass);

				castBeam();
				playAbility();

				if (abilityCount % 4 == 0) {
					setLockout(true);
					removeLock(4);

				}

				break;
			case "Psychic":
				userClass.getAbility().setActive(true);
				setActive(5, userClass);
				playAbility();

				if (abilityCount % 4 == 0) {
					// lock out of moving, decrease fog of radius for 3 seconds
					System.out.println("INSIDE ABILITY LOCK");
					PauseTransition pause = new PauseTransition(Duration.seconds(3));
					pause.setOnFinished(event -> {
						setLockout(true);

						changeRadius(-4);

						removeLock(3);
					});
					pause.play();

					// revert original view
					changeRadius(4);

				}

				playerImg.setOpacity(0.6);

				PauseTransition pause = new PauseTransition(Duration.seconds(5));
				pause.setOnFinished(event -> playerImg.setOpacity(1.0));
				pause.play();

				break;

			case "Runner":
				userClass.getAbility().setActive(true);
				setRunnerMove(false);

				playAbility();

				double baseDuration = 7.0;
				double decayPerUse = 0.5;
				double minDuration = 2.0;

				double abilityDuration = Math.max(minDuration, baseDuration - decayPerUse * abilityCount);

				setActive(abilityDuration, userClass);
				break;

			case "Nomad":

				userClass.getAbility().setActive(true);
				// find nearest exit based on current pos
				String dir = gameController.getMaze().nearestExit(gameController.getMaze().getGrid(),
						playerController.getPlayer1X(), playerController.getPlayer1Y());
				playAbility();
				// display wisp/wind trail img in associated direction

				createTrail(dir);

				setActive(4, userClass);

				userClass.getAbility().setCooldownTime(userClass.getAbility().getCooldownTime() + 20.0);

			}
			abilityCount++;
		}

	}

	/**
	 * Creates a brief indicator of the nearest exit and steps to be taken, given a direction
	 * @param string dir, direction of ability
	 */
	private void createTrail(String dir) {

		Image image = new Image("/images/arrow.png");
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(150);
		imageView.setFitWidth(150);
		imageView.setLayoutX(1000);
		imageView.setLayoutY(500);

		gameController.getRoot().getChildren().add(imageView);

		switch (dir) {
		case "UP":
			imageView.setRotate(270);
			break;
		case "DOWN":
			imageView.setRotate(90);
			break;
		case "LEFT":
			imageView.setRotate(180);
			break;
		default:
			imageView.setRotate(0);
			break; // RIGHT
		}

		PauseTransition delay = new PauseTransition(Duration.seconds(3));
		delay.setOnFinished(event -> {
			gameController.getRoot().getChildren().remove(imageView);
		});
		delay.play();

	}

	/**
	 * Casts a beam in a cone shape, creating a beam of light, and a small transition revealing all aspects of fog in the area, 
	 * as well as minimap, the path of which will be shared.
	 */
	private void castBeam() {


		if (isLightBeamOnCooldown) {
			return;
		}
		isLightBeamOnCooldown = true;

		// 1. Screen Flash Effect
		Rectangle flashOverlay = new Rectangle(gameController.getRoot().getWidth(),
				gameController.getRoot().getHeight(), Color.WHITE);
		flashOverlay.setOpacity(0.0);
		gameController.getRoot().getChildren().add(flashOverlay);

		FadeTransition fadeIn = new FadeTransition(Duration.millis(100), flashOverlay);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(0.7);

		FadeTransition fadeOut = new FadeTransition(Duration.millis(200), flashOverlay);
		fadeOut.setFromValue(0.7);
		fadeOut.setToValue(0.0);
		fadeOut.setOnFinished(e -> gameController.getRoot().getChildren().remove(flashOverlay));

		fadeIn.setOnFinished(e -> {
			fadeOut.play();

			ImageView beamImage = displayBeamImage();

	
			//After the beam dissapears, the expiry time is set, in which the scene is redrawn, 
			//to reveal the path, from the PlayerController, via gameController and fog class
			PauseTransition visualBeamDuration = new PauseTransition(Duration.millis(1000)); // 0.4 seconds
			visualBeamDuration.setOnFinished(event -> {
				playerController.getRoot().getChildren().remove(beamImage);

				setBeamActive(true);
				beamExpiryTime = System.currentTimeMillis() + 4000;
				this.activeBeamPath = calculateBeamPath();
				gameController.setBeamPath(activeBeamPath); 
				playerController.redrawScene(playerController.getPlayer1X(), playerController.getPlayer1Y());

				PauseTransition revealTimer = new PauseTransition(Duration.seconds(4));
				
				//once finished, set all to false, and tell teh control to no longer reveal
				revealTimer.setOnFinished(e2 -> {
					setBeamActive(false);
					this.activeBeamPath = null; 
					gameController.setBeamPath(null); 

					playerController.redrawScene(playerController.getPlayer1X(), playerController.getPlayer1Y());
				});
				revealTimer.play();
			});
			visualBeamDuration.play();
		});

		fadeIn.play();

	}

	/**
	 * Calculates the beam path based on the radius, and a spread factor to reveal 
	 * a wide area, but as long as the player can see.
	 */
	private List<Point2D> calculateBeamPath() {
		List<Point2D> path = new ArrayList<>();
		int beamLength = (int) (gameController.getRadius() + 2.5);
		// --- FIX: A more aggressive spread factor for a wider cone ---
		double spreadFactor = 1.7;
		double baseSpread = 0.7;

		int visibleTiles = gameController.getRadius() * 2 + 1;
		int gridCenterX = visibleTiles / 2;
		int gridCenterY = visibleTiles / 2;

		String direction = playerController.getDirection();

		for (int i = 0; i <= beamLength; i++) {
			int sliceCenterX = gridCenterX;
			int sliceCenterY = gridCenterY;


			int spread = (int) (baseSpread + (int) Math.floor((i - 1) / spreadFactor));

			for (int j = -spread; j <= spread; j++) {
				int tileX = sliceCenterX;
				int tileY = sliceCenterY;

				//Sets the tile x and y to reveal based on grid center position, according to the legnth of spread
				switch (direction) {
				case "Up":
					tileX = gridCenterX + j; 
					tileY = gridCenterY - i; 
					break;
				case "Down":
					tileX = gridCenterX + j; 
					tileY = gridCenterY + i; 
					break;
				case "Left":
					tileX = gridCenterX - i; 
					tileY = gridCenterY + j; 
					break;
				case "Right":
					tileX = gridCenterX + i;
					tileY = gridCenterY + j; 
					break;
				}

		
				path.add(new Point2D(tileX, tileY));
			
			}
		}
		return path;
	}

	/**
	 * Display a beam image.
	 */
	private ImageView displayBeamImage() {
		InputStream stream = getClass().getResourceAsStream("/images/lightbeam.png");
		if (stream == null) {
			System.err.println("Error: /images/lightbeam.png not found!");
			return null;
		}
		ImageView beamImage = new ImageView(new Image(stream));

		int beamLength = 9;
		double spreadFactor = 2.0;
		int maxSpread = (int) Math.floor((beamLength - 1) / spreadFactor);
		double beamPixelWidth = (maxSpread * 1.8) * playerController.getCellSize();
		int beamPixelLength = beamLength * playerController.getCellSize();
		int cellSize = playerController.getCellSize();


		beamImage.setPreserveRatio(false);

		//Sets the beam rotation, and length depending on direction
		switch (playerController.getDirection()) {
		case "Up":
			beamImage.setRotate(0);
			beamImage.setFitHeight(beamPixelLength);
			beamImage.setFitWidth(beamPixelWidth);
			break;
		case "Down":
			beamImage.setRotate(180);
			beamImage.setFitHeight(beamPixelLength);
			beamImage.setFitWidth(beamPixelWidth);
			break;
		case "Left":
			beamImage.setRotate(270);
			beamImage.setFitWidth(beamPixelWidth);
			beamImage.setFitHeight(beamPixelLength);
			break;
		case "Right":
			beamImage.setRotate(90);
			beamImage.setFitWidth(beamPixelWidth);
			beamImage.setFitHeight(beamPixelLength);
			break;
		}

		switch (playerController.getDirection()) {
		case "Up":
			// Move it up by half the player's size + half the beam's size
			beamImage.setTranslateY(-(cellSize / 2.0) - (beamPixelLength / 2.0));
			break;
		case "Down":
			// Move it down by half the player's size + half the beam's size
			beamImage.setTranslateY((cellSize / 2.0) + (beamPixelLength / 2.0));
			break;
		case "Left":
			// Move it left by half the player's size + half the beam's size
			beamImage.setTranslateX((cellSize - 20) - (beamPixelLength / 2.0));
			break;
		case "Right":
			// Move it right by half the player's size + half the beam's size
			beamImage.setTranslateX((cellSize - 20) + (beamPixelLength / 2.0));
			break;
		}

		beamImage.setMouseTransparent(true);
		playerController.getRoot().getChildren().add(beamImage);

		return beamImage;
	}

	private void setActive(double abilityDuration, Userclass userClass) {
		PauseTransition delay = new PauseTransition(Duration.seconds(abilityDuration));
		delay.setOnFinished(event -> userClass.getAbility().setActive(false));
		delay.play();

	}

	private void removeLock(double seconds) {
		PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
		delay.setOnFinished(event -> setLockout(false));
		delay.play();
	}

	private void playAbility() {
		String userClass = getUserClassName().toLowerCase();
		AudioClip sound = new AudioClip(
				getClass().getResource("/sounds/" + userClass + "_ability" + ".mp3").toExternalForm());
		sound.setVolume(200);
		sound.play();
	}

	private void playPassive(boolean flag) {
		AudioClip sound;
		String userClass = getUserClassName().toLowerCase();

		if (userClass.equals("nomad") && !flag) {
			sound = new AudioClip(getClass().getResource("/sounds/" + userClass + "_passive1.mp3").toExternalForm());
		} else {

			sound = new AudioClip(getClass().getResource("/sounds/" + userClass + "_passive.mp3").toExternalForm());
		}

		sound.setVolume(1.0);
		sound.play();
	}

	private void changeRadius(int amount) {

		gameController.setRadius(gameController.getRadius() + amount);
		gameController.getMinimap().update(gameController.getMaze().getGrid(), playerController.getPlayer1X(),
				playerController.getPlayer1Y(), (gameController.getRadius() / 2) + 1, gameController.getRadius());
		playerController.redrawScene(playerController.getPlayer1X(), playerController.getPlayer1Y());

	}

	public String getUserClass() {
		return userClassName;
	}

	public void setUserClassName(String userClass) {
		this.userClassName = userClass;
	}

	public Ability getAbility() {
		return ability;
	}

	public void setAbility(Ability ability) {
		this.ability = ability;
	}

	public Passive getPassive() {
		return passive;
	}

	public void setPassive(Passive passive) {
		this.passive = passive;
	}

	public Debuff getDebuff() {
		return debuff;
	}

	public boolean isLightBeamOnCooldown() {
		return isLightBeamOnCooldown;
	}

	public void setLightBeamOnCooldown(boolean isLightBeamOnCooldown) {
		this.isLightBeamOnCooldown = isLightBeamOnCooldown;
	}

	public boolean isBeamActive() {
		return isBeamActive;
	}

	public void setBeamActive(boolean isBeamActive) {
		this.isBeamActive = isBeamActive;
	}

	public long getBeamExpiryTime() {
		return beamExpiryTime;
	}

	public void setBeamExpiryTime(long beamExpiryTime) {
		this.beamExpiryTime = beamExpiryTime;
	}

	public GameController getGameController() {
		return gameController;
	}

	public void setGameController(GameController gameController) {
		this.gameController = gameController;
	}

	public List<Point2D> getActiveBeamPath() {
		return activeBeamPath;
	}

	public CooldownIcon getAbilityIcon() {
		return abilityIcon;
	}

	public void setAbilityIcon(CooldownIcon abilityIcon) {
		this.abilityIcon = abilityIcon;
	}

	public CooldownIcon getPassiveIcon() {
		return passiveIcon;
	}

	public void setPassiveIcon(CooldownIcon passiveIcon) {
		this.passiveIcon = passiveIcon;
	}

	public void setActiveBeamPath(List<Point2D> activeBeamPath) {
		this.activeBeamPath = activeBeamPath;
	}

	public String getUserClassName() {
		return userClassName;
	}

	public void setDebuff(Debuff debuff) {
		this.debuff = debuff;
	}

	public String getClassPathImg() {
		return classPathImg;
	}

	public void setClassPathImg(String classPathImg) {
		this.classPathImg = classPathImg;
	}

	public int getAbilityCount() {
		return abilityCount;
	}

	public void setAbilityCount(int abilityCount) {
		this.abilityCount = abilityCount;
	}

	public boolean isRunnerMove() {
		return runnerMove;
	}

	public void setRunnerMove(boolean runnerMove) {
		this.runnerMove = runnerMove;
	}

	public boolean isLockout() {
		return lockout;
	}

	public void setLockout(boolean lockout) {
		this.lockout = lockout;
	}

}
