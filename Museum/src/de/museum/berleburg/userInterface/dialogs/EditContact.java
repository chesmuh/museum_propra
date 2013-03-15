package de.museum.berleburg.userInterface.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ContactNotFoundException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

/**
 * @author Way Dat To
 * 
 */
@SuppressWarnings("serial")
public class EditContact extends JDialog {

	private static EditContact instance;
	private boolean showContact;
	private JTextField textFieldForename;
	private JTextField textFieldName;
	private JTextField textFieldFone;
	private JTextField textFieldEmail;
	private JTextField textFieldFax;
	private JTextArea textAreaDescription;
	private String forename, name, fone, fax, email, description;
	private Contact contact;

	private JComboBox<String> comboBoxRole;
	private ComboBoxModel<String> comboBoxModelRole;

	private JComboBox<String> comboBoxAddress;
	private ComboBoxModel<String> comboBoxModelAddress;

	private Vector<Long> vectorRoleLong = new Vector<Long>();
	private Vector<String> vectorRoleString = new Vector<String>();
	private ArrayList<Role> listRole;

	Vector<Long> vectorAddressLong = new Vector<Long>();
	Vector<String> vectorAddressString = new Vector<String>();
	ArrayList<Address> listAddress;
	
	/** @author Christian Landel */
	private int min_width = 800;
	/** @author Christian Landel */
	private int min_height = 400;

	@Override
	public void dispose()
	{
		MainGUI.getDetailPanel().refresh();
		super.dispose();
	}
	
	/**
	 * Create the dialog.
	 * 
	 * @wbp.parser.constructor
	 */
	
