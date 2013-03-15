package de.museum.berleburg.userInterface.dialogs;

import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.listeners.SimpleListener;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * display a single history element in a dialog
 * @author Christian Landel
 *
 */
@SuppressWarnings("serial")
public class HistoryDetails extends JDialog
{
	LinkedList<SimpleListener> listeners = new LinkedList<SimpleListener>();
	public HistoryDetails (Window owner, History history)
	{
		super(owner);
		init(history);
	}
	public HistoryDetails (Dialog owner, History history)
	{
		super(owner);
		init(history);
	}
	public HistoryDetails (Frame owner, History history)
	{
		super(owner);
		init(history);
	}
	/** register event functions that will be informed when this dialog is closed */
	public void addDisposeListener (SimpleListener listener) {
		listeners.add(listener);
	}
	@Override
	public void dispose() {
		for (SimpleListener listener : listeners)
			listener.event();
		super.dispose();
	}
	private void init (History history)
	{
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);

		if (history.isDeleted()) {
			JLabel lblDeleted = new JLabel ("Das Exponat ist zu diesem Zeitpunkt gelöscht gewesen.");
			GridBagConstraints gbc_lblDeleted = new GridBagConstraints();
			gbc_lblDeleted.anchor = GridBagConstraints.CENTER;
			gbc_lblDeleted.gridx = 0;
			gbc_lblDeleted.gridy = 0;
			gbc_lblDeleted.gridwidth = 2;
			getContentPane().add(lblDeleted, gbc_lblDeleted);
		}
		
		JLabel lblTime = new JLabel("Zeitpunkt:");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.anchor = GridBagConstraints.WEST;
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 1;
		getContentPane().add(lblTime, gbc_lblTime);
		
		JTextField textFieldTime = new JTextField();
		textFieldTime.setEditable(false);
		GridBagConstraints gbc_textFieldTime = new GridBagConstraints();
		gbc_textFieldTime.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldTime.gridx = 1;
		gbc_textFieldTime.gridy = 1;
		getContentPane().add(textFieldTime, gbc_textFieldTime);
		textFieldTime.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.insets = new Insets(0, 0, 5, 5);
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 2;
		getContentPane().add(lblName, gbc_lblName);
		
		JTextField textFieldName = new JTextField();
		textFieldName.setEditable(false);
		GridBagConstraints gbc_textFieldName = new GridBagConstraints();
		gbc_textFieldName.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldName.gridx = 1;
		gbc_textFieldName.gridy = 2;
		getContentPane().add(textFieldName, gbc_textFieldName);
		textFieldName.setColumns(10);
		
		JLabel lblMuseum = new JLabel("Museum:");
		GridBagConstraints gbc_lblMuseum = new GridBagConstraints();
		gbc_lblMuseum.anchor = GridBagConstraints.WEST;
		gbc_lblMuseum.insets = new Insets(0, 0, 5, 5);
		gbc_lblMuseum.gridx = 0;
		gbc_lblMuseum.gridy = 3;
		getContentPane().add(lblMuseum, gbc_lblMuseum);
		
		JTextField textFieldMuseum = new JTextField();
		textFieldMuseum.setEditable(false);
		GridBagConstraints gbc_textFieldMuseum = new GridBagConstraints();
		gbc_textFieldMuseum.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldMuseum.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldMuseum.gridx = 1;
		gbc_textFieldMuseum.gridy = 3;
		getContentPane().add(textFieldMuseum, gbc_textFieldMuseum);
		textFieldMuseum.setColumns(10);
		
		JLabel lblSection = new JLabel("Sektion:");
		GridBagConstraints gbc_lblSection = new GridBagConstraints();
		gbc_lblSection.anchor = GridBagConstraints.WEST;
		gbc_lblSection.insets = new Insets(0, 0, 5, 5);
		gbc_lblSection.gridx = 0;
		gbc_lblSection.gridy = 4;
		getContentPane().add(lblSection, gbc_lblSection);
		
		JTextField textFieldSection = new JTextField();
		textFieldSection.setEditable(false);
		GridBagConstraints gbc_textFieldSection = new GridBagConstraints();
		gbc_textFieldSection.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldSection.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldSection.gridx = 1;
		gbc_textFieldSection.gridy = 4;
		getContentPane().add(textFieldSection, gbc_textFieldSection);
		textFieldSection.setColumns(10);
		
