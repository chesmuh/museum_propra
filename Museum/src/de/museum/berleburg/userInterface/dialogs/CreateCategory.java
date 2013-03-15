package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.TreeCategoryPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

public class CreateCategory extends JDialog {

	/**
	 * Create the CreateCategoryJDialog.
	 * 
	 * @author Alexander Adema
	 * @author Way Dat To (Finding Errors etc.)
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNameCategory;
	private TreeCategoryPanel treeCategoryPanel;
	
	private String nameOfCategory;
	private Long parent_category_id;
	private long museum_id;


	/**
	 * Create the dialog.
	 */
	public CreateCategory() {
		setModal(true);
		setTitle("Neue Kategorie");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setMinimumSize(new Dimension(350,400));
		setBounds(100, 100, 346, 514);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 1.0, 0.0};
		contentPanel.setLayout(gbl_contentPanel);
		
		JTextPane txtpnBitteWhlenSie = new JTextPane();
		txtpnBitteWhlenSie.setEditable(false);
		txtpnBitteWhlenSie.setText("Bitte wählen Sie die Kategorie aus, unter der eine neue Kategorie angelegt werden soll.");
		GridBagConstraints gbc_txtpnBitteWhlenSie = new GridBagConstraints();
		gbc_txtpnBitteWhlenSie.gridwidth = 2;
		gbc_txtpnBitteWhlenSie.fill = GridBagConstraints.BOTH;
		gbc_txtpnBitteWhlenSie.gridx = 0;
		gbc_txtpnBitteWhlenSie.gridy = 0;
		contentPanel.add(txtpnBitteWhlenSie, gbc_txtpnBitteWhlenSie);
		
		JPanel treePanel = new JPanel();
		GridBagConstraints gbc_treePanel = new GridBagConstraints();
		gbc_treePanel.gridwidth = 2;
		gbc_treePanel.fill = GridBagConstraints.BOTH;
		gbc_treePanel.gridx = 0;
		gbc_treePanel.gridy = 1;
		contentPanel.add(treePanel, gbc_treePanel);
		treePanel.setLayout(new BorderLayout(0, 0));
		
		treeCategoryPanel = new TreeCategoryPanel(false);
		treeCategoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		treeCategoryPanel.setPreferredSize(new Dimension(480, 480));
		treeCategoryPanel.setMinimumSize(new Dimension(240, 300));
		treeCategoryPanel.setMaximumSize(new Dimension(480, 32767));
		treeCategoryPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		treeCategoryPanel.setSelection(TreeMainPanel.getInstance().getTreeCategoryPanel().getTree());
		treePanel.add(treeCategoryPanel);
		
		JLabel lblNameOfCategory = new JLabel("Name der Kategorie");
		lblNameOfCategory.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblNameOfCategory = new GridBagConstraints();
		gbc_lblNameOfCategory.gridwidth = 2;
		gbc_lblNameOfCategory.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNameOfCategory.insets = new Insets(0, 0, 0, 5);
		gbc_lblNameOfCategory.gridx = 0;
		gbc_lblNameOfCategory.gridy = 2;
		contentPanel.add(lblNameOfCategory, gbc_lblNameOfCategory);
		
		textFieldNameCategory = new JTextField();
		GridBagConstraints gbc_textFieldNameCategory = new GridBagConstraints();
		gbc_textFieldNameCategory.fill = GridBagConstraints.BOTH;
		gbc_textFieldNameCategory.gridx = 1;
		gbc_textFieldNameCategory.gridy = 2;
		contentPanel.add(textFieldNameCategory, gbc_textFieldNameCategory);
		textFieldNameCategory.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			GridBagConstraints gbc_buttonPane = new GridBagConstraints();
			gbc_buttonPane.anchor = GridBagConstraints.NORTH;
			gbc_buttonPane.fill = GridBagConstraints.HORIZONTAL;
			gbc_buttonPane.gridx = 0;
			gbc_buttonPane.gridy = 1;
			getContentPane().add(buttonPane, gbc_buttonPane);
			{
				JButton btnapply = new JButton("Anlegen");
				btnapply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
	
							try {
								getValuesOfCategory();
								Access.insertCategory(getNameOfCategory(),getParent_Category_id(), getMuseum_id());
								TreeMainPanel.getInstance().refreshTree();
								dispose();
							} catch (MuseumNotFoundException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler: Museum nicht gefunden", JOptionPane.ERROR_MESSAGE);

							} catch (ConnectionException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler mit der DB-Verbindung", JOptionPane.ERROR_MESSAGE);

							} catch (InvalidArgumentsException e) {
								JOptionPane.showMessageDialog(null, e.getMessage(), "Parameter-Fehler", JOptionPane.ERROR_MESSAGE);
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
	}

	
	/**
	 * Get all Values from the category
	 *  
	 * @param text
	 */
	public void getValuesOfCategory(){
		
		

				try {
						setParent_Category_id(treeCategoryPanel.getCategoryId());
					} catch (CategoryNotFoundException e1) {
				
					JOptionPane.showMessageDialog(this, e1.getMessage());
					} catch (NullPointerException e1) {
						setParent_Category_id(null);
				} 

		
		nameOfCategory = textFieldNameCategory.getText();
		try {
			setMuseum_id(TreeMainPanel.getInstance().getMuseumId());
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);		
		} 
	}


	/**
	 * @return the nameOfCategory
	 */
	public String getNameOfCategory() {
		return nameOfCategory;
	}

	/**
	 * @param nameOfCategory
	 */
	public void setNameOfCategory(String nameOfCategory) {
		this.nameOfCategory = nameOfCategory;
	}

	/**
	 * @return the parent_category_id
	 */
	public Long getParent_Category_id() {
		return parent_category_id;
	}

	/**
	 * @param parent_id
	 */
	public void setParent_Category_id(Long parent_id) {
		this.parent_category_id = parent_id;
	}

	/**
	 * @return the museum_id
	 */
	public Long getMuseum_id() {
		return museum_id;
	}

	/**
	 * @param museum_id
	 */
	public void setMuseum_id(Long museum_id) {
		this.museum_id = museum_id;
	}


	/**
	 * @param museum_id the museum_id to set
	 */
	public void setMuseum_id(long museum_id) {
		this.museum_id = museum_id;
	}

}