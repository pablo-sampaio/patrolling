package algorithms.balloon_dfs;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * @author Rodrigo de Sousa
 */
public class LoggerUtil {
	static private FileHandler fileTxt;
	static private SimpleFormatter formatterTxt;

	static public void setup(String loggerName) throws IOException {
		// get the global logger to configure it
		Logger logger = Logger.getLogger(loggerName);

		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");

		Handler[] handlers = rootLogger.getHandlers();

		if (handlers[0] instanceof ConsoleHandler) {
			//rootLogger.removeHandler(handlers[0]);
		}

		logger.setLevel(Level.INFO);

		fileTxt = new FileHandler("Logging.txt");

		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);

	}

}
