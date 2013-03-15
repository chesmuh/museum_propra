package de.museum.berleburg.datastorage;


/**
 * System - Constants can be saved here.
 * 
 * @author Nils Leonhardt
 *
 */
public class Constants {
	private Constants() {
		
	}

    public static final int UPDATE_MANAGER_ID = 0;
    public static final int UPDATE_MANAGER_ID_IMAGE = 15;
    public static final int ADRESS_MANAGER_ID = 1;
    public static final int CATEGORY_MANAGER_ID = 2;
    public static final int CONTACT_MANAGER_ID = 3;
    public static final int EXHIBIT_MANAGER_ID = 4;
    public static final int IMAGE_MANAGER_ID = 5;
    public static final int LABEL_MANAGER_ID = 6;
    public static final int MUSEUM_MANAGER_ID = 7;
    public static final int OUTSOURCED_MANAGER_ID = 8;
    public static final int ROLE_MANAGER_ID = 9;
    public static final int SECTION_MANAGER_ID = 10;
    public static final int HISTORY_MANAGER_ID = 11;
    public static final int BACKUP_INSERT = 12;
    public static final int BACKUP_EXPORT = 13;
    	
	// PATH
	public static final String LOGGER_PATH = "log//database.log";
	public static final String CONFIGURATION_PATH_LOCAL = "data//localdatabase.xml";
        public static final String CONFIGURATION_PATH_SERVER = "data//serverdatabase.xml";
	public static final String IMAGES_PATH = "data//images";
	// DATABASE-CONNECTION
	public static final String DEFAULT_DATABASE_NAME = "ppws12_1";
	public static final String DEFAULT_DATABASE_HOST = "hauteuchdrum.informatik.uni-siegen.de";
	public static final String DEFAULT_DATABASE_PORT = "3306";
	public static final String DEFAULT_DATABASE_PASSWORD = "ppws-1904";
	public static final String DEFAULT_DATABASE_USERNAME = "ppws12_1";
	// DATABASE-TYPE-LENGTH
	public static final int LENGTH_EXHIBIT_NAME = 100;
	public static final int LENGTH_EXHIBIT_RFID = 384;
	public static final int LENGTH_CATEGORY_NAME = 50;
	public static final int LENGTH_SECTION_NAME = 50;
	public static final int LENGTH_ADDRESS_STREET = 50;
	public static final int LENGTH_ADDRESS_HOUSENUMBER = 6;
	public static final int LENGTH_ADDRESS_ZIPCODE = 10;
	public static final int LENGTH_ADDRESS_TOWN = 50;
	public static final int LENGTH_ADDRESS_STATE = 40;
	public static final int LENGTH_ADDRESS_COUNTRY = 60;
	public static final int LENGTH_MUSEUM_NAME = 50;
	public static final int LENGTH_DESTINATION_NAME = 50;
	public static final int LENGTH_CONTACT_NAME = 100;
	public static final int LENGTH_CONTACT_FORENAME = 100;
	public static final int LENGTH_CONTACT_FON = 25;
	public static final int LENGTH_CONTACT_EMAIL = 100;
    public static final int LENGTH_CONTACT_FAX = 100;
	public static final int LENGTH_IMAGES_NAME = 50;
	public static final int LENGTH_LABEL_NAME = 32;	
        public static final int LENGTH_ROLE_NAME = 32;	
	// DATABASE-DEFAULT-VALUES
	// Add '<VALUE>' for <Values> != NULL
	public static final int 	DEFAULT_EXHIBIT_COUNT = 1;
	public static final String 	DEFAULT_EXHIBIT_LABEL = "'nolabel'"; 
	public static final String 	DEFAULT_EXHIBIT_RFID = "NULL";
	public static final String 	DEFAULT_SECTION_PARENT = "NULL";
	public static final String 	DEFAULT_DESCRIPTION_ENDDATE = "NULL";
    public static String 		DEFAULT_EXHIBIT_OUTSOURCED_ID = "NULL";
	public static final long 	DEFAULT_MODEL_ID = -1L;
        
	public static final String 	DEFAULT_FIELD_INSERTED = "CURRENT_TIMESTAMP";
	public static final boolean DEMO_VERSION = false;
}
