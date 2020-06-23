package yaps.map_library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import yaps.files.YapsLexer;
import yaps.files.YapsParsingException;


/**
 * This class parses the map information (coded in "YAPS Map Format") from a InputStream,
 * and returns a Map object. 
 * 
 * @author Pablo A. Sampaio
 */
public class MapParser {
	private String mapName;
	
	private List<String> nodeAttributes = new LinkedList<>();
	private NodeInfo[] nodes;

	private List<String> edgeAttributes = new LinkedList<>();
	private EdgeInfo[] edges;

	private boolean has2Dcoordinates;
	private boolean hasNodeImportances;
	private boolean hasLengths;
	private boolean isDirected; //obs.: a mixed graph is considered directed

	YapsLexer lexer;

	public MapParser() {		
	}
	
	public Map loadFromFile(String fileName) throws YapsParsingException {
		try {
			return loadFromFile(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			File f = new File(fileName);
			throw new YapsParsingException("File not found: " + f.getAbsolutePath());
		}
	}
	
	public Map loadFromFile(InputStream input) throws YapsParsingException {
		lexer = new YapsLexer(input);
		
		parse();
		
		Map map = new Map(this.mapName);
		
		map.setGraph(this.nodes, this.edges);
		map.setProperties(this.hasLengths, this.isDirected, this.has2Dcoordinates, this.hasNodeImportances);
		
		return map;
	}
	
	private void parse() throws YapsParsingException {
		lexer.readString("map");	
		this.mapName = lexer.readString();

		lexer.advanceLines();
		
		parseNodes();
		parseEdges();

		//obs.: it doesn't verify for EOF
	}

	private void parseNodes() throws YapsParsingException {
		lexer.readString("nodes");
		lexer.readSymbol(':');
		
		int numNodes = lexer.readInteger();
		
		if (numNodes <= 0) {
			throw new YapsParsingException("Invalid number of nodes: " + numNodes);
		}
		this.nodes = new NodeInfo[numNodes];
		
		lexer.advanceLines();
		lexer.readString("nodes-attributes");
		lexer.readSymbol(':');
		
		String attr;
		
		do {
			attr = lexer.readString();
			nodeAttributes.add(attr);
			
			if (attr.equals("importance")) {
				this.hasNodeImportances = true;	
			} else if (attr.equals("2d-position")) {
				this.has2Dcoordinates = true;	
			}
			
		} while (lexer.checkSymbol(','));
		
		lexer.advanceLines();

		int x, y;
		String attrValue;
		
		for (int id = 0; id < numNodes; id ++) {
			lexer.readInteger(id);			
			lexer.readSymbol(':');
			
			this.nodes[id] = new NodeInfo(id);			
		
			for (int a = 0; a < this.nodeAttributes.size(); a ++) {
				attrValue = this.nodeAttributes.get(a);
				
				if (attrValue.equals("label")) {
					this.nodes[id].setLabel(lexer.readString()); 
				
				} else if (attrValue.equals("importance")) {
					this.nodes[id].setImportance(lexer.readDecimal()); 
				
				} else if (attrValue.equals("2d-position")) {
					lexer.readSymbol('(');
					x = lexer.readInteger();
					lexer.readSymbol(',');
					y = lexer.readInteger();
					lexer.readSymbol(')');
					this.nodes[id].set2Dposition(x, y);
					
				} else {
					throw new YapsParsingException("Unsupported node attribute : " + attrValue);

				}
			}
			
			lexer.advanceLines();
		}

	}

	private void parseEdges() throws YapsParsingException {
		lexer.readString("edges");
		lexer.readSymbol(':');
		
		int numEdges = lexer.readInteger();
		
		if (numEdges <= 0) {
			throw new YapsParsingException("Invalid number of edges: " + numEdges);
		}
		this.edges = new EdgeInfo[numEdges];
		
		lexer.advanceLines();
		lexer.readString("edges-attributes");
		lexer.readSymbol(':');
		
		String attr;
		
		do {
			attr = lexer.readString();
			edgeAttributes.add(attr);
			
			if (attr.equals("length")) {
				this.hasLengths = true;	
			}
			
		} while (lexer.checkSymbol(','));
		
		lexer.advanceLines();
		
		int nodeFrom, nodeTo;
		boolean directed;
		String attrValue;

		this.isDirected = false;
		
		for (int id = 0; id < numEdges; id ++) {
			lexer.readInteger(id);
			
			nodeFrom = lexer.readInteger();
			directed = (lexer.readEdgeType() == '>');
			nodeTo = lexer.readInteger();
			
			if (directed) {
				this.isDirected = true;
			}
			
			lexer.readSymbol(':');
			
			this.edges[id] = new EdgeInfo(id, directed, nodeFrom, nodeTo);
			
			int length;			
			for (int a = 0; a < this.edgeAttributes.size(); a ++) {
				attrValue = this.edgeAttributes.get(a);
				
				if (attrValue.equals("label")) {
					this.edges[id].setLabel(lexer.readString()); 
				
				} else if (attrValue.equals("length")) {
					length = lexer.readInteger();
					if (length > 0) {
						this.edges[id].setLength(length);
					} else {
						throw new YapsParsingException("Invalid edge length: " + length);
					}
				
				} else {
					throw new YapsParsingException("Unsupported edge attribute : " + attrValue);

				}
			}
			
			if (! this.hasLengths) {
				this.edges[id].setLength(1);
			}

			lexer.advanceLines();
		}
		
	}
	
	public static void main(String[] args) throws YapsParsingException {
		MapParser parser = new MapParser();
		
		parser.loadFromFile("map-example.ymf");
		
		System.out.println(parser.mapName);
		System.out.println(parser.nodeAttributes);
		for (int i = 0; i < parser.nodes.length; i++) {
			System.out.println(parser.nodes[i]);
		}
		for (int i = 0; i < parser.edges.length; i++) {
			System.out.println(parser.edges[i]);
		}
		
	}

}
