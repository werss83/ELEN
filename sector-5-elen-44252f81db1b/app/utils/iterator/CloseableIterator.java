package utils.iterator;

import java.util.Iterator;

/**
 * CloseableIterator.
 *
 * @author Pierre Adam
 * @since 20.08.24
 */
public interface CloseableIterator<T> extends Iterator<T>, java.io.Closeable {
}
