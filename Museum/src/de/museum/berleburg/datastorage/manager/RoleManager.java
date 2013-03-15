package de.museum.berleburg.datastorage.manager;

import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.sql.SQLQueryRole;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Anselm
 */
public class RoleManager extends AbstractManager<Role>
{
    public RoleManager()
    {
        super(SQLQueryRole.class);
    }

    public Collection<Role> getAllRolesByMuseumId(long id)
    {
        HashSet<Role> result = new HashSet<>();
        for (Role role : this.getAll(true))
        {
            if (role.getMuseum_id() == id)
            {
                result.add(role);
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Role model, Collection<Long> blockedIDs) throws ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Contact contact : DataAccess.getInstance().getAllContacts())
        {
            if (contact.getRoleId() == oldID)
            {
                contact.setRoleId(model.getId());
                ModelManagement.getInstance().getContactManager().update(contact, true, false);
            }
        }
    }
}
