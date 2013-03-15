package de.museum.berleburg.logicAccess;

import java.util.ArrayList;

import de.museum.berleburg.datastorage.interfaces.Model;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.DatabaseElement;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;

/**
 * 
 * @author Benedikt
 * 
 */

public class SyncModel {

	private boolean isConflict;
	private Model model;
	private boolean isLocal;
	private long id;
	private static long count=1l;
	private static ArrayList<SyncModel> allSyncs = new ArrayList<>();
	private int listPosition;

	/**
	 * Instantiates a new SyncModel.
	 * 
	 * @param model
	 * @param isConflict
	 * @param isLocal
	 */
	public SyncModel(Model model, boolean isConflict, boolean isLocal, int listPosition) {
		this.model = model;
		this.isConflict = isConflict;
		this.isLocal = isLocal;
		this.listPosition = listPosition;
		id= count++;
		allSyncs.add(this);
	}

	/**
	 * get Type.
	 * 
	 * @return String
	 */
	public String getType() {
		if (model instanceof Address)
			return "Adresse";
		if (model instanceof Category)
			return "Kategorie";
		if (model instanceof Contact)
			return "Kontakt";
		if (model instanceof Exhibit)
			return "Exponat";
		if (model instanceof History)
			return "Historien Element";
		if (model instanceof Image)
			return "Bild";
		if (model instanceof Label)
			return "Label";
		if (model instanceof Museum)
			return "Museum";
		if (model instanceof Outsourced)
			return "Ausstellung/Leihgabe";
		if (model instanceof Role)
			return "Rolle";
		if (model instanceof Section)
			return "Sektion";
		return "?";
	}

	/**
	 * get Name.
	 * 
	 * @return String
	 */
	public String getName() {
		if (model instanceof Address)
			return ((Address) model).toString();
		if (model instanceof Category)
			return ((Category) model).getName();
		if (model instanceof Contact)
			return ((Contact) model).getForename() + " "
					+ ((Contact) model).getName();
		if (model instanceof Exhibit)
			return ((Exhibit) model).getName();
		if (model instanceof History)
			return ((History) model).getName();
		if (model instanceof Image)
			return ((Image) model).getName();
		if (model instanceof Label)
			return ((Label) model).getName();
		if (model instanceof Museum)
			return ((Museum) model).getName();
		if (model instanceof Outsourced)
			return ((Outsourced) model).getName();
		if (model instanceof Role)
			return ((Role) model).getName();
		if (model instanceof Section)
			return ((Section) model).getName();
		return "?";
	}

	/**
	 * get Timestamp.
	 * 
	 * @return String
	 */
	public String getTimestamp() {
		if (model == null)
			return "?";
		if (((DatabaseElement) model).getUpdate() == null)
			return "Dieses Objekt wurde noch nie geupdated!";
		return ((DatabaseElement) model).getUpdate().toString();
	}

	/**
	 * get isConflict boolean.
	 * 
	 * @return boolean
	 */
	public boolean isConflict() {
		return isConflict;
	}

	/**
	 * get Model.
	 * 
	 * @return Model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * get isLocal boolean.
	 * 
	 * @return boolean
	 */
	public boolean isLocal() {
		return isLocal;
	}
	
	public long getId() {
		return id;
	}
	
	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}
	
	public static SyncModel getSyncModelById(long id){
		for(SyncModel step : allSyncs){
			if(step.getId() == id)
				return step;
		}
		return null;
	}
	
	public static void reset(){
		allSyncs = new ArrayList<SyncModel>();
	}

}
