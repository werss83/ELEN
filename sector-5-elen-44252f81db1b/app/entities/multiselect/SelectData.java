package entities.multiselect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * SelectData.
 *
 * @author Pierre Adam
 * @since 20.07.04
 */
public class SelectData {

    /**
     * The Extra data.
     */
    public final Map<String, String> extraData;

    /**
     * The Options.
     */
    public List<SelectOption> options;

    /**
     * The Groups.
     */
    public List<SelectGroup> groups;

    /**
     * Instantiates a new Select data.
     */
    public SelectData() {
        this.options = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.extraData = new HashMap<>();
    }

    /**
     * From enum select data.
     *
     * @param <E>         the type parameter
     * @param eClass      the e class
     * @param translation the translation
     * @return the select data
     */
    public static <E extends Enum<E>> SelectData fromEnum(final Class<E> eClass, final Function<E, String> translation) {
        try {
            final Method valuesMethod = eClass.getDeclaredMethod("values");
            @SuppressWarnings("unchecked") final E[] values = (E[]) valuesMethod.invoke(null);
            return fromEnum(translation, values);
        } catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
            return null;
        }
    }

    /**
     * From enum select data.
     *
     * @param <E>         the type parameter
     * @param translation the translation
     * @return the select data
     */
    @SafeVarargs
    public static <E extends Enum<E>> SelectData fromEnum(final Function<E, String> translation, final E... values) {
        final SelectData selectData = new SelectData();
        for (final E value : values) {
            selectData.options.add(new SelectOption(value.name(), translation.apply(value)));
        }
        return selectData;
    }
}
