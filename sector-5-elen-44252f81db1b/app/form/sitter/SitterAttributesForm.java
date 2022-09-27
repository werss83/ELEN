package form.sitter;

import com.jackson42.play.form.databinders.joda.annotation.JodaDateTimeFormat;
import form.AForm;
import models.sitter.SitterAttributeModel;
import models.sitter.SitterModel;
import models.sitter.enumeration.*;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.libs.Files;
import play.mvc.Http;
import utils.images.ImageProcessing;

import java.awt.*;
import java.util.List;

/**
 * ParentPreferences.
 *
 * @author Pierre Adam
 * @since 20.07.12
 */
@Constraints.ValidateWithPayload
public class SitterAttributesForm extends AForm implements Constraints.ValidatableWithPayload<List<ValidationError>> {

    /**
     * The Woman sitter.
     */
    protected boolean sitterWoman;

    /**
     * The Allowed smoking sitter.
     */
    protected boolean sitterSmoke;

    /**
     * The Sitter guard types.
     */
    protected List<SitterGuardType> sitterGuardTypes;

    /**
     * The Sitter experiences.
     */
    protected List<SitterExperience> sitterExperiences;

    /**
     * The Sitter experience types.
     */
    protected List<SitterExperienceType> sitterExperienceTypes;

    /**
     * The Sitter situation.
     */
    @Constraints.Required
    protected SitterSituation sitterSituation;

    /**
     * The Sitter situation detail.
     */
    protected String sitterSituationDetail;

    /**
     * The Sitter certifications.
     */
    protected List<SitterCertification> sitterCertifications;

    /**
     * The Sitter extra certification.
     */
    protected String sitterExtraCertification;

    /**
     * The Sitter spoken languages.
     */
    protected List<SitterSpokenLanguage> sitterSpokenLanguages;

    /**
     * The Sitter homework capabilities.
     */
    protected List<SitterHomeworkCapability> sitterHomeworkCapabilities;

    /**
     * The Sitter car situation.
     */
    protected SitterCarSituation sitterCarSituation;

    /**
     * The Sitter public transport.
     */
    protected List<SitterPublicTransport> sitterPublicTransports;

    /**
     * The Sitter availabilities.
     */
    protected List<SitterAvailability> sitterAvailabilities;

    /**
     * The Sitter birthday.
     */
    @JodaDateTimeFormat(patterns = "dd/MM/YYYY")
    protected DateTime sitterBirthday;

    /**
     * The Sitter commendation.
     */
    protected String sitterCommendation;

    /**
     * The Sitter information.
     */
    protected String sitterInformation;

    /**
     * The Past experience.
     */
    protected String pastExperience;

    /**
     * The Social security number.
     */
    @Constraints.MaxLength(15)
    protected String socialSecurityNumber;

    /**
     * The sitter picture data.
     */
    protected Http.MultipartFormData.FilePart<Files.TemporaryFile> sitterPicture;

    /**
     * The sitter picture data.
     */
    private byte[] sitterPictureData;

    /**
     * The Picture mime type.
     */
    private String pictureMimeType;

    /**
     * Instantiates a new Parent preferences form.
     */
    public SitterAttributesForm() {
    }

    /**
     * Instantiates a new Parent preferences form.
     *
     * @param sitter the sitter
     */
    public SitterAttributesForm(final SitterModel sitter) {
        if (sitter == null) {
            return;
        }
        final SitterAttributeModel attributes = sitter.getSitterCharacteristic();
        this.sitterWoman = attributes.getGender();
        this.sitterSmoke = attributes.smoke();
        this.sitterGuardTypes = attributes.getGuardTypes();
        this.sitterExperiences = attributes.getExperiences();
        this.sitterExperienceTypes = attributes.getExperienceTypes();
        this.sitterSituation = attributes.getSituation();
        this.sitterSituationDetail = attributes.getSituationDetail();
        this.sitterCertifications = attributes.getCertifications();
        this.sitterExtraCertification = attributes.getExtraCertification();
        this.sitterSpokenLanguages = attributes.getSpokenLanguages();
        this.sitterHomeworkCapabilities = attributes.getHomeworkCapabilities();
        this.sitterCarSituation = attributes.getCarSituation();
        this.sitterPublicTransports = attributes.getPublicTransports();
        this.sitterAvailabilities = attributes.getAvailabilities();
        this.sitterBirthday = sitter.getBirthday().toDateTime(new LocalTime(0));
        this.sitterCommendation = attributes.getCommendation();
        this.sitterInformation = attributes.getInformation();
        this.pastExperience = attributes.getPastExperience();
        this.socialSecurityNumber = attributes.getSocialSecurityNumber();
    }

    /**
     * Is sitter woman boolean.
     *
     * @return the boolean
     */
    public boolean isSitterWoman() {
        return this.sitterWoman;
    }

    /**
     * Sets sitter woman.
     *
     * @param sitterWoman the sitter woman
     */
    public void setSitterWoman(final boolean sitterWoman) {
        this.sitterWoman = sitterWoman;
    }

