package de.museum.berleburg.datastorage.manager;

import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.sql.SQLQueryLabel;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 *
 * @author Anselm
 */
public class LabelManager extends AbstractManager<Label>
{
    public LabelManager()
    {
        super(SQLQueryLabel.class);
    }

    public Collection<Label> getByName(String name)
    {
        HashSet<Label> result = new HashSet<>();
        for (Label step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    public Collection<Label> getByExhibitId(long id)
    {
        HashSet<Label> result = new HashSet<>();
        for (Label step : this.getAll(true))
        {
            if (step.getExhibit_ids().contains(id))
            {
                result.add(step);
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Label model, Collection<Long> blockedIDs) throws ConnectionException
    {
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
    }
}