	public EditContact(long l, boolean sC, JDialog dialog, boolean modal) {
		super(dialog, modal);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setMinimumSize(new Dimension(min_width,min_height));
		setSize(min_width+150, min_height+150);
		showContact = sC;
		instance = this;
		idToContact(l);

		setTitle("Kontakt bearbeiten");
//		setBounds(100, 100, 600, 360);
		setModal(true);
		getContentPane().setLayout(
				new MigLayout("", "[][180:n,grow][]", "[][][][][][][][grow][33px]"));
		{
			JLabel lblForename = new JLabel("Vorname");
			getContentPane().add(lblForename, "cell 0 0,alignx trailing");
		}
		{
			textFieldForename = new JTextField();
			getContentPane().add(textFieldForename, "cell 1 0 2 1,growx");
			textFieldForename.setColumns(10);
		}
		{
			JLabel lblName = new JLabel("Name");
			getContentPane().add(lblName, "cell 0 1,alignx trailing");
		}
		{
			textFieldName = new JTextField();
			getContentPane().add(textFieldName, "cell 1 1 2 1,growx");
			textFieldName.setColumns(10);
		}
		{
			JLabel lblRollenname = new JLabel("Rolle");
			getContentPane().add(lblRollenname, "cell 0 5,alignx right");
		}
		{
			JLabel lblTelefon = new JLabel("Telefon(*)");
			getContentPane().add(lblTelefon, "cell 0 2,alignx trailing");
		}
		{
			textFieldFone = new JTextField();
			getContentPane().add(textFieldFone, "cell 1 2 2 1,growx");
			textFieldFone.setColumns(10);
		}
		{
			JLabel lblEmail = new JLabel("E-Mail(*)");
			getContentPane().add(lblEmail, "cell 0 4,alignx trailing");
		}
		{
			textFieldEmail = new JTextField();
			getContentPane().add(textFieldEmail, "cell 1 4 2 1,growx");
			textFieldEmail.setColumns(10);
		}
		{
			JLabel lblNewLabel = new JLabel("Fax(*)");
			getContentPane().add(lblNewLabel, "cell 0 3,alignx right");
		}
		{
			textFieldFax = new JTextField();
			getContentPane().add(textFieldFax, "cell 1 3 2 1,growx");
			textFieldFax.setColumns(10);
		}
		{
			comboBoxRole = new JComboBox<String>();
			comboBoxModelRole = new DefaultComboBoxModel<String>(
					getVectorsRole());
			comboBoxModelRole.addListDataListener(comboBoxRole);
			comboBoxRole.setModel(comboBoxModelRole);
			getContentPane().add(comboBoxRole, "cell 1 5,growx");
		}
		{
			JButton btnNeueRolle = new JButton("Neue Rolle");
			btnNeueRolle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CreateRole createRole = new CreateRole(instance);
					createRole.setVisible(true);
				}
			});
			getContentPane().add(btnNeueRolle, "flowx,cell 2 5,grow");
		}
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung(*)");
			getContentPane().add(lblBeschreibung, "cell 0 7,alignx trailing,aligny top");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, "cell 1 7 2 1,grow");
			{
				textAreaDescription = new JTextArea();
				scrollPane.setViewportView(textAreaDescription);
			}
		}
		{
			JButton btnNewButton = new JButton("Rolle bearbeiten");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					EditRole editRole = new EditRole(vectorRoleLong
							.get(comboBoxRole.getSelectedIndex()), instance);
					editRole.setVisible(true);

				}
			});
			getContentPane().add(btnNewButton, "cell 2 5,alignx center,growy");
		}
		{
			JLabel lblAdresse = new JLabel("Adresse");
			getContentPane().add(lblAdresse, "cell 0 6,alignx right");
		}
		{
			comboBoxAddress = new JComboBox<String>();
			comboBoxModelAddress = new DefaultComboBoxModel<String>(
					getVectorsAddress());
			comboBoxModelAddress.addListDataListener(comboBoxAddress);
			comboBoxAddress.setModel(comboBoxModelAddress);
			comboBoxAddress.setBounds(158, 207, 320, 20);
			getContentPane().add(comboBoxAddress, "cell 1 6,growx");
		}
		{
			JButton btnNewButton_1 = new JButton("Neue Adresse");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CreateAddress createAddress = new CreateAddress(true);
					createAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNewButton_1, "flowx,cell 2 6,grow");
		}
		{
			JButton btnNewButton_2 = new JButton("Adresse bearbeiten");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EditAddress editAddress = new EditAddress(instance, vectorAddressLong.get(comboBoxAddress.getSelectedIndex()));
					editAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNewButton_2, "cell 2 6,alignx center,growy");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 2 8,growx,aligny top");
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				
				JButton okButton = new JButton("Änderungen speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();

						
						try {
							Access.changeContact(contact.getId(), name,
									forename, fone, email, vectorAddressLong.get(comboBoxAddress.getSelectedIndex()), description, fax, 
									                                            vectorRoleLong.get(comboBoxRole.getSelectedIndex()));
							InformationPanel.getInstance().setText(
									"Änderungen erfolgreich gespeichert!");
							if (showContact) {
								ShowContacts.getInstance().updateTable();
							}
							else{
								CreateLoan.getInstance().refreshContact();
							}
							dispose();

						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (ContactNotFoundException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						}

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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
		
		setValues();
	}

	// Second constructor

	public EditContact(long l, final CreateLoan cl) {
		super();
		setMinimumSize(new Dimension(min_width,min_height));
		setSize(min_width+150, min_height+150);

		instance = this;
		idToContact(l);

		setTitle("Kontakt bearbeiten");
//		setBounds(100, 100, 600, 300);
		setModal(true);
		getContentPane().setLayout(
				new MigLayout("", "[][][8:n:8][180:n,grow][]",
						"[][][][][][][][grow][228px][33px]"));
		{
			JLabel lblForeName = new JLabel("Vorname");
			getContentPane().add(lblForeName, "cell 1 1,alignx trailing");
		}
		{
			textFieldForename = new JTextField();
			getContentPane().add(textFieldForename, "cell 3 1 2 1,growx");
			textFieldForename.setColumns(10);
		}
		{
			JLabel lblName = new JLabel("Name");
			getContentPane().add(lblName, "cell 1 2,alignx trailing");
		}
		{
			textFieldName = new JTextField();
			getContentPane().add(textFieldName, "cell 3 2 2 1,growx");
			textFieldName.setColumns(10);
		}
		{
			JLabel lblRollenname = new JLabel("Rolle");
			getContentPane().add(lblRollenname, "cell 1 6,alignx right");
		}
		{
			JLabel lblTelefon = new JLabel("Telefon(*)");
			getContentPane().add(lblTelefon, "cell 1 3,alignx trailing");
		}
		{
			textFieldFone = new JTextField();
			getContentPane().add(textFieldFone, "cell 3 3 2 1,growx");
			textFieldFone.setColumns(10);
		}
		{
			JLabel lblEmail = new JLabel("E-Mail(*)");
			getContentPane().add(lblEmail, "cell 1 5,alignx trailing");
		}
		{
			textFieldEmail = new JTextField();
			getContentPane().add(textFieldEmail, "cell 3 5 2 1,growx");
			textFieldEmail.setColumns(10);
		}
		{
			JLabel lblNewLabel = new JLabel("Fax(*)");
			getContentPane().add(lblNewLabel, "cell 1 4,alignx right");
		}
		{
			textFieldFax = new JTextField();
			getContentPane().add(textFieldFax, "cell 3 4 2 1,growx");
			textFieldFax.setColumns(10);
		}
		{
			comboBoxRole = new JComboBox<String>();
			comboBoxModelRole = new DefaultComboBoxModel<String>(
					getVectorsRole());
			comboBoxModelRole.addListDataListener(comboBoxRole);
			comboBoxRole.setModel(comboBoxModelRole);
			getContentPane().add(comboBoxRole, "cell 3 6,growx");
		}
		{
			JButton btnNeueRolle = new JButton("Neue Rolle");
			btnNeueRolle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CreateRole createRole = new CreateRole(instance);
					createRole.setVisible(true);
				}
			});
			getContentPane().add(btnNeueRolle, "flowx,cell 4 6");
		}
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung (*)");
			getContentPane().add(lblBeschreibung, "cell 1 7,alignx trailing");
		}
		{
			textAreaDescription = new JTextArea();
			getContentPane().add(textAreaDescription, "cell 3 7 2 2,grow");
		}
		{
			JButton btnNewButton = new JButton("Rolle bearbeiten");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					EditRole editRole = new EditRole(vectorRoleLong
							.get(comboBoxRole.getSelectedIndex()), instance);
					editRole.setVisible(true);

				}
			});
			getContentPane().add(btnNewButton, "cell 4 6");
		}

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 4 9,growx,aligny top");
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				
				JButton okButton = new JButton("Änderungen speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();

			
						try {
							Access.changeContact(contact.getId(), name,
									forename, fone, email, vectorAddressLong.get(comboBoxAddress.getSelectedIndex()), description, fax,vectorRoleLong.get(comboBoxRole.getSelectedIndex()));
							InformationPanel.getInstance().setText(
									"Neue Kontaktperson angelegt");
							cl.refreshContact();
							dispose();
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (ContactNotFoundException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} 

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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
		{
			JButton btnNewButton_1 = new JButton("Neue Adresse");
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CreateAddress createAddress = new CreateAddress();
					createAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNewButton_1, "flowx,cell 4 7");
		}
		{
			JButton btnNewButton_2 = new JButton("Adresse bearbeiten");
			btnNewButton_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					EditAddress editAddress = new EditAddress();
//					edtiAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNewButton_2, "cell 4 7");
		}
		setValues();
	}

	// End second constructor

	/**
	 * Get all data from textfield
	 */
	public void initialize() {
		forename = textFieldForename.getText();
		name = textFieldName.getText();
		fone = textFieldFone.getText();
		email = textFieldEmail.getText();
		fax = textFieldFax.getText();
		description = textAreaDescription.getText();

	}

	/**
	 * Gets the contact to edit by ID
	 * @param long id l
	 */
	public void idToContact(long l) {
		try {
			contact=Access.searchContactID(l);
		} catch (ContactNotFoundException e) {
			  JOptionPane.showMessageDialog(null,
			  e.getMessage(), "Fehler",
			  JOptionPane.ERROR_MESSAGE);
		}
//		ArrayList<Contact> cl = Access.getAllContact();
//		for (Contact actual : cl) {
//			if (actual.getId().equals(l)) {
//				contact = actual;
//			}
//		}

	}
	
	/**
	 * @return Vector<String> of addresses
	 */
	public Vector<String> getVectorsAddress() {

		listAddress = Access.getAllAddress();

		for (Address actual : listAddress) {
			vectorAddressLong.add(actual.getId());
			vectorAddressString.add(actual.getStreet() + " "
					+ actual.getHousenumber() + ", " + actual.getTown());
		}
		return vectorAddressString;
	}

	/**
	 * Gets all data from contact, add to textfield etc.
	 */
	public void setValues() {
		textFieldName.setText(contact.getName());
		textFieldForename.setText(contact.getForename());
		textFieldFone.setText(contact.getFon());
		textAreaDescription.setText(contact.getDescription());
		textFieldFax.setText(contact.getFax());
		textFieldEmail.setText(contact.getEmail());

		ListIterator<Long> itc = vectorRoleLong.listIterator();
		int temp = 0;
		while (itc.hasNext()) {
			Long actual = itc.next();
			temp++;
			if (actual!=null && actual.equals(contact.getRoleId())) {

				comboBoxRole.setSelectedIndex(temp - 1);

			}

		}
		ListIterator<Long> ita=vectorAddressLong.listIterator();
		temp=0;
		while(ita.hasNext())
		{
			Long actual=ita.next();
			temp++;
			if (actual.equals(contact.getAddress_id())){
				comboBoxAddress.setSelectedIndex(temp-1);	
			}

		}

	}

	/**
	 * @return Vector<String> of all roles
	 */
	public Vector<String> getVectorsRole() {
		try {
			listRole = Access.searchRoleByMuseumId(TreeMainPanel.getInstance().getMuseumId());
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null,
					e.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
		for (Role actual : listRole) {

			vectorRoleLong.add(actual.getId());
			vectorRoleString.add(actual.getName());
		}
		return vectorRoleString;
	}

	 public static EditContact getInstance(){
	 return instance;
	 }

	/**
	 * 
	 */
	void refreshVector() {
		vectorRoleLong.clear();
		vectorRoleString.clear();
		comboBoxRole
				.setModel(new DefaultComboBoxModel<String>(getVectorsRole()));

	}
	/**
	 * 
	 */
	void refreshAddress() {
		vectorAddressLong.clear();
		vectorAddressString.clear();
		comboBoxAddress.setModel(new DefaultComboBoxModel<String>(
				getVectorsAddress()));

	}

}
