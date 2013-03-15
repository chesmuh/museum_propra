package de.museum.berleburg.datastorage.manager;

import java.util.Collection;
import java.util.HashSet;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.sql.SQLQueryOutsourced;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * Manages the destinations
 * 
 * @author Nils Leonhardt
 */
public class OutsourcedManager extends AbstractManager<Outsourced> {

	public OutsourcedManager() {
		super(SQLQueryOutsourced.class);
	}

	public Collection<Outsourced> getByName(String name) {
		HashSet<Outsourced> result = new HashSet<>();
		for (Outsourced step : this.getAll(true)) {
			if (step.getName().toLowerCase().contains(name.toLowerCase())) {
				result.add(step);
			}
		}
		return result;
	}

//	@Override
//	public Collection<Outsourced> getAll(boolean local) {
//		HashSet<Outsourced> result = new HashSet<>();
//		if (local) {
//			return this.localDBModels.values();
//		}
//		for (Outsourced step : this.serverDBModels.values()) {
//			if (!step.isDeleted()) {
//				result.add(step);
//			}
//		}
//		return result;
//	}

	@Override
	public void reAssignLocalModel(Outsourced model, Collection<Long> blockedIDs)
			throws ConnectionException {
		while (blockedIDs.contains(model.getId())) {
			this.delete(model, true);
			this.store(model);
		}
	}
}
