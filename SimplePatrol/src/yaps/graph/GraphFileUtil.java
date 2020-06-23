package yaps.graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import yaps.graph.algorithms.AllShortestPaths;


/**
 * Methods to read graph from different file formats.
 * 
 * @author Pablo A. Sampaio
 */
public class GraphFileUtil {
	
	public static Graph read(String fileName, GraphFileFormat format) throws IOException {
		switch (format) {
		case ADJACENCY_LIST:
			return readAdjacencyList(fileName);
		case EDGE_LIST:
			return readEdgeList(fileName);
		case GRAPHML:
			return readGraphml(fileName);
		case SIMPATROL:
			return readSimpatrolFormat(fileName);
		case TSP_LIB:
			return readTspLib(fileName);
		default:
			System.out.println("Invalid format");
			return null;
		}
	}
	
	public static void write(Graph g, String fileName, GraphFileFormat format) throws IOException {
		switch (format) {
		case ADJACENCY_LIST:
			writeAsAdjacencyList(g, fileName);
			break;
		case EDGE_LIST:
			writeAsEdgeList(g, fileName);
			break;
		case GRAPHML:
			writeAsGraphml(g, fileName, true);
			break;
		case SIMPATROL:
			writeAsSimpatrolFormat(g, fileName, true);
			break;
		case TSP_LIB:
			writeAsTspLib(g, fileName, TSPLIB_INFINITE);
			break;
		default:
			System.out.println("Invalid format - no file saved.");
		}
	}
	
	public static void convert(String originFile, GraphFileFormat originFormat, String destinyFile, GraphFileFormat destinyFormat) throws IOException {
		Graph graph = read(originFile, originFormat);
		write(graph, destinyFile, destinyFormat);
	}

	public static Graph readAdjacencyList(String file) throws IOException {
		return readAdjacencyList(file, GraphDataRepr.MIXED, false);
	}
	
	public static Graph readAdjacencyList(String file, GraphDataRepr rep) throws IOException {
		return readAdjacencyList(file, rep, false);
	}

