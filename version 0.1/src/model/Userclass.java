package model;

import model.classInfo.*;
import model.Difficulty;

public class Userclass {

	private String userClass;
	private Ability ability;
	private Passive passive;
	private Debuff debuff;
	private String classPathImg;

	public Userclass(String userClass, Ability ability, Passive passive, Debuff debuff, String classPathImg) {
		super();
		this.userClass = userClass;
		this.ability = ability;
		this.passive = passive;
		this.debuff = debuff;
		this.classPathImg = classPathImg;
	}

	public void applyDifficulty(Difficulty d) {
		switch (userClass) {
		case "Nomad":
			ability.setCooldownTime(d == Difficulty.EASY ? 60 : d == Difficulty.MEDIUM ? 200 : 320);
			//passive.setCooldown(d == Difficulty.EASY ? 120 : d == Difficulty.MEDIUM ? 90 : 60); //the passive for this increases the cd of the ability by 20, 30 and 40s respectiely
			break;

		case "Psychic":
			ability.setCooldownTime(d == Difficulty.EASY ? 30 : d == Difficulty.MEDIUM ? 20 : 10);
			passive.setCooldown(d == Difficulty.EASY ? 120 : d == Difficulty.MEDIUM ? 90 : 60);
			break;

		case "Runner":
			ability.setCooldownTime(d == Difficulty.EASY ? 90 : d == Difficulty.MEDIUM ? 60 : 30);
			passive.setCooldown(d == Difficulty.EASY ? 100 : d == Difficulty.MEDIUM ? 70 : 40);
			break;

		case "Visionary":
			ability.setCooldownTime(d == Difficulty.EASY ? 120 : d == Difficulty.MEDIUM ? 80 : 40);
			passive.setCooldown(d == Difficulty.EASY ? 110 : d == Difficulty.MEDIUM ? 70 : 30);
			break;
		}
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
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

	public void setDebuff(Debuff debuff) {
		this.debuff = debuff;
	}

	public String getClassPathImg() {
		return classPathImg;
	}

	public void setClassPathImg(String classPathImg) {
		this.classPathImg = classPathImg;
	}

}
