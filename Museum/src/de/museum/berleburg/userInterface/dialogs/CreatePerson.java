package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;

public class CreatePerson extends JDialog {

	
	/**
	 * @author Alexander Adema
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JLabel lblName;
	private JLabel lblLastname;
	private JLabel lblBeschreibung;
	private JTextField textFieldName;
	private JTextField textFieldLastName;
	private JTextField textFieldFon;
	private JLabel lblFon;
	private JTextField textFieldEmail;
	private JLabel lblFax;
	private JTextField textFieldFax;
	
	
	private String name, forename, fon, email, description, fax, museumName;
	private Long museumid, roleid;
	private JComboBox<TreeNodeObject> comboBoxRole;
	private JComboBox<TreeNodeObject> comboBoxMuseums;
	
	

	/**
	 * Create the dialog.
	 */
	public CreatePerson() {
		setModal(true);
		setTitle("Neue Kontaktperson");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 539, 557);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			lblName = new JLabel("Name");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblName, 13, SpringLayout.NORTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblName, 10, SpringLayout.WEST, contentPanel);
			contentPanel.add(lblName);
		}
		{
			lblLastname = new JLabel("Nachname");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblLastname, 18, SpringLayout.SOUTH, lblName);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblLastname, 0, SpringLayout.WEST, lblName);
			contentPanel.add(lblLastname);
		}
		{
			lblBeschreibung = new JLabel("Beschreibung");
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblBeschreibung, 0, SpringLayout.WEST, lblName);
			contentPanel.add(lblBeschreibung);
		}
		{
			lblFon = new JLabel("Telefon");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblFon, 22, SpringLayout.SOUTH, lblLastname);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblFon, 0, SpringLayout.WEST, lblName);
			contentPanel.add(lblFon);
		}
		
		JTextArea textAreaDescription = new JTextArea();
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, textAreaDescription, -12, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textAreaDescription, -18, SpringLayout.EAST, contentPanel);
		contentPanel.add(textAreaDescription);
		
		textFieldName = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.WEST, textAreaDescription, 0, SpringLayout.WEST, textFieldName);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldName, 64, SpringLayout.EAST, lblName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldName, -256, SpringLayout.EAST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldName, -3, SpringLayout.NORTH, lblName);
		contentPanel.add(textFieldName);
		textFieldName.setColumns(10);
		
		textFieldLastName = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldLastName, -2, SpringLayout.NORTH, lblLastname);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldLastName, 0, SpringLayout.WEST, textAreaDescription);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldLastName, -255, SpringLayout.EAST, contentPanel);
		contentPanel.add(textFieldLastName);
		textFieldLastName.setColumns(10);
		
		textFieldFon = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldFon, -2, SpringLayout.NORTH, lblFon);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldFon, 0, SpringLayout.WEST, textAreaDescription);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldFon, -209, SpringLayout.EAST, contentPanel);
		contentPanel.add(textFieldFon);
		textFieldFon.setColumns(10);
		
		textFieldEmail = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldEmail, -198, SpringLayout.EAST, contentPanel);
		textFieldEmail.setColumns(10);
		contentPanel.add(textFieldEmail);
		
		JLabel lblEmail = new JLabel("E-Mail");
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldEmail, 64, SpringLayout.EAST, lblEmail);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldEmail, -3, SpringLayout.NORTH, lblEmail);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblEmail, 55, SpringLayout.SOUTH, lblFon);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblEmail, 0, SpringLayout.EAST, lblName);
		contentPanel.add(lblEmail);
		{
			textFieldFax = new JTextField();
			sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldFax, 0, SpringLayout.EAST, textFieldEmail);
			textFieldFax.setColumns(10);
			contentPanel.add(textFieldFax);
		}
		{
			lblFax = new JLabel("Fax");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldFax, -2, SpringLayout.NORTH, lblFax);
			sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldFax, 80, SpringLayout.EAST, lblFax);
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblFax, 19, SpringLayout.SOUTH, lblFon);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblFax, 0, SpringLayout.WEST, lblName);
			contentPanel.add(lblFax);
		}
		
		
		JLabel lblMuseum = new JLabel("Museum");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblBeschreibung, 61, SpringLayout.SOUTH, lblMuseum);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMuseum, 20, SpringLayout.SOUTH, lblEmail);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblMuseum, 0, SpringLayout.WEST, lblName);
		contentPanel.add(lblMuseum);
		
		JLabel lblRolle = new JLabel("Rolle");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblRolle, 22, SpringLayout.SOUTH, lblMuseum);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblRolle, 0, SpringLayout.WEST, lblName);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblRolle, 0, SpringLayout.EAST, lblBeschreibung);
		contentPanel.add(lblRolle);
		
		comboBoxRole = new JComboBox<TreeNodeObject>();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textAreaDescription, 17, SpringLayout.SOUTH, comboBoxRole);
		sl_contentPanel.putConstraint(SpringLayout.WEST, comboBoxRole, 0, SpringLayout.WEST, textAreaDescription);
		sl_contentPanel.putConstraint(SpringLayout.EAST, comboBoxRole, -5, SpringLayout.EAST, textFieldFon);
		contentPanel.add(comboBoxRole);
		
		comboBoxMuseums = new JComboBox<TreeNodeObject>();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, comboBoxRole, 16, SpringLayout.SOUTH, comboBoxMuseums);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, comboBoxMuseums, -5, SpringLayout.NORTH, lblMuseum);
		sl_contentPanel.putConstraint(SpringLayout.WEST, comboBoxMuseums, 0, SpringLayout.WEST, textAreaDescription);
		sl_contentPanel.putConstraint(SpringLayout.EAST, comboBoxMuseums, -5, SpringLayout.EAST, textFieldFon);
		contentPanel.add(comboBoxMuseums);
		
		setMuseumToChoice();
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton OkButton = new JButton("Erstellen");
				OkButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
