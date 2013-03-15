package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
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
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;

public class CreateRole extends JDialog {

	/**
	 * @author Alexander Adema
	 */
	private static final long serialVersionUID = 5383034154171574312L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldNameRole;
	private long museumid;
	private String museumname;
	private String rolename;
	/**
	 * Create the dialog.
	 */
	public CreateRole() {
		setModal(true);
		setTitle("Neue Rolle");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 303, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		textFieldNameRole = new JTextField();
		textFieldNameRole.setBounds(117, 10, 151, 19);
		contentPanel.add(textFieldNameRole);
		textFieldNameRole.setColumns(10);
		
		JLabel lblRollenname = new JLabel("Rollenname");
		lblRollenname.setBounds(12, 12, 88, 15);
		contentPanel.add(lblRollenname);
		
		/*JLabel lblMuseum = new JLabel("Museum");
		lblMuseum.setBounds(12, 53, 70, 15);
		contentPanel.add(lblMuseum);*/
		
		/*comboBox = new JComboBox<TreeNodeObject>();
		comboBox.setBounds(117, 48, 151, 24);
		contentPanel.add(comboBox);
		fillChoice();*/
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Erstellen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						getValues();
						
						try {
							Access.insertRole(rolename, museumid);
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Falsche Werte eingetragen", JOptionPane.ERROR_MESSAGE);
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
						}
						InformationPanel.getInstance().setText("Neue Rolle erstellt");
						CreateContact.getInstance().refreshVector();
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
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	
	public CreateRole(final EditContact eC) {
		setModal(true);
		setTitle("Rollenerstellung");
		setBounds(100, 100, 303, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		textFieldNameRole = new JTextField();
		textFieldNameRole.setBounds(117, 10, 151, 19);
		contentPanel.add(textFieldNameRole);
		textFieldNameRole.setColumns(10);
		
		JLabel lblRollenname = new JLabel("Rollenname");
		lblRollenname.setBounds(12, 12, 88, 15);
		contentPanel.add(lblRollenname);
		
		JLabel lblMuseum = new JLabel("Museum");
		lblMuseum.setBounds(12, 53, 70, 15);
		contentPanel.add(lblMuseum);
		
		/*comboBox = new JComboBox<TreeNodeObject>();
		comboBox.setBounds(117, 48, 151, 24);
		contentPanel.add(comboBox);
		fillChoice(); */
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Erstellen");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						getValues();
						
						try {
							Access.insertRole(rolename, museumid);
							InformationPanel.getInstance().setText("Neue Rolle erstellt");
							eC.refreshVector();
							dispose();
						} catch (InvalidArgumentsException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Falsche Werte", JOptionPane.ERROR_MESSAGE);
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
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
	
	/*public void fillChoice(){
		allmuseums = Access.getAllMuseums();
		for (Museum i : allmuseums) {
			TreeNodeObject o = new TreeNodeObject(i.getName());
			o.setMuseumId(i.getId());
			comboBox.addItem(o);
		} */
	
	/**
	 * Getting all values for saving a role
	 * 
	 * @param text
	 */
	public void getValues() {
		/*museumname = ((TreeNodeObject)comboBox.getSelectedItem()).getName();
		museumid = ((TreeNodeObject)comboBox.getSelectedItem()).getMuseumId();*/
		try{
		museumid=MuseumMainPanel.getInstance().getMuseumId();
		museumname=Access.searchMuseumID(museumid).getName(); }
		catch(MuseumNotFoundException e){}
		rolename = textFieldNameRole.getText();
	}

	/**
	 * 
	 * @return museumid
	 */
	public long getMuseumid() {
		return museumid;
	}

	/**
	 * 
	 * @param museumid
	 */
	public void setMuseumid(long museumid) {
		this.museumid = museumid;
	}

	/**
	 * 
	 * @return museumname
	 */
	public String getMuseumname() {
		return museumname;
	}

	/**
	 * 
	 * @param museumname
	 */
	public void setMuseumname(String museumname) {
		this.museumname = museumname;
	}

	/**
	 * 
	 * @return rolename
	 */
	public String getRolename() {
		return rolename;
	}

	/**
	 * 
	 * @param rolename
	 */
	public void setRolename(String rolename) {
		this.rolename = rolename;
	}
}
