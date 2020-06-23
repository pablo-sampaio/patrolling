package yaps.map_library;

/**
 * Class that holds information about the node in the simulador, including dynamic 
 * information set during simulation.
 * 
 * @author Pablo A. Sampaio
 */
public class NodeInfo {
	public final int index;
	private String label;
	private int pos_x;
	private int pos_y;
	
	private double importance;
	private String informationMark;

	//add other properties here... 
	
	NodeInfo(int i) {
		this.index = i;
		this.set2Dposition(i % 10, i / 10);
	}

	public String getLabel() {
		return label;
	}

	void setLabel(String label) {
		this.label = label;
	}

	public int getPosX() {
		return pos_x;
	}

	public int getPosY() {
		return pos_y;
	}
	
	void set2Dposition(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
	}

	public double getImportance() {
		return importance;
	}

	void setImportance(double importance) {
		assert(importance >= 0);
		this.importance = importance;
	}

	public String getInformationMark() {
		return informationMark;
	}

	void setInformationMark(String informationMark) {
		this.informationMark = informationMark;
	}

	public String toString() {
		return String.format("{node %d (%s): (%d, %d)}", index, label, pos_x, pos_y);		
	}
}
