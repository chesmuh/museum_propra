package de.museum.berleburg.datastorage.sql;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.exceptions.ConnectionException;

public class SQLQueryImage extends AbstractSQLQuery<Image>
{

    public SQLQueryImage() throws SQLException, ConnectionException
    {
        super(MuseumDB.Images.getCreateTableSQL(),
                MuseumDB.Images.ID, MuseumDB.Images.TABLE_NAME,
                MuseumDB.Images.IMAGE,
                MuseumDB.Images.NAME,
                MuseumDB.Images.EXHIBIT_ID,
                MuseumDB.Images.DELETED,
                MuseumDB.Images.INSERTED,
                MuseumDB.Images.UPDATE);
    }
    private PreparedStatement getById;
    private PreparedStatement getByIdServer;
    private PreparedStatement getAllByExhibit;

    @Override
    public void init() throws SQLException
    {
        super.init();
        String sql = "SELECT * FROM " + MuseumDB.Images.TABLE_NAME + " WHERE " + MuseumDB.Images.ID + " = ?";
        this.getById = this.localConnection.prepareStatement(sql);
        if (this.serverConnection != null)
        {
            this.getByIdServer = this.serverConnection.prepareStatement(sql);
        }
        sql = "SELECT * FROM " + MuseumDB.Images.TABLE_NAME + " WHERE " + MuseumDB.Images.EXHIBIT_ID + " = ?";
        this.getAllByExhibit = this.localConnection.prepareStatement(sql);
    }

    /**
     * Returns the query to get all images by exhibit
     *
     * @param table
     * @return
     */
    @Override
    protected String prepareGetAll()
    {
        return "SELECT " + MuseumDB.Images.ID + ","
                + MuseumDB.Images.NAME + ","
                + MuseumDB.Images.EXHIBIT_ID + ","
                + MuseumDB.Images.DELETED + ","
                + MuseumDB.Images.INSERTED + ","
                + MuseumDB.Images.UPDATE
                + " FROM " + MuseumDB.Images.TABLE_NAME;
    }

    /**
     * Returns all images belonging to given exhibit
     *
     * @param exhibitId
     * @return
     * @throws Exception
     */
    public Collection<Image> getAllByExhibitId(Long exhibitId) throws SQLException
    {
        ArrayList<Image> result = new ArrayList<>();
        this.bindValues(this.getAllByExhibit, exhibitId);
        ResultSet resultSet = this.getAllByExhibit.executeQuery();
        while (resultSet.next())
        {
            Long id = resultSet.getLong(MuseumDB.Images.ID);
            String name = resultSet.getString(MuseumDB.Images.NAME);
            Blob blob = resultSet.getBlob(MuseumDB.Images.IMAGE);
            long exhibit_id = resultSet.getLong(MuseumDB.Images.EXHIBIT_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Images.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Images.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Images.UPDATE);

            byte[] bytes = blob.getBytes(1, (int) blob.length());

            result.add(
                    new Image(bytes, name, exhibit_id,
                    id, deleted, insert, updated));
        }
        return result;
    }

    @Override
    public Collection<Image> loadAll(boolean local, ProcessCallBack callBack) throws SQLException, UnsupportedOperationException
    {
        ArrayList<Image> result = new ArrayList<>();
        ResultSet resultSet = local ? this.loadAll.executeQuery() : this.loadAllBackup.executeQuery();
        int rowcount = 0;
        if (resultSet.last())
        {
            rowcount = resultSet.getRow();
            resultSet.beforeFirst();
        }
        final int percent = ((int) (rowcount / 100) == 0) ? 1 : rowcount / 100;
        int percentage = 0;
        int i = 0;
        while (resultSet.next())
        {
            Long id = resultSet.getLong(MuseumDB.Images.ID);
            String name = resultSet.getString(MuseumDB.Images.NAME);
            long exhibit_id = resultSet.getLong(MuseumDB.Images.EXHIBIT_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Images.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Images.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Images.UPDATE);

            result.add(new Image(name, exhibit_id, id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.IMAGE_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Image model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getRawImage(),
                model.getName(),
                model.getExhibit_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        if (local)
        {
            this.update.executeUpdate();
        }
        else
        {
            this.updateServer.executeUpdate();
        }
    }

    @Override
    public void store(Image model) throws SQLException
    {
        this.bindValues(this.store,
                model.getRawImage(),
                model.getName(),
                model.getExhibit_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }

    @Override
    public Image getById(boolean local, Long idToGet) throws SQLException
    {
        this.bindValues(local ? this.getById : this.getByIdServer, idToGet);
        ResultSet resultSet = local ? this.getById.executeQuery() : this.getByIdServer.executeQuery();
        if (resultSet.next())
        {
            Long id = resultSet.getLong(MuseumDB.Images.ID);
            String name = resultSet.getString(MuseumDB.Images.NAME);
            Blob blob = resultSet.getBlob(MuseumDB.Images.IMAGE);
            long exhibit_id = resultSet.getLong(MuseumDB.Images.EXHIBIT_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Images.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Images.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Address.UPDATE);
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            return new Image(bytes, name, exhibit_id, id, deleted, insert, updated);
        }
        return null;
    }
}
