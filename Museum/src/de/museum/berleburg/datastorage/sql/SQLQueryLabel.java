package de.museum.berleburg.datastorage.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Anselm
 */
public class SQLQueryLabel extends AbstractSQLQuery<Label>
{

    public SQLQueryLabel() throws SQLException, ConnectionException
    {
        super(MuseumDB.Label.getCreateTableSQL(),
                MuseumDB.Label.ID, MuseumDB.Label.TABLE_NAME,
                MuseumDB.Label.NAME,
                MuseumDB.Label.DELETED,
                MuseumDB.Label.INSERTED,
                MuseumDB.Label.UPDATE);
    }
    PreparedStatement updateList1;
    PreparedStatement updateList2;
    PreparedStatement deleteList;
    PreparedStatement updateList1Server;
    PreparedStatement updateList2Server;
    PreparedStatement deleteListServer;

    @Override
    public void init() throws SQLException
    {
        super.init();
        String sql = "DELETE FROM " + MuseumDB.ExhibitLabel.TABLE_NAME
                + " WHERE " + MuseumDB.ExhibitLabel.LABEL_ID + " = ?";
        this.updateList1 = this.localConnection.prepareStatement(sql);
        if (this.serverConnection != null)
        {
            this.updateList1Server = this.serverConnection.prepareCall(sql);
        }
        sql = "INSERT INTO " + MuseumDB.ExhibitLabel.TABLE_NAME
                + "\n( " + MuseumDB.ExhibitLabel.EXHIBIT_ID + "," + MuseumDB.ExhibitLabel.LABEL_ID + ")"
                + "\nVALUES (?,?) \nON DUPLICATE KEY UPDATE "
                + MuseumDB.ExhibitLabel.EXHIBIT_ID + "= VALUES(" + MuseumDB.ExhibitLabel.EXHIBIT_ID + ")";
        this.updateList2 = this.localConnection.prepareStatement(sql);
        if (this.serverConnection != null)
        {
            this.updateList2Server = this.serverConnection.prepareCall(sql);
        }
        sql = "DELETE FROM " + MuseumDB.ExhibitLabel.TABLE_NAME
                + " WHERE " + MuseumDB.ExhibitLabel.LABEL_ID + " = ?";
        this.deleteList = this.localConnection.prepareStatement(sql);
        if (this.serverConnection != null)
        {
            this.deleteListServer = this.serverConnection.prepareCall(sql);
        }
    }

    @Override
    protected String prepareGetAll()
    {
        return "SELECT " + MuseumDB.Label.ID + ","
                + MuseumDB.Label.NAME + ","
                + MuseumDB.Label.DELETED + ","
                + MuseumDB.Label.INSERTED + ","
                + MuseumDB.Label.UPDATE + ","
                + MuseumDB.ExhibitLabel.EXHIBIT_ID
                + "\nFROM " + MuseumDB.Label.TABLE_NAME
                + "\nLEFT JOIN " + MuseumDB.ExhibitLabel.TABLE_NAME
                + "\nON " + MuseumDB.Label.ID + " = " + MuseumDB.ExhibitLabel.LABEL_ID;
    }

    @Override
    public Collection<Label> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        HashMap<Long, Label> result = new HashMap<>();
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
            Label label;
            Long id = resultSet.getLong(MuseumDB.Label.ID);
            if (result.containsKey(id)) // label already added!
            {
                label = result.get(id);
                Long exhibit_id = resultSet.getLong(MuseumDB.ExhibitLabel.EXHIBIT_ID);
                if (exhibit_id != null)
                {
                    label.addExhibit_id(exhibit_id);
                }
            }
            else
            {
                String name = resultSet.getString(MuseumDB.Label.NAME);
                Timestamp deleted = resultSet.getTimestamp(MuseumDB.Label.DELETED);
                Timestamp insert = resultSet.getTimestamp(MuseumDB.Label.INSERTED);
                Timestamp updated = resultSet.getTimestamp(MuseumDB.Address.UPDATE);

                result.put(id, label = new Label(name, id, deleted, insert, updated));
                Long exhibit_id = resultSet.getLong(MuseumDB.ExhibitLabel.EXHIBIT_ID);
                if (exhibit_id != null)
                {
                    label.addExhibit_id(exhibit_id);
                }
                if (i % percent == 0)
                {
                    if (callBack != null)
                    {
                        callBack.updateProcess(++percentage, Constants.LABEL_MANAGER_ID);
                    }
                }
            }
        }
        return result.values();
    }

    @Override
    public void update(boolean local, Label model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        if (local)
        {
            this.update.executeUpdate();

            this.updateList1.setObject(1, model.getId());
            this.updateList1.execute();
        }
        else
        {
            this.updateServer.executeUpdate();

            this.updateList1Server.setObject(1, model.getId());
            this.updateList1Server.execute();
        }


        if (model.getExhibit_ids().size() > 0)
        {
            for (Long exhibitId : model.getExhibit_ids())
            {
                if (local)
                {
                    this.updateList2.setObject(1, exhibitId);
                    this.updateList2.setObject(2, model.getId());
                    this.updateList2.addBatch();
                }
                else
                {
                    this.updateList2Server.setObject(1, exhibitId);
                    this.updateList2Server.setObject(2, model.getId());
                    this.updateList2Server.addBatch();
                }
            }
            if (local)
            {
                this.updateList2.executeBatch();
            }
            else
            {
                this.updateList2Server.executeBatch();
            }
        }
    }

    @Override
    public void store(Label model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);

        this.updateList1.setObject(1, model.getId());
        this.updateList1.execute();

        if (model.getExhibit_ids().size() > 0)
        {
            for (Long exhibitId : model.getExhibit_ids())
            {
                this.updateList2.setObject(1, exhibitId);
                this.updateList2.setObject(2, model.getId());
                this.updateList2.addBatch();
            }
            this.updateList2.executeBatch();
        }
    }

    @Override
    public void delete(boolean local, Label model) throws SQLException
    {
        super.delete(local, model);
        this.bindValues(local ? this.deleteList : this.deleteListServer, model.getId());
        if (local)
        {
            this.deleteList.execute();
        }
        else
        {
            this.deleteListServer.execute();
        }
    }
}
