package simulator.gui;

import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import simulator.control.TMAPGraphDrawUtil;
import yaps.graph.Graph;
import yaps.simulator.core.Algorithm;
import yaps.simulator.core.Simulator;

/**
 * This class represents the main gui interface of TMAP Simulator.
 * 
 * @author Alison Carrera
 *
 */
public class HomeScreen extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3928044544075999935L;
	private JPanel contentPane;
	private static HomeScreen instance;
	private Thread s = null;

	public static void executeScreen() {
		if (instance == null) {
			instance = new HomeScreen();
		}

	}

	/**
	 * Create the main frame of the simulator.
	 * 
	 * @throws InterruptedException
	 */
	public HomeScreen() {

		//This area made all configuration of graphic elements positions like buttons, area of graph visualization.
		// --> This area cannot be changed.

		setTitle("TMAP SIMULATOR");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnArchive = new JMenu("Actions");
		menuBar.add(mnArchive);

		JMenuItem mntmStart = new JMenuItem("Start");
		mnArchive.add(mntmStart);

		JMenuItem mntmStop = new JMenuItem("Stop");
		mnArchive.add(mntmStop);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		Panel graphDrawArea = new Panel();
		contentPane.add(graphDrawArea);
		graphDrawArea.setLayout(new BoxLayout(graphDrawArea, BoxLayout.Y_AXIS));
		setLocationRelativeTo(null);
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setVisible(true);

		// <----- End of the area

		// O peso das arestas tem que ser 1, pois o simulador considera o peso 1
		// como uma ida simples pela aresta.
		Graph graph = new Graph(8);
		graph.addEdge(0, 1, 1, false);
		graph.addEdge(0, 2, 1, false);
		graph.addEdge(2, 3, 1, false);
		graph.addEdge(3, 1, 1, false);
		graph.addEdge(1, 4, 1, false);
		graph.addEdge(4, 6, 1, false);
		graph.addEdge(6, 7, 1, false);
		graph.addEdge(4, 5, 1, false);
		graph.addEdge(5, 7, 1, false);
		
		
		/*Graph graph = new Graph(10);
		graph.addEdge(0, 3, 1, false);
		graph.addEdge(0, 4, 1, false);
		graph.addEdge(4, 3, 1, false);
		graph.addEdge(4, 5, 1, false);
		graph.addEdge(5, 1, 1, false);
		graph.addEdge(5, 6, 1, false);
		graph.addEdge(3, 2, 1, false);
		graph.addEdge(2, 1, 1, false);
		graph.addEdge(1, 6, 1, false);
		graph.addEdge(6, 7, 1, false);
		graph.addEdge(7, 8, 1, false);
		graph.addEdge(8, 2, 1, false);
		graph.addEdge(8, 9, 1, false);
		graph.addEdge(7, 9, 1, false);
		graph.addEdge(9, 0, 1, false); //Teste 1
		
		/*Graph graph = new Graph(30);
		graph.addEdge(0, 1, 1, false);
		graph.addEdge(1, 2, 1, false);
		graph.addEdge(2, 3, 1, false);
		graph.addEdge(3, 9, 1, false);
		graph.addEdge(9, 15, 1, false);
		graph.addEdge(15, 16, 1, false);
		graph.addEdge(16, 14, 1, false);
		graph.addEdge(16, 8, 1, false);
		graph.addEdge(14, 13, 1, false);
		graph.addEdge(13, 12, 1, false);
		graph.addEdge(13, 18, 1, false);
		graph.addEdge(18, 17, 1, false);
		graph.addEdge(18, 19, 1, false);
		graph.addEdge(19, 12, 1, false);
		graph.addEdge(12, 8, 1, false);
		graph.addEdge(8, 4, 1, false);
		graph.addEdge(4, 7, 1, false);
		graph.addEdge(4,10, 1, false);
		graph.addEdge(10, 5, 1, false);
		graph.addEdge(7, 6, 1, false);
		graph.addEdge(6, 11, 1, false);
		graph.addEdge(11, 10, 1, false);
		graph.addEdge(1, 4, 1, false);
		graph.addEdge(9, 8, 1, false);*/
		
		//Teste 3
	/*	graph.addEdge(17, 20, 1, false);
		graph.addEdge(20, 22, 1, false);
		graph.addEdge(20, 21, 1, false);
		graph.addEdge(22, 27, 1, false);
		graph.addEdge(21, 23, 1, false);
		graph.addEdge(23, 24, 1, false);
		graph.addEdge(27, 24, 1, false);
		graph.addEdge(27,25, 1, false);
		graph.addEdge(24, 26, 1, false);
		graph.addEdge(25, 28, 1, false);
		graph.addEdge(24, 28, 1, false);
		graph.addEdge(24, 29, 1, false);
		graph.addEdge(29, 26, 1, false);
		graph.addEdge(22, 21, 1, false);*/

		/*
		 * Graph graph = new Graph(10); graph.addEdge(0, 3, 1, false);
		 * graph.addEdge(0, 4, 1, false); graph.addEdge(4, 3, 1, false);
		 * graph.addEdge(4, 5, 1, false); graph.addEdge(5, 1, 1, false);
		 * graph.addEdge(5, 6, 1, false); graph.addEdge(3, 2, 1, false);
		 * graph.addEdge(2, 1, 1, false); graph.addEdge(1, 6, 1, false);
		 * graph.addEdge(6, 7, 1, false); graph.addEdge(7, 8, 1, false);
		 * graph.addEdge(8, 2, 1, false); graph.addEdge(8, 9, 1, false);
		 * graph.addEdge(7, 9, 1, false); graph.addEdge(9, 0, 1, false);
		 */

		int[] agentsNodes = new int[] { 0,1 };

		//Object responsible to draw graph and agents on gui.
		TMAPGraphDrawUtil u = new TMAPGraphDrawUtil(graph, 40);
		/*
		 * u.setNodePosition("4", "-1", "0"); u.setNodePosition("5", "1", "0");
		 * u.setNodePosition("0", "0", "0"); u.setNodePosition("1", "0", "1");
		 * u.setNodePosition("2", "0", "-1");
		 */

		//This method link the graphic information generated by TMAPGraphDrawUtil to the graphic component in gui, for human view.
		contentPane.add(u.getViewPanel(false, true));

		// System.out.println(contentPane.getWidth());
		// System.out.println(contentPane.getHeight());

		Simulator simulator = new Simulator();
//		SearchBasedFullPathAlgorithm algorithm = new SearchBasedFullPathAlgorithm(
//				AlgorithmTypes.A_STAR);
		Algorithm algorithm = null; //new RandomAlgorithm(); //TODO: mudar aqui

		// Now we need to set GUI (TMAPGraphDrawUtil) to the simulator with we
		// want a gui.
		simulator.setGUI(u);
		simulator.setGraph(graph);
		simulator.setAgentsInitialNodes(agentsNodes);
		simulator.setTotalTime(22);
		simulator.setAlgorithm(algorithm);
		simulator.setTempoNoMili(20); // Tempo que o agente ficara em um n� antes de sair em milisegundos.
		

		//Start Simulation
		mntmStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Start Simulation - If GUI is activated, we need to do it in a
				// separated Thread to not freeze the GUI.
				s = new Thread(simulator);
				s.start();
				
			}
		});

		//Stop Simulation
		mntmStop.addActionListener(new ActionListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {

				s.stop();

			}
		});

	}

}
