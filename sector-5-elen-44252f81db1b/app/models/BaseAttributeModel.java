package models;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * BaseAttributeModel.
 *
 * @param <T> the type parameter
 * @author Lucas Stadelmann
 * @since 20.07.03
 */
@MappedSuperclass
public class BaseAttributeModel<T extends BaseAttributeModel<T>> extends BaseModel<T> {

    /**
     * The Gender.
     * FALSE = Male
     * TRUE = Female
     */
    @Column(name = "gender", nullable = false, unique = false)
    protected boolean gender;

    /**
     * The Have it own transport.
     */
    @Column(name = "have_car", nullable = false, unique = false)
    protected boolean haveCar;

    /**
     * The Speaks english.
     */
    @Column(name = "speaks_english", nullable = false, unique = false)
    protected boolean speaksEnglish;

    /**
     * The Experienced with children.
     */
    @Column(name = "experience_with_children", nullable = false, unique = false)
    protected boolean experienceWithChildren;

    /**
     * The Smoker.
     */
    @Column(name = "smoke", nullable = false, unique = false)
    protected boolean smoke;

    /**
     * The Childhood training.
     */
    @Column(name = "childhood_training", nullable = false, unique = false)
    protected boolean childhoodTraining;

    /**
     * Instantiates a new Sitter requirement model.
     *
     * @param tClass the t class
     */
    public BaseAttributeModel(final Class<T> tClass) {
        super(tClass);
    }
}
