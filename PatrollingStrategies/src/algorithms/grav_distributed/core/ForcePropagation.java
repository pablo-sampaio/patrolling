package algorithms.grav_distributed.core;


public enum ForcePropagation {
	NODE_NO_DISTANCE ("NodeX"),
	NODE ("Node"),
	EDGE ("Edge"),
	MIXED ("Mixed");
	
	private String identifier; 
	
	private ForcePropagation(String s) {
		identifier = s;
	}
	
	public String toString() {
		return identifier;
	}
}
