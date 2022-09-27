package models.account;

import io.ebean.Finder;
import models.BaseModel;
import models.parent.ParentModel;
import models.sitter.SitterModel;
import org.joda.time.DateTime;
import org.postgis.Point;
import security.Role;
import security.password.PasswordHandler;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * AccountModel.
 *
 * @author Pierre Adam
 * @since 19.02.28
 */
@Entity
@Table(name = "account")
public class AccountModel extends BaseModel<AccountModel> {

    /**
     * Helpers to request model.
     */
    public static final Finder<Long, AccountModel> find = new Finder<>(AccountModel.class);

    /**
     * First name of the account holder.
     */
    @Size(max = 128)
    @Column(name = "first_name", nullable = false, unique = false)
    private String firstName;

    /**
     * Last name of the account holder.
     */
    @Size(max = 128)
    @Column(name = "last_name", nullable = false, unique = false)
    private String lastName;

    /**
     * Email addresses registered to this account.
     */
    @Size(max = 512)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * The address
     */
    @Size(max = 512)
    @Column(name = "address", nullable = false, unique = false)
    private String address;

    /**
     * The address
     */
    @Size(max = 256)
    @Column(name = "city", nullable = false, unique = false)
    private String city;

    /**
     * The zip code
     */
    @Size(max = 16)
    @Column(name = "zip_code", nullable = false, unique = false)
    private String zipCode;

    /**
     * The address complement
     */
    @Size(max = 256)
    @Column(name = "additional_address", nullable = true, unique = false)
    private String additionalAddress;

    /**
     * The address complement
     */
    @Column(name = "coordinate", nullable = false, unique = false)
    private Point coordinate;

    /**
     * The phone number
     */
    @Size(max = 16)
    @Column(name = "phone_number", nullable = false, unique = false)
    private String phoneNumber;

    /**
     * The Profile picture.
     */
    @Size(max = 1024)
    @Column(name = "profile_picture", nullable = true, unique = false)
    private String profilePicture;

    /**
     * The Password for this account.
     */
    @Size(max = 256)
    @Column(name = "password", nullable = true, unique = false)
    private String password;

    /**
     * The google sso id
     */
    @Size(max = 32)
    @Column(name = "google_id", nullable = true, unique = true)
    private String googleId;

    /**
     * The facebook sso id
     */
    @Size(max = 32)
    @Column(name = "facebook_id", nullable = true, unique = true)
    private String facebookId;

    /**
     * The twitter sso id
     */
    @Size(max = 32)
    @Column(name = "twitter_id", nullable = true, unique = true)
    private String twitterId;

    /**
     * The Last login.
     */
    @Column(name = "last_login", nullable = true, unique = false)
    private DateTime lastLogin;

    /**
     * Is the account active.
     */
    @Column(name = "active", nullable = false, unique = false)
    private Boolean active;

    /**
     * Is the account active.
     */
    @Column(name = "email_verified", nullable = false, unique = false)
    private Boolean emailVerified;

    /**
     * The Roles.
     */
    @OneToMany(targetEntity = RoleModel.class, mappedBy = "account", fetch = FetchType.LAZY)
    private List<RoleModel> roles;

    /**
     * The Parent.
     */
    @OneToOne(targetEntity = ParentModel.class, mappedBy = "account", fetch = FetchType.EAGER, optional = true)
    private ParentModel parent;

    /**
     * The Sitter.
     */
    @OneToOne(targetEntity = SitterModel.class, mappedBy = "account", fetch = FetchType.EAGER, optional = true)
    private SitterModel sitter;

    /**
     * Instantiates a new Account model.
     */
    AccountModel() {
        super(AccountModel.class);
    }

