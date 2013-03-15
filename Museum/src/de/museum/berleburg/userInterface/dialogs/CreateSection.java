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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;
import de.museum.berleburg.userInterface.panels.TreeSectionPanel;



public class CreateSection extends JDialog {
	
	
	/**
	 * Create the CreateSectionJDialog.
	 * 
	 * @author Alexander Adema
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textFieldNameSection;
	private JTextArea textAreaSectionDescription;
	private TreeSectionPanel treeSectionPanel;
	
	String nameSection, sectionDescription;

	
	private Long section_id;
	private long museum_id;




	/**
	 * Create the dialog.
	 */
	public CreateSection() {
		setModal(true);
		setTitle("Neue Sektion");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 480, 593);
		getContentPane().setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(8, 514, 446, 30);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton btnCreateSection = new JButton("Sektion anlegen");
				btnCreateSection.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						getValuesOfSection();
						
						
						try {
							Access.insertSection(nameSection, sectionDescription, section_id, museum_id);
							dispose();
							TreeMainPanel.getInstance().refreshTree();
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Falsche Werte", JOptionPane.ERROR_MESSAGE);
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} catch (MuseumNotFoundException e3) {
							JOptionPane.showMessageDialog(null, e3.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						}
	
					}
				});
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				
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
		lblObersektionAuswhlen.setBounds(8, 49, 143, 14);
		getContentPane().add(lblObersektionAuswhlen);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(159, 324, 288, 160);
		getContentPane().add(scrollPane);
		
		textAreaSectionDescription = new JTextArea();
		scrollPane.setViewportView(textAreaSectionDescription);
		
		JLabel lblDescriptionSection = new JLabel("Beschreibung(*)");
		lblDescriptionSection.setFont(new Font("Arial", Font.BOLD, 13));
		lblDescriptionSection.setBounds(8, 324, 143, 30);
		getContentPane().add(lblDescriptionSection);
		
		Panel panel = new Panel();
		panel.setBounds(159, 49, 288, 253);
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
		
		
	}
	
	
	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- Methods -------------------------------------------- **/
	/**  --------------------------------------------------------------------------------------- **/
	
	
	
	/**
	 * All variables will be get for savin a new section
	 * 
	 * @param text
	 */
	public void getValuesOfSection(){
		
		nameSection = getTextFieldNameSection().getText();
		sectionDescription = getTextAreaSectionDescription().getText();
		try {
				setSection_id(treeSectionPanel.getSectionId());
				if (getSection_id() == null) {
					setSection_id((long) 0);
				}
		} catch (SectionNotFoundException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), "Sektion nicht gefunden", JOptionPane.ERROR_MESSAGE);

		} 
		try {
			setMuseum_id(treeSectionPanel.getMuseumId());
		} catch (MuseumNotFoundException e) {
			InformationPanel.getInstance().setText(e.getMessage());
			JOptionPane.showMessageDialog(null, e.getMessage(), "Museum nicht gefunden", JOptionPane.ERROR_MESSAGE);
		} 
		
		
	}


	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- getter & setter ------------------------------------ **/
	/**  --------------------------------------------------------------------------------------- **/
	
	/**
	 * 
	 * @return textFieldNameSection
	 */
	public JTextField getTextFieldNameSection() {
		return textFieldNameSection;
	}


	/**
	 * 
	 * @param textFieldNameSection
	 */
	public void setTextFieldNameSection(JTextField textFieldNameSection) {
		this.textFieldNameSection = textFieldNameSection;
	}


	/**
	 * 
	 * @return textAreaSectionDescription
	 */
	public JTextArea getTextAreaSectionDescription() {
		return textAreaSectionDescription;
	}


	/**
	 * 
	 * @param textAreaSectionDescription
	 */
	public void setTextAreaSectionDescription(JTextArea textAreaSectionDescription) {
		this.textAreaSectionDescription = textAreaSectionDescription;
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
	
	
	
	
	
}
