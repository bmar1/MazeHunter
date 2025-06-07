package model.classInfo;

public class Debuff {

	private String description;
	private String classType;
	
	public Debuff(String description, String classType) {
		super();
		this.description = description;
		this.classType = classType;
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
	
	// use only once, will activate the logic at the start
	public void activateDebuff() {
		
	}
	
}
