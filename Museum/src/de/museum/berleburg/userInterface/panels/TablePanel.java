package de.museum.berleburg.userInterface.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.AddToExhibition;
import de.museum.berleburg.userInterface.dialogs.AddToLoan;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;
import de.museum.berleburg.userInterface.dialogs.MassChange;
import de.museum.berleburg.userInterface.models.TableSearchModel;
import de.museum.berleburg.userInterface.table.BooleanMainColorRenderer;
import de.museum.berleburg.userInterface.table.StringMainColorRenderer;
import de.museum.berleburg.userInterface.table.TableHeaderRenderer;
import de.museum.berleburg.userInterface.table.TableModelMain;

public class TablePanel extends JPanel {

	/**
	 * TablePanel
	 * 
	 * @author Frank Hülsmann
	 */

	private static final long serialVersionUID = 7893403462158931316L;
	private JTable table;
	private TableModelMain model;
	private JPopupMenu popUp;
	private static TablePanel instance = null;
	private static int currentRow;
	private static ArrayList<Long> checkedList = new ArrayList<Long>();
	private ArrayList<Long> deleteIds = new ArrayList<Long>();
	private ArrayList<Exhibit> currentList = new ArrayList<Exhibit>();
	private boolean isSelected = false;
	private TableSearchModel searchModel;

	private TablePanel() {
		instance = this;
		setBorder(null);
		setLayout(new GridBagLayout());

		ArrayList<Long> emptyList = new ArrayList<Long>();

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);

		table = new JTable();

		updateTable(0, "", emptyList, emptyList, emptyList, emptyList,
				emptyList, emptyList, emptyList, emptyList, false, true);

		table.setBackground(Color.white);

		table.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
	
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());
				long id = model.getId(row);
				
				checkAllCheckBoxes();

				if (row >= 0 && row < table.getRowCount()) {
					currentRow = table.getSelectedRow();
					isSelected = true;
					TableButtonPanel.getInstance().setButtonsEnabled(true);

				} else {
					table.clearSelection();
				}

