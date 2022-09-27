package models.options;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import exceptions.BadRuntimeTypeException;
import io.ebean.ExpressionList;
import io.ebean.Finder;
import io.ebean.annotation.EnumValue;
import models.BaseModel;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * GenericOptionModel.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Lucas Stadelmann
 * @since 20.06.08
 */
@MappedSuperclass
public abstract class GenericOptionModel<T extends Enum<T>, U extends BaseModel<U>> extends BaseModel<U> {

    /**
     * The class of the enum used as option.
     */
    @Transient
    private final Class<T> enumClass;

    /**
     * The key of option
     */
    @Size(max = 32)
    @Column(name = "opt_key", nullable = false, unique = false)
    protected String key;

    /**
     * The value of the option.
     */
    @Column(name = "opt_value", nullable = true, unique = false, columnDefinition = "TEXT")
    private String value;

    /**
     * Constructor.
     *
     * @param enumClass The class
     * @param uClass    the u class
     * @param key       the key
     */
    public GenericOptionModel(final Class<T> enumClass,
                              final Class<U> uClass,
                              final T key) {
        super(uClass);
        this.enumClass = enumClass;
        if (key != null) {
            this.key = valueFromEnum(this.enumClass, key);
        }
    }

    /**
     * Apply a where close to an ebean query.
     *
     * @param <E>    The type of the key
     * @param <M>    The type of the result from the query
     * @param eClass The class of the key
     * @param finder the finder
     * @param key    The key to search
     * @return The query with the search close
     */
    public static <E extends Enum<E>, M extends GenericOptionModel<E, M>> ExpressionList<M> whereKey(final Class<E> eClass,
                                                                                                     final Finder<Long, M> finder,
                                                                                                     final E key) {
        return whereKey(eClass, finder.query().where(), key);
    }

    /**
     * Apply a where close to an ebean query.
     *
     * @param <E>    The type of the key
     * @param <M>    The type of the result from the query
     * @param eClass The class of the key
     * @param query  The original query
     * @param key    The key to search
     * @return The query with the search close
     */
    public static <E extends Enum<E>, M extends GenericOptionModel<E, M>> ExpressionList<M> whereKey(final Class<E> eClass,
                                                                                                     final ExpressionList<M> query,
                                                                                                     final E key) {
        return query.eq("key", valueFromEnum(eClass, key));
    }

    /**
     * Give the textual representation of an enum.
     * Is the opposite of the function enumFromValue.
     *
     * @param <T>    The type of the enum
     * @param tClass The class of the enum
     * @param key    The value in the enum
     * @return The textual representation
     */
    protected static <T extends Enum<T>> String valueFromEnum(final Class<T> tClass,
                                                              final T key) {
        String strKey = key.toString();
        try {
            final EnumValue annotation = tClass.getField(key.name()).getAnnotation(EnumValue.class);
            if (annotation != null) {
                strKey = annotation.value();
            }
        } catch (final NoSuchFieldException e) {
            return strKey;
        }
        return strKey;
    }

    /**
     * Give the enum from a textual representation of this enum.
     * Is the opposite of the function valueFromEnum.
     *
     * @param <T>    The type of the enum
     * @param tClass The class T
     * @param value  The textual representation
     * @return Tfhe enum or null
     */
    protected static <T extends Enum<T>> T enumFromValue(final Class<T> tClass,
                                                         final String value) {
        for (final Field field : tClass.getFields()) {
            final EnumValue annotation = field.getAnnotation(EnumValue.class);
            if (annotation != null && annotation.value().equals(value)) {
                return T.valueOf(tClass, field.getName());
            }
        }
        return T.valueOf(tClass, value);
    }

    /**
     * Get the raw value for the key.
     *
     * @return the raw value of the key
     */
    public String getRawKey() {
        return this.key;
    }

    /**
     * Set the raw value for the key.
     * Should not be used outside of this class or outside of the daughter classes.
     * Using this method without understanding the consequences can induce some critical side effect.
     *
     * @param rawKey The new key.
     */
    protected void setRawKey(final String rawKey) {
        this.key = rawKey;
    }

    /**
     * Get the raw data for the value.
     *
     * @return The raw value
     */
    public String getRawValue() {
        return this.value;
    }

    /**
     * Set the raw data for the value.
     * Should not be used outside of this class or outside of the daughter classes.
     * Using this method without understanding the consequences can induce some critical side effect.
     *
     * @param rawValue The new value
     */
    protected void setRawValue(final String rawValue) {
        this.value = rawValue;
    }

    /**
     * Get the key.
     *
     * @return The key option
     */
    public T getKeyEnum() {
        return enumFromValue(this.enumClass, this.key);
    }

