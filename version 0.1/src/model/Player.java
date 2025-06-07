package model;

import model.classInfo.Ability;
import model.classInfo.Debuff;
import model.classInfo.Passive;

import java.util.*;
import java.util.List;

import controller.GameController;
import controller.PlayerController;
import javafx.animation.PauseTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;

@SuppressWarnings("unused")
public class Player {

	private String name;
	private double speed = 1.0;
	private Userclass userClass;
	private int playerID;

	private PlayerController playerController;
	private GameController gameController;

	public Player(String name, PlayerController playerController, GameController gameController, int playerID) {
		this.name = name;
		this.playerID = playerID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public Userclass getUserClass(String className) {

		// get the user class based on the class name, all default values in cd
		switch (className) {
		case "Nomad":
			userClass = new Userclass("Nomad",
					new Ability("Sense Exit", 0, "Can faintly sense which area the exit is located in.", "Nomad"),
					new Passive("Can sense if a path leads to a dead end (2 min cooldown).", "Nomad", 0),
					new Debuff("Cooldown increases by 20s (Easy), 30s (Medium), 40s (Hard) per use.", "Nomad"),
					"images/nomad.png");
			break;

		case "Psychic":
			userClass = new Userclass("Psychic",
					new Ability("Phase Walk", 0,
							"Can traverse through walls for 6 seconds (2x per maze, 3x for size >30).", "Psychic"),
					new Passive("Can briefly increase radius of fog of war after a short delay (2 min cooldown).",
							"Psychic", 0),
					new Debuff("After multiple ability uses, loses visibility in the fog and needs recovery time.",
							"Psychic"),
					"images/psychic.png");
			break;

		case "Runner":
			userClass = new Userclass("Runner",
					new Ability("Sprint", 90, "Gains a movement speed buff for 5 seconds.", "Runner"),
					new Passive("Leaves a glowing trail of footsteps that lasts for 3-4 steps.", "Runner", 0),
					new Debuff("Sprint speed decreases with repeated usage due to exhaustion.", "Runner"),
					"images/visionary.png"); // set back to runner.png
			break;

		case "Visionary":
			userClass = new Userclass("Visionary",
					new Ability("Light Beam", 0, "Casts a light beam in the facing direction, clearing fog for 5s.",
							"Visionary"),
					new Passive("Can see last 4-5 steps even in fog, and return to them using P.", "Visionary", 0),
					new Debuff("After several uses, must recover for 4s before abilities can be used again.",
							"Visionary"),
					"/images/visionary.png");
			break;
		}

		setUserClass(userClass);

		return userClass;
	}

	public void useAbility(Userclass userClass, ImageView playerImg) {
		userClass.getAbility().setActive(true);

		if (!userClass.getAbility().isOnCooldown()) {

			switch (userClass.getUserClass()) {

			case "Visionary":
				// using x and y, determine players currently direction (i.e if its 16:15 that
				// must mean they are facing north
				// cast straight line to reveal fog of war, then simply updateFog after x amount
				// of seconds

				break;
			case "Psychic":
				playerImg.setOpacity(0.6);

				PauseTransition pause = new PauseTransition(Duration.seconds(6));
				pause.setOnFinished(event -> playerImg.setOpacity(1.0));
				pause.play();

				PauseTransition psych = new PauseTransition(Duration.seconds(6));
				psych.setOnFinished(event -> userClass.getAbility().setActive(false));
				psych.play();
				break;

			case "Runner":
				PauseTransition ability = new PauseTransition(Duration.seconds(5));
				ability.setOnFinished(event -> userClass.getAbility().setActive(false));
				ability.play();
				break;
			}
			// logic for actually executing this ability in the game frame
			// check if the ability is on cd, if not execute it in the game (using
			// controllers etc)
			// reset the timer, by doing lastUsedTime = sustem.currentTimeMillis(), and if
			// its less than the given cd time above, it means
			// the ability is still on cd
		}
	}

	public void setUserClass(Userclass userClass) {
		this.userClass = userClass;
	}

}
