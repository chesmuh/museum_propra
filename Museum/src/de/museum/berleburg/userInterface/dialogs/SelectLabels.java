package de.museum.berleburg.userInterface.dialogs;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.listeners.LabelListener;
import de.museum.berleburg.userInterface.listeners.SimpleListener;
import de.museum.berleburg.userInterface.models.LabelListModel;
import de.museum.berleburg.userInterface.panels.LabelsPanel;

/**
 * A dialog with a LabelsPanel and a list of selected Labels.
 * Add LabelListeners with the addListener(...) method to get the selected labels.
 * <p>
 * example:
 * <pre>{@code}
 * SelectLabels dialog = new SelectLabels();
 * dialog.addListener ( new LabelListener() {
 * 	public void event(Collection&ltLabel&gt labels) {
 * 		System.out.println("Chosen Labels:");
 * 		for (Label label : labels)
 * 			System.out.println("\t"+label.getName());
 * 	}
 * });
 * @author Christian Landel
 *
 */
@SuppressWarnings("serial")
public class SelectLabels extends JDialog
{
	private JList<String> list = new JList<String>();
	LinkedList<LabelListener> listeners = new LinkedList<LabelListener>();
	LinkedList<SimpleListener> disposeListeners = new LinkedList<SimpleListener>();
	private LabelsPanel labelsPanel;
	Dialog parent;

	public SelectLabels(Dialog owner) {
		super(owner);
		init();
		parent = owner;
		parent.setEnabled(false);
	}
	public SelectLabels (Dialog owner, Exhibit exhibit) {
		super(owner);
		init();
		parent = owner;
		parent.setEnabled(false);
		List<Label> selected = Access.getAllLabelsByExhibitId(exhibit.getId());
		list.setModel(new LabelListModel(selected,list));
	}
	public SelectLabels (Dialog owner, Collection<Label> labels) {
		super(owner);
		init();
		parent = owner;
		parent.setEnabled(false);
		List<Label> labelsList = new LinkedList<Label>();
		for (Label label : labels)
			labelsList.add(label);
		list.setModel(new LabelListModel(labelsList,list));
	}
	/**
	 * Add a listener that will be notified when labels were selected.
	 * A list with the selected items will be sent to its event(Label) function,
	 * when and only if the user clicks OK (not the close button nor cancel).
	 */
	public void addListener (LabelListener listener) {
		listeners.add(listener);
	}
	/**
	 * Add a listener that will be notified when the dialog is closed,
	 * regardless of user input (whether "OK" or "Cancel" was chosen).
	 */
	public void addDisposeListener (SimpleListener listener) {
		disposeListeners.add(listener);
	}
	
