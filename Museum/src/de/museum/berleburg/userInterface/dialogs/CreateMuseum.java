package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;



public class CreateMuseum extends JDialog {

	/**
	 * Create the CreateMuseumJDialog.
	 * 
	 * @author Alexander Adema
	 * @author Way Dat To (small Changes) 
	 */

	 
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNameMuseum;
	private JTextField textFieldStreet;
	private JTextField textFieldZip;
	private JTextField textFieldCity;
	private JTextField textFieldCountry;
	private JTextField textFieldState;
	private JTextField textFieldHouseNumber;
	private JTextArea textAreaDescriptionMuseum;
	


	String nameMuseum, streetMuseum, cityMuseum, countryMuseum, stateMuseum, nameContact, lastNameContact, email1, descriptionContact, descriptionMuseum,telephone, fax, houseNumberMuseum, zipMuseum, rolename ;

//	double zipMuseum;
//	double houseNumberMuseum;
//	double telephone, fax;

	/**
	 * Create the dialog.
	 */
	public CreateMuseum() {
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Neues Museum");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(300, 100, 480, 480);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setMinimumSize(new Dimension(10, 640));
		contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[120px][22px][498px]", "[20px][][15px][grow]"));
		
		JLabel lblNameDMuseums = new JLabel("Name d. Museums");
		lblNameDMuseums.setFont(new Font("Arial", Font.BOLD, 13));
		contentPanel.add(lblNameDMuseums, "cell 0 0,alignx left,aligny center");
		
		textFieldNameMuseum = new JTextField();
		contentPanel.add(textFieldNameMuseum, "cell 2 0,alignx left,growy");
		textFieldNameMuseum.setColumns(28);
		
		JPanel panelAdress = new JPanel();
		panelAdress.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(panelAdress, "cell 0 1 3 1,growx,aligny top");
		panelAdress.setLayout(new MigLayout("", "[109px][160px][31px][46px][10px][23px][13px][74px]", "[20px][20px][20px]"));
		
		textFieldStreet = new JTextField();
		panelAdress.add(textFieldStreet, "cell 1 0 3 1,growx,aligny top");
		textFieldStreet.setColumns(10);
		
		JLabel lblHausnr = new JLabel("HausNr");
		lblHausnr.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblHausnr, "cell 5 0,growx,aligny center");
		
		textFieldHouseNumber = new JTextField();
		panelAdress.add(textFieldHouseNumber, "cell 7 0,alignx right,aligny top");
		textFieldHouseNumber.setColumns(4);
		
		textFieldCity = new JTextField();
		panelAdress.add(textFieldCity, "cell 5 1 3 1,growx,aligny top");
		textFieldCity.setColumns(10);
		
		JLabel lblStrasse = new JLabel("Strasse");
		lblStrasse.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblStrasse, "cell 0 0,growx,aligny center");
		
		JLabel lblPlz = new JLabel("PLZ");
		lblPlz.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblPlz, "cell 0 1,alignx left,aligny center");
		
		JLabel lblOrt = new JLabel("Ort");
		lblOrt.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblOrt, "cell 3 1,growx,aligny top");
		
		textFieldZip = new JTextField();
		panelAdress.add(textFieldZip, "cell 1 1,alignx left,aligny top");
		textFieldZip.setColumns(10);
		
		textFieldCountry = new JTextField();
		panelAdress.add(textFieldCountry, "cell 1 2,growx,aligny top");
		textFieldCountry.setColumns(10);
		
		JLabel lblCountry = new JLabel("Bundesland");
		lblCountry.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblCountry, "cell 0 2,growx,aligny center");
		

		
		textFieldState = new JTextField();
		textFieldState.setColumns(10);
		panelAdress.add(textFieldState, "cell 5 2 3 1,growx,aligny top");
		
		JLabel lblState = new JLabel("Land");
		lblState.setFont(new Font("Arial", Font.BOLD, 13));
		panelAdress.add(lblState, "cell 3 2,growx,aligny center");
		
		JLabel lbldescription = new JLabel("Beschreibung(*)");
		contentPanel.add(lbldescription, "cell 0 2,alignx left,aligny top");
		lbldescription.setFont(new Font("Arial", Font.BOLD, 13));
		

		textAreaDescriptionMuseum = new JTextArea();

