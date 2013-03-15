package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.sql.SQLQueryAddress;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the adresses
 *
 * @author Anselm Brehme
 */
public class AddressManager extends AbstractManager<Address>
{

    public AddressManager()
    {
        super(SQLQueryAddress.class);
    }

    public Collection<Address> getAddressByMuseum(Collection<Museum> museums)
    {
        HashSet<Address> result = new HashSet<>();
        for (Museum step : museums)
        {
            result.add(step.getAddress());
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Address model, Collection<Long> blockedIDs) throws SQLException, ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Museum museum : DataAccess.getInstance().getAllMuseum())
        {
            if (museum.getAddress_id() == oldID)
            {
                museum.setAddress_id(model.getId());
                ModelManagement.getInstance().getMuseumManager().update(museum, true, false);
            }
        }
        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
        {
            if (outsourced.getAddress_id() == oldID)
            {
                outsourced.setAddress_id(model.getId());
                ModelManagement.getInstance().getOutsourcedManager().update(outsourced, true, false);
            }
        }
        for (Contact contact : DataAccess.getInstance().getAllContacts())
        {
            if (contact.getAddress_id() == oldID)
            {
                contact.setAddress_id(model.getId());
                ModelManagement.getInstance().getContactManager().update(contact, true, false);
            }
        }
    }
}