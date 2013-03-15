package de.museum.berleburg.userInterface.dialogs;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;
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
public class CreateContact extends JDialog {

	private static CreateContact instance;
	private JTextField textFieldForename;
	private JTextField textFieldName;
	private JTextField textFieldFone;
	private JTextField textFieldEmail;
	private JTextField textFieldFax;
	private JTextArea textAreaDescription;
	private String forename, name, fone, fax, email, description;

	private JComboBox<String> comboBoxRole;
	private ComboBoxModel<String> comboBoxModelRole;

	private JComboBox<String> comboBoxAddress;
	private ComboBoxModel<String> comboBoxModelAddress;

	private Vector<Long> vectorRoleLong = new Vector<Long>();
	private Vector<String> vectorRoleString = new Vector<String>();
	private ArrayList<Role> listRole;

	private Vector<Long> vectorAddressLong = new Vector<Long>();
	private Vector<String> vectorAddressString = new Vector<String>();
	private ArrayList<Address> listAddress;

	/**
	 * Create the dialog.
	 */
	public CreateContact() {
		instance = this;
		setModal(true);
		setTitle("Neue Kontaktperson");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 640, 480);
		getContentPane().setLayout(
				new MigLayout("", "[][160px:n,grow][120px:n,grow][]", "[][][][][][][][228px][33px]"));
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
			getContentPane().add(lblName, "cell 0 1,alignx right");
		}
		{
			textFieldName = new JTextField();
			getContentPane().add(textFieldName, "cell 1 1 2 1,growx");
			textFieldName.setColumns(10);
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
			textFieldFax = new JTextField();
			getContentPane().add(textFieldFax, "cell 1 3 2 1,growx");
			textFieldFax.setColumns(10);
		}
		{
			JLabel lblEmail = new JLabel("E-Mail(*)");
			getContentPane().add(lblEmail, "cell 0 4,alignx trailing");
		}
		{
			JLabel lblNewLabel = new JLabel("Fax(*)");
			getContentPane().add(lblNewLabel, "cell 0 3,alignx right");
		}
		{
			textFieldEmail = new JTextField();
			getContentPane().add(textFieldEmail, "cell 1 4 2 1,growx");
			textFieldEmail.setColumns(10);
		}
		{
			JLabel lblRolle = new JLabel("Rolle");
			getContentPane().add(lblRolle, "cell 0 5,alignx right");
		}

		{
			JButton btnNeueRolle = new JButton("Neue Rolle");
			btnNeueRolle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CreateRole createRole = new CreateRole();
					createRole.setVisible(true);
					
				}
			});
			{
				comboBoxRole = new JComboBox<String>();
				comboBoxModelRole = new DefaultComboBoxModel<String>(
						getVectorsRole());
				comboBoxModelRole.addListDataListener(comboBoxRole);
				comboBoxRole.setModel(comboBoxModelRole);
				getContentPane().add(comboBoxRole, "cell 1 5,growx");
			}
			getContentPane().add(btnNeueRolle, "flowx,cell 2 5,growx");
		}
		{
			JButton btnRolleBearbeiten = new JButton("Rolle bearbeiten");
			btnRolleBearbeiten.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					EditRole editRole = new EditRole(vectorRoleLong
							.get(comboBoxRole.getSelectedIndex()), false,
							CreateContact.this, true);
					editRole.setVisible(true);

				}
			});
			getContentPane().add(btnRolleBearbeiten, "cell 3 5,growx");
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
			JButton btnNeueAdresse = new JButton("Neue Adresse");
			btnNeueAdresse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CreateAddress createAddress = new CreateAddress(false);
					createAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNeueAdresse, "flowx,cell 2 6,growx");
		}
		{
			JButton btnAdresseBearbeiten = new JButton("Adresse bearbeiten");
			btnAdresseBearbeiten.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EditAddress editAddress = new EditAddress(instance,
							vectorAddressLong.get(comboBoxAddress
									.getSelectedIndex()));
					editAddress.setVisible(true);
				}
			});
			getContentPane().add(btnAdresseBearbeiten, "cell 3 6");
		}
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung(*)");
			getContentPane().add(lblBeschreibung,
					"cell 0 7,alignx trailing,aligny top");
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
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 1 8 2 1,growx,aligny top");
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				
				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();

						try {
							if(comboBoxRole.getItemCount()==0){
								JOptionPane.showMessageDialog(null,
										"Bitte erstellen sie eine Rolle!", "Keine Rolle vorhanden",
										JOptionPane.ERROR_MESSAGE);
								
							}
							else {
								Access.insertContact(name, forename, fone, email,
										vectorAddressLong.get(comboBoxAddress
												.getSelectedIndex()), description,
										fax, vectorRoleLong.get(comboBoxRole
												.getSelectedIndex()));
								InformationPanel.getInstance().setText(
										"Neue Kontaktperson angelegt");
								long roleid = vectorRoleLong.get(comboBoxRole.getSelectedIndex());
								Role role = Access.searchRoleId(roleid);
								Museum museum = Access.searchMuseumID(role.getMuseum_id());
								if(MainGUI.getDetailPanel().getLastDisplayed()!=null && MainGUI.getDetailPanel().getLastDisplayed() instanceof Museum)
								{
									if(museum.getId().equals(((Museum)MainGUI.getDetailPanel().getLastDisplayed()).getId()))
										MainGUI.getDetailPanel().setDetails(museum);	
								}
								dispose();
							}
							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
							} catch (MuseumNotFoundException e1) {
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

	}

	// Second constructor
	public CreateContact(final boolean isCreateLoan) {
		instance = this;
		setModal(true);
		setTitle("Neuer Kontakt");
		setBounds(100, 100, 640, 480);
		getContentPane().setLayout(
				new MigLayout("",
						"[::8px][][8:n:8][160px:n,grow][120px:n,grow][][]",
						"[][][][][][][grow][][228px][33px]"));
		{
			JLabel lblForename = new JLabel("Vorname");
			getContentPane().add(lblForename, "cell 1 1,alignx trailing");
		}
		{
			textFieldForename = new JTextField();
			getContentPane().add(textFieldForename, "cell 3 1 2 1,growx");
			textFieldForename.setColumns(10);
		}
		{
			JLabel lblName = new JLabel("Name");
			getContentPane().add(lblName, "cell 1 2,alignx right");
		}
		{
			textFieldName = new JTextField();
			getContentPane().add(textFieldName, "cell 3 2 2 1,growx");
			textFieldName.setColumns(10);
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
			textFieldFax = new JTextField();
			getContentPane().add(textFieldFax, "cell 3 4 2 1,growx");
			textFieldFax.setColumns(10);
		}
		{
			JLabel lblEmail = new JLabel("E-Mail(*)");
			getContentPane().add(lblEmail, "cell 1 5,alignx trailing");
		}
		{
			JLabel lblNewLabel = new JLabel("Fax(*)");
			getContentPane().add(lblNewLabel, "cell 1 4,alignx right");
		}
		{
			textFieldEmail = new JTextField();
			getContentPane().add(textFieldEmail, "cell 3 5 2 1,growx");
			textFieldEmail.setColumns(10);
		}
		{
			JLabel lblRolle = new JLabel("Rolle");
			getContentPane().add(lblRolle, "cell 1 6,alignx right");
		}

		{
			JButton btnNeueRolle = new JButton("Neue Rolle");
			btnNeueRolle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CreateRole createRole = new CreateRole();
					createRole.setVisible(true);
				}
			});
			{
				comboBoxRole = new JComboBox<String>();
				comboBoxModelRole = new DefaultComboBoxModel<String>(
						getVectorsRole());
				comboBoxModelRole.addListDataListener(comboBoxRole);
				comboBoxRole.setModel(comboBoxModelRole);
				getContentPane().add(comboBoxRole, "cell 3 6,growx");
			}
			getContentPane().add(btnNeueRolle, "flowx,cell 4 6,growx");
		}
		{
			JButton btnRolleBearbeiten = new JButton("Rolle bearbeiten");
			btnRolleBearbeiten.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					EditRole editRole = new EditRole(vectorRoleLong
							.get(comboBoxRole.getSelectedIndex()), false,
							CreateContact.this, true);
					editRole.setVisible(true);

				}
			});
			getContentPane().add(btnRolleBearbeiten, "cell 5 6,growx");
		}
		{
			JLabel lblAdresse = new JLabel("Adresse");
			getContentPane().add(lblAdresse, "cell 1 7,alignx right");
		}
		{

			comboBoxAddress = new JComboBox<String>();
			comboBoxModelAddress = new DefaultComboBoxModel<String>(
					getVectorsAddress());
			comboBoxModelAddress.addListDataListener(comboBoxAddress);
			comboBoxAddress.setModel(comboBoxModelAddress);
			comboBoxAddress.setBounds(158, 207, 320, 20);
			getContentPane().add(comboBoxAddress, "cell 3 7,growx");
		}
		{
			JButton btnNeueAdresse = new JButton("Neue Adresse");
			btnNeueAdresse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CreateAddress createAddress = new CreateAddress(false);
					createAddress.setVisible(true);
				}
			});
			getContentPane().add(btnNeueAdresse, "flowx,cell 4 7,growx");
		}
		{
			JButton btnAdresseBearbeiten = new JButton("Adresse bearbeiten");
			btnAdresseBearbeiten.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EditAddress editAddress = new EditAddress(instance,
							vectorAddressLong.get(comboBoxAddress
									.getSelectedIndex()));
					editAddress.setVisible(true);
				}
			});
			getContentPane().add(btnAdresseBearbeiten, "cell 5 7");
		}
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung (*)");
			getContentPane().add(lblBeschreibung,
					"cell 1 8,alignx trailing,aligny top");
		}
		{
			textAreaDescription = new JTextArea();
			getContentPane().add(textAreaDescription, "cell 3 8 2 1,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 4 9 2 1,growx,aligny top");
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();

						try {
							if(comboBoxRole.getItemCount()==0){
								JOptionPane.showMessageDialog(null,
										"Bitte erstellen sie eine Rolle!", "Keine Rolle vorhanden",
										JOptionPane.ERROR_MESSAGE);
								
							}
							else {
								Access.insertContact(name, forename, fone, email,
										vectorAddressLong.get(comboBoxAddress
												.getSelectedIndex()), description,
										fax, vectorRoleLong.get(comboBoxRole
												.getSelectedIndex()));

								// Detailansicht refreshen
								long roleid = vectorRoleLong.get(comboBoxRole.getSelectedIndex());
								Role role = Access.searchRoleId(roleid);
								Museum museum = Access.searchMuseumID(role.getMuseum_id());
								if(MainGUI.getDetailPanel().getLastDisplayed()!=null && MainGUI.getDetailPanel().getLastDisplayed() instanceof Museum)
								{
									if(museum.getId().equals(((Museum)MainGUI.getDetailPanel().getLastDisplayed()).getId()))
										MainGUI.getDetailPanel().setDetails(museum);	
								}

								if (isCreateLoan) {
									CreateLoan.getInstance().refreshContact();
									CreateLoan.getInstance().refreshAddress();
								} else {
									EditLoan.getInstance().refreshContact();
									EditLoan.getInstance().refreshAdress();
								}
								InformationPanel.getInstance().setText(
										"Neue Kontaktperson angelegt");
								dispose();
								
							}
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (MuseumNotFoundException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE); //There was a mistake....
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
						if(isCreateLoan)
							CreateLoan.getInstance().refreshAddress();
						else 
							EditLoan.getInstance().refreshAdress();
							
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

	}

	// End second constructor

	/**
	 * Get all values from textfields and areas
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
	 * Get all Addresses and add them to two vectors
	 * 
	 * @return
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
	 * Get all Roles and add them to two vectors
	 * 
	 * @return
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

	public static CreateContact getInstance() {
		if (instance == null) {
			instance = new CreateContact();
		}
		return instance;
	}

	void refreshVector() {
		vectorRoleLong.clear();
		vectorRoleString.clear();
		comboBoxRole
				.setModel(new DefaultComboBoxModel<String>(getVectorsRole()));

	}

	void refreshAddress() {
		vectorAddressLong.clear();
		vectorAddressString.clear();
		comboBoxAddress.setModel(new DefaultComboBoxModel<String>(
				getVectorsAddress()));

	}

}
