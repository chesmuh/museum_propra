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

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ExceptionHandler;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.CreateContact;
import de.museum.berleburg.userInterface.dialogs.CreateSection;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;
import de.museum.berleburg.userInterface.dialogs.EditMuseum;
import de.museum.berleburg.userInterface.dialogs.EditSection;
import de.museum.berleburg.userInterface.dialogs.MuseumChoose;

/**
 * Create the SectionPanel.
 * 
 * @author Maximilian Beck
 * 
 * 
 */
public class TreeSectionPanel extends JPanel {

	private static final long serialVersionUID = 8610384835709697547L;

	private JPopupMenu popUp;
	private JTree tree;
	private static Long lastUsedMuseum = 0L;
	public TreePath path;

	/**
	 * @param rightclickable
	 */

	public TreeSectionPanel(boolean rightclickable) {

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

				private void rightClick(MouseEvent e) {
					int x = e.getX();
					int y = e.getY();
					int selRow = tree.getRowForLocation(x, y);
					TreePath path = tree.getPathForLocation(x, y);

					/**
					 * Listener on right mouseclick
					 */

					if (e.isPopupTrigger()) {

						if (selRow == -1) {

						}

						else if (path.getLastPathComponent() == path
								.getPathComponent(0)) {
							tree.setSelectionPath(path);
							buildPopupRoot();
							popUp.show(e.getComponent(), x, y);

						} else {
							tree.setSelectionPath(path);
							buildPopup();
							popUp.show(e.getComponent(), x, y);
						}
					}

					/**
					 * Listener on left mouseclick
					 */

					if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

						TableButtonPanel.getInstance().setRetoureVisible(false);

						if (selRow == -1) {

						}

						else if (path.getLastPathComponent() == path
								.getPathComponent(0)) {
							TablePanel table = TablePanel.getInstance();
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									Museum museum = Access.searchMuseumID(Id);
									ArrayList<Long> empty = new ArrayList<Long>();
									MainGUI.getDetailPanel().setDetails(museum);
									ArrayList<Section> sections = Access
											.getAllSubSectionsFromMuseum(Id);
									ArrayList<Long> sectionId = new ArrayList<Long>();
									for (Section s : sections) {
										sectionId.add(s.getId());
									}
									table.updateTable(Id, empty, empty, empty,
											sectionId, empty, empty, empty,
											empty);
								}

							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							TablePanel table = TablePanel.getInstance();

							try {
								Long Id = getSectionId();
								if(Id!= null){
									Section section = Access.searchSectionID(Id);
									MainGUI.getDetailPanel().setDetails(section);
									table.updateTable(0, 0, Id, 0, 0);
								}
							} catch (SectionNotFoundException e1) {
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
		Collection<Section> sections = Access
				.getAllSubSectionsFromMuseum(MuseumMainPanel.getInstance()
						.getMuseumId());

		for (Section s : sections) {
			if ((s.getParent_id() == null) || (s.getParent_id().equals(0L))) {
				listSectionsBySectionId(s, nodeMuseum);
			}
		}

		return nodeMuseum;
	}

	/**
	 * 
	 * @param Section
	 * @param nodeSection
	 * @throws Exception
	 */

	private void listSectionsBySectionId(Section section,
			DefaultMutableTreeNode nodeSection) throws Exception {
		TreeNodeObject local = new TreeNodeObject(section.getName());
		local.setSectionId(section.getId());
		DefaultMutableTreeNode nodeLocal = new DefaultMutableTreeNode(local);
		nodeSection.add(nodeLocal);

		Collection<Section> childs = Access
				.getAllSubSectionsFromSection(section.getId());

		for (Section s : childs)
			listSectionsBySectionId(s, nodeLocal);
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

			Collections.sort(expandedRows);

		}

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

			Collections.sort(expandedRows);

		}

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
		final JMenuItem addSection = new JMenuItem("Obersektion hinzufügen");
		final JMenuItem addContact = new JMenuItem("Kontaktperson hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(editMus);
		popUp.add(delMus);
		popUp.addSeparator();
		popUp.add(addSection);
		popUp.addSeparator();
		popUp.add(addContact);

		editMus.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {

					EditMuseum dialog = new EditMuseum(getMuseumId());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
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
					if (eingabe == 0) {
						int eingabe_2 = JOptionPane
								.showConfirmDialog(
										null,
										"ACHTUNG: Bei Löschen des Museums werden ebenso alle Unterelemente gelöscht!\nMöchten Sie fortfahren?",
										"Warnung", JOptionPane.YES_NO_OPTION,
										JOptionPane.WARNING_MESSAGE);
						if (eingabe_2 == 0) {
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

		addSection.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateSection dialog = new CreateSection();
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

	/** PopUp on right click in normal entries */

	public void buildPopup() {

		final JMenuItem addExhibit = new JMenuItem("Exponat hinzufügen");
		final JMenuItem addSection = new JMenuItem("Untersektion hinzufügen");
		final JMenuItem editSection = new JMenuItem("Sektion bearbeiten");
		final JMenuItem deleteSection = new JMenuItem("Sektion löschen");
		final JMenuItem deleteSectionAndExh = new JMenuItem(
				"Sektion mit Exponaten löschen");
		// final JMenuItem toExhibit = new JMenuItem(
		// "Sektion zur Ausstellung hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(addExhibit);
		popUp.add(addSection);
		popUp.addSeparator();
		popUp.add(editSection);
		popUp.add(deleteSection);
		popUp.add(deleteSectionAndExh);
		// popUp.add(toExhibit);

		addExhibit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditExhibit dialog = new EditExhibit(Access
							.searchMuseumID(getMuseumId()), Access
							.searchSectionID(getSectionId()), null);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		addSection.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					CreateSection dialog = new CreateSection();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		editSection.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					EditSection dialog = new EditSection(getSectionId());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteSection.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long Id = getSectionId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									TreeSectionPanel.this,
									"Sind Sie sicher, dass Sie die ausgewählte Sektion mit allen Untersektionen löschen möchten?\nHinweis: Alle zugehörigen Exponate werden \""
											+ getSelection().getParentPath()
													.getLastPathComponent()
													.toString()
											+ "\" hinzugefügt.",
									"Sektion löschen",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == 0) {
						Access.deleteSection(Id);
						TreeMainPanel.getInstance().refreshTree();
					}
				} catch (Exception e1) {
					JOptionPane
							.showMessageDialog(
									TreeSectionPanel.this,
									"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Sektion gewählt haben.");
					InformationPanel.getInstance().setText(e1.getMessage());
				}
			}
		});

		deleteSectionAndExh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long sectionId = getSectionId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									null,
									"Sind Sie sicher, dass Sie die ausgewählte Sektion mit allen Untersektionen und allen zugehörigen Exponaten löschen möchten?\nHinweis: Alle zugehörigen Exponate werden ebenfalls gelöscht!",
									"Sektion mit Exponaten löschen",
									JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == 0) {
						ArrayList<Section> sections = Access
								.getAllSubSectionsFromSection(sectionId);
						sections.add(Access.searchSectionID(sectionId));
						for (Section s : sections) {

							ArrayList<Exhibit> exhibits = Access
									.getAllExhibitsBySection(s.getId());
							for (Exhibit ex : exhibits) {
								Access.deleteExhibit(ex.getId());
								MainGUI.getDetailPanel().refresh();
							}
						}
						Access.deleteSection(sectionId);
						TreeMainPanel.getInstance().refreshTree();
					}
				} catch (Exception e1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie eine Sektion gewählt haben.");
					InformationPanel.getInstance().setText(e1.getMessage());
				}
			}
		});

		// toExhibit.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		// try {
		// AddToExhibition dialog = new
		// AddToExhibition(Access.getAllExhibitsBySection(getSectionId()));
		// dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// dialog.setVisible(true);
		// } catch (Exception e1) {
		// JOptionPane.showMessageDialog(null,
		// e1.getMessage(), "Fehler",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// }
		// });

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
		if (tree.getLastSelectedPathComponent() == null)
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
	 * @return Long the section id
	 * @throws SectionNotFoundException
	 */
	public Long getSectionId() throws SectionNotFoundException {

		TreeNodeObject selection = getLastSelected();
		if (selection == null)
			return null;
		if (selection.getSectionId() == null) {
			if (selection.getMuseumId() == null)
				throw new SectionNotFoundException(
						"Es wurde keine Sektion gewählt!");
		}
		return selection.getSectionId();

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

			Collections.sort(expandedRows);

			for (Integer i : expandedRows) {
				tree.expandRow(i);
			}
			tree.setSelectionRow(selectedRow);
		}
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