    /**
     * Default constructor.
     *
     * @param firstName   the first name
     * @param lastName    the last name
     * @param email       the email
     * @param address     the address
     * @param city        the city
     * @param zipCode     the zip code
     * @param phoneNumber the phone number
     */
    public AccountModel(final String firstName,
                        final String lastName,
                        final String email,
                        final String address,
                        final String city,
                        final String zipCode,
                        final String phoneNumber) {
        super(AccountModel.class);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.phoneNumber = phoneNumber;
        this.active = true;
        this.emailVerified = false;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return String.format("%s %s", this.firstName, this.lastName);
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Gets email verified.
     *
     * @return the email verified
     */
    public Boolean getEmailVerified() {
        return this.emailVerified;
    }

    /**
     * Sets email verified.
     *
     * @param emailVerified the email verified
     */
    public void setEmailVerified(final Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * Gets postal code.
     *
     * @return the postal code
     */
    public String getZipCode() {
        return this.zipCode;
    }

    /**
     * Gets short zip code.
     *
     * @return the short zip code
     */
    public String getShortZipCode() {
        if (this.zipCode.startsWith("97") || this.zipCode.startsWith("98")) {
            return this.zipCode.substring(0, 3);
        }
        return this.zipCode.substring(0, 2);
    }

    /**
     * Sets postal code.
     *
     * @param zipCode the postal code
     */
    public void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets address complement.
     *
     * @return the address complement
     */
    public String getAdditionalAddress() {
        return this.additionalAddress;
    }

    /**
     * Sets address complement.
     *
     * @param additionalAddress the address complement
     */
    public void setAdditionalAddress(final String additionalAddress) {
        this.additionalAddress = additionalAddress;
    }

    /**
     * Gets point.
     *
     * @return the point
     */
    public Point getCoordinate() {
        return this.coordinate;
    }

    /**
     * Sets point.
     *
     * @param coordinate the point
     */
    public void setCoordinate(final Point coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets phone number.
     *
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets profile picture.
     *
     * @return the profile picture
     */
    public String getProfilePicture() {
        return this.profilePicture;
    }

    /**
     * Sets profile picture.
     *
     * @param profilePicture the profile picture
     */
    public void setProfilePicture(final String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(final String password) {
        final PasswordHandler passwordHandler = new PasswordHandler();
        this.password = passwordHandler.newPassword(password);
    }

    /**
     * Check password boolean.
     *
     * @param passwordToCheck the password
     * @return the boolean
     */
    public boolean checkPassword(final String passwordToCheck) {
        final PasswordHandler passwordHandler = new PasswordHandler(this.password);
        return passwordHandler.checkPassword(passwordToCheck);
    }

    /**
     * Gets google id.
     *
     * @return the google id
     */
    public String getGoogleId() {
        return this.googleId;
    }

    /**
     * Sets google id.
     *
     * @param googleId the google id
     */
    public void setGoogleId(final String googleId) {
        this.googleId = googleId;
    }

    /**
     * Gets facebook id.
     *
     * @return the facebook id
     */
    public String getFacebookId() {
        return this.facebookId;
    }

    /**
     * Sets facebook id.
     *
     * @param facebookId the facebook id
     */
    public void setFacebookId(final String facebookId) {
        this.facebookId = facebookId;
    }

    /**
     * Gets twitter id.
     *
     * @return the twitter id
     */
    public String getTwitterId() {
        return this.twitterId;
    }

    /**
     * Sets twitter id.
     *
     * @param twitterId the twitter id
     */
    public void setTwitterId(final String twitterId) {
        this.twitterId = twitterId;
    }

    /**
     * Gets active.
     *
     * @return the active
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(final Boolean active) {
        this.active = active;
    }

    /**
     * Gets last login.
     *
     * @return the last login
     */
    public DateTime getLastLogin() {
        return this.lastLogin;
    }

    /**
     * Sets last login.
     *
     * @param lastLogin the last login
     */
    public void setLastLogin(final DateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Gets roles.
     *
     * @return the roles
     */
    public List<RoleModel> getRoles() {
        return this.roles;
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public ParentModel getParent() {
        return this.parent;
    }

    /**
     * Sets parent.
     *
     * @param parent the parent
     */
    public void setParent(final ParentModel parent) {
        this.parent = parent;
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
     * Pre remove.
     */
    @PreRemove
    public void preRemove() {
        this.email = String.format("%s.rm%d", this.email, this.id);
        this.googleId = null;
        this.facebookId = null;
        this.twitterId = null;
    }

    /**
     * Check if the account has the given role.
     *
     * @param role the role
     * @return the boolean
     */
    public boolean hasRole(final Role role) {
        return RoleModel.find.query()
            .where()
            .eq("account", this)
            .eq("role", role)
            .setMaxRows(1)
            .findOne() != null;
    }

    /**
     * Has password boolean.
     *
     * @return the boolean
     */
    public boolean hasPassword() {
        return this.password != null && !this.password.isEmpty();
    }
}
