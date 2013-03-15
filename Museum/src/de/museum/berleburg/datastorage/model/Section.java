package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Nils Leonhardt
 */
public class Section extends DatabaseElement
{
    private String name;
    private String description;
    private Long parent_id;
    private long museum_id;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param description
     * @param parent_id
     * @param museum_id
     * @param id
     * @param deleted
     * @param insert
     */
    public Section(String name, String description, Long parent_id, long museum_id,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.description = description;
        if (parent_id!=null && parent_id.equals(0L))
        	this.parent_id=null;
        else
        	this.parent_id=parent_id;
        this.museum_id = 0 == museum_id ? null : museum_id;
    }

    public Section(String name, String description, Long parent_id, long museum_id)
    {
        this.name = name;
        this.description = description;
        this.parent_id = parent_id;
        this.museum_id = museum_id;
    }

    public Section getParent()
    {
        return this.parent_id == null ? null : DataAccess.getInstance().getSectionById(this.parent_id);
    }

    public Museum getMuseum()
    {
        return DataAccess.getInstance().getMuseumById(this.museum_id);
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
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
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

    @Override
    public String toString()
    {
        return "Section{" + "name=" + name + ", description=" + description + ", parent_id=" + parent_id + ", museum_id=" + museum_id + '}';
    }
}
