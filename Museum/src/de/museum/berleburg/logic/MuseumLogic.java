package de.museum.berleburg.logic;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 * 
 * @author Jochen, Caroline
 * 
 */

public class MuseumLogic {

	/**
	 * Saves the museum currentMuseum. 
	 * 
	 * @param currentMuseum
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ConnectionException
	 */
	public static void saveMuseum(Museum currentMuseum)
			throws FileNotFoundException, IOException, ConnectionException {
		DataAccess.getInstance().update(currentMuseum);
	}

	/* --------------------------------------------------------------------- */
	/* --------------------------- add functions --------------------------- */
	/* --------------------------------------------------------------------- */

	/**
	 * Adds section toAdd to currentMuseum. 
	 * 
	 * @param currentMuseum
	 * @param toAdd
	 * @return if of added Section
	 */
	public static long addSubSection(Museum currentMuseum, Section toAdd) {
		toAdd.setMuseum_id(currentMuseum.getId());
		toAdd.setParent_id(null);
		return toAdd.getId();
	}

	/* --------------------------------------------------------------------- */
	/* --------------------------- delete functions ------------------------ */
	/* --------------------------------------------------------------------- */

	/**
	 * Deletes currentMuseum. 
	 * 
	 * @param currentMuseum
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteMuseum(Museum currentMuseum)
			throws ModelAlreadyDeletedException, ConnectionException {
		Collection<Contact> contact = DataAccess.getInstance()
				.getContactByMuseumId(currentMuseum.getId());
		for (Contact toDelete : contact) {
			if(!toDelete.isDeleted())
				DataAccess.getInstance().delete(toDelete);
		}
		Collection<Section> toDelete = (DataAccess.getInstance()
				.getAllSectionByMuseum(currentMuseum.getId()));
		for (Section step : toDelete) {
			if(!step.isDeleted())
				SectionLogic.deleteSection(step);
		}
		for(Exhibit step : DataAccess.getInstance().getAllExhibitsByMuseum(currentMuseum.getId())){
			for(Image img : DataAccess.getInstance().getAllImagesByExhibit(step.getId())){
				DataAccess.getInstance().delete(img);
			}
			DataAccess.getInstance().delete(step);
		}
		for(Category step : DataAccess.getInstance().getAllCategoriesByMuseumId(currentMuseum.getId())){
			DataAccess.getInstance().delete(step);
		}
		for(Outsourced step : DataAccess.getInstance().getAllOutsourced()){
			if(currentMuseum.getId().equals(step.getMuseum_id())){
				DataAccess.getInstance().delete(step);
			}
		}
		for(Role step : DataAccess.getInstance().getAllRolesByMuseumId(currentMuseum.getId())){
			DataAccess.getInstance().delete(step);
		}
		DataAccess.getInstance().delete(currentMuseum);
	}

	/* --------------------------------------------------------------------- */
	/* --------------------------- getter and setter ----------------------- */
	/* --------------------------------------------------------------------- */

	/**
	 * Sets address of currentMuseum. 
	 * 
	 * @param currentMuseum
	 * @param toAdd
	 * @return id of added Address
	 */
	public static long setAddress(Museum currentMuseum, Address toAdd) {
		currentMuseum.setAddress_id(toAdd.getId());
		return toAdd.getId();
	}
}
