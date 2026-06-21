package rewrite.villagerhelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VHLogger {
    private static final String PREFIX = "[VH]";
    private final Logger logger;

    public VHLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    public void info(String msg, Object... args) {
        logger.info(PREFIX + " " + msg, args);
    }

    public void debug(String msg, Object... args) {
        logger.debug(PREFIX + " " + msg, args);
    }

    public void warn(String msg, Object... args) {
        logger.warn(PREFIX + " " + msg, args);
    }

    public void error(String msg, Object... args) {
        logger.error(PREFIX + " " + msg, args);
    }
}
