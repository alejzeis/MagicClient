package net.beaconpe.magicclient.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Logger used by the client.
 */
public class ClientLogger {
    private Logger logger;

    public ClientLogger(String logName){
        logger = LogManager.getLogger(logName);
    }

    public void debug(String msg){
        logger.debug(msg);
    }

    public void info(String msg){
        logger.info(msg);
    }

    public void warn(String msg){
        logger.warn(msg);
    }

    public void error(String msg){
        logger.error(msg);
    }

    public void fatal(String msg){
        logger.fatal(msg);
    }
}
