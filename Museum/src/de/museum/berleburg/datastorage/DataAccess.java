package de.museum.berleburg.datastorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

import de.museum.berleburg.datastorage.backup.DumpCreator;
import de.museum.berleburg.datastorage.backup.InsertBackup;
import de.museum.berleburg.datastorage.interfaces.Model;
import de.museum.berleburg.datastorage.manager.ModelManagement;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.manager.UpdateManager;
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
import de.museum.berleburg.exceptions.AddressHasNoValueException;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.DatabaseDriverNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumIDNotFoundException;
import de.museum.berleburg.exceptions.NotAZipFileException;

/**
 *
 * @author Nils Leonhardt
 */
public class DataAccess
{

    private ModelManagement management;
    private boolean init = false;
    private static DataAccess instance;

    /**
     * Returns the updateManager containing all updates and conflicts for
     * updating the local Database
     *
     * @author Anselm Brehme
     * @return the UpdateManager
     * @throws ConnectionException when the connection is invalid
     */
    public UpdateManager updateLocalDatabase(ProcessCallBack callBack) throws ConnectionException
    {
        UpdateManager manager = new UpdateManager(true);
        this.management.getAddressManager().updateLocalDatabase(manager, callBack);
        this.management.getCategoryManager().updateLocalDatabase(manager, callBack);
        this.management.getContactManager().updateLocalDatabase(manager, callBack);
        this.management.getOutsourcedManager().updateLocalDatabase(manager, callBack);
        this.management.getExhibitManager().updateLocalDatabase(manager, callBack);
        this.management.getHistoryManager().updateLocalDatabase(manager, callBack);
        this.management.getImageManager().updateLocalDatabase(manager, callBack);
        this.management.getMuseumManager().updateLocalDatabase(manager, callBack);
        this.management.getSectionManager().updateLocalDatabase(manager, callBack);
        this.management.getLabelManager().updateLocalDatabase(manager, callBack);
        this.management.getRoleManager().updateLocalDatabase(manager, callBack);
        return manager;
    }

    /**
     * Returns the updateManager containing all updates and conflicts for
     * updating the local Database
     *
     * @author Anselm Brehme
     * @return the Updatemanger
     * @throws ConnectionException when the connection is invalid
     */
    public UpdateManager updateServerDatabase(ProcessCallBack callBack) throws ConnectionException
    {
        UpdateManager manager = new UpdateManager(false);
        this.management.getAddressManager().updateServerDatabase(manager, callBack);
        this.management.getCategoryManager().updateServerDatabase(manager, callBack);
        this.management.getContactManager().updateServerDatabase(manager, callBack);
        this.management.getOutsourcedManager().updateServerDatabase(manager, callBack);
        this.management.getExhibitManager().updateServerDatabase(manager, callBack);
        this.management.getHistoryManager().updateServerDatabase(manager, callBack);
        this.management.getImageManager().updateServerDatabase(manager, callBack);
        this.management.getMuseumManager().updateServerDatabase(manager, callBack);
        this.management.getSectionManager().updateServerDatabase(manager, callBack);
        this.management.getLabelManager().updateServerDatabase(manager, callBack);
        this.management.getRoleManager().updateServerDatabase(manager, callBack);
        return manager;
    }

    /**
     * Tries to initialize the connection if not yet initialized
     *
     * @author Anselm Brehme
     * @throws ConnectionException when failing to establish a connection
     * @throws IOException when failing to read the configurations
     */
    private void doInit() throws ConnectionException
    {
        if (!init)
        {
            try
            {
                this.start(null);
            }
            catch (IOException ignored)
            {
                throw new IllegalStateException("Could not initialize!", ignored);
            }
        }
    }

    private DataAccess()
    {
        this.management = ModelManagement.getInstance();
    }

    /**
     * Returns an instance of the DataAccess
     *
     * @return the instance
     */
    public static DataAccess getInstance()
    {
        if (instance == null)
        {
            instance = new DataAccess();
        }
        return instance;
    }

