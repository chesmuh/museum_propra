package de.museum.berleburg.userInterface.panels;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;

import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.listeners.LabelListener;
import de.museum.berleburg.userInterface.models.LabelListModel;

/**
 * A panel with a list of existing labels, a simple filter and
 * create/delete/rename buttons. Use the dialog() function to create a JDialog
 * containing a LabelsPanel.
 * 
 * @author Christian Landel
 * 
 */
@SuppressWarnings("serial")
public class LabelsPanel extends JPanel {
	private JTextField textFieldFilter;
	private JList<String> list = new JList<String>();
	private Collection<LabelListener> listeners = new LinkedList<LabelListener>();
	private Collection<LabelListener> deleteListeners = new LinkedList<LabelListener>();

	/**
	 * Listeners added to this panel will be informed of changes in labels.
	 * <p>
	 * example:
	 * 
	 * <pre>
	 * {@code}
	 * JDialog dialog = new JDialog();
	 * LabelsPanel lp = new LabelsPanel();
	 * ...
	 * dialog.add(lp);
	 * lp.addListener ( new LabelListener() {
	 * 	public void event(Collection&ltLabel&gt labels) {
	 * 		System.out.println("Labels were modified:");
	 * 		for (Label label : labels)
	 * 			System.out.println("\t"+label.getName());
	 * 	}
	 * });
	 */
	public void addListener(LabelListener listener) {
		listeners.add(listener);
	}

	public void addDeleteListener(LabelListener listener) {
		deleteListeners.add(listener);
	}

	/**
	 * update the JList with the available Labels, applying the filter
	 */
	private void update() {
		ArrayList<Label> availablel = new ArrayList<Label>();
		for (Label label : Access.getAllLabels())
			if (label.getName().toUpperCase()
					.contains(textFieldFilter.getText().toUpperCase()))
				availablel.add(label);
		getModel().set(availablel);
	}

	/**
	 * @return a model of type LabelListModel, representing the content in the
	 *         list
	 */
	public LabelListModel getModel() {
		ListModel<String> get = list.getModel();
		if (!(get instanceof LabelListModel)) {
			LabelListModel set = new LabelListModel(new LinkedList<Label>(),
					list);
			list.setModel(set);
			return set;
		}
		return (LabelListModel) get;
	}

