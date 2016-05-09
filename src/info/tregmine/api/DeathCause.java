package info.tregmine.api;

public enum DeathCause {
	ADMIN("admin");
	
	private String name;
	
	private DeathCause(String s){
		this.name = s;
	}
	
	public String getName(){
		return this.name;
	}
}
