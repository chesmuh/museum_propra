package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub, Anselm Brehme
 */
public class SQLQueryCategory extends AbstractSQLQuery<Category>
{

    public SQLQueryCategory() throws SQLException, ConnectionException
    {
        super(MuseumDB.Category.getCreateTableSQL(),
                MuseumDB.Category.ID, MuseumDB.Category.TABLE_NAME,
                MuseumDB.Category.NAME,
                MuseumDB.Category.MUSEUM_ID,
                MuseumDB.Category.PARENT_ID,
                MuseumDB.Category.DELETED,
                MuseumDB.Category.INSERTED,
                MuseumDB.Category.UPDATE);
    }

    @Override
    public Collection<Category> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Category> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Category.ID);
            String name = resultSet.getString(MuseumDB.Category.NAME);
            Long museum_id = resultSet.getLong(MuseumDB.Category.MUSEUM_ID);
            Long parent_id = resultSet.getLong(MuseumDB.Category.PARENT_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Category.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Category.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Category.UPDATE);
            result.add(new Category(name, museum_id, parent_id,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.CATEGORY_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Category model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getMuseum_id(),
                model.getParent_id(),
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
    public void store(Category model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getMuseum_id(),
                model.getParent_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
