package yaps.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A list of double values implemented along with basic 
 * statistical operations.
 * 
 * @author Pablo A. Sampaio
 */
public class DoubleList {
	private List<Double> numbers;
	
	public DoubleList() {
		numbers = new LinkedList<Double>();
	}
	
	public DoubleList(int size) {
		numbers = new ArrayList<Double>(size);
		for (int i = 0; i < size; i++) {
			numbers.add(0.0d);
		}
	}
	
	public void add(double n) {
		numbers.add(n);
	}

	public void insertAll(DoubleList list) {
		numbers.addAll(list.numbers);
	}

	public int size() {
		return numbers.size();
	}
	
	public double get(int index) {
		return numbers.get(index);
	}
	
	public void set(int timeT, double num) {
		numbers.set(timeT, num);
	}
	
	public double max() {
		double max = Double.NEGATIVE_INFINITY;		
		for (double x : numbers) {
			if (x > max) {
				max = x;
			}
		}		
		return max;
	}
	
	public double min() {
		double min = Double.POSITIVE_INFINITY;		
		for (double x : numbers) {
			if (x < min) {
				min = x;
			}
		}		
		return min;
	}
	
	public double sum() {
		double sum = 0.0d;		
		for (double x : numbers) {
			sum += x;
		}		
		return sum;		
	}
	

	// arithmetic mean
	public double mean() {
		return average();		
	}
	public double average() {
		return sum() / size();		
	}

	// square root of the mean of the squares of the values 
	// also called "root mean square"
	public double quadraticMean() {
		double squaredSum = 0.0d;		
		for (int i = 0; i < numbers.size(); i ++) {
			squaredSum += numbers.get(i) * numbers.get(i);
		}
		return Math.pow(squaredSum/numbers.size(), 0.5d);
	}
	
	//also called "power mean"
	public double generalizedMean(double p) {
		double sum = 0.0d; //attention: 'sum' can get too big and overflow
		                   //maybe a "big decimal" library should be used (problem: can't use floating point as exponent in 'pow' operation of Java's BigDecimal)
		for (int i = 0; i < numbers.size(); i ++) {
			sum += Math.pow(numbers.get(i),p);
		}
		return Math.pow(sum/size() , 1.0d / p);
	}
	
	public double variance() {
		double sumSquareDifferences = 0.0d;
		
		double average = mean();
		double difference;
		
		for (double n : numbers) {
			difference = (n - average);
			sumSquareDifferences += (difference * difference);
		}
		
		return sumSquareDifferences / size(); //better than (size() - 1) because of the unit lists		
	}

	public double standardDeviation() {
		return Math.sqrt(variance());		
	}

	public String toString() {
		return numbers.toString();
	}
	
}
