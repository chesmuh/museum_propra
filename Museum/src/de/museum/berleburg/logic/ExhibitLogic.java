package de.museum.berleburg.logic;

import java.util.ArrayList;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;

/**
 * 
 * @author Marco
 * 
 */

public class ExhibitLogic {

	/**
	 * Deletes an exhibit.
	 * 
	 * @param toDelete
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteExhibit(Exhibit toDelete)
			throws ModelAlreadyDeletedException, ConnectionException {

		ArrayList<Image> images = new ArrayList<>(DataAccess.getInstance()
				.getAllImagesByExhibit(toDelete.getId()));
		for(Image step : images)
			if(!step.isDeleted())
				DataAccess.getInstance().delete(step);

		DataAccess.getInstance().delete(toDelete);
	}

	/**
	 * Saves an exhibit.
	 * 
	 * @param toSave
	 * @throws ConnectionException
	 */
	public static void saveExhibit(Exhibit toSave) throws ConnectionException {
		DataAccess.getInstance().update(toSave);
	}

	/**
	 * Changes the category of exhibit.
	 * 
	 * @param exhibit
	 * @param destination
	 */
	public static void moveToCategory(Exhibit exhibit, Category destination) {
		exhibit.setCategory_id(destination.getId());
	}

	/**
	 * Changes the section of exhibit.
	 * 
	 * @param exhibit
	 * @param destination
	 */
	public static void moveToSection(Exhibit exhibit, Section destination) {
		exhibit.setSection_id(destination.getId());
	}

	/**
	 * Moves exhibit into another museum.
	 * 
	 * @param exhibit
	 * @param museum
	 * @throws MuseumNotFoundException
	 * @throws CategoryNotFoundException
	 */
	public static void moveToMuseum(Exhibit exhibit, Museum museum)
			throws CategoryNotFoundException, MuseumNotFoundException {
		long exhibitMuseum_id = exhibit.getMuseum_id();
		exhibit.setMuseum_id(museum.getId());
		if (exhibitMuseum_id != museum.getId())
			moveToDefaultCategory(exhibit);
		exhibit.setSection_id(null);
	}

	/**
	 * Puts an exhibit into the default category (e.g. when its category is
	 * deleted)
	 * 
	 * @param exhibit
	 * @throws MuseumNotFoundException
	 * @throws CategoryNotFoundException
	 */
	public static void moveToDefaultCategory(Exhibit exhibit)
			throws CategoryNotFoundException, MuseumNotFoundException {
		exhibit.setCategory_id(LogicManager
				.getInstance()
				.getMiscellaneousCategory(
						LogicManager.getInstance().searchMuseumById(
								exhibit.getMuseum_id())).getId());
	}

	/**
	 * Puts exhibit into an exhibition.
	 * 
	 * @param exhibit
	 * @param outsourced
	 */
	public static void moveToOutsourced(Exhibit exhibit, Outsourced outsourced) {
            outsourced.getExhibitIds().put(exhibit.getId(), null);
	}
}
