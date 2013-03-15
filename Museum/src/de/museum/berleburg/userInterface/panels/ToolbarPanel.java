package de.museum.berleburg.userInterface.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.dialogs.CreateCategory;
import de.museum.berleburg.userInterface.dialogs.CreateSection;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;

/**
 * A JPanel Panels for most used functions
 * 
 * 
 * @author Way Dat To
 * 
 */

@SuppressWarnings("serial")
public class ToolbarPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public ToolbarPanel() {
		setMaximumSize(new Dimension(320, 40));
		setMinimumSize(new Dimension(160, 40));
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JToolBar toolBar = new JToolBar();
		toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		toolBar.setBorderPainted(false);
		toolBar.setPreferredSize(new Dimension(200, 40));
		toolBar.setMinimumSize(new Dimension(200, 40));
		toolBar.setMaximumSize(new Dimension(200, 40));
		toolBar.setFloatable(false);
		add(toolBar);

		JButton newExp = new JButton(new ImageIcon(getClass().getResource(
				"Add.png")));
		newExp.setToolTipText("Erzeugt ein neues Exponat");
		newExp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EditExhibit dialog = new EditExhibit();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		newExp.setBorder(null);
		newExp.setBorderPainted(false);
		newExp.setMinimumSize(new Dimension(40, 40));
		newExp.setMaximumSize(new Dimension(40, 40));
		toolBar.add(newExp);

		JButton newKat = new JButton(new ImageIcon(getClass().getResource(
				"label.png")));
		newKat.setToolTipText("Erzeugt eine neue Kategorie");
		newKat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateCategory dialog = new CreateCategory();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		newKat.setBorder(null);
		newKat.setMinimumSize(new Dimension(40, 40));
		newKat.setMaximumSize(new Dimension(40, 40));
		toolBar.add(newKat);

