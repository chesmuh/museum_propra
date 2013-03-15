package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ContactNotFoundException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.table.TableModelContact;


public class ShowContacts extends JDialog {

	/**
	 * @author Frank Hülsmann
	 */
	private static final long serialVersionUID = -5432048624927094918L;
	private final JPanel tablePanel = new JPanel();
	private JPanel buttonPane = new JPanel();
	private JButton editButton = new JButton("Ändern");
	private JButton deleteButton = new JButton("Löschen");
	private JButton cancelButton = new JButton("Schließen");
	private static ShowContacts instance = null;
	
	private JTable table;
	private TableModelContact model;

	/**
	 * Create the dialog.
	 */
	public ShowContacts(JFrame owner, boolean modal) {
		super(owner, modal);
		instance = this;
		setBounds(100, 100, 450, 300);
		setTitle("Alle Kontakte");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		getContentPane().setLayout(new BorderLayout());
		tablePanel.setLayout(new FlowLayout());
		tablePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(tablePanel, BorderLayout.CENTER);
		table = new JTable();
		tablePanel.setBorder(null);
		tablePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		tablePanel.add(new JScrollPane(table), c);
		
		updateTable();
		
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		
		editButton.setActionCommand("Edit");
		editButton.setEnabled(false);
		editButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedRow = table.getSelectedRow();

					Long contactId = model.getContactId(selectedRow);
					
					EditContact dialog = new EditContact(contactId, true, ShowContacts.this, true);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				
				
			}
		});
		buttonPane.add(editButton);
		
		deleteButton.setActionCommand("OK");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				
				int selectedRow = table.getSelectedRow();
				
				String contactName = model.getFullName(selectedRow);
				if (JOptionPane.showConfirmDialog(null,
						"Wollen sie den Kontakt \"" + contactName + "\" wirklich löschen?",
						"Exponat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try 
					{
						Long deleteId = model.getContactId(table.getSelectedRow());
						Contact ct = Access.searchContactID(deleteId);

						try {
							Access.deleteContact(deleteId);
						} catch (IntegrityException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null,
									"Diese Kontaktperson wird noch verwendet und kann nicht gelöscht werden!", "Kontaktperson in Verwendung",
									JOptionPane.ERROR_MESSAGE);
						}
						updateTable();
						
						//Update DetailPanel
						long roleid = ct.getRoleId();
						Role role = Access.searchRoleId(roleid);
						Museum museum = Access.searchMuseumID(role.getMuseum_id());
						if(MainGUI.getDetailPanel().getLastDisplayed()!=null && MainGUI.getDetailPanel().getLastDisplayed() instanceof Museum)
						{
							if(museum.getId().equals(((Museum)MainGUI.getDetailPanel().getLastDisplayed()).getId()))
								MainGUI.getDetailPanel().setDetails(museum);	
						}
						
					} catch (ConnectionException e1) { 
						JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);						
					} catch (ContactNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE); 
					} catch (ModelAlreadyDeletedException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					} catch (MuseumNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}

				} else {
					InformationPanel.getInstance().setText(
							"Exponat wurde nicht gelöscht");
				}
				
			}
		});
		buttonPane.add(deleteButton);
		
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPane.add(cancelButton);
		
		getRootPane().setDefaultButton(cancelButton);
		
		table.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {

				editButton.setEnabled(true);
				deleteButton.setEnabled(true);
				
			}
			

		});
		
	}
	
	public ShowContacts()
	{}
	
	/**
	 * 
	 * @return instance
	 */
	public static ShowContacts getInstance()
	{
		if(instance == null)
		{
			instance = new ShowContacts();
		}
		return instance;
	}
	
	
	/**
	 * Table Update
	 */
	public void updateTable()
	{
		model = new TableModelContact(Access.getAllContact());
		if(table!=null)table.setModel(model);
		if(table!=null)table.getTableHeader().setReorderingAllowed(false);
		if(table!=null)table.setRowSelectionAllowed(true);
		if(table!=null)table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model.fireTableRowsUpdated(0, model.getRowCount());
	}

}
