package simulator.control;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;
import org.graphstream.ui.view.util.MouseManager;

import yaps.graph.Edge;

/**
 * Converts a YAPS Graph to a GraphStream Graph for visualization. This allow
 * and construct a visual simulator for Timed Patrolling Multiagent (TMAP) <br/>
 * <b> All the graph view configuration can be done here.</b>
 * 
 * @author Alison Carrera
 */

public class TMAPGraphDrawUtil {

	private Graph graphStream;
	private yaps.graph.Graph graph;
	private TMAPAgentDrawUtil agent;
	private JLabel jlabel;
	private int velocity = 40;

	/**
	 * 
	 * Constructor of the graph view.
	 * 
	 * @param graph Graph that will be drawn.
	 * @param velocity Agents movement velocity.
	 */
	public TMAPGraphDrawUtil(yaps.graph.Graph graph, int velocity) {
		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		this.graph = graph;
		this.velocity = velocity;
		this.createGraphStream(graph);
		jlabel = new JLabel();
	}

	/**
	 * Create and paint each agent in the view.
	 * 
	 * @param agentsInitialPath
	 *            An array representing the agents initial position.
	 */
	public void prepareAgents(int[] agentsInitialPath) {
		agent = new TMAPAgentDrawUtil(graphStream, agentsInitialPath, velocity);
	}

