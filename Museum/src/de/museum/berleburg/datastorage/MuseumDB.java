package de.museum.berleburg.datastorage;

/**
 * Constants for Database Access.
 *
 * @author Nils Leonhardt, Robert Straub, Anselm Brehme
 *
 */
public class MuseumDB
{

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `%s` (\n";
    private static final String TYPE_PRIMARYKEY = " INT UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT";
    private static final String TYPE_VARCHAR = " VARCHAR(%d)";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INT_UNSIGNED = " INT UNSIGNED";
    private static final String TYPE_VARBINARY = " VARBINARY(%d)";
    private static final String TYPE_TIMESTAMP = " TIMESTAMP";
    private static final String TYPE_MEDIUMBLOB = " MEDIUMBLOB";
    private static final String TYPE_REAL = " REAL";
    private static final String EXTENSION_REFERENCES = " REFERENCES %s (%s)";
    private static final String EXTENSION_NOTNULL = " NOT NULL";
    private static final String EXTENSION_NULL = " NULL";
    private static final String EXTENSION_DEFAULT = " DEFAULT %s";
    private static final String NEXTFIELD = ",\n";
    private static final String FINISHFIELDS = ");";
    public static final String CREATE_DATABASE_LOCAL = "CREATE DATABASE IF NOT EXISTS "
            + Configuration.getInstance().getLocalDatabase().getDatabaseName() + ";";
    public static final String CREATE_DATABASE_BACKUP = "CREATE DATABASE IF NOT EXISTS "
            + Configuration.getInstance().getServerDatabase().getDatabaseName() + ";";

    //TODO create database on startUp if not found!
    public static String references(String tableName, String fieldName)
    {
        return String.format(EXTENSION_REFERENCES, tableName, fieldName);
    }

    public static String defValue(Object defValue)
    {
        return String.format(EXTENSION_DEFAULT, defValue);
    }

    public static String varchar(int length)
    {
        return String.format(TYPE_VARCHAR, length);
    }

    public static String varbinary(int length)
    {
        return String.format(TYPE_VARBINARY, length);
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Exhibit
    {

        private Exhibit()
        {
        }
        public static final String TABLE_NAME = "exhibit";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SECTION_ID = "section_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String COUNT = "count";
        public static final String RFID = "rfid";
        public static final String MUSEUM_ID = "museum_id";
        public static final String WERT = "wert";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_EXHIBIT_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NOTNULL)
                    .append(NEXTFIELD).append(SECTION_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Section.TABLE_NAME, Section.ID))
                    .append(NEXTFIELD).append(CATEGORY_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Category.TABLE_NAME, Category.ID))
                    .append(NEXTFIELD).append(COUNT + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_EXHIBIT_COUNT))
                    .append(NEXTFIELD).append(RFID)
                    .append(varbinary(Constants.LENGTH_EXHIBIT_RFID))
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD)
                    .append(WERT + TYPE_REAL).append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Category
    {

        private Category()
        {
        }
        public static final String TABLE_NAME = "category";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String MUSEUM_ID = "museum_id";
        public static final String PARENT_ID = "parent_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_CATEGORY_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD).append(PARENT_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL).append(references(TABLE_NAME, ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Section
    {

        private Section()
        {
        }
        public static final String TABLE_NAME = "section";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String PARENT_ID = "parent_id";
        public static final String MUSEUM_ID = "museum_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_SECTION_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NOTNULL)
                    .append(NEXTFIELD).append(PARENT_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL).append(references(TABLE_NAME, ID))
                    .append(NEXTFIELD).append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Address
    {

        private Address()
        {
        }
        public static final String TABLE_NAME = "address";
        public static final String ID = "id";
        public static final String STREET = "street";
        public static final String HOUSENUMBER = "housenumber";
        public static final String ZIPCODE = "zipcode";
        public static final String TOWN = "town";
        public static final String STATE = "state";
        public static final String COUNTRY = "country";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(STREET)
                    .append(varchar(Constants.LENGTH_ADDRESS_STREET))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(HOUSENUMBER)
                    .append(varchar(Constants.LENGTH_ADDRESS_HOUSENUMBER))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(ZIPCODE)
                    .append(varchar(Constants.LENGTH_ADDRESS_ZIPCODE))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD).append(TOWN)
                    .append(varchar(Constants.LENGTH_ADDRESS_TOWN))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD).append(STATE)
                    .append(varchar(Constants.LENGTH_ADDRESS_STATE))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(COUNTRY)
                    .append(varchar(Constants.LENGTH_ADDRESS_COUNTRY))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DELETED + TYPE_TIMESTAMP).append(EXTENSION_NULL)
                    .append(NEXTFIELD).append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt Hier noch Exhibit einbauen
     *
     */
    public static class History
    {

        private History()
        {
        }
        public static final String TABLE_NAME = "history";
        public static final String ID = "id";
        public static final String EXHIBIT_ID = "exhibit_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String SECTION_ID = "section_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String COUNT = "count";
        public static final String RFID = "rfid";
        public static final String MUSEUM_ID = "museum_id";
        public static final String WERT = "wert";
        public static final String OUTSOURCED_ID = "outsourced_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String STARTDATE = "startdate";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(EXHIBIT_ID + TYPE_INT_UNSIGNED).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_EXHIBIT_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NOTNULL)
                    .append(NEXTFIELD).append(SECTION_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Section.TABLE_NAME, Section.ID))
                    .append(NEXTFIELD).append(CATEGORY_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Category.TABLE_NAME, Category.ID))
                    .append(NEXTFIELD).append(COUNT + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_EXHIBIT_COUNT))
                    .append(NEXTFIELD).append(RFID)
                    .append(varbinary(Constants.LENGTH_EXHIBIT_RFID))
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD)
                    .append(WERT + TYPE_REAL).append(EXTENSION_NOTNULL)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(OUTSOURCED_ID).append(TYPE_INT_UNSIGNED)
                    .append(references(Outsourced.TABLE_NAME, Outsourced.ID))
                    .append(NEXTFIELD)
                    .append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD).append(STARTDATE + TYPE_TIMESTAMP)
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Museum
    {

        private Museum()
        {
        }
        public static final String TABLE_NAME = "museum";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ADDRESS_ID = "address_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME).append(varchar(Constants.LENGTH_MUSEUM_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NOTNULL)
                    .append(NEXTFIELD).append(ADDRESS_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Address.TABLE_NAME, Address.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Outsourced
    {

        private Outsourced()
        {
        }
        public static final String TABLE_NAME = "outsourced";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String STARTDATE = "startDate";
        public static final String ENDDATE = "endDate";
        public static final String ADDRESS_ID = "address_id";
        public static final String CONTACT_ID = "contact_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String MUSEUM_ID = "museum_id";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_DESTINATION_NAME))
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NULL)
                    .append(NEXTFIELD).append(STARTDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(ENDDATE + TYPE_TIMESTAMP).append(EXTENSION_NULL)
                    .append(NEXTFIELD).append(ADDRESS_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Address.TABLE_NAME, Address.ID))
                    .append(NEXTFIELD).append(CONTACT_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Contact.TABLE_NAME, Contact.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD).append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString(), OutsourcedExhibits.getCreateTableSQL()
                    };
        }
    }

    public static class OutsourcedExhibits
    {

        private OutsourcedExhibits()
        {
        }
        public static final String TABLE_NAME = "outsourced_exhibits";
        public static final String OUTSOURCED_ID = "outsourced_id";
        public static final String EXHIBIT_ID = "exhibit_id";
        public static final String GIVENBACK = "givenback";

        public static String getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(
                    String.format(CREATE_TABLE, TABLE_NAME))
                    .append(OUTSOURCED_ID + TYPE_INT_UNSIGNED).append(EXTENSION_NULL)
                    .append(references(Outsourced.TABLE_NAME, Outsourced.ID))
                    .append(NEXTFIELD)
                    .append(EXHIBIT_ID + TYPE_INT_UNSIGNED).append(EXTENSION_NULL)
                    .append(references(Exhibit.TABLE_NAME, Exhibit.ID))
                    .append(NEXTFIELD)
                    .append(GIVENBACK + TYPE_TIMESTAMP).append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return sb.toString();
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Contact
    {

        private Contact()
        {
        }
        public static final String TABLE_NAME = "contact";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String FORNAME = "forename";
        public static final String FON = "fon";
        public static final String EMAIL = "email";
        public static final String DESCRIPTION = "description";
        public static final String FAX = "fax";
        public static final String ADDRESS_ID = "address_id";
        public static final String ROLE_ID = "role_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME)
                    .append(varchar(Constants.LENGTH_CONTACT_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(FORNAME)
                    .append(varchar(Constants.LENGTH_CONTACT_FORENAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD).append(FON)
                    .append(varchar(Constants.LENGTH_CONTACT_FON))
                    .append(EXTENSION_NULL).append(NEXTFIELD).append(EMAIL)
                    .append(varchar(Constants.LENGTH_CONTACT_EMAIL))
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(DESCRIPTION + TYPE_TEXT).append(EXTENSION_NULL)
                    .append(NEXTFIELD).append(FAX)
                    .append(varchar(Constants.LENGTH_CONTACT_FAX))
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(ADDRESS_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(Address.TABLE_NAME, Address.ID))
                    .append(NEXTFIELD).append(ROLE_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Role.TABLE_NAME, Role.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Images
    {

        public static final String TABLE_NAME = "images";
        public static final String ID = "id";
        public static final String EXHIBIT_ID = "exhibit_id";
        public static final String IMAGE = "image";
        public static final String NAME = "name";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(IMAGE + TYPE_MEDIUMBLOB).append(EXTENSION_NOTNULL)
                    .append(NEXTFIELD).append(NAME)
                    .append(varchar(Constants.LENGTH_IMAGES_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(EXHIBIT_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Exhibit.TABLE_NAME, Exhibit.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(" INDEX (").append(EXHIBIT_ID).append(")")
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    /**
     *
     * @author Nils Leonhardt, Robert Straub
     *
     */
    public static class Label
    {

        public static final String TABLE_NAME = "label";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME).append(varchar(Constants.LENGTH_LABEL_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(DELETED + TYPE_TIMESTAMP).append(EXTENSION_NULL)
                    .append(NEXTFIELD).append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString(), ExhibitLabel.getCreateTableSQL()
                    };
        }
    }

    /**
     *
     * @author Robert Straub
     *
     */
    public static class Role
    {

        public static final String TABLE_NAME = "role";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String MUSEUM_ID = "museum_id";
        public static final String DELETED = "deleted";
        public static final String INSERTED = "inserted";
        public static final String UPDATE = "updated";

        public static String[] getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME)).append(ID + TYPE_PRIMARYKEY).append(NEXTFIELD)
                    .append(NAME).append(varchar(Constants.LENGTH_ROLE_NAME))
                    .append(EXTENSION_NOTNULL).append(NEXTFIELD)
                    .append(MUSEUM_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NULL)
                    .append(references(Museum.TABLE_NAME, Museum.ID))
                    .append(NEXTFIELD).append(DELETED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL).append(NEXTFIELD)
                    .append(INSERTED + TYPE_TIMESTAMP)
                    .append(EXTENSION_NOTNULL)
                    .append(defValue(Constants.DEFAULT_FIELD_INSERTED))
                    .append(NEXTFIELD)
                    .append(UPDATE + TYPE_TIMESTAMP)
                    .append(EXTENSION_NULL)
                    .append(FINISHFIELDS);
            return new String[]
                    {
                        sb.toString()
                    };
        }
    }

    public static class ExhibitLabel
    {

        public static final String TABLE_NAME = "exhibit_label";
        public static final String EXHIBIT_ID = "exhibit_id";
        public static final String LABEL_ID = "label_id";

        public static String getCreateTableSQL()
        {
            StringBuilder sb = new StringBuilder(String.format(CREATE_TABLE,
                    TABLE_NAME))
                    .append(EXHIBIT_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(MuseumDB.Exhibit.TABLE_NAME,
                    MuseumDB.Exhibit.ID))
                    .append(NEXTFIELD)
                    .append(LABEL_ID + TYPE_INT_UNSIGNED)
                    .append(EXTENSION_NOTNULL)
                    .append(references(MuseumDB.Label.TABLE_NAME,
                    MuseumDB.Label.ID))
                    .append(NEXTFIELD)
                    .append("primary key(" + EXHIBIT_ID + ", " + LABEL_ID + ")")
                    .append(FINISHFIELDS);
            return sb.toString();
        }
    }
}
