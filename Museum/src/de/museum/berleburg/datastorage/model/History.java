package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Anselm
 */
public class History extends DatabaseElement
{
    private long exhibit_id;
    private String name;
    private String description;
    private Long section_id;
    private Long category_id;
    private long count;
    private String rfid;
    private Long museum_id;
    private Timestamp startdate;
    private double wert;
    private Long outsourced_id;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param startdate
     * @param exhibit_id
     * @param museum_id
     * @param section_id
     * @param outsourced_id
     * @param wert
     */
    public History(long exhibit_id, String name, String description, Long section_id, Long category_id, long count, String rfid, Long museum_id, double wert, Long outsourced_id, Timestamp startdate, 
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.exhibit_id = exhibit_id;
        this.name = name;
        this.description = description;
        /** @author Christian Landel */
        if (section_id!=null && section_id.equals(0L))
        	this.section_id=null;
        else
        	this.section_id = section_id;
        /** @author Christian Landel */
        if (category_id!=null && category_id.equals(0L))
        	category_id=null;
        else
        	this.category_id = category_id;
        this.count = count;
        this.rfid = rfid;
        this.museum_id = museum_id;
        this.startdate = startdate;
        this.wert = wert;
        
        this.outsourced_id = outsourced_id;
    }

    public History(long exhibit_id, String name, String description, Long section_id, Long category_id, long count, String rfid, Long museum_id, double wert, Long outsourced_id,
            Timestamp startdate, Timestamp insert, Timestamp deleted, Timestamp update)
    {
        super(-1, deleted, insert, update);
        this.exhibit_id = exhibit_id;
        this.name = name;
        this.description = description;
        /** @author Christian Landel */
        if (section_id!=null && section_id.equals(0L))
        	this.section_id=null;
        else
        	this.section_id = section_id;
        /** @author Christian Landel */
        if (category_id!=null && category_id.equals(0L))
        	category_id=null;
        else
        	this.category_id = category_id;
        this.count = count;
        this.rfid = rfid;
        this.museum_id = museum_id;
        this.startdate = startdate;
        this.wert = wert;
        
        this.outsourced_id = outsourced_id;
    }

    /**
     * @return the exhibit_id
     */
    public long getExhibit_id()
    {
        return exhibit_id;
    }

    /**
     * @param exhibit_id the exhibit_id to set
     */
    public void setExhibit_id(long exhibit_id)
    {
        this.exhibit_id = exhibit_id;
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
     * @return the section_id
     */
    public Long getSection_id()
    {
        return section_id;
    }

    /**
     * @param section_id the section_id to set
     */
    public void setSection_id(Long section_id)
    {
        this.section_id = section_id;
    }

    /**
     * @return the category_id
     */
    public Long getCategory_id()
    {
        return category_id;
    }

    /**
     * @param category_id the category_id to set
     */
    public void setCategory_id(Long category_id)
    {
        this.category_id = category_id;
    }

    /**
     * @return the count
     */
    public long getCount()
    {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(long count)
    {
        this.count = count;
    }

    /**
     * @return the rfid
     */
    public String getRfid()
    {
        return rfid;
    }

    /**
     * @param rfid the rfid to set
     */
    public void setRfid(String rfid)
    {
        this.rfid = rfid;
    }

    /**
     * @return the museum_id
     */
    public Long getMuseum_id()
    {
        return museum_id;
    }

    /**
     * @param museum_id the museum_id to set
     */
    public void setMuseum_id(Long museum_id)
    {
        this.museum_id = museum_id;
    }

    /**
     * @return the startdate
     */
    public Timestamp getStartdate()
    {
        return startdate;
    }

    /**
     * @param startdate the startdate to set
     */
    public void setStartdate(Timestamp startdate)
    {
        this.startdate = startdate;
    }

    public double getWert()
    {
        return wert;
    }

    public void setWert(double wert)
    {
        this.wert = wert;
    }

    public Long getOutsourced_id()
    {
        return outsourced_id;
    }
    
    public Outsourced getOutsourced()
    {
    	return DataAccess.getInstance().getOutsourcedById(outsourced_id);
    }

    public void setOutsourced_id(Long outsourced_id)
    {
        this.outsourced_id = outsourced_id;
    }
}