//						setMuseumName(choiceMuseum.getSelectedItem());
						getValuesForSaving();
						
							try {
								Access.insertContact(name, forename, fon, email, museumid, description, fax, roleid);
							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
							}
							catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Museum nicht gefunden", JOptionPane.ERROR_MESSAGE);
							}
							catch (InvalidArgumentsException e1) {
								JOptionPane.showMessageDialog(null, e1.getMessage(), "Falsche Werte eingetragen", JOptionPane.ERROR_MESSAGE);
							}
					
						
					}
				});
				OkButton.setActionCommand("OK");
				buttonPane.add(OkButton);
				getRootPane().setDefaultButton(OkButton);
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
	}
	
	
	/**
	 * Getting all museums and setting them to the choice
	 * 
	 * @param text
	 */
	public void setMuseumToChoice(){
		ArrayList<Museum> allmuseums = Access.getAllMuseums();
		for (Museum i : allmuseums) {
			TreeNodeObject o = new TreeNodeObject(i.getName());
			o.setMuseumId(i.getId());
			comboBoxMuseums.addItem(o);
		}
		
	}
	
	/**
	 * Getting all roles and setting them to the choice
	 * 
	 * @param text
	 */
	public void setRoleToChoice(){
		ArrayList<Role> allmuseums = Access.getAllRole();
		for (Role i : allmuseums) {
			TreeNodeObject o = new TreeNodeObject(i.getName());
			o.setRoleid(i.getId());
			comboBoxRole.addItem(o);
		}
	}
	
	/**
	 * Getting all values for saving a person
	 * 
	 * @param text
	 */
	public void getValuesForSaving(){
		
		name = getTextFieldLastName().getText();
		forename = getTextFieldName().getText();
		museumName = ((TreeNodeObject)comboBoxMuseums.getSelectedItem()).getName();
		((TreeNodeObject)comboBoxRole.getSelectedItem()).getName();
		roleid = ((TreeNodeObject)comboBoxRole.getSelectedItem()).getRoleid();
		ArrayList<de.museum.berleburg.datastorage.model.Museum> museumsidObject = null;
		
			museumsidObject = Access.searchMuseumName(museumName);
		
		for (de.museum.berleburg.datastorage.model.Museum museum : museumsidObject) {
			museumid = museum.getId();
		}
		description = getDescription();
		fon = getTextFieldFon().getText();
		fax = getTextFieldFax().getText();
		email = getTextFieldEmail().getText();
		
	}




	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @return name
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return forename
	 */
	public String getForename() {
		return forename;
	}



	/**
	 * @param forename
	 */
	public void setForename(String forename) {
		this.forename = forename;
	}



	/**
	 * @return fon
	 */
	public String getFon() {
		return fon;
	}



	/**
	 * @param fon
	 */
	public void setFon(String fon) {
		this.fon = fon;
	}



	/**
	 * @return email
	 */
	public String getEmail() {
		return email;
	}



	/**
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}



	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}



	/**
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}



	/**
	 * @return fax
	 */
	public String getFax() {
		return fax;
	}



	/**
	 * @param fax
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}



	/**
	 * @return museumid
	 */
	public Long getMuseumid() {
		return museumid;
	}



	/**
	 * @param museumid
	 */
	public void setMuseumid(Long museumid) {
		this.museumid = museumid;
	}


	/**
	 * @return textFieldName
	 */
	public JTextField getTextFieldName() {
		return textFieldName;
	}


	/**
	 * @param textFieldName
	 */
	public void setTextFieldName(JTextField textFieldName) {
		this.textFieldName = textFieldName;
	}


	/**
	 * @return textFieldLastName
	 */
	public JTextField getTextFieldLastName() {
		return textFieldLastName;
	}


	/**
	 * @param textFieldLastName
	 */
	public void setTextFieldLastName(JTextField textFieldLastName) {
		this.textFieldLastName = textFieldLastName;
	}


	/**
	 * @return textFieldFon
	 */
	public JTextField getTextFieldFon() {
		return textFieldFon;
	}


	/**
	 * @param textFieldFon
	 */
	public void setTextFieldFon(JTextField textFieldFon) {
		this.textFieldFon = textFieldFon;
	}


	/**
	 * @return textFieldEmail
	 */
	public JTextField getTextFieldEmail() {
		return textFieldEmail;
	}


	/**
	 * @param textFieldEmail
	 */
	public void setTextFieldEmail(JTextField textFieldEmail) {
		this.textFieldEmail = textFieldEmail;
	}


	/**
	 * @return textFieldFax
	 */
	public JTextField getTextFieldFax() {
		return textFieldFax;
	}


	/**
	 * @param textFieldFax
	 */
	public void setTextFieldFax(JTextField textFieldFax) {
		this.textFieldFax = textFieldFax;
	}


	/**
	 * @return museumName
	 */
	public String getMuseumName() {
		return museumName;
	}


	/**
	 * @param museumName
	 */
	public void setMuseumName(String museumName) {
		this.museumName = museumName;
	}


	/**
	 * @return roleid
	 */
	public Long getRoleid() {
		return roleid;
	}


	/**
	 * @param roleid
	 */
	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}
}
