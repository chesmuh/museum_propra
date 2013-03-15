package de.museum.berleburg.datastorage.manager;

import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.datastorage.sql.SQLQueryMuseum;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the museums
 *
 * @author Anselm Brehme
 */
public class MuseumManager extends AbstractManager<Museum>
{

    public MuseumManager()
    {
        super(SQLQueryMuseum.class);
    }

    public Collection<Museum> getByMuseumName(String name)
    {
        HashSet<Museum> result = new HashSet<>();
        for (Museum step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Museum model, Collection<Long> blockedIDs) throws ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Role role : DataAccess.getInstance().getAllRoles())
        {
            if (role.getMuseum_id() == oldID)
            {
                role.setMuseum_id(model.getId());
                ModelManagement.getInstance().getRoleManager().update(role, true, false);
            }
        }
        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
        {
            if (outsourced.getMuseum_id() == oldID)
            {
                outsourced.setMuseum_id(model.getId());
                ModelManagement.getInstance().getOutsourcedManager().update(outsourced, true, false);
            }
        }
        for (History history : DataAccess.getInstance().getAllHistory())
        {
            if (history.getMuseum_id() == oldID)
            {
                history.setMuseum_id(model.getId());
                ModelManagement.getInstance().getHistoryManager().update(history, true, false);
            }
        }
        for (Section section : DataAccess.getInstance().getAllSections())
        {
            if (section.getMuseum_id() == oldID)
            {
                section.setMuseum_id(model.getId());
                ModelManagement.getInstance().getSectionManager().update(section, true, false);
            }
        }
        for (Category category : DataAccess.getInstance().getAllCategories())
        {
            if (category.getMuseum_id() == oldID)
            {
                category.setMuseum_id(model.getId());
                ModelManagement.getInstance().getCategoryManager().update(category, true, false);
            }
        }
        for (Exhibit exhibit : DataAccess.getInstance().getAllExhibits())
        {
            if (exhibit.getMuseum_id() == oldID)
            {
                exhibit.setMuseum_id(model.getId());
                ModelManagement.getInstance().getExhibitManager().update(exhibit, true, false);
            }
        }
    }
}
