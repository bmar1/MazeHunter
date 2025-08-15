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
	private int abilityCount;
	private boolean runnerMove = false;
	private boolean lockout;
	private String direction = "Right";

	private PlayerController playerController;
	private GameController gameController;

	public Player(String name, PlayerController playerController, GameController gameController, int playerID) {
		this.name = name;
		this.playerID = playerID;
		setDirection("Right");
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

	public int getAbilityCount() {
		return abilityCount;
	}

	public void setAbilityCount(int abilityCount) {
		this.abilityCount = abilityCount;
	}


	public void setUserClass(Userclass userClass) {
		this.userClass = userClass;
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

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	
	
	

}
