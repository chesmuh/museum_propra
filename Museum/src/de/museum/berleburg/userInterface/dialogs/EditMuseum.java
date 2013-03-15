package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;

public class EditMuseum extends JDialog {

	/**
	 * @author Alexander Adema
	 * @author Way Dat To (Just Bugfix)
	 */
	private static final long serialVersionUID = 7311090536091997832L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldMuseumName;
	private long museumid;
	private Museum museum;
	private String museumname;
	private String description;
	private JTextArea textAreaDesxription;

	/**
	 * Create the dialog.
	 */
	public EditMuseum(final long museumid) {
		setModal(true);
		setTitle("Museum bearbeiten");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 480, 370);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblMuseumsname = new JLabel("Name d. Museums:");
			lblMuseumsname.setFont(new Font("Arial", Font.BOLD, 13));
			lblMuseumsname.setBounds(10, 12, 134, 15);
			contentPanel.add(lblMuseumsname);
		}
		{
			textFieldMuseumName = new JTextField();
			textFieldMuseumName.setBounds(154, 10, 242, 19);
			contentPanel.add(textFieldMuseumName);
			textFieldMuseumName.setColumns(10);
		}

		JButton btnChangeAddress = new JButton("Adresse ändern");
		btnChangeAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					museum=Access.searchMuseumID(museumid);
					EditAddress i = new EditAddress(museum.getAddress_id());
					i.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					i.setVisible(true);
				} catch (MuseumNotFoundException e2) {
					JOptionPane.showMessageDialog(null, e2.getMessage(), "Museum nicht gefunden", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnChangeAddress.setBounds(154, 41, 158, 25);
		contentPanel.add(btnChangeAddress);
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 95, 444, 194);
			contentPanel.add(scrollPane);
			
					textAreaDesxription = new JTextArea();
					scrollPane.setViewportView(textAreaDesxription);
		}
		
		JLabel label = new JLabel("Beschreibung(*)");
		label.setFont(new Font("Arial", Font.BOLD, 13));
		label.setBounds(10, 78, 100, 15);
		contentPanel.add(label);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Museum speichern");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						getValues();

						try {
							Access.changeMuseum(museumid, museumname, description);
							dispose();
							
							Museum selected = Access.searchMuseumID(MuseumMainPanel.getInstance().getMuseumTreeNode().getMuseumId());
							MuseumMainPanel.getInstance().setMuseumToSelect(selected);
							MuseumMainPanel.getInstance().refreshComboBox();
							MainGUI.getDetailPanel().setDetails(selected);
							
							
							Object lastDisplayed = MainGUI.getDetailPanel().getLastDisplayed();
							if(lastDisplayed!=null&&lastDisplayed instanceof Museum)
							{
								if(lastDisplayed.equals(Access.searchMuseumID(museumid)))
									MainGUI.getDetailPanel().setDetails(Access.searchMuseumID(museumid));
							}
							
							
							
						} catch (Exception e2) {
							String str;
							if(e2 instanceof InvalidArgumentsException)str = "Fehler beim Speichern!\n"+"("+e2.getMessage()+")\n"+"Möchten Sie den Vorgang wiederholen?";
							else str = "Fehler beim Speichern!\nMöchten Sie den Vorgang wiederholen?";
									
							int reply = JOptionPane.showConfirmDialog(null, str, "Change Exception", JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								
							} else if (reply == JOptionPane.NO_OPTION) {
								dispose();
							}							
							InformationPanel.getInstance().setText("Museum wurde nicht geändert");

						}

					}
				});
				
				JLabel label_1 = new JLabel("(*) Optional");
				buttonPane.add(label_1);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Abbrechen");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		fillboxes(museumid);
	}

	/**
	 * The function to fill the textfields with content from the Database
	 * 
	 * @param text
	 */
	public void fillboxes(Long museumid) {
		Museum mu = null;
		try {
			mu = Access.searchMuseumID(museumid);

		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		}
		textFieldMuseumName.setText(mu.getName());
		textAreaDesxription.setText(mu.getDescription());

	}

	/**
	 * The function to get all values that have been edited
	 * 
	 * @param text
	 */
	public void getValues() {
		museumname = textFieldMuseumName.getText();
		description = textAreaDesxription.getText();
	}

	/**
	 * 
	 * @return museumname
	 */
	public String getMuseumname() {
		return museumname;
	}

	/**
	 * 
	 * @param museumname
	 */
	public void setMuseumname(String museumname) {
		this.museumname = museumname;
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
