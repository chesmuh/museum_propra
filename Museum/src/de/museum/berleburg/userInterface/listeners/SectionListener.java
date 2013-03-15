package de.museum.berleburg.userInterface.listeners;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Section;

public interface SectionListener {
	public void event (Section sections);
	public void event (Museum museums);
}


