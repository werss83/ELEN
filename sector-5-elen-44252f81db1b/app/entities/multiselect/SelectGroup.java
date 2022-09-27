package entities.multiselect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SelectGroup.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
public class SelectGroup {

    /**
     * The Group Name.
     */
    public final String name;

    /**
     * The Options.
     */
    public final List<SelectOption> options;

    /**
     * The Extra data.
     */
    public final Map<String, String> extraData;

    /**
     * Instantiates a new Select group.
     *
     * @param name the name
     */
    public SelectGroup(final String name) {
        this.name = name;
        this.options = new ArrayList<>();
        this.extraData = new HashMap<>();
    }
}
