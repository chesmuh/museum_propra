package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;
import de.museum.berleburg.userInterface.panels.TreeSectionPanel;
import de.museum.berleburg.userInterface.table.TableModelMassChange;

public class MassChange extends JDialog {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();

	private TablePanel tablePanel;
	
	private ArrayList<Exhibit> toChange = new ArrayList<Exhibit>();
	private ArrayList<Long> ExhibitIds;
	private Long museum_id;
	private TreeNodeObject museumNode;

	private ArrayList<String> SectionNames;
	private JComboBox<TreeNodeObject> comboBoxCategory;
	private JComboBox<TreeNodeObject> comboBoxMuseum;
	private JTable tableChoosenObjects;
	private JPanel panelChoosenObjects;
	private Long section_id;
	
	private TableModelMassChange model;
	private Exhibit choose2;
	private ArrayList<Exhibit> choose3;
	private JCheckBox chckbxMuseum;
	private JCheckBox chckbxSection;
	private JCheckBox chckbxCategory;
	private TreeSectionPanel paneltreeSection;
	private JComboBox<TreeNodeObject> result;


	/**
	 * Create the dialog.
	 */
	public MassChange(ArrayList<Long> choose) {
		setBounds(100, 100, 596, 597);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		this.setModal(true);
		
		panelChoosenObjects = new JPanel();
		panelChoosenObjects.setBounds(10, 303, 560, 212);
		contentPanel.add(panelChoosenObjects);
		panelChoosenObjects.setLayout(null);
		
		tableChoosenObjects = new JTable();
//		tableChoosenObjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableChoosenObjects.setBounds(0, 0, 583, 188);
		panelChoosenObjects.setBorder(null);
		panelChoosenObjects.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		panelChoosenObjects.add(new JScrollPane(tableChoosenObjects), c);
		choose3 = new ArrayList<Exhibit>();
		for (Long long1 : choose) {
			try {
				choose2 = Access.searchExhibitID(long1);
			} catch (ExhibitNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			choose3.add(choose2);
		}
		model = new TableModelMassChange(choose3);
		tableChoosenObjects.setModel(model);
		tableChoosenObjects.getTableHeader().setReorderingAllowed(false);
		tableChoosenObjects.setRowSelectionAllowed(true);
		tableChoosenObjects.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		
		chckbxSection = new JCheckBox("Sektion");
		chckbxSection.setBounds(10, 7, 97, 23);
		contentPanel.add(chckbxSection);
		
		chckbxCategory = new JCheckBox("Kategorie");
		chckbxCategory.setBounds(283, 7, 97, 23);
		contentPanel.add(chckbxCategory);
		
		paneltreeSection = new TreeSectionPanel(false);
		paneltreeSection.setBounds(10, 37, 220, 237);
		contentPanel.add(paneltreeSection);
		
		comboBoxCategory = new JComboBox<TreeNodeObject>();
		comboBoxCategory.setBounds(283, 37, 220, 20);
		contentPanel.add(comboBoxCategory);
		

		
		comboBoxMuseum = getComboClone();
		comboBoxMuseum.setBounds(283, 142, 220, 20);
		comboBoxMuseum.setEnabled(false);
		contentPanel.add(comboBoxMuseum);
		
//		comboBoxMuseum.setBounds(283, 142, 220, 20);
//		contentPanel.add(comboBoxMuseum);
		
		chckbxMuseum = new JCheckBox("Museum");
		chckbxMuseum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (chckbxMuseum.isSelected()) {
					chckbxCategory.setSelected(true);
					chckbxSection.setSelected(true);
					chckbxCategory.setEnabled(false);
					chckbxSection.setEnabled(false);
					comboBoxMuseum.setEnabled(true);
				}
				else {
					for(int i=0; i< comboBoxMuseum.getItemCount(); i++){
						long currId = comboBoxMuseum.getItemAt(i).getMuseumId();
						if(currId == museum_id)
							comboBoxMuseum.setSelectedIndex(i);
					}
					chckbxCategory.setSelected(false);
					chckbxSection.setSelected(false);
					chckbxCategory.setEnabled(true);
					chckbxSection.setEnabled(true);
					comboBoxMuseum.setEnabled(false);
				}
			}
		});
		chckbxMuseum.setBounds(283, 112, 97, 23);
		contentPanel.add(chckbxMuseum);
		museum_id = MuseumMainPanel.getInstance().getMuseumId();
		museumNode = MuseumMainPanel.getInstance().getMuseumTreeNode();
		for(int i=0; i< comboBoxMuseum.getItemCount(); i++){
			long currId = comboBoxMuseum.getItemAt(i).getMuseumId();
			if(currId == museum_id)
				comboBoxMuseum.setSelectedIndex(i);
			
		}
		
		
		comboBoxMuseum.addActionListener (new ActionListener () {
	        public void actionPerformed(ActionEvent e) {
	        	MuseumMainPanel.getInstance().setMuseumId(((TreeNodeObject)comboBoxMuseum.getSelectedItem()).getMuseumId());
	        	paneltreeSection.setVisible(false);
	        	contentPanel.remove(paneltreeSection);
	        	paneltreeSection = new TreeSectionPanel(false);
	        	paneltreeSection.setVisible(true);
	        	paneltreeSection.setBounds(10, 37, 220, 237);
	        	contentPanel.add(paneltreeSection);
	        		 
	        	contentPanel.remove(comboBoxCategory);
	        	comboBoxCategory = new JComboBox<TreeNodeObject>();
	        	comboBoxCategory.setVisible(true);
	        	comboBoxCategory.setBounds(283, 37, 220, 20);
	    		contentPanel.add(comboBoxCategory);
	    		
	    		try {
					fillchoices2();
				} catch (MuseumNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
	        }
	    });
		
		
		JLabel lblWennBlablabla = new JLabel("Bei einer Verschiebung von Exponaten");
		lblWennBlablabla.setBounds(283, 196, 287, 14);
		contentPanel.add(lblWennBlablabla);
		
		JLabel lblNewLabel = new JLabel("in ein anderes Museum, müssen sowohl");
		lblNewLabel.setBounds(283, 221, 287, 14);
		contentPanel.add(lblNewLabel);
		
		JLabel lblSektionUndKategorie = new JLabel("Sektion als auch Kategorie angepasst");
		lblSektionUndKategorie.setBounds(283, 246, 287, 14);
		contentPanel.add(lblSektionUndKategorie);
		
		JLabel lblWerden = new JLabel("werden!");
		lblWerden.setBounds(283, 272, 70, 15);
		contentPanel.add(lblWerden);
		
		try {
			fillchoices(choose);
			fillchoices2();
		} catch (MuseumNotFoundException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Ändern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						if(!TablePanel.getInstance().getCheckedIds().isEmpty()){
							setExhibitIds(TablePanel.getInstance().getCheckedIds());
						} else {
							ArrayList<Long> tempId = new ArrayList<>();
							tempId.add(new Long(TablePanel.getInstance().getSelectedRowId()));
							setExhibitIds(tempId);
						}
						boolean bErrorOccurred = false;
						for (Long long1: getExhibitIds() ) {
							Exhibit exhibit = null;
							try {
								exhibit = Access.searchExhibitID(long1);
							} catch (ExhibitNotFoundException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
							}
							boolean bAdd = true;
							if(exhibit.getCurrentOutsourced()!=null)
							{
								bAdd = false;
								bErrorOccurred = true;
							}
							if(bAdd)
								toChange.add(exhibit);
						}
						
						
						if ((chckbxSection.isSelected()) && ( chckbxCategory.isSelected() == false)) {
							try {
								
								Access.massChangeSection(toChange, getSelectedSection(), MuseumMainPanel.getInstance().getMuseumId());
							} catch (SectionNotFoundException | IntegrityException | ConnectionException | MuseumNotFoundException e1 ) {
								JOptionPane.showMessageDialog(null,e1.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
							}
						}
						else if ((chckbxCategory.isSelected()) && (chckbxSection.isSelected() == false)) {
							try {
								Access.massChangeCategory(toChange, getSelectedCategory());
							} catch (CategoryNotFoundException | ConnectionException e1) {
								JOptionPane.showMessageDialog(null,e1.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
							}
						}
						else if ((chckbxSection.isSelected()) && (chckbxCategory.isSelected())){
							try {
								Access.massChangeCategory(toChange, getSelectedCategory());
								Access.massChangeSection(toChange, getSelectedSection(), MuseumMainPanel.getInstance().getMuseumId());
							} catch (CategoryNotFoundException | ConnectionException e1) {
								JOptionPane.showMessageDialog(null,e1.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
							} catch (SectionNotFoundException | IntegrityException | MuseumNotFoundException e1 ) {
								JOptionPane.showMessageDialog(null,e1.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
							}

						}
						else JOptionPane.showMessageDialog(null, "Bitte eine Checkbox waehlen!");
						
						if(bErrorOccurred)
							JOptionPane.showConfirmDialog(null,"Einige Exponate konnten nicht verschoben werden,\nda sie derzeit ausgeliehen/ausgestellt sind.","Fehler beim Verschieben",JOptionPane.WARNING_MESSAGE);
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	
	
	/**
	 * Returning the ComboBox as result
	 * 
	 * @return result
	 */
	public JComboBox<TreeNodeObject> getComboClone(){
		  result = new JComboBox<TreeNodeObject>();
		  result.setBounds(283, 142, 220, 20);
		  for(int i=0; i<MuseumMainPanel.getInstance().getComboBoxMuseum().getItemCount(); i++){
			  result.addItem(MuseumMainPanel.getInstance().getComboBoxMuseum().getItemAt(i));
		  }
		  return result;
	}
	
	/**
	 * Filling the choices with content 
	 * 
	 * @param choose
	 * @throws MuseumNotFoundException
	 */
	public void fillchoices(ArrayList<Long> choose) throws MuseumNotFoundException{
//		public void fillchoices() throws MuseumNotFoundException{		
			Exhibit ex = null;
			for (Long choose2 : choose) {
				try {
					ex = Access.searchExhibitID(choose2);
				} catch (ExhibitNotFoundException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				setMuseum_id(ex.getMuseum_id());// = museum2.getId();
			}
		}	
			
		public void fillchoices2() throws MuseumNotFoundException{	
			//Filling the category-names
			ArrayList<Category> allCategories = Access.getAllCategoriesByMuseum(MuseumMainPanel.getInstance().getMuseumId());
			for (Category i : allCategories) {
				TreeNodeObject o = new TreeNodeObject(i.getName());
				o.setCategoryId(i.getId());
				comboBoxCategory.addItem(o);
			}
		}
		
		@Override
		public void dispose(){
			super.dispose();
			MuseumMainPanel.getInstance().setMuseumId(museum_id);
			MuseumMainPanel.getInstance().setMuseumTreeNode(museumNode);
			try {
				MuseumMainPanel.getInstance().setMuseumToSelect(Access.searchMuseumID(museum_id));
			} catch (MuseumNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
			MuseumMainPanel.getInstance().refreshComboBox();
		}

		/**
		 * Getting the selected Category-ID
		 * 
		 * @return resultid
		 */

		public long getSelectedCategory() throws CategoryNotFoundException
		{		
			long resultId;
			
			TreeNodeObject cat;
			cat = (TreeNodeObject) comboBoxCategory.getSelectedItem();
			resultId = cat.getCategoryId();
			
//			TreeNodeObject o;
//			o = (TreeNodeObject)comboBoxCategory.getSelectedItem();
////			resultId = ((TreeNodeObject) comboBoxCategory.getSelectedItem()).getCategoryId();
//			resultId = o.getCategoryId();
			return resultId;
		}
		
		/**
		 * Getting the selected Section-ID
		 * 
		 * @return sectionId
		 */
		public long getSelectedSection()
		{
			Long sectionId = null;
			
				try {
					sectionId = paneltreeSection.getSectionId();
				} catch (SectionNotFoundException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				if (sectionId == null) 
					sectionId = 0L;
		
			return sectionId;
		}
		
		

		/**
		 * 
		 * @return ExhibitIds
		 */
		public ArrayList<Long> getExhibitIds() {
			return ExhibitIds;
		}

		/**
		 * 
		 * @param exhibitIds
		 */
		public void setExhibitIds(ArrayList<Long> exhibitIds) {
			ExhibitIds = exhibitIds;
		}

		/**
		 * 
		 * @return SectionNames
		 */
		public ArrayList<String> getSectionNames() {
			return SectionNames;
		}

		/**
		 * 
		 * @param sectionNames
		 */
		public void setSectionNames(ArrayList<String> sectionNames) {
			SectionNames = sectionNames;
		}
		
		/**
		 * 
		 * @return museum_id
		 */
		public long getMuseum_id() {
			return museum_id;
		}

		/**
		 * 
		 * @param museum_id
		 */
		public void setMuseum_id(long museum_id) {
			this.museum_id = museum_id;
		}

		/**
		 * 
		 * @return tablePanel
		 */
		public TablePanel getTablePanel() {
			return tablePanel;
		}

		/**
		 * 
		 * @param tablePanel
		 */
		public void setTablePanel(TablePanel tablePanel) {
			this.tablePanel = tablePanel;
		}

		/**
		 *  
		 * @return section_id
		 */
		public Long getSection_id() {
			return section_id;
		}

		/**
		 * 
		 * @param section_id
		 */
		public void setSection_id(Long section_id) {
			this.section_id = section_id;
		}
}
