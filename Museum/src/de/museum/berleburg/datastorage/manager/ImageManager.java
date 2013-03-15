package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.sql.SQLQueryImage;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionTimeOutException;

/**
 * Manages the images
 *
 * @author Nils Leonhardt
 */
public class ImageManager extends AbstractManager<Image>
{

    public ImageManager()
    {
        super(SQLQueryImage.class);
    }

    /**
     * Gets all Images that do belong to given exhibit-id
     *
     * @param id the exhibit-id
     * @return the images
     */
    public Collection<Image> getByExhibitId(Long id) throws ConnectionException
    {
        try
        {
            return ((SQLQueryImage) this.sqlQuery).getAllByExhibitId(id);
        }
        catch (SQLTimeoutException e)
        {
            throw new ConnectionTimeOutException("Connection timed out", e);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Unexpected SQLError!", e);
        }
    }

    /**
     * Gets all Images that do belong to given exhibit
     *
     * @param exhibit the exhibit
     * @return the images
     */
    public Collection<Image> getByExhibit(Exhibit exhibit) throws ConnectionException
    {
        return this.getByExhibitId(exhibit.getId());
    }

    /**
     * Gets all Images that do belong to given exhibits
     *
     * @param exhibits the exhibits
     * @return the images
     */
    public Collection<Image> getByExhibits(Collection<Exhibit> exhibits) throws ConnectionException
    {
        HashSet<Image> result = new HashSet<>();
        for (Exhibit step : exhibits)
        {
            result.addAll(getByExhibit(step));
        }
        return result;
    }

    @Override
    public void store(Image model) throws ConnectionException
    {
        try
        {
            model.setInsert(new Timestamp(System.currentTimeMillis()));
            this.sqlQuery.store(model);
        }
        catch (SQLTimeoutException e)
        {
            throw new ConnectionTimeOutException("Connection timed out", e);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Unexpected SQLError!", e);
        }
    }

    @Override
    public Image getbyId(Long id, boolean local)
    {
        try
        {
            return this.sqlQuery.getById(local, id);
        }
        catch (SQLException ex)
        {
            throw new IllegalStateException("Error while getting Image", ex);
        }
    }

    @Override
    public void reAssignLocalModel(Image model, Collection<Long> blockedIDs) throws ConnectionException
    {
        model = this.getbyId(model.getId(), true);
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
    }

    @Override
    public void loadServerDB(ProcessCallBack callBack) throws ConnectionException
    {
        super.loadServerDB(callBack);
        this.loadAll(true, callBack);
    }
}
