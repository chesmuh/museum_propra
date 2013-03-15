package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

/**
 *
 * @author Robert Straub
 */
public class Role extends DatabaseElement
{

    private String name;
    private long museum_id;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param id
     * @param museum_id
     * @param deleted
     * @param insert
     */
    public Role(String name, long museum_id, 
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.museum_id = museum_id;
    }

    public Role(String name ,long museum_id)
    {
        this.name = name;
        this.museum_id = museum_id;
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

    public long getMuseum_id()
    {
        return museum_id;
    }

    public void setMuseum_id(long museum_id)
    {
        this.museum_id = museum_id;
    }

    @Override
    public String toString()
    {
        return "Role{" + "name=" + name + ", museum_id=" + museum_id + '}';
    }
}
