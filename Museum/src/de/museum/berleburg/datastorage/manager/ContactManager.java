package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.sql.SQLQueryContact;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the contacts
 *
 * @author Anselm Brehme
 */
public class ContactManager extends AbstractManager<Contact>
{

    public ContactManager()
    {
        super(SQLQueryContact.class);
    }

    public Collection<Contact> getContactByName(String name, String forename)
    {
        HashSet<Contact> result = new HashSet<>();
        for (Contact step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase())
                    || step.getForename().toLowerCase()
                    .contains(forename.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    public Collection<Contact> getContactByMuseumId(long id)
    {
        HashSet<Contact> result = new HashSet<>();
        for (Contact contact : this.localDBModels.values())
        {
            if (contact.getRoleId() != null)
            {
                Role role = DataAccess.getInstance().getRoleById(
                        contact.getRoleId());
                if (role == null)
                {
                    continue;
                }
                if (role.getMuseum_id() == id)
                {
                    result.add(contact);
                }
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Contact model, Collection<Long> blockedIDs) throws SQLException, ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
        {
            if (outsourced.getContact_id() == oldID)
            {
                outsourced.setContact_id(model.getId());
                ModelManagement.getInstance().getOutsourcedManager().update(outsourced, true, false);
            }
        }
    }
}
