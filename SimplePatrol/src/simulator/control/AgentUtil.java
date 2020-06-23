package simulator.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.Timer;
import org.graphstream.ui.spriteManager.Sprite;

/**
 * This class represents a drawn agent in the graph.
 * 
 * @author Alison Carrera
 */
public class AgentUtil {

	private Sprite sprite;
	private Timer timer;
	private String nextNode;
	private List<String> graphEdgesGlobal;

	// Auxiliares globais do timer.

	private boolean firstExecution = true;
	private boolean backAgent = false;
	private boolean controlFlag1 = false;
	private boolean controlFlag2 = false;
	private int velocity = 40;

	/**
	 * 
	 * Constructor of an agent in the view context.
	 * 
	 * @param sprite The sprite who represents an agent.
	 * @param edges The edges of the graph.
	 * @param velocity Velocity of an agent.
	 */
	public AgentUtil(Sprite sprite, List<String> edges, int velocity) {
		this.sprite = sprite;
		this.graphEdgesGlobal = edges;
		this.velocity = velocity;
		createTimer();

	}	

	/**
	 * This method says if a current agent is moving or not.
	 * @return A current agent is moving or not.
	 */
	public boolean isAgentMoving() {
		return this.timer.isRunning();
	}

	/**
	 * This method set the next node for an agent.
	 * @param nextNode Next node for an agent.
	 */
	public void updateNextNode(String nextNode) {

		if (this.nextNode == null) {
			this.nextNode = nextNode;
		} else {
			char origin = this.nextNode.toCharArray()[1];
			char destiny = nextNode.toCharArray()[0];

			if (origin == destiny) {
				destiny = nextNode.toCharArray()[1];
			}

			this.nextNode = String.valueOf(origin) + String.valueOf(destiny);
		}

		this.timer.start();

	}

	// Do the movement animation of an agent.
	/**
	 * This method does the movement animation of an agent.
	 */
	private void createTimer() {
		this.timer = new Timer(velocity, new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				if (controlFlag1) {
					if (graphEdgesGlobal.contains(nextNode)) {
						sprite.attachToEdge(nextNode);
						sprite.setPosition(0, sprite.getY(), sprite.getZ());
						backAgent = false;
					} else {
						String reverse2 = new StringBuffer(nextNode).reverse()
								.toString();
						sprite.attachToEdge(reverse2);
						sprite.setPosition(1, sprite.getY(), sprite.getZ());
					}

					controlFlag1 = false;
				}

				if (controlFlag2) {
					if (graphEdgesGlobal.contains(nextNode)) {

						sprite.attachToEdge(nextNode);
						sprite.setPosition(0, sprite.getY(), sprite.getZ());

					} else {
						String reverse = new StringBuffer(nextNode).reverse()
								.toString();

						if (graphEdgesGlobal.contains(reverse)) {

							sprite.attachToEdge(reverse);
							sprite.setPosition(1, sprite.getY(), sprite.getZ());
							backAgent = true;

						}
					}

					controlFlag2 = false;
				}

				if (firstExecution) {

					if (graphEdgesGlobal.contains(nextNode)) {
						sprite.attachToEdge(nextNode);
						sprite.setPosition(0);
					} else {
						String reverse = new StringBuffer(nextNode).reverse()
								.toString();

						if (graphEdgesGlobal.contains(reverse)) {
							sprite.attachToEdge(reverse);
							sprite.setPosition(1, sprite.getY(), sprite.getZ());
							backAgent = true;
						}
					}

					firstExecution = false;
				}

				if (backAgent) {

					String reverse = new StringBuffer(nextNode).reverse()
							.toString();

					if (graphEdgesGlobal.contains(reverse)) {

						if (sprite.getX() <= 1) {

							sprite.setPosition(sprite.getX() - 0.015,
									sprite.getY(), sprite.getZ());

						}

						if (sprite.getX() <= 0) {
							controlFlag1 = true;

							((Timer) e.getSource()).stop();

						}
					}
				}

				if (graphEdgesGlobal.contains(nextNode)) {

					if (sprite.getX() <= 1) {

						sprite.setPosition(sprite.getX() + 0.015,
								sprite.getY(), sprite.getZ());

					}

					if (sprite.getX() >= 1) {
						controlFlag2 = true;

						((Timer) e.getSource()).stop();

					}
				}

			}

		});
	}

}
