package de.museum.berleburg.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.interfaces.Pair;
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
import de.museum.berleburg.exceptions.AddressNotFoundException;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ContactNotFoundException;
import de.museum.berleburg.exceptions.DatabaseDriverNotFoundException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.HistoryElementNotFoundException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumIDNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.NotAZipFileException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.exceptions.PictureNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.SyncModel;
import de.museum.berleburg.logicAccess.Updatelist;

/**
 *
 * @author Benedikt, Christian, Caroline, Jochen, FSchikowski, Marco
 *
 */
public class LogicManager
{

    private static LogicManager instance;
    private UpdateManager manager;

    /**
     * @author Benedikt
     *
     * Initiates the LogicManager.
     *
     * @throws ConnectionException
     * @throws IOException
     */
    private LogicManager(ProcessCallBack callBack) throws ConnectionException,
            IOException
    {
        DataAccess.getInstance().start(callBack);
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ getter and setter ---------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Singleton design pattern (modified). Instance will be created by
     * startSystem which is necessary to execute before any other method.
     *
     * @return single instance of Manager
     */
    public static LogicManager getInstance()
    {
        return instance;
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ general functions ---------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Parses two strings to a double.
     *
     * @author Benedikt
     * @param euro
     * @param cent
     * @return
     */
    public double parsePrice(String euro, String cent)
    {
        double fullValue = Double.parseDouble(euro);
        return fullValue + Double.parseDouble(cent) / 100;
    }

    /**
     * Parses the double back to two strings.
     *
     * @author Benedikt
     * @param fullValue
     * @return
     */
    public String[] reParsePrice(double fullValue)
    {
        String[] result = new String[2];
        result[0] = (int) fullValue + "";
        result[1] = Long.toString(Math
                .round((fullValue - (int) fullValue) * 100));
        if (result[1].length() < 2)
        {
            result[1] = "0" + result[1];
        }
        return result;
    }

    /**
     * Starts the system and initiates the manager.
     *
     * @author Benedikt
     *
     * @param file the file from where the database is loaded
     * @throws IOException
     * @throws ConnectionException
     */
    public static void startSystem(ProcessCallBack callBack)
            throws ConnectionException, IOException
    {
        instance = new LogicManager(callBack);
    }

    /**
     * Exports the database to a file.
     *
     * @author Benedikt
     *
     * @param file the file
     * @throws AddressHasNoValueException
     * @throws NotAZipFileException
     * @throws DatabaseDriverNotFoundException
     * @throws MuseumIDNotFoundException
     * @throws FileNotFoundException
     */
    public void exportDatabase(File file, Integer museum_id)
            throws ConnectionException, FileNotFoundException,
            MuseumIDNotFoundException, DatabaseDriverNotFoundException,
            NotAZipFileException, AddressHasNoValueException
    {
        DataAccess.getInstance().exportDatabase(file, museum_id);
    }

    /**
     * Imports the database from a file and loads everything into the program.
     *
     * @author Benedikt
     *
     * @param file the file
     * @throws ConnectionException
     */
    public void importDatabase(File file) throws SQLException,
            ConnectionException
    {
        DataAccess.getInstance().importDatabase(file);
    }

    /**
     * Prepares The Commit.
     *
     * @author Marco
     * @return
     * @throws ConnectionException
     */
    public Updatelist prepareCommit(ProcessCallBack callBack)
            throws ConnectionException
    {
        manager = DataAccess.getInstance().updateServerDatabase(callBack);
        Updatelist local = new Updatelist(manager, true);
        return local;
    }

    /**
     * Finalizes the Commit.
     *
     * @author Marco
     * @throws ConnectionException
     */
    public boolean commit(ProcessCallBack updateCallBack)
            throws ConnectionException
    {
        return manager.finalizeUpdate(updateCallBack);
    }

    /**
     * Prepares the Update.
     *
     * @author Marco
     * @return
     * @throws ConnectionException
     */
    public Updatelist prepareUpdate(ProcessCallBack callBack)
            throws ConnectionException
    {
        manager = DataAccess.getInstance().updateLocalDatabase(callBack);
        Updatelist server = new Updatelist(manager, false);
        return server;
    }

    /**
     * Finalizes the Update.
     *
     * @author Marco
     * @throws ConnectionException
     */
    public boolean update(ProcessCallBack updateCallBack)
            throws ConnectionException
    {
        return manager.finalizeUpdate(updateCallBack);
    }

    /**
     * @author Caroline
     * @param rolde_id
     * @return if role is used in a contact
     *
     */
    public boolean roleIsUsed(Long role_id)
    {
        for (Contact step : DataAccess.getInstance().getAllContacts())
        {
            if (role_id.equals(step.getRoleId()))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ add functions -------------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Inserts a museum.
     *
     * @param toAdd
     * @return the id of the museum
     * @throws ConnectionException
     */
    public Long insertMuseum(Museum toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts a section.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertSection(Section toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts an exhibit.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertExhibit(Exhibit toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts a category.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertCategory(Category toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts an outsourced.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertOutsourced(Outsourced toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Adds an exhibit to an outsourced.
     *
     * @param toAdd
     * @param outsourced
     * @return
     * @throws ConnectionException
     */
    public Long addToOutsourced(Exhibit toAdd, Outsourced outsourced) throws ConnectionException
    {
        if (DataAccess.getInstance().getOutsourcedById(outsourced.getId()) == null
         || DataAccess.getInstance().getOutsourcedById(outsourced.getId()).getId() == 0L)
        {
            insertOutsourced(outsourced);
        }
        outsourced.getExhibitIds().put(toAdd.getId(), null);
        ExhibitLogic.saveExhibit(toAdd); // update for history
        OutsourcedLogic.saveOutsourced(outsourced);
        return outsourced.getId();
    }

    /**
     * Inserts an address.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertAddress(Address toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts a contact.
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertContact(Contact toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Inserts a picture.
     *
     * @param picture
     * @return
     * @throws ConnectionException
     */
    public Long insertPicture(Image picture) throws ConnectionException
    {
        DataAccess.getInstance().store(picture);
        return picture.getId();
    }

    /**
     * Inserts a Label.
     *
     * @author Marco
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertLabel(Label toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /**
     * Adds an Exhibit to a Label.
     *
     * @author Marco
     *
     * @param toAdd
     * @param label
     * @return
     * @throws ConnectionException
     */
    public Long addToLabel(Exhibit toAdd, Label label)
            throws ConnectionException
    {
        if (DataAccess.getInstance().getLabelById(label.getId()) == null)
        {
            insertLabel(label);
        }
        List<Long> exhibitList = label.getExhibit_ids();
        exhibitList.add(toAdd.getId());
        label.setExhibit_ids(exhibitList);
        DataAccess.getInstance().update(label);
        return label.getId();
    }

    /**
     * Inserts a role.
     *
     * @author Benedikt
     *
     * @param toAdd
     * @return
     * @throws ConnectionException
     */
    public Long insertRole(Role toAdd) throws ConnectionException
    {
        DataAccess.getInstance().store(toAdd);
        return toAdd.getId();
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ update functions ----------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Copies a categoryTree.
     *
     * @author Benedikt
     * @author Fschikowski
     *
     * @param toCopy
     * @param museum_id
     * @return
     * @throws ConnectionException
     * @throws CategoryNotFoundException
     * @throws InvalidArgumentsException
     * @throws MuseumNotFoundException
     */
    public ArrayList<Long> copyCategories(ArrayList<Category> toCopy,
            long museum_id) throws ConnectionException,
            MuseumNotFoundException, InvalidArgumentsException
    {
        ArrayList<String> parentNames = new ArrayList<String>();
        ArrayList<Category> added = new ArrayList<Category>();
        ArrayList<Long> ids = new ArrayList<Long>();
        for (Category step : toCopy)
        {
            if (CategoryLogic.hasParent(step))
            {
                try
                {
                    parentNames.add(LogicManager.getInstance()
                            .searchCategoryById(step.getParent_id()).getName());
                }
                catch (CategoryNotFoundException e)
                {
                    // all ok.
                    e.printStackTrace();
                }
            }
            else
            {
                parentNames.add("");
            }
        }
        for (Category step : toCopy)
        {
            Category toAdd = new Category(step.getName(), museum_id, null);
            ids.add(this.insertCategory(toAdd));
            added.add(toAdd);
        }

        Iterator<String> stringIter = parentNames.iterator();
        Iterator<Category> catIter = toCopy.iterator();
        for (Category step : added)
        {
            Long parent_id = null;
            String toCheck = stringIter.next();
            if (CategoryLogic.hasParent(catIter.next()))
            {
                for (Category parent : added)
                {
                    if (parent.getName().equals(toCheck))
                    {
                        parent_id = parent.getId();
                    }
                }
                try
                {
                    this.changeCategory(step, step.getName(),
                            step.getMuseum_id(), parent_id);
                }
                catch (CategoryNotFoundException e)
                {
                    // all ok
                    e.printStackTrace();
                }
            }
        }
        return ids;
    }

    /**
     * Changes the fields of a museum.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param description
     * @throws ConnectionException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void changeMuseum(Museum toChange, String name, String description)
            throws FileNotFoundException, IOException, ConnectionException
    {
        toChange.setName(name);
        toChange.setDescription(description);
        MuseumLogic.saveMuseum(toChange);
    }

    /**
     * Changes the fields of a section.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param description
     * @param parent_id
     * @param museum_id
     * @throws ConnectionException
     * @throws MuseumNotFoundException
     * @throws CategoryNotFoundException
     */
    public void changeSection(Section toChange, String name,
            String description, Long parent_id, long museum_id)
            throws ConnectionException, CategoryNotFoundException,
            MuseumNotFoundException
    {
        if (museum_id != toChange.getMuseum_id())
        {
            ArrayList<Section> childs = LogicManager
                    .getAllSectionsBySection(toChange);
            ArrayList<Exhibit> changeCategory = new ArrayList<>();
            changeCategory.addAll(LogicManager
                    .getAllExhibitsBySection(toChange));
            for (Section step : childs)
            {
                changeCategory.addAll(LogicManager
                        .getAllExhibitsBySection(step));
            }
            for (Exhibit step : changeCategory)
            {
                step.setMuseum_id(museum_id);
                step.setCategory_id(this.getMiscellaneousCategory(
                        this.searchMuseumById(museum_id)).getId());
                ExhibitLogic.saveExhibit(step);
            }
        }
        toChange.setName(name);
        toChange.setDescription(description);
        toChange.setParent_id(parent_id);
        toChange.setMuseum_id(museum_id);
        SectionLogic.saveSection(toChange);
    }

    /**
     * Changes the fields of an exhibit.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param description
     * @param section_id
     * @param category_id
     * @param count
     * @param rfid
     * @param museum_id
     * @param outsourced_id
     * @param price
     * @throws ConnectionException
     */
    public void changeExhibit(Exhibit toChange, String name,
            String description, Long section_id, Long category_id, long count,
            String rfid, long museum_id, double price)
            throws ConnectionException
    {
        toChange.setName(name);
        toChange.setDescription(description);
        toChange.setSection_id(section_id);
        toChange.setCategory_id(category_id);
        toChange.setCount(count);
        toChange.setRfid(rfid);
        toChange.setMuseum_id(museum_id);
        toChange.setWert(price);
        ExhibitLogic.saveExhibit(toChange);
    }

    /**
     * Changes the fields of a category.
     *
     * @author Benedikt
     * @author Marco
     *
     * @param toChange
     * @param museum_id
     * @param parent_id
     * @throws ConnectionException
     * @throws MuseumNotFoundException
     * @throws CategoryNotFoundException
     * @throws InvalidArgumentsException
     */
    public void changeCategory(Category toChange, String name, long museum_id,
            Long parent_id) throws ConnectionException,
            CategoryNotFoundException, MuseumNotFoundException,
            InvalidArgumentsException
    {

        Category parentCategory = LogicManager.getInstance()
                .searchCategoryById(parent_id);
        Category miscellaneousCategory = LogicManager.getInstance()
                .getMiscellaneousCategory(toChange.getMuseum());

        if (parentCategory != null
                && parentCategory.equals(miscellaneousCategory))
        {
            throw new InvalidArgumentsException(
                    "Unter der Kategorie \"Sonstiges\" kann keine Unterkategorie erstellt werden!");
        }

        if (toChange.equals(miscellaneousCategory))
        {
            throw new InvalidArgumentsException(
                    "Die Standard-Kategorie \"Sonstiges\" darf nicht bearbeitet werden!");
        }

        toChange.setName(name);
        toChange.setMuseum_id(museum_id);
        toChange.setParent_id(parent_id);
        CategoryLogic.saveCategory(toChange);
    }

    /**
     * Changes the fields of an image.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param image
     * @param name
     * @param exhibit_id
     * @throws IOException
     * @throws ConnectionException
     */
    public void changePicture(Image toChange, byte[] image, String name,
            long exhibit_id) throws IOException, ConnectionException
    {
        toChange.setRawImage(image);
        toChange.setName(name);
        toChange.setExhibit_id(exhibit_id);
        ImageLogic.savePicture(toChange);
    }

    /**
     * Changes the fields of an address.
     *
     * @param toChange
     * @param street
     * @param housenumber
     * @param zipcode
     * @param town
     * @param state
     * @param country
     * @throws ConnectionException
     */
    public void changeAddress(Address toChange, String street,
            String housenumber, String zipcode, String town, String state,
            String country) throws ConnectionException
    {
        toChange.setStreet(street);
        toChange.setHousenumber(housenumber);
        toChange.setZipcode(zipcode);
        toChange.setTown(town);
        toChange.setState(state);
        toChange.setCountry(country);
        DataAccess.getInstance().update(toChange);
    }

    /**
     * Changes the fields of a contact.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param forename
     * @param fon
     * @param email
     * @param museum_id
     * @param description
     * @param fax
     * @throws ConnectionException
     */
    public void changeContact(Contact toChange, String name, String forename,
            String fon, String email, String description, String fax,
            Long role_id, long address_id) throws ConnectionException
    {
        toChange.setName(name);
        toChange.setForename(forename);
        toChange.setFon(fon);
        toChange.setEmail(email);
        toChange.setDescription(description);
        toChange.setFax(fax);
        toChange.setRoleId(role_id);
        toChange.setAddress_id(address_id);
        DataAccess.getInstance().update(toChange);
    }

    /**
     * Changes the fields of a label.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @throws ConnectionException
     */
    public void changeLabel(Label toChange, String name)
            throws ConnectionException
    {
        toChange.setName(name);
        DataAccess.getInstance().update(toChange);
    }

    /**
     * Changes the fields of an outsourced.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param description
     * @param startDate
     * @param endDate
     * @param Address_id
     * @param contact_id
     * @throws ConnectionException
     */
    public void changeOutsourced(Outsourced toChange, String name,
            String description, Date startDate, Date endDate, Long Address_id,
            Long contact_id) throws ConnectionException
    {
        toChange.setName(name);
        toChange.setDescription(description);
        toChange.setStartDate(startDate);
        toChange.setEndDate(endDate);
        toChange.setAddress_id(Address_id);
        toChange.setContact_id(contact_id);
        OutsourcedLogic.saveOutsourced(toChange);
    }

    /**
     * Changes the fields of a role.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param name
     * @param museum_id
     * @throws ConnectionException
     */
    public static void changeRole(Role toChange, String name, long museum_id)
            throws ConnectionException
    {
        toChange.setName(name);
        toChange.setMuseum_id(museum_id);
        DataAccess.getInstance().update(toChange);
    }

    /**
     * Checks if no exhibit is in the outsourced.
     *
     * @author Benedikt
     *
     * @param toCheck
     * @return
     */
    public boolean isEveryThingBack(Outsourced toCheck)
    {
        return OutsourcedLogic.isEveryThingBack(toCheck);
    }

    /**
     * Checks if the outsourced is expired.
     *
     * @author Benedikt
     *
     * @param toCheck
     * @return
     */
    public boolean isExpired(Outsourced toCheck)
    {
        return OutsourcedLogic.isExpired(toCheck);
    }

    /**
     * Checks if the outsourced is permanent outsourced.
     *
     * @author Benedikt
     *
     * @param toCheck
     * @return
     */
    public boolean isPermanentOutsourced(Outsourced toCheck)
    {
        return OutsourcedLogic.isPermanentOutsourced(toCheck);
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ delete functions ----------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Deletes a museum.
     *
     * @author Benedikt
     *
     * @param id the id of the museum
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void deleteMuseum(Long id) throws ModelAlreadyDeletedException,
            ConnectionException
    {
        MuseumLogic.deleteMuseum(DataAccess.getInstance().getMuseumById(id));
    }

    /**
     * Deletes a section.
     *
     * @author Benedikt
     *
     * @param id the id of the section
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void deleteSection(Long id) throws ModelAlreadyDeletedException,
            ConnectionException
    {
        SectionLogic.deleteSection(DataAccess.getInstance().getSectionById(id));
    }

    /**
     * Deletes an exhibit.
     *
     * @author Benedikt
     *
     * @param id the id of the exhibit
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void deleteExhibit(Long id) throws ModelAlreadyDeletedException,
            ConnectionException
    {
        ExhibitLogic.deleteExhibit(DataAccess.getInstance().getExhibitById(id));

    }

    /**
     * Removes an exhibit from outsourced.
     *
     * @author Benedikt
     *
     * @param exhibit_id
     * @return
     * @throws ConnectionException
     */
    public void removeFromOutsourced(Long exhibit_id, Outsourced outsourced)
            throws ConnectionException
    {
        if (outsourced.getExhibitIds().containsKey(exhibit_id))
        {
            if (outsourced.getExhibitIds().get(exhibit_id) == null) // if already given back
            {
                outsourced.getExhibitIds().put(exhibit_id, new Timestamp(System.currentTimeMillis()));
                OutsourcedLogic.saveOutsourced(outsourced);
                ExhibitLogic.saveExhibit(DataAccess.getInstance().getExhibitById(exhibit_id));
            }
        }
    }

    /**
     * Deletes a category.
     *
     * @author Benedikt
     *
     * @param id the id of the category
     * @throws ModelAlreadyDeletedException
     * @throws ConnectionException
     * @throws InvalidArgumentsException
     * @throws CategoryNotFoundException
     */
    public void deleteCategory(Long id) throws ConnectionException,
            ModelAlreadyDeletedException, InvalidArgumentsException,
            CategoryNotFoundException
    {
        Category toDelete = DataAccess.getInstance().getCategoryById(id);
        if (toDelete.getName().equals("Sonstiges"))
        {
            throw new InvalidArgumentsException(
                    "Die Kategorie mit dem Namen \"Sonstiges\" kann nicht gelöscht werden!");
        }
        CategoryLogic.delete(toDelete);
    }

    /**
     * Deletes an Address.
     *
     * @author Benedikt
     *
     * @param id
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     * @throws IntegrityException
     */
    public void deleteAddress(Long id) throws ModelAlreadyDeletedException,
            ConnectionException, IntegrityException
    {
        Address toDelete = DataAccess.getInstance().getAddressById(id);
        ArrayList<Contact> contacts = new ArrayList<>(DataAccess.getInstance()
                .getAllContacts());
        ArrayList<Museum> museums = new ArrayList<>(DataAccess.getInstance()
                .getAllMuseum());
        ArrayList<Outsourced> outsourced = new ArrayList<>(DataAccess
                .getInstance().getAllOutsourced());
        boolean inUse = false;
        for (Contact step : contacts)
        {
            if (step.getAddress_id() == id)
            {
                inUse = true;
            }
        }
        if (inUse)
        {
            throw new IntegrityException(toDelete,
                    "Die Addresse kann nicht gelöscht werden, weil sie derzeit noch benutzt wird. ");
        }
        for (Museum step : museums)
        {
            if (step.getAddress_id() == id)
            {
                inUse = true;
            }
        }
        if (inUse)
        {
            throw new IntegrityException(toDelete,
                    "Die Addresse kann nicht gelöscht werden, weil sie derzeit noch benutzt wird. ");
        }
        for (Outsourced step : outsourced)
        {
            if (step.getAddress_id() == id)
            {
                inUse = true;
            }
        }
        if (inUse)
        {
            throw new IntegrityException(toDelete,
                    "Die Addresse kann nicht gelöscht werden, weil sie derzeit noch benutzt wird. ");
        }
        DataAccess.getInstance().delete(toDelete);
    }

    /**
     * Deletes a loan.
     *
     * @author Benedikt
     *
     * @param id the id of the loan
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     * @throws IntegrityException
     */
    public void deleteOutsourced(Long id) throws ModelAlreadyDeletedException,
            ConnectionException, IntegrityException
    {
        OutsourcedLogic.deleteOutsourced(DataAccess.getInstance()
                .getOutsourcedById(id));
    }

    /**
     * Deletes a picture.
     *
     * @author Benedikt
     *
     * @param id the id of the picture
     * @throws Exception
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void deletePicture(Long id) throws ModelAlreadyDeletedException,
            ConnectionException, Exception
    {
        DataAccess.getInstance().delete(
                DataAccess.getInstance().getImageById(id));
    }

    /**
     * Deletes a contact detail.
     *
     * @author Benedikt
     *
     * @param id the id of the contact detail
     * @throws ContactNotFoundException
     * @throw ConnectionException
     * @throws ModelAlreadyDeletedExceptio
     */
    public void deleteContactDetail(Long id)
            throws ModelAlreadyDeletedException, ConnectionException,
            ContactNotFoundException, IntegrityException
    {
        Collection<Outsourced> alloutsourced = DataAccess.getInstance()
                .getAllOutsourced();
        for (Outsourced o : alloutsourced)
        {
            if (o.getContact_id() != null && o.getContact_id().equals(id))
            {
                throw new IntegrityException(null,
                        "Der Kontakt kann nicht gelöscht werden, da er noch benutzt wird.");
            }
        }
        DataAccess.getInstance().delete(searchContactDetailById(id));
    }

    /**
     * Deletes a Label.
     *
     * @author Marco
     *
     * @param id
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void deleteLabel(Long id) throws ModelAlreadyDeletedException,
            ConnectionException
    {
        DataAccess.getInstance().delete(
                DataAccess.getInstance().getLabelById(id));
    }

    /**
     * Removes an exhibit from a label.
     *
     * @author Marco
     *
     * @param exhibitId
     * @param label
     * @return
     * @throws ConnectionException
     */
    public Long removeFromLabel(Long exhibitId, Label label)
            throws ConnectionException
    {
        List<Long> exhibitList = label.getExhibit_ids();
        exhibitList.remove(exhibitId);
        label.setExhibit_ids(exhibitList);
        DataAccess.getInstance().update(label);
        return label.getId();
    }

    /**
     * Deletes a role.
     *
     * @author Benedikt
     *
     * @param id
     * @throws ModelAlreadyDeletedException
     * @throws ConnectionException
     * @throws IntegrityException
     */
    public void deleteRole(Long id) throws ModelAlreadyDeletedException,
            ConnectionException, IntegrityException
    {
        for (Contact step : DataAccess.getInstance().getAllContacts())
        {
            if (id.equals(step.getRoleId()))
            {
                throw new IntegrityException(
                        step,
                        "Die gewaehlte Rolle wird noch benutzt und kann deshalb nicht geloescht werden.");
            }
        }

        DataAccess.getInstance().delete(searchRoleById(id));
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ mass changes --------------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Removes exhibits from an outsourced.
     *
     * @author Benedikt
     *
     * @param exhibit_ids
     * @throws ConnectionException
     */
    public void massRemoveFromOutsourced(ArrayList<Long> exhibit_ids)
            throws ConnectionException
    {
        for (Long id : exhibit_ids)
        {
            for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
            {
                if (outsourced.getExhibitIds().containsKey(id))
                {
                    this.removeFromOutsourced(id, outsourced);
                }
            }
        }
    }

    /**
     * Deletes exhibits.
     *
     * @author Ralf Heukäufer
     *
     * @param dlist
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     */
    public void massExhibitDelete(ArrayList<Long> dlist)
            throws ModelAlreadyDeletedException, ConnectionException
    {
        for (Long s : dlist)
        {
            LogicManager.getInstance().deleteExhibit(s);
        }
    }

    /**
     * Changes a section of exhibits.
     *
     * @author Ralf Heukäufer
     *
     * @param list
     */
    public void massExhibitSectionChange(ArrayList<Long> list, Long sectionID)
    {
        for (Long s : list)
        {
            if (s != sectionID)
            {
                DataAccess.getInstance().getExhibitById(s)
                        .setSection_id(sectionID);
            }
        }
    }

    /**
     * Changes a category exhibits.
     *
     * @author Ralf Heukäufer
     *
     * @param list
     */
    public void massExhibitCategoryChange(ArrayList<Long> list, Long categoryID)
    {
        for (Long s : list)
        {
            if (s != categoryID)
            {
                DataAccess.getInstance().getExhibitById(s)
                        .setCategory_id(categoryID);

            }
        }
    }

    /**
     * Changes section or museum of exhibits.
     *
     * @author Benedikt
     *
     * @param toChange
     * @param target
     * @throws IntegrityException
     * @throws ConnectionException
     * @throws Exception
     */
    public void massSectionChange(ArrayList<Exhibit> toChange, Object target)
            throws IntegrityException, ConnectionException
    {
        if (target == null)
        {
            throw new IntegrityException(target, "Es wurde kein Ziel angegeben");
        }
        if (target instanceof Section)
        {
            for (Exhibit exhibit : toChange)
            {
                exhibit.setMuseum_id(((Section) target).getMuseum_id());
                exhibit.setSection_id(((Section) target).getId());
                ExhibitLogic.saveExhibit(exhibit);
            }
        }
        else if (target instanceof Museum)
        {
            for (Exhibit exhibit : toChange)
            {
                exhibit.setMuseum_id(((Museum) target).getId());
                exhibit.setSection_id(null);
                ExhibitLogic.saveExhibit(exhibit);
            }
        }
        else
        {
            throw new IntegrityException(target, "Das Ziel vom Typ \""
                    + target.getClass().getSimpleName()
                    + "\" kann nicht verwendet werden!");
        }
    }

    /**
     * Change outsourced of exhibits.
     *
     * @author Caroline Bender
     *
     * @param toChange
     * @param target
     * @throws ConnectionException
     */
    public void massAddOutsourced(ArrayList<Exhibit> toChange, Outsourced target)
            throws ConnectionException
    {
        for (Exhibit exhibit : toChange)
        {
            ExhibitLogic.moveToOutsourced(exhibit, target);
            ExhibitLogic.saveExhibit(exhibit);
        }
    }

    /**
     * Changes category of exhibits.
     *
     * @author Jochen Saßmannshausen
     *
     * @param toChange
     * @param target
     * @throws ConnectionException
     * @throws Exception
     */
    public void massChangeCategory(ArrayList<Exhibit> toChange, Category target)
            throws ConnectionException
    {
        for (Exhibit exhibit : toChange)
        {
            ExhibitLogic.moveToCategory(exhibit, target);
            ExhibitLogic.saveExhibit(exhibit);
        }
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ move functions ------------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Moves a section to the target.
     *
     * @author Benedikt
     *
     * @param toMove
     * @param target
     * @throws IntegrityException
     * @throws ConnectionException
     * @throws IOException
     * @throws FileNotFoundException
     * @throws IntegrityException
     */
    public void moveSection(Section toMove, Object target)
            throws IntegrityException, ConnectionException,
            FileNotFoundException, IOException, IntegrityException
    {
        if (target == null)
        {
            throw new IntegrityException(target, "Es wurde kein Ziel angegeben");
        }
        if (target instanceof Section)
        {
            SectionLogic.moveSection((Section) target, toMove);
            SectionLogic.saveSection((Section) target);
        }
        else if (target instanceof Museum)
        {
            MuseumLogic.addSubSection((Museum) target, toMove);
            MuseumLogic.saveMuseum((Museum) target);
        }
        else
        {
            throw new IntegrityException(target, "Kann Sektion \""
                    + toMove.getName() + "\" nicht zum Ziel vom Typ \""
                    + target.getClass().getSimpleName() + "\" verschieben!");
        }
    }

    /**
     * Moves an exhibit to the target.
     *
     * @author Benedikt
     *
     * @param toMove
     * @param target
     * @throws IntegrityException
     * @throws ConnectionException
     * @throws MuseumNotFoundException
     * @throws CategoryNotFoundException
     */
    public void moveExhibit(Exhibit toMove, Object target)
            throws IntegrityException, ConnectionException,
            CategoryNotFoundException, MuseumNotFoundException
    {
        if (target == null)
        {
            throw new IntegrityException(target, "Es wurde kein Ziel angegeben");
        }
        if (target.getClass().getSimpleName().equals("Section"))
        {
            ExhibitLogic.moveToSection(toMove, (Section) target);
            ExhibitLogic.saveExhibit(toMove);
        }
        else
        {
            ExhibitLogic.moveToMuseum(toMove, (Museum) target);
        }
    }

    /**
     * Moves a category to another.
     *
     * @author Benedikt
     *
     * @param toMove
     * @param target
     * @throws ConnectionException
     * @throws IntegrityException
     */
    public void moveCategory(Category toMove, Category target)
            throws ConnectionException, IntegrityException
    {
        /* target=null, if move to museum */

        CategoryLogic.move(toMove, target);
        CategoryLogic.saveCategory(toMove);
    }

    /**
     * Moves an exhibit to an outsourced.
     *
     * @author Benedikt
     *
     * @param toMove
     * @param target
     * @throws ConnectionException
     */
    public void moveExhibitToOutsourced(Exhibit toMove, Outsourced target)
            throws ConnectionException
    {
        ExhibitLogic.moveToOutsourced(toMove, target);
        ExhibitLogic.saveExhibit(toMove);
    }

    /**
     * Moves an exhibit to target category.
     *
     * @author Benedikt
     *
     * @param exhibit
     * @param target
     * @throws ConnectionException
     */
    public void moveToCategory(Exhibit exhibit, Category target)
            throws ConnectionException
    {
        ExhibitLogic.moveToCategory(exhibit, target);
        ExhibitLogic.saveExhibit(exhibit);
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ get all functions ---------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Gets all subsections of another section.
     *
     * @author Benedikt
     *
     * @param section
     * @return
     */
    public static ArrayList<Section> getAllSectionsBySection(Section section)
    {
        ArrayList<Section> local = new ArrayList<Section>(DataAccess
                .getInstance().getAllSectionsBySection(section.getId()));
        Collections.sort(local, new SectionComparator());
        return local;
    }

    /**
     * Gets all sections of a museum.
     *
     * @author Benedikt
     *
     * @param museum
     * @return
     */
    public static ArrayList<Section> getAllSectionsByMuseum(Museum museum)
    {
        ArrayList<Section> local = new ArrayList<Section>(DataAccess
                .getInstance().getAllSectionByMuseum(museum.getId()));
        Collections.sort(local, new SectionComparator());
        return local;
    }

    /**
     * Gets all exhibits which are directly contained by a museum.
     *
     * @author Benedikt
     *
     * @param museum_id
     * @return
     */
    public static ArrayList<Exhibit> getAllExhibitsByMuseumSectionIsNull(
            long museum_id)
    {
        return new ArrayList<Exhibit>(DataAccess.getInstance()
                .getAllExhibitsByMuseumSectionIsNull(museum_id));
    }

    /**
     * Gets all exhibits of a section.
     *
     * @author Benedikt
     *
     * @param museum
     * @return
     */
    public static ArrayList<Exhibit> getAllExhibitsBySection(Section section)
    {
        return new ArrayList<Exhibit>(DataAccess.getInstance()
                .getAllExhibitsBySectionIds(Arrays.asList(section.getId())));
    }

    /**
     * Gets all exhibits in a museum.
     *
     * @author Benedikt
     *
     * @param museum
     * @return
     */
    public static ArrayList<Exhibit> getAllExhibitsByMuseum(Museum museum)
    {
        ArrayList<Exhibit> result = new ArrayList<Exhibit>();
        if (museum == null)
        {
            return result;
        }
        else
        {
            for (Exhibit step : DataAccess.getInstance().getAllExhibits())
            {
                if (museum.getId().equals(step.getMuseum_id()))
                {
                    result.add(step);
                }
            }
            return result;
        }
    }

    /**
     * Gets all museums.
     *
     * @author Benedikt
     * @author Marco
     *
     * @return all museums
     */
    public static ArrayList<Museum> getAllMuseums()
    {
        ArrayList<Museum> result = new ArrayList<Museum>(DataAccess
                .getInstance().getAllMuseum());
        Collections.sort(result,
                LogicManager.getInstance().new MuseumComparator());
        return result;
    }

    /**
     * Gets all sections.
     *
     * @author Benedikt
     *
     * @return all sections
     */
    public static ArrayList<Section> getAllSections(Museum museum)
    {
        return new ArrayList<Section>(DataAccess.getInstance()
                .getAllSectionByMuseum(museum.getId()));
    }

    /**
     * Gets all exhibits by category.
     *
     * @author Benedikt
     *
     * @param category_id
     * @return
     */
    public static ArrayList<Exhibit> getAllExhibitsByCategory(long category_id)
    {
        return new ArrayList<Exhibit>(DataAccess.getInstance()
                .getAllExhibitsByCategory(category_id));
    }

    /**
     * Gets all exhibits by outsourced.
     *
     * @author Benedikt
     *
     * @param outsourced_id
     * @return
     */
    public static ArrayList<Exhibit> getAllExhibitsByOutsourced(long outsourced_id)
    {
        Outsourced outsourced = DataAccess.getInstance().getOutsourcedById(outsourced_id);
        ArrayList<Exhibit> result = new ArrayList<Exhibit>();
        for (Long key : outsourced.getExhibitIds().keySet())
        {
            result.add(DataAccess.getInstance().getExhibitById(key));
        }
        return result;
    }

    /**
     * Gets all address.
     *
     * @author Benedikt
     * @author Marco
     *
     * @return all address
     */
    public static ArrayList<Address> getAllAddress()
    {
        ArrayList<Address> result = new ArrayList<Address>(DataAccess
                .getInstance().getAllAddress());
        Collections.sort(result,
                LogicManager.getInstance().new AddressComparator());
        return result;
    }

    /**
     * Gets all contacts.
     *
     * @author Caroline
     *
     * @return all contacts
     */
    public static ArrayList<Contact> getAllContact()
    {
        ArrayList<Contact> contacts = new ArrayList<Contact>(DataAccess
                .getInstance().getAllContacts());
        Collections.sort(contacts,
                LogicManager.getInstance().new ContactComparator());
        return contacts;
    }

    /**
     * Gets all categories by a museum.
     *
     * @author Benedikt
     * @author Marco
     *
     * @return all categories by a museum
     */
    public static ArrayList<Category> getAllCategoriesByMuseum(Museum museum)
    {
        ArrayList<Category> result = new ArrayList<Category>(DataAccess
                .getInstance().getAllCategoriesByMuseumId(museum.getId()));
        Collections.sort(result,
                LogicManager.getInstance().new CategoryCategoryComparator());
        return result;
    }

    /**
     * Gets all categories by a category.
     *
     * @author Benedikt
     * @author Marco
     *
     * @param category
     * @return
     */
    public static ArrayList<Category> getAllCategoriesByCategory(
            Category category)
    {
        ArrayList<Category> result = new ArrayList<Category>(DataAccess
                .getInstance().getChildCategories(category.getId()));
        Collections.sort(result,
                LogicManager.getInstance().new CategoryCategoryComparator());
        return result;
    }

    /**
     * Gets all outsourced.
     *
     * @author Benedikt
     * @author Marco
     *
     * @return all outsourced
     */
    public static ArrayList<Outsourced> getAllOutsourced(Museum museum)
    {
        ArrayList<Outsourced> local = new ArrayList<Outsourced>(DataAccess
                .getInstance().getAllOutsourced());
        ArrayList<Outsourced> result = new ArrayList<Outsourced>();
        for (Outsourced step : local)
        {
            if (step != null && step.getMuseum_id() == museum.getId())
            {
                result.add(step);
            }
        }
        Collections.sort(result,
                LogicManager.getInstance().new OutsourcedComparator());
        return result;
    }

    /**
     * Gets all loans.
     *
     * @author Jochen Saßmannshausen
     * @author Caroline Bender
     * @author Marco
     *
     * @return A list of all Loans of allMuseums
     */
    public static ArrayList<Outsourced> getAllLoans()
    {
        ArrayList<Outsourced> temp = new ArrayList<Outsourced>();
        ArrayList<Outsourced> all = new ArrayList<Outsourced>(DataAccess
                .getInstance().getAllOutsourced());
        for (Outsourced os : all)
        {
            if (os.getContact_id() != null && !os.getContact_id().equals(0L))
            {
                temp.add(os);
            }
        }
        Collections.sort(temp,
                LogicManager.getInstance().new OutsourcedComparator());
        return temp;
    }

    /**
     * Gets all exhibitions.
     *
     * @author Jochen Saßmannshausen
     * @author Caroline Bender
     * @author Marco
     *
     * @return A list of all Exhibitions of all Museums
     */
    public static ArrayList<Outsourced> getAllExhibitions()
    {
        ArrayList<Outsourced> temp = new ArrayList<Outsourced>();
        ArrayList<Outsourced> all = new ArrayList<Outsourced>(DataAccess
                .getInstance().getAllOutsourced());
        for (Outsourced os : all)
        {
            if (os.getContact_id() == null || os.getContact_id() == 0)
            {
                temp.add(os);
            }
        }
        Collections.sort(temp,
                LogicManager.getInstance().new OutsourcedComparator());
        return temp;
    }

    /**
     * Gets all loans by a museum.
     *
     * @author Jochen Saßmannshausen
     * @author Caroline Bender
     * @author Marco
     *
     * @param museum
     * @return A list of all loans of the specified museum
     */
    public static ArrayList<Outsourced> getAllLoans(Museum museum)
    {
        ArrayList<Outsourced> temp = new ArrayList<Outsourced>();
        ArrayList<Outsourced> all = new ArrayList<Outsourced>(
                LogicManager.getAllOutsourced(museum));
        for (Outsourced os : all)
        {
            if (os.getContact_id() != null && !os.getContact_id().equals(0L))
            {
                temp.add(os);
            }
        }
        Collections.sort(temp,
                LogicManager.getInstance().new OutsourcedComparator());
        return temp;
    }

    /**
     * Gets all exhibitions by a museum.
     *
     * @author Jochen Saßmannshausen
     * @author Caroline Bender
     * @author Marco
     *
     * @param museum
     * @return A list of all exhibitions of the specified museum
     */
    public static ArrayList<Outsourced> getAllExhibitions(Museum museum)
    {
        ArrayList<Outsourced> temp = new ArrayList<Outsourced>();
        ArrayList<Outsourced> all = new ArrayList<Outsourced>(
                LogicManager.getAllOutsourced(museum));
        for (Outsourced os : all)
        {
            if (os.getContact_id() == null || os.getContact_id().equals(0L))
            {
                temp.add(os);
            }
        }
        Collections.sort(temp,
                LogicManager.getInstance().new OutsourcedComparator());
        return temp;
    }

    /**
     * Gets all existing labels.
     *
     * @author Marco
     *
     * @return list of all Labels
     */
    public static ArrayList<Label> getAllLabels()
    {
        ArrayList<Label> result = new ArrayList<Label>(DataAccess.getInstance()
                .getAllLabels());
        Collections.sort(result,
                LogicManager.getInstance().new LabelComparator());
        return result;
    }

    /**
     * Gets all labels by exhibitId
     *
     * @author Marco
     *
     * @param exhibit
     * @return
     */
    public static ArrayList<Label> getAllLabelsByExhibitId(long exhibitId)
    {
        ArrayList<Label> result = new ArrayList<Label>(DataAccess.getInstance()
                .searchLabelsByExhibitId(exhibitId));
        Collections.sort(result,
                LogicManager.getInstance().new LabelComparator());
        return result;
    }

    /**
     * Gets all history.
     *
     * @author Marco
     *
     * @return
     */
    public static ArrayList<History> getAllHistory()
    {
        return new ArrayList<History>(DataAccess.getInstance().getAllHistory());
    }

    /**
     * Gets all roles
     *
     * @author Benedikt
     * @author Marco
     *
     * @return A list of all roles
     */
    public static ArrayList<Role> getAllRole()
    {
        ArrayList<Role> result = new ArrayList<Role>(DataAccess.getInstance()
                .getAllRoles());
        Collections.sort(result,
                LogicManager.getInstance().new RoleComparator());
        return result;
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ search functions ----------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Search museum by name or by id.
     *
     * @author Benedikt
     *
     * @throws MuseumNotFoundException
     */
    public ArrayList<Museum> searchMuseumByName(String name)
    {
        return new ArrayList<Museum>(DataAccess.getInstance()
                .searchMuseumByName(name));
    }

    public Museum searchMuseumById(Long id) throws MuseumNotFoundException
    {
        Museum result = DataAccess.getInstance().getMuseumById(id);
        if (result == null)
        {
            throw new MuseumNotFoundException("Das Museum mit der Id " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * search Museum by address Id.
     *
     * @author Marco
     *
     * @param addressId
     * @return
     */
    public ArrayList<Museum> searchMuseumByAddressId(long addressId)
    {
        ArrayList<Museum> result = new ArrayList<Museum>();
        for (Museum m : LogicManager.getAllMuseums())
        {
            if (m.getAddress_id() == (addressId))
            {
                result.add(m);
            }
        }
        return result;
    }

    /**
     * Gets miscellaneous category by museum.
     *
     * @author Benedikt
     *
     * @param museum
     * @return miscellaneous category
     * @throws CategoryNotFoundException
     */
    public Category getMiscellaneousCategory(Museum museum)
            throws CategoryNotFoundException
    {
        ArrayList<Category> categories = LogicManager
                .getAllCategoriesByMuseum(museum);
        for (Category result : categories)
        {
            if (result.getName().equals("Sonstiges"))
            {
                return result;
            }
        }
        throw new CategoryNotFoundException(
                "Die Kategorie wurde nicht gefunden!");
    }

    /**
     * Search section by name or id.
     *
     * @author Benedikt
     * @author Marco
     *
     * @throws SectionNotFoundException
     */
    public ArrayList<Section> searchSectionByName(String name, Museum museum)
    {
        // TODO

        // if (name.equalsIgnoreCase("keiner Sektion zugeordnet")) {
        // ArrayList<Section> result = new ArrayList<Section>();
        // for (Section s : LogicManager.getAllSections(museum)) {
        // if (s.getParent_id() == 0) {
        // result.add(s);
        // }
        // }
        // return result;
        // }
        //
        // else {
        // ArrayList<Section> result = new ArrayList<Section>();
        // for (Section s : DataAccess.getInstance().searchSectionByName(name))
        // {
        // if (s.getMuseum_id() == museum.getId()) {
        // result.add(s);
        // }
        // }
        // return result;
        // }

        ArrayList<Section> result = new ArrayList<Section>();
        for (Section s : DataAccess.getInstance().searchSectionByName(name))
        {
            if (s.getMuseum_id() == museum.getId())
            {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Search section by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return section
     * @throws SectionNotFoundException
     */
    public Section searchSectionById(Long id) throws SectionNotFoundException
    {
        Section result = DataAccess.getInstance().getSectionById(id);
        if (result == null)
        {
            throw new SectionNotFoundException("Die Sektion mit der Id " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search exhibits from one specific museum by name.
     *
     * @author Ralf Heukäufer
     *
     * @throws ExhibitNotFoundException
     */
    public ArrayList<Exhibit> searchExhibitByName(String name, long museum_id)
    {
        ArrayList<Exhibit> result = new ArrayList<Exhibit>(DataAccess
                .getInstance().searchExhibitsByName(name));
        ArrayList<Exhibit> wrongid = new ArrayList<Exhibit>();
        for (Exhibit e : result)
        {
            if (e.getMuseum_id() != museum_id)
            {
                wrongid.add(e);
            }
        }
        result.removeAll(wrongid);
        return result;
    }

    /**
     * Search exhibit by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return exhibit
     * @throws ExhibitNotFoundException
     */
    public Exhibit searchExhibitById(Long id) throws ExhibitNotFoundException
    {
        Exhibit result = DataAccess.getInstance().getExhibitById(id);
        if (result == null)
        {
            throw new ExhibitNotFoundException("Das Exponat mit der Id " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search exhibits by a label.
     *
     * @author Marco
     *
     * @param label
     * @return
     * @throws LabelNotFoundException
     * @throws ExhibitNotFoundException
     */
    public ArrayList<Exhibit> searchExhibitsByLabelAndMuseum(Label label,
            Museum museum) throws LabelNotFoundException,
            ExhibitNotFoundException
    {

        ArrayList<Exhibit> result = new ArrayList<Exhibit>(DataAccess
                .getInstance().getAllExhibitsByMuseum(museum.getId()));
        Collection<Long> exhibitIds = label.getExhibit_ids();
        HashSet<Exhibit> historyExhibits = new HashSet<Exhibit>();
        for (Long l : exhibitIds)
        {
            historyExhibits.add(searchExhibitById(l));
        }
        result.retainAll(historyExhibits);
        return result;
    }

    /**
     * Search category by name.
     *
     * @author Benedikt
     *
     * @param name
     * @return
     */
    public ArrayList<Category> searchCategoryByName(String name)
    {
        return new ArrayList<Category>(DataAccess.getInstance()
                .searchCategoriesByName(name));
    }

    /**
     * Search category by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return
     * @throws CategoryNotFoundException
     */
    public Category searchCategoryById(Long id)
            throws CategoryNotFoundException
    {
        Category result;
        if (id == null)
        {
            result = null;
        }
        else
        {
            result = DataAccess.getInstance().getCategoryById(id);
        }
        if (result == null && id != null)
        {
            throw new CategoryNotFoundException("Die Kategorie mit der Id "
                    + id + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search outsourced by name.
     *
     * @author Benedikt
     *
     * @param name
     * @return
     */
    public ArrayList<Outsourced> searchOutsourcedByName(String name)
    {
        return new ArrayList<Outsourced>(DataAccess.getInstance()
                .searchOutsourcedByName(name));
    }

    /**
     * Search outsourced by id.
     *
     * @author Benedikt
     *
     * @throws OutsourcedNotFoundException
     */
    public Outsourced searchOutsourcedById(Long id)
            throws OutsourcedNotFoundException
    {
        Outsourced result = DataAccess.getInstance().getOutsourcedById(id);
        if (result == null)
        {
            throw new OutsourcedNotFoundException(
                    "Die Ausstellung oder Leihgabe mit der Id " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search contact detail by name and forename or id.
     *
     * @author Benedikt
     *
     * @throws ContactDetailNotFoundException
     */
    public ArrayList<Contact> searchContactDetailByName(String name,
            String forename)
    {
        return new ArrayList<Contact>(DataAccess.getInstance()
                .searchContactByName(name, forename));
    }

    public Contact searchContactDetailById(Long id)
            throws ContactNotFoundException
    {
        Contact result = DataAccess.getInstance().getContactById(id);
        if (result == null)
        {
            throw new ContactNotFoundException("Die Kontaktdaten mit der Id "
                    + id + " wurden nicht grfunden.");
        }
        return result;
    }

    /**
     * Search address by name of the museum or id.
     *
     * @author Benedikt
     *
     * @throws AddressNotFoundException
     */
    public ArrayList<Address> searchAddressByMuseumName(String museumName)
    {
        return new ArrayList<Address>(DataAccess.getInstance()
                .searchAddressByMuseumName(museumName));
    }

    /**
     * Search contact by museum id.
     *
     * @author Benedikt
     *
     * @param museumId
     * @return
     */
    public ArrayList<Contact> searchContactByMuseumId(long museumId)
    {
        return new ArrayList<Contact>(DataAccess.getInstance()
                .getContactByMuseumId(museumId));
    }

    /**
     * Search address by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return address
     * @throws AddressNotFoundException
     */
    public Address searchAddressById(Long id) throws AddressNotFoundException
    {
        Address result = DataAccess.getInstance().getAddressById(id);
        if (result == null)
        {
            throw new AddressNotFoundException("Die Addresse mit der Id " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search label by id.
     *
     * @author Marco
     *
     * @param labelId
     * @return
     */
    public Label searchLabelById(long labelId) throws LabelNotFoundException
    {
        Label result = DataAccess.getInstance().getLabelById(labelId);
        if (result == null)
        {
            throw new LabelNotFoundException("Das Label mit der ID: " + labelId
                    + " konnte nicht gefunden werden.");
        }
        return result;
    }

    /**
     * Search label by name.
     *
     * @author Marco
     *
     * @param labelName
     * @return
     */
    public ArrayList<Label> searchLabelByName(String labelName)
            throws LabelNotFoundException
    {
        ArrayList<Label> labelList = new ArrayList<Label>(DataAccess
                .getInstance().searchLabelsByName(labelName));
        if (labelList.isEmpty())
        {
            throw new LabelNotFoundException(labelName);
        }
        return labelList;
    }

    /**
     * Search history by exhibit id.
     *
     * @author Marco, Caroline
     *
     * @param exhibitId
     * @return list of histories sorted by date
     */
    public ArrayList<History> searchHistoryElementsByExhibitId(long exhibitId)
    {
        ArrayList<History> history = new ArrayList<History>(DataAccess
                .getInstance().getHistoryByExhibitId(exhibitId));
        Collections.sort(history, new HistoryComparator());
        return history;

    }

    /**
     * Search history by id.
     *
     * @author Marco
     *
     * @param id
     * @return
     * @throws HistoryElementNotFoundException
     */
    public History searchHistoryElementById(Long id)
            throws HistoryElementNotFoundException
    {
        History result = DataAccess.getInstance().getHistoryById(id);
        if (result == null)
        {
            throw new HistoryElementNotFoundException();
        }
        return result;
    }

    /**
     * Search history by name.
     *
     * @author Marco
     *
     * @param historyName
     * @return
     * @throws HistoryElementNotFoundException
     */
    public ArrayList<History> searchHistoryElementByName(String historyName)
            throws HistoryElementNotFoundException
    {
        ArrayList<History> result = (ArrayList<History>) DataAccess
                .getInstance().getHistoryByName(historyName);
        if (result == null)
        {
            throw new HistoryElementNotFoundException();
        }
        return result;
    }

    /**
     * Search picture by name of the exhibit.
     *
     * @author Benedikt
     *
     * @throws Exception
     *
     * @throws PictureNotFoundException
     * @throws ExhibitNotFoundException
     * @throws DatabaseNotInitializesException
     */
    public ArrayList<Image> searchPictureByExhibitName(String exhibitName)
            throws Exception
    {
        return new ArrayList<Image>(DataAccess.getInstance()
                .searchImagesByExhibitName(exhibitName));
    }

    /**
     * Search picture by exhibitId.
     *
     * @author Benedikt
     *
     * @param exhibit_id
     * @return list of Images
     * @throws Exception
     */
    public ArrayList<Image> searchPictureByExhibitId(long exhibit_id)
            throws Exception
    {
        Collection<Image> allImages = DataAccess.getInstance()
                .getAllImagesByExhibit(exhibit_id);
        ArrayList<Image> result = new ArrayList<Image>(allImages.size());
        for (Image step : allImages)
        {
            if (!step.isDeleted())
            {
                result.add(step);
            }
        }
        return result;
    }

    /**
     * Search picture by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return Image
     * @throws ConnectionException
     * @throws PictureNotFoundException
     */
    public Image searchPictureById(Long id) throws ConnectionException,
            PictureNotFoundException
    {
        Image result = DataAccess.getInstance().getImageById(id);
        if (result == null)
        {
            throw new PictureNotFoundException(
                    "Das Bild des Exponats mit dem Namen " + id
                    + " wurde nicht gefunden.");
        }
        return result;
    }

    /**
     * Search role by id.
     *
     * @author Benedikt
     *
     * @param id
     * @return Role
     */
    public Role searchRoleById(Long id)
    {
        return DataAccess.getInstance().getRoleById(id);
    }

    /**
     * Gets all roles by museumId.
     *
     * @author FSchikowski
     * @author Marco
     *
     * @param id
     * @return list of roles of the museum with musuem_id id
     */
    public ArrayList<Role> getAllRolesByMuseumId(long id)
    {
        ArrayList<Role> result = new ArrayList<Role>(DataAccess.getInstance()
                .getAllRolesByMuseumId(id));
        Collections.sort(result,
                LogicManager.getInstance().new RoleComparator());
        return result;
    }

    /**
     * Search contact by address id.
     *
     * @author Marco
     *
     * @param addressId
     * @return
     */
    public ArrayList<Contact> searchContactByAddressId(long addressId)
    {
        ArrayList<Contact> result = new ArrayList<Contact>();
        for (Contact c : DataAccess.getInstance().getAllContacts())
        {
            if (c.getAddress_id() == (addressId))
            {
                result.add(c);
            }
        }
        return result;
    }

    /**
     * Special search with intend and extend elements.
     *
     * @author Ralf Heukäufer
     *
     * @param MuseumID
     * @param proCategory
     * @param contraCategory
     * @param proSection
     * @param contraSection
     * @param proLabel
     * @param contraLabel
     * @param proOutsourced
     * @param contraOutsourced
     * @return List of Exhibits
     * @throws MuseumNotFoundException
     */
    public ArrayList<Exhibit> specialSearch(long museumID, String exhibitname,
            ArrayList<Long> proCategory, ArrayList<Long> contraCategory,
            ArrayList<Long> proSection, ArrayList<Long> contraSection,
            ArrayList<Long> proLabel, ArrayList<Long> contraLabel,
            ArrayList<Long> proOutsourced, ArrayList<Long> contraOutsourced,
            boolean normalsearch) throws MuseumNotFoundException
    {

        Collection<Exhibit> currList;
        Collection<Exhibit> currcatprolist = new HashSet<Exhibit>();
        Collection<Exhibit> currcatcontralist = new HashSet<Exhibit>();
        Collection<Exhibit> currsectioncontralist = new HashSet<Exhibit>();
        Collection<Exhibit> currlabelprolist = new HashSet<Exhibit>();
        Collection<Exhibit> currlabelcontralist = new HashSet<Exhibit>();
        Collection<Exhibit> curroutprolist = new HashSet<Exhibit>();
        Collection<Exhibit> curroutcontralist = new HashSet<Exhibit>();

        if (normalsearch == true)
        {
            if (museumID == 0)
            {
                ArrayList<Exhibit> result = new ArrayList<Exhibit>();
                return result;
            }
            if (exhibitname.equals(""))
            {
                return sortExhibitsByName(LogicManager
                        .getAllExhibitsByMuseum(this.searchMuseumById(museumID)));
            }
            else
            {
                return sortExhibitsByName(this.searchExhibitByName(exhibitname,
                        museumID));
            }
        }

        if (proSection.isEmpty())
        {
            currList = LogicManager.getAllExhibitsByMuseum(this
                    .searchMuseumById(museumID));
        }
        else
        {
            currList = DataAccess.getInstance().getAllExhibitsBySectionIds(
                    proSection);
        }

        if (!proCategory.isEmpty())
        {

            for (Long l : proCategory)
            {
                for (Exhibit e : currList)
                {
                    if (l.equals(e.getCategory_id()))
                    {
                        currcatprolist.add(e);
                    }
                }
            }
            currList.retainAll(currcatprolist);
        }

        if (!contraCategory.isEmpty())
        {

            for (Long l : contraCategory)
            {
                for (Exhibit e : currList)
                {
                    if (l.equals(e.getCategory_id()))
                    {
                        currcatcontralist.add(e);
                    }
                }
            }
            currList.removeAll(currcatcontralist);
        }

        if (!contraSection.isEmpty())
        {

            for (Long l : contraSection)
            {
                for (Exhibit e : currList)
                {
                    if (l.equals(e.getSection_id()))
                    {
                        currsectioncontralist.add(e);
                    }
                }
            }
            currList.removeAll(currsectioncontralist);
        }

        if (!proLabel.isEmpty())
        {

            for (Long l : proLabel)
            {
                for (Exhibit e : currList)
                {
                    for (Label m : e.getLabels())
                    {
                        if (l.equals(m.getId()))
                        {
                            currlabelprolist.add(e);
                        }
                    }
                }
            }
            currList.retainAll(currlabelprolist);
        }

        if (!contraLabel.isEmpty())
        {

            for (Long l : contraLabel)
            {
                for (Exhibit e : currList)
                {
                    for (Label m : e.getLabels())
                    {
                        if (l.equals(m.getId()))
                        {
                            currlabelcontralist.add(e);
                        }
                    }
                }
            }
            currList.removeAll(currlabelcontralist);
        }
        if (!proOutsourced.isEmpty())
        {
            for (Long l : proOutsourced)
            {
                Outsourced outsourced = DataAccess.getInstance().getOutsourcedById(l);
                for (Long key : outsourced.getExhibitIds().keySet())
                {
                	Exhibit e = DataAccess.getInstance().getExhibitById(key);
                    curroutprolist.add(e);
                    if(e.isDeleted())
                    	currList.add(e);
                }
            }
            currList.retainAll(curroutprolist);
        }

        if (!contraOutsourced.isEmpty())
        {

            for (Long l : contraOutsourced)
            {
                Outsourced outsourced = DataAccess.getInstance().getOutsourcedById(l);
                for (Long key : outsourced.getExhibitIds().keySet())
                {
                    curroutcontralist.add(DataAccess.getInstance().getExhibitById(key));
                }
            }
            currList.removeAll(curroutcontralist);
        }

        ArrayList<Exhibit> returnlist = new ArrayList<Exhibit>(currList);

        if (proSection.isEmpty() && exhibitname.equals("")
                && proCategory.isEmpty() && contraCategory.isEmpty()
                && contraSection.isEmpty() && proLabel.isEmpty()
                && contraLabel.isEmpty() && proOutsourced.isEmpty()
                && contraOutsourced.isEmpty())
        {
            return sortExhibitsByName(LogicManager.getAllExhibitsByMuseum(this
                    .searchMuseumById(museumID)));
        }

        if (!exhibitname.equals(""))
        {
            ArrayList<Exhibit> namesearch = this.searchExhibitByName(
                    exhibitname, museumID);
            namesearch.retainAll(returnlist);
            return sortExhibitsByName(namesearch);
        }
        else
        {
            return sortExhibitsByName(returnlist);
        }

    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ exhibit sort functions
     * ----------------------
     * ----------------------------------------------------------------------
     */
    /**
     * Sorts a list of exhibits by name, by section, by category or by state in
     * ordinary or reverse order.
     *
     * @author Jochen Saßmannshausen
     * @author Caroline Bender
     *
     * @param exhibits list of exhibits to sort
     * @return sorted list of exhibits
     */
    public ArrayList<Exhibit> sortExhibitsByName(ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new NameComparator());
        }
        return temp;
    }

    public ArrayList<Exhibit> sortExhibitsBySection(ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new SectionExhibitComparator());
        }
        return temp;
    }

    public ArrayList<Exhibit> sortExhibitsByCategory(ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new CategoryComparator());
        }
        return temp;
    }

    public ArrayList<Exhibit> sortExhibitsByState(ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new StateComparator());
        }
        return temp;
    }

    /**
     * Sorts reverse.
     *
     * @author Benedikt
     *
     * @param exhibits
     * @return
     */
    public ArrayList<Exhibit> sortReverseExhibitsByName(
            ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp,
                    Collections.reverseOrder(new NameComparator()));
        }
        return temp;
    }

    public ArrayList<Exhibit> sortReverseExhibitsBySection(
            ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp,
                    Collections.reverseOrder(new SectionExhibitComparator()));
        }
        return temp;
    }

    public ArrayList<Exhibit> sortReverseExhibitsByCategory(
            ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp,
                    Collections.reverseOrder(new CategoryComparator()));
        }
        return temp;
    }

    public ArrayList<Exhibit> sortReverseExhibitsByState(
            ArrayList<Exhibit> exhibits)
    {
        ArrayList<Exhibit> temp = new ArrayList<Exhibit>(exhibits);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp,
                    Collections.reverseOrder(new StateComparator()));
        }
        return temp;
    }

    /**
     * Sorts a syncModel by name.
     *
     * @author Benedikt
     *
     * @param models
     * @return
     */
    public ArrayList<SyncModel> sortSyncModel(ArrayList<SyncModel> models)
    {
        ArrayList<SyncModel> temp = new ArrayList<SyncModel>(models);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new SyncComparator());
        }
        return temp;
    }

    public ArrayList<Pair<SyncModel, SyncModel>> sortSyncPair(
            ArrayList<Pair<SyncModel, SyncModel>> models)
    {
        ArrayList<Pair<SyncModel, SyncModel>> temp = new ArrayList<Pair<SyncModel, SyncModel>>(
                models);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new SyncPairComparator());
        }
        return temp;
    }

    /**
     * Sorts labels by name.
     *
     * @author FSchikowski
     */
    public ArrayList<Label> sortLabelsByName(ArrayList<Label> label)
    {
        ArrayList<Label> temp = new ArrayList<Label>(label);
        if (temp != null && !temp.isEmpty())
        {
            Collections.sort(temp, new LabelComparator());
        }
        return temp;
    }

    /*
     * ----------------------------------------------------------------------
     * ------------------------------ exhibit comparators ----------------------
     * ----------------------------------------------------------------------
     */
    /**
     * @author FSchikowski
     *
     */
    private class LabelComparator implements Comparator<Label>
    {

        public int compare(Label l1, Label l2)
        {
            int cmp = l1.getName().compareToIgnoreCase(l2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return l1.getId().compareTo(l2.getId());
        }
    }

    /**
     * @author Caroline Bender
     * @author Jochen Saßmannshausen Comparator for comparision of exhibits by
     * name, by section or by category second comparision criterion is always
     * the id oh the exhibit
     *
     */
    private class NameComparator implements Comparator<Exhibit>
    {

        public int compare(Exhibit e1, Exhibit e2)
        {
            int cmp = e1.getName().compareToIgnoreCase(e2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return e1.getId().compareTo(e2.getId());
        }
    }

    private class SectionExhibitComparator implements Comparator<Exhibit>
    {

        public int compare(Exhibit e1, Exhibit e2)
        {
            String name1;
            String name2;
            if (e1.getSection_id() == null)
            {
                name1 = "keiner Sektion zugeordnet";
            }
            else
            {
                name1 = e1.getSection().getName();
            }
            if (e2.getSection_id() == null)
            {
                name2 = "keiner Sektion zugeordnet";
            }
            else
            {
                name2 = e2.getSection().getName();
            }
            int cmp = name1.compareToIgnoreCase(name2);
            if (cmp != 0)
            {
                return cmp;
            }
            return e1.getId().compareTo(e2.getId());
        }
    }

    private static class SectionComparator implements Comparator<Section>
    {

        @Override
        public int compare(Section s1, Section s2)
        {
            return s1.getName().toLowerCase()
                    .compareTo(s2.getName().toLowerCase());
        }
    }

    private class CategoryComparator implements Comparator<Exhibit>
    {

        @Override
        public int compare(Exhibit e1, Exhibit e2)
        {
            String name1;
            String name2;
            if (e1.getCategory() == null)
            {
                name1 = "keiner Sektion zugeordnet";
            }
            else
            {
                name1 = e1.getCategory().getName();
            }
            if (e2.getCategory() == null)
            {
                name2 = "keiner Sektion zugeordnet";
            }
            else
            {
                name2 = e2.getCategory().getName();
            }
            int cmp = name1.compareToIgnoreCase(name2);
            if (cmp != 0)
            {
                return cmp;
            }
            return e1.getId().compareTo(e2.getId());
        }
    }

    private class StateComparator implements Comparator<Exhibit>
    {
        @Override
        public int compare(Exhibit e1, Exhibit e2)
        {
            Timestamp t1 = null;
            Timestamp t2 = null;
            for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
            {
                if (outsourced.getExhibitIds().containsKey(e1.getId()))
                {
                    t1 = outsourced.getExhibitIds().get(e1.getId());
                }
                if (outsourced.getExhibitIds().containsKey(e2.getId()))
                {
                    t2 = outsourced.getExhibitIds().get(e2.getId());
                }
            }
            if (t1==null)
            {
                return -1;
            }
            if (t2==null)
            {
                return 1;
            }
            return t1.compareTo(t2);
        }
    }

    private class SyncComparator implements Comparator<SyncModel>
    {

        public int compare(SyncModel s1, SyncModel s2)
        {
            if (s1.isConflict())
            {
                if (s2.isConflict())
                {
                    if (s1.getType().equals(s2.getType()))
                    {
                        return s1.getName().compareTo(s2.getName());
                    }
                    else
                    {
                        return s1.getType().compareTo(s2.getType());
                    }
                }
                else
                {
                    return -1;
                }
            }
            else if (s2.isConflict())
            {
                return 1;
            }
            else
            {
                if (s1.getType().equals(s2.getType()))
                {
                    return s1.getName().compareTo(s2.getName());
                }
                else
                {
                    return s1.getType().compareTo(s2.getType());
                }
            }
        }
    }

    private class SyncPairComparator implements
            Comparator<Pair<SyncModel, SyncModel>>
    {

        @Override
        public int compare(Pair<SyncModel, SyncModel> p1,
                Pair<SyncModel, SyncModel> p2)
        {
            if (p1.getLeft().isConflict())
            {
                if (p2.getLeft().isConflict())
                {
                    if (p1.getLeft() == null)
                    {
                        if (p2.getLeft() == null)
                        {
                            if (p1.getRight().getType()
                                    .equals(p2.getRight().getType()))
                            {
                                return p1.getRight().getName()
                                        .compareTo(p2.getRight().getName());
                            }
                            else
                            {
                                return p1.getRight().getType()
                                        .compareTo(p2.getRight().getType());
                            }
                        }
                        else
                        {
                            if (p2.getRight() == null)
                            {
                                if (p1.getRight().getType()
                                        .equals(p2.getLeft().getType()))
                                {
                                    return p1.getRight().getName()
                                            .compareTo(p2.getLeft().getName());
                                }
                                else
                                {
                                    return p1.getRight().getType()
                                            .compareTo(p2.getLeft().getType());
                                }
                            }
                            else
                            {
                                if (p1.getRight().getType()
                                        .equals(p2.getRight().getType()))
                                {
                                    return p1.getRight().getName()
                                            .compareTo(p2.getRight().getName());
                                }
                                else
                                {
                                    return p1.getRight().getType()
                                            .compareTo(p2.getRight().getType());
                                }
                            }
                        }
                    }
                    else
                    {
                        if (p2.getLeft() != null)
                        {
                            if (p1.getLeft().getType()
                                    .equals(p2.getLeft().getType()))
                            {
                                return p1.getLeft().getName()
                                        .compareTo(p2.getLeft().getName());
                            }
                            else
                            {
                                return p1.getLeft().getType()
                                        .compareTo(p2.getLeft().getType());
                            }
                        }
                        else
                        {
                            if (p1.getRight() == null)
                            {
                                if (p1.getLeft().getType()
                                        .equals(p2.getRight().getType()))
                                {
                                    return p1.getLeft().getName()
                                            .compareTo(p2.getRight().getName());
                                }
                                else
                                {
                                    return p1.getLeft().getType()
                                            .compareTo(p2.getRight().getType());
                                }
                            }
                            else
                            {
                                if (p1.getRight().getType()
                                        .equals(p2.getRight().getType()))
                                {
                                    p1.getRight().getName()
                                            .compareTo(p2.getRight().getName());
                                }
                                else
                                {
                                    p1.getRight().getType()
                                            .compareTo(p2.getRight().getType());
                                }
                            }
                        }
                    }
                }
                else
                {
                    return -1;
                }
            }
            else if (p2.getLeft().isConflict())
            {
                return 1;
            }
            else
            {
                if (p1.getLeft().getType().equals(p2.getLeft().getType()))
                {
                    return p1.getLeft().getName()
                            .compareTo(p2.getLeft().getName());
                }
                else
                {
                    return p1.getLeft().getType()
                            .compareTo(p2.getLeft().getType());
                }
            }
            return 0;
        }
    }

    /**
     * @author Caroline Comparator for comparision of two Historys by date
     *
     */
    private class HistoryComparator implements Comparator<History>
    {

        public int compare(History h1, History h2)
        {

            return h1.getStartdate().compareTo(h2.getStartdate());
        }
    }

    /**
     *
     * @author Caroline Comparator for comparision of two Contacts by museum_id
     * and name
     *
     */
    private class ContactComparator implements Comparator<Contact>
    {

        public int compare(Contact c1, Contact c2)
        {
            Role r1 = DataAccess.getInstance().getRoleById(c1.getRoleId());
            Role r2 = DataAccess.getInstance().getRoleById(c2.getRoleId());
            long musId1;
            long musId2;
            if (r1 != null)
            {
                musId1 = r1.getMuseum_id();
            }
            else
            {
                musId1 = 0L;
            }
            if (r2 != null)
            {
                musId2 = r2.getMuseum_id();
            }
            else
            {
                musId2 = 0L;
            }

            if (musId1 < musId2)
            {
                return -1;
            }
            if (musId1 > musId2)
            {
                return 1;
            }
            return c1.getName().compareToIgnoreCase(c2.getName());

        }
    }

    /**
     * Compares two Categories.
     *
     * @author Marco
     */
    private class CategoryCategoryComparator implements Comparator<Category>
    {

        public int compare(Category c1, Category c2)
        {
            int cmp = c1.getName().compareToIgnoreCase(c2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return c1.getId().compareTo(c2.getId());
        }
    }

    /**
     * Compares two Outsourced Objects.
     *
     * @author Marco
     */
    private class OutsourcedComparator implements Comparator<Outsourced>
    {

        public int compare(Outsourced o1, Outsourced o2)
        {
            int cmp = o1.getName().compareToIgnoreCase(o2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return o1.getId().compareTo(o2.getId());
        }
    }

    /**
     * Compares two Museums.
     *
     * @author Marco
     */
    private class MuseumComparator implements Comparator<Museum>
    {

        public int compare(Museum m1, Museum m2)
        {
            int cmp = m1.getName().compareToIgnoreCase(m2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return m1.getId().compareTo(m2.getId());
        }
    }

    /**
     * Compares two Roles.
     *
     * @author Marco
     */
    private class RoleComparator implements Comparator<Role>
    {

        public int compare(Role r1, Role r2)
        {
            int cmp = r1.getName().compareToIgnoreCase(r2.getName());
            if (cmp != 0)
            {
                return cmp;
            }
            return r1.getId().compareTo(r2.getId());
        }
    }

    /**
     * Compares two Addresses.
     *
     * @author Marco
     */
    private class AddressComparator implements Comparator<Address>
    {

        public int compare(Address a1, Address a2)
        {
            int cmp = a1.getStreet().compareToIgnoreCase(a2.getStreet());
            if (cmp != 0)
            {
                return cmp;
            }
            return a1.getId().compareTo(a2.getId());
        }
    }
}
