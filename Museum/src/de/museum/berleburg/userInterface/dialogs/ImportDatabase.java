package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;

public class ImportDatabase extends JDialog {

	/**
	 * @author Alexander Adema
	 */
	
	
	private static final long serialVersionUID = 4158453304849016927L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldDbFile;
	private String path;
	private File fileName;
	private JButton okButton;
	private JButton btnChooseDbFile;
	private JButton cancelButton;
	private JLabel lblLoading;
	
	/** when there is any loading in processing, do not close the window
	 * @author Christian Landel
	 */
	private boolean loading = false;
	

	/**
	 * Create the dialog.
	 */
	public ImportDatabase()
	{
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setModal(true);
		setTitle("Backup laden");
		setBounds(100, 100, 496, 145);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		textFieldDbFile = new JTextField();
		textFieldDbFile.setEditable(false);
		textFieldDbFile.setBounds(10, 22, 275, 20);
		contentPanel.add(textFieldDbFile);
		textFieldDbFile.setColumns(10);
		
		btnChooseDbFile = new JButton("Datei wählen");
		btnChooseDbFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
		        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		        fc.setSelectedFile(new File(textFieldDbFile.getText()));
		        int returnVal = fc.showOpenDialog(null);
		        
//		        File f;
		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		            setFileName(fc.getSelectedFile());
		            path = getFileName().getPath();
		            textFieldDbFile.setText(path);
		        }
			}
		});
		btnChooseDbFile.setBounds(322, 20, 133, 23);
		contentPanel.add(btnChooseDbFile);
		{
			lblLoading = new JLabel("");
			lblLoading.setVisible(false);
			lblLoading.setIcon(new ImageIcon(ImportDatabase.class.getResource("/de/museum/berleburg/userInterface/dialogs/ajax-loader.gif")));
			lblLoading.setBounds(199, 11, 86, 63);
			contentPanel.add(lblLoading);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Import");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							if (textFieldDbFile.getText().isEmpty()) {
								JOptionPane.showMessageDialog(getContentPane(), "Bitte eine Datei auswählen!");
								return;
							}
							
							invisible();
							lblLoading.setVisible(true);
							new Thread(new Runnable() {
								public void run() {	
									

									loading=true;
									try {
										Access.importDatabase(getFileName());
										dispose();
									} catch (FileNotFoundException e) {
										JOptionPane.showMessageDialog(null, e.getMessage(), "Import-Fehler", JOptionPane.ERROR_MESSAGE);
									} catch (ConnectionException e) {
										JOptionPane.showMessageDialog(null, e.getMessage(), "Import-Fehler", JOptionPane.ERROR_MESSAGE);
									} catch (SQLException e) {
										JOptionPane.showMessageDialog(null, e.getMessage(), "Import-Fehler", JOptionPane.ERROR_MESSAGE);
									}
									finally {
										try {
											Thread.sleep(5000);
										} catch (InterruptedException e) {
											JOptionPane.showMessageDialog(null, e.getMessage(), "Import-Fehler", JOptionPane.ERROR_MESSAGE);
										}
										loading=false;
										
										JOptionPane.showMessageDialog(null, "Der Import wurde abgeschlossen.\nStarten Sie das Programm erneut, damit die Änderungen wirksam werden.", "Import abgeschlossen", JOptionPane.INFORMATION_MESSAGE);
										dispose();
										System.exit(0);
									}
								}
							},"Export UI Thread").start();

						
						
						
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
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
	 * Setting the buttons and the textfield invisible
	 * 
	 * @param text
	 */
	public void invisible(){
		cancelButton.setVisible(false);
		okButton.setVisible(false);
		btnChooseDbFile.setVisible(false);
		textFieldDbFile.setVisible(false);
		
	}
		

	/**
	 * @return the fileName
	 */
	public File getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(File fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * override the dispose function so we have control over it
	 * @author Christian Landel
	 */
	@Override
	public void dispose() {
		if (!loading)
			super.dispose();
	}
	
}
