package de.museum.berleburg.datastorage.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub, Anselm Brehme
 */
public class SQLQueryOutsourced extends AbstractSQLQuery<Outsourced>
{
    
    
    public SQLQueryOutsourced() throws SQLException, ConnectionException
    {
        super(MuseumDB.Outsourced.getCreateTableSQL(),
                MuseumDB.Outsourced.ID, MuseumDB.Outsourced.TABLE_NAME,
                MuseumDB.Outsourced.NAME,
                MuseumDB.Outsourced.DESCRIPTION,
                MuseumDB.Outsourced.STARTDATE,
                MuseumDB.Outsourced.ENDDATE,
                MuseumDB.Outsourced.ADDRESS_ID,
                MuseumDB.Outsourced.CONTACT_ID,
                MuseumDB.Outsourced.MUSEUM_ID,
                MuseumDB.Outsourced.DELETED,
                MuseumDB.Outsourced.INSERTED,
                MuseumDB.Outsourced.UPDATE);
    }

    @Override
    public Collection<Outsourced> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Outsourced> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Outsourced.ID);
            String name = resultSet.getString(MuseumDB.Outsourced.NAME);
            String description = resultSet.getString(MuseumDB.Outsourced.DESCRIPTION);
            Date startDate = resultSet.getDate(MuseumDB.Outsourced.STARTDATE);
            Date endDate = resultSet.getDate(MuseumDB.Outsourced.ENDDATE);
            Long address_id = resultSet.getLong(MuseumDB.Outsourced.ADDRESS_ID);
            Long contact_id = resultSet.getLong(MuseumDB.Outsourced.CONTACT_ID);
            Long museum_id = resultSet.getLong(MuseumDB.Outsourced.MUSEUM_ID);

            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Outsourced.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Outsourced.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Outsourced.UPDATE);
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ").append(MuseumDB.OutsourcedExhibits.EXHIBIT_ID).append(",")
                    .append(MuseumDB.OutsourcedExhibits.GIVENBACK)
                    .append(" FROM `").append(MuseumDB.OutsourcedExhibits.TABLE_NAME)
                    .append("` WHERE ").append(MuseumDB.OutsourcedExhibits.OUTSOURCED_ID).append(" = ").append(id);
            ResultSet resultSet2;
            if (local)
            {
                resultSet2 = this.localConnection.prepareStatement(sql.toString()).executeQuery();
            }
            else
            {
                resultSet2 = this.serverConnection.prepareStatement(sql.toString()).executeQuery();
            }
            HashMap<Long,Timestamp> exhibits = new HashMap<>();
            while (resultSet2.next())
            {
                exhibits.put(resultSet2.getLong(MuseumDB.OutsourcedExhibits.EXHIBIT_ID), resultSet2.getTimestamp(MuseumDB.OutsourcedExhibits.GIVENBACK));
            }
            
            result.add(
                    new Outsourced(name, description, startDate, endDate, address_id, contact_id, museum_id, exhibits,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.OUTSOURCED_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Outsourced model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getStartDate(),
                model.getEndDate(),
                model.getAddress_id(),
                model.getContact_id(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ")
            .append(MuseumDB.OutsourcedExhibits.TABLE_NAME).append("(")
            .append(MuseumDB.OutsourcedExhibits.OUTSOURCED_ID).append(",")
            .append(MuseumDB.OutsourcedExhibits.EXHIBIT_ID).append(",")
            .append(MuseumDB.OutsourcedExhibits.GIVENBACK).append(")")
            .append(" VALUES (?,?,?)");
        PreparedStatement insertStmt = local ? this.localConnection.prepareStatement(sql.toString()) : this.serverConnection.prepareStatement(sql.toString());
        sql = new StringBuilder();
        sql.append("DELETE FROM ").append(MuseumDB.OutsourcedExhibits.TABLE_NAME).append(" WHERE ")
            .append(MuseumDB.OutsourcedExhibits.OUTSOURCED_ID).append( " = ").append(model.getId());
        if (local)
        {
            this.localConnection.prepareStatement(sql.toString()).execute();
            this.update.executeUpdate();
        }
        else
        {
            this.serverConnection.prepareStatement(sql.toString()).execute();
            this.updateServer.executeUpdate();
        }
        for (Entry<Long,Timestamp> entry : model.getExhibitIds().entrySet())
        {
            insertStmt.setObject(1, model.getId());
            insertStmt.setObject(2, entry.getKey());
            insertStmt.setObject(3, entry.getValue());
            insertStmt.addBatch();
        }
        insertStmt.executeBatch();
    }

    @Override
    public void store(Outsourced model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getDescription(),
                model.getStartDate(),
                model.getEndDate(),
                model.getAddress_id(),
                model.getContact_id(),
                model.getMuseum_id(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ")
            .append(MuseumDB.OutsourcedExhibits.TABLE_NAME).append("(")
            .append(MuseumDB.OutsourcedExhibits.OUTSOURCED_ID).append(",")
            .append(MuseumDB.OutsourcedExhibits.EXHIBIT_ID).append(",")
            .append(MuseumDB.OutsourcedExhibits.GIVENBACK).append(")")
            .append(" VALUES (?,?,?)");
       PreparedStatement insertStmt = this.localConnection.prepareStatement(sql.toString());
       for (Entry<Long,Timestamp> entry : model.getExhibitIds().entrySet())
        {
            insertStmt.setObject(1, model.getId());
            insertStmt.setObject(2, entry.getKey());
            insertStmt.setObject(3, entry.getValue());
            insertStmt.addBatch();
        }
        insertStmt.executeBatch();
    }

    @Override
    public void delete(boolean local, Outsourced model) throws SQLException
    {
        super.delete(local, model);
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(MuseumDB.OutsourcedExhibits.TABLE_NAME).append(" WHERE ")
            .append(MuseumDB.OutsourcedExhibits.OUTSOURCED_ID).append( " = ").append(model.getId());
        if (local)
        {
            this.localConnection.prepareStatement(sql.toString()).execute();
        }
        else
        {
            this.serverConnection.prepareStatement(sql.toString()).execute();
        }
    }
}
