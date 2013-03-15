package de.museum.berleburg.userInterface.dialogs;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeCategoryPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;

public class DuplicateCategory extends JDialog {

	/**
	 * Duplicates the Category from museum
	 * 
	 * @author Maximilian Beck
	 * 
	 */
	private static final long serialVersionUID = -7972138794901787603L;
	private Long category_id;
	private Long museum_id;
	private JComboBox<TreeNodeObject> comboBox;
	private TreeCategoryPanel treeCategoryPanel;

	/**
	 * Create the dialog.
	 */
	public DuplicateCategory() {
		setModal(true);
		setTitle("Kategorie duplizieren");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 400, 500);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{0.0, 1.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
		comboBox = new JComboBox<TreeNodeObject>();
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.anchor = GridBagConstraints.WEST;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 2;
		getContentPane().add(comboBox, gbc_comboBox);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.gridwidth = 2;
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 3;
			getContentPane().add(buttonPane, gbc_buttonPane);
			{
				JButton btnapply = new JButton("Duplizieren");
				
				btnapply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

						try {
							cloneCategory();
							TreeMainPanel.getInstance().refreshTree();
							dispose();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null,
									"Bitte wählen Sie eine Kategorie aus.",
									"Kategorie wählen",
									JOptionPane.INFORMATION_MESSAGE);
						}

					}
				});
				btnapply.setActionCommand("OK");
				buttonPane.add(btnapply);
				getRootPane().setDefaultButton(btnapply);
			}
			{
				JButton btnCancel = new JButton("Abbrechen");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
		
		JLabel label = new JLabel("Zielmuseum:");
		label.setFont(new Font("Dialog", Font.BOLD, 13));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 2;
		getContentPane().add(label, gbc_label);
		
		treeCategoryPanel = new TreeCategoryPanel(false);
		treeCategoryPanel.setSelection(TreeMainPanel.getInstance()
				.getTreeCategoryPanel().getTree());
		GridBagConstraints gbc_treeCategoryPanel = new GridBagConstraints();
		gbc_treeCategoryPanel.gridwidth = 2;
		gbc_treeCategoryPanel.fill = GridBagConstraints.BOTH;
		gbc_treeCategoryPanel.gridx = 0;
		gbc_treeCategoryPanel.gridy = 1;
		getContentPane().add(treeCategoryPanel, gbc_treeCategoryPanel);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Bitte wählen Sie die Kategorie, die Sie duplizieren möchten sowie das Museum, in das dupliziert werden soll, aus.\nUm alle Kategorien zu duplizieren, wählen Sie das Museum aus.");
		textPane.setEditable(false);
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.gridwidth = 2;
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 0;
		getContentPane().add(textPane, gbc_textPane);

		ArrayList<Museum> allmuseums = Access.getAllMuseums();
		for (Museum i : allmuseums) {
			if (i.getId() != MuseumMainPanel.getInstance().getMuseumId()){
	        TreeNodeObject o = new TreeNodeObject(i.getName());
	        o.setMuseumId(i.getId());
	        comboBox.addItem(o);
			}
		}
		
		setMuseum_id(getMuseumTreeNode().getMuseumId());
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

					TreeNodeObject o = (TreeNodeObject) comboBox
							.getSelectedItem();
					if (o == null)
						return;
					setMuseum_id(o.getMuseumId());
				}
			}
		});
	}

	/**
	 * Clones the Category
	 */
	public void cloneCategory() {

		if (treeCategoryPanel.getSelection().getPathCount() > 1) {
			try {
				category_id = treeCategoryPanel.getCategoryId();
				Access.copyCategory(category_id, museum_id);
			} catch (ConnectionException e1) {
				JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
			} catch (CategoryNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (MuseumNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidArgumentsException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
			
//			catch (CategoryNotFoundException | ConnectionException e1) {
//				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
//						JOptionPane.ERROR_MESSAGE);
//
//			}
		} else if (treeCategoryPanel.getSelection().getPathCount() == 1) {
			try {
				ArrayList<Category> allCategories = Access.getAllCategoriesByMuseum(MuseumMainPanel.getInstance()
								.getMuseumId());
				ArrayList<Category> categoriesNoParent = new ArrayList<Category>();
				
				for (Category cat : allCategories){
					if(cat.getParent_id()==null || cat.getParent_id().equals(0L)){
						categoriesNoParent.add(cat);
					}
				}
				for (Category c : categoriesNoParent) {
					Access.copyCategory(c.getId(), museum_id);
				}

			} catch (ConnectionException e) {
				JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
			}
			 catch (MuseumNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			} catch (InvalidArgumentsException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	/* Getter */
	
	/**
	 * @return Long the museum id
	 */
	public Long getMuseum_id() {
		return museum_id;
	}

	/**
	 * 
	 * @return TreeNodeObject the museum tree node
	 */
	public TreeNodeObject getMuseumTreeNode() {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			if (comboBox.getItemAt(i).getMuseumId() == museum_id)
				return comboBox.getItemAt(i);
		}
		TreeNodeObject o = (TreeNodeObject) comboBox.getSelectedItem();
		
		return o;
	}

	/* Setter */

	/**
	 * @param museum_id
	 */
	public void setMuseum_id(Long museum_id) {
		this.museum_id = museum_id;
	}

}
