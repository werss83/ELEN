package models.sitter;

import io.ebean.annotation.DbArray;
import models.BaseAttributeModel;
import models.sitter.enumeration.*;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SitterAttributeModel.
 *
 * @author Lucas Stadelmann
 * @since 20.07.03
 */
@Entity
@Table(name = "sitter_attribute")
public class SitterAttributeModel extends BaseAttributeModel<SitterAttributeModel> {

    /**
     * The Guard types.
     */
    @DbArray
    @Column(name = "guard_type", unique = false, nullable = false)
    private List<SitterGuardType> guardTypes;

    /**
     * The Experiences.
     */
    @DbArray
    @Column(name = "experiences", unique = false, nullable = false)
    private List<SitterExperience> experiences;

    /**
     * The Experience types.
     */
    @DbArray
    @Column(name = "experience_types", unique = false, nullable = false)
    private List<SitterExperienceType> experienceTypes;

    /**
     * The Situation.
     */
    @Column(name = "situation", unique = false, nullable = false)
    private SitterSituation situation;

    /**
     * The Situation detail.
     */
    @Column(name = "situation_detail", unique = false, nullable = true, columnDefinition = "TEXT")
    private String situationDetail;

    /**
     * The Certifications.
     */
    @DbArray
    @Column(name = "certifications", unique = false, nullable = false)
    private List<SitterCertification> certifications;

    /**
     * The Extra certification.
     */
    @Column(name = "extra_certification", unique = false, nullable = true, columnDefinition = "TEXT")
    private String extraCertification;

    /**
     * The Spoken languages.
     */
    @DbArray
    @Column(name = "spoken_languages", unique = false, nullable = false)
    private List<SitterSpokenLanguage> spokenLanguages;

    /**
     * The Homework capabilities.
     */
    @DbArray
    @Column(name = "homework_capabilities", unique = false, nullable = false)
    private List<SitterHomeworkCapability> homeworkCapabilities;

    /**
     * The Car situation.
     */
    @Column(name = "car_situation", unique = false, nullable = false)
    private SitterCarSituation carSituation;

    /**
     * The Public transport.
     */
    @DbArray
    @Column(name = "public_transports", unique = false, nullable = false)
    private List<SitterPublicTransport> publicTransports;

    /**
     * The Availabilities.
     */
    @DbArray
    @Column(name = "availabilities", unique = false, nullable = false)
    private List<SitterAvailability> availabilities;

    /**
     * The Commendation.
     */
    @Column(name = "commendation", unique = false, nullable = true, columnDefinition = "TEXT")
    private String commendation;

    /**
     * The Information.
     */
    @Column(name = "information", unique = false, nullable = true, columnDefinition = "TEXT")
    private String information;

    /**
     * The Sitter.
     */
    @OneToOne(targetEntity = SitterModel.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "sitter_id", nullable = false, unique = true)
    private SitterModel sitter;

    /**
     * The Past experience.
     */
    @Column(name = "past_experience", unique = false, nullable = true, columnDefinition = "TEXT")
    private String pastExperience;

    /**
     * The Past experience.
     */
    @Constraints.MaxLength(15)
    @Column(name = "social_security_number", unique = false, nullable = true)
    private String socialSecurityNumber;

    /**
     * Instantiates a new Sitter characteristic model.
     */
    public SitterAttributeModel() {
        super(SitterAttributeModel.class);
        this.setGuardTypes(new ArrayList<>());
        this.setExperiences(new ArrayList<>());
        this.setExperienceTypes(new ArrayList<>());
        this.setSituation(SitterSituation.ACTIVE_WITHOUT_CHILDREN);
        this.setCertifications(new ArrayList<>());
        this.setSpokenLanguages(new ArrayList<>());
        this.setHomeworkCapabilities(new ArrayList<>());
        this.setCarSituation(SitterCarSituation.NO);
        this.setPublicTransports(new ArrayList<>());
        this.setAvailabilities(new ArrayList<>());
    }

