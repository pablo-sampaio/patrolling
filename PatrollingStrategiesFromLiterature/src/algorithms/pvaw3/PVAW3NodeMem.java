package algorithms.pvaw3;

/**
 * Esta classe representa as informa��es que s�o guardadas em cada n� no PVAW3
 *
 * @author Rodrigo de Sousa
 */
class PVAW3NodeMem {
	
	private int visitTime; //sigma
	private int t1; //taus
	private int t2;
	private int nextVertex; //pr�ximo v�rtice do ciclo do l�der
	
	public PVAW3NodeMem(){
		setVisitTime(0);
		setT1(0);
		setT2(0);
		setNextVertex(-1);
	}

	public int getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(int time) {
		this.visitTime = time;
	}

	public int getT1() {
		return t1;
	}

	public void setT1(int t1) {
		this.t1 = t1;
	}

	public int getT2() {
		return t2;
	}

	public void setT2(int t2) {
		this.t2 = t2;
	}

	public int getNextVertex() {
		return nextVertex;
	}

	public void setNextVertex(int nextVertex) {
		this.nextVertex = nextVertex;
	}

}
