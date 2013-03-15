package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub, Nils Leonhardt, Anselm Brehme
 *
 */
public class SQLQuerySection extends AbstractSQLQuery<Section>
{

    public SQLQuerySection() throws SQLException, ConnectionException
    {
        super(MuseumDB.Section.getCreateTableSQL(),
                MuseumDB.Section.ID, MuseumDB.Section.TABLE_NAME,
                MuseumDB.Section.NAME,
                MuseumDB.Section.DESCRIPTION,
                MuseumDB.Section.PARENT_ID,
                MuseumDB.Section.MUSEUM_ID,
                MuseumDB.Section.DELETED,
                MuseumDB.Section.INSERTED,
                MuseumDB.Section.UPDATE);
    }

    @Override
    public Collection<Section> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Section> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Section.ID);
            String name = resultSet.getString(MuseumDB.Section.NAME);
            String description = resultSet.getString(MuseumDB.Section.DESCRIPTION);
            Long parent_id = resultSet.getLong(MuseumDB.Section.PARENT_ID);
            Long museum_id = resultSet.getLong(MuseumDB.Section.MUSEUM_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Section.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Section.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Section.UPDATE);
            result.add(
                    new Section(name, description, parent_id, museum_id,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.SECTION_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Section model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getParent_id(),
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
    public void store(Section model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getDescription(),
                model.getParent_id(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
