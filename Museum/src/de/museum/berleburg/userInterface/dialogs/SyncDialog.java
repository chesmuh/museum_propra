package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.interfaces.Pair;
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
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.logicAccess.SyncModel;
import de.museum.berleburg.logicAccess.Updatelist;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;
import de.museum.berleburg.userInterface.table.BooleanSyncColorRenderer;
import de.museum.berleburg.userInterface.table.StringSyncColorRenderer;
import de.museum.berleburg.userInterface.table.TableHeaderRenderer;
import de.museum.berleburg.userInterface.table.TableModelSync;

public class SyncDialog extends JDialog {

	private static final long serialVersionUID = -2251893424052027747L;
	private JPanel buttonPanel = new JPanel();
	private JTable tableLocal = new JTable();
	private JTable tableSync = new JTable();
	private JTable tableServer = new JTable();
	private final JButton btnSync;
	private final JTextPane textLocal = new JTextPane();
	private final JTextPane textServer = new JTextPane();
	private final JPanel rightButtonPanel = new JPanel();
	private final JButton btnRN = new JButton("<");
	private final JButton btnRS = new JButton(">");
	private final JButton btnLN = new JButton(">");
	private final JButton btnLS = new JButton("<");
	private final JPanel leftButtonPanel = new JPanel();
	private boolean isdCommit;
	private Updatelist prepareList;
	private ArrayList<SyncModel> syncList = new ArrayList<>();
	private ArrayList<SyncModel> localList = new ArrayList<>();
	private ArrayList<SyncModel> serverList = new ArrayList<>();
	private final JLabel lblLokaleDatenbank = new JLabel("Lokale Datenbank");
	private final JLabel lblZuSynchronisieren = new JLabel("Zu Synchronisieren");
	private final JLabel lblExterneDatenbank = new JLabel("Externe Datenbank");
	private int currentRow;
	private JScrollPane scrollPaneLocal;
	private JScrollPane scrollPaneServer;
	TableModelSync modelSync;
	TableModelSync modelServer;
	TableModelSync modelLocal;
	StringBuilder localsB;
	StringBuilder serversB;

