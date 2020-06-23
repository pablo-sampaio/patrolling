package algorithms.balloon_dfs;

class EdgeInfo implements Comparable {
	
	private int target;
	private int visitTime;
	private int pressure;
	private boolean hasBeenVisited;
	
	public EdgeInfo(int target){
		this.setTarget(target);
		setVisitTime(0);
		setHasBeenVisited(false);
	}

	public int getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(int visitTime) {
		this.visitTime = visitTime;
	}

	public int getPressure() {
		return pressure;
	}

	public void setPressure(int pressure) {
		this.pressure = pressure;
	}

	public boolean hasBeenVisited() {
		return hasBeenVisited;
	}

	public void setHasBeenVisited(boolean hasBeenVisited) {
		this.hasBeenVisited = hasBeenVisited;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	@Override
	public int compareTo(Object obj) {
		
		return Integer.compare(visitTime, ((EdgeInfo)obj).getVisitTime());
	}

}