    /**
     * Gets the sitter gender.
     *
     * @return false for a male or true for a female
     */
    public boolean getGender() {
        return this.gender;
    }

    /**
     * Sets sitter gender.
     *
     * @param gender the gender
     */
    public void setGender(final boolean gender) {
        this.gender = gender;
    }

    /**
     * Gets guard types.
     *
     * @return the guard types
     */
    public List<SitterGuardType> getGuardTypes() {
        return this.guardTypes;
    }

    /**
     * Sets guard types.
     *
     * @param guardTypes the guard types
     */
    public void setGuardTypes(final List<SitterGuardType> guardTypes) {
        this.guardTypes = guardTypes;
    }

    /**
     * Gets experiences.
     *
     * @return the experiences
     */
    public List<SitterExperience> getExperiences() {
        return this.experiences;
    }

    /**
     * Sets experiences.
     *
     * @param experiences the experiences
     */
    public void setExperiences(final List<SitterExperience> experiences) {
        this.experiences = experiences;
        this.experienceWithChildren = this.experiences.contains(SitterExperience.FAMILY_EXPERIENCE)
            || this.experiences.contains(SitterExperience.PROFESSIONAL_EXPERIENCE);
    }

    /**
     * Gets experience types.
     *
     * @return the experience types
     */
    public List<SitterExperienceType> getExperienceTypes() {
        return this.experienceTypes;
    }

    /**
     * Sets experience types.
     *
     * @param experienceTypes the experience types
     */
    public void setExperienceTypes(final List<SitterExperienceType> experienceTypes) {
        this.experienceTypes = experienceTypes;
    }

    /**
     * Gets situation.
     *
     * @return the situation
     */
    public SitterSituation getSituation() {
        return this.situation;
    }

    /**
     * Sets situation.
     *
     * @param situation the situation
     */
    public void setSituation(final SitterSituation situation) {
        this.situation = situation;
    }

    /**
     * Gets situation detail.
     *
     * @return the situation detail
     */
    public String getSituationDetail() {
        return this.situationDetail;
    }

    /**
     * Sets situation detail.
     *
     * @param situationDetail the situation detail
     */
    public void setSituationDetail(final String situationDetail) {
        this.situationDetail = situationDetail;
    }

    /**
     * Gets certifications.
     *
     * @return the certifications
     */
    public List<SitterCertification> getCertifications() {
        return this.certifications;
    }

    /**
     * Sets certifications.
     *
     * @param certifications the certifications
     */
    public void setCertifications(final List<SitterCertification> certifications) {
        this.certifications = certifications;
        this.childhoodTraining = this.certifications.contains(SitterCertification.YOUNG_CHILDREN_TRAINING);
    }

    /**
     * Gets extra certification.
     *
     * @return the extra certification
     */
    public String getExtraCertification() {
        return this.extraCertification;
    }

    /**
     * Sets extra certification.
     *
     * @param extraCertification the extra certification
     */
    public void setExtraCertification(final String extraCertification) {
        this.extraCertification = extraCertification;
    }

    /**
     * Gets spoken languages.
     *
     * @return the spoken languages
     */
    public List<SitterSpokenLanguage> getSpokenLanguages() {
        return this.spokenLanguages;
    }

    /**
     * Sets spoken languages.
     *
     * @param spokenLanguages the spoken languages
     */
    public void setSpokenLanguages(final List<SitterSpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
        this.speaksEnglish = this.spokenLanguages.contains(SitterSpokenLanguage.ENGLISH);
    }

    /**
     * Gets homework capabilities.
     *
     * @return the homework capabilities
     */
    public List<SitterHomeworkCapability> getHomeworkCapabilities() {
        return this.homeworkCapabilities;
    }