    /**
     * Set the key.
     * Should not be used outside of this class or outside of the daughter classes.
     * Using this method without understanding the consequences can induce some critical side effect.
     *
     * @param keyEnum The key to set
     */
    protected void setKeyEnum(final T keyEnum) {
        this.key = valueFromEnum(this.enumClass, keyEnum);
    }

    /**
     * Get the value as string.
     *
     * @return The value
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public String getValueAsString() {
        return this.safeGet(safeValue -> safeValue, String.class);
    }

    /**
     * Set the value as string
     *
     * @param valueAsString The value
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public void setValueAsString(final String valueAsString) {
        this.safeSet(() -> valueAsString, String.class);
    }

    /**
     * Get the value as long.
     *
     * @return The value as long
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public Boolean getValueAsBoolean() {
        return this.safeGet(Boolean::parseBoolean, Boolean.class);
    }

    /**
     * Set the value as a boolean
     *
     * @param valueAsBoolean The value
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public void setValueAsBoolean(final Boolean valueAsBoolean) {
        this.safeSet(valueAsBoolean::toString, Boolean.class);
    }

    /**
     * Get the value as integer.
     *
     * @return The value as integer
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public Integer getValueAsInteger() {
        return this.safeGet(safeValue -> {
            try {
                return Integer.valueOf(safeValue);
            } catch (final NumberFormatException e) {
                this.logger.error(String.format("%s - Error occurred during parsing a Integer option value : %s -> %s",
                    this.enumClass.getName(), this.getRawKey(), safeValue), e);
                return 0;
            }
        }, Integer.class);
    }

    /**
     * Set the value as integer
     *
     * @param valueAsInteger The value
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public void setValueAsInteger(final Integer valueAsInteger) {
        this.safeSet(valueAsInteger::toString, Integer.class);
    }

    /**
     * Get the value as long.
     *
     * @return The value as long
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public Long getValueAsLong() {
        return this.safeGet(safeValue -> {
            try {
                return Long.valueOf(safeValue);
            } catch (final NumberFormatException e) {
                this.logger.error(String.format("%s - Error occurred during parsing a Long option value : %s -> %s",
                    this.enumClass.getName(), this.getRawKey(), safeValue), e);
                return 0L;
            }
        }, Long.class);
    }

    /**
     * Set the value as long
     *
     * @param valueAsLong The value
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public void setValueAsLong(final Long valueAsLong) {
        this.safeSet(valueAsLong::toString, Long.class);
    }

    /**
     * Get the value as a list of long.
     *
     * @return The value as a list of long
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public List<Long> getValueAsLongList() {
        return this.getValueAsSimpleList(Long.class);
    }

    /**
     * Set the value from a list of long.
     *
     * @param list The list of long
     * @throws RuntimeException if an error occur
     */
    public void setValueAsLongList(final List<Long> list) {
        this.setValueAsSimpleList(Long.class, list);
    }

    /**
     * Get the value as a list of integer.
     *
     * @return The value as a list of integer
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public List<Integer> getValueAsIntegerList() {
        return this.getValueAsSimpleList(Integer.class);
    }

    /**
     * Set the value from a list of integer.
     *
     * @param list The list of integer
     * @throws RuntimeException if an error occur
     */
    public void setValueAsIntegerList(final List<Integer> list) {
        this.setValueAsSimpleList(Integer.class, list);
    }

    /**
     * Get the value as a list of string.
     *
     * @return The value as a list of string
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    public List<String> getValueAsStringList() {
        return this.getValueAsSimpleList(String.class);
    }

    /**
     * Set the value from a list of string.
     *
     * @param list The list of string
     * @throws RuntimeException if an error occur
     */
    public void setValueAsStringList(final List<String> list) {
        this.setValueAsSimpleList(String.class, list);
    }

