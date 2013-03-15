package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;

/**
 * 
 * @author Way Dat To
 *
 */

@SuppressWarnings("serial")
public class InsertLoan extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldStartDay;
	private JTextField textFieldEndDay;
	private JTextField textFieldStartMonth;
	private JTextField textFieldEndMonth;
	private JTextField textFieldStartYear;
	private JTextField textFieldEndYear;

	private JComboBox<String> comboBoxMuseum;
	private ComboBoxModel<String> comboBoxModelMuseum;
	@SuppressWarnings("unused")
	private JComboBox<String> comboBoxContact;
	@SuppressWarnings("unused")
	private JComboBox<String> comboBoxAdress;

	@SuppressWarnings("unused")
	private String startDate, endDate;

	Date dateStart;
	Date dateEnd;
	DateFormat formatter = null;

	Vector<Long> vectorMuseumLong = new Vector<Long>();
	Vector<String> vectorMuseumString = new Vector<String>();
	ArrayList<Museum> listMuseum = new ArrayList<Museum>();
	
	Vector<Long> vectorContactLong = new Vector<Long>();
	Vector<String> vectorContactString = new Vector<String>();
	ArrayList<Contact> listContact = new ArrayList<Contact>();
	
	Vector<Long> vectorAdressLong = new Vector<Long>();
	Vector<String> vectorAdressString = new Vector<String>();
	ArrayList<Contact> listAdress = new ArrayList<Contact>();


	/**
	 * Create the dialog.
	 */
	public InsertLoan() {
		setTitle("Neuer Verleih");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 480, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		
			listMuseum = Access.getAllMuseums();
		

		contentPanel.setLayout(new MigLayout("", "[][40:n:40][40:n:40,grow][][140:n:140,grow][60:n:60,grow]", "[][][][][][grow]"));
		{
			JLabel lblStartDatum = new JLabel("Start Datum");
			contentPanel.add(lblStartDatum, "cell 0 0,alignx left");
		}
		{
			textFieldStartDay = new JTextField();
			contentPanel.add(textFieldStartDay, "cell 1 0,growx");
			textFieldStartDay.setColumns(10);
		}
		{
			textFieldStartMonth = new JTextField();
			contentPanel.add(textFieldStartMonth, "cell 2 0,growx");
			textFieldStartMonth.setColumns(10);
		}
		{
			textFieldStartYear = new JTextField();
			contentPanel.add(textFieldStartYear, "cell 3 0,growx");
			textFieldStartYear.setColumns(10);
		}
		{
			JLabel lblTtmmjjjj = new JLabel("TT.MM.JJJJ");
			contentPanel.add(lblTtmmjjjj, "cell 4 0");
		}
		{
			JLabel lblEndDatum = new JLabel("End Datum");
			contentPanel.add(lblEndDatum, "cell 0 1,alignx left");
		}
		{
			textFieldEndDay = new JTextField();
			contentPanel.add(textFieldEndDay, "cell 1 1,growx");
			textFieldEndDay.setColumns(10);
		}
		{
			textFieldEndMonth = new JTextField();
			contentPanel.add(textFieldEndMonth, "cell 2 1,growx");
			textFieldEndMonth.setColumns(10);
		}
		{
			textFieldEndYear = new JTextField();
			contentPanel.add(textFieldEndYear, "cell 3 1,growx");
			textFieldEndYear.setColumns(10);
		}
		{
			JLabel lblTtmmjjjj_1 = new JLabel("TT.MM.JJJJ");
			contentPanel.add(lblTtmmjjjj_1, "cell 4 1");
		}
		{
			JLabel lblKontaktPerson = new JLabel("Kontaktperson");
			contentPanel.add(lblKontaktPerson, "cell 0 2,alignx left");
		}
		{
			JComboBox<String> comboBoxContact = new JComboBox<String>();
			contentPanel.add(comboBoxContact, "cell 1 2 3 1,growx");
		}
		{
			JButton btnNewButton = new JButton("Neue Kontaktperson");
			btnNewButton.setMaximumSize(new Dimension(140, 23));
			contentPanel.add(btnNewButton, "cell 4 2,growx");
		}
		{
			JLabel lblKontaktadresse = new JLabel("Kontaktadresse");
			contentPanel.add(lblKontaktadresse, "cell 0 3,alignx left");
		}
		{
			JComboBox<String> comboBoxAdress = new JComboBox<String>();
			contentPanel.add(comboBoxAdress, "cell 1 3 3 1,growx");
		}
		{
			JButton btnNeueKontaktadresse = new JButton("Neue Kontaktadresse");
			btnNeueKontaktadresse.setMaximumSize(new Dimension(140, 23));
			contentPanel.add(btnNeueKontaktadresse, "cell 4 3,growx");
		}
		{
			JLabel lblMuseum = new JLabel("Museum");
			contentPanel.add(lblMuseum, "cell 0 4,alignx left");
		}
		{
			comboBoxMuseum = new JComboBox<String>();
			comboBoxMuseum.setMinimumSize(new Dimension(150, 20));
			contentPanel.add(comboBoxMuseum, "cell 1 4 3 1,growx");
			comboBoxModelMuseum = new DefaultComboBoxModel<String>(getVectorsMuseum());
			comboBoxModelMuseum.addListDataListener(comboBoxMuseum);
			comboBoxMuseum.setModel(comboBoxModelMuseum);
			comboBoxMuseum.setBounds(158, 207, 320, 20);
			contentPanel.add(comboBoxMuseum);

		}
		{
			JButton btnNeuesMuseum = new JButton("Neues Museum");
			btnNeuesMuseum.setMaximumSize(new Dimension(140, 23));
			contentPanel.add(btnNeuesMuseum, "cell 4 4,growx");
		}
		{
			JLabel lblOptional = new JLabel("* Optional");
			contentPanel.add(lblOptional, "cell 5 4");
		}
		{
			JLabel lblBeschreibung = new JLabel("Beschreibung");
			contentPanel.add(lblBeschreibung, "cell 0 5,aligny top");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 1 5 4 1,grow");
			{
				JTextArea textAreaDescription = new JTextArea();
				scrollPane.setViewportView(textAreaDescription);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						MainGUI.getInformationPanel().setText(
								"Neuer Verleih erfolgreich erstellt.");
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

	public Vector<String> getVectorsMuseum() {

		for (Museum actual : listMuseum) {
			vectorMuseumLong.add(actual.getId());
			vectorMuseumString.add(actual.getName());
		}
		return vectorMuseumString;
	}
}