		JButton newSek = new JButton(new ImageIcon(getClass().getResource(
				"ContainerRed.png")));
		newSek.setToolTipText("Erzeugt eine neue Sektion");
		newSek.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateSection dialog = new CreateSection();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});
		newSek.setBorder(null);
		newSek.setMaximumSize(new Dimension(40, 40));
		newSek.setMinimumSize(new Dimension(40, 40));
		toolBar.add(newSek);

		JButton btnDel = new JButton(new ImageIcon(getClass().getResource(
				"remove2.png")));

		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (TreeMainPanel.getInstance() == null) {

				} else {
					int in;
					Long id;
					in = TreeMainPanel.getInstance().getTabbedPane()
							.getSelectedIndex();
					if (in == 0) {
						try {
							id = TreeMainPanel.getInstance()
									.getTreeSectionPanel().getSectionId();
							if (id != null) {
								int eingabe = JOptionPane
										.showConfirmDialog(
												null,
												"Sind Sie sicher, dass Sie die ausgewählte Sektion mit allen Untersektionen löschen möchten?\nHinweis: Alle zugehörigen Exponate werden \""
														+ TreeMainPanel
																.getInstance()
																.getTreeSectionPanel()
																.getSelection()
																.getParentPath()
																.getLastPathComponent()
																.toString()
														+ "\" hinzugefügt.",
												"Sektion löschen",
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
								if (eingabe == 0) {
									Access.deleteSection(id);
									TreeMainPanel.getInstance().refreshTree();
								}
							} else {
								JOptionPane
										.showMessageDialog(
												null,
												"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Sektion gewählt haben.");
							}
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {
							JOptionPane
									.showMessageDialog(
											null,
											"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Sektion gewählt haben.");
//							InformationPanel.getInstance().setText(
//									e1.getMessage());
						}

					} else if (in == 1) {
						try {
							if (TreeMainPanel.getInstance()
									.getTreeCategoryPanel().getCategoryId() != null) {
								id = TreeMainPanel.getInstance()
										.getTreeCategoryPanel().getCategoryId();
								int eingabe = JOptionPane
										.showConfirmDialog(
												null,
												"Sind Sie sicher, dass Sie die ausgewählte Kategorie mit allen Unterkategorien löschen möchten?\nHinweis: Alle zugehörigen Exponate werden der \"Sonstiges\"-Kategorie hinzugefügt.",
												"Kategorie löschen",
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
								if (eingabe == 0) {
									Access.deleteCategory(id);
									TreeMainPanel.getInstance().refreshTree();
								}
							}

						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e0) {
							JOptionPane.showMessageDialog(null,
									e0.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						}

						catch (Exception e1) {
							JOptionPane
									.showMessageDialog(
											null,
											"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Kategorie gewählt haben.");
//							InformationPanel.getInstance().setText(
//									e1.getMessage());
						}
					} else if (in == 2) {
						boolean loan = true;

						try {
							if (TreeMainPanel.getInstance()
									.getTreeExhibitionPanel().getExhibitionId() == null)
								id = 0L;
							else
								id = TreeMainPanel.getInstance()
										.getTreeExhibitionPanel()
										.getExhibitionId();
							int eingabe = 1;
							if (id != 0) {
								eingabe = JOptionPane
										.showConfirmDialog(
												null,
												"Sind Sie sicher, dass Sie die ausgewählte Ausstellung löschen möchten?",
												"Ausstellung löschen",
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
								loan = false;
							}
							if (eingabe == 0) {
								Access.deleteExhibition(id);
								TreeMainPanel.getInstance().refreshTree();
							}
						} catch (ModelAlreadyDeletedException e1) {
							// Empty is correct
						} catch (ConnectionException e1) {
							// Empty is correct
						} catch (IntegrityException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
//							InformationPanel.getInstance().setText(
//									e1.getMessage());
						} catch (Exception e1) {
							JOptionPane
									.showMessageDialog(
											null,
											"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Ausstellung oder eine Leihgabe gewählt haben.");
							loan = false;
//							InformationPanel.getInstance().setText(
//									e1.getMessage());
						}

						if (loan) {
							try {
								id = TreeMainPanel.getInstance()
										.getTreeExhibitionPanel().getLoanId();
								int eingabe = JOptionPane
										.showConfirmDialog(
												null,
												"Sind Sie sicher, dass Sie die ausgewählte Leihgabe löschen möchten?",
												"Leihgabe löschen",
												JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE);
								if (eingabe == 0) {
									Access.deleteExhibition(id);
									TreeMainPanel.getInstance().refreshTree();
								}
							} catch (ModelAlreadyDeletedException e1) {
								JOptionPane
										.showMessageDialog(
												null,
												"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Ausstellung oder eine Leihgabe gewählt haben.");
//								InformationPanel.getInstance().setText(
//										e1.getMessage());
							} catch (ConnectionException e1) {
								// JOptionPane.showMessageDialog(null,"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Ausstellung oder eine Leihgabe gewählt haben.");
								JOptionPane.showMessageDialog(null,
										"Verbindungsfehler zur Datenbank!",
										"Datenbankfehler",
										JOptionPane.ERROR_MESSAGE);
//								InformationPanel.getInstance().setText(
//										e1.getMessage());
							} catch (IntegrityException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
//								InformationPanel.getInstance().setText(
//										e1.getMessage());
							} catch (Exception e0) {
								JOptionPane
										.showMessageDialog(
												null,
												"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Ausstellung oder eine Leihgabe gewählt haben.");
//								InformationPanel.getInstance().setText(
//										e0.getMessage());

							}
						}
						// Caro: Das ist doch eigenlich nach obiger Aussage
						// "Eintrag konnte nicht entfernt...." überflüssig
						/*
						 * if(!deleted){ JOptionPane.showMessageDialog(null,
						 * "Es wurde keine Ausstellung/Leihgabe gewählt!");
						 * InformationPanel.getInstance().setText(
						 * "Es wurde keine Ausstellung/Leihgabe gewählt!"); }
						 */
						// TreeMainPanel.getInstance().refreshTree();
					}

					else if (in == 3) {
						try {
							id = TreeMainPanel.getInstance()
									.getTreeLabelPanel().getLabelId();
							int eingabe = JOptionPane
									.showConfirmDialog(
											null,
											"Sind Sie sicher, dass Sie das ausgewählte Label löschen möchten?",
											"Label löschen",
											JOptionPane.YES_NO_OPTION,
											JOptionPane.QUESTION_MESSAGE);
							if (eingabe == 0) {
								Access.deleteLabel(id);
								TreeMainPanel.getInstance().refreshTree();
							}
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									"Verbindungsfehler zur Datenbank!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {
							JOptionPane
									.showMessageDialog(
											null,
											"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie ein Label gewählt haben.");
//							InformationPanel.getInstance().setText(
//									e1.getMessage());
						}

					}
				}
			}
		}

		);

		btnDel.setToolTipText("Löscht ein ausgewähltes Element");
		btnDel.setMinimumSize(new Dimension(40, 40));
		btnDel.setMaximumSize(new Dimension(40, 40));
		btnDel.setBorderPainted(false);
		btnDel.setBorder(null);
		toolBar.add(btnDel);

		// JButton ShowExp = new JButton(new
		// ImageIcon(getClass().getResource("Search.png") ));
		// ShowExp.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// EditExhibition editLoan = new EditExhibition(2L);
		// editLoan.setVisible(true);
		// }
		// });
		// ShowExp.setToolTipText("Zeigt alle Exponate einer abgelaufener Ausstellung an");
		// ShowExp.setMinimumSize(new Dimension(40, 40));
		// ShowExp.setMaximumSize(new Dimension(40, 40));
		// ShowExp.setBorder(null);
		// toolBar.add(ShowExp);

	}

}