	/**
	 * rename a label; only possible if one label is selected
	 */
	private void renameLabel() {
		Collection<Label> selection = getModel().getSelection();
		if (selection.size() != 1) {
			JOptionPane.showMessageDialog(this,
					"Es muss genau 1 Label zum Umbenennen ausgewählt werden",
					"Umbennen nicht möglich", JOptionPane.WARNING_MESSAGE);
			return;
		}
		for (Label label : selection) {
			String name = JOptionPane.showInputDialog(
					this,
					"Bitte geben Sie einen neuen Namen für das Label "
							+ label.getName() + " an:",
					"Neuer Name für ein Label", JOptionPane.QUESTION_MESSAGE);
			if (name == null) // user canceled the dialog
				return;
			
			if (name.isEmpty()) { // user entered nothing; better ask again!
				renameLabel();
				return;
			}
			
			try {
				Access.changeLabel(label.getId(), name);
			} catch (ConnectionException e1) {
				JOptionPane.showMessageDialog(null,
						"Verbindungsfehler zur Datenbank!", "Datenbankfehler",
						JOptionPane.ERROR_MESSAGE);
			} /*
			 * catch (LabelNotFoundException e1) {
			 * JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
			 * JOptionPane.ERROR_MESSAGE); } catch (InvalidArgumentsException
			 * e1) { JOptionPane.showMessageDialog(null, e1.getMessage(),
			 * "Fehler", JOptionPane.ERROR_MESSAGE); }
			 */
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Fehler beim Umbenennen: "
						+ e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				renameLabel();
			}
		}
		for (LabelListener listener : listeners)
			listener.event(selection);
		update();
		TreeMainPanel.getInstance().refreshTree();
	}

	/**
	 * fetch the current entries from listAvailable and delete them, if
	 * confirmed by the user
	 */
	private void deleteLabel() {
		Collection<Label> selection = getModel().getSelection();
		if (selection.isEmpty())
			JOptionPane.showMessageDialog(this,
					"Kein Objekt zum löschen gewählt.",
					"Löschen nicht möglich", JOptionPane.WARNING_MESSAGE);
		else {
			String listedNames = "";
			for (Label l : selection)
				if (listedNames.equals(""))
					listedNames = l.getName();
				else
					listedNames += ", " + l.getName();
			int selSize = selection.size();
			int answer = selSize > 1 ? JOptionPane.showConfirmDialog(this,
					"Die gewählten " + selSize + " Labels löschen?\n("
							+ listedNames + ")", "Sicherheitsabfrage",
					JOptionPane.YES_NO_OPTION) : JOptionPane.showConfirmDialog(
					this, "Das gewählte Label löschen?\n(" + listedNames + ")",
					"Sicherheitsabfrage", JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION)
				return;
		}
		try {
			for (Label label : selection)
				Access.deleteLabel(label.getId());
		} catch (ConnectionException e1) {
			JOptionPane.showMessageDialog(null,
					"Verbindungsfehler zur Datenbank!", "Datenbankfehler",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(null, e.getMessage(),
							"Fehler beim Löschen von Labels",
							JOptionPane.ERROR_MESSAGE);
		}
		for (LabelListener l : deleteListeners)
			l.event(selection);
		update();
		TreeMainPanel.getInstance().refreshTree();
	}

	/**
	 * open a dialog and create a new label in the DB if desired and possible
	 */
	public static Long createLabel(Component parent) {
		Long id = null;
		String name = JOptionPane.showInputDialog(parent,
				"Bitte geben Sie das neue Label, d.h. einen Bezeichner, ein:",
				"neues Label erstellen", JOptionPane.QUESTION_MESSAGE);
		if (name == null)
			return null;
		try {
			id = Access.insertLabel(name);
		} catch (ConnectionException e1) {
			JOptionPane.showMessageDialog(null,
					"Verbindungsfehler zur Datenbank!", "Datenbankfehler",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parent, e.getMessage(),
					"Fehler beim Erstellen des Labels",
					JOptionPane.ERROR_MESSAGE);
			return LabelsPanel.createLabel(parent);
		}
		TreeMainPanel.getInstance().refreshTree();
		return id;
	}

	/**
	 * open a dialog and create a new label in the DB if desired and possible
	 */
	public static void createLabel() {
		LabelsPanel.createLabel(null);
	}

	/**
	 * create a dialog containing a panel of this type
	 */
	public static JDialog dialog(Component parent) {
		JDialog result;
		if (parent instanceof Frame)
			result = new JDialog((Frame) parent);
		else if (parent instanceof Dialog)
			result = new JDialog((Dialog) parent);
		else if (parent instanceof Window)
			result = new JDialog((Window) parent);
		else
			result = new JDialog();
		result.setModal(true);
		LabelsPanel lp = new LabelsPanel();
		result.add(lp);
		result.setMinimumSize(new Dimension(300,300));
		result.setSize(350, 400);
		result.setTitle("Labels verwalten");
		result.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		result.setVisible(true);
		return result;
	}

	/**
	 * create a dialog containing a panel of this type
	 */
	public static JDialog dialog() {
		return LabelsPanel.dialog(null);
	}

	public LabelsPanel() {
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[] { 1.0 };
		gbl.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0 };
		setLayout(gbl);

		JLabel label = new JLabel("Vorhandene Labels");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
		scrollPane.getViewport().setView(list);

		JPanel panelFilter = new JPanel();
		GridBagConstraints gbc_panelFilter = new GridBagConstraints();
		gbc_panelFilter.anchor = GridBagConstraints.NORTH;
		gbc_panelFilter.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelFilter.gridx = 0;
		gbc_panelFilter.gridy = 2;
		add(panelFilter, gbc_panelFilter);
		panelFilter.setLayout(new BoxLayout(panelFilter, BoxLayout.X_AXIS));

		JLabel lblFilter = new JLabel("Anzeige eingrenzen:");
		panelFilter.add(lblFilter);

		textFieldFilter = new JTextField();
		panelFilter.add(textFieldFilter);
		textFieldFilter.setColumns(10);

		JButton btnClearFilter = new JButton("x");
		panelFilter.add(btnClearFilter);

		JPanel panelAction = new JPanel();
		GridBagConstraints gbc_panelAction = new GridBagConstraints();
		gbc_panelAction.gridx = 0;
		gbc_panelAction.gridy = 3;
		add(panelAction, gbc_panelAction);
		panelAction.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

		JButton btnCreateLabel = new JButton("erstellen");
		panelAction.add(btnCreateLabel);

		JButton btnDeleteLabel = new JButton("löschen");
		panelAction.add(btnDeleteLabel);

		JButton btnRenameLabel = new JButton("umbenennen");
		panelAction.add(btnRenameLabel);

		/**
		 * the current instance(this) for the listeners; this is NOT a singleton
		 */
		final LabelsPanel instance = this;

		textFieldFilter.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				update();
			}

			public void keyTyped(KeyEvent e) {
			}
		});
		btnClearFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textFieldFilter.setText("");
				update();
			}
		});
		btnCreateLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Long id = LabelsPanel.createLabel(instance);
				if (id != null) {
					try {
						Collection<Label> result = new LinkedList<Label>();
						result.add(Access.searchLabelById(id));
						for (LabelListener listener : listeners)
							listener.event(result);
					} catch (LabelNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(),
								"Fehler", JOptionPane.ERROR_MESSAGE);
					}
				}
				update();
			}
		});
		btnDeleteLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteLabel();
				update();
			}
		});
		btnRenameLabel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renameLabel();
			}
		});

		textFieldFilter.setText("");
		update();
	}
}
