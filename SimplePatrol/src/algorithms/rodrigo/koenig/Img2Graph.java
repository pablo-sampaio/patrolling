package algorithms.rodrigo.koenig;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import yaps.graph.Graph;


/**
 * Criado para converter para grafo o ambiente usado nos experimentos do artigo de 2001 de Koenig & Liu.
 * Lê uma imagem bmp, e converte cada quadrado 20x20 pixels em um nó do grafo.
 * 
 * @author Rodrigo de Sousa
 */
public class Img2Graph {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
	
		//BufferedImage image = ImageIO.read(new File("./maps/office.bmp"));
		//recreateImg(createCellArray(image, 20, 20));
		
		Graph graph = img2Graph("./maps/office.bmp", 20, 20);
		System.out.println(graph.toString());
	}

	private static BufferedImage loadImg(String path) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));

		return image;
	}
	
	public static Graph img2Graph(String path, int cellWidth, int cellHeight) throws IOException{
		BufferedImage image = loadImg(path);
		Cell[][] cellArray = createCellArray(image, cellWidth, cellHeight);
		
		return toGraph(cellArray);
	}

	private static Cell[][] createCellArray(BufferedImage image, int cellWidth,
			int cellHeight) throws IOException {
		
		int vCells = image.getHeight() / cellHeight;
		int hCells = image.getWidth() / cellWidth;

		Cell[][] cellArray = new Cell[vCells][hCells];

		int nodeIndex = -1;
		for (int i = 0; i < image.getHeight(); i += cellHeight) {
			for (int j = 0; j < image.getWidth(); j += cellWidth) {

				Cell cell = new Cell(image.getRGB(j, i));
				if (image.getRGB(j, i) == Color.WHITE.getRGB()) {
					nodeIndex++;
					cell.setIndex(nodeIndex);
				}

				cellArray[i / cellHeight][j / cellWidth] = cell;

			}
		}

		return cellArray;
	}

	private static Graph toGraph(Cell[][] cellArray) {
		int nodesNumber = 0;
		for (int i = 0; i < cellArray.length; i++) {
			for (int j = 0; j < cellArray[0].length; j++) {
				if (cellArray[i][j].getColor() == Color.WHITE.getRGB()) {
					nodesNumber++;
				}
			}
		}

		Graph graph = new Graph(nodesNumber);

		for (int i = 0; i < cellArray.length; i++) {
			for (int j = 0; j < cellArray[0].length; j++) {
				if (cellArray[i][j].getColor() == Color.WHITE.getRGB()) {

					if (i != 0) {
						if (cellArray[i - 1][j].getColor() == Color.WHITE
								.getRGB()) {
							graph.addUndirectedEdge(
									cellArray[i][j].getIndex(),
									cellArray[i - 1][j].getIndex(), 1);
						}
					}

					if (j != 0) {
						if (cellArray[i][j - 1].getColor() == Color.WHITE
								.getRGB()) {
							graph.addUndirectedEdge(
									cellArray[i][j].getIndex(),
									cellArray[i][j - 1].getIndex(), 1);
						}
					}
				}
			}
		}
		
		return graph;
	}

	private static void paint(int x, int y, BufferedImage image, int rgb) {
		for (int i = x; i < x + 20; i++) {
			for (int j = y; j < y + 20; j++) {
				image.setRGB(i, j, rgb);
			}
		}
	}

	private static void recreateImg(Cell[][] array) throws IOException {
		BufferedImage image = new BufferedImage(800, 600,
				BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {

				image.setRGB(i, j, Color.WHITE.getRGB());

			}
		}

		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (array[i][j].getColor() == Color.GRAY.getRGB()) {
					paint(j * 20, i * 20, image, Color.GRAY.getRGB());
				} else if (array[i][j].getColor() == Color.BLACK.getRGB()) {
					paint(j * 20, i * 20, image, Color.BLACK.getRGB());
				} else if (array[i][j].getColor() == Color.WHITE.getRGB()) {
					paint(j * 20, i * 20, image, Color.GREEN.getRGB());
				}
			}
		}

		File outputfile = new File("recreatedImage.bmp");
		ImageIO.write(image, "bmp", outputfile);

	}

	public static class Cell {
		private int index;
		private int color;

		public Cell(int index, int color) {
			this.index = index;
			this.color = color;
		}

		public Cell(int color) {
			this.color = color;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}

	}
}
