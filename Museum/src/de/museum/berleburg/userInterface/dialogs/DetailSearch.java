package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;
import de.museum.berleburg.userInterface.panels.TreeCategoryPanel;
import de.museum.berleburg.userInterface.panels.TreeExhibitionPanel;
import de.museum.berleburg.userInterface.panels.TreeLabelPanel;
import de.museum.berleburg.userInterface.panels.TreeSectionPanel;

/**
 * DetailSearch
 * 
 * @author Timo Funke
 */
public class DetailSearch extends JDialog {


	private static final long serialVersionUID = 1L;
	
	
	private final JPanel contentPanel = new JPanel();
	private JTextField txtSearchName;
	private JLabel lblSearchfor;
	private JLabel lblInformationlabel;
	
	private Boolean includeCategorie = true; // Include chosen category standardly
	private Boolean includeSection = true; // Include chosen section standardly
	private Boolean includeLabel = true; // Include chosen label standardly
	private Boolean includeOutsourced = true; // Include chosen outsourced standardly
	private JTextField txtChosenCategory;
	private JTextField txtChosenSection;
	private JTextField txtChosenLabel;
	private JTextField txtChosenOutsourced;
	private JLabel lblInclude;
	private JLabel lblExclude;
	
	private JRadioButton rdbtnExcludeSection;
	private JRadioButton rdbtnIncludeSection;
	private JRadioButton rdbtnExcludeCategory;
	private JRadioButton rdbtnIncludeCategory;
	private JRadioButton rdbtnExcludeLabel;
	private JRadioButton rdbtnIncludeLabel;
	private JRadioButton rdbtnExcludeOutsourced;
	private JRadioButton rdbtnIncludeOutsourced;
	private JButton btnAddSectionFilter;
	private JButton btnAddCategoryFilter;
	private JButton btnAddLabelFilter;
	private JButton btnAddOutsourcedFilter;
	
	private Point dialogPosition;
	
	private TreeCategoryPanel categoryTree;
	private TreeSectionPanel sectionTree;
	private TreeLabelPanel labelTree;
	private TreeExhibitionPanel outsourcedTree;
	
	private long museumId;
	private boolean onlyInMuseum;
		
	private ArrayList<Long> proCategoryIDList = new ArrayList<Long>();
	private ArrayList<Long> contraCategoryIDList = new ArrayList<Long>();
	private ArrayList<Long> proSectionIDList = new ArrayList<Long>();
	private ArrayList<Long> contraSectionIDList = new ArrayList<Long>();
	private ArrayList<Long> proLabelIDList = new ArrayList<Long>();
	private ArrayList<Long> contraLabelIDList = new ArrayList<Long>();
	private ArrayList<Long> proOutsourcedIDList = new ArrayList<Long>();
	private ArrayList<Long> contraOutsourcedIDList = new ArrayList<Long>();
	
	
	private String searchText;
	int categoryPosition = 423;
	int sectionPosition = 423;
	int labelPosition = 423;
	int outsourcedPosition = 423;
	int addToSizeAndPosition = 30;
	int textfieldHeight = 30;
	int countCat = 1;
	int countSec = 1;
	int countLab = 1;
	int countOut = 1;
	int dialogWidth = 970;
	int dialogHeight = 500;
	int maximumFilter = 15;


