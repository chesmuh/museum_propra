package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.sql.SQLQueryCategory;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the categories
 *
 * @author Anselm Brehme
 */
public class CategoryManager extends AbstractManager<Category>
{

    public CategoryManager()
    {
        super(SQLQueryCategory.class);
    }

    public Collection<Category> getByName(String name)
    {
        Collection<Category> result = new HashSet<>();
        for (Category step : this.getAll(true))
        {
            if (step.getName().toLowerCase().contains(name.toLowerCase()))
            {
                result.add(step);
            }
        }
        return result;
    }

    public Collection<Category> getAllByMuseumId(long id)
    {
        HashSet<Category> ret = new HashSet<>();

        for (Category c : this.getAll(true))
        {
            if (c.getMuseum_id() == id)
            {
                ret.add(c);
            }
        }
        return ret;
    }

    public Collection<Category> getCategoryByMuseum(Collection<Museum> museums)
    {
        HashSet<Category> result = new HashSet<>();
        for (Museum step : museums)
        {
            result.addAll(getAllByMuseumId(step.getId()));
        }
        return result;
    }

    public Collection<Category> getChildCategories(long id)
    {
        HashSet<Category> result = new HashSet<>();
        for (Category category : this.getAll(true))
        {
            if (category.getParent_id() != null && category.getParent_id() == id)
            {
                result.add(category);
            }
        }
        return result;
    }

    @Override
    public void reAssignLocalModel(Category model, Collection<Long> blockedIDs) throws SQLException, ConnectionException
    {
        long oldID = model.getId();
        while (blockedIDs.contains(model.getId()))
        {
            this.delete(model, true);
            this.store(model);
        }
        for (Exhibit exhibit : DataAccess.getInstance().getAllExhibitsByCategory(oldID))
        {
            exhibit.setCategory_id(model.getId());
            ModelManagement.getInstance().getExhibitManager().update(exhibit, true, false);
        }
        for (History history : DataAccess.getInstance().getAllHistory())
        {
            if (history.getCategory_id() == oldID)
            {
                history.setCategory_id(model.getId());
                ModelManagement.getInstance().getHistoryManager().update(history, true, false);
            }
        }
    }

    public Collection<Category> getAllSubCategories(long id)
    {
        HashSet<Category> result = new HashSet<>();
        result.add(this.getbyId(id, true));
        return this.getAllSubCategories(result, id);
    }
    
    private Collection<Category> getAllSubCategories(HashSet<Category> result, long id)
    {
        Collection<Category> temp = this.getChildCategories(id);
        result.addAll(temp);
        for (Category category : temp)
        {
            this.getAllSubCategories(result, category.getId());
        }
        return result;
    }
}
