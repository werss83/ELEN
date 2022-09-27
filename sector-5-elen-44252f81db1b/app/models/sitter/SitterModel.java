package models.sitter;

import io.ebean.Finder;
import models.BaseModel;
import models.account.AccountModel;
import models.sitter.enumeration.SitterOptionKey;
import models.unavailability.PlannedUnavailabilityModel;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

/**
 * SitterModel.
 *
 * @author Lucas Stadelmann
 * @since 20.06.05
 */
@Entity
@Table(name = "sitter")
public class SitterModel extends BaseModel<SitterModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, SitterModel> find = new Finder<>(SitterModel.class);

    /**
     * The Account.
     */
    @OneToOne(targetEntity = AccountModel.class, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private AccountModel account;

    /**
     * The Sitter unavailability;
     */
    @OneToMany(targetEntity = PlannedUnavailabilityModel.class, mappedBy = "sitter", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<PlannedUnavailabilityModel> unavailability;

    /**
     * The Sitter characteristic.
     */
    @OneToOne(targetEntity = SitterAttributeModel.class, mappedBy = "sitter", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private SitterAttributeModel sitterCharacteristic;

    /**
     * The Sitter options.
     */
    @OneToMany(targetEntity = SitterOptionModel.class, mappedBy = "sitter", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<SitterOptionModel> options;

    /**
     * The Birthday.
     */
    @Column(name = "birthday", unique = false, nullable = true)
    private LocalDate birthday;

    /**
     * The Picture.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "picture", unique = false, nullable = true, columnDefinition = "BYTEA")
    private byte[] picture;

    /**
     * The Picture ETAG.
     */
    @Size(max = 32)
    @Column(name = "picture_mime_type", unique = false, nullable = true)
    private String pictureMimeType;

    /**
     * The Picture ETAG.
     */
    @Size(max = 32)
    @Column(name = "picture_etag", unique = false, nullable = true)
    private String pictureEtag;

    /**
     * Instantiates a new Sitter model.
     */
    SitterModel() {
        super(SitterModel.class);
    }

    /**
     * Instantiates a new Sitter model.
     *
     * @param account the account
     */
    public SitterModel(final AccountModel account) {
        super(SitterModel.class);
        this.account = account;
    }

    /**
     * Instantiates a new Sitter model.
     *
     * @param account the account
     */
    public SitterModel(final AccountModel account,
                       final SitterAttributeModel sitterCharacteristic) {
        this(account);
        this.sitterCharacteristic = sitterCharacteristic;
        this.sitterCharacteristic.setSitter(this);
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public AccountModel getAccount() {
        return this.account;
    }

    /**
     * Sets account.
     *
     * @param account the account
     */
    public void setAccount(final AccountModel account) {
        this.account = account;
    }

    /**
     * Gets unavailability.
     *
     * @return the unavailability
     */
    public List<PlannedUnavailabilityModel> getUnavailability() {
        return this.unavailability;
    }

    /**
     * Sets unavailability.
     *
     * @param unavailability the unavailability
     */
    public void setUnavailability(final List<PlannedUnavailabilityModel> unavailability) {
        this.unavailability = unavailability;
    }

    /**
     * Add unavailability.
     *
     * @param unavailability the unavailability
     */
    public boolean addUnavailability(final PlannedUnavailabilityModel unavailability) {
        return this.unavailability.add(unavailability);
    }

    /**
     * Remove unavailability boolean.
     *
     * @param unavailability the unavailability
     * @return the boolean
     */
    public boolean removeUnavailability(final PlannedUnavailabilityModel unavailability) {
        return this.unavailability.remove(unavailability);
    }

    /**
     * Gets sitter characteristic.
     *
     * @return the sitter characteristic
     */
    public SitterAttributeModel getSitterCharacteristic() {
        return this.sitterCharacteristic;
    }

    /**
     * Sets sitter characteristic.
     *
     * @param sitterCharacteristic the sitter characteristic
     */
    public void setSitterCharacteristic(final SitterAttributeModel sitterCharacteristic) {
        this.sitterCharacteristic = sitterCharacteristic;
    }

    /**
     * Gets options.
     *
     * @return the options
     */
    public List<SitterOptionModel> getOptions() {
        return this.options;
    }

    /**
     * Gets birthday.
     *
     * @return the birthday
     */
    public LocalDate getBirthday() {
        return this.birthday;
    }

    /**
     * Sets birthday.
     *
     * @param birthday the birthday
     */
    public void setBirthday(final LocalDate birthday) {
        this.birthday = birthday;
    }

    /**
     * Get picture byte.
     *
     * @return the byte
     */
    public byte[] getPicture() {
        return this.picture;
    }

    /**
     * Sets picture.
     *
     * @param picture  the picture
     * @param mimeType the mime type
     */
    public void setPicture(@NotNull final byte[] picture, @NotNull final String mimeType) {
        this.picture = picture;
        this.pictureMimeType = mimeType;
        this.pictureEtag = UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Remove picture.
     */
    public void removePicture() {
        this.pictureMimeType = null;
        this.pictureEtag = null;
        this.picture = null;
    }

    /**
     * Gets picture mime type.
     *
     * @return the picture mime type
     */
    public String getPictureMimeType() {
        return this.pictureMimeType;
    }

    /**
     * Gets picture etag.
     *
     * @return the picture etag
     */
    public String getPictureEtag() {
        return this.pictureEtag;
    }

    /**
     * Gets option.
     *
     * @param key the key
     * @return the option
     */
    private SitterOptionModel getOption(final SitterOptionKey key) {
        return SitterOptionModel.whereKey(
            SitterOptionModel.find.query()
                .where()
                .eq("sitter", this),
            key
        ).findOneOrEmpty().orElse(new SitterOptionModel(key, this));
    }
}
