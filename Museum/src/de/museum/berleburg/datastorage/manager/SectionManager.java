package de.museum.berleburg.datastorage.manager;

import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.datastorage.sql.SQLQuerySection;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the sections
 *
 * @author Anselm Brehme
 */
public class SectionManager extends AbstractManager<Section>
{

    public SectionManager()
    {
        super(SQLQuerySection.class);
    }

    /**
     * Gets all sections that do belong to the museum with given id
     *
     * @param id the museum-id
     * @return the sections of the museum
     */
    public Collection<Section> getByMuseumId(Long id)
    {
        HashSet<Section> ret = new HashSet<>();
        for (Section s : this.getAll(true))
        {
            if (s.getMuseum_id() == id)
            {
                ret.add(s);
            }
        }
        return ret;
    }

    /**
     * Gets all sections that do belong to the given museum
     *
     * @param museum the museum
     * @return the sections of the museum
     */
    public Collection<Section> getByMuseum(Museum museum)
    {
        return this.getByMuseumId(museum.getId());
    }

    /**
     * Gets all sections that have given parent-section-id
     *
     * @param id the parent-section-id
     * @return the sub-sections
     */
    public Collection<Section> getByParentSectionId(Long id)
    {
        HashSet<Section> ret = new HashSet<>();
        for (Section s : this.getAll(true))
        {
            if (s.getParent_id() == id)
            {
                ret.add(s);
            }
        }
        return ret;
    }

    /**
     * Gets all sections that have given parent-section
     *
     * @param section the parent-section
     * @return the sub-sections
     */
    public Collection<Section> getByParentSection(Section section)
    {
        return this.getByParentSectionId(section.getId());
    }

    public Collection<Section> getByName(String name)
    {
        HashSet<Section> result = new HashSet<>();
        for (Section step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    public Collection<Section> getAllSubSections(long id)
    {
        HashSet<Section> result = new HashSet<>();
        result.add(this.getbyId(id, true));
        return this.getAllSubSections(result, id);
    }

    private Collection<Section> getAllSubSections(HashSet<Section> result, long id)
    {
        Collection<Section> temp = this.getByParentSectionId(id);
        result.addAll(temp);
        for (Section section : temp)
        {
            this.getAllSubSections(result, section.getId());
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Section model, Collection<Long> blockedIDs) throws ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Exhibit exhibit : DataAccess.getInstance().getAllExhibits())
        {
            if (exhibit.getSection_id() == oldID)
            {
                exhibit.setSection_id(model.getId());
                ModelManagement.getInstance().getExhibitManager().update(exhibit, true, false);
            }
        }
        for (History history : DataAccess.getInstance().getAllHistory())
        {
            if (history.getSection_id() == oldID)
            {
                history.setSection_id(model.getId());
                ModelManagement.getInstance().getHistoryManager().update(history, true, false);
            }
        }
    }
}
