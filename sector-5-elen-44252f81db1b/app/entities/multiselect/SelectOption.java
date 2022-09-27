package entities.multiselect;

import java.util.HashMap;
import java.util.Map;

/**
 * SelectOption.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
public class SelectOption {

    /**
     * The Key.
     */
    public final String key;

    /**
     * The Value.
     */
    public final String name;

    /**
     * The Extra data.
     */
    public final Map<String, String> extraData;

    /**
     * Instantiates a new Select option.
     *
     * @param key  the key
     * @param name the name
     */
    public SelectOption(final String key, final String name) {
        this.key = key;
        this.name = name;
        this.extraData = new HashMap<>();

        for (final Map.Entry<String, String> stringStringEntry : this.extraData.entrySet()) {

        }
    }
}