    /**
     * Sets homework capabilities.
     *
     * @param homeworkCapabilities the homework capabilities
     */
    public void setHomeworkCapabilities(final List<SitterHomeworkCapability> homeworkCapabilities) {
        this.homeworkCapabilities = homeworkCapabilities;
    }

    /**
     * Gets car situation.
     *
     * @return the car situation
     */
    public SitterCarSituation getCarSituation() {
        return this.carSituation;
    }

    /**
     * Sets car situation.
     *
     * @param carSituation the car situation
     */
    public void setCarSituation(final SitterCarSituation carSituation) {
        this.carSituation = carSituation;
        this.haveCar = this.carSituation == SitterCarSituation.YES;
    }

    /**
     * Gets public transport.
     *
     * @return the public transport
     */
    public List<SitterPublicTransport> getPublicTransports() {
        return this.publicTransports;
    }

    /**
     * Sets public transport.
     *
     * @param publicTransports the public transport
     */
    public void setPublicTransports(final List<SitterPublicTransport> publicTransports) {
        this.publicTransports = publicTransports;
    }

    /**
     * Gets availabilities.
     *
     * @return the availabilities
     */
    public List<SitterAvailability> getAvailabilities() {
        return this.availabilities;
    }

    /**
     * Sets availabilities.
     *
     * @param availabilities the availabilities
     */
    public void setAvailabilities(final List<SitterAvailability> availabilities) {
        this.availabilities = availabilities;
    }

    /**
     * Gets commendation.
     *
     * @return the commendation
     */
    public String getCommendation() {
        return this.commendation;
    }

    /**
     * Sets commendation.
     *
     * @param commendation the commendation
     */
    public void setCommendation(final String commendation) {
        this.commendation = commendation;
    }

    /**
     * Gets information.
     *
     * @return the information
     */
    public String getInformation() {
        return this.information;
    }

    /**
     * Sets information.
     *
     * @param information the information
     */
    public void setInformation(final String information) {
        this.information = information;
    }

    /**
     * Gets if the sitter have a car.
     *
     * @return if the sitter have a car
     */
    public boolean haveACar() {
        return this.haveCar;
    }

    /**
     * Gets if the sitter speaks english.
     *
     * @return if the sitter speaks english
     */
    public boolean speaksEnglish() {
        return this.speaksEnglish;
    }

    /**
     * Gets if the sitter have experience with children.
     *
     * @return if the sitter have experience with children
     */
    public boolean haveExperienceWithChildren() {
        return this.experienceWithChildren;
    }

    /**
     * Gets if the sitter smoke.
     *
     * @return if the sitter smoke
     */
    public boolean smoke() {
        return this.smoke;
    }

    /**
     * Sets if the sitter smoke.
     *
     * @param smoke if the sitter smoke
     */
    public void setSmoke(final boolean smoke) {
        this.smoke = smoke;
    }

    /**
     * Gets if the sitter have childhood training.
     *
     * @return if the sitter have childhood training
     */
    public boolean haveChildhoodTraining() {
        return this.childhoodTraining;
    }

    /**
     * Gets sitter.
     *
     * @return the sitter
     */
    public SitterModel getSitter() {
        return this.sitter;
    }

    /**
     * Sets sitter.
     *
     * @param sitter the sitter
     */
    public void setSitter(final SitterModel sitter) {
        this.sitter = sitter;
    }

    /**
     * Gets past experience.
     *
     * @return the past experience
     */
    public String getPastExperience() {
        return this.pastExperience;
    }

    /**
     * Sets past experience.
     *
     * @param pastExperience the past experience
     */
    public void setPastExperience(final String pastExperience) {
        this.pastExperience = pastExperience;
    }

    /**
     * Gets social security number.
     *
     * @return the social security number
     */
    public String getSocialSecurityNumber() {
        return this.socialSecurityNumber;
    }

    /**
     * Sets social security number.
     *
     * @param socialSecurityNumber the social security number
     */
    public void setSocialSecurityNumber(final String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }
}