	/**
	 * @author Frank Hülsmann, Benedikt
	 * 
	 *         Create the dialog.
	 */
	public SyncDialog(boolean isCommit, JFrame owner, boolean modal) {
		super(owner, modal);
		this.isdCommit = isCommit;
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainGUI.class
								.getResource("/de/museum/berleburg/userInterface/logo.png")));
		syncList.clear();
		if (isdCommit) {
			btnSync = new JButton("Commit");
			setTitle("Commit");
			try {
				ProcessDialog dialog = new ProcessDialog(this,
						btnSync.getLocation());
				dialog.setVisible(true);
				prepareList = Access.prepareCommit(dialog);
				dialog.dispose();
			} catch (ConnectionException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
			}
			btnRN.setEnabled(false);
			btnRS.setEnabled(false);

		} else {
			btnSync = new JButton("Update");
			setTitle("Update");
			try {
				ProcessDialog dialog = new ProcessDialog(this,
						btnSync.getLocation());
				dialog.setVisible(true);
				prepareList = Access.prepareUpdate(dialog);
				dialog.dispose();
			} catch (ConnectionException e1) {
				JOptionPane.showMessageDialog(this, e1.getMessage(),
						"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
			}
			btnLN.setEnabled(false);
			btnLS.setEnabled(false);

		}
		localList = prepareList.getLocalList();
		serverList = prepareList.getServerList();

		setBounds(100, 100, 750, 510);
		getContentPane().setLayout(
				new MigLayout("",
						"[grow][grow][grow][grow][][grow][][grow][grow]",
						"[][grow][][grow][]"));

		getContentPane().add(lblLokaleDatenbank, "cell 0 0");

		getContentPane().add(lblZuSynchronisieren, "cell 5 0");

		getContentPane().add(lblExterneDatenbank, "cell 8 0");

		getContentPane().add(
				new JScrollPane(tableSync,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				"flowx,cell 4 1 2 1,alignx center,growy");

		tableLocal.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int row = tableLocal.getSelectedRow();

						tableServer.changeSelection(row, 1, false, false);

						if (row >= 0 && row < tableLocal.getRowCount()) {
							currentRow = row;

							toAreaLocal();
							toAreaServer();
							addConflictText();

						} else {
							tableServer.clearSelection();
							tableLocal.clearSelection();
						}
					}
				});

		tableServer.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						int row = tableServer.getSelectedRow();

						tableLocal.changeSelection(row, 1, false, false);

						if (row >= 0 && row < tableServer.getRowCount()) {
							currentRow = row;

							toAreaLocal();
							toAreaServer();
							addConflictText();

						} else {
							tableServer.clearSelection();
							tableLocal.clearSelection();
						}
					}
				});

		btnLN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<SyncModel> local = getCheckedLocalModels();
				if (!local.isEmpty()) {
					syncList.addAll(local);
					updateTableSync();
					updateTableLocal();
					updateTableServer();
				}
			}
		});

		btnLS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<SyncModel> sync = getCheckedSyncModels();
				if (!sync.isEmpty()) {
					syncList.removeAll(sync);
					updateTableSync();
					updateTableLocal();
					updateTableServer();
				}
			}
		});

		leftButtonPanel.setLayout(new BorderLayout());
		leftButtonPanel.add(btnLN, BorderLayout.NORTH);
		leftButtonPanel.add(btnLS, BorderLayout.CENTER);

		btnRN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<SyncModel> server = getCheckedServerModels();
				if (!server.isEmpty()) {
					syncList.addAll(server);
					updateTableSync();
					updateTableLocal();
					updateTableServer();
				}
			}
		});

		btnRS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<SyncModel> sync = getCheckedSyncModels();
				if (!sync.isEmpty()) {
					syncList.removeAll(sync);
					updateTableSync();
					updateTableLocal();
					updateTableServer();
				}
			}
		});

		rightButtonPanel.setLayout(new BorderLayout());
		rightButtonPanel.add(btnRN, BorderLayout.NORTH);
		rightButtonPanel.add(btnRS, BorderLayout.CENTER);

		getContentPane().add(leftButtonPanel,
				"cell 2 1,alignx center,aligny center");
		getContentPane().add(rightButtonPanel,
				"cell 6 1 2 1,alignx center,aligny center");
		scrollPaneServer = new JScrollPane(tableServer,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(scrollPaneServer,
				"flowx,cell 8 1,alignx right,growy");

		getContentPane().add(buttonPanel,
				"cell 5 3,alignx center,aligny bottom");

		btnSync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						int answer = JOptionPane
								.showConfirmDialog(
										null,
										"Sind Sie sicher, dass die gewählten Eingaben korrekt sind?",
										"Frage", JOptionPane.YES_NO_OPTION,
										JOptionPane.QUESTION_MESSAGE);
						if (answer == JOptionPane.YES_OPTION) {
							TreeNodeObject selectedMuseum = MuseumMainPanel
									.getInstance().getMuseumTreeNode();
							if (isdCommit) {
								for (SyncModel step : localList) {
									if (step.isConflict()) {
										try {
											prepareList.ignoreConflict(step);
										} catch (InvalidArgumentsException e) {
											JOptionPane.showMessageDialog(null, e.getMessage(),
													"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
										}
									} else {
										prepareList.ignoreUpdate(step);
									}
								}
								for (SyncModel step : syncList) {
									if (step.isConflict()) {
										try {
											prepareList.solveConflict(step);
										} catch (InvalidArgumentsException e) {
											JOptionPane.showMessageDialog(null, e.getMessage(),
													"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
										}
									}
								}
								try {
									ProcessDialog dialog = new ProcessDialog(
											SyncDialog.this, btnSync
													.getLocation());
									dialog.setVisible(true);
									Access.commit(dialog);
									dialog.dispose();
								} catch (ConnectionException e1) {
									JOptionPane.showMessageDialog(null, e1.getMessage(),
											"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
								}
							} else {
								for (SyncModel step : serverList) {
									if (step.isConflict()) {
										try {
											prepareList.ignoreConflict(step);
										} catch (InvalidArgumentsException e) {
											JOptionPane.showMessageDialog(null, e.getMessage(),
													"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
										}
									} else {
										prepareList.ignoreUpdate(step);
									}
								}
								for (SyncModel step : syncList) {
									if (step.isConflict()) {
										try {
											prepareList.solveConflict(step);
										} catch (InvalidArgumentsException e) {
											JOptionPane.showMessageDialog(null, e.getMessage(),
													"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
										}
									}
								}
								try {
									ProcessDialog dialog = new ProcessDialog(
											SyncDialog.this, btnSync
													.getLocation());
									dialog.setVisible(true);
									Access.update(dialog);
									dialog.dispose();
								} catch (ConnectionException e1) {
									JOptionPane.showMessageDialog(null, e1.getMessage(),
											"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
								}
							}
							SyncDialog.this.closeDialog();
							MuseumMainPanel.getInstance().refreshComboBox();
							try {
								MuseumMainPanel
										.getInstance()
										.setMuseumToSelect(
												Access.searchMuseumID(selectedMuseum
														.getMuseumId()));
							} catch (MuseumNotFoundException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(),
										"Datenbankfehler", JOptionPane.ERROR_MESSAGE);
							}
							MuseumMainPanel.getInstance().refreshComboBox();
						}
					}
				}).start();
			}
		});

		buttonPanel.add(btnSync);
		textServer.setEditable(false);
		textServer.setText("Test");

		scrollPaneLocal = new JScrollPane(tableLocal,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		getContentPane().add(new JScrollPane(textServer),
				"flowx,cell 8 3 1 2,grow");
		getContentPane().add(scrollPaneLocal, "cell 0 1,alignx left,growy");
		textLocal.setEditable(false);
		textLocal.setText("Test");

		getContentPane().add(new JScrollPane(textLocal), "cell 0 3 1 2,grow");

		JScrollBar scrollBarLocal = scrollPaneLocal.getVerticalScrollBar();
		scrollBarLocal.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {

				Point p = scrollPaneLocal.getViewport().getViewPosition();
				scrollPaneServer.getViewport().setViewPosition(p);

			}
		});

		JScrollBar scrollBarServer = scrollPaneServer.getVerticalScrollBar();
		scrollBarServer.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {

				Point p = scrollPaneServer.getViewport().getViewPosition();
				scrollPaneLocal.getViewport().setViewPosition(p);

			}
		});

		updateTableLocal();
		updateTableServer();
		updateTableSync();

		tableLocal.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				TableModelSync tableModelLocal = (TableModelSync) tableLocal
						.getModel();
				// TableModelSync tableModelSync = (TableModelSync)
				// tableSync.getModel();
				//
				int row = tableLocal.rowAtPoint(e.getPoint());
				// long iD = tableModelLocal.getId(row);
				//
				// int toRow = tableModelSync.getRowById(iD);
				// if((!isdCommit) && (toRow != -1))
				// {
				//
				// tableSync.changeSelection(toRow, 1, false, false);
				// }

				tableServer.changeSelection(row, 1, false, false);

				if (row >= 0 && row < tableLocal.getRowCount()) {

					row = tableLocal.getSelectedRow();
					currentRow = row;

					toAreaLocal();
					toAreaServer();
					addConflictText();

				} else {

					tableLocal.clearSelection();
				}

				int rowIndex = tableLocal.getSelectedRow();
				if (rowIndex < 0) {
					return;
				}

				if (e.getClickCount() == 2
						&& e.getModifiers() == InputEvent.BUTTON1_MASK) {

					tableModelLocal.setCheckBox(true, row);
					tableModelLocal.fireTableCellUpdated(rowIndex, rowIndex);
				}
			}
		});

		tableServer.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int row = tableServer.rowAtPoint(e.getPoint());
				tableLocal.changeSelection(row, 1, false, false);

				if (row >= 0 && row < tableServer.getRowCount()) {
					row = tableServer.getSelectedRow();
					currentRow = row;

					toAreaLocal();
					toAreaServer();
					addConflictText();

				} else {

					tableServer.clearSelection();
				}

				int rowIndex = tableServer.getSelectedRow();
				if (rowIndex < 0) {
					return;
				}
			}
		});

		tableSync.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				int row = tableSync.rowAtPoint(e.getPoint());
				// int col = tableLocal.columnAtPoint(e.getPoint());

				if (row >= 0 && row < tableSync.getRowCount()) {
					row = tableSync.getSelectedRow();

				} else {

					tableSync.clearSelection();
				}

				int rowIndex = tableSync.getSelectedRow();
				if (rowIndex < 0) {
					return;
				}
			}
		});

		this.setMinimumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(800, 600));

		if (prepareList.getConflictList().isEmpty()
				&& prepareList.getNoConflictList().isEmpty()) {
			if (isdCommit)
				JOptionPane.showMessageDialog(this,
						"Ihr System enthält keine neuen Daten für den Server!");
			else
				JOptionPane.showMessageDialog(this, "Ihr System ist aktuell!");
			this.closeDialog();
		} else {
			this.setVisible(true);
		}
	}

	public void toAreaLocal() {
		TableModelSync tableModel = (TableModelSync) tableLocal.getModel();
		SyncModel model = SyncModel.getSyncModelById(tableModel
				.getId(currentRow));

		localsB = new StringBuilder();

		localsB.append("Typ: " + model.getType() + " \n");
		localsB.append("Name: " + model.getName() + " \n");
		localsB.append("Zuletzt geändert: " + model.getTimestamp());

		textLocal.setText(localsB.toString());

	}

	public void toAreaServer() {
		TableModelSync tableModel = (TableModelSync) tableServer.getModel();
		SyncModel model = SyncModel.getSyncModelById(tableModel
				.getId(currentRow));

		serversB = new StringBuilder();

		serversB.append("Typ: " + model.getType() + " \n");
		serversB.append("Name: " + model.getName() + " \n");
		serversB.append("Zuletzt geändert: " + model.getTimestamp());

		textServer.setText(serversB.toString());

	}

	public void addConflictText() {
		TableModelSync tableloclaModel = (TableModelSync) tableLocal.getModel();
		SyncModel localmodel = SyncModel.getSyncModelById(tableloclaModel
				.getId(currentRow));
		TableModelSync tableserverModel = (TableModelSync) tableServer
				.getModel();
		SyncModel servermodel = SyncModel.getSyncModelById(tableserverModel
				.getId(currentRow));

		if (localmodel.isConflict()) {
			if ((localmodel.getModel() != null && servermodel.getModel() != null)) {
				localsB.append("\nEs gibt Unterschiede bei: \n");
				serversB.append("\nEs gibt Unterschiede bei: \n");
				if ((((DatabaseElement) localmodel.getModel()).isDeleted() && !((DatabaseElement) servermodel
						.getModel()).isDeleted())
						|| (!((DatabaseElement) localmodel.getModel())
								.isDeleted() && ((DatabaseElement) servermodel
								.getModel()).isDeleted())) {
					if (((DatabaseElement) localmodel.getModel()).isDeleted()) {
						localsB.append("Objekt gelöscht!\n");
						serversB.append("Objekt lokal gelöscht!\n");
					} else {
						localsB.append("Objekt wurde vom Server gelöscht!\n");
						serversB.append("Objekt gelöscht!\n");
					}
				} else {
					if (localmodel.getModel() instanceof Address
							&& servermodel.getModel() instanceof Address) {
						if (!((Address) localmodel.getModel()).getCountry()
								.equals(((Address) servermodel.getModel())
										.getCountry())) {
							localsB.append("Bundesland: "
									+ ((Address) localmodel.getModel())
											.getCountry() + "\n");
							serversB.append("Bundesland: "
									+ ((Address) servermodel.getModel())
											.getCountry() + "\n");
						}
						if (!((Address) localmodel.getModel()).getHousenumber()
								.equals(((Address) servermodel.getModel())
										.getHousenumber())) {
							localsB.append("Hausnummer: "
									+ ((Address) localmodel.getModel())
											.getHousenumber() + "\n");
							serversB.append("Hausnummer: "
									+ ((Address) servermodel.getModel())
											.getHousenumber() + "\n");
						}
						if (!((Address) localmodel.getModel()).getState()
								.equals(((Address) servermodel.getModel())
										.getState())) {
							localsB.append("Land: "
									+ ((Address) localmodel.getModel())
											.getState() + "\n");
							serversB.append("Land: "
									+ ((Address) servermodel.getModel())
											.getState() + "\n");
						}
						if (!((Address) localmodel.getModel()).getStreet()
								.equals(((Address) servermodel.getModel())
										.getStreet())) {
							localsB.append("Straße: "
									+ ((Address) localmodel.getModel())
											.getStreet() + "\n");
							serversB.append("Straße: "
									+ ((Address) servermodel.getModel())
											.getStreet() + "\n");
						}
						if (!((Address) localmodel.getModel()).getTown()
								.equals(((Address) servermodel.getModel())
										.getTown())) {
							localsB.append("Stadt: "
									+ ((Address) localmodel.getModel())
											.getTown() + "\n");
							serversB.append("Stadt: "
									+ ((Address) servermodel.getModel())
											.getTown() + "\n");
						}
						if (!((Address) localmodel.getModel()).getZipcode()
								.equals(((Address) servermodel.getModel())
										.getZipcode())) {
							localsB.append("PLZ: "
									+ ((Address) localmodel.getModel())
											.getZipcode() + "\n");
							serversB.append("PLZ: "
									+ ((Address) servermodel.getModel())
											.getZipcode() + "\n");
						}
					} else if (localmodel.getModel() instanceof Category
							&& servermodel.getModel() instanceof Category) {
						if (((Category) localmodel.getModel()).getMuseum_id() != (((Category) servermodel
								.getModel()).getMuseum_id())) {
							localsB.append("Museum: "
									+ ((Category) localmodel.getModel())
											.getMuseum().getName() + "\n");
							serversB.append("Museum: "
									+ ((Category) servermodel.getModel())
											.getMuseum().getName() + "\n");
						}
						if (((Category) localmodel.getModel()).getParent_id() != (((Category) servermodel
								.getModel()).getParent_id())) {
							Category parent = ((Category) localmodel.getModel())
									.getParent();
							String parentName = parent == null ? ((Category) localmodel
									.getModel()).getMuseum().getName() : parent
									.getName();
							localsB.append("Oberkategorie: " + parentName
									+ "\n");
							parent = ((Category) localmodel.getModel())
									.getParent();
							parentName = parent == null ? ((Category) localmodel
									.getModel()).getMuseum().getName() : parent
									.getName();
							serversB.append("Oberkategorie: " + parentName
									+ "\n");
						}
					} else if (localmodel.getModel() instanceof Contact
							&& servermodel.getModel() instanceof Contact) {
						if (!((Contact) localmodel.getModel()).getEmail()
								.equals(((Contact) servermodel.getModel())
										.getEmail())) {
							localsB.append("Email: "
									+ ((Contact) localmodel.getModel())
											.getEmail() + "\n");
							serversB.append("Email: "
									+ ((Contact) servermodel.getModel())
											.getEmail() + "\n");
						}
						if (!((Contact) localmodel.getModel()).getFax().equals(
								((Contact) servermodel.getModel()).getFax())) {
							localsB.append("Fax: "
									+ ((Contact) localmodel.getModel())
											.getFax() + "\n");
							serversB.append("Fax: "
									+ ((Contact) servermodel.getModel())
											.getFax() + "\n");
						}
						if (!((Contact) localmodel.getModel()).getFon().equals(
								((Contact) servermodel.getModel()).getFon())) {
							localsB.append("Telefonnummer: "
									+ ((Contact) localmodel.getModel())
											.getFon() + "\n");
							serversB.append("Telefonnummer: "
									+ ((Contact) servermodel.getModel())
											.getFon() + "\n");
						}
						if (!((Contact) localmodel.getModel()).getForename()
								.equals(((Contact) servermodel.getModel())
										.getForename())) {
							localsB.append("Vorname: "
									+ ((Contact) localmodel.getModel())
											.getForename() + "\n");
							serversB.append("Vorname: "
									+ ((Contact) servermodel.getModel())
											.getForename() + "\n");
						}
						if (!((Contact) localmodel.getModel()).getName()
								.equals(((Contact) servermodel.getModel())
										.getName())) {
							localsB.append("Name: "
									+ ((Contact) localmodel.getModel())
											.getName() + "\n");
							serversB.append("Name: "
									+ ((Contact) servermodel.getModel())
											.getName() + "\n");
						}
						if (!((Contact) localmodel.getModel()).getDescription()
								.equals(((Contact) servermodel.getModel())
										.getDescription())) {
							localsB.append("Beschreibung :"
									+ ((Contact) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung :"
									+ ((Contact) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof Exhibit
							&& servermodel.getModel() instanceof Exhibit) {
						if (!((Exhibit) localmodel.getModel()).getCategory_id()
								.equals(((Exhibit) servermodel.getModel())
										.getCategory_id())) {
							localsB.append("Kategorie: "
									+ ((Exhibit) localmodel.getModel())
											.getCategory().getName() + "\n");
							serversB.append("Kategorie: "
									+ ((Exhibit) servermodel.getModel())
											.getCategory().getName() + "\n");
						}
						if (((Exhibit) localmodel.getModel()).getCount() != (((Exhibit) servermodel
								.getModel()).getCount())) {
							localsB.append("Anzahl: "
									+ ((Exhibit) localmodel.getModel())
											.getCount() + "\n");
							serversB.append("Anzahl: "
									+ ((Exhibit) servermodel.getModel())
											.getCount() + "\n");
						}
						if (!((Exhibit) localmodel.getModel()).getLabels()
								.equals(((Exhibit) servermodel.getModel())
										.getLabels())) {
							localsB.append("Vorname: "
									+ ((Exhibit) localmodel.getModel())
											.getCategory().getName() + "\n");
							serversB.append("Vorname: "
									+ ((Exhibit) servermodel.getModel())
											.getCategory().getName() + "\n");
						}
						if (!((Exhibit) localmodel.getModel()).getDescription()
								.equals(((Exhibit) servermodel.getModel())
										.getDescription())) {
							localsB.append("Beschreibung: "
									+ ((Exhibit) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung: "
									+ ((Exhibit) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof History
							&& servermodel.getModel() instanceof History) {
						if (((History) localmodel.getModel()).getCount() != (((History) servermodel
								.getModel()).getCount())) {
							localsB.append("Anzahl: "
									+ ((History) localmodel.getModel())
											.getCount() + "\n");
							serversB.append("Anzahl: "
									+ ((History) servermodel.getModel())
											.getCount() + "\n");
						}
						if (!((History) localmodel.getModel()).getDescription()
								.equals(((History) servermodel.getModel())
										.getDescription())) {
							localsB.append("Beschreibung: "
									+ ((History) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung: "
									+ ((History) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof Image
							&& servermodel.getModel() instanceof Image) {
						if (((Image) localmodel.getModel()).getExhibit_id() != (((Image) servermodel
								.getModel()).getExhibit_id())) {
							localsB.append("Exponat: "
									+ ((Image) localmodel.getModel())
											.getExhibit().getName() + "\n");
							serversB.append("Exponat: "
									+ ((Image) servermodel.getModel())
											.getExhibit().getName() + "\n");
						}
						if (!((Image) localmodel.getModel()).getName().equals(
								((Image) servermodel.getModel()).getName())) {
							localsB.append("Exponat: "
									+ ((Image) localmodel.getModel()).getName()
									+ "\n");
							serversB.append("Exponat: "
									+ ((Image) servermodel.getModel())
											.getName() + "\n");
						}
					} else if (localmodel.getModel() instanceof Label
							&& servermodel.getModel() instanceof Label) {
						// nothing to check here
					} else if (localmodel.getModel() instanceof Museum
							&& servermodel.getModel() instanceof Museum) {
						if (((Museum) localmodel.getModel()).getAddress_id() != (((Museum) servermodel
								.getModel()).getAddress_id())) {
							localsB.append(((Museum) localmodel.getModel())
									.getAddress().toString() + "\n");
							serversB.append(((Museum) servermodel.getModel())
									.getAddress().toString() + "\n");
						}
						if (!((Museum) localmodel.getModel()).getDescription()
								.equals(((Museum) servermodel.getModel())
										.getDescription())) {
							localsB.append("Beschreibung: "
									+ ((Museum) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung: "
									+ ((Museum) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof Outsourced
							&& servermodel.getModel() instanceof Outsourced) {
						if (!((Outsourced) localmodel.getModel())
								.getAddress_id().equals(
										((Outsourced) servermodel.getModel())
												.getAddress_id())) {
							localsB.append(((Outsourced) localmodel.getModel())
									.getAddress().toString() + "\n");
							serversB.append(((Outsourced) servermodel
									.getModel()).getAddress().toString() + "\n");
						}
						if (!((Outsourced) localmodel.getModel())
								.getStartDate().equals(
										((Outsourced) servermodel.getModel())
												.getStartDate())) {
							localsB.append("Beschreibung: "
									+ ((Outsourced) localmodel.getModel())
											.getStartDate().toString() + "\n");
							serversB.append("Beschreibung: "
									+ ((Outsourced) servermodel.getModel())
											.getStartDate().toString() + "\n");
						}
						if (!((Outsourced) localmodel.getModel()).getEndDate()
								.equals(((Outsourced) servermodel.getModel())
										.getEndDate())) {
							localsB.append("Beschreibung: "
									+ ((Outsourced) localmodel.getModel())
											.getEndDate().toString() + "\n");
							serversB.append("Beschreibung: "
									+ ((Outsourced) servermodel.getModel())
											.getEndDate().toString() + "\n");
						}
						if (!((Outsourced) localmodel.getModel())
								.getDescription().equals(
										((Outsourced) servermodel.getModel())
												.getDescription())) {
							localsB.append("Beschreibung: "
									+ ((Outsourced) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung: "
									+ ((Outsourced) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof Section
							&& servermodel.getModel() instanceof Section) {
						if (((Section) localmodel.getModel()).getMuseum_id() != (((Section) servermodel
								.getModel()).getMuseum_id())) {
							localsB.append("Museum: "
									+ ((Section) localmodel.getModel())
											.getMuseum().getName() + "\n");
							serversB.append("Museum: "
									+ ((Section) servermodel.getModel())
											.getMuseum().getName() + "\n");
						}
						if (!((Section) localmodel.getModel()).getParent_id()
								.equals(((Section) servermodel.getModel())
										.getParent())) {
							Section parent = ((Section) localmodel.getModel())
									.getParent();
							String parentName = parent == null ? ((Section) localmodel
									.getModel()).getMuseum().getName() : parent
									.getName();
							localsB.append("Obersektion: " + parentName + "\n");
							parent = ((Section) localmodel.getModel())
									.getParent();
							parentName = parent == null ? ((Section) localmodel
									.getModel()).getMuseum().getName() : parent
									.getName();
							serversB.append("Obersektion: " + parentName + "\n");
						}
						if (!((Section) localmodel.getModel()).getDescription()
								.equals(((Section) servermodel.getModel())
										.getDescription())) {
							localsB.append("Beschreibung: "
									+ ((Section) localmodel.getModel())
											.getDescription() + "\n");
							serversB.append("Beschreibung: "
									+ ((Section) servermodel.getModel())
											.getDescription() + "\n");
						}
					} else if (localmodel.getModel() instanceof Role
							&& servermodel.getModel() instanceof Role) {
						// nothing to check here too
					}
				}
			}

		}
		textLocal.setText(localsB.toString());
		textServer.setText(serversB.toString());
	}

	public void updateTableLocal() {

		// localList = Access.sortSyncModel(localList);
		ArrayList<Pair<SyncModel, SyncModel>> help = Access
				.sortSyncPair(prepareList.getCompleteList());
		localList = new ArrayList<SyncModel>();
		for (int i = 0; i < help.size(); i++) {
			localList.add(help.get(i).getLeft());
		}
		ArrayList<SyncModel> help2 = new ArrayList<>();
		for (SyncModel step : syncList) {
			help2.add(prepareList.getCompleteList().get(step.getListPosition())
					.getLeft());
		}

		localList.removeAll(help2);

		modelLocal = new TableModelSync(localList);

		tableLocal.setModel(modelLocal);
		tableLocal.setBorder(null);
		tableLocal.setRowSelectionAllowed(true);
		tableLocal.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableLocal.setDefaultRenderer(String.class,
				new StringSyncColorRenderer());
		tableLocal.setDefaultRenderer(Boolean.class,
				new BooleanSyncColorRenderer());

		tableLocal.getTableHeader().setReorderingAllowed(false);
		tableLocal
				.getColumnModel()
				.getColumn(0)
				.setHeaderRenderer(
						new TableHeaderRenderer(tableLocal.getTableHeader()));

		// CheckBoxSpaltenGröße setzen
		tableLocal.getColumnModel().getColumn(0).setMinWidth(20);
		tableLocal.getColumnModel().getColumn(0).setMaxWidth(20);

		modelLocal.fireTableRowsUpdated(0, modelLocal.getRowCount());
	}

	public void updateTableServer() {
		// serverList = Access.sortSyncModel(serverList);
		ArrayList<Pair<SyncModel, SyncModel>> help = Access
				.sortSyncPair(prepareList.getCompleteList());
		serverList = new ArrayList<SyncModel>();
		for (int i = 0; i < help.size(); i++) {
			serverList.add(help.get(i).getRight());
		}
		ArrayList<SyncModel> help2 = new ArrayList<>();
		for (SyncModel step : syncList) {
			help2.add(prepareList.getCompleteList().get(step.getListPosition())
					.getRight());
		}

		serverList.removeAll(help2);

		modelServer = new TableModelSync(serverList);

		tableServer.setModel(modelServer);
		tableServer.setBorder(null);
		tableServer.setRowSelectionAllowed(true);
		tableServer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableServer.setDefaultRenderer(String.class,
				new StringSyncColorRenderer());
		tableServer.setDefaultRenderer(Boolean.class,
				new BooleanSyncColorRenderer());

		tableServer.getTableHeader().setReorderingAllowed(false);
		tableServer
				.getColumnModel()
				.getColumn(0)
				.setHeaderRenderer(
						new TableHeaderRenderer(tableServer.getTableHeader()));

		// CheckBoxSpaltenGröße setzen
		tableServer.getColumnModel().getColumn(0).setMinWidth(20);
		tableServer.getColumnModel().getColumn(0).setMaxWidth(20);
		modelServer.fireTableRowsUpdated(0, modelServer.getRowCount());
	}

	public void updateTableSync() {
		syncList = Access.sortSyncModel(syncList);

		modelSync = new TableModelSync(syncList);

		tableSync.setModel(modelSync);
		tableSync.setBorder(null);
		tableSync.setRowSelectionAllowed(true);
		tableSync.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableSync.setDefaultRenderer(String.class,
				new StringSyncColorRenderer());
		tableSync.setDefaultRenderer(Boolean.class,
				new BooleanSyncColorRenderer());

		tableSync.getTableHeader().setReorderingAllowed(false);
		tableSync
				.getColumnModel()
				.getColumn(0)
				.setHeaderRenderer(
						new TableHeaderRenderer(tableSync.getTableHeader()));

		// CheckBoxSpaltenGröße setzen
		tableSync.getColumnModel().getColumn(0).setMinWidth(20);
		tableSync.getColumnModel().getColumn(0).setMaxWidth(20);
		modelSync.fireTableRowsUpdated(0, modelSync.getRowCount());
	}

	public ArrayList<SyncModel> getCheckedLocalModels() {
		ArrayList<SyncModel> list = new ArrayList<>();

		for (int i = 0; i < modelLocal.getRowCount(); i++) {
			Boolean checked = (Boolean) modelLocal.getValueAt(i, 0);
			if (checked) {
				list.add(SyncModel.getSyncModelById(modelLocal.getId(i)));
			}

		}
		return list;
	}

	public ArrayList<SyncModel> getCheckedSyncModels() {
		ArrayList<SyncModel> list = new ArrayList<>();

		for (int i = 0; i < modelSync.getRowCount(); i++) {
			Boolean checked = (Boolean) modelSync.getValueAt(i, 0);
			if (checked) {
				list.add(SyncModel.getSyncModelById(modelSync.getId(i)));
			}

		}
		return list;

	}

	public ArrayList<SyncModel> getCheckedServerModels() {
		ArrayList<SyncModel> list = new ArrayList<>();

		for (int i = 0; i < modelServer.getRowCount(); i++) {
			Boolean checked = (Boolean) modelServer.getValueAt(i, 0);
			if (checked) {
				list.add(SyncModel.getSyncModelById(modelServer.getId(i)));
			}

		}
		return list;
	}

	public void closeDialog() {
		SyncModel.reset();
		dispose();
	}
}