    /**
     * Load the Config from the File.
     *
     * @author Nils Leonhardt
     *
     * @throws IOException
     */
    private void loadConfig() throws IOException
    {
        File file = new File(Constants.CONFIGURATION_PATH_LOCAL);
        File file2 = new File(Constants.CONFIGURATION_PATH_SERVER);

        if (file.exists())
        {
            Configuration.loadConfigurations(file, file2);
        }
        else
        {
            Configuration.getInstance().setDefault();
            Configuration.saveConfiguration(file, file2);
        }
    }

    // -------------
    // Miscellaneous
    // -------------
    /**
     *
     * @author Nils Leonhardt (comments)
     *
     * @throws IOException Throws error, if Config was unable to save.
     */
    public void stop() throws IOException
    {
        Configuration.saveConfiguration(new File(
                Constants.CONFIGURATION_PATH_LOCAL), new File(
                Constants.CONFIGURATION_PATH_SERVER));
        init = false;
    }

    /**
     *
     * @author Nils Leonhardt (comments)
     *
     * @throws ConnectionException when failing to establish a connection
     * @throws IOException Load Config from File
     */
    public void start(ProcessCallBack callBack) throws IOException, ConnectionException
    {
        loadConfig();
        this.management.start(callBack);// The managers do create their tables if needed before loading
        init = true;
    }

    /**
     *
     * @author Nils Leonhardt (comments)
     *
     * @param file the File, to Save the Backup
     * @param museumID the Museum to save
     * @throws MuseumIDNotFoundException Museum not Found
     * @throws DatabaseDriverNotFoundException Database-Driver not found
     * @throws NotAZipFileException File has to be a zip.
     * @throws AddressHasNoValueException empty Address
     * @throws FileNotFoundException
     */
    public void exportDatabase(File file, Integer museumID)
            throws MuseumIDNotFoundException, DatabaseDriverNotFoundException,
            NotAZipFileException, AddressHasNoValueException,
            FileNotFoundException, ConnectionException
    {

        DumpCreator.backup(file.getAbsolutePath(), (Integer) museumID, null); //TODO do the callback
    }

    public void importDatabase(File file) throws ConnectionException, SQLException
    {
        this.doInit();
        InsertBackup.insertBackup(file.getAbsolutePath(), null); //TODO process update
    }

    // MODEL-METHODS:
    /**
     * Stores all given models into the local database.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param <M> any ModelType
     * @param models the models to store
     * @throws ConnectionException when the connection is invalid
     */
    public <M extends Model> void store(M... models) throws ConnectionException
    {
        this.doInit();
        this.management.store(models);
    }

    /**
     * Update all given models in the local database.
     *
     * @author Anselm (added by Nils Leonhardt)
     * @param <M> any ModelType
     * @param models the models to update
     * @throws ConnectionException when the connection is invalid
     */
    public <M extends Model> void update(M... models) throws ConnectionException
    {
        this.doInit();
        this.management.update(true, true, models);
    }

    /**
     * Marks all given models as deleted.
     *
     * @author Anselm (added by Nils Leonhardt)
     * @param <M> any ModelType
     * @param models the models to mark as deleted
     * @throws ConnectionException when the connection is invalid
     * @throws ModelAlreadyDeletedException
     */
    public <M extends Model> void delete(M... models)
            throws ModelAlreadyDeletedException, ConnectionException
    {
        this.doInit();
        this.management.markAsDeleted(true, models);
    }

    // ADRESS-Model:
    /**
     * Find all Addresses by Museum Name
     *
     * @author Nils Leonhardt
     *
     * @param name the name
     * @return Collection with Addresses. Can't be null!
     */
    public Collection<Address> searchAddressByMuseumName(String name)
    {
        return management.getAddressManager().getAddressByMuseum(
                management.getMuseumManager().getByMuseumName(name));
    }

    /**
     * Get all registered Addresses
     *
     * @author Nils Leonhardt
     *
     * @return Collection with Addresses. Can't be null!
     */
    public Collection<Address> getAllAddress()
    {
        return management.getAddressManager().getAll(true);
    }

    /**
     * Gets an Adress by ID
     *
     * @param id
     * @return the Adress with given ID or null if not found
     */
    public Address getAddressById(Long id)
    {
        return management.getAddressManager().getbyId(id, true);
    }