	/**
	 * Create the dialog.
	 * 
	 * @param oldSearchText
	 * @param p (Position of the Button)
	 */
	public DetailSearch(String oldSearchText, Point p) {
		setBackground(Color.WHITE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(DetailSearch.class.getResource("/de/museum/berleburg/userInterface/panels/Search.png")));
		getContentPane().setIgnoreRepaint(true);
		dialogPosition = p;
		setModal(true);

//		setUndecorated(true); //hide frame
		setTitle("erweiterte Suche");

		setSize(dialogWidth, dialogHeight);


		//Dialog Position over the DetailSearchButton
		dialogPosition.x-= (dialogWidth - 140); 
		setLocation(dialogPosition);
		dialogPosition.x+= (dialogWidth - 140); // Give default position back
		

		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		lblSearchfor = new JLabel("Suchen nach Exponat:");
		contentPanel.add(lblSearchfor);
		lblSearchfor.setBounds(80, 14, 180, 20); 

		txtSearchName = new JTextField();
		txtSearchName.setToolTipText("Name des Exponats nach dem gesucht werden soll");
		contentPanel.add(txtSearchName);
		txtSearchName.setBounds(250, 11, 215, textfieldHeight); 
		txtSearchName.setColumns(15);
		txtSearchName.setText(oldSearchText);
			

		// Add ...filter buttons
		btnAddSectionFilter = new JButton("Sektionsfilter hinzufügen");
		btnAddSectionFilter
				.setToolTipText("Fügt den gewählten Sektionsfilter hinzu");
		btnAddSectionFilter.setBounds(10, 392, 215, textfieldHeight);
		contentPanel.add(btnAddSectionFilter);
		btnAddSectionFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblInformationlabel.setText("");
				if (countSec <= maximumFilter) {
					addSectionFilter();
				}
				repaint();
			}
		});
		
		
		btnAddCategoryFilter = new JButton("Kategoriefilter hinzufügen");
		btnAddCategoryFilter
				.setToolTipText("Fügt den gewählten Kategoriefilter hinzu");
		btnAddCategoryFilter.setBounds(250, 392, 215, textfieldHeight);
		contentPanel.add(btnAddCategoryFilter);
		btnAddCategoryFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblInformationlabel.setText("");
				if (countCat <= maximumFilter) {
					addCategoryFilter();
				}
				repaint();
			}
		});

		
		btnAddLabelFilter = new JButton("Labelfilter hinzufügen");
		btnAddLabelFilter
				.setToolTipText("Fügt den gewählten Labelfilter hinzu");
		btnAddLabelFilter.setBounds(490, 392, 215, textfieldHeight);
		contentPanel.add(btnAddLabelFilter);
		btnAddLabelFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblInformationlabel.setText("");
				if (countLab <= maximumFilter) {
					addLabelFilter();
				}
				repaint();
			}
		});

		btnAddOutsourcedFilter = new JButton("Ausstellungsfilter hinzufügen");
		btnAddOutsourcedFilter
				.setToolTipText("Fügt den gewählten Ausstellungs-/Leihgabenfilter hinzu");
		btnAddOutsourcedFilter.setBounds(730, 392, 215, textfieldHeight);
		contentPanel.add(btnAddOutsourcedFilter);
		btnAddOutsourcedFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblInformationlabel.setText("");
				if (countOut <= maximumFilter) {
					addOutsourcedFilter();
				}
				repaint();
			}
		});
		
		
		
		
		// Tree Panels
		sectionTree = new TreeSectionPanel(false);
		sectionTree.setToolTipText("Sektionen wählen");
		sectionTree.setBounds(10, 80, 215, 301);
		contentPanel.add(sectionTree);
		
		categoryTree = new TreeCategoryPanel(false);
		categoryTree.setToolTipText("Kategorie wählen");
		categoryTree.setBounds(250, 80, 215, 301);
		contentPanel.add(categoryTree);
		
		labelTree = new TreeLabelPanel(false);
		labelTree.setToolTipText("Labels wählen");
		labelTree.setBounds(490, 80, 215, 301);
		contentPanel.add(labelTree);

		outsourcedTree = new TreeExhibitionPanel(false);
		outsourcedTree.setToolTipText("Ausstellung oder Leihgaben wählen");
		outsourcedTree.setBounds(730, 80, 215, 301);
		contentPanel.add(outsourcedTree);

		// RadioButtons for SectionFilter
		rdbtnIncludeSection = new JRadioButton(
				"Einschließen");
		rdbtnIncludeSection.setBackground(Color.WHITE);
		rdbtnIncludeSection.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnIncludeSection
				.setToolTipText("Soll die gewählte Sektion bei bei Suche eingeschlossen werden?");
		rdbtnIncludeSection.setBounds(10, 50, 108, 23);
		rdbtnIncludeSection.setSelected(true);
		rdbtnIncludeSection.setActionCommand(getTitle());
		contentPanel.add(rdbtnIncludeSection);

		rdbtnIncludeSection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeSection(true);
			}
		});

		rdbtnExcludeSection = new JRadioButton(
				"Ausschließen");
		rdbtnExcludeSection.setBackground(Color.WHITE);
		rdbtnExcludeSection.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnExcludeSection
				.setToolTipText("Soll die gewählte Sektion bei bei Suche ausgeschlossen werden?");
		rdbtnExcludeSection.setHorizontalAlignment(SwingConstants.RIGHT);
		rdbtnExcludeSection.setBounds(116, 50, 109, 23);
		contentPanel.add(rdbtnExcludeSection);

		rdbtnExcludeSection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeSection(false);
			}
		});

		ButtonGroup sectionGroup = new ButtonGroup();
		sectionGroup.add(rdbtnIncludeSection);
		sectionGroup.add(rdbtnExcludeSection);
		
		
		// RadioButtons for CategoryFilter
		rdbtnIncludeCategory = new JRadioButton(
				"Einschließen");
		rdbtnIncludeCategory.setBackground(Color.WHITE);
		rdbtnIncludeCategory.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnIncludeCategory
				.setToolTipText("Soll die gewählte Kategorie bei bei Suche eingeschlossen werden?");
		rdbtnIncludeCategory.setBounds(250, 50, 108, 23);
		rdbtnIncludeCategory.setSelected(true);
		rdbtnIncludeCategory.setActionCommand(getTitle());
		contentPanel.add(rdbtnIncludeCategory);
		
		rdbtnIncludeCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeCategorie(true);
			}
		});

		rdbtnExcludeCategory = new JRadioButton(
				"Ausschließen");
		rdbtnExcludeCategory.setBackground(Color.WHITE);
		rdbtnExcludeCategory.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnExcludeCategory
				.setToolTipText("Soll die gewählte Kategorie bei bei Suche ausgeschlossen werden?");
		rdbtnExcludeCategory.setHorizontalAlignment(SwingConstants.RIGHT);
		rdbtnExcludeCategory.setBounds(356, 50, 109, 23);
		contentPanel.add(rdbtnExcludeCategory);

		rdbtnExcludeCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeCategorie(false);
			}
		});

		ButtonGroup categoryGroup = new ButtonGroup();
		categoryGroup.add(rdbtnIncludeCategory);
		categoryGroup.add(rdbtnExcludeCategory);
		
		
		// RadioButtons for LabelFilter
		rdbtnIncludeLabel = new JRadioButton(
				"Einschließen");
		rdbtnIncludeLabel.setBackground(Color.WHITE);
		rdbtnIncludeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnIncludeLabel
				.setToolTipText("Soll das gewählte Label bei bei Suche eingeschlossen werden?");
		rdbtnIncludeLabel.setBounds(490, 50, 108, 23);
		rdbtnIncludeLabel.setSelected(true);
		rdbtnIncludeLabel.setActionCommand(getTitle());
		contentPanel.add(rdbtnIncludeLabel);
		
		rdbtnIncludeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeLabel(true);
			}
		});

		rdbtnExcludeLabel = new JRadioButton(
				"Ausschließen");
		rdbtnExcludeLabel.setBackground(Color.WHITE);
		rdbtnExcludeLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnExcludeLabel
				.setToolTipText("Soll das gewählte Label bei bei Suche ausgeschlossen werden?");
		rdbtnExcludeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		rdbtnExcludeLabel.setBounds(596, 50, 109, 23);
		contentPanel.add(rdbtnExcludeLabel);

		rdbtnExcludeLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeLabel(false);
			}
		});

		ButtonGroup LabelGroup = new ButtonGroup();
		LabelGroup.add(rdbtnIncludeLabel);
		LabelGroup.add(rdbtnExcludeLabel);

		
		// RadioButtons for OutsourcedFilter
		rdbtnIncludeOutsourced = new JRadioButton(
				"Einschließen");
		rdbtnIncludeOutsourced.setBackground(Color.WHITE);
		rdbtnIncludeOutsourced.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnIncludeOutsourced
				.setToolTipText("Soll die gewählte Ausstellung/Leihgabe bei bei Suche eingeschlossen werden?");
		rdbtnIncludeOutsourced.setBounds(730, 50, 108, 23);
		rdbtnIncludeOutsourced.setSelected(true);
		rdbtnIncludeOutsourced.setActionCommand(getTitle());
		contentPanel.add(rdbtnIncludeOutsourced);
		
		rdbtnIncludeOutsourced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeOutsourced(true);
			}
		});

		rdbtnExcludeOutsourced = new JRadioButton(
				"Ausschließen");
		rdbtnExcludeOutsourced.setBackground(Color.WHITE);
		rdbtnExcludeOutsourced.setFont(new Font("Dialog", Font.PLAIN, 12));
		rdbtnExcludeOutsourced
				.setToolTipText("Soll die gewählte Ausstellung/Leihgabe bei bei Suche ausgeschlossen werden?");
		rdbtnExcludeOutsourced.setHorizontalAlignment(SwingConstants.RIGHT);
		rdbtnExcludeOutsourced.setBounds(836, 50, 109, 23);
		contentPanel.add(rdbtnExcludeOutsourced);

		rdbtnExcludeOutsourced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setIncludeOutsourced(false);
			}
		});

		ButtonGroup OutsourcedGroup = new ButtonGroup();
		OutsourcedGroup.add(rdbtnIncludeOutsourced);
		OutsourcedGroup.add(rdbtnExcludeOutsourced);
		
		


		// Button Pane on South Panel
		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.WHITE);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton btnStartSearch = new JButton("Suche starten");
		btnStartSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				setSearchText(txtSearchName.getText());
				lblInformationlabel.setText("Suche gestartet!");
				try {
					setMuseumId(MuseumMainPanel.getInstance().getMuseumId());
				} catch (NullPointerException e1) {
					//lblInformationlabel.setText("Bitte Museum wählen");
					JOptionPane.showMessageDialog(null, "Bitte ein Museum wählen", "Fehler", JOptionPane.ERROR_MESSAGE);
				}
				
				TablePanel.getInstance().updateTable(getMuseumId(), getSearchText(), proCategoryIDList, contraCategoryIDList, proSectionIDList, contraSectionIDList, proLabelIDList, contraLabelIDList, proOutsourcedIDList, contraOutsourcedIDList, false, false);  
				
			}
		});
		
		lblInformationlabel = new JLabel();
		lblInformationlabel.setPreferredSize(new Dimension (570,23));
		buttonPane.add(lblInformationlabel);
		btnStartSearch.setToolTipText("Startet die Suche mit den angegebenen Kriterien");
		buttonPane.add(btnStartSearch);
		getRootPane().setDefaultButton(btnStartSearch);

		JButton btnClearFilter = new JButton("Filter löschen");
		btnClearFilter.setToolTipText("Alle Filter werden gelöscht");
		btnClearFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				dispose();
				DetailSearch dialog = new DetailSearch(txtSearchName.getText(), dialogPosition);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

			}
		});
		buttonPane.add(btnClearFilter);
		
		
		
		
		// Cancel Button
		JButton btnCancel = new JButton("Abbrechen");
		buttonPane.add(btnCancel);
		btnCancel.setToolTipText("Abbrechen");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnCancel.setActionCommand("Cancel");

	}
	/**
	 * adds a new Sectionfilter
	 */
	public void addSectionFilter() {

		try {
			Long secID = null;
			Section sec = null;
			
			
			
//			try {
				secID = sectionTree.getSectionId();
				sec = Access.searchSectionID(secID);
//			} catch (NullPointerException e) {
				
//			}
			
			
			if (proSectionIDList.contains(secID) || contraSectionIDList.contains(secID)) {
				lblInformationlabel.setText("Sektion "+ sec.getName() + " wurde bereits gewählt");
				
			} else {
				
				
				// TextField that gives out the chosen section and a label if it is included or not
				txtChosenSection = new JTextField();
				txtChosenSection.setBounds(50, sectionPosition, 175, textfieldHeight);
				contentPanel.add(txtChosenSection);
				txtChosenSection.setColumns(10);
				txtChosenSection.setEditable(false);
				txtChosenSection.setBackground(Color.WHITE);
				
				if (getIncludeSection()) {
					
					proSectionIDList.add(secID);
					txtChosenSection.setText(sec.getName());
					lblInclude = new JLabel();
					lblInclude.setBounds(10, sectionPosition, 40, textfieldHeight);
					lblInclude.setText("mit");
					contentPanel.add(lblInclude);
					
				} else {
					contraSectionIDList.add(secID);
					txtChosenSection.setText(sec.getName());
					lblExclude = new JLabel();
					lblExclude.setBounds(10, sectionPosition, 40, textfieldHeight);
					lblExclude.setText("ohne");
					contentPanel.add(lblExclude);
				}
				
				sectionPosition += addToSizeAndPosition;
				
				rdbtnExcludeSection.setEnabled(false);
				rdbtnIncludeSection.setEnabled(false);
				
				if (countSec >= countCat && countSec >= countLab && countSec >= countOut)
					setSize(dialogWidth, dialogHeight += addToSizeAndPosition);
				countSec++;
				
			}
			
			
			
			
		}  catch (SectionNotFoundException e1) {
			//lblInformationlabel.setText(e1.getMessage());
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException e1) {
			
			if (proSectionIDList.isEmpty() && contraSectionIDList.isEmpty()) {
								
				txtChosenSection = new JTextField();
				txtChosenSection.setBounds(50, sectionPosition, 175, textfieldHeight);
				contentPanel.add(txtChosenSection);
				txtChosenSection.setColumns(10);
				txtChosenSection.setEditable(false);
				txtChosenSection.setBackground(Color.WHITE);
				
				if (getIncludeSection()) {
					
					try {
						for (Section step : Access.getAllSubSectionsFromMuseum(MuseumMainPanel.getInstance().getMuseumId())) {
							contraSectionIDList.add(step.getId());
						}
					} catch (MuseumNotFoundException e) {
						//lblInformationlabel.setText(e.getMessage());
						JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}
					
					txtChosenSection.setText("keiner Sektion zugeordnet");
					lblInclude = new JLabel();
					lblInclude.setBounds(10, sectionPosition, 40, textfieldHeight);
					lblInclude.setText("mit");
					contentPanel.add(lblInclude);
					
				} else {
					try {
						for (Section step : Access.getAllSubSectionsFromMuseum(MuseumMainPanel.getInstance().getMuseumId())) {
							proSectionIDList.add(step.getId());
						}
					} catch (MuseumNotFoundException e) {
						//lblInformationlabel.setText(e.getMessage());
						JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}
					
					txtChosenSection.setText("keiner Sektion zugeordnet");
					lblExclude = new JLabel();
					lblExclude.setBounds(10, sectionPosition, 40, textfieldHeight);
					lblExclude.setText("ohne");
					contentPanel.add(lblExclude);
				}
				
				sectionPosition += addToSizeAndPosition;
				
				btnAddSectionFilter.setEnabled(false);
				btnAddSectionFilter.setToolTipText("Neben \"keiner Sektion zugeordnet\" können keine weiteren Sektionsfilter gewählt werden");
				rdbtnExcludeSection.setEnabled(false);
				rdbtnIncludeSection.setEnabled(false);
				
				if (countSec >= countCat && countSec >= countLab && countSec >= countOut)
					setSize(dialogWidth, dialogHeight += addToSizeAndPosition);
				countSec++;
				
			} else {
				lblInformationlabel.setText("\"keiner Sektion zugeordnet\" kann nicht hinzugefügt werden!");
				
			
			}
			
		} catch (Exception e1) {
			//lblInformationlabel.setText("Es wurde keine Sektion gewählt!");
			JOptionPane.showMessageDialog(null, "Es wurde keine Sektion gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
		}

	}
	/**
	 * adds a new Categoryfilter
	 */
	public void addCategoryFilter() {

		try {
			Long catID = null;
			Category cat = null;

			catID = categoryTree.getCategoryId();
			cat = Access.searchCategoryID(catID);

			if (proCategoryIDList.contains(catID)
					|| contraCategoryIDList.contains(catID)) {
				lblInformationlabel.setText("Kategorie " + cat.getName() + " wurde bereits gewählt");

			} else {

				// TextField that gives out the chosen category and a label if
				// it is included or not
				txtChosenCategory = new JTextField();
				txtChosenCategory.setBounds(290, categoryPosition, 175, textfieldHeight);
				contentPanel.add(txtChosenCategory);
				txtChosenCategory.setColumns(10);
				txtChosenCategory.setEditable(false);
				txtChosenCategory.setBackground(Color.WHITE);

				if (getIncludeCategorie()) {
					proCategoryIDList.add(catID);
					txtChosenCategory.setText(cat.getName());
					lblInclude = new JLabel();
					lblInclude.setBounds(250, categoryPosition, 40, textfieldHeight);
					lblInclude.setText("mit");
					contentPanel.add(lblInclude);
				} else {
					contraCategoryIDList.add(catID);
					txtChosenCategory.setText(cat.getName());
					lblExclude = new JLabel();
					lblExclude.setBounds(250, categoryPosition, 40, textfieldHeight);
					lblExclude.setText("ohne");
					contentPanel.add(lblExclude);
				}

				categoryPosition += addToSizeAndPosition;

				rdbtnExcludeCategory.setEnabled(false);
				rdbtnIncludeCategory.setEnabled(false);
				
				if (countCat >= countSec && countCat >= countLab
						&& countCat >= countOut)
					setSize(dialogWidth, dialogHeight += addToSizeAndPosition);
				countCat++;

			}

		} catch (CategoryNotFoundException e) {
			//lblInformationlabel.setText(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			//lblInformationlabel.setText("Es wurde keine Kategorie gewählt!");
			JOptionPane.showMessageDialog(null, "Es wurde keine Kategorie gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	/**
	 * adds a new Labelfilter
	 */
	public void addLabelFilter() {

		try {
			Long labID = null;
			Label lab = null;

			labID = labelTree.getLabelId();
			lab = Access.searchLabelById(labID);

			if (proLabelIDList.contains(labID)
					|| contraLabelIDList.contains(labID)) {
				lblInformationlabel.setText("Label " + lab.getName() + " wurde bereits gewählt");

			} else {

				// TextField that gives out the chosen label and a label if it
				// is included or not
				txtChosenLabel = new JTextField();
				txtChosenLabel.setBounds(530, labelPosition, 175,
						textfieldHeight);
				contentPanel.add(txtChosenLabel);
				txtChosenLabel.setColumns(10);
				txtChosenLabel.setEditable(false);
				txtChosenLabel.setBackground(Color.WHITE);

				if (getIncludeLabel()) {
					proLabelIDList.add(labID);
					txtChosenLabel.setText(lab.getName());
					lblInclude = new JLabel();
					lblInclude.setBounds(490, labelPosition, 40, textfieldHeight);
					lblInclude.setText("mit");
					contentPanel.add(lblInclude);
				} else {
					contraLabelIDList.add(labID);
					txtChosenLabel.setText(lab.getName());
					lblExclude = new JLabel();
					lblExclude.setBounds(490, labelPosition, 40, textfieldHeight);
					lblExclude.setText("ohne");
					contentPanel.add(lblExclude);
				}

				labelPosition += addToSizeAndPosition;
				
//				rdbtnExcludeLabel.setEnabled(false);
//				rdbtnIncludeLabel.setEnabled(false);
				
				if (countLab >= countSec && countLab >= countCat
						&& countLab >= countOut)
					setSize(dialogWidth, dialogHeight += addToSizeAndPosition);
				countLab++;

			}

		} catch (LabelNotFoundException e) {
			//lblInformationlabel.setText(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			//lblInformationlabel.setText("Es wurde kein Label gewählt!");
			JOptionPane.showMessageDialog(null, "Es wurde kein Label gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * adds a new Outsourcedfilter
	 */
	public void addOutsourcedFilter() {

		try {
			Long outID = null;
			Outsourced out = null;

			try {
				outID = outsourcedTree.getExhibitionId();
				out = Access.searchExhibitonID(outID);
			} catch (NullPointerException e) {
					outID = outsourcedTree.getLoanId();
					out = Access.searchLoanID(outID);				
			}
			
			

			if (proOutsourcedIDList.contains(outID)
					|| contraOutsourcedIDList.contains(outID)) {
				lblInformationlabel.setText("Ausstellung/Leihgabe " + out.getName() + " wurde bereits gewählt");

			} else {

				// TextField that gives out the chosen outsourced and a label if
				// it is included or not
				txtChosenOutsourced = new JTextField();
				txtChosenOutsourced.setBounds(770, outsourcedPosition, 175,
						textfieldHeight);
				contentPanel.add(txtChosenOutsourced);
				txtChosenOutsourced.setColumns(10);
				txtChosenOutsourced.setEditable(false);
				txtChosenOutsourced.setBackground(Color.WHITE);

				if (getIncludeOutsourced()) {
					proOutsourcedIDList.add(outID);
					txtChosenOutsourced.setText(out.getName());
					lblInclude = new JLabel();
					lblInclude.setBounds(730, outsourcedPosition, 40, textfieldHeight);
					lblInclude.setText("mit");
					contentPanel.add(lblInclude);
				} else {
					contraOutsourcedIDList.add(outID);
					txtChosenOutsourced.setText(out.getName());
					lblExclude = new JLabel();
					lblExclude.setBounds(730, outsourcedPosition, 40, textfieldHeight);
					lblExclude.setText("ohne");
					contentPanel.add(lblExclude);
				}
				
				outsourcedPosition += addToSizeAndPosition;
				
				rdbtnExcludeOutsourced.setEnabled(false);
				rdbtnIncludeOutsourced.setEnabled(false);
				
				if (countOut >= countSec && countOut >= countCat
						&& countOut >= countLab)
					setSize(dialogWidth, dialogHeight += addToSizeAndPosition);
				countOut++;

			}

		} catch (OutsourcedNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Es wurde keine Ausstellung/Leihgabe gewählt!", "Fehler", JOptionPane.ERROR_MESSAGE);
		}

	}

	
	
	/**
	 * @return the museumId
	 */
	public long getMuseumId() {
		return museumId;
	}
	/**
	 * @param museumId the museumId to set
	 */
	public void setMuseumId(long museumId) {
		this.museumId = museumId;
	}
	/**
	 * @return the includeCategorie
	 */
	public Boolean getIncludeCategorie() {
		return includeCategorie;
	}
	/**
	 * @param includeCategorie the includeCategorie to set
	 */
	public void setIncludeCategorie(Boolean includeCategorie) {
		this.includeCategorie = includeCategorie;
	}
	/**
	 * @return the includeSection
	 */
	public Boolean getIncludeSection() {
		return includeSection;
	}
	/**
	 * @param includeSection the includeSection to set
	 */
	public void setIncludeSection(Boolean includeSection) {
		this.includeSection = includeSection;
	}
	/**
	 * @return the includeLabel
	 */
	public Boolean getIncludeLabel() {
		return includeLabel;
	}
	/**
	 * @param includeLabel the includeLabel to set
	 */
	public void setIncludeLabel(Boolean includeLabel) {
		this.includeLabel = includeLabel;
	}
	/**
	 * @return the includeOutsourced
	 */
	public Boolean getIncludeOutsourced() {
		return includeOutsourced;
	}
	/**
	 * @param includeOutsourced the includeOutsourced to set
	 */
	public void setIncludeOutsourced(Boolean includeOutsourced) {
		this.includeOutsourced = includeOutsourced;
	}
	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}
	/**
	 * @param searchText the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	/**
	 * @return the onlyInMuseum
	 */
	public boolean isOnlyInMuseum() {
		return onlyInMuseum;
	}
	/**
	 * @param onlyInMuseum the onlyInMuseum to set
	 */
	public void setOnlyInMuseum(boolean onlyInMuseum) {
		this.onlyInMuseum = onlyInMuseum;
	}
	
}