    /**
     * Get the value as a list of T
     *
     * @param <V>    The type of the list
     * @param vClass The class of T
     * @return The value as a list of T
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    protected <V> List<V> getValueAsSimpleList(final Class<V> vClass) {
        final ObjectMapper mapper = new ObjectMapper();
        final JavaType type = mapper.getTypeFactory().constructCollectionType(ArrayList.class, vClass);

        return this.safeGet(safeValue -> {
            try {
                return mapper.readValue(safeValue, type);
            } catch (final IOException e) {
                this.logger.error(String.format("%s - Error occurred during parsing of the JSON value option : %s -> %s",
                    this.enumClass.getName(), this.getRawKey(), safeValue), e);
                return new ArrayList<>();
            }
        }, List.class, vClass);
    }

    /**
     * Set the value from a list of T.
     *
     * @param <V>    The type of the list
     * @param vClass The class of T
     * @param list   The list of T
     * @throws RuntimeException if an error occur
     */
    public <V> void setValueAsSimpleList(final Class<V> vClass,
                                         final List<V> list) {
        this.safeSet(() -> {
            final ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(list);
            } catch (final JsonProcessingException e) {
                this.logger.error(
                    String.format(
                        "%s - Error occurred while mapping List<%s> to json. Key : %s",
                        this.enumClass.getName(),
                        vClass.getName(),
                        this.getRawKey()
                    ),
                    e
                );
                throw new RuntimeException("Error while mapping the List of elements.", e);
            }
        }, List.class, vClass);
    }

    /**
     * Get the value as an enum T.
     *
     * @param <V>    The type of the enum
     * @param vClass The class of T
     * @return The enum T or null
     * @throws BadRuntimeTypeException If the types doesn't match
     */
    protected <V extends Enum<V>> V getValueAsEnum(final Class<V> vClass) {
        return this.safeGet(safeValue -> enumFromValue(vClass, safeValue), vClass);
    }

    /**
     * Set the value as an enum T.
     *
     * @param <V>         The type of the enum
     * @param vClass      The class of T
     * @param valueAsEnum The value
     * @throws BadRuntimeTypeException If the types doesn't match
     */
    protected <V extends Enum<V>> void setValueAsEnum(final Class<V> vClass,
                                                      final V valueAsEnum) {
        this.safeSet(() -> valueFromEnum(vClass, valueAsEnum), vClass);
    }

    /**
     * Sets value as object.
     *
     * @param <V>    the type parameter
     * @param vClass the v class
     * @return the value as object
     * @throws BadRuntimeTypeException If the type doesn't match
     * @throws RuntimeException        If the object can't be read correctly
     */
    protected <V> V getValueAsObject(final Class<V> vClass) {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectReader reader = mapper.readerFor(vClass);

        return this.safeGet(safeValue -> {
            if (safeValue == null) {
                return null;
            }
            try {
                return reader.readValue(safeValue);
            } catch (final JsonProcessingException e) {
                this.logger.error(
                    String.format(
                        "%s - Error occurred while reading %s from json. Key : %s",
                        this.enumClass.getName(),
                        vClass.getName(),
                        this.getRawKey()
                    ),
                    e
                );
                throw new RuntimeException("Invalid value.", e);
            }
        }, vClass);
    }

    /**
     * Sets value as object.
     *
     * @param <V>         the type parameter
     * @param vClass      the v class
     * @param objectValue the object value
     * @throws RuntimeException if an error occur
     */
    protected <V> void setValueAsObject(final Class<V> vClass, final V objectValue) {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectWriter writer = mapper.writerFor(vClass);

        this.safeSet(() -> {
            try {
                return writer.writeValueAsString(objectValue);
            } catch (final JsonProcessingException e) {
                this.logger.error(
                    String.format(
                        "%s - Error occurred while mapping %s to json. Key : %s",
                        this.enumClass.getName(),
                        vClass.getName(),
                        this.getRawKey()
                    ),
                    e
                );
                throw new RuntimeException("Error while mapping the object.", e);
            }
        }, vClass);
    }

    /**
     * Get and check the context defined by the annotation Option.
     * Give a safe value to get. The value can be null.
     *
     * @param <R>    The type of the result
     * @param getter The actual getter
     * @param types  The types to check
     * @return The result of the getter
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    protected <R> R safeGet(final Function<String, R> getter,
                            final Class<?>... types) {
        final Option option = this.getOptionAnnotation();
        if (option == null || this.isTypeCorrect(option, types)) {
            if (this.value != null) {
                return getter.apply(this.value);
            } else if (option != null && !option.defaultValue().isEmpty()) {
                return getter.apply(option.defaultValue());
            } else {
                return null;
            }
        } else {
            throw new BadRuntimeTypeException();
        }
    }

    /**
     * Set and check the context defined by the annotation Option.
     *
     * @param setter The actual getter
     * @param types  The types to check
     * @throws BadRuntimeTypeException If the type doesn't match
     */
    protected void safeSet(final Supplier<String> setter,
                           final Class<?>... types) {
        final Option option = this.getOptionAnnotation();
        if (option == null || this.isTypeCorrect(option, types)) {
            this.value = setter.get();
        } else {
            throw new BadRuntimeTypeException();
        }
    }

    /**
     * Get the annotation Option of the current key.
     *
     * @return The annotation Option
     * @see Option
     */
    protected Option getOptionAnnotation() {
        try {
            return this.enumClass.getField(this.getKeyEnum().name()).getAnnotation(Option.class);
        } catch (final NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Check is the type match the option default value annotation type.
     *
     * @param option  The annotation
     * @param classes The expected classes
     * @return {@code true} if it match
     */
    protected boolean isTypeCorrect(final Option option, final Class<?>... classes) {
        if (option == null) {
            return false;
        }
        final Class<?>[] type = option.type();

        return Arrays.equals(type, classes);
    }
}
