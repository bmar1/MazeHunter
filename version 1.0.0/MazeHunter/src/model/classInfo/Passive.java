package model.classInfo;

public class Passive {

	private String description;
	private String classType;
	private int cooldown;
	private long lastUsedTime;
	private boolean isActive;
	
	public Passive(String description, String classType, int cooldown) {
		super();
		this.description = description;
		this.classType = classType;
		this.cooldown = cooldown;
		this.lastUsedTime = 0;
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
		 return (System.currentTimeMillis() - lastUsedTime) < cooldown * 1000;
	}

	
	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
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
	