package view; // Or your preferred package

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.net.URL;

public class CooldownIcon {

	private final StackPane view;
	private final ImageView iconView;
	private final Circle cooldownOverlay;
	private final Label timerLabel;

	private AnimationTimer cooldownTimer;
	private MediaPlayer readySound;

	// --- NEW: STATE-TRACKING VARIABLES ---
	private boolean isTimerActive = false;
	private long cooldownEndTime = 0; // The System.currentTimeMillis() when the cooldown will be over.

	public CooldownIcon(String iconPath, double size) {
		view = new StackPane();
		view.setPrefSize(size, size);

		// 1. The Icon Image (Bottom Layer)
		Image iconImage = new Image(getClass().getResourceAsStream(iconPath));
		iconView = new ImageView(iconImage);
		iconView.setFitWidth(size);
		iconView.setFitHeight(size);

		// 2. The Cooldown Overlay (Middle Layer)
		// This is a semi-transparent black circle that will cover the icon.
		cooldownOverlay = new Circle(size / 2, new Color(0, 0, 0, 0.6));
		cooldownOverlay.setVisible(false); // Initially hidden

		// 3. The Timer Text (Top Layer)
		timerLabel = new Label();
		timerLabel.setFont(Font.font("Verdana", 24));
		timerLabel.setTextFill(Color.WHITE);

		timerLabel.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1);");
		timerLabel.setVisible(false); // Initially hidden

		// 4. Add all layers to the StackPane
		view.getChildren().addAll(iconView, cooldownOverlay, timerLabel);

		// 5. Style the container to be a circle with a border
		Circle clip = new Circle(size / 2, size / 2, size / 2);
		view.setClip(clip); // This forces the square StackPane to be rendered as a circle.
		view.setStyle("-fx-border-color: rgba(255, 255, 255, 0.5);" + "-fx-border-width: 4px;" + "-fx-border-radius: "
				+ (size / 2) + ";" // Make the border circular
		);

		StackPane.setAlignment(iconView, Pos.CENTER);
		StackPane.setAlignment(cooldownOverlay, Pos.CENTER);
		StackPane.setAlignment(timerLabel, Pos.CENTER);

		// Load the "ready" sound
		try {
			URL resource = getClass().getResource("/sounds/ability_ready.mp3"); // CHANGE TO YOUR SOUND FILE
			if (resource != null) {
				Media sound = new Media(resource.toString());
				readySound = new MediaPlayer(sound);
			}
		} catch (Exception e) {
			System.err.println("Error loading ready sound: " + e.getMessage());
		}
	}

	/**
	 * Starts the cooldown animation.
	 * 
	 * @param durationInSeconds The total duration of the cooldown.
	 */
	public void startCooldown(double durationInSeconds) {
		// If the duration is zero or less, do nothing.
		if (durationInSeconds <= 0) {
			return;
		}

		// Stop any previous timer that might be running
		if (cooldownTimer != null) {
			cooldownTimer.stop();
		}

		// --- NEW: SET THE STATE ---
		isTimerActive = true;
		cooldownEndTime = System.currentTimeMillis() + (long) (durationInSeconds * 1000);

		cooldownOverlay.setVisible(true);
		timerLabel.setVisible(true);

		cooldownTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long currentTime = System.currentTimeMillis();
				long remainingMillis = cooldownEndTime - currentTime;

				if (remainingMillis > 0) {
					// Update the text, formatted to one decimal place
					timerLabel.setText(String.format("%.1f", remainingMillis / 1000.0));
				} else {
					// Cooldown finished
					playSound();
					stopAndReset();
				}
			}
		};
		cooldownTimer.start();
	}

	/**
	 * --- NEW: A HELPER METHOD TO CLEANLY STOP THE TIMER --- This is called when
	 * the timer finishes naturally or is stopped manually.
	 */
	private void stopAndReset() {
		if (cooldownTimer != null) {
			cooldownTimer.stop();
		}
		cooldownOverlay.setVisible(false);
		timerLabel.setVisible(false);
		isTimerActive = false;
		cooldownEndTime = 0;
	}

	private void playSound() {
		if (readySound != null) {
			readySound.stop();
			readySound.play();
		}
	}

	/**
	 * --- NEW: PUBLIC METHOD TO CHECK IF THE COOLDOWN IS ACTIVE ---
	 */
	public boolean isCooldownActive() {
		return isTimerActive;
	}

	/**
	 * --- NEW: PUBLIC METHOD TO GET THE REMAINING TIME ---
	 */
	public double getRemainingCooldownSeconds() {
		if (!isTimerActive) {
			return 0.0;
		}
		long remainingMillis = cooldownEndTime - System.currentTimeMillis();
		// Return the remaining time in seconds, ensuring it's not negative.
		return Math.max(0, remainingMillis / 1000.0);
	}

	public StackPane getView() {
		return view;
	}
}