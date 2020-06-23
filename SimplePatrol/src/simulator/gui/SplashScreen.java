package simulator.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import scala.xml.include.sax.Main;

/**
 * 
 * This class represents the Splash Screen frame.
 * 
 * @author Alison Carrera
 *
 */

public class SplashScreen extends JFrame {

	private static final long serialVersionUID = -4131603661766221538L;
	private JPanel splashScreenPanel;
	private int timeSplashScreen = 3000;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// System.setProperty("org.graphstream.ui.renderer",
		// "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SplashScreen frame = new SplashScreen();
					frame.executeTime();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the SplashScreen scene.
	 */
	public SplashScreen() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setBounds(100, 100, 450, 261);

		URL url = Main.class.getResource("/splash_screen_450x261.png");

		splashScreenPanel = new JPanel() {

			private static final long serialVersionUID = -7070993351642777171L;

			@Override
			protected void paintComponent(Graphics g) {

				Image img = new ImageIcon(url).getImage();

				super.paintComponent(g);
				g.drawImage(img, 0, 0, null);
			}
		};
		splashScreenPanel.setBorder(new LineBorder(UIManager
				.getColor("Button.disabledForeground"), 1, true));
		splashScreenPanel.setLayout(new BorderLayout(0, 0));
		setContentPane(splashScreenPanel);

		setLocationRelativeTo(null);
		setVisible(true);

	}

	/**
	 * This method wait a time and open gui interface after Splash Screen logo apparition.
	 */
	private void executeTime() {

		Timer timer = new Timer(timeSplashScreen, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				HomeScreen.executeScreen();
			}
		});

		timer.setRepeats(false);
		timer.start();
	}

}
