package interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * CafPercentage.
 *
 * @author Pierre Adam
 * @since 20.07.23
 */
public interface CafPercentage {

    @JsonIgnore
    default Long addPercentage(final Long base, final Double multiplier) {
        return (long) ((base * multiplier) - base);
    }

    @JsonIgnore
    default Long addPercentage(final Long base, final Double multiplier, final boolean enabled) {
        return enabled ? this.addPercentage(base, multiplier) : 0L;
    }
}
