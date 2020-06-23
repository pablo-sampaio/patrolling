package search_library.sample_problems;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import search_library.AbstractSearchNode;
import search_library.methods.BreadthFirstSearch;
import search_library.methods.UniformCostSearch;
import search_library.sample_problems.RubiksCube.Actions;

public class RubiksCubeSearchNode extends AbstractSearchNode {
	private RubiksCube state;
	private Actions lastAction;
	private Set<Actions> actionsAvailable;

	public RubiksCubeSearchNode() {
		this.state = new RubiksCube();
		this.lastAction = null;
		this.actionsAvailable = new HashSet<Actions>(Arrays.asList(RubiksCube.Actions.values()));
	}

	public RubiksCubeSearchNode(RubiksCube startState, Set<Actions> actions) {
		this.state = new RubiksCube(startState);
		this.lastAction = null;
		this.actionsAvailable = actions;
	}

	private RubiksCubeSearchNode(RubiksCubeSearchNode father, Actions action) {
		this.state = new RubiksCube(father.state);

		this.state.applyAction(action);
		this.lastAction = action;
		this.actionsAvailable = father.actionsAvailable;

		setFatherNode(father);
		setCurrentCost(this.state.getEffectiveMoves());
	}

	@Override
	public boolean isGoal() {
		return state.isSolution();
	}

	@Override
	public LinkedList<AbstractSearchNode> expand() {
		LinkedList<AbstractSearchNode> childStates = new LinkedList<>();
		RubiksCubeSearchNode child;

		for (Actions action : this.actionsAvailable) {
			child = new RubiksCubeSearchNode(this, action);
			childStates.add(child);
		}

		return childStates;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RubiksCubeSearchNode)) {
			return false;
		}
		RubiksCubeSearchNode other = (RubiksCubeSearchNode) o;
		return this.state.equals(other.state);
	}

	@Override
	public int hashCode() {
		return this.state.hashCode();
	}

	public String toString() {
		String str = "";

		if (this.lastAction != null) {
			str += this.getFatherNode().toString();
			str += "MOVE: " + this.lastAction + "\n";
			str += "\n";
		}

		return str + this.state.toString() + "Effective moves: " + this.state.getEffectiveMoves() + "\n";
	}

}
