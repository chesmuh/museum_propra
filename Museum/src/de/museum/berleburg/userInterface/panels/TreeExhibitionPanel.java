package de.museum.berleburg.userInterface.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ExceptionHandler;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.CreateContact;
import de.museum.berleburg.userInterface.dialogs.CreateExhibition;
import de.museum.berleburg.userInterface.dialogs.CreateLoan;
import de.museum.berleburg.userInterface.dialogs.EditExhibition;
import de.museum.berleburg.userInterface.dialogs.EditLoan;
import de.museum.berleburg.userInterface.dialogs.EditMuseum;
import de.museum.berleburg.userInterface.dialogs.MuseumChoose;

/**
 * Create the ExhibitionPanel.
 * 
 * @author Maximilian Beck
 * 
 */
public class TreeExhibitionPanel extends JPanel {

	private static final long serialVersionUID = -6082817903074215734L;

	private JPopupMenu popUp;
	private JTree tree;
	private static Long lastUsedMuseum = 0L;
	private static TreeExhibitionPanel instance = null;
	public TreePath path;

	// private boolean rightclickable = true;

	/**
	 * @param rightclickable
	 */

	public TreeExhibitionPanel(boolean rightclickable) {

		instance = this;
		// this.rightclickable = rightclickable;

		setMaximumSize(new Dimension(590, 32767));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);

		/** Create the tree */

		DefaultMutableTreeNode nodeMuseum;
		try {
			if (MuseumMainPanel.getInstance().getMuseumId() == null) {
				nodeMuseum = new DefaultMutableTreeNode(MuseumMainPanel
						.getInstance().getMuseumTreeNode());
			} else
				nodeMuseum = createTree(MuseumMainPanel.getInstance()
						.getMuseumTreeNode());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
			nodeMuseum = new DefaultMutableTreeNode();
		}

		this.tree = new JTree(nodeMuseum);

