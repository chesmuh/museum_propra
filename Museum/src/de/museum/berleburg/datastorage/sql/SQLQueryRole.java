package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub
 */
public class SQLQueryRole extends AbstractSQLQuery<Role>
{

    public SQLQueryRole() throws SQLException, ConnectionException
    {
        super(MuseumDB.Role.getCreateTableSQL(),
                MuseumDB.Role.ID, MuseumDB.Role.TABLE_NAME,
                MuseumDB.Role.NAME,
                MuseumDB.Role.MUSEUM_ID,
                MuseumDB.Role.DELETED,
                MuseumDB.Role.INSERTED,
                MuseumDB.Role.UPDATE);
    }

    @Override
    public Collection<Role> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Role> result = new ArrayList<>();
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
            String name = resultSet.getString(MuseumDB.Role.NAME);
            Long museum_id = resultSet.getLong(MuseumDB.Role.MUSEUM_ID);

            Long id = resultSet.getLong(MuseumDB.Role.ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Role.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Role.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Role.UPDATE);
            result.add(new Role(name, museum_id, id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.ROLE_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Role model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getMuseum_id(),
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
    public void store(Role model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
