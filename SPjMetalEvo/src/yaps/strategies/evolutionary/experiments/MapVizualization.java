package yaps.strategies.evolutionary.experiments;

import java.io.File;
import java.io.IOException;

import yaps.graph.Graph;
import yaps.graph.GraphFileUtil;
import yaps.strategies.evolutionary.utils.GraphUtils;

public class MapVizualization {

	public static void main(String[] args) throws IOException {
		
		String[] maps = { "map_cicles_corridor", "map_city_traffic",
				"map_grid", "map_islands", "map_random_directed_1",
				"map_random_directed_2" };

		String pathToMap = "maps/"+maps[3]+".xml";
		
		Graph graph = GraphFileUtil.readSimpatrolFormat(pathToMap);
		
		File file = new File(pathToMap.replace(".xml", ".dot"));
		
		GraphUtils.toDOTFile(graph, file);

	}

}
