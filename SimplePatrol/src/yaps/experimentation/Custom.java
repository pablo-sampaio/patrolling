package yaps.experimentation;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import yaps.util.DoubleList;

/**
 *
 * @author Rodrigo de Sousa
 *
 */
public class Custom extends XYLineAndShapeRenderer {
	
	private double baseShapeScale = 1;

	public Custom() {
	}

	public Custom(boolean lines, boolean shapes) {
		super(lines, shapes);
	}

	
	
	/*@Override
	public Shape getLegendShape(int series) {
		Shape shapeS  = super.getLegendShape(series);
		AffineTransform at = new AffineTransform();
		at.scale(baseShapeScale, baseShapeScale);
		shapeS = at.createTransformedShape(shapeS);
		return shapeS;
	}

	@Override
	public Shape getItemShape(int row, int column) {
		Shape shapeS  = super.getItemShape(row, column);
		AffineTransform at = new AffineTransform();
		at.scale(baseShapeScale, baseShapeScale);
		shapeS = at.createTransformedShape(shapeS);
		return shapeS;
	}*/

	public double getBaseShapeScale() {
		return baseShapeScale;
	}

	public void setBaseShapeScale(double baseShapeScale) {
		this.baseShapeScale = baseShapeScale;
	}
	
	
	
	
	
	
	
	
	
	

}
