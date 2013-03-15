package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.TreeCategoryPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

public class EditCategory extends JDialog {

	/**
	 * Create the EditCategoryJDialog.
	 * 
	 * 
	 * @author Way Dat To
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNameCategory;
	private TreeCategoryPanel treeCategoryPanel;

	private String nameOfCategory;
	private Long parent_category_id;
	private long museum_id;
	private Category category;
	private long id;

	/**
	 * Launch the application.
	 */

	@Override
	public void dispose() {
		MainGUI.getDetailPanel().refresh();
		super.dispose();
	}

	/**
	 * Create the dialog.
	 */
	public EditCategory(long toEdit) {
		id = toEdit;
		try {
			category = Access.searchCategoryID(toEdit);
		} catch (CategoryNotFoundException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setModal(true);
		setTitle("Kategorie bearbeiten");
		setMinimumSize(new Dimension(350, 400));
		setBounds(100, 100, 346, 514);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0 };
		getContentPane().setLayout(gridBagLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridBagConstraints gbc_contentPanel = new GridBagConstraints();
		gbc_contentPanel.fill = GridBagConstraints.BOTH;
		gbc_contentPanel.gridx = 0;
		gbc_contentPanel.gridy = 0;
		getContentPane().add(contentPanel, gbc_contentPanel);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_contentPanel.rowWeights = new double[] { 0.0, 1.0, 0.0 };
		contentPanel.setLayout(gbl_contentPanel);

		JTextPane txtpnBitteWhlenSie = new JTextPane();
		txtpnBitteWhlenSie.setEditable(false);
		txtpnBitteWhlenSie
				.setText("Bitte wählen Sie eine neue Kategorie aus, unter der Sie die zu bearbeitende Kategorie verschieben möchten.");
		GridBagConstraints gbc_txtpnBitteWhlenSie = new GridBagConstraints();
		gbc_txtpnBitteWhlenSie.fill = GridBagConstraints.BOTH;
		gbc_txtpnBitteWhlenSie.gridwidth = 2;
		gbc_txtpnBitteWhlenSie.gridx = 0;
		gbc_txtpnBitteWhlenSie.gridy = 0;
		contentPanel.add(txtpnBitteWhlenSie, gbc_txtpnBitteWhlenSie);

		JPanel treePanel = new JPanel();
		GridBagConstraints gbc_treePanel = new GridBagConstraints();
		gbc_treePanel.fill = GridBagConstraints.BOTH;
		gbc_treePanel.gridwidth = 2;
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
		treeCategoryPanel.setSelection(TreeMainPanel.getInstance()
				.getTreeCategoryPanel().getTree());
		treePanel.add(treeCategoryPanel);

		JLabel lblNameOfCategory = new JLabel("Name der Kategorie");
		lblNameOfCategory.setFont(new Font("Arial", Font.BOLD, 13));
		GridBagConstraints gbc_lblNameOfCategory = new GridBagConstraints();
		gbc_lblNameOfCategory.fill = GridBagConstraints.HORIZONTAL;
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
				JButton btnapply = new JButton("Änderung speichern");
				btnapply.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

						getValuesOfCategory();
						try {
							if (parent_category_id!=null&&id == parent_category_id) {
								parent_category_id = DataAccess.getInstance()
										.getCategoryById(id).getParent_id();
							}
							Access.changeCategory(id, nameOfCategory,
									museum_id, parent_category_id);
							InformationPanel.getInstance().setText(
									"Änderungen an Kategorie erfolgreich!");
							TreeMainPanel.getInstance().refreshTree();
							dispose();
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (CategoryNotFoundException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (MuseumNotFoundException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
							getValuesOfCategory();
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
		setValues();
	}

	public void getValuesOfCategory() {

		if (treeCategoryPanel.getSelection().getPathCount() > 1) {
			try {
				setParent_Category_id(treeCategoryPanel.getCategoryId());
			} catch (CategoryNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		nameOfCategory = getTextFieldNameCategory().getText();
		try {
			setMuseum_id(treeCategoryPanel.getMuseumId());
		} catch (MuseumNotFoundException e) {

			JOptionPane.showMessageDialog(null, "Museum nicht gefunden!",
					"Fehler", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(this, e.getMessage());

		}
	}

	public JTextField getTextFieldNameCategory() {
		return textFieldNameCategory;
	}

	public String getNameOfCategory() {
		return nameOfCategory;
	}

	public void setNameOfCategory(String nameOfCategory) {
		this.nameOfCategory = nameOfCategory;
	}

	public Long getParent_Category_id() {
		return parent_category_id;
	}

	public void setParent_Category_id(Long parent_id) {
		this.parent_category_id = parent_id;
	}

	public Long getMuseum_id() {
		return museum_id;
	}

	public void setMuseum_id(Long museum_id) {
		this.museum_id = museum_id;
	}

	private void setValues() {
		textFieldNameCategory.setText(category.getName());
		treeCategoryPanel.setSelection(TreeMainPanel.getInstance()
				.getTreeCategoryPanel().getTree());
	}

	public static void checkCategoryId(long myCategory, long parentCategory)
			throws ParentIdException2 {
		if (myCategory != parentCategory) {

		} else {
			throw new ParentIdException2();
		}

	}
}

@SuppressWarnings("serial")
class ParentIdException2 extends Exception {
	public ParentIdException2() {
		super(
				"Ausgewählte Kategorie ist ungültig.\n Bitte wählen Sie eine andere Kategorie.");
	}

}