		JLabel lblCategory = new JLabel("Kategorie:");
		GridBagConstraints gbc_lblCategory = new GridBagConstraints();
		gbc_lblCategory.anchor = GridBagConstraints.WEST;
		gbc_lblCategory.insets = new Insets(0, 0, 5, 5);
		gbc_lblCategory.gridx = 0;
		gbc_lblCategory.gridy = 5;
		getContentPane().add(lblCategory, gbc_lblCategory);
		
		JTextField textFieldCategory = new JTextField();
		textFieldCategory.setEditable(false);
		GridBagConstraints gbc_textFieldCategory = new GridBagConstraints();
		gbc_textFieldCategory.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldCategory.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldCategory.gridx = 1;
		gbc_textFieldCategory.gridy = 5;
		getContentPane().add(textFieldCategory, gbc_textFieldCategory);
		textFieldCategory.setColumns(10);
		
		JLabel lblOutsourced = new JLabel("");
		GridBagConstraints gbc_lblOutsourced = new GridBagConstraints();
		gbc_lblOutsourced.anchor = GridBagConstraints.WEST;
		gbc_lblOutsourced.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutsourced.gridx = 0;
		gbc_lblOutsourced.gridy = 6;
		getContentPane().add(lblOutsourced, gbc_lblOutsourced);
		
		JTextField textFieldOutsourced = new JTextField();
		textFieldOutsourced.setEditable(false);
		GridBagConstraints gbc_textFieldOutsourced = new GridBagConstraints();
		gbc_textFieldOutsourced.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldOutsourced.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldOutsourced.gridx = 1;
		gbc_textFieldOutsourced.gridy = 6;
		getContentPane().add(textFieldOutsourced, gbc_textFieldOutsourced);
		textFieldOutsourced.setColumns(10);
		
		JLabel lblDescription = new JLabel("Beschreibung:");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblDescription.insets = new Insets(0, 0, 0, 5);
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 7;
		getContentPane().add(lblDescription, gbc_lblDescription);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 7;
		getContentPane().add(scrollPane, gbc_scrollPane);
		
		JTextArea textAreaDescription = new JTextArea();
		scrollPane.setViewportView(textAreaDescription);
		textAreaDescription.setEditable(false);
		textAreaDescription.setText(history.getDescription());
		
		
		textFieldTime.setText(new SimpleDateFormat("EE, 'der' dd.MM.yyyy, 'um' HH:mm:ss")
								.format(history.getInsert()));
		textFieldName.setText(history.getName());
		try {textFieldMuseum.setText(Access.searchMuseumID(history.getMuseum_id()).getName());
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}

		/** check if the museum was deleted due to being separated during the backup */
		boolean museumDeleted = (Long)history.getMuseum_id()==null || history.getMuseum_id()<=0L;
		
		//   S E C T I O N
		if (museumDeleted) {
			textFieldSection.setText("(nicht verfügbar)");
			textFieldSection.setEnabled(false);
		}
		else if (history.getSection_id()==null || history.getSection_id().equals(0L)) {
			textFieldSection.setText("(keine)");
			textFieldSection.setEnabled(false);
		}
		else
			try {
				textFieldSection.setText(
					Access.searchSectionID(history.getSection_id())
						.getName()
				);
			} catch (SectionNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}

		
		//   C A T E G O R Y
		if (museumDeleted) {
			textFieldCategory.setText("(nicht verfügbar)");
			textFieldCategory.setEnabled(false);
		}
		else if (history.getCategory_id()==null || history.getCategory_id().equals(0L)) {
			textFieldCategory.setText("(keine)");
			textFieldCategory.setEnabled(false);
		}
		else
			try {
				textFieldCategory.setText(
					Access.searchCategoryID(history.getCategory_id())
						.getName()
				);
			} catch (CategoryNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		
		//   O U T S O U R C E D
		Outsourced outsourced=null;
		if (history.getOutsourced_id()!=null)
			outsourced=history.getOutsourced();
		if (outsourced!=null)
		{
			if (outsourced.isLoan())
				lblOutsourced.setText("Leihgabe:");
			else
				lblOutsourced.setText("Ausstellung:");
			textFieldOutsourced.setText(outsourced.getName());
		}
		else
			textFieldOutsourced.setEnabled(false);
		
		// DESCRIPTION
		try {
		} catch (Exception e) {
			textAreaDescription.setText(e.getMessage());
			textAreaDescription.setEnabled(false);
		}

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
		setSize(500, 350);
		try {
			setTitle("Historienelement des Exponats \""
					+Access.searchExhibitID(
						history.getExhibit_id())
							.getName()
					+"\""
			);
		} catch (Exception e) {
			setTitle("Historienelement");
		}
	}
}