    // CATEGORY-Models:
    /**
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name
     * @return Collection with Categories. Can't be null!
     */
    public Collection<Category> searchCategoriesByName(String name)
    {
        return management.getCategoryManager().getByName(name);
    }

    /**
     * Get all Categories by MuseumName.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name Name to search
     * @return Collection with Categories. Can't be null!
     * @throws CategoryNotFoundException Unknown Category
     */
    public Collection<Category> searchAllCategoriesByMuseumName(String name)
    {
        return management.getCategoryManager().getCategoryByMuseum(
                management.getMuseumManager().getByMuseumName(name));
    }

    /**
     * Get all registered Categories.
     *
     * @author Nils Leonhardt
     *
     * @return Collection with Categories. Can't be null!
     */
    public Collection<Category> getAllCategories()
    {
        return management.getCategoryManager().getAll(true);
    }

    /**
     * Get Category by ID.
     *
     * @author Nils Leonhardt
     *
     * @param id The ID
     * @return The Category
     */
    public Category getCategoryById(long id)
    {
        return management.getCategoryManager().getbyId(id, true);
    }

    /**
     * Get all Child Categories from this Category.
     *
     * @author Nils Leonhardt
     *
     * @param id The ID
     * @return Collection with Categories. Can't be null!
     */
    public Collection<Category> getChildCategories(long id)
    {
        return management.getCategoryManager().getChildCategories(id);
    }

    /**
     * Get All Categories by Museum, where Parent == null.
     *
     * @author Nils Leonhardt
     *
     * @param id Museum ID
     * @return Collection with Categories where Parent == null!
     */
    public Collection<Category> getAllCategoriesByMuseumId(long id)
    {
        return management.getCategoryManager().getAllByMuseumId(id);
    }
    
    public Collection<Category> getAllSubCategories(long id)
    {
        return management.getCategoryManager().getAllSubCategories(id);
    }

    // CONTACT-Models:
    /**
     * Search Contact by Name. Not CaseSensitive!
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name LastName
     * @param forename ForeName
     * @return Collection with Contacts. Can't be null!
     */
    public Collection<Contact> searchContactByName(String name, String forename)
    {
        return management.getContactManager().getContactByName(name, forename);
    }

    /**
     * Get all registerd Contacts.
     *
     * @author Nils Leonhardt
     *
     * @return Collection with all Contatcs. Can't be null!
     */
    public Collection<Contact> getAllContacts()
    {
        return management.getContactManager().getAll(true);
    }

    /**
     * Get Contact by ID.
     *
     * @author Nils Leonhardt
     *
     * @param id The ID
     * @return Contact
     */
    public Contact getContactById(long id)
    {
        return management.getContactManager().getbyId(id, true);
    }

    /**
     * Get Contacts by Museum ID.
     *
     * @author Nils Leonhardt
     *
     * @param id Museum ID
     * @return Collection with Contacts. Can't be null!
     */
    public Collection<Contact> getContactByMuseumId(long id)
    {
        return this.management.getContactManager().getContactByMuseumId(id);
    }

    // EXHIBIT-Models:
    /**
     * Find Exhibits by Name. Not CaseSensitive.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name Name to find.
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> searchExhibitsByName(String name)
    {
        return management.getExhibitManager().getByName(name);
    }

    /**
     * Get all Exhibits.
     *
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> getAllExhibits()
    {
        return management.getExhibitManager().getAll(true);
    }

    /**
     * Get Exhibit count.
     *
     * @return
     */
    public int getExhibitCount()
    {
        return management.getExhibitManager().getCount();
    }

