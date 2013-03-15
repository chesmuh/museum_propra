package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;
import de.museum.berleburg.userInterface.panels.TreeSectionPanel;



public class EditSection extends JDialog {
	
	
	/**
	 * Create the EditSectionJDialog.
	 * 
	 * @author Way Dat To
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldNameSection;
	private JTextArea textAreaSectionDescription;
	private TreeSectionPanel treeSectionPanel;
	private long id;
	private Section section;
	
	String nameSection, sectionDescription;
 //	Museum nameMuseum;
	
	private Long section_id;
	private long museum_id;
//	Section nameParentSection;


	@Override
	public void dispose()
	{
		MainGUI.getDetailPanel().refresh();
		super.dispose();
	}

	/**
	 * Create the dialog.
	 */
	public EditSection(long toEdit) {
		id=toEdit;
		try {
			section=Access.searchSectionID(id);
		} catch (SectionNotFoundException e2) {
			JOptionPane.showMessageDialog(null,
					e2.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
		setModal(true);
		setTitle("Sektion bearbeiten");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 10, 480, 667);
		getContentPane().setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(23, 601, 439, 30);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton btnCreateSection = new JButton("Sektion ändern");
				btnCreateSection.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						
						
//						if (getSection_id() == 0) {
//							setSection_id((long) 0);
//						}
						getValuesOfSection();
						try {
							Access.changeSection(id, nameSection, sectionDescription, section_id, museum_id);
							TreeMainPanel.getInstance().refreshTree();
							InformationPanel.getInstance().setText("Änderungen erfolgreich gespeichert!");
							Section s = Access.searchSectionID(id);
							MainGUI.getDetailPanel().setDetails(s);
							dispose();
							
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						}
					}

				});
				btnCreateSection.setActionCommand("OK");
				buttonPane.add(btnCreateSection);
				getRootPane().setDefaultButton(btnCreateSection);
			}
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		textFieldNameSection = new JTextField();
		textFieldNameSection.setBounds(159, 11, 197, 20);
		getContentPane().add(textFieldNameSection);
		textFieldNameSection.setColumns(10);
		
		JLabel lblNameOfSection = new JLabel("Name der Sektion");
		lblNameOfSection.setFont(new Font("Arial", Font.BOLD, 13));
		lblNameOfSection.setBounds(10, 14, 114, 14);
		getContentPane().add(lblNameOfSection);
		
		JLabel lblObersektionAuswhlen = new JLabel("Museum / Obersektion");
		lblObersektionAuswhlen.setFont(new Font("Arial", Font.BOLD, 11));
		lblObersektionAuswhlen.setBounds(10, 48, 143, 14);
		getContentPane().add(lblObersektionAuswhlen);
		
		textAreaSectionDescription = new JTextArea();
		textAreaSectionDescription.setBounds(159, 361, 303, 234);
		getContentPane().add(textAreaSectionDescription);
		
		JLabel lblDescriptionSection = new JLabel("Beschreibung");
		lblDescriptionSection.setFont(new Font("Arial", Font.BOLD, 13));
		lblDescriptionSection.setBounds(10, 366, 143, 30);
		getContentPane().add(lblDescriptionSection);
		
		Panel panel = new Panel();
		panel.setBounds(159, 74, 301, 281);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		
		treeSectionPanel = new TreeSectionPanel(false);
		treeSectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		treeSectionPanel.setPreferredSize(new Dimension(480, 480));
		treeSectionPanel.setMinimumSize(new Dimension(240, 300));
		treeSectionPanel.setMaximumSize(new Dimension(480, 32767));
		treeSectionPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		treeSectionPanel.setSelection(TreeMainPanel.getInstance().getTreeSectionPanel().getTree());
		
		
		panel.add(treeSectionPanel);
				
		JLabel lblChoose = new JLabel("auswählen");
		lblChoose.setFont(new Font("Arial", Font.BOLD, 11));
		lblChoose.setBounds(8, 74, 143, 14);
		getContentPane().add(lblChoose);
		
		final JComboBox<TreeNodeObject> museumBox = MuseumMainPanel.getInstance().getComboClone();
		museumBox.setSelectedIndex(MuseumMainPanel.getInstance().getComboBoxMuseum().getSelectedIndex());
		museumBox.setBounds(159, 43, 301, 24);
		getContentPane().add(museumBox);
		museumBox.addActionListener (new ActionListener () {
	        public void actionPerformed(ActionEvent e) {
	        	TreeNodeObject o = (TreeNodeObject) museumBox.getSelectedItem();
        		if(o==null)return;
        		setMuseum_id(o.getMuseumId());
        		Long localMuseumId = MuseumMainPanel.getInstance().getMuseumId();
        		MuseumMainPanel.getInstance().setMuseumId(o.getMuseumId());
        		treeSectionPanel.refreshTreeWithoutTable();
        		MuseumMainPanel.getInstance().setMuseumId(localMuseumId);
	        }
		});
		
		setValues();
		TreeNodeObject o = (TreeNodeObject) museumBox.getSelectedItem();
		if(o==null)return;
		setMuseum_id(o.getMuseumId());
		
	}
	
	
	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- Methods -------------------------------------------- **/
	/**  --------------------------------------------------------------------------------------- **/
	
	
	
	/**
	 * All variables will be get for savin a new section
	 */
	public void getValuesOfSection(){
		
		nameSection = getTextFieldNameSection().getText();
		sectionDescription = getTextAreaSectionDescription().getText();
		try {
			setSection_id(treeSectionPanel.getSectionId());
			try {
				checkSectionId(id, section_id!=null?section_id:0);
			} catch (SectionIdException e) {
				section_id = section.getParent_id();
			}
			if (getSection_id()==null||getSection_id().equals(-1)) {
				setSection_id((long) 0);
			}
		} catch (SectionNotFoundException e1) {
			//InformationPanel.getInstance().setText(e1.getMessage());
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		
//		nameParentSection = userInterface.SectionChoose.getSelectedObjectSection();  //Sectionsobjekt wird übergeben
		
//		section_id = nameParentSection.getID();
		
//		nameMuseum = userInterface.SectionChoose.getSelectedObjectMuseum(); //Museumobjekt wird übergeben
		
//		museum_id = nameMuseum.getID();
		
	}


	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- getter & setter ------------------------------------ **/
	/**  --------------------------------------------------------------------------------------- **/
	
	public JTextField getTextFieldNameSection() {
		return textFieldNameSection;
	}


	public void setTextFieldNameSection(JTextField textFieldNameSection) {
		this.textFieldNameSection = textFieldNameSection;
	}


	public JTextArea getTextAreaSectionDescription() {
		return textAreaSectionDescription;
	}


	public void setTextAreaSectionDescription(JTextArea textAreaSectionDescription) {
		this.textAreaSectionDescription = textAreaSectionDescription;
	}

	public Long getSection_id() {
		return section_id;
	}


	public void setSection_id(Long section_id) {
		this.section_id = section_id;
	}


	public long getMuseum_id() {
		return museum_id;
	}


	public void setMuseum_id(long museum_id) {
		this.museum_id = museum_id;
	}
	
	private void setValues(){
	textFieldNameSection.setText(section.getName());
	textAreaSectionDescription.setText(section.getDescription());
	treeSectionPanel.setSelection(TreeMainPanel.getInstance()
			.getTreeSectionPanel().getTree());
	
		
	}
	
	public static void checkSectionId(long mySection, long parentSection)
			throws SectionIdException {
		if (mySection != parentSection) {

		} else {
			throw new SectionIdException();
		}

	}
}

@SuppressWarnings("serial")
class SectionIdException extends Exception {
	public SectionIdException() {
		super("Ausgewählte Sektion ist ungültig. Bitte wählen Sie eine andere Sektion.");
	}
}
