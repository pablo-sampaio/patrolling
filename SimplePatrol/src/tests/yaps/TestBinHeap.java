package tests.yaps;

import yaps.util.priority_queue.BinHeapPQueue;
import yaps.util.priority_queue.PQueueElement;

public class TestBinHeap {
	public static void main(String[] args) {
		TestElement[] testData = new TestElement[] {
				new TestElement(5),
				new TestElement(4),
				new TestElement(2),
				new TestElement(3),
				new TestElement(5),
				new TestElement(0),
		};
		BinHeapPQueue<TestElement> heap = new BinHeapPQueue<TestElement>(testData);
		
		System.out.println(heap);
		System.out.println("Remove-Max: " + heap.removeMaximum());
		System.out.println(heap);
		System.out.println("Remove-Min: " + heap.removeMinimum());
		System.out.println(heap);
		System.out.println("Remove-Max: " + heap.removeMaximum());
		System.out.println(heap);
	}
}

class TestElement extends PQueueElement {
	private int key;
	TestElement(Integer k) { key = k; }
	@Override
	public double getKey() {
		return key;
	}
	public String toString() { return "" + key; }
}
