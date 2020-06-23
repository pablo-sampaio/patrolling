package yaps.strategies.evolutionary.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import yaps.graph.Graph;
import yaps.graph.Path;
import yaps.util.RandomUtil;

/**
 * This class implements the Fungal Colony based method for calculating graph
 * equipartition.
 * 
 * @author V&iacute;tor Torre&atilde;o & Diogo Melo
 *
 */
public class FungalColonyEquipartition extends GraphEquipartition {
	
	private boolean addPath;

	public FungalColonyEquipartition(Graph graph, List<Integer> centers, boolean addPath) {
		super(graph, centers);
		this.addPath = addPath;
	}

	@Override
	public HashMap<Integer, HashSet<Integer>> getPartitions() {
		Integer[] nodeColony = new Integer[getGraph().getNumNodes()];
		for( Integer n: GraphUtils.getNodeList(getGraph()) ){
			if(centers.contains(n)){
				nodeColony[n] = n;
			}else{
				nodeColony[n] = -1;
			}
		}
		
		HashMap<Integer, HashSet<Integer>> cntrColony = new
				HashMap<Integer, HashSet<Integer>>();

		HashMap<Integer, List<Integer>> cntrNeigbrListMap = new
				HashMap<Integer, List<Integer>>();
		
		for(Integer c: centers){
			cntrNeigbrListMap.put(c, GraphUtils.getOrderedDistanceList(getGraph(), this.shortestPaths, c));
			cntrColony.put(c, new HashSet<Integer>());
		}
		
		boolean loop = true;
		while(loop){
			loop = false;
			for(Integer center: centers){
				List<Integer> cList = cntrNeigbrListMap.get(center);
				if(includeNext(center, nodeColony, cList)){
					loop = true;
					continue;
				}
				loop = loop || false;

			}
		}

		if(addPath){
			for(Integer n: GraphUtils.getNodeList(getGraph())){
				Path p = this.shortestPaths.getPath(nodeColony[n], n);
				if (p != null) {
					cntrColony.get(nodeColony[n]).addAll(p);
				}	
			}
		}else{
			for(Integer n: GraphUtils.getNodeList(getGraph())){
				cntrColony.get(nodeColony[n]).add(n);
			}
		}
		
		List<Integer> graphNodes = GraphUtils.getNodeList(getGraph());
		for(Integer c: centers){
			while(cntrColony.get(c).size() < 2){
				Integer destination = RandomUtil.chooseAtRandom(graphNodes);
				Path path = this.shortestPaths.getPath(c, destination);
				if (path != null) {
					cntrColony.get(c).addAll(path);
				}
			}
		}
		
		return cntrColony;
	}
	
	private static boolean includeNext(Integer cntr, Integer[] cntrList, List<Integer> ngborList){

		while(!ngborList.isEmpty()){

			Integer n = ngborList.remove(0);

			if(cntrList[n].equals(-1)){
				cntrList[n] = cntr;
				return true;
			}else{
				continue;
			}

		}

		return false;
	}

}
