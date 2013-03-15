package de.museum.berleburg.datastorage.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.interfaces.Model;
import de.museum.berleburg.datastorage.interfaces.Pair;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionTimeOutException;

public class UpdateManager
{

    private boolean local;
    private Collection<Pair<Model, Model>> noConflicts = new ArrayList<>();
    private Collection<Pair<Model, Model>> conflicts = new ArrayList<>();

    public UpdateManager(boolean local)
    {
        this.local = local;
    }

    /**
     * Conflict ragarding updateTime User has to decide if he wants to keep his
     * local data or keep the server-data
     *
     * @param updateFrom
     * @param toUpdate
     */
    public void addConflict(Model updateFrom, Model toUpdate)
    {
        this.conflicts.add(new Pair<>(updateFrom, toUpdate));
    }

    /**
     * No conflict / or resolved ID-conflict
     *
     * @param updateFrom
     * @param toUpdate
     */
    public void addNoConflict(Model updateFrom, Model toUpdate)
    {
        this.noConflicts.add(new Pair<>(updateFrom, toUpdate));
    }

    /**
     * Model missing in database to update from User has to decide if he wants
     * to keep his local data or delete it.
     *
     * @param toUpdate
     */
    public void addMissingConflict(Model toUpdate)
    {
        this.conflicts.add(new Pair<Model, Model>(null, toUpdate));
    }

    /**
     * Resolves a conflict
     *
     * @param conflict the conflict to resolve
     * @param resolution first model is the wished new state (null for delete),
     * second model is the original state
     */
    public void resolveConflict(Pair<Model, Model> conflict, Pair<Model, Model> resolution)
    {
        if (resolution.getLeft() == null && resolution.getRight() == null)
        {
            throw new IllegalStateException("Both states cannot be null!");
        }
        this.conflicts.remove(conflict);
        this.noConflicts.add(resolution);
    }

    /**
     * Ignores the given conflict
     *
     * @param conflict
     */
    public void ignoreConflict(Pair<Model, Model> conflict)
    {
        this.ignore(conflict, true);
    }

