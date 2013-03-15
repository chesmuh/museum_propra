package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.AddressHasNoValueException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.DatabaseDriverNotFoundException;
import de.museum.berleburg.exceptions.MuseumIDNotFoundException;
import de.museum.berleburg.exceptions.NotAZipFileException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;

public class ExportDatabase extends JDialog {


	private static final long serialVersionUID = 5166693684760529172L;
	/**
	 * @author Alexander Adema
	 */
	

	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldPath;
	private JLabel lblDateipfad;
	private String path;
	private String filename;
	private Long museumid;
	private JCheckBox checkboxCompleteBackup;
	private JComboBox<TreeNodeObject> comboBoxMuseum;
	private JLabel lblLoading;
	private JButton btnSelectDataPath;
	private JButton btnExport;
	private JButton cancelButton;
	private JLabel lblMuseum;

	/**
	 * Create the dialog.
	 */
	public ExportDatabase() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setTitle("Backup erstellen");
		setBounds(100, 100, 524, 206);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			lblDateipfad = new JLabel("Dateiname:");
			sl_contentPanel.putConstraint(SpringLayout.NORTH, lblDateipfad, 10, SpringLayout.NORTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, lblDateipfad, 12, SpringLayout.WEST, contentPanel);
			contentPanel.add(lblDateipfad);
		}
		
		textFieldPath = new JTextField();
		textFieldPath.setEditable(false);
		sl_contentPanel.putConstraint(SpringLayout.WEST, textFieldPath, 132, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblDateipfad, -3, SpringLayout.WEST, textFieldPath);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, textFieldPath, -2, SpringLayout.NORTH, lblDateipfad);
		contentPanel.add(textFieldPath);
		textFieldPath.setColumns(10);
		
		btnSelectDataPath = new JButton("Speicherort");
		sl_contentPanel.putConstraint(SpringLayout.WEST, btnSelectDataPath, 369, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, btnSelectDataPath, -10, SpringLayout.EAST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, textFieldPath, -14, SpringLayout.WEST, btnSelectDataPath);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, btnSelectDataPath, -5, SpringLayout.NORTH, lblDateipfad);
		btnSelectDataPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				FileDialog fd = new FileDialog(parent, "", 1);
				
				 	JFileChooser fc = new JFileChooser();
			        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			        fc.setSelectedFile(new File(textFieldPath.getText()));
			        int returnVal = fc.showOpenDialog(null);
			        
			        File f;
			        if (returnVal == JFileChooser.APPROVE_OPTION)
			        {
			            f = fc.getSelectedFile();
			            path = f.getPath();
			            textFieldPath.setText(path);
			        }
			}
		});
		contentPanel.add(btnSelectDataPath);
		
		lblMuseum = new JLabel("Museum");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblMuseum, 30, SpringLayout.SOUTH, lblDateipfad);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblMuseum, 12, SpringLayout.WEST, contentPanel);
		contentPanel.add(lblMuseum);
		
		checkboxCompleteBackup = new JCheckBox("Komplettes Backup");
		sl_contentPanel.putConstraint(SpringLayout.WEST, checkboxCompleteBackup, 0, SpringLayout.WEST, textFieldPath);
		checkboxCompleteBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkboxCompleteBackup.isSelected()) {
					comboBoxMuseum.setEnabled(false);
				}
				else comboBoxMuseum.setEnabled(true);
			}
		});
		contentPanel.add(checkboxCompleteBackup);
		
		comboBoxMuseum = new JComboBox<TreeNodeObject>();
		sl_contentPanel.putConstraint(SpringLayout.WEST, comboBoxMuseum, 132, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, checkboxCompleteBackup, 16, SpringLayout.SOUTH, comboBoxMuseum);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblMuseum, -3, SpringLayout.WEST, comboBoxMuseum);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, comboBoxMuseum, -5, SpringLayout.NORTH, lblMuseum);
		contentPanel.add(comboBoxMuseum);
		
		lblLoading = new JLabel("");
		sl_contentPanel.putConstraint(SpringLayout.EAST, comboBoxMuseum, -17, SpringLayout.WEST, lblLoading);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lblLoading, 6, SpringLayout.SOUTH, btnSelectDataPath);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lblLoading, 0, SpringLayout.WEST, btnSelectDataPath);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lblLoading, -30, SpringLayout.SOUTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lblLoading, -7, SpringLayout.EAST, contentPanel);
		lblLoading.setVisible(false);
		lblLoading.setIcon(new ImageIcon(ExportDatabase.class.getResource("/de/museum/berleburg/userInterface/dialogs/ajax-loader.gif")));
		contentPanel.add(lblLoading);
		fillcombo();
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnExport = new JButton("Export");
				btnExport.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (textFieldPath.getText().isEmpty()) {
							JOptionPane.showMessageDialog(getContentPane(), "Bitte einen Dateinamen angeben!");
							return;
						}
						getValues();
						
						try {
							invisibleAll();
							lblLoading.setVisible(true);
							new Thread(new Runnable() {
								public void run() {
									
										if (!checkboxCompleteBackup.isSelected()) {
											JOptionPane.showMessageDialog(getContentPane(), "Folgende Daten werden nicht mit exportiert: \n\n" +
													"- Die Verweise der Exponatshistorien auf andere Museen \n\n");
										}
									
										try {
											
											Access.exportDatabase(path, museumid);
											JOptionPane.showMessageDialog(getContentPane(), "Export erfolgreich!");
							
										} catch (NumberFormatException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Nummernformat falsch!");
										} catch (FileNotFoundException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Datei oder Verzeichnis nicht gefunden!");							
										} catch (MuseumIDNotFoundException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Museums Id nicht gefunden!");								
										} catch (DatabaseDriverNotFoundException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Datenbanktreiber nicht gefunden!");								
										} catch (NotAZipFileException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Keine ZIP File!");									
										} catch (AddressHasNoValueException e1) {
											JOptionPane.showMessageDialog(getContentPane(), "Adresse hat keinen Wert!");									
										} catch (SQLException e1) {
											JOptionPane.showMessageDialog(null, e1.getMessage(), "SQL-Fehler", JOptionPane.ERROR_MESSAGE);
										} catch (ConnectionException e1) {
											JOptionPane.showMessageDialog(null, e1.getMessage(), "Verbindungsfehler", JOptionPane.ERROR_MESSAGE);
										}
										
										dispose();
				
										
									
								}
							},"Export UI Thread").start();

						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, e2.getMessage(), "Thread-Exception", JOptionPane.ERROR_MESSAGE);
					
						}
					}
				});
				btnExport.setActionCommand("OK");
				buttonPane.add(btnExport);
				getRootPane().setDefaultButton(btnExport);
			}
			{
				cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	/**
	 * Getting all values for exporting the database to a zip-file
	 * 
	 * @param text
	 */
	public void getValues() {
		
		if (checkboxCompleteBackup.isSelected()) {
			museumid = null;
			
		} else {
			museumid = getSelectedMuseum();

		}
		path = textFieldPath.getText();
		
		
		if (!path.endsWith(".zip")) {
			path = path + ".zip";
		}
		
				
	}
	
	/**
	 * set all fields invisible during the export is containing
	 * 
	 * @param text
	 */
	public void invisibleAll(){
		btnSelectDataPath.setVisible(false);
		btnExport.setVisible(false);
		cancelButton.setVisible(false);
		textFieldPath.setVisible(false);
		comboBoxMuseum.setVisible(false);
		checkboxCompleteBackup.setVisible(false);
		lblDateipfad.setVisible(false);
		lblMuseum.setVisible(false);
	}
	
	/**
	 * Filling the comboBox by museums
	 * 
	 * @param text
	 */
	public void fillcombo(){
		
		ArrayList<Museum> allMuseums = Access.getAllMuseums();
		for (Museum i : allMuseums) {
			TreeNodeObject o = new TreeNodeObject(i.getName());
			o.setMuseumId(i.getId());
			comboBoxMuseum.addItem(o);
		}
		
	}
	
	/**
	 * Getting the selected museumid
	 * 
	 * @return museumid
	 */
	public Long getSelectedMuseum()
	{
		TreeNodeObject o;
		o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();
		setMuseumid(o.getMuseumId());// = o.getMuseumId();
		return museumid;
		
	}
	

	/**
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * 
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * 
	 * @return museumid
	 */
	public Long getMuseumid() {
		return museumid;
	}

	/**
	 * 
	 * @param museumid
	 */
	public void setMuseumid(Long museumid) {
		this.museumid = museumid;
	}
}
