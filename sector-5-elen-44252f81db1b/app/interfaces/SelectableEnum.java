package interfaces;

import entities.multiselect.SelectData;
import entities.multiselect.SelectOption;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Selectable.
 *
 * @author Pierre Adam
 * @since 20.07.17
 */
public interface SelectableEnum {

    /**
     * Forge a SelectData object that can be given to a MultipleSelect.
     *
     * @param <T>           the type parameter
     * @param values        the values
     * @param enumToLabel   the enum to label
     * @param excludeValues the exclude values
     * @return the select data
     */
    @SafeVarargs
    static <T extends Enum<T>> SelectData selectData(final T[] values, final Function<T, String> enumToLabel, final T... excludeValues) {
        return selectData(values, enumToLabel, Arrays.asList(excludeValues));
    }

    /**
     * Forge a SelectData object that can be given to a MultipleSelect.
     *
     * @param <T>         the type parameter
     * @param values      the values
     * @param enumToLabel the enum to label
     * @param exclusions  the exclusions
     * @return the select data
     */
    static <T extends Enum<T>> SelectData selectData(final T[] values, final Function<T, String> enumToLabel, final List<T> exclusions) {
        final SelectData selectData = new SelectData();

        for (final T data : values) {
            if (exclusions.contains(data)) {
                continue;
            }
            selectData.options.add(new SelectOption(data.name(), enumToLabel.apply(data)));
        }

        return selectData;
    }
}
