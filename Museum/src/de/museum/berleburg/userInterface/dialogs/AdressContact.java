package de.museum.berleburg.userInterface.dialogs;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;


/**
 * 
 * @author Way Dat
 *
 */

@SuppressWarnings("serial")
public class AdressContact extends JDialog {
	private JTextField textFieldStreet;
	private JTextField textFieldCity;
	private JTextField textFieldState;
	private JTextField textFieldCountry;
	private JTextField textFieldNumber;
	private JTextField textFieldZip;
	String street,city,state,country,number,zip;

	/**
	 * Create the dialog.
	 */
	public AdressContact() {
		setModal(true);
		setTitle("Adresse anlegen");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 440, 200);
		getContentPane().setLayout(new MigLayout("", "[12][80:n][120:n,grow][12:n][80:n][60px:n,grow][grow][434px]", "[][][][][20:n][33px]"));
		{
			JLabel lblStreet = new JLabel("Straße");
			getContentPane().add(lblStreet, "cell 1 0,alignx left");
		}
		{
			textFieldStreet = new JTextField();
			getContentPane().add(textFieldStreet, "cell 2 0,growx");
			textFieldStreet.setColumns(10);
		}
		{
			JLabel lblNumber = new JLabel("Hausnummer");
			getContentPane().add(lblNumber, "cell 4 0");
		}
		{
			textFieldNumber = new JTextField();
			getContentPane().add(textFieldNumber, "cell 5 0,growx");
			textFieldNumber.setColumns(10);
		}
		{
			JLabel lblCity = new JLabel("Stadt");
			getContentPane().add(lblCity, "cell 1 1");
		}
		{
			textFieldCity = new JTextField();
			getContentPane().add(textFieldCity, "cell 2 1,growx");
			textFieldCity.setColumns(10);
		}
		{
			JLabel lblZip = new JLabel("Postleitzahl");
			getContentPane().add(lblZip, "cell 4 1,alignx left,aligny center");
		}
		{
			textFieldZip = new JTextField();
			getContentPane().add(textFieldZip, "cell 5 1,growx");
			textFieldZip.setColumns(10);
		}
		{
			JLabel lblState = new JLabel("Bundesland");
			getContentPane().add(lblState, "cell 1 2,alignx left");
		}
		{
			textFieldState = new JTextField();
			getContentPane().add(textFieldState, "cell 2 2,growx");
			textFieldState.setColumns(10);
		}
		{
			JLabel lblCountry = new JLabel("Land");
			getContentPane().add(lblCountry, "cell 1 3,alignx left");
		}
		{
			textFieldCountry = new JTextField();
			getContentPane().add(textFieldCountry, "cell 2 3,growx");
			textFieldCountry.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 0 5 8 1,growx,aligny top");
			{
				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();
					
						try {
							Access.insertAddress(street, number, zip, city, state, country);
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
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
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	
	
	
//Second constructor
	
	public AdressContact(CreateLoan createLoan) {
		final CreateLoan cll=createLoan;
		setModal(true);
		setTitle("Adresse anlegen");
		setBounds(100, 100, 440, 200);
		getContentPane().setLayout(new MigLayout("", "[12][80:n][120:n,grow][12:n][80:n][60px:n,grow][grow][434px]", "[][][][][20:n][33px]"));
		{
			JLabel lblStreet = new JLabel("Straße");
			getContentPane().add(lblStreet, "cell 1 0,alignx left");
		}
		{
			textFieldStreet = new JTextField();
			getContentPane().add(textFieldStreet, "cell 2 0,growx");
			textFieldStreet.setColumns(10);
		}
		{
			JLabel lblNumber = new JLabel("Hausnummer");
			getContentPane().add(lblNumber, "cell 4 0");
		}
		{
			textFieldNumber = new JTextField();
			getContentPane().add(textFieldNumber, "cell 5 0,growx");
			textFieldNumber.setColumns(10);
		}
		{
			JLabel lblCity = new JLabel("Stadt");
			getContentPane().add(lblCity, "cell 1 1");
		}
		{
			textFieldCity = new JTextField();
			getContentPane().add(textFieldCity, "cell 2 1,growx");
			textFieldCity.setColumns(10);
		}
		{
			JLabel lblZip = new JLabel("Postleitzahl");
			getContentPane().add(lblZip, "cell 4 1,alignx left,aligny center");
		}
		{
			textFieldZip = new JTextField();
			getContentPane().add(textFieldZip, "cell 5 1,growx");
			textFieldZip.setColumns(10);
		}
		{
			JLabel lblState = new JLabel("Bundesland");
			getContentPane().add(lblState, "cell 1 2,alignx left");
		}
		{
			textFieldState = new JTextField();
			getContentPane().add(textFieldState, "cell 2 2,growx");
			textFieldState.setColumns(10);
		}
		{
			JLabel lblCountry = new JLabel("Land");
			getContentPane().add(lblCountry, "cell 1 3,alignx left");
		}
		{
			textFieldCountry = new JTextField();
			getContentPane().add(textFieldCountry, "cell 2 3,growx");
			textFieldCountry.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 0 5 8 1,growx,aligny top");
			{
				JButton okButton = new JButton("Anlegen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();
					
						try {
							Access.insertAddress(street, number, zip, city, state, country);
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						}
						cll.refreshAddress();
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
	
// seond constructor end
	
	public void initialize(){
		street = textFieldStreet.getText();
		number = textFieldNumber.getText();
		city = textFieldCity.getText();
		zip = textFieldZip.getText();
		country = textFieldCountry.getText();
		state = textFieldState.getText();
	}

}
