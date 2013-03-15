package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Nils Leonhardt
 */
public class Category extends DatabaseElement
{

    private String name;
    private long museum_id;
    private Long parent_id; // can be null

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param museum_id
     * @param parent_id
     * @param id
     * @param deleted
     * @param insert
     */
    public Category(String name, Long museum_id, Long parent_id,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.museum_id = museum_id;
        /** @author Christian Landel */
        if (parent_id!=null && parent_id==0L)
        	this.parent_id=null;
        else
        	this.parent_id=parent_id;
    }

    public Category(String name, long museum_id, Long parent_id)
    {
        this.name = name;
        this.museum_id = museum_id;
        /** @author Christian Landel */
        if (parent_id!=null && parent_id==0L)
        	this.parent_id=null;
        else
        	this.parent_id=parent_id;
    }

    public Museum getMuseum()
    {
        return DataAccess.getInstance().getMuseumById(getMuseum_id());
    }

    /**
     * 
     * @return parent Category
     */
    public Category getParent()
    {
        return this.parent_id == null ? null : DataAccess.getInstance().getCategoryById(this.parent_id);
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the museum_id
     */
    public long getMuseum_id()
    {
        return museum_id;
    }

    /**
     * @param museum_id the museum_id to set
     */
    public void setMuseum_id(long museum_id)
    {
        this.museum_id = museum_id;
    }

    /**
     * @return the parent_id
     */
    public Long getParent_id()
    {
        return parent_id;
    }

    /**
     * @param parent_id the parent_id to set
     */
    public void setParent_id(Long parent_id)
    {
        this.parent_id = parent_id;
    }

    @Override
    public String toString()
    {
        return "Category{" + "name=" + name + ", museum_id=" + museum_id + ", parent_id=" + parent_id + '}';
    }
}
