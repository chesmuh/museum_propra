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

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.table.TableModelAdress;

public class ShowAddresses extends JDialog {

	/**
	 * @author Frank Hülsmann
	 */
	private static final long serialVersionUID = -5432048624927094918L;
	private final JPanel tablePanel = new JPanel();
	private JPanel buttonPane = new JPanel();
	private JButton editButton = new JButton("Ändern");
	private JButton deleteButton = new JButton("Löschen");
	private JButton cancelButton = new JButton("Abbrechen");
	private JTable table;
	private TableModelAdress model;
	private static ShowAddresses instance = null;

	/**
	 * Create the dialog.
	 */
	public ShowAddresses(JFrame frame, boolean modal) {
		super(frame, modal);
		instance = this;
		setBounds(100, 100, 450, 300);
		setTitle("Alle Adressen");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		getContentPane().setLayout(new BorderLayout());
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
				
					Long addressId = model.getAddressId(selectedRow);
					
					EditAddress dialog = new EditAddress(addressId, true, ShowAddresses.this, true);
					dialog.setVisible(true);
				}
				
			
		});
		buttonPane.add(editButton);
		
		deleteButton.setActionCommand("OK");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				int selectedRow = table.getSelectedRow();
				
				String addressName = model.getAddressName(selectedRow);				
				if (JOptionPane.showConfirmDialog(null,
						"Wollen sie die Adresse \"" + addressName + "\" wirklich löschen?",
						"Exponat löschen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try 
					{
						Long deleteId = model.getAddressId(table.getSelectedRow());
						Access.deleteAddress(deleteId);
						updateTable();
					} catch (ConnectionException e1) {
						JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
					} catch (ModelAlreadyDeletedException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					} catch (IntegrityException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}

				} else {
					JOptionPane.showConfirmDialog(ShowAddresses.this, "Exponat wurde nicht gelöscht!");
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

	public ShowAddresses() {
	}

	/**
	 * 
	 * @return instance
	 */
	public static ShowAddresses getInstance()
	{
		if (instance == null)
		{
			instance = new ShowAddresses();
		}
		return instance;
	}
	
	/**
	 * Table Update
	 * 
	 */
	public void updateTable()
	{
		model = new TableModelAdress(Access.getAllAddress());
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		model.fireTableRowsUpdated(0, model.getRowCount());
	}
	
	
}
