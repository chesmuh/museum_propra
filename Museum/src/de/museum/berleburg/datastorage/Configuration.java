package de.museum.berleburg.datastorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionFailedException;

/**
 * Global Configuration File. Can be saved to XML.
 *
 * @author Nils Leonhardt, Anselm Brehme
 *
 */
public class Configuration
{
    // Thread-Save.

    private static Configuration instance = new Configuration();
    @XStreamOmitField
    private Connection connection = null;
    @XStreamOmitField
    private Connection serverConnections = null;
    private Database localdatabase = new Database();
    private Database serverDatabase = new Database();
    private boolean debugMode = false;

    private Configuration()
    {
    }

    public static Configuration getInstance()
    {
        return instance;
    }

    public void overrideConfiguration(Configuration newConfiguration)
    {
        instance = newConfiguration;
    }

    public Database getLocalDatabase()
    {
        return this.localdatabase;
    }

    public Database getServerDatabase()
    {
        return this.serverDatabase;
    }

    public void setDebugMode(boolean b)
    {
        this.debugMode = b;
    }

    public boolean isDebugMode()
    {
        return this.debugMode;
    }

    public Connection getConnection() throws ConnectionException
    {

        if (this.connection == null)
        {
            try
            {
                String uri = "jdbc:mysql://" + this.localdatabase.host + ":"
                        + this.localdatabase.port + "/" + localdatabase.databaseName
                        + "?user=" + this.localdatabase.username + "&password="
                        + this.localdatabase.password;

                this.connection = DriverManager.getConnection(uri);
            }
            catch (SQLException ex)
            {
            	throw new ConnectionFailedException("Failed to establish connection!", ex);
            }
        }
        return this.connection;
    }

    public Connection getServerConnection() throws ConnectionFailedException
    {
        try
        {
            if (this.serverConnections == null)
            {
                if (serverDatabase.databaseName.equals(""))
                {
                    return null;
                }
                String uri = "jdbc:mysql://" + this.serverDatabase.host + ":"
                        + this.serverDatabase.port + "/"
                        + serverDatabase.databaseName + "?user="
                        + this.serverDatabase.username + "&password="
                        + this.serverDatabase.password;
                this.serverConnections = DriverManager.getConnection(uri);
            }
            return this.serverConnections;
        }
        catch (SQLException ex)
        {
            throw new ConnectionFailedException("Failed to establish connection!", ex);
        }
    }

    public void setDefault()
    {
        this.localdatabase.databaseName = Constants.DEFAULT_DATABASE_NAME;
        this.localdatabase.host = Constants.DEFAULT_DATABASE_HOST;
        this.localdatabase.port = Constants.DEFAULT_DATABASE_PORT;
        this.localdatabase.password = Constants.DEFAULT_DATABASE_PASSWORD;
        this.localdatabase.username = Constants.DEFAULT_DATABASE_USERNAME;

        this.serverDatabase.databaseName = "museumbackup";
        this.serverDatabase.host = Constants.DEFAULT_DATABASE_HOST;
        this.serverDatabase.port = Constants.DEFAULT_DATABASE_PORT;
        this.serverDatabase.password = Constants.DEFAULT_DATABASE_PASSWORD;
        this.serverDatabase.username = Constants.DEFAULT_DATABASE_USERNAME;

        this.debugMode = false;
    }

    public static void loadConfigurations(File localConfig, File serverConfig)
    {
        XStream xs = new XStream(new DomDriver());
        instance.localdatabase = (Database) xs.fromXML(localConfig);
        instance.serverDatabase = (Database) xs.fromXML(serverConfig);
    }

    public static void saveConfiguration(File localConfig, File backUpConfig)
            throws FileNotFoundException, IOException
    {
        localConfig.createNewFile();
        backUpConfig.createNewFile();
        XStream xs = new XStream(new DomDriver());
        xs.toXML(instance.localdatabase, new FileOutputStream(localConfig));
        xs.toXML(instance.serverDatabase, new FileOutputStream(backUpConfig));
    }
}