	@Override
	public void dispose() {
		parent.setEnabled(true);
		super.dispose();
	}
	
//	/** somewhat hacky: a variable that prevents new confirm dialogs from popping up */
//	private boolean confirmDialogIsUp = false;
//	/**
//	 * override the dispose function so the user will be asked if sure
//	 */
	//TODO: why does this cause problems??? (see EditExhibit -> close())
//	@Override
//	public void dispose() {
//		if (confirmDialogIsUp)
//			return;
//		confirmDialogIsUp=true;
//		int answer = JOptionPane.showConfirmDialog(this,
//						"Label-Auswahl abbrechen?", "Sicherheitsabfrage", JOptionPane.YES_NO_OPTION);
//		if (answer==JOptionPane.YES_OPTION)
//			close();
//		else
//			confirmDialogIsUp=false;
//	}
//	/**
//	 * close the window without further confirmation
//	 */
//	public void close() {
//		confirmDialogIsUp=true;
//		for (SimpleListener listener : disposeListeners)
//			listener.event();
//		super.dispose();
//	}
	/**
	 * add selected items from the LabelsPanel to the list, if not already there
	 */
	private void select() {
		getModel().add(labelsPanel.getModel().getSelection());
	}
	/**
	 * remove the selected items from the list
	 */
	private void deselect() {
		getModel().remove(getModel().getSelection());
	}
	/**
	 * @return a model of type LabelListModel, representing the content in the list
	 */
	private LabelListModel getModel() {
		ListModel<String> get = list.getModel();
		if (! (get instanceof LabelListModel) ) {
			LabelListModel set = new LabelListModel(new LinkedList<Label>(),list);
			list.setModel(set);
			return set;
		}
		return (LabelListModel) get;
	}
	private void init ()
	{
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		GridBagLayout gbl = new GridBagLayout();
		gbl.columnWeights = new double[]{1.0, 0.0, 0.0, 1.0};
		gbl.rowWeights = new double[]{1.0};
		getContentPane().setLayout(gbl);
		
		labelsPanel = new LabelsPanel();
		GridBagConstraints gbc_lp = new GridBagConstraints();
		gbc_lp.fill=GridBagConstraints.BOTH;
		gbc_lp.gridx=0;
		gbc_lp.gridy=0;
		getContentPane().add(labelsPanel,gbc_lp);
		
		JButton btnSelect = new JButton(">");
		GridBagConstraints gbc_btnSelect = new GridBagConstraints();
		gbc_btnSelect.insets = new Insets(20, 0, 20, 6);
		gbc_btnSelect.fill = GridBagConstraints.BOTH;
		gbc_btnSelect.gridx = 1;
		gbc_btnSelect.gridy = 0;
		getContentPane().add(btnSelect, gbc_btnSelect);
		
		JButton btnDeselect = new JButton("<");
		GridBagConstraints gbc_btnDeselect = new GridBagConstraints();
		gbc_btnDeselect.insets = new Insets(20, 6, 20, 0);
		gbc_btnDeselect.fill = GridBagConstraints.BOTH;
		gbc_btnDeselect.gridx = 2;
		gbc_btnDeselect.gridy = 0;
		getContentPane().add(btnDeselect, gbc_btnDeselect);
		
		JPanel panelRight = new JPanel();
		GridBagLayout gbl_panelRight = new GridBagLayout();
		gbl_panelRight.columnWeights = new double[]{1.0};
		gbl_panelRight.rowWeights = new double[]{0.0, 1.0, 0.0};
		panelRight.setLayout(gbl_panelRight);
		GridBagConstraints gbc_panelRight = new GridBagConstraints();
		gbc_panelRight.fill=GridBagConstraints.BOTH;
		gbc_panelRight.gridx=3;
		gbc_panelRight.gridy=0;
		getContentPane().add(panelRight,gbc_panelRight);
		
		JLabel lblSelection = new JLabel("gewählte Labels");
		GridBagConstraints gbc_lblSelection = new GridBagConstraints();
		gbc_lblSelection.gridx = 0;
		gbc_lblSelection.gridy = 0;
		panelRight.add(lblSelection, gbc_lblSelection);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		panelRight.add(scrollPane, gbc_scrollPane);
		scrollPane.getViewport().setView(list);
		
		JPanel panelOkCancel = new JPanel();
		GridBagConstraints gbc_panelOkCancel = new GridBagConstraints();
		gbc_panelOkCancel.fill = GridBagConstraints.BOTH;
		gbc_panelOkCancel.gridx = 0;
		gbc_panelOkCancel.gridy = 2;
		panelRight.add(panelOkCancel, gbc_panelOkCancel);
		
		JButton btnOk = new JButton("OK");
		panelOkCancel.add(btnOk);
		
		JButton btnCancel = new JButton("Abbrechen");
		panelOkCancel.add(btnCancel);
		
		/** the current instance for the anonymous classes; this is NOT a singleton */
		final SelectLabels instance = this;

		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				select();
			}
		});
		btnDeselect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deselect();
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (LabelListener listener : listeners)
					listener.event(getModel().get());
				//empty the list so the events cannot be sent more than once
				listeners=new LinkedList<LabelListener>();
				dispose();
			}
		});
		
		labelsPanel.addListener(new LabelListener() {
			public void event (Collection<Label> labels) {
				for (ListDataListener listener : getModel().getListDataListeners())
					listener.contentsChanged(
							new ListDataEvent(instance, ListDataEvent.CONTENTS_CHANGED,
							0,getModel().getSize()-1));
			}
		});
		labelsPanel.addDeleteListener(new LabelListener() {
			public void event(Collection<Label> labels) {
				getModel().remove(labels);
			}
		});
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Labels auswählen und bearbeiten");
		setSize(600,400);
		setVisible(true);
		setModal(false);
	}
}
