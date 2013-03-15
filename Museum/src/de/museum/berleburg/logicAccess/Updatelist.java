package de.museum.berleburg.logicAccess;

import java.util.ArrayList;

import de.museum.berleburg.datastorage.interfaces.Model;
import de.museum.berleburg.datastorage.interfaces.Pair;
import de.museum.berleburg.datastorage.manager.UpdateManager;
import de.museum.berleburg.exceptions.InvalidArgumentsException;

/**
 * 
 * @author Benedikt
 * 
 */
//TODO if nothing to update OR commit show message instead of opening the dialog!
public class Updatelist {

	private ArrayList<SyncModel> server;
	private ArrayList<SyncModel> local;
	private UpdateManager manager;
	private ArrayList<Pair<Model, Model>> conflictList;
	private ArrayList<Pair<Model, Model>> noConflictList;
	private ArrayList<Pair<SyncModel, SyncModel>> completeList;

	/**
	 * creates new Updatelist
	 * 
	 * @param manager
	 */
	public Updatelist(UpdateManager manager, boolean commit) {
		this.manager = manager;
		conflictList = new ArrayList<>(manager.getConflicts());
		noConflictList = new ArrayList<>(manager.getNoConflicts());
		completeList = new ArrayList<Pair<SyncModel, SyncModel>>();

		local = new ArrayList<>();
		server = new ArrayList<>();
		
		int listPosition = 0;
		
		if (commit) {
			for (Pair<Model, Model> p : conflictList) {
				local.add(new SyncModel((Model) p.getLeft(), true, true, listPosition));
				server.add(new SyncModel((Model) p.getRight(), true, false, listPosition));
				completeList.add(new Pair<>(local.get(listPosition), server.get(listPosition)));
				listPosition++;
			}

			for (Pair<Model, Model> p : noConflictList) {
				local.add(new SyncModel((Model) p.getLeft(), false, true, listPosition));
				server.add(new SyncModel((Model) p.getRight(), false, false, listPosition));
				completeList.add(new Pair<>(local.get(listPosition), server.get(listPosition)));
				listPosition++;
			}
		} else {
			for (Pair<Model, Model> p : conflictList) {
				local.add(new SyncModel((Model) p.getRight(), true, true, listPosition));
				server.add(new SyncModel((Model) p.getLeft(), true, false, listPosition));
				completeList.add(new Pair<>(local.get(listPosition), server.get(listPosition)));
				listPosition++;
			}

			for (Pair<Model, Model> p : noConflictList) {
				local.add(new SyncModel((Model) p.getRight(), false, true, listPosition));
				server.add(new SyncModel((Model) p.getLeft(), false, false, listPosition));
				completeList.add(new Pair<>(local.get(listPosition), server.get(listPosition)));
				listPosition++;
			}
		}

	}

	/**
	 * Solves a problem. First model of conflict is from the local database, the
	 * second one is from the server. In resolution is the first model the on
	 * that should be updated (can be null if the model should be deleted) and
	 * the second one is the one which should contain the new data.
	 * 
	 * @param conflict
	 * @throws InvalidArgumentsException if the selected item is no conflict. 
	 */
	public void solveConflict(SyncModel solution)
			throws InvalidArgumentsException {
		if (manager != null) {
			Pair<Model, Model> conflict;
			Pair<Model, Model> resolution;
			if (solution.getListPosition() <= conflictList.size()) {
				conflict = conflictList.get(solution.getListPosition());
				if (solution.getModel() == null) {
					resolution = new Pair<>(null, conflictList
							.get(solution.getListPosition()).getRight());
				} else
					resolution = new Pair<>(solution.getModel(), null);
				manager.resolveConflict(conflict, resolution);
			} else {
				throw new InvalidArgumentsException(
						"Das gewaehlte Element hat keinen Konflikt");
			}
		}
	}

	/**
	 * Ignores the conflict. 
	 * 
	 * @param conflict
	 * @throws InvalidArgumentsException if the selected item is no conflict. 
	 */
	public void ignoreConflict(SyncModel conflict)
			throws InvalidArgumentsException {
		if (manager != null) {
			Pair<Model, Model> conflictPair;
			if (conflict.getListPosition() <= conflictList.size()) {
				conflictPair = conflictList.get(conflict.getListPosition());
				manager.ignoreConflict(conflictPair);
			} else {
				throw new InvalidArgumentsException(
						"Das gewaehlte Element hat keinen Konflikt");
			}
		}
	}
	
	/**
	 * If toIgnore is a conflict, use ignoreConflict!
	 * 
	 * @param toIgnore
	 */
	public void ignoreUpdate(SyncModel toIgnore){
		manager.ignoreUpdate(noConflictList.get(toIgnore.getListPosition()-conflictList.size())); 
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<SyncModel> getLocalList() {
		return local;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<SyncModel> getServerList() {
		return server;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Pair<Model, Model>> getConflictList() {
		return conflictList;
	}

	public ArrayList<Pair<Model, Model>> getNoConflictList() {
		return noConflictList;
	}
	
	public ArrayList <Pair<SyncModel, SyncModel>> getCompleteList(){
		return completeList;
	}

	/**
	 * 
	 * @return
	 */
	public UpdateManager getManager() {
		return manager;
	}

}
