package algorithms.balloon_dfs;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import yaps.graph.Graph;


/**
 * @author Rodrigo de Sousa
 */
public class BalloonDFSViewer {
	
	public BalloonDFSViewer(Graph g){
		Canvas canvas = new Canvas(gridGraphToImg(g)); 
		
		setUpWindow(canvas);
	}
	
	public BufferedImage gridGraphToImg(Graph g){
		int height = 0;
		int width = 0;
		int cellSize = 10;
		int border = 1;
		int imgHeight = 0;
		int imgWidth = 0;
		int[] rgbArray = {Color.white.getRGB()};
		
		for(int i = 1; i < g.getNumNodes(); i++){
			if(g.getOutEdges(i).size() < 3){
				width = i + 1;
				height = g.getNumNodes() / width;
				break;
			}
		}
		
		imgHeight = height*cellSize+height*border+border;
		imgWidth = width*cellSize+width*border+border;
		
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		//img.setRGB(0, 0, imgWidth, imgHeight, rgbArray, 0, 0);
		
		for(int i = 0; i < width; i++){
			img.setRGB(i*(cellSize+border+1), 0, border, imgHeight, rgbArray, 0, 0);
		}
		
		return img;
	}
	
	public void setUpWindow(Canvas canvas){
		
		

		
		Frame frame = new Frame("Teste");
		frame.setSize(600, 600);
		frame.add(canvas);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		try {
			Thread.sleep(500);
			//canvas.change();
			System.out.println("ok");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private class Canvas extends JComponent{
		
		private BufferedImage image;
		
		public Canvas(BufferedImage img){
			this.image = img;
		}
		
		public Canvas(){
			image = new BufferedImage(100, 100,
					BufferedImage.TYPE_INT_RGB);
			for(int i = 0; i < 50; i++){
				image.setRGB(i, i, Color.GREEN.getRGB());
			}
		}
		
		public void change(){
			for(int i = 0; i < 50; i++){
				image.setRGB(i, i, Color.RED.getRGB());
			}
			this.repaint();
		}

		@Override
		protected void paintComponent(Graphics arg0) {
			// TODO Auto-generated method stub
			
			arg0.drawImage(image, 0, 0, null);
			
		}
		
	}

}