    private void ignoreExhibit(Pair<Model, Model> update, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(update) : this.noConflicts.remove(update))
        {
            long exhibit_id = update.getLeft() == null ? update.getRight().getId() : update.getLeft().getId();
            long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Image)
                {
                    id = entry.getLeft() == null
                            ? ((Image) entry.getRight()).getExhibit_id()
                            : ((Image) entry.getLeft()).getExhibit_id();
                    if (id == exhibit_id)
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof History)
                {
                    id = entry.getLeft() == null
                            ? ((History) entry.getRight()).getExhibit_id()
                            : ((History) entry.getLeft()).getExhibit_id();
                    if (id == exhibit_id)
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Outsourced)
                {
                    if (((Outsourced)entry.getLeft()).getExhibitIds().keySet().contains(exhibit_id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Label)
                {
                    if (entry.getLeft() == null)
                    {
                        if (((Label) entry.getRight()).getExhibit_ids().contains(exhibit_id))
                        {
                            ((Label) entry.getRight()).removeExhibit_id(exhibit_id);
                        }
                    }
                    else
                    {
                        if (((Label) entry.getLeft()).getExhibit_ids().contains(exhibit_id))
                        {
                            ((Label) entry.getLeft()).removeExhibit_id(exhibit_id);
                        }
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Image)
                {
                    id = entry.getLeft() == null
                            ? ((Image) entry.getRight()).getExhibit_id()
                            : ((Image) entry.getLeft()).getExhibit_id();
                    if (id == exhibit_id)
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof History)
                {
                    id = entry.getLeft() == null
                            ? ((History) entry.getRight()).getExhibit_id()
                            : ((History) entry.getLeft()).getExhibit_id();
                    if (id == exhibit_id)
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Label)
                {
                    if (entry.getLeft() == null)
                    {
                        if (((Label) entry.getRight()).getExhibit_ids().contains(exhibit_id))
                        {
                            ((Label) entry.getRight()).removeExhibit_id(exhibit_id);
                        }
                    }
                    else
                    {
                        if (((Label) entry.getLeft()).getExhibit_ids().contains(exhibit_id))
                        {
                            ((Label) entry.getLeft()).removeExhibit_id(exhibit_id);
                        }
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    /**
     * Ignores the given update and dependend Models
     *
     * @param update
     */
    public void ignoreUpdate(Pair<Model, Model> update)
    {
        this.ignore(update, false);
    }

    private int amountIgnored = 0;

    public int getAmountIgnored()
    {
        return amountIgnored;
    }
    
    private void ignore(Pair<Model, Model> pair, boolean isConflict)
    {
        amountIgnored++;
        if (pair.getLeft() instanceof Exhibit || pair.getRight() instanceof Exhibit)
        {
            this.ignoreExhibit(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Outsourced || pair.getRight() instanceof Outsourced)
        {
            this.ignoreOutsourced(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Section || pair.getRight() instanceof Section)
        {
            this.ignoreSection(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Museum || pair.getRight() instanceof Museum)
        {
            this.ignoreMuseum(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Address || pair.getRight() instanceof Address)
        {
            this.ignoreAdress(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Category || pair.getRight() instanceof Category)
        {
            this.ignoreCategory(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Contact || pair.getRight() instanceof Contact)
        {
            this.ignoreContact(pair, isConflict);
        }
        else if (pair.getLeft() instanceof History || pair.getRight() instanceof History)
        {
            this.ignoreHistory(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Image || pair.getRight() instanceof Image)
        {
            this.ignoreImage(pair, isConflict);
        }
         else if (pair.getLeft() instanceof Role || pair.getRight() instanceof Role)
        {
            this.ignoreRole(pair, isConflict);
        }
        else if (pair.getLeft() instanceof Label || pair.getRight() instanceof Label)
        {
            this.ignoreLabel(pair, isConflict);
        }
    }

    /**
     * Finishes the update
     *
     * @return true if no conflict remained
     * @throws SQLException
     */
    public boolean finalizeUpdate(ProcessCallBack updateCallBack) throws ConnectionTimeOutException, ConnectionException
    {
        try
        {
            Connection localCon = Configuration.getInstance().getConnection();
            Connection serverCon = Configuration.getInstance().getConnection();
            localCon.setAutoCommit(true);
            serverCon.setAutoCommit(false);
            if (conflicts.isEmpty())
            {
                int i = 0;
                int imageCounter = 0;
                ModelManagement instance = ModelManagement.getInstance();
                final int percent = noConflicts.size() / 100 == 0 ? 1 : noConflicts.size() / 100;
                int percentage = 0;
                for (Pair<Model, Model> pair : noConflicts)
                {
                    i++;
                    if (pair.getLeft() == null)
                    {
                        instance.remove(local, pair.getRight());
                    }
                    else
                    {
                        if (pair.getLeft() instanceof Image)
                        {
                            imageCounter++;
                            Image image = ModelManagement.getInstance().getImageManager().getbyId(pair.getLeft().getId(), !local);
                            //TODO custom image update without data when no change!
                            instance.update(false, true, image);
                            instance.update(true, false, image);
                            if (imageCounter % 50 == 0)
                            {
                                localCon.commit();
                                serverCon.commit();
                            }
                        }
                        else if (pair.getLeft() instanceof Exhibit)
                        {
                            instance.getExhibitManager().updateWithoutHistory((Exhibit) pair.getLeft(), false, true);
                            instance.getExhibitManager().updateWithoutHistory((Exhibit) pair.getLeft(), true, false);
                        }
                        else
                        {
                            instance.update(false, true, pair.getLeft());
                            instance.update(true, false, pair.getLeft());
                        }
                    }
                    if (i % 2000 == 0)
                    {
                        localCon.commit();
                        serverCon.commit();
                    }
                    if (i % percent == 0)
                    {
                        if (updateCallBack != null)
                        {
                            if (pair.getLeft() instanceof Image)
                            {
                                updateCallBack.updateProcess(++percentage, Constants.UPDATE_MANAGER_ID_IMAGE);
                            }
                            else
                            {
                                updateCallBack.updateProcess(++percentage, Constants.UPDATE_MANAGER_ID);
                            }

                        }
                    }
                }
                localCon.commit();
                serverCon.commit();
                localCon.setAutoCommit(true);
                serverCon.setAutoCommit(true);
                if (this.local)
                {
                    for (Pair<Model, Model> pair : noConflicts)
                    {
                        if (pair.getLeft() != null)
                        {
                             instance.addLocalLoaded(pair.getLeft());
                        }
                    }
                }
                return true;
            }
            return false;
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Unexpected SQLError!", e);
        }
    }

    public Collection<Pair<Model, Model>> getConflicts()
    {
        return conflicts;
    }

    public Collection<Pair<Model, Model>> getNoConflicts()
    {
        return noConflicts;
    }

    private void ignoreOutsourced(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict)
        {
           this.conflicts.remove(pair);
        }
        else
        {
           this.noConflicts.remove(pair);
        }
    }

    private void ignoreSection(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getSection_id()
                            : ((Exhibit) entry.getLeft()).getSection_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Section)
                {
                    id = entry.getLeft() == null
                            ? ((Section) entry.getRight()).getParent_id()
                            : ((Section) entry.getLeft()).getParent_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getSection_id()
                            : ((Exhibit) entry.getLeft()).getSection_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Section)
                {
                    id = entry.getLeft() == null
                            ? ((Section) entry.getRight()).getParent_id()
                            : ((Section) entry.getLeft()).getParent_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    private void ignoreMuseum(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getMuseum_id()
                            : ((Exhibit) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Category)
                {
                    id = entry.getLeft() == null
                            ? ((Category) entry.getRight()).getMuseum_id()
                            : ((Category) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Section)
                {
                    id = entry.getLeft() == null
                            ? ((Section) entry.getRight()).getMuseum_id()
                            : ((Section) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof History)
                {
                    id = entry.getLeft() == null
                            ? ((History) entry.getRight()).getMuseum_id()
                            : ((History) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getMuseum_id()
                            : ((Outsourced) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Role)
                {
                    id = entry.getLeft() == null
                            ? ((Role) entry.getRight()).getMuseum_id()
                            : ((Role) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getMuseum_id()
                            : ((Exhibit) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Category)
                {
                    id = entry.getLeft() == null
                            ? ((Category) entry.getRight()).getMuseum_id()
                            : ((Category) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Section)
                {
                    id = entry.getLeft() == null
                            ? ((Section) entry.getRight()).getMuseum_id()
                            : ((Section) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof History)
                {
                    id = entry.getLeft() == null
                            ? ((History) entry.getRight()).getMuseum_id()
                            : ((History) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getMuseum_id()
                            : ((Outsourced) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Role)
                {
                    id = entry.getLeft() == null
                            ? ((Role) entry.getRight()).getMuseum_id()
                            : ((Role) entry.getLeft()).getMuseum_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    private void ignoreAdress(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Museum)
                {
                    id = entry.getLeft() == null
                            ? ((Museum) entry.getRight()).getAddress_id()
                            : ((Museum) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getAddress_id()
                            : ((Outsourced) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Contact)
                {
                    id = entry.getLeft() == null
                            ? ((Contact) entry.getRight()).getAddress_id()
                            : ((Contact) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Museum)
                {
                    id = entry.getLeft() == null
                            ? ((Museum) entry.getRight()).getAddress_id()
                            : ((Museum) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getAddress_id()
                            : ((Outsourced) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Contact)
                {
                    id = entry.getLeft() == null
                            ? ((Contact) entry.getRight()).getAddress_id()
                            : ((Contact) entry.getLeft()).getAddress_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    private void ignoreCategory(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getCategory_id()
                            : ((Exhibit) entry.getLeft()).getCategory_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Category)
                {
                    id = entry.getLeft() == null
                            ? ((Category) entry.getRight()).getParent_id()
                            : ((Category) entry.getLeft()).getParent_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Exhibit)
                {
                    id = entry.getLeft() == null
                            ? ((Exhibit) entry.getRight()).getCategory_id()
                            : ((Exhibit) entry.getLeft()).getCategory_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
                else if (entry.getLeft() instanceof Category)
                {
                    id = entry.getLeft() == null
                            ? ((Category) entry.getRight()).getParent_id()
                            : ((Category) entry.getLeft()).getParent_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    private void ignoreContact(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getContact_id()
                            : ((Outsourced) entry.getLeft()).getContact_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Outsourced)
                {
                    id = entry.getLeft() == null
                            ? ((Outsourced) entry.getRight()).getContact_id()
                            : ((Outsourced) entry.getLeft()).getContact_id();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }

    private void ignoreHistory(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict) 
        {
            this.conflicts.remove(pair);
        }
        else
        {
            this.noConflicts.remove(pair);
        }
    }

    private void ignoreImage(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict)
        {
            this.conflicts.remove(pair);
        }
        else
        {
            this.noConflicts.remove(pair);
        }
    }

    private void ignoreLabel(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict)
        {
            this.conflicts.remove(pair);
        }
        else
        {
            this.noConflicts.remove(pair);
        }
    }

    private void ignoreRole(Pair<Model, Model> pair, boolean isConflict)
    {
        if (isConflict ? this.conflicts.remove(pair) : this.noConflicts.remove(pair))
        {
            Long outsourced_id = pair.getLeft() == null ? pair.getRight().getId() : pair.getLeft().getId();
            Long id;
            Collection<Pair<Model, Model>> toRemove = new HashSet<>();
            for (Pair<Model, Model> entry : conflicts)
            {
                if (entry.getLeft() instanceof Contact)
                {
                    id = entry.getLeft() == null
                            ? ((Contact) entry.getRight()).getRoleId()
                            : ((Contact) entry.getLeft()).getRoleId();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, true);
            }
            toRemove.clear();
            for (Pair<Model, Model> entry : noConflicts)
            {
                if (entry.getLeft() instanceof Contact)
                {
                    id = entry.getLeft() == null
                            ? ((Contact) entry.getRight()).getRoleId()
                            : ((Contact) entry.getLeft()).getRoleId();
                    if (outsourced_id.equals(id))
                    {
                        toRemove.add(entry);
                    }
                }
            }
            for (Pair<Model, Model> remove : toRemove)
            {
                this.ignore(remove, false);
            }
        }
    }
}
