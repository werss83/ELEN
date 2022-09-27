package models.care;

import models.BaseAttributeModel;

import javax.persistence.*;

/**
 * CareCriteriaModel.
 *
 * @author Lucas Stadelmann
 * @since 20.07.03
 */
@Entity
@Table(name = "care_criteria")
public class CareCriteriaModel extends BaseAttributeModel<CareCriteriaModel> {

    /**
     * The Booked care.
     */
    @OneToOne(targetEntity = BookedCareModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "booked_care_id", nullable = false, unique = true)
    private BookedCareModel bookedCare;

    /**
     * Instantiates a new Care preference model.
     */
    public CareCriteriaModel() {
        super(CareCriteriaModel.class);
    }

    /**
     * Gets booked care.
     *
     * @return the booked care
     */
    public BookedCareModel getBookedCare() {
        return this.bookedCare;
    }

    /**
     * Sets booked care.
     *
     * @param bookedCare the booked care
     */
    public void setBookedCare(final BookedCareModel bookedCare) {
        this.bookedCare = bookedCare;
    }
}
