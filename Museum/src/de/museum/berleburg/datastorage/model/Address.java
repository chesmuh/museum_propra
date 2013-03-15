package de.museum.berleburg.datastorage.model;

import java.sql.Timestamp;

/**
 *
 * @author Nils Leonhardt
 */
public class Address extends DatabaseElement
{
    private String street;
    private String housenumber;
    private String zipcode;
    private String town;
    private String state;
    private String country;

    /**
     * Database constructor. Do not use unless you are a database!
     *
     * @param street
     * @param housenumber
     * @param zipcode
     * @param town
     * @param state
     * @param country
     * @param id
     * @param deleted
     * @param insert
     */
    public Address(String street, String housenumber, String zipcode,
            String town, String state, String country,
            long id, Timestamp deleted, Timestamp insert, Timestamp update)
    {
        super(id, deleted, insert, update);
        this.street = street;
        this.housenumber = housenumber;
        this.zipcode = zipcode;
        this.town = town;
        this.state = state;
        this.country = country;
    }

    public Address(String street, String housenumber, String zipcode, String town, String state, String country)
    {
        this.street = street;
        this.housenumber = housenumber;
        this.zipcode = zipcode;
        this.town = town;
        this.state = state;
        this.country = country;
    }

    /**
     * @return the street
     */
    public String getStreet()
    {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street)
    {
        this.street = street;
    }

    /**
     * @return the housenumber
     */
    public String getHousenumber()
    {
        return housenumber;
    }

    /**
     * @param housenumber the housenumber to set
     */
    public void setHousenumber(String housenumber)
    {
        this.housenumber = housenumber;
    }

    /**
     * @return the zipcode
     */
    public String getZipcode()
    {
        return zipcode;
    }

    /**
     * @param zipcode the zipcode to set
     */
    public void setZipcode(String zipcode)
    {
        this.zipcode = zipcode;
    }

    /**
     * @return the town
     */
    public String getTown()
    {
        return town;
    }

    /**
     * @param town the town to set
     */
    public void setTown(String town)
    {
        this.town = town;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }

    @Override
    public String toString()
    {
        return "Address{" + "street=" + street + ", housenumber=" + housenumber + ", zipcode=" + zipcode + ", town=" + town + ", state=" + state + ", country=" + country + '}';
    }
}
