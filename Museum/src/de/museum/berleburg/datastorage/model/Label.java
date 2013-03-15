package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Anselm
 */
public class Label extends DatabaseElement
{
    private String name;
    private List<Long> exhibit_ids;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param name
     * @param exhibit_ids
     * @param id
     * @param deleted
     * @param insert
     */
    public Label(String name, 
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.name = name;
        this.exhibit_ids = new ArrayList<>();
    }

    public Label(String name)
    {
        this.name = name;
        this.exhibit_ids = new ArrayList<>();;
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
     * @return the exhibit_ids
     */
    public List<Long> getExhibit_ids()
    {
        return exhibit_ids;
    }

    /**
     * @param exhibit_ids the exhibit_ids to set
     */
    public void setExhibit_ids(List<Long> exhibit_ids)
    {
        this.exhibit_ids = exhibit_ids;
    }

    /**
     * @param exhibit_id the exhibit id to add
     */
    public void addExhibit_id(Long exhibit_id)
    {
        this.exhibit_ids.add(exhibit_id);
    }

    /**
     * @param exhibit_id the exhibit id to remove
     */
    public void removeExhibit_id(Long exhibit_id)
    {
        this.exhibit_ids.remove(exhibit_id);
    }

    @Override
    public String toString()
    {
        return "Label{" + "name=" + name + ", exhibit_ids=" + exhibit_ids + '}';
    }

}
