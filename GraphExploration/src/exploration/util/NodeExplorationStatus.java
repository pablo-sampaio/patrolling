package exploration.util;

public enum NodeExplorationStatus {
	//UNKNOWN, 
	DIRECT_ANCESTOR ("ANCST"),
	NOT_EXPANDED ("--"),
	PARTIALLY_EXPANDED ("EXPS_PART"),			//an agent has arrived and has started expanding (but there is no information about explored neighbors)
	EXPANDED_PARTIALLY_EXPLORED ("EXPL_PART"), 	//one or more agents have started to explore some of its neighbors (other neighbors are not explored yet)
	FULLY_EXPLORED ("EXPL_FULL");				//all neighbors where explored (or are in exploration by one or more agents)
	
	private String str;
	
	private NodeExplorationStatus(String shortName) {
		this.str = shortName;
	}
	
	public String toString() {
		return str;
	}
}
