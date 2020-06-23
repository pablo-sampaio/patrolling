package yaps.experimentation;

import java.util.ArrayList;
import yaps.util.DoubleList;

/**
 * This class keeps the results of simulations run by {@link ExperimentPerformer} for a specific algorithm
 * in a specific map measured by a specific metrics. The results are kept as a {@link DoubleList} for 
 * each size of society configured in the map settings ({@link ExperimentPerformer#getMaps()}). 
 *  
 * @author Pablo Sampaio
 * @author Rodrigo de Sousa
 *
 */
public class MetricReport {

	private String name;
	private ArrayList<DoubleList> doubleLists;

	MetricReport(String name) {
		this.name = name;
		this.doubleLists = new ArrayList<DoubleList>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addValues(DoubleList values) {
		this.doubleLists.add(values);
	}

	public double getMean(int index) {
		return this.doubleLists.get(index).mean();
	}

	public double getStdDev(int index) {
		return this.doubleLists.get(index).standardDeviation();
	}
	
	public double getHighestMean(){
		double highest = 0;
		
		for(int i = 0; i < doubleLists.size(); i++){
			if(getMean(i) > highest){
				highest = getMean(i);
			}
		}
		
		return highest;
	}
}