	public TMAPAgentDrawUtil getAgentUtil() {
		return agent;
	}

	 
	/**
	 * Default Style of the graph.
	 */
	private void defaultNodeStyle() {
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "fill-color: gray;");
			node.addAttribute("ui.style", "size: 25px;");
			node.addAttribute("ui.style", "text-size: 12;");
			node.addAttribute("ui.style", "text-color: black;");
			node.addAttribute("ui.label", node.getId());
		}
	}

	/**
	 * Show the edge lenght of all edges on the graph.
	 */
	public void showEdgeLenght() {
		for (org.graphstream.graph.Edge edge : graphStream.getEachEdge()) {

			edge.addAttribute("ui.style", "text-size: 13;");
			edge.addAttribute("ui.style", "text-offset: 5;");
			edge.addAttribute("ui.style", "text-style: bold;");
			int source = Integer.valueOf(edge.getSourceNode().getId());
			int target = Integer.valueOf(edge.getTargetNode().getId());
			int lenght = graph.getEdgeLength(source, target);
			edge.addAttribute("ui.label", String.valueOf(lenght));

		}
	}

	
	/**
	 * Converts YAPS Graph API to a Graph Stream for visualization.
	 * @param graph Graph that will be converted.
	 */
	private void createGraphStream(yaps.graph.Graph graph) {

		graphStream = new MultiGraph("");

		for (int i = 0; i < graph.getNumNodes(); i++) {
			graphStream.addNode(String.valueOf(i));

		}

		List<Edge> edge = graph.getEdges();

		for (int j = 0; j < edge.size(); j++) {

			try {
				graphStream.addEdge(String.valueOf(edge.get(j).getSource())
						+ String.valueOf(edge.get(j).getTarget()), String
						.valueOf(edge.get(j).getSource()), String.valueOf(edge
						.get(j).getTarget()), edge.get(j).isDirected());
			} catch (EdgeRejectedException | IdAlreadyInUseException s) {
				// Handle the convertion error, of multiple same edges.
				// We can make a better solution in here.
			}

		}

	}

	/**
	 * Return the control where the graph was painted. <i> This view auto adjust
	 * the graph's layout and the nodes can be handled by a mouse in the
	 * view.</i>
	 * 
	 * @return A ViewPanel that can be integrated with a JPanel.
	 */
	public ViewPanel getViewPanel(boolean enableNodeMove, boolean autoLayout) {

		if(autoLayout)
		{
			Toolkit.computeLayout(graphStream, 0.99);
		}	
		
		Viewer viewer = new Viewer(graphStream,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.setCloseFramePolicy(CloseFramePolicy.HIDE_ONLY);
		
	
		ViewPanel view = viewer.addDefaultView(false);		
		this.defaultNodeStyle();

		if (!enableNodeMove) {

			viewer.getDefaultView().setMouseManager(new MouseManager() {
				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseDragged(MouseEvent e) {
				}

				@Override
				public void mouseMoved(MouseEvent e) {
				}

				@Override
				public void init(GraphicGraph graph, View view) {
				}

				@Override
				public void release() {
				}
			});
		}		
		
	    jlabel.setFont(new Font("Verdana",1,15));	    
	    jlabel.setHorizontalAlignment(JLabel.CENTER);
	    view.add(jlabel);

		return view;
	}
	
	public void setTextTurn(String text)
	{
		this.jlabel.setText(text);
	}

	/* Style configuration - Only the basic */

	/* Node Style */

	/**
	 * Change all node's color of the graph. <i>Example: "gray", "blue"...</i>
	 * 
	 * @param color
	 *            The color that will be painted in node.
	 */
	public void setAllNodesColor(String color) {
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "fill-color: " + color + ";");
		}
	}

	/**
	 * Change all node's color of the graph in RGB format. <i>Example:
	 * 255,244,255</i>
	 */
	public void setAllNodesColorRGB(int r, int g, int b) {
		StringBuilder build = new StringBuilder();
		build.append("rgb(").append(r).append(",").append(g).append(",")
				.append(b).append(");");
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "fill-color: " + build.toString());
		}
	}

	/**
	 * Change size of all nodes of the graph.
	 * 
	 * @param nodeSize
	 *            Node size in pixels
	 */
	public void setAllNodeSize(int nodeSize) {
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "size: " + nodeSize + "px ;");
		}
	}

	/**
	 * Set the text size of all nodes in graph.
	 * 
	 * @param textSize
	 *            Node text size.
	 */
	public void setAllNodeTextSize(int textSize) {
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "text-size: " + textSize + ";");
		}
	}

	public void setNodePosition(String idNode, String x, String y) {
		graphStream.getNode(idNode).setAttribute("x", x);
		graphStream.getNode(idNode).setAttribute("y", y);
	}

	/**
	 * Set text of one specified node.
	 * 
	 * @param idNode
	 *            Node ID.
	 * @param text
	 *            Text that will be printed inside the node.
	 */
	public void setNodeText(String idNode, String text) {

		graphStream.getNode(idNode).addAttribute("ui.label", text);
	}

	/**
	 * Change text color of all nodes of the graph.
	 * 
	 * @param color
	 *            Node text color.
	 */
	public void setAllNodeTextColor(String color) {
		for (Node node : graphStream.getEachNode()) {
			node.addAttribute("ui.style", "text-color: " + color + " ;");
		}
	}

	/**
	 * Change text color of a node of the graph.
	 * 
	 * @param color
	 *            Node text color.
	 */
	public void setNodeTextColor(String idNode, String color) {

		graphStream.getNode(idNode).addAttribute("ui.style",
				"text-color: " + color + " ;");

	}

	/**
	 * Change node color of the graph. <i>Example: "gray", "blue"...</i>
	 * 
	 * @param color
	 *            The color that will be painted in node.
	 */
	public void setNodesColor(String idNode, String color) {

		graphStream.getNode(idNode).addAttribute("ui.style",
				"fill-color: " + color + ";");

	}

	/**
	 * Change node color of the graph in RGB format. <i>Example: 255,244,255</i>
	 */
	public void setNodesColorRGB(String idNode, int r, int g, int b) {
		StringBuilder build = new StringBuilder();
		build.append("rgb(").append(r).append(",").append(g).append(",")
				.append(b).append(");");

		graphStream.getNode(idNode).addAttribute("ui.style",
				"fill-color: " + build.toString());

	}

	/**
	 * Change size of nodes of the graph.
	 * 
	 * @param nodeSize
	 *            Node size in pixels
	 */
	public void setNodeSize(String idNode, int nodeSize) {

		graphStream.getNode(idNode).addAttribute("ui.style",
				"size: " + nodeSize + "px ;");

	}

	/**
	 * Set the text size of nodes in graph.
	 * 
	 * @param textSize
	 *            Node text size.
	 */
	public void setNodeTextSize(String idNode, int textSize) {

		graphStream.getNode(idNode).addAttribute("ui.style",
				"text-size: " + textSize + ";");

	}

}
