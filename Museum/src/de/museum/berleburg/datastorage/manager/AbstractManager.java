package de.museum.berleburg.datastorage.manager;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.interfaces.ISqlQuery;
import de.museum.berleburg.datastorage.interfaces.Manager;
import de.museum.berleburg.datastorage.model.DatabaseElement;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionFailedException;
import de.museum.berleburg.exceptions.ConnectionTimeOutException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 * Abstract Manager
 * 
 * @author Anselm
 */
public abstract class AbstractManager<M extends DatabaseElement> implements
		Manager<M> {

	protected final ISqlQuery<M> sqlQuery; // QueryExecutor for the manager
	protected Map<Long, M> localDBModels = new HashMap<>(); // all models except
															// "deleted" ones
	protected Map<Long, M> serverDBModels = new HashMap<>();
	private boolean serverDBInit = false;

	public AbstractManager(Class<? extends ISqlQuery<M>> clazz) {
		try {
			this.sqlQuery = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new IllegalStateException("Could not create SqlQueryClass!");
		}
	}

	@Override
	public void update(M model, boolean local, boolean updateTime)
			throws ConnectionException {
		try {
			if (updateTime) {
				model.setUpdate(new Timestamp(System.currentTimeMillis()));
			}
			this.sqlQuery.update(local, model);
		} catch (SQLTimeoutException e) {
			throw new ConnectionTimeOutException("Connection timed out", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Unexpected SQLError!", e);
		}

	}

	@Override
	public void store(M model) throws ConnectionException {
		try {
			model.setInsert(new Timestamp(System.currentTimeMillis()));
			model.setUpdate(new Timestamp(System.currentTimeMillis()));
			this.sqlQuery.store(model);
			this.localDBModels.put(model.getId(), model); // This can only be
															// local db
		} catch (SQLTimeoutException e) {

			throw new ConnectionTimeOutException("Connection timed out", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Unexpected SQLError!", e);
		}
	}

	@Override
	public void markAsDeleted(M model, boolean local)
			throws ConnectionTimeOutException, ModelAlreadyDeletedException {
		try {
			if (model.isDeleted()) {
				throw new ModelAlreadyDeletedException("The model of "
						+ model.getClass().getName() + " was already deleted! ");
			}
			model.setDeleted(new Timestamp(System.currentTimeMillis()));
			model.setUpdate(new Timestamp(System.currentTimeMillis()));
			this.sqlQuery.update(local, model); // updates that it is now
												// deleted
		} catch (SQLTimeoutException e) {
			throw new ConnectionTimeOutException("Connection timed out", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Unexpected SQLError!", e);
		}
	}

	@Override
	public <T extends Manager> T loadAll(boolean local, ProcessCallBack callBack)
			throws ConnectionTimeOutException {
		try {
			if (local) {
				this.localDBModels.clear();
			} else {
				this.serverDBModels.clear();
			}
			Collection<M> read = this.sqlQuery.loadAll(local, callBack);
			for (M model : read) {
				if (local) {
					this.localDBModels.put(model.getId(), model);
				} else {
					this.serverDBModels.put(model.getId(), model);
				}
			}
			return (T) this;
		} catch (SQLTimeoutException e) {
			throw new ConnectionTimeOutException("Connection timed out", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Unexpected SQLError!", e);
		}
	}

	@Override
	public Collection<M> getAll(boolean local) {
		HashSet<M> result = new HashSet<>();
		if (local) {
			for (M step : this.localDBModels.values()) {
				if (!step.isDeleted()) {
					result.add(step);
				}
			}
		} else {
			for (M step : this.serverDBModels.values()) {
				if (!step.isDeleted()) {
					result.add(step);
				}
			}
		}
		return result;
	}

	@Override
	public Collection<M> getAllDeleted(boolean local) {
		HashSet<M> result = new HashSet<>();
		if (local) {
			for (M step : this.localDBModels.values()) {
				if (step.isDeleted()) {
					result.add(step);
				}
			}
		} else {
			for (M step : this.serverDBModels.values()) {
				if (step.isDeleted()) {
					result.add(step);
				}
			}
		}
		return result;
	}

	@Override
	public M getbyId(Long id, boolean local) {
		if (local) {
			return this.localDBModels.get(id);
		} else {
			return this.serverDBModels.get(id);
		}
	}

	public void loadServerDB(ProcessCallBack callBack)
			throws ConnectionException {
		if (Configuration.getInstance().getServerConnection() == null) {
			System.out
					.println("Invalid BackupConnection. Try reconnecting first!");
			this.serverDBInit = false;
			throw new ConnectionFailedException("Invalid Backup connection!");
		} else {
			try {
				if (serverDBInit == false) {
					this.sqlQuery.updateConnections();
					this.sqlQuery.createTables();
					this.sqlQuery.init();
				}
				this.serverDBInit = true;
				this.loadAll(false, callBack);
			} catch (SQLTimeoutException e) {
				throw new ConnectionTimeOutException("Connection timed out", e);
			} catch (SQLException e) {
				throw new IllegalStateException("Unexpected SQLError!", e);
			}
		}
	}

	@Override
	public void delete(M model, boolean local) throws ConnectionException {
		try {
			this.sqlQuery.delete(local, model);
		} catch (SQLTimeoutException e) {
			throw new ConnectionTimeOutException("Connection timed out", e);
		} catch (SQLException e) {
			throw new IllegalStateException("Unexpected SQLError!", e);
		}
	}

	public UpdateManager updateLocalDatabase(UpdateManager updateManager,
			ProcessCallBack callBack) throws ConnectionException {
		this.loadServerDB(callBack);
		Collection<Long> compared = new HashSet<>();
		HashSet<Long> keySet = new HashSet<>(serverDBModels.keySet());
		for (Long serverID : keySet) {
			M serverModel = serverDBModels.get(serverID);
			M localModel = localDBModels.get(serverID);
			if (localModel != null) {
				compared.add(serverID);
			} else {
				updateManager.addNoConflict(serverModel, localModel);
				continue;
			}
			if (localModel.getInsert().compareTo(serverModel.getInsert()) != 0) {
				try {
					long dist = localModel.getInsert().getTime()
							- serverModel.getInsert().getTime();
					if (dist > -1000 && dist < 1000) // dirty fix for weird diff
														// in update time
					{
						// difference is minimal / WHY does this happen???
						// remove: updateManager.addNoConflict(serverModel,
						// localModel);
					} else {
						Configuration.getInstance().getConnection()
								.setAutoCommit(false);
						this.reAssignLocalModel(localModel,
								serverDBModels.keySet());
						Configuration.getInstance().getConnection().commit();
						Configuration.getInstance().getConnection()
								.setAutoCommit(false);
						updateManager.addNoConflict(serverModel, null); // id
																		// conflict
																		// resolved
																		// ->
																		// update
																		// normally
					}
				} catch (SQLException e) {
					throw new IllegalStateException("Unexpected SQLError!", e);
				}
			} else if (localModel.getUpdate()
					.compareTo(serverModel.getUpdate()) != 0) {
				long dist = localModel.getUpdate().getTime()
						- serverModel.getUpdate().getTime();
				if (dist > -1000 && dist < 1000) // dirty fix for weird diff in
													// update time
				{
					// difference is minimal / WHY does this happen???
					// remove: updateManager.addNoConflict(serverModel,
					// localModel);
				} else {
					updateManager.addConflict(serverModel, localModel); // conflict
																		// in
																		// updatetime
																		// ->
																		// ask
																		// user!
				}
			} // else exactly same model

		}
		Collection<Long> localIDs = new HashSet<>(this.localDBModels.keySet());
		localIDs.removeAll(compared);
		if (localIDs.size() > 0) {
			for (Long localID : localIDs) {
				updateManager.addMissingConflict(localDBModels.get(localID)); // update
																				// would
																				// delete
																				// this
																				// model
																				// ->
																				// ask
																				// user!
			}
		}
		return updateManager;
	}

	public UpdateManager updateServerDatabase(UpdateManager updateManager,
			ProcessCallBack callBack) throws ConnectionException {
		this.loadServerDB(callBack);
		HashSet<Long> keySet = new HashSet<>(localDBModels.keySet());
		for (Long localID : keySet) {
			M serverModel = serverDBModels.get(localID);
			M localModel = localDBModels.get(localID);
			if (serverModel == null) {
				updateManager.addNoConflict(localModel, serverModel);
				continue;
			}
			if (localModel.getInsert().compareTo(serverModel.getInsert()) != 0) {
				long dist = localModel.getUpdate().getTime()
						- serverModel.getUpdate().getTime();
				if (!(dist > -1000 && dist < 1000)) // dirty fix for weird diff
													// in update time
				{
					try {
						Configuration.getInstance().getConnection()
								.setAutoCommit(false);
						this.reAssignLocalModel(localModel,
								serverDBModels.keySet());
						Configuration.getInstance().getConnection().commit();
						Configuration.getInstance().getConnection()
								.setAutoCommit(true);
						updateManager.addNoConflict(localModel, null); // id
																		// conflict
																		// resolved
																		// ->
																		// update
																		// normally
					} catch (SQLException e) {
						throw new IllegalStateException("Unexpected SQLError!",
								e);
					}
				}
			} else if (localModel.getUpdate()
					.compareTo(serverModel.getUpdate()) != 0) {
				long dist = localModel.getUpdate().getTime()
						- serverModel.getUpdate().getTime();
				if (dist > -1000 && dist < 1000) // dirty fix for weird diff in
													// update time
				{
					// difference is minimal / WHY does this happen???
					// updateManager.addNoConflict(localModel, serverModel);
				} else {
					updateManager.addConflict(localModel, serverModel); // conflict
																		// in
																		// updatetime
																		// ->
																		// ask
																		// user!
				}
			}// else no change

		}
		return updateManager;
	}
}
