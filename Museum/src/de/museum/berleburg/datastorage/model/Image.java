package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

import de.museum.berleburg.datastorage.DataAccess;

public class Image extends DatabaseElement
{
    private byte[] image;
    private String name;
    private long exhibit_id;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param image
     * @param name
     * @param exhibit_id
     * @param id
     * @param deleted
     * @param insert
     */
    public Image(byte[] image, String name, long exhibit_id,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.image = image;
        this.name = name;
        this.exhibit_id = exhibit_id;
    }

    public Image(String name, long exhibit_id,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.image = new byte[0];
        this.name = name;
        this.exhibit_id = exhibit_id;
    }

    public Image(byte[] image, String name, long exhibit_id)
    {
        this.image = image;
        this.name = name;
        this.exhibit_id = exhibit_id;
    }

    public Exhibit getExhibit()
    {
        return DataAccess.getInstance().getExhibitById(getExhibit_id());
    }

    /**
     * @return the image
     */
    public byte[] getRawImage()
    {
        return image;
    }

    /**
     * @param image the image to set
     */
    public void setRawImage(byte[] image)
    {
        this.image = image;
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

    @Override
    public String toString()
    {
        return "Image{" + "image=" + image + ", name=" + name + ", exhibit_id=" + exhibit_id + '}';
    }
}
