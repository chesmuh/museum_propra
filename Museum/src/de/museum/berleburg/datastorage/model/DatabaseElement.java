package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.interfaces.Model;

/**
 *
 * @author Nils Leonhardt
 */
public class DatabaseElement implements Model
{
    private long id;
    private Timestamp deleted;
    private Timestamp insert;
    private Timestamp update;

    public DatabaseElement(long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        this.id = id;
        this.deleted = deleted;
        this.insert = insert;
        this.update = update;
    }

    /**
     * Creates a new DatabaseElement with id -1 and deleted null to store
     */
    public DatabaseElement()
    {
        this.id = -1L;
        this.deleted = null;
    }

    public Timestamp getUpdate()
    {
        return update;
    }

    public void setUpdate(Timestamp update)
    {
        this.update = update;
    }

    public Timestamp getDeleted()
    {
        return deleted;
    }

    public boolean isDeleted()
    {
        return deleted != null;
    }

    public void setDeleted(Timestamp deleted)
    {
        this.deleted = deleted;
    }

    @Override
    public Long getId()
    {
        return id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    public Timestamp getInsert()
    {
        return insert;
    }

    public void setInsert(Timestamp insert)
    {
        this.insert = insert;
    }
}