    /**
     * Get all Exhibits by Section IDs
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param ids Get All Exhibits by Sections
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsBySectionIds(Collection<Long> ids)
    {
        return management.getExhibitManager().getBySectionIds(ids);
    }

    /**
     * Get all Exhibits by Section.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param sections All Sections
     * @return Collection with Sections. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsBySections(Collection<Section> sections)
    {
        return management.getExhibitManager().getBySections(sections);
    }

    /**
     * Get Exhibits by SubSection
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param sectionId ID
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsBySubSections(Long sectionId)
    {
        return this.getAllExhibitsBySections(this.getAllSubSections(sectionId));
    }

    /**
     * Get all Exhibits by Category ID
     *
     * @author Nils Leonhardt
     *
     * @param id The Category-ID
     * @return Collection with Exhibit. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsByCategory(long id)
    {
        return management.getExhibitManager().getByCategory(id);
    }

    /**
     * Get all Exhibits by Museum ID
     *
     * @author Nils Leonhardt
     *
     * @param id Museum ID
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsByMuseum(long id)
    {
        return management.getExhibitManager().getByMuseum(id);
    }

    /**
     * Get all Exhibits by Museum ID where Section is Null.
     *
     * @author Nils Leonhardt
     *
     * @param id Museum ID
     * @return Collection with Exhibits. Can't be null!
     */
    public Collection<Exhibit> getAllExhibitsByMuseumSectionIsNull(long id)
    {
        return management.getExhibitManager().getByMuseumSectionNull(id);
    }

    /**
     * Get Exhibit by ID
     *
     * @author Nils Leonhardt
     *
     * @param id Exhibit ID
     * @return Exhibit
     */
    public Exhibit getExhibitById(long id)
    {
        return management.getExhibitManager().getbyId(id, true);
    }

    /**
     * Get All Exhibits by Section Name.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name Name to Search.
     * @return Collection with Exhibits.
     */
    public Collection<Exhibit> getAllExhibitsBySectionName(String name)
    {
        return management.getExhibitManager().getAllBySectionName(name);
    }

    // IMAGE-Models:
    /**
     * Search Images by Exhibit Name.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name Exhibit Name
     * @return Collection with Images. Can't be null!
     * @throws ConnectionException when the connection is invalid
     */
    public Collection<Image> searchImagesByExhibitName(String name) throws ConnectionException
    {
        return management.getImageManager().getByExhibits(management.getExhibitManager().getByName(name));
    }

    /**
     * Get all Images by Exhibit Id
     *
     * @author Nils Leonhardt
     *
     * @param id The Exhibit ID
     * @return Collection with Images. Can't be null.
     * @throws ConnectionException when the connection is invalid
     */
    public Collection<Image> getAllImagesByExhibit(Long id) throws ConnectionException
    {
        return management.getImageManager().getByExhibitId(id);
    }

    /**
     * Get Images by ID
     *
     * @author Nils Leonhardt
     *
     * @param id Image ID
     * @return Image
     */
    public Image getImageById(Long id)
    {
        return management.getImageManager().getbyId(id, true);
    }

    // MUSEUM-Models:
    /**
     *
     * @author Nils Leonhardt
     *
     * @param name
     * @return
     */
    public Collection<Museum> searchMuseumByName(String name)
    {
        return management.getMuseumManager().getByMuseumName(name);
    }

    /**
     * Get all Museum.
     *
     * @author Nils Leonhardt
     *
     * @return Collection with Museum. Can't be null.
     */
    public Collection<Museum> getAllMuseum()
    {
        return management.getMuseumManager().getAll(true);
    }

    /**
     * Get Museum by ID.
     *
     * @author Nils Leonhardt
     *
     * @param id Museum-ID
     * @return Museum
     */
    public Museum getMuseumById(long id)
    {
        return management.getMuseumManager().getbyId(id, true);
    }

    // OUTSOURCED-Models:
    /**
     * Search Outsourced by Name.
     *
     * @author Nils Leonhardt
     *
     * @param name Outsourcedname
     * @return
     */
    public Collection<Outsourced> searchOutsourcedByName(String name)
    {
        return management.getOutsourcedManager().getByName(name);
    }

    /**
     * Gets all Outsourced
     *
     * @author Nils Leonhardt
     *
     * @return
     */
    public Collection<Outsourced> getAllOutsourced()
    {
        return management.getOutsourcedManager().getAll(true);
    }

    /**
     * Get Outsourced by ID
     *
     * @author Nils Leonhardt
     *
     * @param id Outsourced ID
     * @return Outsourced
     */
    public Outsourced getOutsourcedById(long id)
    {
        return management.getOutsourcedManager().getbyId(id, true);
    }

