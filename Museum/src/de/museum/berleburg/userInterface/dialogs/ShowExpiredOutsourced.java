package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;
import de.museum.berleburg.userInterface.table.TableModelExpiredOutsourced;

public class ShowExpiredOutsourced extends JDialog
{

	/**
	 * @author Way Dat To
	 * @author Frank Hülsmann
	 * 
	 */
	private static final long serialVersionUID = -5432048624927094918L;
	private final JPanel tablePanel = new JPanel();
	private JPanel comboPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private JButton cancelButton = new JButton("Abbrechen");
	private JTable table;
	private TableModelExpiredOutsourced model;

	private static ShowExpiredOutsourced instance = null;
	private final JButton btnAnzeigen = new JButton("Exponate anzeigen");

	/**
	 * Create the dialog.
	 */
	/**
	 * 
	 */
	public ShowExpiredOutsourced() {
		setModal(true);
		toFront();
		instance = this;
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainGUI.class
								.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 550, 400);
		setTitle("Abgelaufene Ausstellungen und Leihgaben");
		getContentPane().setLayout(new BorderLayout());

		comboPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		tablePanel.setLayout(new GridBagLayout());

		comboPanel.setBorder(null);
		tablePanel.setBorder(null);

		getContentPane().add(tablePanel, BorderLayout.CENTER);
		getContentPane().add(comboPanel, BorderLayout.NORTH);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		table = new JTable();

		table.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

					int row = table.rowAtPoint(e.getPoint());
					long id = (long) model.getId(row);
					Outsourced o;
					btnAnzeigen.setEnabled(true);
					try {
						o = Access.getOutsourcedByID(id);
						MainGUI.getDetailPanel().setDetails(o);
					} catch (OutsourcedNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"", JOptionPane.ERROR_MESSAGE);
					}

					if (e.getClickCount() == 2) {
						btnAnzeigenListener();
					}
				}

			}

		});

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		tablePanel.add(new JScrollPane(table), c);

		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		btnAnzeigen.setEnabled(false);
		btnAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAnzeigenListener();
			}
		});

		buttonPanel.add(btnAnzeigen);
		buttonPanel.add(cancelButton);

		getRootPane().setDefaultButton(cancelButton);

		updateTable();

	}

	public void btnAnzeigenListener() {

		int row = table.getSelectedRow();
		long id = (long) model.getId(row);

		int rowIndex = table.getSelectedRow();
		if (rowIndex < 0) {
			return;
		}
		Outsourced o;
		if (table.getValueAt(row, 0).equals("Ausstellung")) {

			try {
				o = Access.searchExhibitonID(id);
				TablePanel.getInstance().updateTable(o.getMuseum_id(), 0, 0, 0,
						id);
				TablePanel.getInstance().refreshTable();
			} catch (OutsourcedNotFoundException e1) {
				JOptionPane.showConfirmDialog(ShowExpiredOutsourced.this,
						e1.getMessage());
			}
		} else {
			try {
				o = Access.searchLoanID(model.getId(row));
				TablePanel.getInstance().updateTable(o.getMuseum_id(), 0, 0, 0,
						model.getId(row));
				TablePanel.getInstance().refreshTable();
			} catch (OutsourcedNotFoundException e1) {
				JOptionPane.showConfirmDialog(ShowExpiredOutsourced.this,
						e1.getMessage());
			}

		}

	}

	/**
	 * Get instance method
	 * 
	 * @returns instance of the class
	 */
	public static ShowExpiredOutsourced getInstance() {
		if (instance == null) {
			instance = new ShowExpiredOutsourced();
		}

		return instance;
	}

	/**
	 * Updates the Table
	 */
	public void updateTable() {
		model = new TableModelExpiredOutsourced(getAllExpiredOutsourced());
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		model.fireTableDataChanged();
	}

	/**
	 * @return expiredOutsourced
	 */
	public ArrayList<Outsourced> getAllExpiredOutsourced() {
		ArrayList<Outsourced> expiredOutsourced = new ArrayList<Outsourced>();
		try {
			ArrayList<Outsourced> outsourced = new ArrayList<>();
			outsourced = Access.getAllOutsourced(MuseumMainPanel.getInstance()
					.getMuseumId());
			for (Outsourced actualOutsourced : outsourced) {
				try {
					if (Access.isExpired(actualOutsourced.getId())
							&& !actualOutsourced.isDeleted()
							&& !actualOutsourced.allBack()) {
						expiredOutsourced.add(actualOutsourced);

					}
				} catch (OutsourcedNotFoundException e) {
					JOptionPane.showConfirmDialog(this, e.getMessage());
				}
			}
		} catch (MuseumNotFoundException e1) {
			JOptionPane.showConfirmDialog(this, e1.getMessage());
		}

		return expiredOutsourced;
	}
}
