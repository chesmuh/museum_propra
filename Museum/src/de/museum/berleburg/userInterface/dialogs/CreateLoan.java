package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

/**
 * 
 * @author Way Dat To
 * 
 */

@SuppressWarnings("serial")
public class CreateLoan extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldStartDay;
	private JTextField textFieldEndDay;
	private JTextField textFieldStartMonth;
	private JTextField textFieldEndMonth;
	private JTextField textFieldStartYear;
	private JTextField textFieldEndYear;
	private JTextArea textAreaDescription;
	private CreateAddress adressContact;
	private CreateContact contactDialog;
	private static CreateLoan createLoan;
	private boolean addToLoan;

	JComboBox<String> comboBoxContact;
	private ComboBoxModel<String> comboBoxModelContact;

	JComboBox<String> comboBoxAddress;
	private ComboBoxModel<String> comboBoxModelAddress;

	private String startDate, endDate, description;

	Date dateStart;
	Date dateEnd;
	boolean validStart;
	boolean validEnd;

	DateFormat formatter = null;
	String name;

	Vector<Long> vectorMuseumLong = new Vector<Long>();
	Vector<String> vectorMuseumString = new Vector<String>();
	ArrayList<Museum> listMuseum;

	Vector<Long> vectorContactLong = new Vector<Long>();
	Vector<String> vectorContactString = new Vector<String>();
	ArrayList<Contact> listContact;

	Vector<Long> vectorAddressLong = new Vector<Long>();
	Vector<String> vectorAddressString = new Vector<String>();
	ArrayList<Address> listAddress;

	private JTextField textFieldName;

	/**
	 * Create the dialog.
	 */
	public CreateLoan(boolean b) {
		addToLoan = b;
		setModal(true);
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainGUI.class
								.getResource("/de/museum/berleburg/userInterface/logo.png")));
		createLoan = this;
		formatter = new SimpleDateFormat("ddMMyyyy");

		setTitle("Neue Leihgabe");
		setBounds(100, 100, 640, 360);
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);

		contentPanel.setLayout(new MigLayout("",
				"[][40:n:40][40:n:40][80px:n][][]", "[][][][][][grow][]"));
		{
			{
				JLabel lblNameDerLeihgabe = new JLabel("Name der Leihgabe");
				contentPanel
						.add(lblNameDerLeihgabe, "cell 0 0,alignx trailing");
			}
			{
				textFieldName = new JTextField();
				contentPanel.add(textFieldName, "cell 1 0 3 1,growx");
				textFieldName.setColumns(10);
			}
			{
				JButton btnNewButton_2 = new JButton("Adresse bearbeiten");
				btnNewButton_2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						EditAddress editAddress = new EditAddress(
								vectorAddressLong.get(comboBoxAddress
										.getSelectedIndex()), false,
								CreateLoan.this, true);
						editAddress.setVisible(true);
					}
				});
				{
					JButton btnNewButton_1 = new JButton("Kontakt bearbeiten");
					btnNewButton_1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							EditContact editContact = new EditContact(
									vectorContactLong.get(comboBoxContact
											.getSelectedIndex()), false,
									CreateLoan.this, true);
							editContact.setVisible(true);
						}
					});
					contentPanel.add(btnNewButton_1, "cell 5 3,growx");
				}
				contentPanel.add(btnNewButton_2, "cell 5 4,growx");
			}

		}
		{
			comboBoxContact = new JComboBox<String>();
			comboBoxModelContact = new DefaultComboBoxModel<String>(
					getVectorsContact());
			comboBoxModelContact.addListDataListener(comboBoxContact);
			comboBoxContact.setModel(comboBoxModelContact);
			comboBoxContact.setBounds(158, 207, 320, 20);
			contentPanel.add(comboBoxContact, "cell 1 3 3 1,growx");
		}
		{

		}
		{
			comboBoxAddress = new JComboBox<String>();
			comboBoxModelAddress = new DefaultComboBoxModel<String>(
					getVectorsAddress());
			comboBoxModelAddress.addListDataListener(comboBoxAddress);
			comboBoxAddress.setModel(comboBoxModelAddress);
			comboBoxAddress.setBounds(158, 207, 320, 20);
			contentPanel.add(comboBoxAddress, "cell 1 4 3 1,growx");
		}

		{
			JLabel lblStartDatum = new JLabel("Start Datum");
			contentPanel.add(lblStartDatum, "cell 0 1,alignx left");
		}
		{
			textFieldStartDay = new JTextField();
			contentPanel.add(textFieldStartDay, "cell 1 1,growx");
			textFieldStartDay.setColumns(10);
		}
		{
			textFieldStartMonth = new JTextField();
			contentPanel.add(textFieldStartMonth, "cell 2 1,growx");
			textFieldStartMonth.setColumns(10);
		}
		{
			textFieldStartYear = new JTextField();
			contentPanel.add(textFieldStartYear, "cell 3 1,growx");
			textFieldStartYear.setColumns(10);
		}
		{
			JLabel lblTtmmjjjj = new JLabel("TT.MM.JJJJ");
			contentPanel.add(lblTtmmjjjj, "cell 4 1");
		}
		{
			JLabel lblEndDatum = new JLabel("End Datum(*)");
			contentPanel.add(lblEndDatum, "cell 0 2,alignx left");
		}
		{
			textFieldEndDay = new JTextField();
			contentPanel.add(textFieldEndDay, "cell 1 2,growx");
			textFieldEndDay.setColumns(10);
		}
		{
			textFieldEndMonth = new JTextField();
			contentPanel.add(textFieldEndMonth, "cell 2 2,growx");
			textFieldEndMonth.setColumns(10);
		}
		{
			textFieldEndYear = new JTextField();
			contentPanel.add(textFieldEndYear, "cell 3 2,growx");
			textFieldEndYear.setColumns(10);
		}
		{
			JLabel lblTtmmjjjj_1 = new JLabel("TT.MM.JJJJ");
			contentPanel.add(lblTtmmjjjj_1, "cell 4 2");
		}
		{
			JLabel lblKontaktPerson = new JLabel("Kontaktperson");
			contentPanel.add(lblKontaktPerson, "cell 0 3,alignx left");
		}

		{
			JButton btnNewButton = new JButton("Neue Kontaktperson");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					contactDialog = new CreateContact(true);
					contactDialog.setVisible(true);
				}
			});
			contentPanel.add(btnNewButton, "cell 4 3,growx");
		}
		{
			JLabel lblKontaktadresse = new JLabel("Kontaktadresse");
			contentPanel.add(lblKontaktadresse, "cell 0 4,alignx left");
		}

		{
			JButton btnNeueKontaktadresse = new JButton("Neue Kontaktadresse");
			btnNeueKontaktadresse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					adressContact = new CreateAddress(createLoan);
					adressContact.setVisible(true);
				}
			});
			contentPanel.add(btnNeueKontaktadresse, "cell 4 4,growx");
		}

		// {
		// JButton btnNeuesMuseum = new JButton("Neues Museum");
		// btnNeuesMuseum.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// CreateMuseum createMuseum = new CreateMuseum();
		// createMuseum.setVisible(true);
		// }
		// });
		// contentPanel.add(btnNeuesMuseum, "cell 4 5,growx");
		// }
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung(*)");
			contentPanel.add(lblBeschreibung, "cell 0 5,aligny top");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 1 5 5 1,grow");
			{
				textAreaDescription = new JTextArea();
				scrollPane.setViewportView(textAreaDescription);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			contentPanel.add(buttonPane, "cell 0 6 6 1,alignx right");
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);

				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						initialize();

						try {
							try {
								dateStart = (Date) formatter.parse(startDate);
							} catch (ParseException e1) {
							}

							if (endDate.equals("")) {
								dateEnd = null;
							} else {

								try {
									dateEnd = (Date) formatter.parse(endDate);
								} catch (ParseException e1) {
									 JOptionPane
									 .showMessageDialog(
									 null,
									 e1.getMessage(),
									 "Fehler",
									 JOptionPane.ERROR_MESSAGE);
								}
							}
							if (comboBoxContact.getItemCount() == 0) {
								JOptionPane
										.showMessageDialog(
												null,
												"Bitte erstellen Sie eine Kontaktperson.",
												"Fehler",
												JOptionPane.ERROR_MESSAGE);
							}
							int result = JOptionPane.YES_OPTION;
							if(dateEnd!=null && dateEnd.before(new Date())) {
								result = JOptionPane.showConfirmDialog(null, "Das Enddatum befindet sich vor dem heutigen Tag. Möchten Sie die Ausstellung trotzdem speichern?", "Enddatum", JOptionPane.YES_NO_OPTION);
							}
							if(JOptionPane.YES_OPTION == result) {
								Access.insertLoan(
										name,
										description,
										dateStart,
										dateEnd,
										vectorAddressLong.get(comboBoxAddress
												.getSelectedIndex()),
												vectorContactLong.get(comboBoxContact
														.getSelectedIndex()),
														MuseumMainPanel.getInstance().getMuseumId(),
														validStart, validEnd);
								MainGUI.getInformationPanel().setText(
										"Neuer Verleih " + name
										+ " wurde erfolgreich erstellt.");
								// TreeExhibitionPanel.getInstance().refreshTreeWithoutTable();
								TreeMainPanel.getInstance()
								.getTreeExhibitionPanel()
								.refreshTreeWithoutTable();
								if (addToLoan) {
									AddToLoan.getInstance().refresh();
								}
								dispose();
							}

						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
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
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public Vector<String> getVectorsMuseum() {

		listMuseum = Access.getAllMuseums();

		for (Museum actual : listMuseum) {
			vectorMuseumLong.add(actual.getId());
			vectorMuseumString.add(actual.getName());
		}
		return vectorMuseumString;
	}

	public Vector<String> getVectorsContact() {

		try {
			listContact = Access.searchContactByMuseumId(TreeMainPanel
					.getInstance().getMuseumId());
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}
		for (Contact actual : listContact) {
			vectorContactLong.add(actual.getId());
			vectorContactString.add(actual.getName() + ", "
					+ actual.getForename());
		}
		return vectorContactString;
	}

	public Vector<String> getVectorsAddress() {

		listAddress = Access.getAllAddress();

		for (Address actual : listAddress) {
			vectorAddressLong.add(actual.getId());
			vectorAddressString.add(actual.getStreet() + " "
					+ actual.getHousenumber() + ", " + actual.getTown());
		}
		return vectorAddressString;
	}

	public void initialize() {
		description = textAreaDescription.getText();
		description = textAreaDescription.getText();
		startDate = textFieldStartDay.getText() + textFieldStartMonth.getText()
				+ textFieldStartYear.getText();
		endDate = textFieldEndDay.getText() + textFieldEndMonth.getText()
				+ textFieldEndYear.getText();
		
			try {
				validStart = Access.checkDate(textFieldStartDay.getText(),
						textFieldStartMonth.getText(),
						textFieldStartYear.getText());
			} catch (InvalidArgumentsException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		if (endDate.equals(""))
			validEnd = true;
		else
			try {
				validEnd = Access
						.checkDate(textFieldEndDay.getText(),
								textFieldEndMonth.getText(),
								textFieldEndYear.getText());
			} catch (InvalidArgumentsException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
						JOptionPane.ERROR_MESSAGE);
			}
		name = textFieldName.getText();
	}

	void refreshContact() {
		vectorContactLong.clear();
		vectorContactString.clear();
		comboBoxContact.setModel(new DefaultComboBoxModel<String>(
				getVectorsContact()));

	}

	void refreshAddress() {
		vectorAddressLong.clear();
		vectorAddressString.clear();
		comboBoxAddress.setModel(new DefaultComboBoxModel<String>(
				getVectorsAddress()));

	}

	public static CreateLoan getInstance() {
		if (createLoan == null) {
			createLoan = new CreateLoan(false);
		}
		return createLoan;

	}
}
