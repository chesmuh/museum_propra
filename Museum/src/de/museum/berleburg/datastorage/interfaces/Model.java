package de.museum.berleburg.datastorage.interfaces;

/**
 * A Model to save into the database with keytype Long
 *
 * @author Anselm Brehme
 */
public interface Model
{

    /**
     * Returns the key of this model
     *
     * @return the id
     */
    public Long getId();

    /**
     * Sets the key of this model.
     *
     * @param id the id
     */
    public void setId(Long id);
}