//				int rowIndex = table.getSelectedRow();
//				if (rowIndex < 0) {
//					return;
//				}

				if (e.getClickCount() == 2
						&& e.getModifiers() == InputEvent.BUTTON1_MASK
						&& col != 0) {
					try {
						EditExhibit dialog = new EditExhibit(Access
								.searchExhibitID(model.getId(row)));
						dialog.enableEditing(false);
					} catch (Exception e1) {
						// JOptionPane.showConfirmDialog(TablePanel.this,
						// e1.getMessage());
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"Fehler", JOptionPane.ERROR_MESSAGE);
						JOptionPane.showMessageDialog(TablePanel.this,
								e1.getMessage());
					}
				}

				if (e.getClickCount() == 1
						&& e.getModifiers() == InputEvent.BUTTON3_MASK) {
					table.changeSelection(row, col, false, false);
					currentRow = table.getSelectedRow();
					buildPopup(id);
					
					popUp.show(e.getComponent(), e.getX(), e.getY());

				}

			}

		});
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {

						int row = table.getSelectedRow();
						if (row != -1) {
							Long id = model.getId(row);
							Exhibit exhibit = null;
							try {
								exhibit = Access.searchExhibitID(id);
							} catch (ExhibitNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
							MainGUI.getDetailPanel().setDetails(exhibit);
						}
					}
				});

		table.getColumnModel()
				.getColumn(0)
				.setHeaderRenderer(
						new TableHeaderRenderer(table.getTableHeader()));

		add(new JScrollPane(table), c);
		setVisible(true);
	}

	/**
	 * Returns the current instance
	 * 
	 * @return instance
	 */

	public static TablePanel getInstance() {
		if (instance == null) {
			instance = new TablePanel();
		}
		return instance;
	}

	/**
	 * Table Update
	 * 
	 * @param list
	 */

	public void updateTable(long MuseumID, ArrayList<Long> proCategory,
			ArrayList<Long> contraCategory, ArrayList<Long> proSection,
			ArrayList<Long> contraSection, ArrayList<Long> proLabel,
			ArrayList<Long> contraLabel, ArrayList<Long> proOutsourced,
			ArrayList<Long> contraOutsourced) {

		currentList.clear();

		searchModel = new TableSearchModel(MuseumID, "", proCategory,
				contraCategory, proSection, contraSection, proLabel,
				contraLabel, proOutsourced, contraOutsourced, false, false);

		currentList = searchModel.getResultList();
		model = new TableModelMain(currentList);
		table.setModel(model);
		table.setBorder(null);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(String.class, new StringMainColorRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanMainColorRenderer());

		table.getTableHeader().setReorderingAllowed(false);

		// CheckBoxSpaltenGröße setzen
		table.getColumnModel().getColumn(0).setMinWidth(20);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		model.fireTableRowsUpdated(0, model.getRowCount());
		TableButtonPanel.getInstance().setButtonsEnabled(false);
		setVisible(true);
	}

	/**
	 * Table Update
	 * 
	 * @param MuseumID
	 * @param exhibitName
	 * @param proCategory
	 * @param contraCategory
	 * @param proSection
	 * @param contraSection
	 * @param proLabel
	 * @param contraLabel
	 * @param proOutsourced
	 * @param contraOutsourced
	 * @param normalsearch
	 * @param isEmpty
	 */
	public void updateTable(long MuseumID, String exhibitName,
			ArrayList<Long> proCategory, ArrayList<Long> contraCategory,
			ArrayList<Long> proSection, ArrayList<Long> contraSection,
			ArrayList<Long> proLabel, ArrayList<Long> contraLabel,
			ArrayList<Long> proOutsourced, ArrayList<Long> contraOutsourced,
			boolean normalsearch, boolean isEmpty) {

		currentList.clear();

		searchModel = new TableSearchModel(MuseumID, exhibitName, proCategory,
				contraCategory, proSection, contraSection, proLabel,
				contraLabel, proOutsourced, contraOutsourced, normalsearch,
				isEmpty);
		currentList = searchModel.getResultList();

		model = new TableModelMain(currentList);
		table.setModel(model);
		table.setBorder(null);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(String.class, new StringMainColorRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanMainColorRenderer());

		table.getTableHeader().setReorderingAllowed(false);

		// CheckBoxSpaltenGröße setzen
		table.getColumnModel().getColumn(0).setMinWidth(20);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		model.fireTableRowsUpdated(0, model.getRowCount());
		TableButtonPanel.getInstance().setButtonsEnabled(false);
		setVisible(true);
	}

	/**
	 * Sorts the Table
	 * 
	 * @param list
	 */
	public void sortTable(ArrayList<Exhibit> list) {

		model = new TableModelMain(list);
		table.setModel(model);
		table.setBorder(null);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(String.class, new StringMainColorRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanMainColorRenderer());

		table.getTableHeader().setReorderingAllowed(false);

		// CheckBoxSpaltenGröße setzen
		table.getColumnModel().getColumn(0).setMinWidth(20);
		table.getColumnModel().getColumn(0).setMaxWidth(25);
		model.fireTableRowsUpdated(0, model.getRowCount());
		setVisible(true);
	}

	/**
	 * Table Update
	 * 
	 * @param MuseumID
	 * @param category
	 * @param section
	 * @param label
	 * @param outsourced
	 */
	public void updateTable(long MuseumID, long category, long section,
			long label, long outsourced) {

		ArrayList<Long> proCategory = new ArrayList<Long>();
		ArrayList<Long> proSection = new ArrayList<Long>();
		ArrayList<Long> proLabel = new ArrayList<Long>();
		ArrayList<Long> proOutsourced = new ArrayList<Long>();
		ArrayList<Long> contraCategory = new ArrayList<Long>();
		ArrayList<Long> contraSection = new ArrayList<Long>();
		ArrayList<Long> contraLabel = new ArrayList<Long>();
		ArrayList<Long> contraOutsourced = new ArrayList<Long>();

		if (!(category == 0)) {
			proCategory.add(category);
		}

		if (!(section == 0)) {
			proSection.add(section);
		}

		if (!(label == 0)) {
			proLabel.add(label);
		}

		if (!(outsourced == 0)) {
			proOutsourced.add(outsourced);
		}

		searchModel = new TableSearchModel(MuseumID, "", proCategory,
				contraCategory, proSection, contraSection, proLabel,
				contraLabel, proOutsourced, contraOutsourced, false, false);

		currentList = searchModel.getResultList();

		model = new TableModelMain(currentList);

		table.setModel(model);
		table.setBorder(null);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setDefaultRenderer(String.class, new StringMainColorRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanMainColorRenderer());

		table.getTableHeader().setReorderingAllowed(false);

		// CheckBoxSpaltenGröße setzen
		table.getColumnModel().getColumn(0).setMinWidth(20);
		table.getColumnModel().getColumn(0).setMaxWidth(25);

		model.fireTableRowsUpdated(0, model.getRowCount());
		TableButtonPanel.getInstance().setButtonsEnabled(false);
		setVisible(true);
	}

	/**
	 * Table Refresh
	 */
	public void refreshTable() throws NullPointerException {

		isSelected = false;

		if (searchModel == null) {
			throw new NullPointerException(
					"Tabelle (TablePanel) noch nicht initialisiert!");

		} else {
			updateTable(searchModel.getMuseumId(),
					searchModel.getExhibitName(), searchModel.getProCategory(),
					searchModel.getContraCategory(),
					searchModel.getProSection(),
					searchModel.getContraSection(), searchModel.getProLabel(),
					searchModel.getContraLabel(),
					searchModel.getProOutsourced(),
					searchModel.getContraOutsourced(),
					searchModel.isNormalSearch(), searchModel.isEmpty());
			currentList = searchModel.getResultList();

		}

		if (currentList.isEmpty()) {
			TableButtonPanel.getInstance().setButtonsEnabled(false);
		}
		// table.changeSelection(currentRow, 1, false, false);

	}

	/**
	 * Builds the right-click popup
	 * 
	 * @param id
	 */
	public void buildPopup(final long id) {

		final JMenuItem addExhibit = new JMenuItem("Exponat hinzufügen");
		final JMenuItem deleteExhibit = new JMenuItem("Löschen");
		final JMenuItem openExhibit = new JMenuItem("Öffnen");
		final JMenuItem editExhibit = new JMenuItem("Bearbeiten");
		final JMenuItem addToExhibition = new JMenuItem(
				"Zur Austellung hinzufügen");
		final JMenuItem loanExhibit = new JMenuItem("Ausleihen");
		final JMenuItem moveExhibit = new JMenuItem("Verschieben");

		popUp = new JPopupMenu();
		popUp.add(openExhibit);
		popUp.add(editExhibit);
		popUp.add(deleteExhibit);
		popUp.addSeparator();
		popUp.add(moveExhibit);
		popUp.add(loanExhibit);
		popUp.add(addToExhibition);
		popUp.addSeparator();
		popUp.add(addExhibit);
		
		openExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					EditExhibit dialog = new EditExhibit(Access
							.searchExhibitID(getSelectedRowId()));
					dialog.enableEditing(false);
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showConfirmDialog(TablePanel.this,
							"Fenster konnte nicht geöffnet werden.");
				}
			}
		});

		editExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = getCheckedIds();

					if (!isChecked()
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked() && idList.size() >= 2) {
						if (checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TablePanel.this,
											"Ihre Auswahl enthält gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							MassChange dialog = new MassChange(idList);
							dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							dialog.setVisible(true);
						}
					} else {
						EditExhibit dialog = new EditExhibit(Access
								.searchExhibitID(getSelectedRowId()));
						dialog.enableEditing(true);
					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = getCheckedIds();

					if (!isChecked()
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked() && idList.size() >= 2) {
						if (checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TablePanel.this,
											"Ihre Auswahl enthält bereits gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							int reply = JOptionPane.showConfirmDialog(null,
									"Wollen Sie diese " + idList.size()
											+ " Exponate löschen?",
									"Exponate löschen",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								deleteSelectedIds();
								refreshTable();
							} else {

							}
						}
					} else {
						int reply = JOptionPane.showConfirmDialog(null,
								"Wollen Sie dieses Exponat löschen?",
								"Exponat löschen", JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							try {
								Access.deleteExhibit(getSelectedRowId());
							} catch (ModelAlreadyDeletedException e1) {
								JOptionPane.showMessageDialog(TablePanel.this,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(TablePanel.this,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
							refreshTable();
						} else {

						}

					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}

		});

		addExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditExhibit dialog = new EditExhibit(Access
							.searchMuseumID(TreeMainPanel.getInstance()
									.getMuseumId()), null, null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (MuseumNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showConfirmDialog(TablePanel.this,
							"Fenster konnte nicht geöffnet werden.");
				}

			}
		});

		addToExhibition.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = getCheckedIds();

					if (!isChecked()
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht zu einer Ausstellung hinzugefügt werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht zu einer Ausstellung hinzugefügt werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked() && idList.size() >= 2) {
						if (checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TablePanel.this,
											"Ihre Auswahl enthält bereits gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							try {
								AddToExhibition dialog = new AddToExhibition(id);
								dialog.setVisible(true);
							} catch (Exception e1) {
								JOptionPane
										.showConfirmDialog(TablePanel.this,
												"Fenster konnte nicht geöffnet werden.");
							}
						}
					} else {
						try {
							AddToExhibition dialog = new AddToExhibition(id);
							dialog.setVisible(true);
						} catch (Exception e1) {
							JOptionPane.showConfirmDialog(TablePanel.this,
									"Fenster konnte nicht geöffnet werden.");
						}

					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		loanExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = getCheckedIds();

					if (!isChecked()
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht ausgeliehen werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht ausgeliehen werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked() && idList.size() >= 2) {
						if (checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TablePanel.this,
											"Ihre Auswahl enthält bereits gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							AddToLoan loanDialog = new AddToLoan();
							loanDialog.setVisible(true);
						}
					} else {
						AddToLoan loanDialog = new AddToLoan();
						loanDialog.setVisible(true);

					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		moveExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = getCheckedIds();

					if (!isChecked()
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getSelectedRowId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (isChecked() && idList.size() >= 2) {
						if (checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TablePanel.this,
											"Ihre Auswahl enthält gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							MassChange dialog = new MassChange(idList);
							dialog.setVisible(true);
						}
					} else {
						ArrayList<Long> tempList = new ArrayList<>();
						tempList.add(getSelectedRowId());
						MassChange dialog = new MassChange(tempList);
						dialog.setVisible(true);
					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

	}

	/**
	 * Checks all Checkboxes if they are Checked or not
	 * 
	 */
	public void checkAllCheckBoxes() {

		checkedList.clear();

		for (int i = 0; i < model.getRowCount(); i++)

		{
			Boolean checkBox = (Boolean) model.getValueAt(i, 0);
			if (checkBox) {
				checkedList.add(model.getId(i));
			}
		}

	}

	/**
	 * Returns a list with all IDs from the Checked Rows in the table.
	 * 
	 * @return iDs
	 */
	public ArrayList<Long> getCheckedIds() {
		checkAllCheckBoxes();
		return checkedList;
	}

	/**
	 * 
	 * @return iD (ID of current selected Row)
	 * @throws Exception
	 */
	public long getSelectedRowId() {

		long iD = (long) model.getId(currentRow);

		return iD;
	}

	/**
	 * 
	 * @return true if a row is Checked
	 * @return false if no row is Checked
	 */
	public Boolean isChecked() {
		checkAllCheckBoxes();
		if (checkedList.isEmpty()) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 
	 * @return true if a row is Selected
	 * @return false if no row is Selected
	 */
	public Boolean isSelected() {

		return isSelected;
	}

	/**
	 * 
	 * @return true if the table is empty
	 */
	public Boolean isEmpty() {
		checkAllCheckBoxes();
		if (currentList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Delete the selected Ids
	 */
	public void deleteSelectedIds() {

		if (isChecked()) {
			deleteIds.clear();
			deleteIds = getCheckedIds();
		} else {
			deleteIds.clear();
			deleteIds.add(getSelectedRowId());
		}

		if (!isChecked() && isSelected()) {

			try {
				Access.deleteExhibit(getSelectedRowId());
				MainGUI.getDetailPanel().refresh();
			} catch (ModelAlreadyDeletedException e) {
				// JOptionPane.showConfirmDialog(this, e.getMessage());
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionException e) {
				// JOptionPane.showConfirmDialog(this, e.getMessage());
				JOptionPane.showMessageDialog(null,
						"Verbindungsfehler zur Datenbank!", "Datenbankfehler",
						JOptionPane.ERROR_MESSAGE);
			}

		} else if (isChecked()) {
			try {
				Access.massExhibitDelete(deleteIds);
			} catch (ModelAlreadyDeletedException e) {
				// JOptionPane.showConfirmDialog(this, e.getMessage());
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			} catch (ConnectionException e) {
				// JOptionPane.showConfirmDialog(this, e.getMessage());
				JOptionPane.showMessageDialog(null,
						"Verbindungsfehler zur Datenbank!", "Datenbankfehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/**
	 * 
	 * @return the current table list
	 */
	public ArrayList<Exhibit> getCurrentList() {
		return currentList;
	}

	public Boolean checkSelectedIfDeleted() {
		ArrayList<Long> idlist = new ArrayList<>();
		idlist.clear();
		idlist = getCheckedIds();
		boolean result = false;

		for (Long actual : idlist) {
			boolean isDeleted = false;
			try {
				isDeleted = Access.searchExhibitID(actual).isDeleted();
			} catch (ExhibitNotFoundException e) {
				JOptionPane.showMessageDialog(TablePanel.this, e.getMessage());
			}

			if (isDeleted) {
				result = true;
			}
		}

		return result;
	}

}
