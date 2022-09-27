package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service.
 *
 * @author Pierre Adam
 * @since 20.06.21
 */
public abstract class Service {

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * Constructor.
     */
    protected Service() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }
}
