package de.museum.berleburg.datastorage.manager;

import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.sql.SQLQueryHistory;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the history
 *
 * @author Anselm Brehme
 */
public class HistoryManager extends AbstractManager<History>
{
    public HistoryManager()
    {
        super(SQLQueryHistory.class);
    }

    public Collection<History> getHistoryByExhibitId(long exhibit_id)
    {
    	Collection<History> ret = new ArrayList<>();
    	for (History step : this.localDBModels.values())
        {
            if (step.getExhibit_id() == (exhibit_id))
            {
                ret.add(step);
            }
        }
        return ret;
    }

    public Collection<History> getByName(String name)
    {
        Collection<History> ret = new ArrayList<>();
        for (History step : this.getAll(true))
        {
            if (step.getName().equals(name))
            {
                ret.add(step);
            }
        }
        return ret;
    }

    @Override
    public void reAssignLocalModel(History model, Collection<Long> blockedIDs) throws ConnectionException
    {
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
    }
}
