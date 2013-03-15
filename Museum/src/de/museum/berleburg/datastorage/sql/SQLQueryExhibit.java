package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Anselm Brehme
 */
public class SQLQueryExhibit extends AbstractSQLQuery<Exhibit>
{

    public SQLQueryExhibit() throws SQLException, ConnectionException
    {
        super(MuseumDB.Exhibit.getCreateTableSQL(),
                MuseumDB.Exhibit.ID, MuseumDB.Exhibit.TABLE_NAME,
                MuseumDB.Exhibit.NAME,
                MuseumDB.Exhibit.DESCRIPTION,
                MuseumDB.Exhibit.SECTION_ID,
                MuseumDB.Exhibit.CATEGORY_ID,
                MuseumDB.Exhibit.COUNT,
                MuseumDB.Exhibit.RFID,
                MuseumDB.Exhibit.MUSEUM_ID,
                MuseumDB.Exhibit.DELETED,
                MuseumDB.Exhibit.INSERTED,
                MuseumDB.Exhibit.WERT,
                MuseumDB.Exhibit.UPDATE);
    }

    @Override
    public Collection<Exhibit> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Exhibit> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Exhibit.ID);
            String name = resultSet.getString(MuseumDB.Exhibit.NAME);
            String description = resultSet.getString(MuseumDB.Exhibit.DESCRIPTION);
            long section_id = resultSet.getLong(MuseumDB.Exhibit.SECTION_ID);
            long category_id = resultSet.getLong(MuseumDB.Exhibit.CATEGORY_ID);
            long count = resultSet.getLong(MuseumDB.Exhibit.COUNT);
            String rfid = resultSet.getString(MuseumDB.Exhibit.RFID);
            long museum_id = resultSet.getLong(MuseumDB.Exhibit.MUSEUM_ID);
            double wert = resultSet.getDouble(MuseumDB.Exhibit.WERT);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Exhibit.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Exhibit.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Exhibit.UPDATE);

            result.add(
                    new Exhibit(name, description, section_id, category_id, count, rfid, museum_id, wert,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.EXHIBIT_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Exhibit model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getSection_id(),
                model.getCategory_id(),
                model.getCount(),
                model.getRfid(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getWert(),
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
    public void store(Exhibit model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getDescription(),
                model.getSection_id(),
                model.getCategory_id(),
                model.getCount(),
                model.getRfid(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getWert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
