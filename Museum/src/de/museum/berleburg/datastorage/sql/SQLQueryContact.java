package de.museum.berleburg.datastorage.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Robert Straub, Anselm Brehme
 */
public class SQLQueryContact extends AbstractSQLQuery<Contact>
{

    public SQLQueryContact() throws SQLException, ConnectionException
    {
        super(MuseumDB.Contact.getCreateTableSQL(),
                MuseumDB.Contact.ID, MuseumDB.Contact.TABLE_NAME,
                MuseumDB.Contact.NAME,
                MuseumDB.Contact.FORNAME,
                MuseumDB.Contact.FON,
                MuseumDB.Contact.EMAIL,
                MuseumDB.Contact.DESCRIPTION,
                MuseumDB.Contact.FAX,
                MuseumDB.Contact.ADDRESS_ID,
                MuseumDB.Contact.ROLE_ID,
                MuseumDB.Contact.DELETED,
                MuseumDB.Contact.INSERTED,
                MuseumDB.Contact.UPDATE);
    }

    @Override
    public Collection<Contact> loadAll(boolean local, ProcessCallBack callBack) throws SQLException
    {
        ArrayList<Contact> result = new ArrayList<>();
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
            Long id = resultSet.getLong(MuseumDB.Contact.ID);
            String name = resultSet.getString(MuseumDB.Contact.NAME);
            String foreName = resultSet.getString(MuseumDB.Contact.FORNAME);
            String fon = resultSet.getString(MuseumDB.Contact.FON);
            String email = resultSet.getString(MuseumDB.Contact.EMAIL);
            String description = resultSet.getString(MuseumDB.Contact.DESCRIPTION);
            String fax = resultSet.getString(MuseumDB.Contact.FAX);
            Long address_id = resultSet.getLong(MuseumDB.Contact.ADDRESS_ID);
            Long role_id = resultSet.getLong(MuseumDB.Contact.ROLE_ID);
            Timestamp deleted = resultSet.getTimestamp(MuseumDB.Contact.DELETED);
            Timestamp insert = resultSet.getTimestamp(MuseumDB.Contact.INSERTED);
            Timestamp updated = resultSet.getTimestamp(MuseumDB.Contact.UPDATE);
            result.add(
                    new Contact(name, foreName, fon, email, description, fax, address_id, role_id,
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
    public void update(boolean local, Contact model) throws SQLException
    {
        this.bindValues(local ? this.update : this.updateServer,
                model.getId(),
                model.getName(),
                model.getForename(),
                model.getFon(),
                model.getEmail(),
                model.getDescription(),
                model.getFax(),
                model.getAddress_id(),
                model.getRoleId(),
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
    public void store(Contact model) throws SQLException
    {
        this.bindValues(this.store,
                model.getName(),
                model.getForename(),
                model.getFon(),
                model.getEmail(),
                model.getDescription(),
                model.getFax(),
                model.getAddress_id(),
                model.getRoleId(),
                model.getDeleted(),
                model.getInsert(),
                model.getUpdate());
        this.store.execute();
        this.setGeneratedKey(model);
    }
}
