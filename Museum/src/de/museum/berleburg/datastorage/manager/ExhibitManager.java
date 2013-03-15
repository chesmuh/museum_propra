package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.datastorage.sql.SQLQueryExhibit;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionTimeOutException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 * Manages the exhibits
 *
 * @author Anselm Brehme
 */
public class ExhibitManager extends AbstractManager<Exhibit>
{

    public ExhibitManager()
    {
        super(SQLQueryExhibit.class);
    }

    @Override
    public void update(Exhibit model, boolean local, boolean uptadeTime) throws ConnectionException
    {

        super.update(model, local, uptadeTime);
        if (local) // BackupDb does not need separate history
        {
            //Problem on updating server db all local exhibits receive an update (incrementing update timestamp) too
            DataAccess.getInstance().store(
                    new History(
                    model.getId(),
                    model.getName(),
                    model.getDescription(),
                    model.getSection_id(),
                    model.getCategory_id(),
                    model.getCount(),
                    model.getRfid(),
                    model.getMuseum_id(),
                    model.getWert(),
                    model.getCurrentOutsourced() == null ? null : model.getCurrentOutsourced().getId(),
                    new Timestamp(System.currentTimeMillis()),
                    model.getInsert(),
                    model.getDeleted(),
                    model.getUpdate()));
        }
    }

    public void updateWithoutHistory(Exhibit model, boolean local, boolean updateTime) throws ConnectionException
    {
        super.update(model, local, updateTime);
    }

    @Override
    public void store(Exhibit model) throws ConnectionException
    {
        super.store(model);
        DataAccess.getInstance().store(
                new History(
                model.getId(),
                model.getName(),
                model.getDescription(),
                model.getSection_id(),
                model.getCategory_id(),
                model.getCount(),
                model.getRfid(),
                model.getMuseum_id(),
                model.getWert(),
                model.getCurrentOutsourced() == null ? null : model.getCurrentOutsourced().getId(),
                new Timestamp(System.currentTimeMillis()),
                model.getInsert(),
                model.getDeleted(),
                model.getUpdate()));
    }

    /**
     * Gets all Exhibits that do belong to given section-ids
     *
     * @param ids the section-ids
     * @return the exhibits
     */
    public Collection<Exhibit> getBySectionIds(Collection<Long> ids)
    {
        HashSet<Exhibit> ret = new HashSet<>();
        for (Exhibit e : this.getAll(true))
        {
            if (ids.contains(e.getSection_id()))
            {
                ret.add(e);
            }
        }
        return ret;
    }

    /**
     * Gets all Exhibits that do belong to given sections
     *
     * @param sections the sections
     * @return the exhibits
     */
    public Collection<Exhibit> getBySections(Collection<Section> sections)
    {
        HashSet<Exhibit> ret = new HashSet<>();
        for (Exhibit e : this.getAll(true))
        {
            if (sections.contains(e.getSection()))
            {
                ret.add(e);
            }
        }
        return ret;
    }

    public Collection<Exhibit> getByCategory(long id)
    {
        HashSet<Exhibit> ret = new HashSet<>();
        for (Exhibit e : this.getAll(true))
        {
            if (e.getCategory_id() == id)
            {
                ret.add(e);
            }
        }
        return ret;
    }

    public Collection<Exhibit> getAllBySectionName(String name)
    {
        Collection<Exhibit> result = new HashSet<>();
        for (Exhibit exhibit : this.getAll(true))
        {
            if (exhibit.getSection() != null
                    && exhibit.getSection().getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(exhibit);
            }
        }
        return result;
    }

    public Collection<Exhibit> getByName(String name)
    {
        Collection<Exhibit> result = new HashSet<>();
        for (Exhibit step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    public Collection<Exhibit> getByMuseum(long id)
    {
        Collection<Exhibit> result = new HashSet<>();
        for (Exhibit step : this.getAll(true))
        {
            if (step.getMuseum_id() == id)
            {
                result.add(step);
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Exhibit model, Collection<Long> blockedIDs) throws SQLException, ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        //TODO query in image to change the id there
        for (Label label : DataAccess.getInstance().getAllLabels())
        {
            if (label.getExhibit_ids().contains(oldID))
            {
                label.removeExhibit_id(oldID);
                label.addExhibit_id(model.getId());
                DataAccess.getInstance().update(label);
            }
        }
    }

    public Collection<Exhibit> getByMuseumSectionNull(long id)
    {

        Collection<Exhibit> result = new HashSet<>();
        for (Exhibit step : this.getAll(true))
        {
            if (step.getMuseum_id() == id && step.getSection() == null)
            {
                result.add(step);
            }
        }
        return result;
    }
    
    @Override
    public void markAsDeleted(Exhibit model, boolean local)
    		throws ConnectionTimeOutException, ModelAlreadyDeletedException {
    	try {
			if (model.isDeleted()) {
				throw new ModelAlreadyDeletedException("The model of "
						+ model.getClass().getName() + " was already deleted! ");
			}
			model.setDeleted(new Timestamp(System.currentTimeMillis()));
			this.update(model, local, true); // updates that it is now
												// deleted
		} catch (ConnectionException e) {
			// ignore
		}
    }
    
    public int getCount() {
    	return getAll(true).size();
    }
}
