package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

/**
 *
 * @author Nils Leonhardt
 */
public class Contact extends DatabaseElement
{
    private String name;
    private String forename;
    private String fon;
    private String email;
    private String description;
    private String fax;
    private long address_id;
    private Long roleId;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param forename
     * @param fon
     * @param email
     * @param museum_id
     * @param description
     * @param fax
     * @param id
     * @param deleted
     * @param insert
     */
    public Contact(String name, String forename, String fon, String email, String description, String fax, long address_id, Long roleId,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.forename = forename;
        this.fon = fon;
        this.email = email;
        this.description = description;
        this.fax = fax;
        this.address_id = address_id;
        /** @author Christian Landel */
        if (roleId!=null && roleId==0L)
        	this.roleId=null;
        else
        	this.roleId=roleId;
    }

    public Contact(String name, String forename, String fon, String email, String description, String fax, long address_id, Long roleId)
    {
        this.name = name;
        this.forename = forename;
        this.fon = fon;
        this.email = email;
        this.description = description;
        this.fax = fax;
        this.address_id = address_id;
        /** @author Christian Landel */
        if (roleId!=null && roleId==0L)
        	this.roleId=null;
        else
        	this.roleId=roleId;
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
     * @return the forename
     */
    public String getForename()
    {
        return forename;
    }

    /**
     * @param forename the forename to set
     */
    public void setForename(String forename)
    {
        this.forename = forename;
    }

    /**
     * @return the fon
     */
    public String getFon()
    {
        return fon;
    }

    /**
     * @param fon the fon to set
     */
    public void setFon(String fon)
    {
        this.fon = fon;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
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
     * @return the fax
     */
    public String getFax()
    {
        return fax;
    }

    /**
     * @param fax the fax to set
     */
    public void setFax(String fax)
    {
        this.fax = fax;
    }

    public Long getRoleId()
    {
        return roleId;
    }

    public void setRoleId(Long roleId)
    {
        this.roleId = roleId;
    }

    public long getAddress_id()
    {
        return address_id;
    }

    public void setAddress_id(long adress_id)
    {
        this.address_id = adress_id;
    }

    @Override
    public String toString()
    {
        return "Contact{" + "name=" + name + ", forename=" + forename + ", fon=" + fon + ", email=" + email + ", description=" + description + ", fax=" + fax + ", roleId=" + roleId + '}';
    }
}
