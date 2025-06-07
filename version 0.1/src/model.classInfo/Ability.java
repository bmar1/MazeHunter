package model.classInfo;

public class Ability {

	private String name;
	private double cooldownTime;
	private String description;
	private String classType;
	private long lastUsedTime;
	private boolean isActive;
	
	public Ability(String name, double cooldownTime, String description, String classType) {
		super();
		this.name = name;
		this.cooldownTime = setCooldownTime(cooldownTime);
		this.description = description;
		this.classType = classType;
		this.lastUsedTime = 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCooldownTime() {
		return cooldownTime;
	}

	public double setCooldownTime(double cooldownTime) {
	     if (cooldownTime >= 0.0 && cooldownTime <= 400.0) {
	        this.cooldownTime = cooldownTime;
	     	return cooldownTime;
	     }
	     else {
	    	 this.cooldownTime = 0.0;
	    	 return 0.0;
	     }
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClassType() {
		return classType;
	}

	public void setClassType(String classType) {
		this.classType = classType;
	}
	
	
	
	public boolean isOnCooldown() {
		// returns true or false if the sys time is less than the cooldown time (if its less than yes it still has x seconds left
		 return (System.currentTimeMillis() - lastUsedTime) < cooldownTime * 1000;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	
	
}
	
	



