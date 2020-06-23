package yaps.metrics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;


/**
 * This class holds a list of visits in a simulation. It is used to calculate metrics 
 * of the simulation (to measure how "efficiently" the nodes were visited).
 * <br><br>
 * This class is prepared to order visits by time (especially if they are inserted in
 * the proper order, as it is typical in the simulations). 
 * 
 * @author Pablo A. Sampaio
 */
public class VisitsList {
	private List<Visit> visitList;
	private long lastTime;
		
	public VisitsList() {
		this.visitList = new LinkedList<Visit>();
		this.lastTime = 0;
	}

	public VisitsList(List<Visit> visits) {
		this.visitList = new ArrayList<Visit>(visits.size());
		this.lastTime = 0;
		for (Visit v: visits){
			this.addVisit(v);
		}
	}
	
	public List<Visit> toList() {
		return Collections.unmodifiableList(visitList);
	}

	/**
	 * The visits must be inserted ordered by time.
	 */
	public void addVisit(Visit visit) {
		if (visit.time < this.lastTime) {
			this.orderedAddVisit(visit);
		}
		visitList.add(visit);
		this.lastTime = visit.time;
	}
	
	public void addVisit(int time, int node, int agent) {
		addVisit(new Visit(time, node, agent));
	}

	public void addVisit(int time, int node) {
		visitList.add(new Visit(time, node, -1));
	}

	public int getNumVisits() {
		return visitList.size();
	}
	
	public Visit getVisit(int index) {
		return visitList.get(index);
	}

	public VisitsList filterByAgent(int agent) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : this.visitList) {
			if (visit.agent == agent) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	public VisitsList filterByVertex(int node) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : this.visitList) {
			if (visit.node == node) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}
	
	/**
	 * Returns a new VisitList. Inclusive limits (closed interval).
	 */
	public VisitsList filterByTime(int from, int to) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : this.visitList) {
			if (visit.time >= from && visit.time <= to) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	/**
	 * Returns a new VisitList. Parameter 'from' is an inclusive limit (closed interval).
	 */
	public VisitsList filterByTime(int from) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : this.visitList) {
			if (visit.time >= from) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	private void orderedAddVisit(Visit v){	
		int left = 0;
		int right = this.visitList.size();
		int midle = (left + right)/2;
		
		Visit u = this.visitList.get(midle);
		
		while (left < right - 1) {
			if (v.time > u.time) {
				left = midle;
			} else {
				right = midle;
			}
			
			midle = (left + right)/2;
			u = this.visitList.get(midle);
		}
		
		if (v.time > u.time) {
			this.visitList.add(midle + 1, v);
		} else {
			this.visitList.add(midle, v);
		}		
	}

	@Override
	public String toString() {		
		if(this.visitList.size() < 20){
			return "VisitsList : lastTime=" + lastTime + " visitList=" + visitList;
		}		
		return "VisitsList : lastTime=" + lastTime + " visitListSize=" + this.visitList.size();
	}

	/**
	 * Merges both lists in this object, maintaining the order. 
	 */
	public void merge(VisitsList other) {
		List<Visit> newList = new ArrayList<Visit>(this.visitList.size() + other.visitList.size());
		Visit u, v;
		int i = 0, j = 0;
		
		while (i < this.visitList.size() && j < other.visitList.size()){
			u = this.visitList.get(i);
			v = other.visitList.get(j);
			
			if (u.time < v.time) {
				newList.add(u);
				i++;
			} else {
				newList.add(v);
				j++;
			}			
		}
		
		while (j < other.visitList.size()){
			newList.add(other.visitList.get(j));
			j++;
		}		
		while (i < this.visitList.size()){
			newList.add(this.visitList.get(i));
			i++;
		}

		this.lastTime = (this.lastTime > other.lastTime ? this.lastTime : other.lastTime);
		this.visitList = newList;		
	}
	
	public void saveWebplayerJson(int agentsNumber) throws IOException {
		List<List<Integer>> agentsVisits = new ArrayList<List<Integer>>();
		
		for (int i = 0; i < agentsNumber; i++) {
			List<Visit> agentVisits = this.filterByAgent(i).toList();
			
			ArrayList<Integer> visits = new ArrayList<Integer>();
			for (Visit visit : agentVisits) {
				visits.add(visit.node);
			}
			
			agentsVisits.add(visits);
		}
		
		Gson gson = new Gson();
		String json = gson.toJson(agentsVisits);
		
		File file = new File("data.json");
		PrintStream out = new PrintStream(file);		
		out.println("visits = '" + json + "';");
		out.close();
		
		System.out.println("Save file " + file.getAbsolutePath());
	}
	
}
