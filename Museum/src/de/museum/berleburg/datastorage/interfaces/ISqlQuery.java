package de.museum.berleburg.datastorage.interfaces;

import java.sql.SQLException;
import java.util.Collection;

import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Interface for SqlQueries of a Model M
 *
 * @author Anselm
 */
public interface ISqlQuery<M extends Model>
{

    /**
     * Loads all models M from database
     *
     * @param local true for using local-database
     * @return a collection of the loaded models
     * @throws SQLException
     */
    Collection<M> loadAll(boolean local, ProcessCallBack callBack) throws SQLException;

    /**
     * Updates the given model in the database
     *
     * @param local true for using local-database
     * @param model the model to update
     * @throws SQLException
     */
    void update(boolean local, M model) throws SQLException;

    /**
     * Stores the given model into database and sets the model id Storing is
     * always in the local database
     *
     * @param model the model to store
     * @throws SQLException
     */
    void store(M model) throws SQLException;

    /**
     * Deletes the given model. Data cannot be retreived later!
     *
     * @param local true for using local-database
     * @param model the model to delete
     * @throws SQLException
     */
    void delete(boolean local, M model) throws SQLException;

    /**
     * Gets a model directly from Database by Id
     *
     * @param local true for using local-database
     * @param id the model-id
     * @return the created model
     * @throws SQLException
     */
    M getById(boolean local, Long id) throws SQLException;

    /**
     * Tries to get both connections
     *
     * @throws ConnectionException
     */
    public void updateConnections() throws ConnectionException;

    /**
     * Creates the tables
     * 
     * @throws SQLException 
     */
    public void createTables() throws SQLException;

    /**
     * Prepares the statements
     * @throws SQLException 
     */
    public void prepareStatements() throws SQLException;

    /**
     * Initializing the Queries
     * 
     * @throws SQLException 
     */
    public void init() throws SQLException;
}
