package utils.iterator.csv;

import akka.util.ByteString;
import io.ebean.QueryIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.iterator.CloseableIterator;

/**
 * CsvIterator.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 20.08.24
 */
public abstract class CsvIterator<T, U> implements CloseableIterator<ByteString> {

    /**
     * The max number of row.
     * Used to counteract the hard limit of Ebean of 1 million result.
     */
    protected static final int MAX_ROW = 1000000000;

    /**
     * The Logger.
     */
    protected final Logger logger;

    /**
     * The Ebean iterator on the orders to process.
     */
    private QueryIterator<T> iterator;

    /**
     * Has a header to display.
     */
    private boolean displayHeader;

    /**
     * Is the iterator closed.
     */
    private boolean iteratorClosed;

    /**
     * The Cache.
     */
    private U cache;

    /**
     * The internal buffer.
     */
    private T buffer;

    /**
     * Build a new instance.
     *
     * @param hasHeader Has header?
     */
    protected CsvIterator(final boolean hasHeader) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.iterator = null;
        this.displayHeader = hasHeader;
        this.iteratorClosed = true;
        this.buffer = null;
        this.cache = null;
    }

    /**
     * Build a new instance.
     *
     * @param hasHeader Has header?
     * @param iterator  Current iterator
     */
    protected CsvIterator(final boolean hasHeader, final QueryIterator<T> iterator) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.iterator = iterator;
        this.displayHeader = hasHeader;
        this.iteratorClosed = false;
        this.buffer = null;
        this.cache = null;
    }

    /**
     * Close the previous iterator and set a new one.
     *
     * @param iterator The iterator
     */
    protected void setIterator(final QueryIterator<T> iterator) {
        this.close();
        this.iterator = iterator;
        this.iteratorClosed = false;
    }

    @Override
    public void close() {
        if (!this.iteratorClosed) {
            this.iteratorClosed = true;
            this.iterator.close();
        }
    }

    @Override
    public boolean hasNext() {
        try {
            if (this.displayHeader || this.buffer != null) {
                return true;
            }
            if (this.iteratorClosed) {
                return false;
            }
            while (this.iterator.hasNext()) {
                this.buffer = this.iterator.next();
                this.cache = null;
                if (this.accept(this.buffer)) {
                    return true;
                }
            }
            this.buffer = null;
            return false;
        } catch (final Exception e) {
            this.logger.error("An error occurred on the hasNext function.", e);
            return false;
        }
    }

    @Override
    public ByteString next() {
        try {
            if (this.displayHeader) {
                this.displayHeader = false;
                return ByteString.fromString(this.renderHeader());
            }
            if (this.hasNext()) {
                final StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < 1500 && this.hasNext(); i++) {
                    stringBuilder.append(this.toCsvLine(this.buffer));
                    this.buffer = null;
                }
                return ByteString.fromString(stringBuilder.toString());
            }
            this.logger.error("Next has been called with hasNext()=False.");
            return ByteString.empty();
        } catch (final Exception e) {
            this.logger.error("An error occurred on the next function.", e);
            return ByteString.empty();
        }
    }

    /**
     * Gets cache.
     *
     * @return the cache
     */
    public U getCache() {
        return this.cache;
    }

    /**
     * Sets cache.
     *
     * @param cache the cache
     */
    public void setCache(final U cache) {
        this.cache = cache;
    }

    /**
     * Accept boolean.
     *
     * @param object the object
     * @return the boolean
     */
    protected abstract boolean accept(final T object);

    /**
     * Render header string.
     *
     * @return the string
     */
    protected abstract String renderHeader();

    /**
     * Analyse string.
     *
     * @param object the object
     * @return the string
     */
    protected abstract String toCsvLine(T object);
}