//		contentPanel.add(textAreaDescriptionMuseum);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, "cell 0 3 3 1,grow");
		scrollPane.setViewportView(textAreaDescriptionMuseum);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnOk = new JButton("Museum anlegen");
				btnOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						
						getValuesForSaving();
						
						try {
							try {
								
//								int reply2 = JOptionPane.showConfirmDialog(null, "Möchten Sie jetzt speichern?", "Speichern", JOptionPane.YES_NO_OPTION);
//								if (reply2 == JOptionPane.YES_OPTION) {
									// it seems to be mixed up with country and state @Benedikt
									long id = Access.insertAllMuseum(nameMuseum, descriptionMuseum, streetMuseum, houseNumberMuseum, zipMuseum, cityMuseum, countryMuseum, stateMuseum);
									dispose();
									Museum selected = Access.searchMuseumID(id);
									MuseumMainPanel.getInstance().setMuseumToSelect(selected);
									MuseumMainPanel.getInstance().refreshComboBox();

									//Die Nullpointerabfrage ist notwendig!
									if(MainGUI.getDetailPanel()!=null)MainGUI.getDetailPanel().setDetails(selected);

									

//								} else if (reply2 == JOptionPane.NO_OPTION) {
									//dispose();
//								}
								int reply = JOptionPane.showConfirmDialog(null, "Möchten Sie jetzt eine Kontaktperson anlegen?", "Kontakt anlegen", JOptionPane.YES_NO_OPTION);
								if (reply == JOptionPane.YES_OPTION) {
									CreateContact i = new CreateContact();
									i.setVisible(true);
									i.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
								} else if (reply == JOptionPane.NO_OPTION) {
									dispose();
								}
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showMessageDialog(CreateMuseum.this, e1.getMessage(), "Fehler Museum not found", JOptionPane.ERROR_MESSAGE);
								InformationPanel.getInstance().setText(e1.getMessage());
							}
							dispose();
							TreeMainPanel.getInstance().refreshTree();
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(CreateMuseum.this, e1.getMessage(), "Fehler Invalid", JOptionPane.ERROR_MESSAGE);
							InformationPanel.getInstance().setText(e1.getMessage());
						}
						catch (ConnectionException e2){
							JOptionPane.showMessageDialog(CreateMuseum.this, e2.getMessage(), "Fehler Invalid", JOptionPane.ERROR_MESSAGE);
						}
						

					}
				});
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);
				
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				JButton btnCancel = new JButton("Abbrechen");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(Access.getAllMuseums().size()==0)
						{
							int reply = JOptionPane.showConfirmDialog(null, "Sie können das Programm nicht ohne ein Museum starten.\nMöchten Sie das Programm beenden?", "Kein Museum", JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) 
							{
								dispose();
								System.exit(0);
							}
						}else dispose();
					}
				});
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
				

			}
			JButton loadbackup = new JButton("Museum importieren");
			loadbackup.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
						setAlwaysOnTop(false);
						ImportDatabase i = new ImportDatabase();
						i.setVisible(true);
						i.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						setAlwaysOnTop(true);
					}
				}
			});
			buttonPane.add(loadbackup);

		}
	}
	
	
	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- Methods -------------------------------------------- **/
	/**  --------------------------------------------------------------------------------------- **/
	
	
	
	/**
	 * Getting all values needed for saving a new museum
	 * 
	 * @param text
	 */
	public void getValuesForSaving(){
		
		nameMuseum = getTextFieldNameMuseum().getText();
		descriptionMuseum = getTextAreaDescriptionMuseum().getText();
		streetMuseum = getTextFieldStreet().getText();
		houseNumberMuseum = getTextFieldHouseNumber().getText();
		zipMuseum = getTextFieldZip().getText();
		cityMuseum = getTextFieldCity().getText();
		stateMuseum = getTextFieldState().getText();
		countryMuseum = getTextFieldCountry().getText();

		
	}

	
	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- getter & setter ------------------------------------ **/
	/**  --------------------------------------------------------------------------------------- **/
	
	/**
	 * @return the textFieldNameMuseum
	 */
	public JTextField getTextFieldNameMuseum() {
		return textFieldNameMuseum;
	}

	/**
	 * @param textFieldNameMuseum
	 */
	public void setTextFieldNameMuseum(JTextField textFieldNameMuseum) {
		this.textFieldNameMuseum = textFieldNameMuseum;
	}

	/**
	 * @return the textFieldStreet
	 */
	public JTextField getTextFieldStreet() {
		return textFieldStreet;
	}

	/**
	 * @param textFieldStreet
	 */
	public void setTextFieldStreet(JTextField textFieldStreet) {
		this.textFieldStreet = textFieldStreet;
	}

	/**
	 * @return the textFieldZip
	 */
	public JTextField getTextFieldZip() {
		return textFieldZip;
	}

	/**
	 * @param textFieldZip
	 */
	public void setTextFieldZip(JTextField textFieldZip) {
		this.textFieldZip = textFieldZip;
	}

	/**
	 * @return the textFieldCity
	 */
	public JTextField getTextFieldCity() {
		return textFieldCity;
	}

	/**
	 * @param textFieldCity
	 */
	public void setTextFieldCity(JTextField textFieldCity) {
		this.textFieldCity = textFieldCity;
	}
	
	/**
	 * @return the textFieldCountry
	 */
	public JTextField getTextFieldCountry(){
		return textFieldCountry;
	}
	
	/**
	 * @param textFieldCountry
	 */
	public void setTextFieldCountry(JTextField textFieldCountry){
		this.textFieldCountry = textFieldCountry;
	}
	
	/**
	 * @return the textFieldState
	 */
	public JTextField getTextFieldState(){
		return textFieldState;
	}
	
	/**
	 * @param textFieldState
	 */
	public void setTextFieldState(JTextField textFieldState){
		this.textFieldState = textFieldState;
	}

	/**
	 * @return the textFieldHouseNumber
	 */
	public JTextField getTextFieldHouseNumber() {
		return textFieldHouseNumber;
	}

	/**
	 * @param textFieldHouseNumber
	 */
	public void setTextFieldHouseNumber(JTextField textFieldHouseNumber) {
		this.textFieldHouseNumber = textFieldHouseNumber;
	}

	/**
	 * @return the textFieldCountry
	 */
	public JTextField getTextFieldEmail2() {
		return textFieldCountry;
	}

	/**
	 * @param textFieldCountry
	 */
	public void setTextFieldEmail2(JTextField textFieldEmail2) {
		this.textFieldCountry = textFieldEmail2;
	}

	/**
	 * @return the textAreaDescriptionMuseum
	 */
	public JTextArea getTextAreaDescriptionMuseum() {
		return textAreaDescriptionMuseum;
	}

	/**
	 * @param textAreaDescriptionMuseum
	 */
	public void setTextAreaDescriptionMuseum(JTextArea textAreaDescriptionMuseum) {
		this.textAreaDescriptionMuseum = textAreaDescriptionMuseum;
	}
}
