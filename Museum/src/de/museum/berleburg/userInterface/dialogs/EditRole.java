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

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;

public class EditRole extends JDialog {

	/**
	 * @author Alexander Adema
	 */
	private static final long serialVersionUID = 217422020808081851L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldRoleName;
	private String rolename;
	private Long museum_id, role_id;
	//private JComboBox<TreeNodeObject> comboBoxMuseum;

	/**
	 * Create the dialog.
	 */
	public EditRole(final Long role_id, final boolean table, JDialog owner, boolean modal) {
		super(owner, modal);
		setTitle("Rollenänderung");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 298, 142);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		{
			JLabel lblRollenname = new JLabel("Rollenname:");
			contentPanel.add(lblRollenname, "cell 0 0,alignx trailing");
		}
		{
			textFieldRoleName = new JTextField();
			contentPanel.add(textFieldRoleName, "cell 1 0,growx");
			textFieldRoleName.setColumns(10);
		}
		
		/*{
			JLabel lblMuseum = new JLabel("Museum:");
			contentPanel.add(lblMuseum, "cell 0 1,alignx trailing");
		}*/
		
		/*{
			comboBoxMuseum = new JComboBox<TreeNodeObject>();
			contentPanel.add(comboBoxMuseum, "cell 1 1,growx");
		}*/
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Rolle speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						getValues();
						
						try {
							Access.changeRole(role_id, rolename, museum_id);
							InformationPanel.getInstance().setText("Rolle geändert");
							if(table){
								ShowRoles.getInstance().updateTable();
								
							}
							else{
								CreateContact.getInstance().refreshVector();
							}
							
							dispose();
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null, e1.getMessage(),"Datenbankfehler",JOptionPane.ERROR_MESSAGE);
						}
						
						if(Access.roleIsUsed(role_id)) {
							try{
							MainGUI.getDetailPanel().setDetails(Access
									.searchMuseumID(MuseumMainPanel.getInstance().getMuseumId()));  }
						catch(MuseumNotFoundException e0) {
							JOptionPane.showMessageDialog(null,e0.getMessage(),"Fehler",JOptionPane.ERROR_MESSAGE);
							
						    }
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
		
		setValues(role_id);
		//fillcombo();
		
	}
	

	/**
	 * Second Constructor
	 * 
	 * @param role_id
	 * @param eC
	 */
	public EditRole(final Long role_id, final EditContact eC) {
		setModal(true);
		setTitle("Rollenänderung");
		setBounds(100, 100, 298, 142);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		{
			JLabel lblRollenname = new JLabel("Rollenname:");
			contentPanel.add(lblRollenname, "cell 0 0,alignx trailing");
		}
		{
			textFieldRoleName = new JTextField();
			contentPanel.add(textFieldRoleName, "cell 1 0,growx");
			textFieldRoleName.setColumns(10);
		}
		{
			JLabel lblMuseum = new JLabel("Museum:");
			contentPanel.add(lblMuseum, "cell 0 1,alignx trailing");
		}
		
		/*{
			comboBoxMuseum = new JComboBox<TreeNodeObject>();
			contentPanel.add(comboBoxMuseum, "cell 1 1,growx");
		}*/
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Rolle speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						getValues();
						
						try {
							Access.changeRole(role_id, rolename, museum_id);
							InformationPanel.getInstance().setText("Rolle geändert");
							eC.refreshVector();
							dispose();
						} catch (ConnectionException e1) {
							JOptionPane.showMessageDialog(null,e1.getMessage(),"Datenbanlfehler",JOptionPane.ERROR_MESSAGE);
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
		
		setValues(role_id);
		//fillcombo();
		
	}
	

	//end SecondKonstruktor
	/**
	 * Getting all values for saving a role
	 * 
	 * @param text
	 */
	public void getValues(){
		rolename = textFieldRoleName.getText();
//		Role ro = Access.searchRoleId(role_id);
//		museum_id = ro.getMuseum_id();
		//museum_id = ((TreeNodeObject)comboBoxMuseum.getSelectedItem()).getMuseumId();
		museum_id=MuseumMainPanel.getInstance().getMuseumId();
		
	}
	
	/**
	 * Filling up the textfields with content by role_id
	 * 
	 * @param role_id
	 */
	public void setValues(Long role_id){
		Role ro = null;
		ro = Access.searchRoleId(role_id);
		rolename = ro.getName();
		textFieldRoleName.setText(rolename);
		//comboBoxMuseum.setSelectedItem(ro.equals(museum_id));
		
	}
	
	/**
	 * Filling the comboBox with content by id
	 * 
	 * @param text
	 */
	/*public void fillcombo(){
		ArrayList<Museum> allmuseums = Access.getAllMuseums();
		for (Museum i : allmuseums) {
			TreeNodeObject o = new TreeNodeObject(i.getName());
			o.setMuseumId(i.getId());
			comboBoxMuseum.addItem(o);
		}
		
	} */
	
	/**
	 * 
	 * @return museum_id
	 */
	public Long getMuseum_id() {
		return museum_id;
	}
	
	/**
	 * 
	 * @param museum_id
	 */
	public void setMuseum_id(Long museum_id) {
		this.museum_id = museum_id;
	}

	/**
	 * 
	 * @return role_id
	 */
	public Long getRole_id() {
		return role_id;
	}

	/**
	 * 
	 * @param role_id
	 */
	public void setRole_id(Long role_id) {
		this.role_id = role_id;
	}

}
