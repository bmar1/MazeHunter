package model.classInfo;

public class Passive {

	private String description;
	private String classType;
	private int cooldown;
	
	public Passive(String description, String classType, int cooldown) {
		super();
		this.description = description;
		this.classType = classType;
		this.cooldown = cooldown;
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
	

	
	public int getCooldown() {
		return cooldown;
	}

	public void setCooldown(int cooldown) {
		this.cooldown = cooldown;
	}

	// use only once, will activate the logic at the start
	public void activatePassive() {
		
	}
	
}
	