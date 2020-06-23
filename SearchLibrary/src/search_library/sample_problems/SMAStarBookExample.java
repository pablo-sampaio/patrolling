package search_library.sample_problems;

import java.util.LinkedList;

import search_library.AbstractSearchNode;

/**
 * Example used to explain the SMA* in the book <i>Artificial Intelligence: A
 * Modern Approach (1st ed.) (S. Russel & P. Norvig)</i>.
 * 
 * @author Pablo A. Sampaio
 * @author Alison Carrera
 */
public class SMAStarBookExample extends AbstractSearchNode {
	public String no;

	public SMAStarBookExample() {
		this.no = "A";
		setFutureCostEstimate(12);
	}

	private SMAStarBookExample(String node, int futureEval) {
		this.no = node;
		setFutureCostEstimate(futureEval);
	}

	@Override
	public boolean isGoal() {
		if (no.equals("D") || no.equals("I") || no.equals("F") || no.equals("J")) {
			return true;
		}

		return false;
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {

		LinkedList<AbstractSearchNode> successors = new LinkedList<AbstractSearchNode>();
		SMAStarBookExample n1, n2;

		if (no.equals("A")) {
			n1 = new SMAStarBookExample("B", 15);
			n1.setFatherNode(this);

			n2 = new SMAStarBookExample("G", 13);
			n2.setFatherNode(this);

			successors.add(n1);
			successors.add(n2);
		}

		if (no.equals("B")) {
			n1 = new SMAStarBookExample("C", 25);
			n1.setFatherNode(this);

			n2 = new SMAStarBookExample("D", 20);
			n2.setFatherNode(this);

			successors.add(n1);
			successors.add(n2);
		}

		if (no.equals("C")) {
			n1 = new SMAStarBookExample("E", 35);
			n1.setFatherNode(this);

			n2 = new SMAStarBookExample("F", 30);
			n2.setFatherNode(this);

			successors.add(n1);
			successors.add(n2);
		}

		if (no.equals("G")) {
			n1 = new SMAStarBookExample("H", 18);
			n1.setFatherNode(this);

			n2 = new SMAStarBookExample("I", 24);
			n2.setFatherNode(this);

			successors.add(n1);
			successors.add(n2);
		}

		if (no.equals("H")) {
			n1 = new SMAStarBookExample("J", 24);
			n1.setFatherNode(this);

			n2 = new SMAStarBookExample("K", 29);
			n2.setFatherNode(this);

			successors.add(n1);
			successors.add(n2);
		}

		return successors;
	}

	@Override
	public String toString() {
		return no;
	}

}
