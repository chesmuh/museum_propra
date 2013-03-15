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

import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ExceptionHandler;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.CreateContact;
import de.museum.berleburg.userInterface.dialogs.EditMuseum;
import de.museum.berleburg.userInterface.dialogs.MuseumChoose;

/**
 * Create the LabelPanel.
 * 
 * @author Maximilian Beck
 * 
 * 
 */
public class TreeLabelPanel extends JPanel {

	private static final long serialVersionUID = -4378230876822443848L;

	private JPopupMenu popUp;
	private JTree tree;
	private static Long lastUsedMuseum = 0L;
	private static TreeLabelPanel instance = null;
	public TreePath path;

	/**
	 * @param rightclickable
	 */

	public TreeLabelPanel(boolean rightclickable) {
		instance = this;

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

						} else if (path.getLastPathComponent() == path
								.getPathComponent(0)) {
							try {
								Long Id = getMuseumId();
								if (Id != null) {
									Museum museum = Access.searchMuseumID(Id);
									MainGUI.getDetailPanel().setDetails(museum);
								}
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						} else {
							TablePanel table = TablePanel.getInstance();

							try {
								Long labelId = getLabelId();
								Long museumId = getMuseumId();
								// Label Label = Access.searchLabelById(Id);
								// MainGUI.getDetailPanel().setDetails(Label);
								table.updateTable(museumId, 0, 0, labelId, 0);
							} catch (LabelNotFoundException
									| MuseumNotFoundException e1) {
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
		DefaultMutableTreeNode mainNode = new DefaultMutableTreeNode(local);

		Collection<Label> labels = Access.getAllLabels();

		for (Label s : labels) {
			TreeNodeObject label = new TreeNodeObject(s.getName());
			label.setLabelId(s.getId());
			DefaultMutableTreeNode nodeLabel = new DefaultMutableTreeNode(label);
			mainNode.add(nodeLabel);

		}

		return mainNode;
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

	/** PopUp on right click in blank area */

	// public void buildPopupBlankArea() {
	//
	// final JMenuItem newMus = new JMenuItem("Neues Museum");
	//
	// popUp = new JPopupMenu();
	// popUp.add(newMus);
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
		final JMenuItem addLabel = new JMenuItem("Label hinzufügen");
		final JMenuItem addContact = new JMenuItem("Kontaktperson hinzufügen");

		popUp = new JPopupMenu();
		popUp.add(editMus);
		popUp.add(delMus);
		popUp.addSeparator();
		popUp.add(addLabel);
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

		addLabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					LabelsPanel.createLabel();
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

		final JMenuItem addLabel = new JMenuItem("Label hinzufügen");
		final JMenuItem editLabel = new JMenuItem("Labels bearbeiten");
		final JMenuItem deleteLabel = new JMenuItem("Label löschen");

		popUp = new JPopupMenu();
		popUp.add(addLabel);
		popUp.add(editLabel);
		popUp.add(deleteLabel);

		addLabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					LabelsPanel.createLabel();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		editLabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					LabelsPanel.dialog();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		deleteLabel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				try {
					long Id = getLabelId();
					int eingabe = JOptionPane
							.showConfirmDialog(
									null,
									"Sind Sie sicher, dass Sie das ausgewählte Label löschen möchten?",
									"Label löschen", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE);
					if (eingabe == 0) {
						Access.deleteLabel(Id);
						TreeMainPanel.getInstance().refreshTree();
					}

				} catch (Exception e1) {
					JOptionPane
							.showMessageDialog(
									null,
									"Der Eintrag konnte nicht entfernt werden! \nStellen Sie sicher, dass sie ein Label gewählt haben.");
					InformationPanel.getInstance().setText(e1.getMessage());
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
	 * @return Long the label id
	 * @throws LabelNotFoundException
	 */
	public Long getLabelId() throws LabelNotFoundException {

		TreeNodeObject selection = getLastSelected();
		if (selection.getLabelId() == null) {
			if (selection.getMuseumId() == null) {
				throw new LabelNotFoundException("Es wurde kein Label gewählt!");
			}
		}
		return selection.getLabelId();

	}

	/**
	 * 
	 * @return TreeLabelPanel actual instance
	 */
	public static TreeLabelPanel getInstance() {

		if (instance == null) {

			instance = new TreeLabelPanel(false);
		}
		return instance;

	}

	/**
	 * 
	 * @return TreeLabelPanel actual instance rightclickable
	 */
	public static TreeLabelPanel getInstanceRightclickable() {

		if (instance == null) {

			instance = new TreeLabelPanel(true);
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