    /**
     * Is sitter smoke boolean.
     *
     * @return the boolean
     */
    public boolean isSitterSmoke() {
        return this.sitterSmoke;
    }

    /**
     * Sets sitter smoke.
     *
     * @param sitterSmoke the sitter smoke
     */
    public void setSitterSmoke(final boolean sitterSmoke) {
        this.sitterSmoke = sitterSmoke;
    }

    /**
     * Gets sitter guard types.
     *
     * @return the sitter guard types
     */
    public List<SitterGuardType> getSitterGuardTypes() {
        return this.sitterGuardTypes;
    }

    /**
     * Sets sitter guard types.
     *
     * @param sitterGuardTypes the sitter guard types
     */
    public void setSitterGuardTypes(final List<SitterGuardType> sitterGuardTypes) {
        this.sitterGuardTypes = sitterGuardTypes;
    }

    /**
     * Gets sitter experiences.
     *
     * @return the sitter experiences
     */
    public List<SitterExperience> getSitterExperiences() {
        return this.sitterExperiences;
    }

    /**
     * Sets sitter experiences.
     *
     * @param sitterExperiences the sitter experiences
     */
    public void setSitterExperiences(final List<SitterExperience> sitterExperiences) {
        this.sitterExperiences = sitterExperiences;
    }

    /**
     * Gets sitter experience types.
     *
     * @return the sitter experience types
     */
    public List<SitterExperienceType> getSitterExperienceTypes() {
        return this.sitterExperienceTypes;
    }

    /**
     * Sets sitter experience types.
     *
     * @param sitterExperienceTypes the sitter experience types
     */
    public void setSitterExperienceTypes(final List<SitterExperienceType> sitterExperienceTypes) {
        this.sitterExperienceTypes = sitterExperienceTypes;
    }

    /**
     * Gets sitter situation.
     *
     * @return the sitter situation
     */
    public SitterSituation getSitterSituation() {
        return this.sitterSituation;
    }

    /**
     * Sets sitter situation.
     *
     * @param sitterSituation the sitter situation
     */
    public void setSitterSituation(final SitterSituation sitterSituation) {
        this.sitterSituation = sitterSituation;
    }

    /**
     * Gets sitter situation detail.
     *
     * @return the sitter situation detail
     */
    public String getSitterSituationDetail() {
        return this.sitterSituationDetail;
    }

    /**
     * Sets sitter situation detail.
     *
     * @param sitterSituationDetail the sitter situation detail
     */
    public void setSitterSituationDetail(final String sitterSituationDetail) {
        this.sitterSituationDetail = sitterSituationDetail;
    }

    /**
     * Gets sitter certifications.
     *
     * @return the sitter certifications
     */
    public List<SitterCertification> getSitterCertifications() {
        return this.sitterCertifications;
    }

    /**
     * Sets sitter certifications.
     *
     * @param sitterCertifications the sitter certifications
     */
    public void setSitterCertifications(final List<SitterCertification> sitterCertifications) {
        this.sitterCertifications = sitterCertifications;
    }

    /**
     * Gets sitter extra certification.
     *
     * @return the sitter extra certification
     */
    public String getSitterExtraCertification() {
        return this.sitterExtraCertification;
    }

    /**
     * Sets sitter extra certification.
     *
     * @param sitterExtraCertification the sitter extra certification
     */
    public void setSitterExtraCertification(final String sitterExtraCertification) {
        this.sitterExtraCertification = sitterExtraCertification;
    }

    /**
     * Gets sitter spoken languages.
     *
     * @return the sitter spoken languages
     */
    public List<SitterSpokenLanguage> getSitterSpokenLanguages() {
        return this.sitterSpokenLanguages;
    }

    /**
     * Sets sitter spoken languages.
     *
     * @param sitterSpokenLanguages the sitter spoken languages
     */
    public void setSitterSpokenLanguages(final List<SitterSpokenLanguage> sitterSpokenLanguages) {
        this.sitterSpokenLanguages = sitterSpokenLanguages;
    }

    /**
     * Gets sitter homework capabilities.
     *
     * @return the sitter homework capabilities
     */
    public List<SitterHomeworkCapability> getSitterHomeworkCapabilities() {
        return this.sitterHomeworkCapabilities;
    }

    /**
     * Sets sitter homework capabilities.
     *
     * @param sitterHomeworkCapabilities the sitter homework capabilities
     */
    public void setSitterHomeworkCapabilities(final List<SitterHomeworkCapability> sitterHomeworkCapabilities) {
        this.sitterHomeworkCapabilities = sitterHomeworkCapabilities;
    }

    /**
     * Gets sitter car situation.
     *
     * @return the sitter car situation
     */
    public SitterCarSituation getSitterCarSituation() {
        return this.sitterCarSituation;
    }

    /**
     * Sets sitter car situation.
     *
     * @param sitterCarSituation the sitter car situation
     */
    public void setSitterCarSituation(final SitterCarSituation sitterCarSituation) {
        this.sitterCarSituation = sitterCarSituation;
    }

