package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.interfaces.Model;
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
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 * Holds the manager for all models
 *
 * @author Anselm
 */
public class ModelManagement
{

    private static ModelManagement instance;

    /**
     * Returns the instance of the ModelManagement used in DataAccess
     *
     * @return the ModelManagement
     */
    public static ModelManagement getInstance()
    {
        if (instance == null)
        {
            instance = new ModelManagement();
        }
        return instance;
    }
    private AddressManager addressManager;
    private CategoryManager categoryManager;
    private ContactManager contactManager;
    private OutsourcedManager outsourcedManager;
    private ExhibitManager exhibitManager;
    private HistoryManager historyManager;
    private ImageManager imageManager;
    private MuseumManager museumManager;
    private SectionManager sectionManager;
    private LabelManager labelManager;
    private RoleManager roleManager;

    /**
     * @return the addressManager
     */
    public AddressManager getAddressManager()
    {
        return addressManager;
    }

    /**
     * @return the categoryManager
     */
    public CategoryManager getCategoryManager()
    {
        return categoryManager;
    }

    /**
     * @return the contactManager
     */
    public ContactManager getContactManager()
    {
        return contactManager;
    }

    /**
     * @return the outsourcedManager
     */
    public OutsourcedManager getOutsourcedManager()
    {
        return outsourcedManager;
    }

    /**
     * @return the exhibitManager
     */
    public ExhibitManager getExhibitManager()
    {
        return exhibitManager;
    }

    /**
     * @return the historyManager
     */
    public HistoryManager getHistoryManager()
    {
        return historyManager;
    }

    /**
     * @return the imageManager
     */
    public ImageManager getImageManager()
    {
        return imageManager;
    }

    /**
     * @return the museumManager
     */
    public MuseumManager getMuseumManager()
    {
        return museumManager;
    }

    /**
     * @return the sectionManager
     */
    public SectionManager getSectionManager()
    {
        return sectionManager;
    }

    /**
     * @return the labelManager
     */
    public LabelManager getLabelManager()
    {
        return labelManager;
    }

    /**
     * @return the roleManager
     */
    public RoleManager getRoleManager()
    {
        return roleManager;
    }

    /**
     * Starts all Managers and loads in the models from local database
     *
     * @throws SQLException
     */
    public void start(ProcessCallBack callBack) throws ConnectionException
    {
        Configuration.getInstance().getConnection();
        addressManager = new AddressManager().loadAll(true,callBack);
        museumManager = new MuseumManager().loadAll(true,callBack);
        sectionManager = new SectionManager().loadAll(true,callBack);
        categoryManager = new CategoryManager().loadAll(true,callBack);
        contactManager = new ContactManager().loadAll(true,callBack);
        outsourcedManager = new OutsourcedManager().loadAll(true,callBack);
        exhibitManager = new ExhibitManager().loadAll(true,callBack);
        imageManager = new ImageManager().loadAll(true,callBack);
        labelManager = new LabelManager().loadAll(true,callBack);
        roleManager = new RoleManager().loadAll(true,callBack);
        historyManager = new HistoryManager().loadAll(true,callBack);
    }

    /**
     * Deletes/Removes given model forever! Data cannot be retreived
     *
     * @param <M> any ModelType
     * @param local delete on local database
     * @param models the models to delete
     * @throws SQLException
     */
    public <M extends Model> void remove(boolean local, M... models) throws ConnectionException
    {
        for (M model : models)
        {
            if (model instanceof Address)
            {
                this.addressManager.delete((Address) model, local);
                this.addressManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Category)
            {
                this.categoryManager.delete((Category) model, local);
                this.categoryManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Contact)
            {
                this.contactManager.delete((Contact) model, local);
                this.contactManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Exhibit)
            {
                this.exhibitManager.delete((Exhibit) model, local);
                this.exhibitManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof History)
            {
                this.historyManager.delete((History) model, local);
                this.historyManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Image)
            {
                this.imageManager.delete((Image) model, local);
                this.imageManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Label)
            {
                this.labelManager.delete((Label) model, local);
                this.labelManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Museum)
            {
                this.museumManager.delete((Museum) model, local);
                this.museumManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Outsourced)
            {
                this.outsourcedManager.delete((Outsourced) model, local);
                this.outsourcedManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Section)
            {
                this.sectionManager.delete((Section) model, local);
                this.sectionManager.localDBModels.remove(model.getId());
            }
            else if (model instanceof Role)
            {
                this.roleManager.delete((Role) model, local);
                this.roleManager.localDBModels.remove(model.getId());
            }
        }

    }

