package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Anselm
 */
public class SQLQueryHistory extends AbstractSQLQuery<History>
{

    public SQLQueryHistory() throws SQLException, ConnectionException
    {
        super(MuseumDB.History.getCreateTableSQL(),
                MuseumDB.History.ID, MuseumDB.History.TABLE_NAME,
                MuseumDB.History.EXHIBIT_ID,
                MuseumDB.History.NAME,
                MuseumDB.History.DESCRIPTION,
                MuseumDB.History.SECTION_ID,
                MuseumDB.History.CATEGORY_ID,
                MuseumDB.History.COUNT,
                MuseumDB.History.RFID,
                MuseumDB.History.MUSEUM_ID,
                MuseumDB.History.WERT,
                MuseumDB.History.OUTSOURCED_ID,
                MuseumDB.History.DELETED,
                MuseumDB.History.INSERTED,
                MuseumDB.History.STARTDATE,
                MuseumDB.History.UPDATE);
    }

    @Override
    public Collection<History> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<History> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.History.ID);
            Long exhibit_id = resultSet.getLong(MuseumDB.History.EXHIBIT_ID);
            String name = resultSet.getString(MuseumDB.History.NAME);
            String description = resultSet.getString(MuseumDB.History.DESCRIPTION);
            long section_id = resultSet.getLong(MuseumDB.History.SECTION_ID);
            long category_id = resultSet.getLong(MuseumDB.History.CATEGORY_ID);
            long count = resultSet.getLong(MuseumDB.History.COUNT);
            String rfid = resultSet.getString(MuseumDB.History.RFID);
            long museum_id = resultSet.getLong(MuseumDB.History.MUSEUM_ID);
            Double wert = resultSet.getDouble(MuseumDB.History.WERT);
            Long outsourced_id = resultSet.getLong(MuseumDB.History.OUTSOURCED_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.History.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.History.INSERTED);
            Timestamp startdate = resultSet.getTimestamp(MuseumDB.History.STARTDATE);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.History.UPDATE);
            result.add(
                    new History(exhibit_id, name, description, section_id, category_id, count, rfid, museum_id, wert, outsourced_id, startdate,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.HISTORY_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, History model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getExhibit_id(),
                model.getName(),
                model.getDescription(),
                model.getSection_id(),
                model.getCategory_id(),
                model.getCount(),
                model.getRfid(),
                model.getMuseum_id(),
                model.getWert(),
                model.getOutsourced_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getStartdate(),
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
    public void store(History model) throws SQLException
    {
        this.bindValues(this.store,
                model.getExhibit_id(),
                model.getName(),
                model.getDescription(),
                model.getSection_id(),
                model.getCategory_id(),
                model.getCount(),
                model.getRfid(),
                model.getMuseum_id(),
                model.getWert(),
                model.getOutsourced_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getStartdate(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
