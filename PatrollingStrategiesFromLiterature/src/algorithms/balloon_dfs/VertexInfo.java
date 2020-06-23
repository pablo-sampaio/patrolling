package algorithms.balloon_dfs;


class VertexInfo {
	
	private int agentId;
	private int visitTime;
	
	public VertexInfo(){
		setVisitTime(0);
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public int getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(int time) {
		this.visitTime = time;
	}

}
