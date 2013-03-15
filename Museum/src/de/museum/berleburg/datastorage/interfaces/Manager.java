package de.museum.berleburg.datastorage.interfaces;

import java.sql.SQLException;
import java.util.Collection;

import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IdNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 * Manager interface for a database-model
 *
 * @author Anselm Brehme
 */
public interface Manager<M extends Model>
{

    /**
     * Returns all loaded models
     *
     * @param local
     * @return a collection of the models
     */
    Collection<M> getAll(boolean local);

    /**
     * Returns all loaded models that are marked as deleted
     *
     * @param local
     * @return a collection of the models marked as deleted
     */
    Collection<M> getAllDeleted(boolean local);

    /**
     * Returns a single model identified by the model-id
     *
     * @param id the model-id
     * @return the corresponding model OR null
     * @throws IdNotFoundException
     */
    M getbyId(Long id, boolean local) throws IdNotFoundException;

    /**
     * Updates given model in the database
     *
     * @param model the model to update
     * @throws ConnectionException
     */
    void update(M model, boolean local, boolean updateTime) throws ConnectionException;

    /**
     * Stores given model into the database and sets the id (only local!)
     *
     * @param model the new model to store
     * @throws ConnectionException
     */
    void store(M model) throws ConnectionException;

    /**
     * Marks given model as deleted
     *
     * @param model the model to mark as deleted
     * @throws ModelAlreadyDeletedException
     * @throws ConnectionException
     */
    void markAsDeleted(M model, boolean local) throws ConnectionException, ModelAlreadyDeletedException;

    /**
     * Deletes given model! Data cannot be retrieved!
     *
     * @param model the model to delete
     * @throws ConnectionException
     */
    void delete(M model, boolean local) throws ConnectionException;

    /**
     * Loads all Models from database
     *
     * @param <T> the actual ManagerType
     * @param callBack the processCallBack
     * @return fluent interface
     * @throws ConnectionException
     */
    <T extends Manager> T loadAll(boolean local, ProcessCallBack callBack) throws ConnectionException;

    /**
     * Deletes and restores the model and changes all references to it, in order
     * to retain UniqueID consistency
     *
     * @param model the model to restore
     * @param blockedIDs the ids that cannot be used
     */
    void reAssignLocalModel(M model, Collection<Long> blockedIDs) throws SQLException, ConnectionException;
}
