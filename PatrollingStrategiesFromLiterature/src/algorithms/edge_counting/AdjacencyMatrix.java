package algorithms.edge_counting;

public class AdjacencyMatrix<T> {
	
	private T[][] matrix;
	
	public AdjacencyMatrix(int size){
		matrix = (T[][]) new Object[size][size];
		
	}
	
	public T get(int source, int target){
		return matrix[source][target];
	}
	
	public void set(int source, int target, T value){
		matrix[source][target] = value;
		matrix[target][source] = value;
	}
	
	public void setAll(T value){
		for(int i = 0; i < matrix.length; i++){
			for(int j = 0; j < matrix[0].length; j++){
				matrix[i][j] = value;
			}
		}
	}

}
