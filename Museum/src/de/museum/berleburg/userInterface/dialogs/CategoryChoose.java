package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.listeners.CategoryListener;
import de.museum.berleburg.userInterface.panels.TreeCategoryPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

public class CategoryChoose extends JDialog {

	/**
	 * @author Alexander Adema, Timo Funke, Frank Hülsmann
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	private Long categoryId;
	private Long parentID;
	private Long museumID;
	private String categoryName;
	private List<CategoryListener> listeners = new LinkedList<CategoryListener>();

	private TreeCategoryPanel categoryTree;

	/**
	 * Create the dialog.
	 */
	public CategoryChoose() {
		/** @author Christian Landel */
		super();
		init();
	}
	public CategoryChoose(Dialog owner) {
		/** @author Christian Landel */
		super(owner);
		init();
	}
	
	private void init()
	{
		setModal(true);
		setMinimumSize(new Dimension(320, 400));
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 321, 400);
		getContentPane().setLayout(new BorderLayout());

		categoryTree = new TreeCategoryPanel(false);
		categoryTree.setToolTipText("Wählen sie eine Kategorie");
		categoryTree.setBounds(0, 0, 312, 342);
		categoryTree.setSelection(TreeMainPanel.getInstance()
				.getTreeCategoryPanel().getTree());
		contentPanel.add(categoryTree);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnChooseCategory = new JButton("Auswählen");
				btnChooseCategory.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						getValuesFromCategoryTree();

						Category cat = null;
						try {
							cat = Access.searchCategoryID(getCategoryId());
						} catch (CategoryNotFoundException e1) {
							JOptionPane.showConfirmDialog(CategoryChoose.this,
									e1.getMessage());
						}

						for (CategoryListener listener : listeners)
							listener.event(cat);

						dispose();

					}
				});
				btnChooseCategory.setActionCommand("OK");
				buttonPane.add(btnChooseCategory);
				getRootPane().setDefaultButton(btnChooseCategory);
			}

		}
	}
	/**
	 * add the listener
	 * @param listener
	 */
	public void addListener(CategoryListener listener) {
		listeners.add(listener);
	}
	
	
	/**
	 * get the Values from the Category Tree
	 */
	public void getValuesFromCategoryTree() {

		try {
			setMuseumID(categoryTree.getMuseumId());
		} catch (MuseumNotFoundException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}

		try {
			setCategoryId(categoryTree.getCategoryId());
		} catch (CategoryNotFoundException e1) {
			JOptionPane.showMessageDialog(this, e1.getMessage());
		}
		try {
			if(getCategoryId() == null)
				throw new CategoryNotFoundException("Das Museum ist keine Kategorie!");
			Category cat = Access.searchCategoryID(getCategoryId());
			setCategoryName(cat.getName());

		} catch (CategoryNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}

	}

	/**
	 * @return the categoryId
	 */
	public Long getCategoryId() {
		return categoryId;
	}

	/**
	 * @param categoryId
	 *            the categoryId to set
	 */
	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * @return the parentID
	 */
	public Long getParentID() {
		return parentID;
	}

	/**
	 * @param parentID
	 *            the parentID to set
	 */
	public void setParentID(Long parentID) {
		this.parentID = parentID;
	}

	/**
	 * @return the museumID
	 */
	public Long getMuseumID() {
		return museumID;
	}

	/**
	 * @param museumID
	 *            the museumID to set
	 */
	public void setMuseumID(Long museumID) {
		this.museumID = museumID;
	}

	/**
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName
	 *            the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

}
