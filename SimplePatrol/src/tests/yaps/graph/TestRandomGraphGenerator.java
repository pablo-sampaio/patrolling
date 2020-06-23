package tests.yaps.graph;

import yaps.graph.generators.TreeGenerator;

public class TestRandomGraphGenerator {
	
	public static void main(String[] args) {
		TreeGenerator t = new TreeGenerator(5);
		for (int i = 0; i < 3; i++) {
			System.out.println(t.generate());
		}
	}
	
}
