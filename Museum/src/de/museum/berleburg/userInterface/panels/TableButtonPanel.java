package de.museum.berleburg.userInterface.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.dialogs.AddToExhibition;
import de.museum.berleburg.userInterface.dialogs.AddToLoan;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;
import de.museum.berleburg.userInterface.dialogs.MassChange;

public class TableButtonPanel extends JPanel {

	private static final long serialVersionUID = -6336107100245282669L;
	private TablePanel table;
	private static TableButtonPanel instance = null;

	private JButton btnOpenExhibit;
	private JButton btnEditExhibit;
	private JButton btnDeleteExhibit;
	private JButton btnAddToExhibition;
	private JButton btnLoan;
	private JButton btnRetoure;

	/**
	 * Create the panel.
	 * 
	 * @author Timo Funke, Frank Hülsmann
	 * @param northpanelf
	 */
	public TableButtonPanel() {
		instance = this;
		this.table = TablePanel.getInstance();

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		btnOpenExhibit = new JButton("Detailansicht");
		btnOpenExhibit.setPreferredSize(new Dimension(330, 25));
		btnOpenExhibit.setMinimumSize(new Dimension(120, 25));
		btnOpenExhibit.setToolTipText("Detailansicht öffnen");
		btnOpenExhibit.setEnabled(false);
		add(btnOpenExhibit);

		btnEditExhibit = new JButton("Exponat bearbeiten");
		btnEditExhibit.setPreferredSize(new Dimension(330, 25));
		btnEditExhibit.setMinimumSize(new Dimension(120, 25));
		btnEditExhibit.setToolTipText("Markiertes Exponat bearbeiten");
		btnEditExhibit.setEnabled(false);
		add(btnEditExhibit);

		btnDeleteExhibit = new JButton("Exponat löschen");
		btnDeleteExhibit.setPreferredSize(new Dimension(330, 25));
		btnDeleteExhibit.setMinimumSize(new Dimension(120, 25));
		btnDeleteExhibit.setToolTipText("markiertes Exponat endgültig löschen");
		btnDeleteExhibit.setEnabled(false);
		add(btnDeleteExhibit);

		btnAddToExhibition = new JButton("Ausstellen");
		btnAddToExhibition.setPreferredSize(new Dimension(330, 25));
		btnAddToExhibition.setMinimumSize(new Dimension(120, 25));
		btnAddToExhibition.setToolTipText("Zu einer Ausstellung hinzufügen");
		btnAddToExhibition.setEnabled(false);
		add(btnAddToExhibition);

		btnLoan = new JButton("Verleihen");
		btnLoan.setPreferredSize(new Dimension(330, 25));
		btnLoan.setToolTipText("Exponat verleihen");
		btnLoan.setMinimumSize(new Dimension(120, 25));
		btnLoan.setEnabled(false);
		add(btnLoan);

		btnRetoure = new JButton("Retoure");
		btnRetoure.setPreferredSize(new Dimension(330, 25));
		btnRetoure.setToolTipText("Exponat von Ausstellung/Leihgabe zurück");
		btnRetoure.setMinimumSize(new Dimension(120, 25));
		btnRetoure.setEnabled(false);
		btnRetoure.setVisible(false);
		add(btnRetoure);

		btnAddToExhibition.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = table.getCheckedIds();

					if (!table.isChecked()
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht zu einer Ausstellung hinzugefügt werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht zu einer Ausstellung hinzugefügt werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked() && idList.size() >= 2) {
						if (table.checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TableButtonPanel.this,
											"Ihre Auswahl enthält bereits gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							try {
								AddToExhibition dialog = new AddToExhibition(
										getExhibitId());
								dialog.setVisible(true);
							} catch (Exception e1) {
								JOptionPane
										.showConfirmDialog(
												TableButtonPanel.this,
												"Fenster konnte nicht geöffnet werden.");
							}
						}
					} else {
						try {
							AddToExhibition dialog = new AddToExhibition(
									getExhibitId());
							dialog.setVisible(true);
						} catch (Exception e1) {
							JOptionPane.showConfirmDialog(
									TableButtonPanel.this,
									"Fenster konnte nicht geöffnet werden.");
						}

					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		btnOpenExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					EditExhibit dialog = new EditExhibit(Access
							.searchExhibitID(table.getSelectedRowId()));
					dialog.enableEditing(false);
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		btnEditExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = table.getCheckedIds();

					if (!table.isChecked()
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked() && idList.size() >= 2) {
						if (table.checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TableButtonPanel.this,
											"Ihre Auswahl enthält gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							MassChange dialog = new MassChange(idList);
							dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							dialog.setVisible(true);
						}
					} else {
						EditExhibit dialog = new EditExhibit(Access
								.searchExhibitID(getExhibitId()));
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		btnDeleteExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = table.getCheckedIds();

					if (!table.isChecked()
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht bearbeitet werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked() && idList.size() >= 2) {
						if (table.checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TableButtonPanel.this,
											"Ihre Auswahl enthält bereits gelöschte Exponate. Bitte überprüfen Sie Ihre Auswahl!",
											"Fehler", JOptionPane.ERROR_MESSAGE);
						} else {
							int reply = JOptionPane.showConfirmDialog(null,
									"Wollen Sie diese " + idList.size()
											+ " Exponate löschen?",
									"Exponate löschen",
									JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								table.deleteSelectedIds();
								table.refreshTable();
							} else {

							}
						}
					} else {
						int reply = JOptionPane.showConfirmDialog(null,
								"Wollen Sie dieses Exponat löschen?",
								"Exponat löschen", JOptionPane.YES_NO_OPTION);
						if (reply == JOptionPane.YES_OPTION) {
							try {
								Access.deleteExhibit(getExhibitId());
							} catch (ModelAlreadyDeletedException e1) {
								JOptionPane.showMessageDialog(
										TableButtonPanel.this, e1.getMessage(),
										"Fehler", JOptionPane.ERROR_MESSAGE);
							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(
										TableButtonPanel.this, e1.getMessage(),
										"Fehler", JOptionPane.ERROR_MESSAGE);
							}
							table.refreshTable();
						} else {

						}

					}
				} catch (ExhibitNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		btnLoan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					ArrayList<Long> idList = new ArrayList<>();
					idList = table.getCheckedIds();

					if (!table.isChecked()
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht ausgeliehen werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked()
							&& idList.size() < 2
							&& Access.searchExhibitID(getExhibitId())
									.isDeleted()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Das Exponat wurde bereits gelöscht und kann nicht ausgeliehen werden",
										"Fehler", JOptionPane.ERROR_MESSAGE);
					} else if (table.isChecked() && idList.size() >= 2) {
						if (table.checkSelectedIfDeleted()) {
							JOptionPane
									.showMessageDialog(
											TableButtonPanel.this,
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

		btnRetoure.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// Wenn Zeile selektiert ist && NICHT gecheckt
				if (table.isSelected() && !table.isChecked()) {
					Long id = table.getSelectedRowId();
					String name = null;
					try {
						name = Access.searchExhibitID(id).getName();
					} catch (ExhibitNotFoundException e2) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e2.getMessage(),
								"Fehler", JOptionPane.ERROR_MESSAGE);
					}

					if (JOptionPane.showConfirmDialog(null,
							"Wollen sie das Exponat " + name + " zurückgeben?",
							"Retoure", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
							Access.removeFromOutsourced(id);
							TablePanel.getInstance().refreshTable();
							InformationPanel.getInstance().setText(
									"Exponat wurde zurückgegeben");
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {

							InformationPanel
									.getInstance()
									.setText(
											"Exponat konnte nicht zurückgegeben werden");
						}

					}
				}
				// Wenn genau EINE Zeile gecheckt ist!
				else if (table.getCheckedIds().size() == 1 && table.isChecked()) {
					ArrayList<Long> ids = table.getCheckedIds();
					Long id = null;

					for (Long i : ids) {
						id = i;
					}

					String name = null;
					try {
						name = Access.searchExhibitID(id).getName();
					} catch (ExhibitNotFoundException e2) {
						// TODO Auto-generated catch block
						JOptionPane.showMessageDialog(null, e2.getMessage(),
								"Fehler", JOptionPane.ERROR_MESSAGE);
					}

					if (JOptionPane.showConfirmDialog(null,
							"Wollen sie das Exponat " + name + " zurückgeben?",
							"Retoure", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
							Access.removeFromOutsourced(id);
							TablePanel.getInstance().refreshTable();
							InformationPanel.getInstance().setText(
									"Exponat wurde zurückgegeben");

						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {

							InformationPanel
									.getInstance()
									.setText(
											"Exponat konnte nicht zurückgegeben werden");
						}

					}
				}
				// Wenn mehr als eine zeile gecheckt ist.
				else if (table.getCheckedIds().size() >= 2) {
					ArrayList<Long> ids = table.getCheckedIds();
					int count = ids.size();

					if (JOptionPane.showConfirmDialog(null, "Wollen sie diese "
							+ count + " Exponate zurückgeben?", "Retoure",
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
							Access.massRemoveFromOutosurced(ids);
							TablePanel.getInstance().refreshTable();
							TreeMainPanel.getInstance().refreshTree();
							InformationPanel.getInstance().setText(
									"Exponat wurde zurückgegeben");
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {

							InformationPanel
									.getInstance()
									.setText(
											"Exponate konnten nicht zurückgegeben werden");
						}

					}

				}
			}

		});

	}

	/**
	 * 
	 * @param enabled
	 *            set the buttons enabled
	 */
	public void setButtonsEnabled(Boolean enabled) {

		if (btnRetoure.isVisible()) {
			btnRetoure.setEnabled(enabled);
		}

		btnOpenExhibit.setEnabled(enabled);
		btnEditExhibit.setEnabled(enabled);
		btnDeleteExhibit.setEnabled(enabled);
		btnAddToExhibition.setEnabled(enabled);
		btnLoan.setEnabled(enabled);

	}

	/**
	 * 
	 * @param isVisible
	 *            sets the Rotoure Visible
	 */
	public void setRetoureVisible(Boolean isVisible) {
		btnRetoure.setVisible(isVisible);
	}

	/**
	 * 
	 * @return instance
	 */
	public static TableButtonPanel getInstance() {
		if (instance == null) {
			instance = new TableButtonPanel();
		}
		return instance;
	}

	/**
	 * 
	 * @return the selected rowID
	 */
	public long getExhibitId() {
		return table.getSelectedRowId();
	}

}
