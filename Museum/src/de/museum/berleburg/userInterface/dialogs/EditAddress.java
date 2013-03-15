package de.museum.berleburg.userInterface.dialogs;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.exceptions.AddressNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;


/**
 * 
 * @author Way Dat
 *
 */

@SuppressWarnings("serial")
public class EditAddress extends JDialog {
	private boolean showAddress;
	private JTextField textFieldStreet;
	private JTextField textFieldCity;
	private JTextField textFieldState;
	private JTextField textFieldCountry;
	private JTextField textFieldNumber;
	private JTextField textFieldZip;
	String street,city,state,country,number,zip;
	ArrayList<Address> list;
	Address address;

	/**
	 * Launch the application.
	 */
	@Override
	public void dispose()
	{
		MainGUI.getDetailPanel().refresh();
		super.dispose();
	}

	/**
	 * Create the dialog.
	 * @wbp.parser.constructor
	 */
	public EditAddress(final long id, boolean sA, JDialog dialog, boolean modal) {
		super(dialog, modal);
		this.showAddress=sA;
		idToAddress(id);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		
//		addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent arg0) {
//				int reply = JOptionPane.showConfirmDialog(null,
//						"Das Bearbeiten der Adresse wirklich beenden?",
//						"", JOptionPane.YES_NO_OPTION);
//				if (reply == JOptionPane.YES_OPTION) {
//					dispose();
//				} else if (reply == JOptionPane.NO_OPTION) {
//				}
//			}
//		});
		
		setTitle("Adresse bearbeiten");
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
				JButton okButton = new JButton("Änderung speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();
					
						try {
							Access.changeAddress(id, street, number, zip, city, state, country);
							if (showAddress){
								ShowAddresses.getInstance().updateTable();
							}
							else {
								CreateLoan.getInstance().refreshAddress();
							}
							
							dispose();
							InformationPanel.getInstance().setText("Änderung an der Adresse erfolgreich.");
							
						} catch (AddressNotFoundException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
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
		setValues();
//		setMinimumSize(new Dimension(450,250));
//		setMaximumSize(new Dimension(700,350));
		setSize(500,250);
		setResizable(false);
	}
	
	
	
	
//Second constructor
	
	public EditAddress(final Object CreateOrEditContact, long id) {
		
		idToAddress(id);
		
		setModal(true);
		setTitle("Adresse bearbeiten");
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
				JButton okButton = new JButton("Änderung speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();
					
						try {
							Access.changeAddress(address.getId(), street, number, zip, city, state, country);
							if (CreateOrEditContact instanceof EditContact){
								EditContact.getInstance().refreshAddress();
							}
							else if(CreateOrEditContact instanceof CreateContact){
								CreateContact.getInstance().refreshAddress();
							}
							InformationPanel.getInstance().setText("Änderung an der Adresse erfolgreich.");
							dispose();
							

							
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (AddressNotFoundException e1) {
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
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setValues();
//		setMinimumSize(new Dimension(450,250));
//		setMaximumSize(new Dimension(700,350));
		setSize(500,250);
		setResizable(false);
	}
	
// seond constructor end
	
	//Third constructor
	
public EditAddress(final long id) {
		
		idToAddress(id);
		
		setModal(true);
		setTitle("Adresse bearbeiten");
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
				JButton okButton = new JButton("Änderung speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						initialize();
					
						try {
							Access.changeAddress(id, street, number, zip, city, state, country);
							dispose();
							InformationPanel.getInstance().setText("Änderung an der Adresse erfolgreich.");
							
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
						} catch (AddressNotFoundException e1) {
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
		setValues();
//		setMinimumSize(new Dimension(450,250));
//		setMaximumSize(new Dimension(700,350));
		setSize(500,250);
		setResizable(false);
	}
		
	// third constructor end
	
	public void initialize(){
		street = textFieldStreet.getText();
		number = textFieldNumber.getText();
		city = textFieldCity.getText();
		zip = textFieldZip.getText();
		country = textFieldCountry.getText();
		state = textFieldState.getText();
	}
	
	public void idToAddress(long l){
		list=Access.getAllAddress();
		for (Address actual:list)
		{
			if(actual.getId().equals(l)){
				address=actual;
				
			}
		}
	}
	
	public void setValues(){
		textFieldStreet.setText(address.getStreet());
		textFieldNumber.setText(address.getHousenumber());
		textFieldCity.setText(address.getTown());
		textFieldZip.setText(address.getZipcode());
		textFieldCountry.setText(address.getCountry());
		textFieldState.setText(address.getState());
	}

}