    public <M extends Model> void update(boolean local, boolean updateTime, M... models) throws ConnectionException
    {

        for (M model : models)
        {
            if (model instanceof Address)
            {
                this.addressManager.update((Address) model, local, updateTime);
            }
            else if (model instanceof Category)
            {
                this.categoryManager.update((Category) model, local, updateTime);
            }
            else if (model instanceof Contact)
            {
                this.contactManager.update((Contact) model, local, updateTime);
            }
            else if (model instanceof Exhibit)
            {
                this.exhibitManager.update((Exhibit) model, local, updateTime);
            }
            else if (model instanceof History)
            {
                this.historyManager.update((History) model, local, updateTime);
            }
            else if (model instanceof Image)
            {
                this.imageManager.update((Image) model, local, updateTime);
            }
            else if (model instanceof Label)
            {
                this.labelManager.update((Label) model, local, updateTime);
            }
            else if (model instanceof Museum)
            {
                this.museumManager.update((Museum) model, local, updateTime);
            }
            else if (model instanceof Outsourced)
            {
                this.outsourcedManager.update((Outsourced) model, local, updateTime);
            }
            else if (model instanceof Section)
            {
                this.sectionManager.update((Section) model, local, updateTime);
            }
            else if (model instanceof Role)
            {
                this.roleManager.update((Role) model, local, updateTime);
            }
        }

    }

    public <M extends Model> void markAsDeleted(boolean local, M... models) throws ModelAlreadyDeletedException, ConnectionTimeOutException
    {
        for (M model : models)
        {
            if (model instanceof Address)
            {
                this.addressManager.markAsDeleted((Address) model, local);
            }
            else if (model instanceof Category)
            {
                this.categoryManager.markAsDeleted((Category) model, local);
            }
            else if (model instanceof Contact)
            {
                this.contactManager.markAsDeleted((Contact) model, local);
            }
            else if (model instanceof Exhibit)
            {
                this.exhibitManager.markAsDeleted((Exhibit) model, local);
            }
            else if (model instanceof History)
            {
                this.historyManager.markAsDeleted((History) model, local);
            }
            else if (model instanceof Image)
            {
                this.imageManager.markAsDeleted((Image) model, local);
            }
            else if (model instanceof Label)
            {
                this.labelManager.markAsDeleted((Label) model, local);
            }
            else if (model instanceof Museum)
            {
                this.museumManager.markAsDeleted((Museum) model, local);
            }
            else if (model instanceof Outsourced)
            {
                this.outsourcedManager.markAsDeleted((Outsourced) model, local);
            }
            else if (model instanceof Section)
            {
                this.sectionManager.markAsDeleted((Section) model, local);
            }
            else if (model instanceof Role)
            {
                this.roleManager.markAsDeleted((Role) model, local);
            }
        }

    }

    public <M extends Model> void store(M... models) throws ConnectionException
    {
        for (M model : models)
        {
            if (model instanceof Address)
            {
                this.addressManager.store((Address) model);
            }
            else if (model instanceof Category)
            {
                this.categoryManager.store((Category) model);
            }
            else if (model instanceof Contact)
            {
                this.contactManager.store((Contact) model);
            }
            else if (model instanceof Exhibit)
            {
                this.exhibitManager.store((Exhibit) model);
            }
            else if (model instanceof History)
            {
                this.historyManager.store((History) model);
            }
            else if (model instanceof Image)
            {
                this.imageManager.store((Image) model);
            }
            else if (model instanceof Label)
            {
                this.labelManager.store((Label) model);
            }
            else if (model instanceof Museum)
            {
                this.museumManager.store((Museum) model);
            }
            else if (model instanceof Outsourced)
            {
                this.outsourcedManager.store((Outsourced) model);
            }
            else if (model instanceof Section)
            {
                this.sectionManager.store((Section) model);
            }
            else if (model instanceof Role)
            {
                this.roleManager.store((Role) model);
            }
        }
    }
    
    public void addLocalLoaded(Model model)
    {
        if (model instanceof Address)
        {
            this.addressManager.localDBModels.put(model.getId(), (Address) model);
        }
        else if (model instanceof Category)
        {
            this.categoryManager.localDBModels.put(model.getId(), (Category) model);
        }
        else if (model instanceof Contact)
        {
            this.contactManager.localDBModels.put(model.getId(), (Contact) model);
        }
        else if (model instanceof Exhibit)
        {
            this.exhibitManager.localDBModels.put(model.getId(), (Exhibit) model);
        }
        else if (model instanceof History)
        {
            this.historyManager.localDBModels.put(model.getId(), (History) model);
        }
        else if (model instanceof Image)
        {
            this.imageManager.localDBModels.put(model.getId(), (Image) model);
        }
        else if (model instanceof Label)
        {
            this.labelManager.localDBModels.put(model.getId(), (Label) model);
        }
        else if (model instanceof Museum)
        {
            this.museumManager.localDBModels.put(model.getId(), (Museum) model);
        }
        else if (model instanceof Outsourced)
        {
            this.outsourcedManager.localDBModels.put(model.getId(), (Outsourced) model);
        }
        else if (model instanceof Section)
        {
            this.sectionManager.localDBModels.put(model.getId(), (Section) model);
        }
        else if (model instanceof Role)
        {
            this.roleManager.localDBModels.put(model.getId(), (Role) model);
        }
    }
}
