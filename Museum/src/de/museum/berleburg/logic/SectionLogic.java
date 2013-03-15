package de.museum.berleburg.logic;

import java.util.ArrayList;
import java.util.Arrays;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;

/**
 * @author Ralf Heukaeufer
 * 
 */

public class SectionLogic {

	/**
	 * Deletes a section and its subsections. All exhibits are given to the
	 * parent of the section.
	 * 
	 * @param toDelete
	 * @throws ModelAlreadyDeletedException
	 * @throws ConnectionException
	 */
	public static void deleteSection(Section toDelete)
			throws ModelAlreadyDeletedException, ConnectionException {

		ArrayList<Exhibit> Exhibitlist = new ArrayList<Exhibit>();

		Exhibitlist.addAll(DataAccess.getInstance().getAllExhibitsBySectionIds(
				Arrays.asList(toDelete.getId())));
		for (Exhibit currE : Exhibitlist) {
			currE.setSection_id(toDelete.getParent_id());
			ExhibitLogic.saveExhibit(currE);
		}
		deleteAllSubsections(toDelete, toDelete.getParent_id());
		DataAccess.getInstance().delete(toDelete);

	}

	/**
	 * Moves a section to another section.
	 * 
	 * @param parent
	 * @param toAdd
	 * @throws IntegrityException
	 */
	public static void moveSection(Section parent, Section toAdd)
			throws IntegrityException {
		if (SectionLogic.isParent(toAdd, parent))
			throw new IntegrityException(toAdd,
					"Eine Sektion kann nicht unter sich selbst verschoben werden!");
	}

	/**
	 * Returns true if section a is parent of b.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @param a
	 * @param b
	 * @return
	 * @throws DatabaseNotInitializesException
	 * 
	 */
	private static boolean isParent(Section a, Section b) {
		if (b.getId() == null || b.getId().equals(0L))
			return false;
		if (b.getParent_id().equals(a.getId()))
			return true;
		else
			return isParent(b.getParent(), a);
	}

	public static void saveSection(Section toSave) throws ConnectionException {
		DataAccess.getInstance().update(toSave);
	}

	/**
	 * Deletes all Subsections from the Section where it runs in the SQL
	 * Database.
	 * 
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteAllSubsections(Section toDelete, Long parentId)
			throws ModelAlreadyDeletedException, ConnectionException {
		ArrayList<Exhibit> Exhibitlist = new ArrayList<Exhibit>();
		for (Section currS : DataAccess.getInstance().getAllSectionsBySection(
				toDelete.getId())) {
			deleteAllSubsections(currS, parentId);
			Exhibitlist.addAll(DataAccess.getInstance()
					.getAllExhibitsBySectionIds(Arrays.asList(currS.getId())));
			DataAccess.getInstance().delete(currS);
		}
		for (Exhibit currE : Exhibitlist) {
			currE.setSection_id(parentId);
			ExhibitLogic.saveExhibit(currE);
		}
	}
	
	/**
	 * Checks if section a is a childsection of section b. 
	 * 
	 * @author Caroline Bender
	 * @author Jochen Saßmannshausen 
	 * @return true if a is childsection of b
	 * 
	 */
	public static boolean isChildSection(long a, long b) {
		Section s;
		try {
			s = Access.searchSectionID(a);
		} catch (SectionNotFoundException e) {
			return false;
		}
		// TODO Auto-generated catch block
		while (s != null && s.getId() != 0) {
			if (s.getParent_id() != null && s.getParent_id().equals(b))
				return true;
			s = s.getParent();
		}
		return false;
	}

}
