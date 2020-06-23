package yaps.graph.algorithms;

import java.util.LinkedList;
import java.util.List;

import org.python.core.PyList;
import org.python.util.PythonInterpreter;

import yaps.graph.Edge;
import yaps.graph.Graph;


public class WeightedMatching extends GraphAlgorithm {
	private boolean perfectMatch;
	private boolean maxWeight;
	private int[] mate;

	public WeightedMatching(Graph g, boolean maxWeight, boolean perfectMatchOnly) {
		super(g);
		this.maxWeight = maxWeight;
		this.perfectMatch = perfectMatchOnly;
	}
	
	public int getMatched(int node) {
		return mate[node];
	}
	
	public List<Edge> getMatching() {
		List<Edge> list = new LinkedList<Edge>();
		int numNodes = this.graph.getNumNodes();
		
		for (int i = 0; i < numNodes; i++) {
			if (i < mate[i]) {
				list.add(new Edge(i, mate[i], graph.getEdgeLength(i, mate[i]), false));
			}
		}
		
		return list;
	}

    public void compute() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        
        interpreter.exec("aux1 = sys.path[0]");
        interpreter.exec("directory = aux1[0 : aux1.rfind('\\\\')]"); //directory should point to "lib" directory
        interpreter.exec("sys.path.append(directory)"); //("sys.path.append('C:\\Users\\Pablo\\Documents\\Projetos\\Projetos de Patrulha\\SimplePatrol')");
        //interpreter.exec("print sys.path");
        
        interpreter.exec("import mwmatching");

        interpreter.set("aux1", new PyList( convertToEdgeList() ));
        
        if (this.perfectMatch) {
        	interpreter.exec("mt = mwmatching.maxWeightMatching(aux1, True)");
        } else {
        	interpreter.exec("mt = mwmatching.maxWeightMatching(aux1, False)");
        }
        
        PyList m = (PyList) interpreter.get("mt");
        this.mate = new int[graph.getNumNodes()];
        
        for (int i = 0; i < mate.length; i++) {
			this.mate[i] = (Integer) m.get(i);
		}
    	
    	interpreter.close();
    }
    
    private List<int[]> convertToEdgeList() {
    	List<int[]> listTuples = new LinkedList<int[]>();
    	int numVertices = this.graph.getNumNodes();
    	
    	for (int v = 0; v < numVertices; v++) {
    		for (Edge e : graph.getOutEdges(v)) {
    			//to avoid inserting an edge twice 
    			if (v < e.getTarget()) {
    				if (maxWeight) {
    					listTuples.add(new int[]{v, e.getTarget(), e.getLength()});	
    				} else {
    					listTuples.add(new int[]{v, e.getTarget(), - e.getLength()});
    				}
    			}
    		}
			
		}
    	
    	return listTuples;
    }
    
}
