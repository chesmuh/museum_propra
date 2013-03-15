package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Nils Leonhardt
 */
public class Outsourced extends DatabaseElement
{

    private String name; // can be null
    private String description; // can be null
    private Date startDate;
    private Date endDate;
    private Long address_id; // can be null
    private Long contact_id; // can be null
    private Long museum_id;
    private HashMap<Long, Timestamp> exhibitIds;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param description
     * @param startDate
     * @param endDate
     * @param address_id
     * @param contact_id
     * @param id
     * @param deleted
     */
    public Outsourced(String name, String description, Date startDate,
            Date endDate, Long address_id, Long contact_id, Long museum_id,
            HashMap<Long, Timestamp> exhibitIds, long id, Timestamp deleted,
            Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        /**
         * @author Christian Landel
         */
        if (address_id != null && address_id.equals(0L))
        {
            this.address_id = null;
        }
        else
        {
            this.address_id = address_id;
        }
        /**
         * @author Christian Landel
         */
        if (contact_id != null && contact_id.equals(0L))
        {
            this.contact_id = null;
        }
        else
        {
            this.contact_id = contact_id;
        }
        this.museum_id = museum_id;

        this.exhibitIds = exhibitIds;
    }

    public Outsourced(String name, String description, Date startDate,
            Date endDate, Long address_id, Long contact_id, Long museum_id)
    {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        if (address_id != null)
        {
            this.address_id = 0 == address_id ? null : address_id;
        }
        else
        {
            this.address_id = null;
        }

        if (contact_id != null)
        {
            this.contact_id = 0 == contact_id ? null : contact_id;
        }
        else
        {
            this.contact_id = null;
        }
        this.museum_id = museum_id;

        this.exhibitIds = new HashMap<Long, Timestamp>();
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
     * @return the startDate
     */
    public Date getStartDate()
    {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate()
    {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    /**
     * @return the Address_id
     */
    public Long getAddress_id()
    {
        return address_id;
    }

    /**
     * @param Address_id the Address_id to set
     */
    public void setAddress_id(Long Address_id)
    {
        this.address_id = Address_id;
    }

    /**
     * @return the contact_id
     */
    public Long getContact_id()
    {
        return contact_id;
    }

    /**
     * @param contact_id the contact_id to set
     */
    public void setContact_id(Long contact_id)
    {
        this.contact_id = contact_id;
    }

    public void setMuseum_id(Long museum_id)
    {
        this.museum_id = museum_id;
    }

    public Long getMuseum_id()
    {
        return museum_id;
    }

    @Override
    public String toString()
    {
        return "Outsourced{" + "name=" + name + ", description=" + description
                + ", startDate=" + startDate + ", endDate=" + endDate
                + ", Address_id=" + address_id + ", contact_id=" + contact_id
                + '}';
    }

    // added by Tim Wesener
    public void addExhibit(Long key, Timestamp value)
    {
        if (key == null)
        {
            return;
        }
        System.out.print("Outsourced added Exhibit!");
        this.exhibitIds.put(key, value);
    }

    public HashMap<Long, Timestamp> getExhibitIds()
    {
        return exhibitIds;
    }

    public boolean allBack()
    {

        for (Timestamp time : this.exhibitIds.values())
        {
            if (time == null)
            {
                return false;
            }
        }

        return true;
    }

    public boolean contains(Exhibit e)
    {
        return this.exhibitIds.containsKey(e.getId());
    }

    public Timestamp givenBack(long exhibit_id)
    {
        if (this.getExhibitIds().containsKey(exhibit_id))
        {
            return this.getExhibitIds().get(exhibit_id);
        }
        return null;
    }

    public boolean isLoan()
    {
        if (this.contact_id != null)
        {
            return true;
        }
        return false;
    }
}
