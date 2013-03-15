package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeExhibitionPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

@SuppressWarnings("serial")
public class CreateExhibition extends JDialog {

	/**
	 * Create the CreateMuseumJDialog.
	 * 
	 * @author Way Dat To
	 */
	// private static final long serialVersionUID = 1L;
	private JPanel contentPanel = new JPanel();

	private boolean addToExhibition;
	private JTextField textFieldNameMuseum;
	private JTextField textFieldStartDay;
	private JTextField textFieldEndDay;
	private JTextArea textAreaDescription;
	private JTextField textFieldStartMonth;
	private JTextField textFieldStartYear;
	private JTextField textFieldEndMonth;
	private JTextField textFieldEndYear;

	private String name;
	private String description;
	private String startDate;
	private String endDate;
	Date dateStart;
	Date dateEnd;
	DateFormat formatter = null;

	Vector<Long> vectorLong = new Vector<Long>();
	Vector<String> vectorString = new Vector<String>();
	ArrayList<Museum> list = new ArrayList<Museum>();

	JOptionPane pane;

	/**
	 * Create the dialog.
	 */
	public CreateExhibition(boolean b) {

		addToExhibition = b;

		setMinimumSize(new Dimension(550, 480));
		setSize(new Dimension(550, 480));
		setModal(true);
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainGUI.class
								.getResource("/de/museum/berleburg/userInterface/logo.png")));
		getContentPane().setMaximumSize(new Dimension(2147483647, 640));
		setTitle("Neue Ausstellung");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setMinimumSize(new Dimension(10, 480));
		contentPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		JLabel exhibtionName = new JLabel("Titel der Ausstellung");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, exhibtionName, 12,
				SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, exhibtionName, 19,
				SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, exhibtionName, -372,
				SpringLayout.EAST, contentPanel);
		exhibtionName.setFont(new Font("Arial", Font.BOLD, 13));
		contentPanel.add(exhibtionName);

		textFieldNameMuseum = new JTextField();
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldNameMuseum,
				-2, SpringLayout.NORTH, exhibtionName);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldNameMuseum,
				180, SpringLayout.WEST, contentPanel);
		textFieldNameMuseum.setHorizontalAlignment(SwingConstants.LEFT);
		textFieldNameMuseum.setMaximumSize(new Dimension(640, 2147483647));
		contentPanel.add(textFieldNameMuseum);
		textFieldNameMuseum.setColumns(10);

		JPanel panelAdress = new JPanel();
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, panelAdress, -40,
				SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, panelAdress, 519,
				SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, panelAdress, 19,
				SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, panelAdress, 38,
				SpringLayout.NORTH, contentPanel);
		panelAdress.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPanel.add(panelAdress);
		panelAdress.setLayout(null);

		textFieldStartDay = new JTextField();
		textFieldStartDay.setMinimumSize(new Dimension(40, 20));
		textFieldStartDay.setBounds(158, 8, 40, 20);
		panelAdress.add(textFieldStartDay);
		textFieldStartDay.setColumns(10);

		textFieldEndDay = new JTextField();
		textFieldEndDay.setBounds(158, 39, 40, 20);
		panelAdress.add(textFieldEndDay);
		textFieldEndDay.setColumns(10);

		JLabel exhibitionEnd = new JLabel("Ende der Ausstellung(*)");
		exhibitionEnd.setBounds(10, 36, 138, 14);
		panelAdress.add(exhibitionEnd);

		JLabel exhibitionStart = new JLabel("Anfang der Ausstellung");
		exhibitionStart.setBounds(10, 11, 138, 14);
		panelAdress.add(exhibitionStart);

		JLabel exhibitionDescription = new JLabel("Beschreibung (*)");
		exhibitionDescription.setFont(new Font("Arial", Font.BOLD, 13));
		exhibitionDescription.setBounds(10, 61, 109, 20);
		panelAdress.add(exhibitionDescription);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(158, 67, 320, 124);
		panelAdress.add(scrollPane);

		textAreaDescription = new JTextArea();
		scrollPane.setViewportView(textAreaDescription);

		textFieldStartMonth = new JTextField();
		textFieldStartMonth.setMinimumSize(new Dimension(40, 20));
		textFieldStartMonth.setColumns(10);
		textFieldStartMonth.setBounds(208, 8, 40, 20);
		panelAdress.add(textFieldStartMonth);

		textFieldStartYear = new JTextField();
		textFieldStartYear.setMinimumSize(new Dimension(40, 20));
		textFieldStartYear.setColumns(10);
		textFieldStartYear.setBounds(258, 8, 40, 20);
		panelAdress.add(textFieldStartYear);

		textFieldEndMonth = new JTextField();
		textFieldEndMonth.setColumns(10);
		textFieldEndMonth.setBounds(208, 39, 40, 20);
		panelAdress.add(textFieldEndMonth);

		textFieldEndYear = new JTextField();
		textFieldEndYear.setColumns(10);
		textFieldEndYear.setBounds(258, 39, 40, 20);
		panelAdress.add(textFieldEndYear);

		JLabel lblNewLabel = new JLabel("TT.MM.JJJJ");
		lblNewLabel.setMinimumSize(new Dimension(80, 16));
		lblNewLabel.setBounds(320, 11, 68, 14);
		panelAdress.add(lblNewLabel);

		JLabel label = new JLabel("TT.MM.JJJJ");
		label.setMinimumSize(new Dimension(80, 16));
		label.setBounds(320, 42, 68, 14);
		panelAdress.add(label);

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setMaximumSize(new Dimension(32767, 640));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JLabel lblOptional = new JLabel("(*) Optional");
				buttonPane.add(lblOptional);

				JButton btnOk = new JButton("Ausstellung erstellen");
				btnOk.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						name = textFieldNameMuseum.getText();
						description = textAreaDescription.getText();
						startDate = textFieldStartDay.getText()
								+ textFieldStartMonth.getText()
								+ textFieldStartYear.getText();
						endDate = textFieldEndDay.getText()
								+ textFieldEndMonth.getText()
								+ textFieldEndYear.getText();
						// TODO Caro:Änderungen vorgenommen zur Datumsprüfung
						boolean validStart = false;
						
							try {
								validStart = Access.checkDate(
										textFieldStartDay.getText(),
										textFieldStartMonth.getText(),
										textFieldStartYear.getText());
							} catch (InvalidArgumentsException e2) {
								JOptionPane.showMessageDialog(null,
										e2.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
						boolean validEnd = false;
						if (endDate.equals(""))
							validEnd = true;
						else
							try {
								validEnd = Access.checkDate (
										textFieldEndDay.getText(),
										textFieldEndMonth.getText(),
										textFieldEndYear.getText());
							} catch (InvalidArgumentsException e2) {
								JOptionPane.showMessageDialog(null,
										e2.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
							formatter = new SimpleDateFormat("ddMMyyyy");
						try {
							dateStart = (Date) formatter.parse(startDate);
						} catch (ParseException e1) { /*
							JOptionPane.showMessageDialog(null,
									"Ungültiges Startdatum!", "Fehler",
									JOptionPane.ERROR_MESSAGE);
							// TODO Caro: überschneidet sich mit unseren
							// Felermeldungen
							/*
							 * JOptionPane.showM JLabel lblchooseMuseum = new
							 * JLabel("Wählen Sie ihr Museum");
							 * lblchooseMuseum.setBounds(10, 210, 138, 14);
							 * JLabel lblOptional = new JLabel("* Optional");
							 * lblOptional.setBounds(10, 240, 138, 14);
							 * panelAdress.add(lblOptional);
							 * 
							 * panelAdress.add(lblchooseMuseum);essageDialog(null
							 * , "Ungültiges Startdatum!", "Fehler",
							 * JOptionPane.ERROR_MESSAGE);
							 */
						}

						if (endDate.equals("")) {
							dateEnd = null;
							try {
								Access.insertExhibition(name, description,
										dateStart, dateEnd, MuseumMainPanel
												.getInstance().getMuseumId(),
										validStart, validEnd);
								InformationPanel.getInstance().setText(
										"Eine neue Ausstellung " + name
												+ " wurde erstellt.");
								TreeMainPanel.getInstance()
										.getTreeExhibitionPanel()
										.refreshTreeWithoutTable();
								dispose();

							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(null,
										"Verbindungsfehler zur Datenbank!",
										"Datenbankfehler",
										JOptionPane.ERROR_MESSAGE);
							} catch (InvalidArgumentsException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}

							if (addToExhibition) {
								AddToExhibition.getInstance().refresh();
							}
						} else {
							try {
								dateEnd = (Date) formatter.parse(endDate);
							} catch (ParseException e1) {
								// TODO: Caro: überschneidet sich mit unseren
								// Fehlermeldungen
								/*JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE); */
							}
							try {
								int result = JOptionPane.YES_OPTION;
								if(dateEnd.before(new Date())) {
									result = JOptionPane.showConfirmDialog(null, "Das Enddatum befindet sich vor dem heutigen Tag. Möchten Sie die Ausstellung trotzdem speichern?", "Enddatum", JOptionPane.YES_NO_OPTION);
								}
								if(JOptionPane.YES_OPTION == result) {
									Access.insertExhibition(name, description,
											dateStart, dateEnd, MuseumMainPanel
											.getInstance().getMuseumId(),
											validStart, validEnd);
									InformationPanel.getInstance().setText(
											"Eine neue Ausstellung " + name
											+ " wurde erstellt.");
									TreeExhibitionPanel.getInstance()
									.refreshTreeWithoutTable();
									dispose();
								}
							} catch (ConnectionException e1) {
								JOptionPane.showMessageDialog(null,
										"Verbindungsfehler zur Datenbank!",
										"Datenbankfehler",
										JOptionPane.ERROR_MESSAGE);
							} catch (InvalidArgumentsException e1) {
								JOptionPane.showMessageDialog(null,
										e1.getMessage(), "Fehler",
										JOptionPane.ERROR_MESSAGE);
							}
							if (addToExhibition) {
								InformationPanel.getInstance().setText(
										AddToExhibition.getInstance()
												.toString());
								AddToExhibition.getInstance().refresh();
							}

						}

					}
				});
				btnOk.setActionCommand("OK");
				buttonPane.add(btnOk);
				getRootPane().setDefaultButton(btnOk);
			}
			{
				JButton btnCancel = new JButton("Abbrechen");
				btnCancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				btnCancel.setActionCommand("Cancel");
				buttonPane.add(btnCancel);
			}
		}
	}

	public Vector<String> getVectors() {
		list = Access.getAllMuseums();

		for (Museum actual : list) {
			vectorLong.add(actual.getId());
			vectorString.add(actual.getName());
		}

		return vectorString;
	}
}
