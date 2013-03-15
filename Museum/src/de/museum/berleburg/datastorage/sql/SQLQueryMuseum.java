package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Nils Leonhardt, Anselm Brehme
 */
public class SQLQueryMuseum extends AbstractSQLQuery<Museum>
{

    public SQLQueryMuseum() throws SQLException, ConnectionException
    {
        super(MuseumDB.Museum.getCreateTableSQL(), MuseumDB.Museum.ID, MuseumDB.Museum.TABLE_NAME,
                MuseumDB.Museum.NAME,
                MuseumDB.Museum.DESCRIPTION,
                MuseumDB.Museum.ADDRESS_ID,
                MuseumDB.Museum.DELETED,
                MuseumDB.Museum.INSERTED,
                MuseumDB.Museum.UPDATE);
    }

    @Override
    public Collection<Museum> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Museum> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Museum.ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Museum.DELETED);
            String name = resultSet.getString(MuseumDB.Museum.NAME);
            String description = resultSet.getString(MuseumDB.Museum.DESCRIPTION);
            Long address_id = resultSet.getLong(MuseumDB.Museum.ADDRESS_ID);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Museum.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Museum.UPDATE);
            result.add(
                    new Museum(name, description, address_id,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.MUSEUM_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Museum model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getAddress_id(),
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
    public void store(Museum model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getDescription(),
                model.getAddress_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