    /**
     * Gets sitter public transport.
     *
     * @return the sitter public transport
     */
    public List<SitterPublicTransport> getSitterPublicTransports() {
        return this.sitterPublicTransports;
    }

    /**
     * Sets sitter public transport.
     *
     * @param sitterPublicTransports the sitter public transport
     */
    public void setSitterPublicTransports(final List<SitterPublicTransport> sitterPublicTransports) {
        this.sitterPublicTransports = sitterPublicTransports;
    }

    /**
     * Gets sitter disponibilities.
     *
     * @return the sitter disponibilities
     */
    public List<SitterAvailability> getSitterAvailabilities() {
        return this.sitterAvailabilities;
    }

    /**
     * Sets sitter disponibilities.
     *
     * @param sitterAvailabilities the sitter disponibilities
     */
    public void setSitterAvailabilities(final List<SitterAvailability> sitterAvailabilities) {
        this.sitterAvailabilities = sitterAvailabilities;
    }

    /**
     * Gets sitter birthday.
     *
     * @return the sitter birthday
     */
    public DateTime getSitterBirthday() {
        return this.sitterBirthday;
    }

    /**
     * Sets sitter birthday.
     *
     * @param sitterBirthday the sitter birthday
     */
    public void setSitterBirthday(final DateTime sitterBirthday) {
        this.sitterBirthday = sitterBirthday;
    }

    /**
     * Gets sitter commendation.
     *
     * @return the sitter commendation
     */
    public String getSitterCommendation() {
        return this.sitterCommendation;
    }

    /**
     * Sets sitter commendation.
     *
     * @param sitterCommendation the sitter commendation
     */
    public void setSitterCommendation(final String sitterCommendation) {
        this.sitterCommendation = sitterCommendation;
    }

    /**
     * Gets sitter information.
     *
     * @return the sitter information
     */
    public String getSitterInformation() {
        return this.sitterInformation;
    }

    /**
     * Sets sitter information.
     *
     * @param sitterInformation the sitter information
     */
    public void setSitterInformation(final String sitterInformation) {
        this.sitterInformation = sitterInformation;
    }

    /**
     * Gets sitter picture.
     *
     * @return the sitter picture
     */
    public Http.MultipartFormData.FilePart<Files.TemporaryFile> getSitterPicture() {
        return this.sitterPicture;
    }

    /**
     * Sets sitter picture.
     *
     * @param sitterPicture the sitter picture
     */
    public void setSitterPicture(final Http.MultipartFormData.FilePart<Files.TemporaryFile> sitterPicture) {
        this.sitterPicture = sitterPicture;
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

    /**
     * Update criteria.
     *
     * @param sitter the sitter
     */
    public void updateAttributes(final SitterModel sitter) {
        final SitterAttributeModel attributes = sitter.getSitterCharacteristic();
        attributes.setGender(this.sitterWoman);
        attributes.setSmoke(this.sitterSmoke);
        attributes.setGuardTypes(this.sitterGuardTypes);
        attributes.setExperiences(this.sitterExperiences);
        attributes.setExperienceTypes(this.sitterExperienceTypes);
        attributes.setSituation(this.sitterSituation);
        attributes.setSituationDetail(this.sitterSituationDetail);
        attributes.setCertifications(this.sitterCertifications);
        attributes.setExtraCertification(this.sitterExtraCertification);
        attributes.setSpokenLanguages(this.sitterSpokenLanguages);
        attributes.setHomeworkCapabilities(this.sitterHomeworkCapabilities);
        attributes.setCarSituation(this.sitterCarSituation);
        attributes.setPublicTransports(this.sitterPublicTransports);
        attributes.setAvailabilities(this.sitterAvailabilities);
        attributes.setCommendation(this.sitterCommendation);
        attributes.setInformation(this.sitterInformation);
        attributes.setPastExperience(this.pastExperience);
        attributes.setSocialSecurityNumber(this.socialSecurityNumber);
        if (this.sitterBirthday != null) {
            sitter.setBirthday(this.sitterBirthday.toLocalDate());
        } else {
            sitter.setBirthday(null);
        }
        if (this.sitterPictureData != null) {
            sitter.setPicture(this.sitterPictureData, this.pictureMimeType);
        }
    }

    @Override
    public List<ValidationError> validate(final Constraints.ValidationPayload payload) {
        return this.easyListValidate(validationErrors -> {
            if (this.sitterPicture != null) {
                try {
                    final ImageProcessing.ImageResult result = ImageProcessing.resize(this.sitterPicture.getRef(), new Dimension(512, 512), true);
                    if (result != null) {
                        this.sitterPictureData = result.getDataAsByteArray();
                        this.pictureMimeType = result.getMimeType();
                    } else {
                        validationErrors.add(new ValidationError("sitterPicture", "error.invalid"));
                    }
                } catch (final Exception e) {
                    this.logger.debug("An error occurred.", e);
                    validationErrors.add(new ValidationError("sitterPicture", "error.invalidImageFormat"));
                }
            }
        });
    }
}
