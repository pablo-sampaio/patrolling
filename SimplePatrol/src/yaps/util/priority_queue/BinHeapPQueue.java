package yaps.util.priority_queue;


/**
 * Priority queue implemented as a binary heap.
 * 
 * @author Pablo A. Sampaio
 *
 * @param <T> type of the element
 */
public class BinHeapPQueue<T extends PQueueElement> implements PQueue<T> {
	private PQueueElement heap[];
	private int usedSize;

	private boolean discardExtra;
	
	public BinHeapPQueue(int maxSize, boolean discardWhenFull) {
		this.heap = new PQueueElement[maxSize];
		this.usedSize = 0;
		this.discardExtra = discardWhenFull;
	}
	
	public BinHeapPQueue(int maxSize) {
		this(maxSize, false);
	}
	
	public BinHeapPQueue(T data[], int maxSize, boolean discardWhenFull) {
		if (maxSize < data.length) {
			throw new IllegalArgumentException("Argument maxSize cannot be lower than data.length!");
		}
		this.heap = new PQueueElement[maxSize];
		this.discardExtra = discardWhenFull;
		this.usedSize = data.length;
		
		int halfSizeFloor = (usedSize / 2) - 1;
		for (int i = data.length - 1; i > halfSizeFloor; i--) {
			this.heap[i] = data[i];
			this.heap[i].setIndex(i);
		}
		for (int i = halfSizeFloor; i >= 0; i--) {
			this.heap[i] = data[i];
			this.heap[i].setIndex(i);
			this.siftDown(i, usedSize-1); 
		}
	}
	
	public BinHeapPQueue(T data[], int maxSize) {
		this(data, maxSize, false);
	}
	
	public BinHeapPQueue(T data[]) {
		this(data, data.length, false);
	}

	@Override
	public boolean isEmpty() {
		return usedSize == 0;
	}
	
	@Override
	public void add(T element) {
		if (usedSize < heap.length) {
			
			heap[usedSize] = element;
			element.setIndex(usedSize);
		
			siftUp(usedSize);
		
			usedSize ++;
		
		} else if (discardExtra) {
			
			if (element.getKey() < heap[usedSize-1].getKey()) {
				heap[usedSize-1] = element;
				element.setIndex(usedSize-1);
				
				siftUp(usedSize-1);
			}

		} else {
			throw new UnsupportedOperationException("Heap is full! Size: " + usedSize + ".");
				
		}

	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T getMinimum() {
		if (usedSize == 0) {
			throw new UnsupportedOperationException("There is no minimum - heap is empty!");
		}
		return (T)heap[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T removeMinimum() {
		if (usedSize == 0) {
			throw new UnsupportedOperationException("Cannot remove - heap is empty!");
		}
		
		PQueueElement minimum = heap[0];
		usedSize --;
		
		if (usedSize > 0) {
			heap[0] = heap[usedSize];
			heap[0].setIndex(0);
			heap[usedSize] = null; 

			siftDown(0, usedSize-1);
		}
		//check();
		
		return (T)minimum;
	}
	
	public T removeMaximum() {
		if (usedSize == 0)  return null;

		int i = this.usedSize - 1;
		PQueueElement max = this.heap[i];
		int maxPos = i;
		i --;
		
		//find...
		while (i > 0) {
			if (this.heap[i].getKey() > max.getKey()) {
				max = this.heap[i];
				maxPos = i;
			}
			i --;
		}
		
		//...then, remove
		usedSize --;

		if (maxPos != usedSize) {
			heap[maxPos] = heap[usedSize]; //last goes to the position of max
			heap[maxPos].setIndex(maxPos);
		}
		heap[usedSize] = null;
		
		siftDown(i, usedSize-1);
		
		return (T)max;
	}
	
	// precisa implementar o m�todo equals() da subclasse de PQueueElement
	@Override
	public void remove(T element) {
		if (usedSize == 0) {
			throw new UnsupportedOperationException("Cannot remove - heap is empty!");
		}
		
		// pode ser mais eficiente?
		for (int i = 0; i < usedSize; i++) {
			if (heap[i].equals(element)) {
				usedSize --;
				
				heap[i] = heap[usedSize];
				heap[i].setIndex(i);
				heap[usedSize] = null;
				
				siftDown(i, usedSize-1);
				return;
			}
		}
		
		throw new UnsupportedOperationException("Element not found!");
	}
	
	@Override
	public int size() {
		return usedSize;
	}
	
	public int capacity() {
		return heap.length;
	}

	@Override
	public void decreaseKey(T element) {
		int position = element.getIndex();
		
		if (heap[position] != element) {
			throw new UnsupportedOperationException("Invalid element position! Values: " + element + " and " + heap[position] );
		}
		
		//aten��o: n�o verifica se a chave (realmente) diminuiu
		
		siftUp(position);
	}
	
	private void siftUp(int position) {
		if (position != 0) {
			int father = (position-1) / 2;
			
			if (heap[position].getKey() < heap[father].getKey()) {
				swap(position, father);
				siftUp(father);
			}
		}
	}

	void siftDown(int start, int end) {
	    int pai = start;
	    int filho = (2*pai) + 1; 
	    
	    boolean finished = false;

	    while (filho <= end && !finished) {

	    	// encontra o menor dos filhos
	        if ( ((filho+1) <= end) && (heap[filho+1].getKey() < heap[filho].getKey()) ) {
	            filho ++;
	        }

	        // se o menor dos filhos for menor que o pai, troca os dois 
	        // e continua a partir do filho (seguindo para baixo)
	        if (heap[filho].getKey() < heap[pai].getKey()) {
	            swap(pai, filho);
	            pai = filho;
	            filho = (2*pai) + 1;
	        } else {
	        	finished = true;
	        }

	    }

	}
	
	private void swap(int i, int j) {
		PQueueElement temp; 
		
		temp = heap[i];
		heap[i] = heap[j];
		heap[j] = temp;
		
		heap[i].setIndex(i);
		heap[j].setIndex(j);
	}
	
	public String toString() {
		String str = "";
		for (int i = 0; i < usedSize; i++) {
			str += "v" + heap[i] + "/" + heap[i].getKey() + " ";
		}
		return str + "\n";
	}

}
