package de.museum.berleburg.userInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.Database;


/**
 * Local config dialog
 * @author Nils Leonhardt, Timo Funke
 *
 */
public class ConfigurationWindowLocal extends JDialog {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel pnlButtons = new JPanel();
	private final JPanel pnleditLocal = new JPanel();
	
	private final JTextField txtDatabaseName = new JTextField(30);
	private final JTextField txtUsername = new JTextField(30);
	private final JTextField txtPasswort = new JTextField(30);
	private final JLabel lblHost_1 = new JLabel("Host:");
	private final JLabel lblDatabase = new JLabel("Database Name:");
	private final JLabel lblPort = new JLabel("Port:");
	private final JLabel lblUsername = new JLabel("Username:");
	private final JLabel lblPassword = new JLabel("Passwort:");
	private final JCheckBox chckbxUserfid = new JCheckBox("useRFID");
	private final JLabel lblComport = new JLabel("Comport:");
	private final JButton btnCancel = new JButton("Abbruch");
	private final JButton btnOk = new JButton("Ok");
	private final JTextField txtHost = new JTextField(20);
	private  SpinnerNumberModel modelDBPort = new SpinnerNumberModel(3306, 0, 65535,1);
	private JSpinner spnDBPort = new JSpinner(modelDBPort);
	private final SpinnerNumberModel modelComPort = new SpinnerNumberModel(3306, 0, 65535,1);
	private final JSpinner spnComPort = new JSpinner(modelComPort);
	
	
	private boolean useRfid = false;
	private boolean showSafeMessage;
	
	/**
	 * change or create the local config
	 * @param changeConfig
	 */
	public ConfigurationWindowLocal(Boolean changeConfig) {
		/**
		 * Fills fields with content
		 */
		if (changeConfig) {
			File local = new File(Constants.CONFIGURATION_PATH_LOCAL);
			File backUp = new File(Constants.CONFIGURATION_PATH_SERVER);
			Configuration.loadConfigurations(local, backUp);
			Database localDB = Configuration.getInstance().getLocalDatabase();
			
							
			txtHost.setText(localDB.getHost());
			int port = 3306;
                        try
                        {
                            port = Integer.parseInt(localDB.getPort());
                        }
                        catch (NumberFormatException e)
                        {
                            JOptionPane.showMessageDialog(null, "Ungültiger Datenbank Port! ("+localDB.getPort()+")\n"
                                    + "Port wurde auf 3306 gesetzt.",
                                    "Configurations-Fehler", JOptionPane.ERROR_MESSAGE);
                        }
                        spnDBPort.setValue(port);
			txtDatabaseName.setText(localDB.getDatabaseName());
			txtUsername.setText(localDB.getUsername());
			txtPasswort.setText(localDB.getPassword());
			
			showSafeMessage = changeConfig;
			
		}
		
		buildWindow();
		
	}
	


	/**
	 * Creates the window
	 */
	public void buildWindow() {
		
		setTitle("Lokale Datenbank");
		setPreferredSize(new Dimension(450,300));
		setLocation(100, 100);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		
		pnlButtons.add(btnOk, BorderLayout.EAST);
		pnlButtons.add(btnCancel, BorderLayout.WEST);
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		
		
		getContentPane().add(pnleditLocal, BorderLayout.CENTER);
		pnleditLocal.setLayout(new MigLayout("", "[][][][]", "[][][][][][][][]"));
		
		pnleditLocal.add(lblHost_1, "cell 0 1");
		
		pnleditLocal.add(lblPort, "cell 0 2");
		
		pnleditLocal.add(lblDatabase, "cell 0 3");
		
		pnleditLocal.add(lblUsername, "cell 0 4");
		
		pnleditLocal.add(lblPassword, "cell 0 5");
		
		pnleditLocal.add(chckbxUserfid, "cell 0 6");
		
		pnleditLocal.add(lblComport, "cell 0 7");
		
		pnleditLocal.add(txtHost, "cell 1 1,growx");

		pnleditLocal.add(spnDBPort, "cell 1 2,growx");
		
		pnleditLocal.add(txtDatabaseName, "cell 1 3");
		
		pnleditLocal.add(txtUsername, "cell 1 4");
		
		pnleditLocal.add(txtPasswort, "cell 1 5");
		
		pnleditLocal.add(spnComPort, "cell 1 7,growx");
		
		lblComport.setVisible(isUseRfid());
		spnComPort.setVisible(isUseRfid());
		
		chckbxUserfid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					setUseRfid(chckbxUserfid.isSelected());
					lblComport.setVisible(isUseRfid());
					spnComPort.setVisible(isUseRfid());
															
			}
		});
		
				
		
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationWindowLocal.this.dispose();
			}
		});
		
		btnOk.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				saveConfiguration();				
				
				dispose();
				
			}
		});
		
		pack();
		setVisible(true);
		
	}
	
	/**
	 * saves the configuration
	 */
	public void saveConfiguration() {
		
		String host = txtHost.getText();
		String port = Integer.toString((int) spnDBPort.getValue());
		String pwd = txtPasswort.getText();
		String user = txtUsername.getText();
		String name = txtDatabaseName.getText();
		
		Database localDB = Configuration.getInstance().getLocalDatabase();
		
		localDB.setHost(host);
		localDB.setPort(port);
		localDB.setDatabaseName(name);
		localDB.setUsername(user);
		localDB.setPassword(pwd);

		
		File local = new File(Constants.CONFIGURATION_PATH_LOCAL);
		File backUp = new File(Constants.CONFIGURATION_PATH_SERVER);

		try {
			Configuration.saveConfiguration(local,backUp);
		} catch (FileNotFoundException e) {
			//JOptionPane.showMessageDialog(btnOk, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, "Datei wurde nicht gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (showSafeMessage) {
			JOptionPane.showMessageDialog(btnOk, "Damit die Änderungen an der lokalen Datenbank wirksam werden, muss das Programm neu gestartet werden!");
		}
		
	}



	/**
	 * @return the useRfid
	 */
	public boolean isUseRfid() {
		return useRfid;
	}

	/**
	 * @param useRfid the useRfid to set
	 */
	public void setUseRfid(boolean useRfid) {
		this.useRfid = useRfid;
	}
	

}
