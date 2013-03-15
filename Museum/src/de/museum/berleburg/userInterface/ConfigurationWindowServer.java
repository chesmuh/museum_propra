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
 * Server config dialog
 * @author Timo Funke
 *
 */
public class ConfigurationWindowServer extends JDialog {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel pnlButtons = new JPanel();
	private final JPanel pnleditServer = new JPanel();
	
	private final JTextField txtDatabaseName = new JTextField(30);
	private final JTextField txtUsername = new JTextField(30);
	private final JTextField txtPasswort = new JTextField(30);
	private final JLabel lblHost_1 = new JLabel("Host:");
	private final JLabel lblDatabase = new JLabel("Database Name:");
	private final JLabel lblPort = new JLabel("Port:");
	private final JLabel lblUsername = new JLabel("Username:");
	private final JLabel lblPassword = new JLabel("Passwort:");
	

	private final JButton btnCancel = new JButton("Abbruch");
	private final JButton btnOk = new JButton("Ok");
	private final JTextField txtHost = new JTextField(20);
	private  SpinnerNumberModel modelDBPort = new SpinnerNumberModel(3306, 0, 65535,1);
	private JSpinner spnDBPort = new JSpinner(modelDBPort);

	
	/**
	 * change or create the server config
	 * @param changeConfig
	 */
	public ConfigurationWindowServer(Boolean changeConfig) {
		/**
		 * Fills fields with content
		 */
		if (changeConfig) {
			File local = new File(Constants.CONFIGURATION_PATH_LOCAL);
			File backUp = new File(Constants.CONFIGURATION_PATH_SERVER);
			Configuration.loadConfigurations(local, backUp);
			Database serverDB = Configuration.getInstance().getServerDatabase();

			txtHost.setText(serverDB.getHost());
			spnDBPort.setValue(Integer.parseInt(serverDB.getPort()));
			txtDatabaseName.setText(serverDB.getDatabaseName());
			txtUsername.setText(serverDB.getUsername());
			txtPasswort.setText(serverDB.getPassword());
		}
		
		buildWindow();
		
	}
	


	/**
	 * Creates the window
	 */
	public void buildWindow() {
		
		setTitle("Server Datenbank");
		setPreferredSize(new Dimension(450,300));
		setLocation(100, 100);
		setModal(true);
		getContentPane().setLayout(new BorderLayout(0, 0));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		
		pnlButtons.add(btnOk, BorderLayout.EAST);
		pnlButtons.add(btnCancel, BorderLayout.WEST);
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);
		
		
		getContentPane().add(pnleditServer, BorderLayout.CENTER);
		pnleditServer.setLayout(new MigLayout("", "[][][][]", "[][][][][][][][]"));
		
		pnleditServer.add(lblHost_1, "cell 0 1");
		
		pnleditServer.add(lblPort, "cell 0 2");
		
		pnleditServer.add(lblDatabase, "cell 0 3");
		
		pnleditServer.add(lblUsername, "cell 0 4");
		
		pnleditServer.add(lblPassword, "cell 0 5");
		
		pnleditServer.add(txtHost, "cell 1 1,growx");

		pnleditServer.add(spnDBPort, "cell 1 2,growx");
		
		pnleditServer.add(txtDatabaseName, "cell 1 3");
		
		pnleditServer.add(txtUsername, "cell 1 4");
		
		pnleditServer.add(txtPasswort, "cell 1 5");
		
		
						
		
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConfigurationWindowServer.this.dispose();
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
		

		Database serverDB = Configuration.getInstance().getServerDatabase();
		
		serverDB.setHost(host);
		serverDB.setPort(port);
		serverDB.setDatabaseName(name);
		serverDB.setUsername(user);
		serverDB.setPassword(pwd);


		

		File local = new File(Constants.CONFIGURATION_PATH_LOCAL);
		File backUp = new File(Constants.CONFIGURATION_PATH_SERVER);

		try {
			Configuration.saveConfiguration(local,backUp);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(btnOk, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	

}
