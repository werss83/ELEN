package utils.iterator;

import akka.stream.Attributes;
import akka.stream.Outlet;
import akka.stream.Shape;
import akka.stream.SourceShape;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

/**
 * CloseableIteratorSource.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 20.08.24
 */
public class CloseableIteratorSource<T> extends GraphStage<SourceShape<T>> {

    /**
     * Define the (sole) output port of this stage.
     */
    public final Outlet<T> out = Outlet.create("IteratorSource.out");

    /**
     * The Iterator.
     */
    private final CloseableIterator<T> iterator;

    /**
     * Define the shape of this stage, which is SourceShape
     * with the port we defined above.
     */
    private final SourceShape<T> shape = SourceShape.of(this.out);

    /**
     * Instantiates a new Closable iterator source.
     *
     * @param iterator the iterator
     */
    public CloseableIteratorSource(final CloseableIterator<T> iterator) {
        this.iterator = iterator;
    }

    /**
     * Get the iterator.
     *
     * @return The current iterator
     */
    public CloseableIterator<T> getIterator() {
        return this.iterator;
    }

    @Override
    public SourceShape<T> shape() {
        return this.shape;
    }

    @Override
    public GraphStageLogic createLogic(final Attributes inheritedAttributes) {
        return new CloseableGraphStageLogic<>(this.shape, this.out, this.iterator);
    }

    /**
     * The type Closeable graph stage logic.
     *
     * @param <T> the type parameter
     */
    public static class CloseableGraphStageLogic<T> extends GraphStageLogic {

        /**
         * The Iterator.
         */
        private final CloseableIterator<T> iterator;

        /**
         * Instantiates a new Closeable graph stage logic.
         *
         * @param shape    the shape
         * @param out      the out
         * @param iterator the iterator
         */
        public CloseableGraphStageLogic(final Shape shape, final Outlet<T> out, final CloseableIterator<T> iterator) {
            super(shape);
            this.iterator = iterator;

            this.setHandler(out, () -> {
                if (iterator.hasNext()) {
                    this.push(out, this.iterator.next());
                } else {
                    this.complete(out);
                }
            });
        }

        @Override
        public void postStop() throws Exception {
            this.iterator.close();
        }
    }
}
