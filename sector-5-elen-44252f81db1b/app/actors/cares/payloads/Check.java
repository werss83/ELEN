package actors.cares.payloads;

import models.care.BookedCareModel;

import java.util.Arrays;
import java.util.List;

/**
 * Check.
 *
 * @author Pierre Adam
 * @since 20.07.25
 */
public class Check {

    /**
     * The To check.
     */
    private final List<BookedCareModel> toCheck;

    /**
     * Instantiates a new Check.
     *
     * @param toCheck the to check
     */
    public Check(final BookedCareModel... toCheck) {
        this.toCheck = Arrays.asList(toCheck);
    }

    /**
     * Instantiates a new Check.
     *
     * @param toCheck the to check
     */
    public Check(final List<BookedCareModel> toCheck) {
        this.toCheck = toCheck;
    }

    /**
     * Gets to check.
     *
     * @return the to check
     */
    public List<BookedCareModel> getToCheck() {
        return this.toCheck;
    }
}
