package tests.yaps;

import java.util.LinkedList;

import yaps.util.lazylists.LazyList;


public class TestLazyList {
	
	public static void main(String[] args) {
		simpleTest();
		//testRange();
	}
	
	public static void simpleTest() {
		LinkedList<Character> theList = new LinkedList<>();
		
		theList.add('a'); theList.add('b');
		theList.add('c'); theList.add('d');
		theList.add('e'); theList.add('f');

		System.out.println("LISTA ORIGINAL: " + theList + ", tamanho " + theList.size());
		System.out.println();
		System.out.println("Operações sucessivas, a partir da lista original:");

		LazyList<Character> view1 = LazyList.toLazyList(theList);  //lista completa
		System.out.println("=> Converte para LazyList: " + view1);
		
		view1 = view1.rotate(2);
		System.out.println("=> Rotaciona no índice 2: " + view1);

		view1 = view1.reverse();
		System.out.println("=> Inverte: " + view1);

		view1 = view1.repeat(3);
		System.out.println("=> Repete 3 vezes: " + view1);

		LazyList<Character> view2, view3;
		view2 = view1.sublist(5, 5);
		view3 = view1.sublist(1, 3);
		System.out.println("=> Forma duas sublistas: \n\t(1) do índice 5 ao 5: " + view2 + "\n\t(2) do índice 1 ao 3: " + view3);

		view1 = view2.concatenate(view3);
		System.out.println("=> Concatena as sublistas: " + view1);

		view1 = view1.repeatUntilSize(10);
		System.out.println("=> Repete ciclicamente até completar 10 itens: " + view1);
		
		System.out.println("=> Converte para lista padrão: " + view1.toPlainList());
	}

	public static void testRange() {
		LazyList<Character> list = LazyList.repeatElement('P', 10);
		
		System.out.println(list);
		
		System.out.println(LazyList.createRangeList(2, 10));
		System.out.println(LazyList.createRangeList(2, 11));
		System.out.println(LazyList.createRangeList(2, 12).toPlainList());
		System.out.println(LazyList.createRangeList(2, 13).toPlainList());
		
		System.out.println(LazyList.createRangeList(2, 10, 3));
		System.out.println(LazyList.createRangeList(2, 11, 3));
		System.out.println(LazyList.createRangeList(2, 12, 3));
		System.out.println(LazyList.createRangeList(2, 13, 3).toPlainList());
		System.out.println(LazyList.createRangeList(2, 14, 3).toPlainList());

		System.out.println(LazyList.createRangeList(10, 2, -3));
		System.out.println(LazyList.createRangeList(11, 2, -3));
		System.out.println(LazyList.createRangeList(12, 2, -3));
		System.out.println(LazyList.createRangeList(13, 2, -3).toPlainList());
		System.out.println(LazyList.createRangeList(14, 2, -3).toPlainList());
		
		System.out.println(LazyList.createRangeList(2, 2, 4).toPlainList());
		
		System.out.println(LazyList.createRangeList(10, 0, 2));
		System.out.println(LazyList.createRangeList(0, 10, -2).toPlainList());
	}

}
