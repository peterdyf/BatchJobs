package net.peter.tools;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

final class DisableLog {
	private DisableLog() {
	}

	static void disable() {
		
		Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		root.setLevel(Level.OFF);

		Logger log4jdbc = (Logger) LoggerFactory.getLogger("jdbc.sqltiming");
		log4jdbc.setLevel(Level.OFF);
	}
}
