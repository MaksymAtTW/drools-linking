import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.INFO

def LOG_PATTERN = "%d %level [%logger{2}][%thread] - %m%n"
def mainLoggers = []


appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		Pattern = LOG_PATTERN
	}
}
mainLoggers << "CONSOLE"


root(INFO, mainLoggers)
