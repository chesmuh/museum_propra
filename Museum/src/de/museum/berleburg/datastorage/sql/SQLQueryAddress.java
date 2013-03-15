package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub, Anselm Brehme
 */
public class SQLQueryAddress extends AbstractSQLQuery<Address>
{

    public SQLQueryAddress() throws SQLException, ConnectionException
    {
        super(MuseumDB.Address.getCreateTableSQL(),
                MuseumDB.Address.ID, MuseumDB.Address.TABLE_NAME,
                MuseumDB.Address.STREET,
                MuseumDB.Address.TOWN,
                MuseumDB.Address.HOUSENUMBER,
                MuseumDB.Address.ZIPCODE,
                MuseumDB.Address.STATE,
                MuseumDB.Address.COUNTRY,
                MuseumDB.Address.DELETED,
                MuseumDB.Address.INSERTED,
                MuseumDB.Address.UPDATE);
    }

    @Override
    public Collection<Address> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Address> result = new ArrayList<>();
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
            i++;
            Long id = resultSet.getLong(MuseumDB.Address.ID);
            String street = resultSet.getString(MuseumDB.Address.STREET);
            String town = resultSet.getString(MuseumDB.Address.TOWN);
            String houseNumber = resultSet.getString(MuseumDB.Address.HOUSENUMBER);
            String zipCode = resultSet.getString(MuseumDB.Address.ZIPCODE);
            String state = resultSet.getString(MuseumDB.Address.STATE);
            String country = resultSet.getString(MuseumDB.Address.COUNTRY);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Address.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Address.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Address.UPDATE);
            result.add(new Address(street, houseNumber, zipCode, town, state, country,
                    id, deleted, insert, updated));
            if (i % percent == 0)
            {
                if (callBack != null)
                {
                    callBack.updateProcess(++percentage, Constants.ADRESS_MANAGER_ID);
                }
            }
        }
        return result;
    }

    @Override
    public void update(boolean local, Address model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getStreet(),
                model.getTown(),
                model.getHousenumber(),
                model.getZipcode(),
                model.getState(),
                model.getCountry(),
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
    public void store(Address model) throws SQLException
    {
        this.bindValues(this.store,
                model.getStreet(),
                model.getTown(),
                model.getHousenumber(),
                model.getZipcode(),
                model.getState(),
                model.getCountry(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
