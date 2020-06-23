package algorithms.rodrigo.refactoring;

import java.util.AbstractList;

import algorithms.rodrigo.NodesMemories;

public class AmbientMemory<T> extends AbstractList<T>{
	
	private NodesMemories<T>[] ambientMemory;

	public AmbientMemory(int nodesNumber) {
		
		ambientMemory = (NodesMemories<T>[]) new Object[nodesNumber];
		
		for(int i = 0; i< nodesNumber; i++){
			ambientMemory[i] = new NodesMemories<T>(nodesNumber);
		}
	}

	@Override
	public T get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

}