		tree.addTreeExpansionListener(new TreeExpansionListener() {

			@Override
			public void treeCollapsed(TreeExpansionEvent arg0) {
				tree.expandRow(0);
			}

			@Override
			public void treeExpanded(TreeExpansionEvent arg0) {
				// do nothing
			}

		});

		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) this.tree
				.getCellRenderer();
		renderer.setLeafIcon(renderer.getClosedIcon());

		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setAlignmentY(Component.TOP_ALIGNMENT);
		tree.setAlignmentX(Component.LEFT_ALIGNMENT);
		tree.setAutoscrolls(true);

		scrollPane.setViewportView(tree);

		if (rightclickable == true) {

			/**
			 * MouseListener for Popup Menue
			 * */

			tree.addMouseListener(new MouseAdapter() {

				public void mousePressed(MouseEvent e) { // Pressed for Linux
					rightClick(e);
				}

				public void mouseReleased(MouseEvent e) { // Released for
															// Windows
					rightClick(e);
				}

				public void rightClick(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					int selRow = tree.getRowForLocation(x, y);
					TreePath path = tree.getPathForLocation(x, y);

					/**
					 * Listener on right mouseclick
					 */

					if (e.isPopupTrigger()) {
						if (selRow == -1) {

						} else if (path.getLastPathComponent() == path
								.getPathComponent(0)) {
							tree.setSelectionPath(path);
							buildPopupRoot();
							popUp.show(e.getComponent(), x, y);

						} else if (path.getLastPathComponent().toString()
								.equals("Ausstellungen")
								|| path.getParentPath().getLastPathComponent()
										.toString().equals("Ausstellungen")) {
							tree.setSelectionPath(path);
							buildPopupExh();
							popUp.show(e.getComponent(), x, y);
						} else if (path.getLastPathComponent().toString()
								.equals("Leihgaben")
								|| path.getParentPath().getLastPathComponent()
										.toString().equals("Leihgaben")) {
							tree.setSelectionPath(path);
							buildPopupLo();
							popUp.show(e.getComponent(), x, y);
						} else if (path.getParentPath().getParentPath()
								.getLastPathComponent().toString()
								.equals("Ausstellungen")) {
							tree.setSelectionPath(path);
							buildPopupExhSub();
							popUp.show(e.getComponent(), x, y);
						} else if (path.getParentPath().getParentPath()
								.getLastPathComponent().toString()
								.equals("Leihgaben")) {
							tree.setSelectionPath(path);
							buildPopupLoaSub();
							popUp.show(e.getComponent(), x, y);
						}
					}

					/**
					 * Listener on left mouseclick
					 */

					if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

						TableButtonPanel.getInstance().setRetoureVisible(true);

						if (selRow == -1) {

						} else if (path.getLastPathComponent().toString()
								.equals("Ausstellungen")) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long musemID = getMuseumId();
								if (musemID != null) {
									ArrayList<Outsourced> allExhibitions = Access
											.getAllExhibitionsByMuseum(musemID);
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allExhibitions) {
										outId.add(out.getId());
									}
									if (outId.isEmpty()) {
										table.updateTable(musemID, "", empty,
												empty, empty, empty, empty,
												empty, outId, empty, false,
												true);
									} else {
										table.updateTable(musemID, empty,
												empty, empty, empty, empty,
												empty, outId, empty);
									}
								}
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if (path.getLastPathComponent().toString()
								.equals("Leihgaben")) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									ArrayList<Outsourced> allOut = Access
											.getAllLoansByMuseum(Id);
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allOut) {
										outId.add(out.getId());
									}

									if (outId.isEmpty()) {
										table.updateTable(Id, "", empty, empty,
												empty, empty, empty, empty,
												outId, empty, false, true);
									} else {
										table.updateTable(Id, empty, empty,
												empty, empty, empty, empty,
												outId, empty);
									}
								}
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if (path.getLastPathComponent() == path
								.getPathComponent(0)) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									Museum museum = Access.searchMuseumID(Id);
									MainGUI.getDetailPanel().setDetails(museum);
									ArrayList<Outsourced> allOut = Access
											.getAllOutsourced(Id);
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allOut) {
										outId.add(out.getId());
									}
									if (outId.isEmpty()) {
										table.updateTable(Id, "", empty, empty,
												empty, empty, empty, empty,
												outId, empty, false, true);
									} else {
										table.updateTable(Id, empty, empty,
												empty, empty, empty, empty,
												outId, empty);
									}
								}
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if (path.getParentPath().getParentPath().getLastPathComponent()
								.toString().equals("Ausstellungen")) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long id = getMuseumId();
								Long outId = getExhibitionId();
								Outsourced out = Access
										.searchExhibitonID(outId);
								MainGUI.getDetailPanel().setDetails(out);
								table.updateTable(id, 0, 0, 0, outId);
							} catch (OutsourcedNotFoundException
									| MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if (path.getParentPath().getParentPath().getLastPathComponent()
								.toString().equals("Leihgaben")) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long id = getMuseumId();
								Long outId = getLoanId();
								Outsourced out = Access.searchLoanID(outId);
								MainGUI.getDetailPanel().setDetails(out);
								table.updateTable(id, 0, 0, 0, outId);
							} catch (OutsourcedNotFoundException
									| MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}

						} else if ( path.getLastPathComponent().toString().equals("Aktuell")) {
							
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									ArrayList<Outsourced> allOut = new ArrayList<Outsourced>();
									if (path.getParentPath().getLastPathComponent()
								.toString().equals("Leihgaben")){
									allOut = Access
											.getAllLoansByMuseum(Id);
									} else if (path.getParentPath().getLastPathComponent()
											.toString().equals("Ausstellungen")){
										allOut = Access
												.getAllExhibitionsByMuseum(Id);
										}
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allOut) {
										if(!Access.isExpired(out.getId())){
										outId.add(out.getId());
										}
									}

									if (outId.isEmpty()) {
										table.updateTable(Id, "", empty, empty,
												empty, empty, empty, empty,
												outId, empty, false, true);
									} else {
										table.updateTable(Id, empty, empty,
												empty, empty, empty, empty,
												outId, empty);
									}
								}
							} catch (MuseumNotFoundException | OutsourcedNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if ( path.getLastPathComponent().toString().equals("Abgelaufen")) {
							
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									ArrayList<Outsourced> allOut = new ArrayList<Outsourced>();
									if (path.getParentPath().getLastPathComponent()
								.toString().equals("Leihgaben")){
									allOut = Access
											.getAllLoansByMuseum(Id);
									} else if (path.getParentPath().getLastPathComponent()
											.toString().equals("Ausstellungen")){
										allOut = Access
												.getAllExhibitionsByMuseum(Id);
										}
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allOut) {
										if(Access.isExpired(out.getId()) && !out.allBack()){
										outId.add(out.getId());
										}
									}

									if (outId.isEmpty()) {
										table.updateTable(Id, "", empty, empty,
												empty, empty, empty, empty,
												outId, empty, false, true);
									} else {
										table.updateTable(Id, empty, empty,
												empty, empty, empty, empty,
												outId, empty);
									}
								}
							} catch (MuseumNotFoundException | OutsourcedNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else if ( path.getLastPathComponent().toString().equals("Beendet")) {
							
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									ArrayList<Outsourced> allOut = new ArrayList<Outsourced>();
									if (path.getParentPath().getLastPathComponent()
								.toString().equals("Leihgaben")){
									allOut = Access
											.getAllLoansByMuseum(Id);
									} else if (path.getParentPath().getLastPathComponent()
											.toString().equals("Ausstellungen")){
										allOut = Access
												.getAllExhibitionsByMuseum(Id);
										}
									ArrayList<Long> outId = new ArrayList<Long>();
									ArrayList<Long> empty = new ArrayList<Long>();
									for (Outsourced out : allOut) {
										if(Access.isExpired(out.getId()) && out.allBack()){
										outId.add(out.getId());
										}
									}

									if (outId.isEmpty()) {
										table.updateTable(Id, "", empty, empty,
												empty, empty, empty, empty,
												outId, empty, false, true);
									} else {
										table.updateTable(Id, empty, empty,
												empty, empty, empty, empty,
												outId, empty);
									}
								}
							} catch (MuseumNotFoundException | OutsourcedNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			});

		}

	}

	/**
	 * 
	 * @param local
	 * @return TreeNodeObject rootOfTree
	 * @throws Exception
	 */

	private DefaultMutableTreeNode createTree(TreeNodeObject local)
			throws Exception {
		DefaultMutableTreeNode nodeMuseum = new DefaultMutableTreeNode(local);

		DefaultMutableTreeNode nodeExhibits = new DefaultMutableTreeNode(
				"Ausstellungen");
		DefaultMutableTreeNode nodeLoans = new DefaultMutableTreeNode(
				"Leihgaben");

		DefaultMutableTreeNode nodeExhibitsExpired = new DefaultMutableTreeNode(
				"Abgelaufen");
		DefaultMutableTreeNode nodeExhibitsCurrent = new DefaultMutableTreeNode(
				"Aktuell");
		DefaultMutableTreeNode nodeExhibitsFinished = new DefaultMutableTreeNode(
				"Beendet");

		nodeExhibits.add(nodeExhibitsCurrent);
		nodeExhibits.add(nodeExhibitsExpired);
		nodeExhibits.add(nodeExhibitsFinished);

		DefaultMutableTreeNode nodeLoansExpired = new DefaultMutableTreeNode(
				"Abgelaufen");
		DefaultMutableTreeNode nodeLoansCurrent = new DefaultMutableTreeNode(
				"Aktuell");
		DefaultMutableTreeNode nodeLoansFinished = new DefaultMutableTreeNode(
				"Beendet");

		nodeLoans.add(nodeLoansCurrent);
		nodeLoans.add(nodeLoansExpired);
		nodeLoans.add(nodeLoansFinished);

		Collection<Outsourced> Exhibitions = Access
				.getAllExhibitionsByMuseum(MuseumMainPanel.getInstance()
						.getMuseumId());
		Collection<Outsourced> Loans = Access
				.getAllLoansByMuseum(MuseumMainPanel.getInstance()
						.getMuseumId());

		for (Outsourced e : Exhibitions) {
			TreeNodeObject localEx = new TreeNodeObject(e.getName());
			localEx.setExhibitionId(e.getId());
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					localEx);
			
			if(!Access.isExpired(e.getId())) {
				nodeExhibitsCurrent.add(node);
			} else if(Access.isExpired(e.getId()) && !e.allBack()) {
				nodeExhibitsExpired.add(node);
			} else if(Access.isExpired(e.getId()) && e.allBack()) {
				nodeExhibitsFinished.add(node);
			}
		}

		for (Outsourced l : Loans) {
			TreeNodeObject localEx = new TreeNodeObject(l.getName());
			localEx.setLoanId(l.getId());
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(
					localEx);
			
			if(!Access.isExpired(l.getId())) {
				nodeLoansCurrent.add(node);
			} else if(Access.isExpired(l.getId()) && !l.allBack()) {
				nodeLoansExpired.add(node);
			} else if(Access.isExpired(l.getId()) && l.allBack()) {
				nodeLoansFinished.add(node);
			}
		}
		nodeMuseum.add(nodeExhibits);
		nodeMuseum.add(nodeLoans);

		return nodeMuseum;
	}

	/**
	 * Refreshes the Tree
	 */

	public void refreshTree() {
		int selectedRow = tree.getMinSelectionRow();
		Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(tree
				.getPathForRow(0));

		ArrayList<Integer> expandedRows = new ArrayList<Integer>();
		if (expandedPaths != null) {
			while (expandedPaths.hasMoreElements()) {
				expandedRows
						.add(tree.getRowForPath(expandedPaths.nextElement()));
			}
		}

		Collections.sort(expandedRows);

		DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
		TreeNodeObject museum = MuseumMainPanel.getInstance()
				.getMuseumTreeNode();
		DefaultMutableTreeNode nodeMuseum;
		try {
			if (MuseumMainPanel.getInstance().getMuseumId() == null) {
				nodeMuseum = new DefaultMutableTreeNode(museum);

			} else {
				nodeMuseum = createTree(museum);
				model.setRoot(nodeMuseum);
				model.nodeStructureChanged(nodeMuseum);

				TablePanel.getInstance().refreshTable();
			}
		} catch (Exception e) {
			String msg = ExceptionHandler.generateUserMessages(e);
			JOptionPane.showMessageDialog(this, msg, "Fehler",
					JOptionPane.ERROR_MESSAGE);
			nodeMuseum = new DefaultMutableTreeNode(museum);
		}

		if (lastUsedMuseum != null
				&& lastUsedMuseum.equals(MuseumMainPanel.getInstance()
						.getMuseumId())) {
			for (Integer i : expandedRows) {
				tree.expandRow(i);
			}
			tree.setSelectionRow(selectedRow);
		}
		lastUsedMuseum = MuseumMainPanel.getInstance().getMuseumId();

	}

	public void refreshTreeWithoutTable() {
		int selectedRow = tree.getMinSelectionRow();
		Enumeration<TreePath> expandedPaths = tree.getExpandedDescendants(tree
				.getPathForRow(0));

		ArrayList<Integer> expandedRows = new ArrayList<Integer>();

		if (expandedPaths != null) {
			while (expandedPaths.hasMoreElements()) {
				expandedRows
						.add(tree.getRowForPath(expandedPaths.nextElement()));
			}
		}

		Collections.sort(expandedRows);

		DefaultTreeModel model = (DefaultTreeModel) this.tree.getModel();
		TreeNodeObject museum = MuseumMainPanel.getInstance()
				.getMuseumTreeNode();
		DefaultMutableTreeNode nodeMuseum;
		try {
			if (MuseumMainPanel.getInstance().getMuseumId() == null) {
				nodeMuseum = new DefaultMutableTreeNode(museum);

			} else {
				nodeMuseum = createTree(museum);
				model.setRoot(nodeMuseum);
				model.nodeStructureChanged(nodeMuseum);

			}
		} catch (Exception e) {
			String msg = ExceptionHandler.generateUserMessages(e);
			JOptionPane.showMessageDialog(this, msg, "Fehler",
					JOptionPane.ERROR_MESSAGE);
			nodeMuseum = new DefaultMutableTreeNode(museum);
		}

		if (lastUsedMuseum != null
				&& lastUsedMuseum.equals(MuseumMainPanel.getInstance()
						.getMuseumId())) {
			for (Integer i : expandedRows) {
				tree.expandRow(i);
			}
			tree.setSelectionRow(selectedRow);
		}
		lastUsedMuseum = MuseumMainPanel.getInstance().getMuseumId();

	}

	/** PopUp on right click in blank area */

	// public void buildPopupBlankArea() {
	//
	// final JMenuItem newMus = new JMenuItem("Neues Museum");
	//
	//
	// popUp = new JPopupMenu();
	// popUp.add(newMus);
	//
	//
	// newMus.addActionListener(new ActionListener() {
	//
	// public void actionPerformed(ActionEvent e) {
	//
	// try {
	// CreateMuseum dialog = new CreateMuseum();
	// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	// dialog.setVisible(true);
	// } catch (Exception e1) {
	// JOptionPane.showMessageDialog(null,
	// e1.getMessage(), "Fehler",
	// JOptionPane.ERROR_MESSAGE);
	// }
	// }
	// });
	//
	// }

	/** PopUp on right click in the root */

	public void buildPopupRoot() {

		final JMenuItem editMus = new JMenuItem("Museum bearbeiten");
		final JMenuItem delMus = new JMenuItem("Museum löschen");
		final JMenuItem addExhibition = new JMenuItem("Ausstellung hinzufügen");
		final JMenuItem addLoan = new JMenuItem("Leihgabe hinzufügen");
		final JMenuItem addContact = new JMenuItem("Kontaktperson hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(editMus);
		popUp.add(delMus);
		popUp.addSeparator();
		popUp.add(addExhibition);
		popUp.add(addLoan);
		popUp.addSeparator();
		popUp.add(addContact);

		editMus.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditMuseum dialog = new EditMuseum(getMuseumId());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
					MuseumMainPanel.getInstance().refreshComboBox();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		delMus.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long Id = getMuseumId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									null,
									"Sind Sie sicher, dass Sie das ausgewählte Museum löschen möchten?",
									"Museum löschen",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == JOptionPane.YES_OPTION) {
						int eingabe_2 = JOptionPane
								.showConfirmDialog(
										null,
										"ACHTUNG: Bei Löschen des Museums werden ebenso alle Unterelemente gelöscht!\nMöchten Sie fortfahren?",
										"Warnung", JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
						if (eingabe_2 == JOptionPane.YES_OPTION) {
							Access.deleteMuseum(Id);
							MainGUI.getFrame().setVisible(false);
							MuseumChoose musDialog = new MuseumChoose(MainGUI
									.getFrame());
							musDialog.setVisible(true);
							MainGUI.getFrame().setVisible(true);
							TreeMainPanel.getInstance().refreshTree();
						}
					}

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		addExhibition.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateExhibition dialog = new CreateExhibition(false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		addLoan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateLoan dialog = new CreateLoan(false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		addContact.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					CreateContact dialog = new CreateContact();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

	/** PopUp on right click in Exhibition entry */

	public void buildPopupExh() {

		final JMenuItem addExhibition = new JMenuItem("Ausstellung hinzufügen");
		// final JMenuItem addExhibit = new
		// JMenuItem("Exponat zu einer Ausstellung hinzufügen");
		// final JMenuItem addSection = new
		// JMenuItem("Sektion zu einer Ausstellung hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(addExhibition);
		popUp.addSeparator();
		// popUp.add(addExhibit);
		// popUp.add(addSection);

		addExhibition.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateExhibition dialog = new CreateExhibition(false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// addExhibit.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		//
		// try {
		//
		// } catch (Exception e1) {
		// JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
		// JOptionPane.ERROR_MESSAGE);
		// }
		//
		// }
		// });
		//
		// addSection.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		//
		// try {
		//
		// } catch (Exception e1) {
		// JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// }
		// });

	}

	/** PopUp on right click in Loan entry */

	public void buildPopupLo() {

		final JMenuItem addLoan = new JMenuItem("Leihgabe hinzufügen");
		// final JMenuItem addExhibit = new
		// JMenuItem("Exponat zu einer Leihgabe hinzufügen");
		// final JMenuItem addSection = new
		// JMenuItem("Sektion zu einer Leihgabe hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(addLoan);
		popUp.addSeparator();
		// popUp.add(addExhibit);
		// popUp.add(addSection);

		addLoan.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateLoan dialog = new CreateLoan(false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// addExhibit.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		//
		// try {
		//
		// } catch (Exception e1) {
		// JOptionPane.showMessageDialog(null,
		// e1.getMessage(), "Fehler",
		// JOptionPane.ERROR_MESSAGE);
		// }
		//
		// }
		// });
		//
		// addSection.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		//
		// try {
		//
		// } catch (Exception e1) {
		// JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// }
		// });

	}

	/** PopUp on right click in Sub-Entries Exhibition */

	public void buildPopupExhSub() {

		final JMenuItem editExh = new JMenuItem("Ausstellung bearbeiten");
		final JMenuItem deleteExh = new JMenuItem("Ausstellung löschen");

		popUp = new JPopupMenu();
		popUp.add(editExh);
		popUp.add(deleteExh);

		editExh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditExhibition dialog = new EditExhibition(
							getExhibitionId());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteExh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long Id = getExhibitionId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									null,
									"Sind Sie sicher, dass Sie die ausgewählte Ausstellung löschen möchten?",
									"Ausstellung löschen",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == 0) {
						Access.deleteExhibition(Id);
						TreeMainPanel.getInstance().refreshTree();
					}

				} catch (IntegrityException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
//					InformationPanel.getInstance().setText(e1.getMessage());

				} catch (ModelAlreadyDeletedException | ConnectionException e2) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Ausstellung gewählt haben.");
//					InformationPanel.getInstance().setText(e2.getMessage());
				}
			}
		});

	}

	/** PopUp on right click in Sub-Entries Loan */

	public void buildPopupLoaSub() {

		final JMenuItem editLoa = new JMenuItem("Leihgabe bearbeiten");
		final JMenuItem deleteLoa = new JMenuItem("Leihgabe löschen");

		popUp = new JPopupMenu();
		popUp.add(editLoa);
		popUp.add(deleteLoa);

		editLoa.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditLoan dialog = new EditLoan(getLoanId());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteLoa.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long Id = getLoanId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									null,
									"Sind Sie sicher, dass Sie die ausgewählte Leihgabe löschen möchten?",
									"Leihgabe löschen",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == 0) {

						Access.deleteExhibition(Id);
						TreeMainPanel.getInstance().refreshTree();
					}

				} catch (IntegrityException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
//					InformationPanel.getInstance().setText(e1.getMessage());
				}

				catch (Exception e1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Leihgabe gewählt haben.");
//					InformationPanel.getInstance().setText(e1.getMessage());
				}
			}
		});

	}

	/* Getter */

	/**
	 * 
	 * @return JTree the Tree
	 */

	public JTree getTree() {
		return tree;
	}

	/**
	 * 
	 * @return TreePath the selected path
	 */
	public TreePath getSelection() {
		return tree.getSelectionPath();
	}

	/**
	 * 
	 * @return int row for selected path
	 */
	public int getSelectedRow() {
		return tree.getRowForPath(getSelection());
	}

	/**
	 * 
	 * @param TreePath
	 *            path
	 * @return int row for path
	 */
	public int getRowForPath(TreePath path) {
		return tree.getRowForPath(path);
	}

	/**
	 * 
	 * @return TreeNodeObject Last selected PathComponent as object
	 */
	public TreeNodeObject getLastSelected() {
		
		if (tree.getLastSelectedPathComponent() == null || ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject() instanceof String)
			return null;
		return (TreeNodeObject) ((DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent()).getUserObject();
	}

	/**
	 * 
	 * @return Long the museum id
	 * @throws MuseumNotFoundException
	 */
	public Long getMuseumId() throws MuseumNotFoundException {
		return MuseumMainPanel.getInstance().getMuseumId();
	}

	/**
	 * 
	 * @return Long the exhibition id
	 * @throws NullPointerException
	 */
	public Long getExhibitionId() throws NullPointerException {

		TreeNodeObject selection = getLastSelected();
		if (selection.getExhibitionId() == null) {
			return null;
		}
		return selection.getExhibitionId();

	}

	/**
	 * 
	 * @return Long the loan id
	 * @throws NullPointerException
	 */
	public Long getLoanId() throws NullPointerException {

		TreeNodeObject selection = getLastSelected();
		if (selection.getLoanId() == null) {
			throw new NullPointerException("Es wurde keine Leihgabe gewählt!");
		}
		return selection.getLoanId();

	}

	/**
	 * 
	 * @return TreeExhibitionPanel actual instance
	 */
	public static TreeExhibitionPanel getInstance() {

		if (instance == null) {

			instance = new TreeExhibitionPanel(false);
		}
		return instance;

	}

	/**
	 * 
	 * @return TreeExhibitionPanel actual instance rightclickable
	 */
	public static TreeExhibitionPanel getInstanceRightclickable() {

		if (instance == null) {

			instance = new TreeExhibitionPanel(true);
		}
		return instance;

	}

	/* Setter */

	/**
	 * 
	 * @param JTree
	 *            tree
	 */
	public void setSelection(JTree treeOrigin) {

		int selectedRow = treeOrigin.getMinSelectionRow();
		Enumeration<TreePath> expandedPaths = treeOrigin
				.getExpandedDescendants(treeOrigin.getPathForRow(0));

		ArrayList<Integer> expandedRows = new ArrayList<Integer>();

		if (expandedPaths != null) {
			while (expandedPaths.hasMoreElements()) {
				expandedRows.add(treeOrigin.getRowForPath(expandedPaths
						.nextElement()));
			}
		}

		Collections.sort(expandedRows);

		for (Integer i : expandedRows) {
			tree.expandRow(i);
		}
		tree.setSelectionRow(selectedRow);

	}
	
	public boolean isRoot(){
		path = getSelection();
		if(path.getLastPathComponent() == path.getPathComponent(0))
		{
			return true;
		}
		else{
			return false;
		}
	}
}
