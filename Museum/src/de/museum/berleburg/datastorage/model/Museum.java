package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Nils Leonhardt
 */
public class Museum extends DatabaseElement
{

    private String name;
    private String description;
    private long address_id;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param description
     * @param address_id
     * @param id
     * @param deleted
     * @param insert
     */
    public Museum(String name, String description, long address_id, 
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.description = description;
        this.address_id = address_id;
    }

    public Museum(String name, String description, long address_id)
    {
        this.name = name;
        this.description = description;
        this.address_id = address_id;
    }

    public Address getAddress()
    {
        return DataAccess.getInstance().getAddressById(getAddress_id());
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
     * @return the address_id
     */
    public long getAddress_id()
    {
        return address_id;
    }

    /**
     * @param address_id the address_id to set
     */
    public void setAddress_id(long address_id)
    {
        this.address_id = address_id;
    }

    @Override
    public String toString()
    {
        return "Museum{" + "name=" + name + ", description=" + description + ", address_id=" + address_id + '}';
    }
}