	public static Graph readAdjacencyList(String file, GraphDataRepr rep, boolean forceUndirected) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(file));

		StringTokenizer tokenizer;
		String line;

		int numVertices;
		
		line = inputFile.readLine();
		numVertices = Integer.parseInt(line.trim());
		
		int startIndex = file.lastIndexOf('\\') + 1;
		if (startIndex <= 0) {
			startIndex = file.lastIndexOf('/') + 1;
			if (startIndex <= 0) {
				startIndex = 0;
			}
		}
		
		//String graphName = file.substring(startIndex);
		Graph graph = new Graph(numVertices, rep);
		
		String sucessorStr, pesoStr;
		int sucessor, peso;
		
		for (int v = 0; v < numVertices; v ++) {
			line = inputFile.readLine();
			tokenizer = new StringTokenizer(line);
			
			while (tokenizer.hasMoreTokens()) {
				sucessorStr = tokenizer.nextToken();
				sucessor = Integer.parseInt(sucessorStr.trim());
				
				pesoStr = tokenizer.nextToken();
				peso = Integer.parseInt(pesoStr);
				
				if (sucessor != -1) {
					if (!forceUndirected) {
						//caso normal: adiciona direcionada
						graph.addDirectedEdge(v, sucessor, peso);
					} else {
						//adiciona somente se não existe, e adiciona não-direcionada
						if (graph.getEdgeLength(sucessor, v) != peso) {
							graph.addUndirectedEdge(v, sucessor, peso);
						}
					}
				}
			}
		}		
		inputFile.close();
		
		return graph;
	}

	public static Graph readEdgeList(String file) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(file));
		
		String line;
		StringTokenizer tokenizer;
		
		line = inputFile.readLine();
		tokenizer = new StringTokenizer(line);
		
		int numVertices = Integer.parseInt(tokenizer.nextToken()); //number of edges is ignored
		String graphName = file.substring(file.lastIndexOf('\\') + 1, file.lastIndexOf('.'));
		Graph graph = new Graph(numVertices, GraphDataRepr.MIXED);
	
		int v, succ, weight;
		line = inputFile.readLine();

		while (line != null && !line.trim().equals("")) {
			tokenizer = new StringTokenizer(line);
			
			v = Integer.parseInt(tokenizer.nextToken().trim());
			succ = Integer.parseInt(tokenizer.nextToken().trim());
			weight = Integer.parseInt(tokenizer.nextToken().trim());
			
			graph.addDirectedEdge(v, succ, weight);

			line = inputFile.readLine();
		}
		inputFile.close();
		
		return graph;
	}
	
	// only for graphml saved with the GraphWriter
	public static Graph readGraphml(String fileName) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
		String line;

		line = inputFile.readLine();
				
		String[] parts = line.split("\"");
		String graphName = parts[1];
		boolean directed = parts[3].equals("directed");
		int numNodes = 0;
		
		line = inputFile.readLine(); //linha do "key" ignorada

		line = inputFile.readLine().trim();		
		while (!line.startsWith("<edge")) {
			numNodes ++;
			line = inputFile.readLine().trim();
		}

		//System.out.printf("Graph named %s, %sdirected, with %s nodes\n", graphName, directed?"":"UN", numNodes);
		Graph graph = new Graph(numNodes, GraphDataRepr.MIXED);
		
		while (line.startsWith("<edge")) {
			parts = line.split("\"");
			
			int source = Integer.parseInt( parts[3].substring(1) );
			int target = Integer.parseInt( parts[5].substring(1) );
			
			line = inputFile.readLine();
			
			int startWeightIndex = line.indexOf('>') + 1; 
			line = line.substring(startWeightIndex, line.indexOf('<', startWeightIndex));
			
			int capacity = Integer.parseInt(line);
			
			//System.out.printf("Aresta: (%s,%s) %s\n", source, target, capacity);
			
			if (directed) {
				graph.addDirectedEdge(source, target, capacity);
			} else {
				graph.addUndirectedEdge(source, target, capacity);
			}
			
			line = inputFile.readLine().trim();
		}
		
		inputFile.close();
		return graph;
	}

	public static final int TSPLIB_INFINITE = 9999999;
	
	public static Graph readTspLib(String fileName) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
		String line;

		line = inputFile.readLine();
		String graphName = line.substring(line.indexOf("NAME:")+5).trim();
		
		line = inputFile.readLine(); //field "TYPE" ignored
		line = inputFile.readLine(); //field "COMMENT" ignored
		
		line = inputFile.readLine();		
		line = line.substring(line.indexOf("DIMENSION:")+10).trim();
		int numNodes = Integer.parseInt(line);

		System.out.printf("Graph named %s, with %s nodes\n", graphName, numNodes);
		Graph graph = new Graph(numNodes, GraphDataRepr.MIXED);
		
		line = inputFile.readLine();
		String weightType = line.substring(line.indexOf("EDGE_WEIGHT_TYPE:")+17).trim();
		line = inputFile.readLine();
		String weightFormat = line.substring(line.indexOf("EDGE_WEIGHT_FORMAT:")+19).trim();
		
		if (!weightType.equals("EXPLICIT") || !weightFormat.equals("FULL_MATRIX")) {
			System.out.println("Weight type/format not supported!");
			inputFile.close();
			return null;
		}
		
		line = inputFile.readLine(); //ignore tag "EDGE_WEIGHT_SECTION"
		
		int source = 0;
		int target = 0;
		long cost;
		String[] parts;
		
		line = inputFile.readLine();
		while (line != null && !line.equals("EOF")) {
			parts = line.split("[ \\t]+");
			for (int i = 1; i < parts.length; i++) { //skips index 0 (always an empty string)
				cost = Long.parseLong(parts[i]);
				if (cost < TSPLIB_INFINITE) {
					System.out.printf("Aresta: (%s,%s) %s\n", source, target, cost);
					graph.addDirectedEdge(source, target, (int)cost);
				}
				target++;
				if (target == numNodes) {
					source ++;
					target = 0;
				}			
			}			
			line = inputFile.readLine();
		}
		
		inputFile.close();
		return graph;
	}
	
	private static int PRECISION_OF_LENGTH = 1; //no decimal places are considered (only the integer part)
	
	public static Graph readSimpatrolFormat(String fileName) throws IOException {
		return readSimpatrolFormat(fileName, GraphDataRepr.MIXED);
	}
	
	public static Graph readSimpatrolFormat(String fileName, GraphDataRepr repr) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
		String line;

		/* reads nodes' information */
		
		HashMap<String,Integer> nodeIndex = new HashMap<>();
		String node;
		int numNodes = 0;
		
		do {
			line = inputFile.readLine().trim();
		} while (!line.startsWith("<node"));

		do {
			node = getAttrValue(line, "id");
			nodeIndex.put(node, numNodes);
			//System.out.printf("Node name: %s, index: %s.\n", node, numNodes);

			numNodes++;			
			line = inputFile.readLine().trim();		
		} while (line.startsWith("<node"));
		
		/* creates the graph object */
		Graph graph = new Graph(numNodes, repr);
		
		/* reads edges' information */		
		String edge;
		int source, target, length;
		boolean directed;
		
		while (!line.startsWith("<edge")) {
			line = inputFile.readLine().trim();
		}
		
		do {
			edge = getAttrValue(line, "id");
			source = nodeIndex.get(getAttrValue(line, "source"));
			target = nodeIndex.get(getAttrValue(line, "target"));
			length = (int)(PRECISION_OF_LENGTH * Double.parseDouble(getAttrValue(line, "length")));
			directed = Boolean.parseBoolean(getAttrValue(line, "directed"));

			//System.out.printf("Edge %s: (%d,%d) len=%d directed: %s\n", edge, source, target, length, directed);
			if (directed) {
				graph.addDirectedEdge(source, target, length);
			} else {
				graph.addUndirectedEdge(target, source, length);	
			}
			
			line = inputFile.readLine().trim();
			
		} while (line.startsWith("<edge"));
			
		inputFile.close();
		return graph;
	}

	private static String getAttrValue(String xmlCode, String field) {
		int attrIndex = xmlCode.indexOf(field); 
		int valueStartIndex = xmlCode.indexOf('\"', attrIndex+1) + 1;
		int valueEndIndex = xmlCode.indexOf('\"', valueStartIndex);
		return xmlCode.substring(valueStartIndex, valueEndIndex);
	}

	public static void writeAsAdjacencyList(Graph graph, String fileName) throws FileNotFoundException {
		PrintStream outputFile = new PrintStream(fileName);
		int numVertices = graph.getNumNodes();
		
		outputFile.println(numVertices);
		
		List<Edge> edges;
		
		for (int v = 0; v < numVertices; v ++) {
			edges = graph.getOutEdges(v);	
			
			for (Edge e : edges) {
				outputFile.printf("%d %d ", e.getTarget(), e.getLength());
			}
			
			outputFile.println("-1 -1");
		}
		
		outputFile.close();
	}

	public static void writeAsEdgeList(Graph graph, String fileName) throws FileNotFoundException {
		PrintStream outputFile = new PrintStream(fileName);
	
		int numEdges = graph.getNumEdges();
		int numVertices = graph.getNumNodes();
		
		outputFile.printf("%d\t%d\r\n", numVertices, numEdges);
		
		List<Edge> edges;
		
		for (int v = 0; v < numVertices; v ++) {
			edges = graph.getOutEdges(v);	
			
			for (Edge e : edges) {
				outputFile.printf("%d\t%d\t%d\r\n", e.getSource(), e.getTarget(), e.getLength());
			}
		}
		
		outputFile.close();
	}

	public static void writeAsTspLib(Graph graph, String fileName, int infiniteValue) throws FileNotFoundException {
		PrintStream output = new PrintStream(fileName);
		int numVertices = graph.getNumNodes();
		
		AllShortestPaths shortestPaths = new AllShortestPaths(graph);
		
		shortestPaths.compute();
		
		output.println("NAME : " + fileName);
		output.println("COMMENT : Created by Pablo A. Sampaio");
		output.println("TYPE : TSP");
		output.println("DIMENSION : " + numVertices);
		output.println("EDGE_WEIGHT_TYPE : EXPLICIT");
		output.println("EDGE_WEIGHT_FORMAT : FULL_MATRIX");

		// Comcorde seems to ignore these parameters
//		output.println("EDGE_DATA_FORMAT : ADJ_LIST");
//		output.println("EDGE_DATA_SECTION");
//		for (int v = 0; v < numVertices; v++) {
//			output.print(v);
//			for (int u : graph.getSuccessors(v)) {
//				output.print(" " + u);
//			}
//			output.println(" -1");
//		}
//		output.println("-1");
		
		output.println("EDGE_WEIGHT_SECTION");
		
		// imprime a matrix completa
		for (int v = 0; v < numVertices; v++) {
			
			for (int u = 0; u < numVertices; u++) {
				if (u != 0) {
					output.print(' ');
				}
				
				if (graph.getEdgeLength(v, u) == 0) {
					output.print(infiniteValue);
				} else {
					output.print(graph.getEdgeLength(v, u));
				}
			}
			
			output.println();
		}
		
		output.println("EOF");
		output.close();
	}

	private static final String SIMPATROL_NODE_LINE = "\t<node id=\"%s\" label=\"%s\" visibility=\"true\" idleness=\"0.0\" fuel=\"false\" " 
			+ "is_enabled=\"true\"/>\n";
	private static final String SIMPATROL_EDGE_LINE = "\t<edge id=\"%s\" source=\"%s\" target=\"%s\" directed=\"%s\" length=\"%s\" "
			+ "visibility=\"true\" is_enabled=\"true\" is_in_dynamic_emitter_memory=\"false\" is_in_dynamic_collector_memory=\"false\"/>\n";

	public static void writeAsSimpatrolFormat(Graph graph, String fileName, boolean directed) throws FileNotFoundException {
		PrintStream output = new PrintStream(fileName);
		
		int numVertices = graph.getNumNodes();
		List<Edge> edges;
		String name;
		
		output.println("<graph label=\"" + DEFAULT_GRAPH_NAME + "\">");
		for (int v = 0; v < numVertices; v ++) {
			name = "v"+v; 
			output.printf(SIMPATROL_NODE_LINE, name, name);
		}
		
		for (int v = 0; v < numVertices; v ++) {
			edges = graph.getOutEdges(v);	
			
			for (Edge e : edges) {
				if (directed) {
					name = "e-" + v + "-" + e.getTarget();
					output.printf(Locale.US, SIMPATROL_EDGE_LINE, name, "v"+v, "v"+e.getTarget(), directed, e.getLength());
				} else if (v <= e.getTarget()) {
					name = "e-" + v + "-" + e.getTarget();
					output.printf(Locale.US, SIMPATROL_EDGE_LINE, name, "v"+v, "v"+e.getTarget(), directed, e.getLength());
				}
			}
		}		
		
		output.println("</graph>");
		output.close();
	}


	private static final String DEFAULT_GRAPH_NAME = "YAPS-unnamed-graph";
	
	public static void writeAsGraphml(Graph graph, String fileName, boolean directed) throws FileNotFoundException {
		PrintStream output = new PrintStream(fileName);
		
		int numVertices = graph.getNumNodes();
		List<Edge> edges;
		String name;
		
		output.printf("<graph id=\"%s\" edgedefault=\"%sdirected\">\n", DEFAULT_GRAPH_NAME, directed?"":"un");
		output.println("<key id=\"w\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>");
		
		for (int v = 0; v < numVertices; v ++) {
			name = "v"+v; 
			output.printf("<node id=\"%s\"/>\n", name);
		}
		
		for (int v = 0; v < numVertices; v ++) {
			edges = graph.getOutEdges(v);	
			
			for (Edge e : edges) {
				if (directed) {
					name = "e-" + v + "-" + e.getTarget();
					output.printf("\t<edge id=\"%s\" source=\"%s\" target=\"%s\">\n", name, "v"+v, "v"+e.getTarget());
					output.printf("\t\t<data key=\"w\">%s</data></edge>\n", e.getLength());
				} else if (v <= e.getTarget()) {
					name = "e-" + v + "-" + e.getTarget();
					output.printf("\t<edge id=\"%s\" source=\"%s\" target=\"%s\">\n", name, "v"+v, "v"+e.getTarget());
					output.printf("\t\t<data key=\"w\">%s</data></edge>\n", e.getLength());
				}
			}
		}		
		
		output.println("</graph>");
		output.close();
	}
	
}
