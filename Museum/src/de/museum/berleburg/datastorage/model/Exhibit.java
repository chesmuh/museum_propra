package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;

/**
 *
 * @author Nils Leonhardt
 */
public class Exhibit extends DatabaseElement
{

    private String name;
    private String description;
    private Long section_id;
    private Long category_id;
    private long count;
    private String rfid;
    private long museum_id;
    private double wert;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param description
     * @param section_id
     * @param category_id
     * @param count
     * @param rfid
     * @param museum_id
     * @param outsourced_id
     * @param id
     * @param deleted
     * @param insert
     */
    public Exhibit(String name, String description, Long section_id, Long category_id, long count, String rfid, long museum_id, double wert,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.description = description;
        /**
         * @author Christian Landel
         */
        if (section_id != null && section_id.equals(0L))
        {
            this.section_id = null;
        }
        else
        {
            this.section_id = section_id;
        }
        /**
         * @author Christian Landel
         */
        if (category_id != null && category_id.equals(0L))
        {
            category_id = null;
        }
        else
        {
            this.category_id = category_id;
        }
        this.count = count;
        this.rfid = rfid;
        this.museum_id = museum_id;
        this.wert = wert;
    }

    public Exhibit(String name, String description, Long section_id, Long category_id, long count, String rfid, long museum_id, double wert)
    {
        this.name = name;
        this.description = description;
        /**
         * @author Christian Landel
         */
        if (section_id != null && section_id.equals(0L))
        {
            this.section_id = null;
        }
        else
        {
            this.section_id = section_id;
        }
        /**
         * @author Christian Landel
         */
        if (category_id != null && category_id.equals(0L))
        {
            category_id = null;
        }
        else
        {
            this.category_id = category_id;
        }
        this.count = count;
        this.rfid = rfid;
        this.museum_id = museum_id;
        this.wert = wert;
    }

    public Section getSection()
    {
        return DataAccess.getInstance().getSectionById(getSection_id());
    }

    public Category getCategory()
    {
        return DataAccess.getInstance().getCategoryById(getCategory_id());
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
        return "Exhibit{" + "name=" + name + ", description=" + description + ", section_id=" + section_id + ", category_id=" + category_id + ", count=" + count + ", rfid=" + rfid + ", museum_id=" + museum_id + "}";
    }

    public Collection<Label> getLabels()
    {
        return DataAccess.getInstance().searchLabelsByExhibitId(this.getId());
    }

    public double getWert()
    {
        return wert;
    }

    public void setWert(double wert)
    {
        this.wert = wert;
    }
    
    public Collection<Outsourced> getOutsourced()
    {
        HashSet<Outsourced> found = new HashSet<>();
        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
        {
            if (outsourced.getExhibitIds().containsKey(this.getId()))
            {
                found.add(outsourced);
            }
        }
        return found;
    }
    
    public boolean isOutsourced()
    {
        return !this.getOutsourced().isEmpty();
    }
    
    public Outsourced getCurrentOutsourced() {
    	for(Outsourced outsourced : this.getOutsourced()) {
    		if(null == outsourced.givenBack(this.getId())) {
    			return outsourced;
    		}
    	}
    	
    	return null;
    }
}