    // SECTION-Models:
    /**
     * Search Section by Name
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name Section Name
     * @return
     */
    public Collection<Section> searchSectionByName(String name)
    {
        return management.getSectionManager().getByName(name);
    }

    /**
     * Get All Sections by Museum.
     *
     * @author Nils Leonhardt
     *
     * @param id Museum ID
     * @return Collection with Section. Can't be null!
     */
    public Collection<Section> getAllSectionByMuseum(Long id)
    {
        return management.getSectionManager().getByMuseumId(id);
    }

    /**
     * Get all SubSections by Sections
     *
     * @author Nils Leonhardt
     *
     * @param id Parent Section ID
     * @return Collection with SubSections. Can't be null!
     */
    public Collection<Section> getAllSectionsBySection(Long id)
    {
        return management.getSectionManager().getByParentSectionId(id);
    }

    /**
     * Get Sections by ID
     *
     * @author Nils Leonhardt
     *
     * @param id Section ID
     * @return Section
     */
    public Section getSectionById(long id)
    {
        return management.getSectionManager().getbyId(id, true);
    }

    /**
     * Get all SubSections by Section ID
     *
     * @author Nils Leonhardt
     *
     * @param id ParentSection id
     * @return Collection with Section. Can't be null!
     */
    public Collection<Section> getAllSubSections(long id)
    {
        return this.management.getSectionManager().getAllSubSections(id);
    }

    public Collection<Section> getAllSections()
    {
        return this.management.getSectionManager().getAll(true);
    }

    // ROLES-Models:
    /**
     * Get Roll by ID
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param roleId
     * @return Role
     */
    public Role getRoleById(Long roleId)
    {
        return this.management.getRoleManager().getbyId(roleId, true);
    }

    /**
     * Get all Rolle.
     *
     * @author Anselm, Robert (added by Nils Leonhardt)
     *
     * @return Collection with Role. Can't be null.
     */
    public Collection<Role> getAllRoles()
    {
        return this.management.getRoleManager().getAll(true);
    }

    /**
     * Get all Roles by Museum.
     *
     * @author Anselm, Robert (added by Nils Leonhardt)
     *
     * @param id Museum ID
     * @return Collection with Roles. Can't be null!
     */
    public Collection<Role> getAllRolesByMuseumId(long id)
    {
        return this.management.getRoleManager().getAllRolesByMuseumId(id);
    }

    // LABEL-Models:
    /**
     * Get Label by ID.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param id
     * @return
     */
    public Label getLabelById(long id)
    {
        return this.management.getLabelManager().getbyId(id, true);
    }

    /**
     * Get all Labels.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @return Collection with Label. Can't be null!
     */
    public Collection<Label> getAllLabels()
    {
        return this.management.getLabelManager().getAll(true);
    }

    /**
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name
     * @return
     */
    public Collection<Label> searchLabelsByName(String name)
    {
        return this.management.getLabelManager().getByName(name);
    }

    /**
     * Search Labels by Exhibit ID.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param id Exhibit ID
     * @return Collection with Labels. Can't be null!
     */
    public Collection<Label> searchLabelsByExhibitId(long id)
    {
        return this.management.getLabelManager().getByExhibitId(id);
    }

    // HISTORY-Models:
    /**
     * Get History by Exhibit ID.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param id Exhibit Id
     * @return Collection with History. Can't be null!
     */
    public Collection<History> getHistoryByExhibitId(long id)
    {
        return this.management.getHistoryManager().getHistoryByExhibitId(id);
    }

    /**
     * Get Hostory by Id.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param id History Id
     * @return History
     */
    public History getHistoryById(long id)
    {
        return this.management.getHistoryManager().getbyId(id, true);
    }

    /**
     * Get History by Name.
     *
     * @author Anselm (added by Nils Leonhardt)
     *
     * @param name HistoryName
     * @return Collection with History. Can't be null!
     */
    public Collection<History> getHistoryByName(String name)
    {
        return this.management.getHistoryManager().getByName(name);
    }

    public Collection<History> getAllHistory()
    {
        return this.management.getHistoryManager().getAll(true);
    }
}
