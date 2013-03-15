package de.museum.berleburg.userInterface.listeners;

import java.util.Collection;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;

/**
 * Exchange Exhibit objects between dialogs. See {@link LabelListener} or {@link EditExhibit}.addListener(...) for example code.
 * @author Christian Landel
 *
 */

public interface ExhibitListener {
		public void event (Collection<Exhibit> exhibits);
}
