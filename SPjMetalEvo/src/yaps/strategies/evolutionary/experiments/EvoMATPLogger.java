package yaps.strategies.evolutionary.experiments;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class EvoMATPLogger {
	
	public static String now() {
		DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss");
		Date date = new Date();
		return dateformat.format(date);
	}
	
	public static final String LOGGER_FILE = "logs/EvoSimplePatrol_" + now() + ".log";
	
	private static EvoMATPLogger evoLogger;
	
	private Logger logger;
	private Handler fileHandler;
	private Handler consoleHandler;
	
	private EvoMATPLogger() {
		this.logger = Logger.getLogger("Evolutionary Simple Patrol");
		try {
			this.fileHandler = new FileHandler(LOGGER_FILE);
			this.fileHandler.setFormatter(new SimpleFormatter());
			this.consoleHandler = new ConsoleHandler();
			this.logger.addHandler(this.consoleHandler);
			this.logger.addHandler(this.fileHandler);
			this.fileHandler.setLevel(Level.ALL);
			this.consoleHandler.setLevel(Level.SEVERE);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		this.logger.setLevel(Level.ALL);
		
	}
	
	public static Logger get() {
		if (evoLogger == null) {
			evoLogger = new EvoMATPLogger();
		}
		return evoLogger.logger;
		
	}

}
