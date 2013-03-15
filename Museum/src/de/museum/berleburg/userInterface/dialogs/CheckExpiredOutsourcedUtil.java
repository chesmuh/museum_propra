package de.museum.berleburg.userInterface.dialogs;

import java.util.ArrayList;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

/**
 * @author Way Dat To
 *
 */
public class CheckExpiredOutsourcedUtil {
	
	/**
	 * Checks if there are any expired outsources
	 * @return boolean
	 */
	public static boolean checkExpiredOutsourced() {

		// Check for expired outsourced
		// -----------------------------------------------------------------------------------------
		ArrayList<Outsourced> outsourced=null;
		try {
			outsourced = Access.getAllOutsourced(TreeMainPanel.getInstance().getMuseumId());
		} catch (MuseumNotFoundException e) {
			return false;
		}
		for (Outsourced actual:outsourced){
			try {
				if (Access.isExpired(actual.getId()) && !actual.isDeleted() && !actual.allBack()){
					return true;
				}
			} catch (OutsourcedNotFoundException e) {
			}
		}
		return false;
	}
}
		
		

		// ----------------------------------------------------------------------------------------

	

