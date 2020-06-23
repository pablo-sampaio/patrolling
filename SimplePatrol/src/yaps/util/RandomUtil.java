package yaps.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomUtil {
	
	//TODO: usar outro gerador, porque o de Java � ruim!
	private static long SEED = 1613; 
	private static Random rand = new Random(SEED);

	public static void resetSeed() {
		rand = new Random(SEED);
	}

	/**
	 * Returns a random integer, uniformly distributed.
	 */
	public static int chooseInteger() {
		return rand.nextInt();
	}
	
	/**
	 * Randomly chooses an integer from the inclusive range {from, from+1, ..., to}.  
	 */
	public static int chooseInteger(int from, int to) {
		return from + rand.nextInt(to-from+1);
	}

	/**
	 * Randomly selects a value inside the List.
	 * 
	 * TODO: unify with randomChoose, rename
	 */
	public static <T> T chooseAtRandom(List<T> list) {
		int rand = chooseInteger(0, list.size() - 1);
		return list.get(rand);
	}
	
	/**
	 * Randomly chooses a certain number of distinct elements from a given list
	 * 
	 * @param numOfItens
	 *            The number of elements in the returned list
	 * @param list
	 *            A list from which to choose elements at random
	 * @return A list of randomly chosen elements from {@code list} with size
	 *         {@code numOfItens}
	 */
	public static <T> List<T> randomChoose(int numOfItens, List<T> list) {
		int listSize = list.size();
		if (numOfItens > listSize || numOfItens < 0) {
			throw new IllegalArgumentException("Incompatible number of elements");
		}
		if (numOfItens == listSize) {
			return new ArrayList<T>(list);
		}
		List<T> chosen = new ArrayList<T>();
		while (chosen.size() < numOfItens) {
			T ob = list.get(RandomUtil.chooseInteger(0, listSize - 1));
			if (!chosen.contains(ob)) {
				chosen.add(ob);
			}
		}
		return chosen;
	}
	
	public static <T> List<T> randomChooseWithRepetition(int numOfItens, List<T> list) {
		int listSize = list.size();
		if (numOfItens > listSize || numOfItens < 0) {
			throw new IllegalArgumentException("Incompatible number of elements");
		}
		if (numOfItens == listSize) {
			return new ArrayList<T>(list);
		}
		List<T> chosen = new ArrayList<T>();
		while (chosen.size() < numOfItens) {
			T ob = list.get(RandomUtil.chooseInteger(0, listSize - 1));
			chosen.add(ob);
		}
		return chosen;
	}
	
	/**
	 * Randomly chooses an index of the array, with probabilities
	 * proportional to the weights assigned for each index. 
	 */
	public static int chooseProportionally(double[] weights) {
		double sum = 0.0d;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
		}
		
		double choice = sum * rand.nextDouble(); //choice is in interval [0;sum)
		
		double partialSum = 0.0d;
		for (int i = 0; i < weights.length; i++) {
			partialSum += weights[i];
			if (choice <= partialSum) {
				return i;
			}
		}
		
		return weights.length - 1; //nunca deveria acontecer!
	}

	/**
	 * Generates a random uniform double in interval [0, 1).
	 */
	public static double chooseDouble() {
		return rand.nextDouble();
	}

	/**
	 * Generates a random uniform double in interval [min, max).
	 */
	public static double chooseDouble(double min, double max) {
		return min + rand.nextDouble()*(max-min);
	}
	
	public static boolean chooseBoolean() {
		return rand.nextBoolean();
	}
	
	public static boolean chooseBoolean(double trueProbability) {
		return rand.nextDouble() <= trueProbability;
	}

	/**
	 * Randomly shuffles (mixes) a list, returning a new one.
	 * Fisher-Yattes algorithm. 
	 */
	public static <T> List<T> shuffle(List<T> l){
		List<T> list = new ArrayList<T>(l);
		int j;
		T temp;
		for (int i = list.size()-1; i >= 1; i --) {
			j = rand.nextInt(i+1); //[0, i]
			temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
		}
		return list;
	}
	
	/**
	 * Randomly shuffles (mixes) a list, changing it directly.
	 * Fisher-Yattes algorithm. 
	 */
	public static <T> void shuffleInline(List<T> list) {
		int j;
		T temp;
		for (int i = list.size()-1; i >= 1; i --) {
			j = rand.nextInt(i+1); //[0, i]
			temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
		}
	}
	
	public static int[] generateArrayOfInts(int arraySize, int rangeMin, int rangeMax) {
		int[] result = new int[arraySize];
		for (int i = 0; i < arraySize; i++) {
			result[i] = RandomUtil.chooseInteger(rangeMin, rangeMax);
		}
		return result;
	}
	
}
