package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

/**
 * 
 * @author Way Dat To
 * 
 */

@SuppressWarnings("serial")
public class EditLoan extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldStartDay;
	private JTextField textFieldEndDay;
	private JTextField textFieldStartMonth;
	private JTextField textFieldEndMonth;
	private JTextField textFieldStartYear;
	private JTextField textFieldEndYear;
	private JTextArea textAreaDescription;

	private JButton okButton;

	private JButton btnNewButton;
	private JButton btnNeueKontaktadresse;

	private CreateAddress adressContact;
	private CreateContact contactDialog;
	private Outsourced loan;
	private static EditLoan instance;
	@SuppressWarnings("unused")
	private ComboBoxModel<String> comboBoxModelMuseum;

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
	
	/** @author Christian Landel */
	private int min_width = 500;
	/** @author Christian Landel */
	private int min_height = 400;

	@Override
	public void dispose() {
		MainGUI.getDetailPanel().refresh();
		super.dispose();
	}

	/**
	 * Create the dialog.
	 */
	public EditLoan(final long id) {
		super();
		setMinimumSize(new Dimension(min_width,min_height));
		setSize(min_width+150, min_height+150);
		setModal(true);
		setTitle("Leihgabe bearbeiten");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
//		setBounds(100, 100, 723, 510);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		instance = this;
		try {
			loan = Access.getOutsourcedByID(id);
		} catch (OutsourcedNotFoundException e2) {
			JOptionPane.showMessageDialog(null,
					e2.getMessage(), "Fehler",
					JOptionPane.ERROR_MESSAGE);
		}

		contentPanel.setLayout(new MigLayout("", "[][40:n:40,grow][40:n:40,grow][grow,fill][140:n:140,grow]", "[][][][][][grow][]"));

		{
			comboBoxModelMuseum = new DefaultComboBoxModel<String>(
					getVectorsMuseum());
			{
				JLabel lblNameDerLeihgabe = new JLabel("Name der Leihgabe");
				contentPanel
						.add(lblNameDerLeihgabe, "cell 0 0,alignx trailing");
			}
			{
				textFieldName = new JTextField();
				contentPanel.add(textFieldName, "cell 1 0 4 1,growx");
				textFieldName.setColumns(10);
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
			JLabel lblStartDatum = new JLabel("Startdatum");
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
			JLabel lblEndDatum = new JLabel("Enddatum *");
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
			btnNewButton = new JButton("Neue Kontaktperson");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					contactDialog = new CreateContact(false);
					contactDialog.setVisible(true);
				}
			});
			btnNewButton.setMaximumSize(new Dimension(140, 23));
			contentPanel.add(btnNewButton, "cell 4 3,growx");
		}
		{
			JLabel lblKontaktadresse = new JLabel("Kontaktadresse");
			contentPanel.add(lblKontaktadresse, "cell 0 4,alignx left");
		}

		{
			btnNeueKontaktadresse = new JButton("Neue Kontaktadresse");
			btnNeueKontaktadresse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					adressContact = new CreateAddress();
					adressContact.setVisible(true);
				}
			});
			btnNeueKontaktadresse.setMaximumSize(new Dimension(140, 23));
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
		// btnNeuesMuseum.setMaximumSize(new Dimension(140, 23));
		// contentPanel.add(btnNeuesMuseum, "cell 4 6,growx");
		// }
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung *");
			contentPanel.add(lblBeschreibung, "cell 0 5,aligny top");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 1 5 4 1,grow");
			{
				textAreaDescription = new JTextArea();
				scrollPane.setViewportView(textAreaDescription);
			}
		}
		{
			JLabel lblOptional = new JLabel("* Optional");
			contentPanel.add(lblOptional, "cell 0 6");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Änderung speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();

						formatter = new SimpleDateFormat("ddMMyyyy");

						try {
							dateStart = formatter.parse(startDate);
						} catch (ParseException e1) {
						}

						if (endDate.equals("")) {
							dateEnd = null;
						} else {

							try {
								dateEnd = formatter.parse(endDate);
							} catch (ParseException e1) {
							}

						}
						try {
							Access.changeOutsourced(id, name, description,
									dateStart, dateEnd, vectorAddressLong
											.get(comboBoxAddress
													.getSelectedIndex()),
									vectorContactLong.get(comboBoxContact
											.getSelectedIndex()), validStart,
									validEnd);
							MainGUI.getInformationPanel()
									.setText(
											"Änderungen wurden erfolgreich gespeichert!");
							TreeMainPanel.getInstance().refreshTree();
//							TreeExhibitionPanel.getInstance().refreshTree();
							dispose();
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,
									e1.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						} catch (OutsourcedNotFoundException e1) {
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

		setValues();
		checkAndSetIfDeleted();
	}

	/**
	 * Gets all Museums and add them to 2 vectors
	 * 
	 * @return ArrayList<String>
	 */
	public Vector<String> getVectorsMuseum() {

		listMuseum = Access.getAllMuseums();

		for (Museum actual : listMuseum) {
			vectorMuseumLong.add(actual.getId());
			vectorMuseumString.add(actual.getName());
		}
		return vectorMuseumString;
	}

	/**
	 * Get all Contacts and add them to vectors
	 * 
	 * @return ArrayList<String>
	 */
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

	/**
	 * Gets all addresses and add them to vectors
	 * 
	 * @return ArrayList<String>
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
	 * Gets all the date form textfields and textareas
	 * 
	 * @throws InvalidArgumentsException
	 */
	public void initialize() {
		description = textAreaDescription.getText();
		description = textAreaDescription.getText();
		startDate = textFieldStartDay.getText() + textFieldStartMonth.getText()
				+ textFieldStartYear.getText();
		endDate = textFieldEndDay.getText() + textFieldEndMonth.getText()
				+ textFieldEndYear.getText();
		if (startDate.equals(""))
			validStart = true;
		else
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

	/**
	 * 
	 */
	void refreshContact() {
		vectorContactLong.clear();
		vectorContactString.clear();
		comboBoxContact.setModel(new DefaultComboBoxModel<String>(
				getVectorsContact()));
	}

	// void refreshMuseum() {
	// vectorMuseumLong.clear();
	// vectorMuseumString.clear();
	// comboBoxMuseum.setModel(new DefaultComboBoxModel<String>(
	// getVectorsMuseum()));
	// }

	/**
	 * Refreshes the Address
	 */
	void refreshAdress() {
		vectorAddressLong.clear();
		vectorAddressString.clear();
		comboBoxAddress.setModel(new DefaultComboBoxModel<String>(
				getVectorsAddress()));
	}

	void idToLoan(long l) {
		ArrayList<Outsourced> al = Access.getAllLoans();
		for (Outsourced actual : al) {
			if (actual.getId() == l) {
				loan = actual;
			}
		}
	}

	/**
	 * When opening the dialog, set all Information about loan to textfields etc
	 */
	void setValues() {
		Calendar c = Calendar.getInstance();
		c.setTime(loan.getStartDate());
		if (c.get(Calendar.DAY_OF_MONTH) < 10) {
			textFieldStartDay.setText("0" + c.get(Calendar.DAY_OF_MONTH));
		} else {
			textFieldStartDay.setText("" + c.get(Calendar.DAY_OF_MONTH));
		}

		if (c.get(Calendar.MONTH) < 9) {
			textFieldStartMonth.setText("0" + (c.get(Calendar.MONTH) + 1));
		} else {
			textFieldStartMonth.setText("" + (c.get(Calendar.MONTH) + 1));
		}
		textFieldStartYear.setText("" + c.get(Calendar.YEAR));

		if (loan.getEndDate() != null) {
			c.setTime(loan.getEndDate());
			if (c.get(Calendar.DAY_OF_MONTH) < 10) {
				textFieldEndDay.setText("0" + c.get(Calendar.DAY_OF_MONTH));

			}

			else {
				textFieldEndDay.setText("" + c.get(Calendar.DAY_OF_MONTH));

			}

			if (c.get(Calendar.MONTH) < 9) {
				textFieldEndMonth.setText("0" + (c.get(Calendar.MONTH) + 1));
			} else {
				textFieldEndMonth.setText("" + (c.get(Calendar.MONTH) + 1));
			}
			textFieldEndYear.setText("" + c.get(Calendar.YEAR));
		}
		textFieldName.setText(loan.getName());
		textAreaDescription.setText(loan.getDescription());

		ListIterator<Long> itc = vectorContactLong.listIterator();
		int temp = 0;
		while (itc.hasNext()) {

			Long actual = itc.next();
			temp++;
			if (actual.equals(loan.getContact_id())) {
				temp--;
				comboBoxContact.setSelectedIndex(temp);

			}

		}
		ListIterator<Long> ita = vectorAddressLong.listIterator();
		temp = 0;
		while (ita.hasNext()) {
			Long actual = ita.next();
			temp++;
			if (actual.equals(loan.getAddress_id())) {
				comboBoxAddress.setSelectedIndex(temp - 1);
			}

		}

	}

	public static EditLoan getInstance() {
		// if (instance==null){
		// instance=new EditLoan();
		// }
		return instance;

	}

	public void checkAndSetIfDeleted() {
		if (loan.isDeleted() == true) {

			textFieldName.setEnabled(false);
			textFieldStartDay.setEnabled(false);
			textFieldEndDay.setEnabled(false);
			textFieldStartMonth.setEnabled(false);
			textFieldEndMonth.setEnabled(false);
			textFieldStartYear.setEnabled(false);
			textFieldEndYear.setEnabled(false);
			textAreaDescription.setEnabled(false);
			comboBoxContact.setEnabled(false);
			comboBoxAddress.setEnabled(false);
			okButton.setEnabled(false);
			btnNewButton.setEnabled(false);
			btnNeueKontaktadresse.setEnabled(false);
			setTitle("Leihgabe [Gelöscht]");
		}
	}
}
