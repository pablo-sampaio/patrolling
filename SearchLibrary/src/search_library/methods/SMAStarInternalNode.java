package search_library.methods;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import search_library.AbstractSearchNode;


class SMAStarInternalNode {
	private static final int INFINITY = Integer.MAX_VALUE;

	private static int nextTime = 0;
	
	private final AbstractSearchNode node;
	private final SMAStarInternalNode parent;
	
	private boolean isGoal;    //to avoid multiple calls to "node.isGoal()" 
	
	private List<AbstractSearchNode> successorStates;  //not efficient, should generate one-by-one (idea: keep only the action to be applied)
	private List<SMAStarInternalNode> successorBranches;
	private int successorIndex;
	private int successorBranchesCount;

	private int minForgottenEval;
	private int minForgottenIndex;   //index of the forgotten branch in the list of branches  
	
	private int eval;
	private int time;
	
	boolean completedFirstIteration; //completed one iteration through its successors (without regenerating any of them)
	boolean inBorder;                //indicates if this node is in the border
	
	int expandedNode = 0;

	/**
	 * Constructor for the start node.
	 */
	SMAStarInternalNode(AbstractSearchNode startNode) {
		this.node = startNode;
		this.parent = null;

		this.isGoal = startNode.isGoal();
		
		this.successorIndex = -1;
		this.successorBranchesCount = 0;
		
		this.minForgottenEval = INFINITY;
		this.minForgottenIndex = -1;

		this.eval = startNode.getFutureCostEstimate();
		this.time = nextTime++;
		
		this.completedFirstIteration = false;
		this.inBorder = false;
	}

	/**
	 * Constructor for non-start nodes. 
	 */
	SMAStarInternalNode(AbstractSearchNode n, SMAStarInternalNode father) {
		this.node = n;
		this.parent = father;
		
		this.isGoal = false;
		
		this.successorIndex = -1;
		this.successorBranchesCount = 0;
		
		this.minForgottenEval = INFINITY;
		this.minForgottenIndex = -1;
		
		this.eval = -1;
		this.time = nextTime++;
		
		this.completedFirstIteration = false;
		this.inBorder = false;
	}

	SMAStarInternalNode getParent() {
		return parent;
	}
	
	boolean isGoal() {
		return this.isGoal;
	}

	int getEval() {
		return eval;
	}
	
	void setEvalInfinity() {
		this.eval = INFINITY;
	}

	AbstractSearchNode getNode() {
		return node;
	}

	int getTime() {
		return time;
	}
	
	boolean completed() {
		return completedFirstIteration;
	}

    //all commented codes below treat node completion as a finished "pass" through all branches, regenerating all those that were cut
//	boolean completed() {
//		return !hasNextSuccessor();
//	}
//
//	private boolean hasNextSuccessor() {
//		if (this.successorBranches == null) { 
//			expandAll();
//		}
//		int size = successorBranches.size();
//
//		//advances "successorIndex" to the position of the next "null" value
//		//i.e., it looks for unexpanded branches (to regenerate them, maybe)
//		while (successorIndex < size && successorBranches.get(successorIndex) != null) {
//			successorIndex ++;
//		}
//		
//		return successorIndex < size;
//	}
	
	private int emptyBranchCyclicSearch(List<SMAStarInternalNode> list, int startPosition) {
		int size = list.size();
		int pos = startPosition % size;
		for (int inc = 0; inc < size; inc++) {
			if (list.get(pos) == null) {
				return pos; 
			}
			pos = (pos + 1) % size;
		}
		throw new Error("No sucessor found (sucessors: " + successorStates.size() + ", count: " + successorBranchesCount + ")!");
	}
	
	SMAStarInternalNode nextSuccessor(int maxDepth) {
		if (this.successorStates == null) { 
			expandAll();
			this.expandedNode = successorStates.size();
		}
		
		if (! hasEmptyBranches()) {
			return null;
		}
		
		this.successorIndex = emptyBranchCyclicSearch(successorBranches, successorIndex);
		
		AbstractSearchNode succState = this.successorStates.get(successorIndex); //note: should be a real regeneration of the successor (uniquely) identified by this index
		SMAStarInternalNode succBranch = new SMAStarInternalNode(succState, this);
		
		this.successorBranches.set(successorIndex, succBranch);
		this.successorBranchesCount ++;
		
		//if it is regenerating the minimum forgotten branch
		if (this.successorIndex == this.minForgottenIndex) {
			this.minForgottenEval = INFINITY;
			this.minForgottenIndex = -1;
		}

		succBranch.isGoal = succBranch.node.isGoal();
		
		if (! succBranch.isGoal && succBranch.node.getDepth() == maxDepth) {
			succBranch.eval = INFINITY;		
		} else {
			succBranch.eval = Math.max(this.eval, succBranch.node.getFutureCostEstimate());
		}
		
		successorIndex ++;
		if (!completedFirstIteration) {
			completedFirstIteration = (successorIndex == successorBranches.size()); //once set true, it is not set false anymore
		}
		
		return succBranch;
	}

	private void expandAll() {
		this.successorStates = node.expand();
		
		int size = successorStates.size();
		this.successorBranches = new ArrayList<>(size);

		for (int i = 0; i < size; i ++) {
			this.successorBranches.add(null); //all branches initialized with "null"
		}
		
		this.successorIndex = 0;
		this.successorBranchesCount = 0;
	}

	void backup(TreeSet<SMAStarInternalNode> border) {
		if (completedFirstIteration) {
			//here, eval is set to the minimum among the "live" branches and the best forgotten one 
			int minSonEval = this.minForgottenEval;
			for (SMAStarInternalNode child : this.successorBranches) {
				if (child != null && child.eval < minSonEval) {
					minSonEval = child.eval;
				}
			}
			
			if (minSonEval != this.eval) {
				border.remove(this);     //remove to reinsert, to reorder "this" node properly in the border
				this.eval = minSonEval;  //probably, must be done here, after removal, because the eval may be used to find "this" internally
				border.add(this); 
				
				if (this.parent != null) {
					this.parent.backup(border);
				}
			}
		}
	}
	
	void removeSuccessor(SMAStarInternalNode succ) {
		int succIndex = this.successorBranches.indexOf(succ);

		this.successorBranches.set(succIndex, null); //doesn't change "successorStates"
		this.successorBranchesCount --;
		
		if (succ.eval < this.minForgottenEval) {
			this.minForgottenEval = succ.eval;
			this.minForgottenIndex = succIndex;
		}
	}
	
	boolean hasEmptyBranches() {
		return this.successorBranchesCount < this.successorStates.size(); //alternative: return this.successorBranches.contains(null);
	}
	
	boolean isLeaf() {
		return this.successorBranchesCount == 0; //alternative: test if the node was not expanded or if all branches were cut (i.e., they are null) 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof SMAStarInternalNode)) {
			return false;
		}
		
		SMAStarInternalNode other = (SMAStarInternalNode)obj;
		
		return this.node.equals(other.node);
	}
	
	@Override
	public String toString() {
		String evalStr = (eval == INFINITY)? "inf" : String.valueOf(eval);
		String minForgottenStr = (minForgottenEval == INFINITY)? "inf" : String.valueOf(minForgottenEval);
		return this.node.toString() + ":f=" + evalStr + "/fgt=" + minForgottenStr + "";
	}

}
