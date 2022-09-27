package models;

import io.ebean.DB;
import io.ebean.Model;
import io.ebean.annotation.SoftDelete;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.PathBindable;

import javax.persistence.*;
import java.util.UUID;

/**
 * BaseModel.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 19.02.28
 */
@MappedSuperclass
public class BaseModel<T extends BaseModel<T>> extends Model implements PathBindable<T> {

    /**
     * The Logger.
     */
    protected final Logger.ALogger logger = Logger.of(this.getClass());

    /**
     * The class of T.
     */
    @Transient
    private final Class<T> tClass;

    /**
     * Entry ID.
     */
    @Id
    protected Long id;

    /**
     * The unique id of the model that can be exposed outside of the database to identify a resource.
     */
    @Column(name = "uid", nullable = false, unique = true)
    protected UUID uid;

    /**
     * Entry creation datetime.
     */
    @Column(name = "created_at", nullable = false)
    protected DateTime createdAt;

    /**
     * Entry last update datetime.
     */
    @Column(name = "last_update", nullable = false)
    protected DateTime lastUpdate;

    /**
     * Soft delete flag.
     */
    @SoftDelete
    @Column(name = "deleted", columnDefinition = "BOOLEAN DEFAULT FALSE")
    protected boolean deleted;

    /**
     * Default constructor.
     *
     * @param tClass the t class
     */
    protected BaseModel(final Class<T> tClass) {
        this.tClass = tClass;
        this.uid = UUID.randomUUID();
        this.createdAt = DateTime.now();
        this.lastUpdate = this.createdAt;
    }

    /**
     * Get the entry ID.
     *
     * @return The entry ID as {@code Long}
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public UUID getUid() {
        return this.uid;
    }

    /**
     * Get the entry creation datetime.
     *
     * @return The creation datetime
     */
    public DateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * Get the entry last update datetime.
     *
     * @return The last update datetime
     */
    public DateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Is deleted boolean.
     *
     * @return the boolean
     */
    public boolean isDeleted() {
        return this.deleted;
    }

    /**
     * Sets deleted.
     *
     * @param deleted the deleted
     */
    public void setDeleted(final boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Update the last update field to the current datetime.
     */
    @PreUpdate
    public void updateLastUpdate() {
        this.lastUpdate = DateTime.now();
    }

    @Override
    public T bind(final String key, final String txt) {
        final T obj;
        try {
            if (this.tClass == null) {
                // Need to instantiate a new logger as at this point the logger of the class is most likely null.
                final Logger.ALogger log = Logger.of(BaseModel.class);
                log.error("tClass is missing ! Did you forgot to create a default constructor in your model ?");
                throw new RuntimeException("Missing tClass, unable to bind the model !");
            }
            obj = DB.find(this.tClass).where().eq("uid", UUID.fromString(txt)).setMaxRows(1).findOne();
            if (obj == null) {
                throw new RuntimeException("404");
            }
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException("404");
        }
        return obj;
    }

    @Override
    public String unbind(final String key) {
        return this.uid.toString();
    }

    @Override
    public String javascriptUnbind() {
        return "function(k, v) {\n"
            + "    return v.uid;"
            + "}";
    }
}
