package de.museum.berleburg.userInterface.dialogs;


import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.userInterface.MainGUI;

public class ProcessDialog extends JDialog implements ProcessCallBack {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar pbarAnzeige;
	private JLabel lblInfo;

	public ProcessDialog(Point point) {
		super();
		init(point);
	}
	
	public ProcessDialog(Dialog owner, Point point) {
		super(owner);
		init(point);
	}

	public ProcessDialog(Frame owner, Point point) {
		super(owner);
		init(point);
	}

	public ProcessDialog(Window owner, Point point) {
		super(owner);
		init(point);
	}

	private void init(Point point) {
		setLocation(point);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		lblInfo = new JLabel("Laden...");
		pbarAnzeige = new JProgressBar(0, 100);
		toFront();
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.getContentPane().add(pbarAnzeige, BorderLayout.CENTER);
		this.getContentPane().add(lblInfo, BorderLayout.SOUTH);
		pbarAnzeige.setValue(0);
		this.pack();
	}

	@Override
	public synchronized void updateProcess(final int percent, final int managerId) {
		
		String info = "Lade %s bitte warten...";
				switch (managerId) {
                                    case Constants.UPDATE_MANAGER_ID:
		                        info = "Synchronisiere...";
		                        break;
                                    case Constants.UPDATE_MANAGER_ID_IMAGE:
		                        info = "Synchronisiere Bilder...";
		                        break;
		                case Constants.ADRESS_MANAGER_ID:
		                        info = String.format(info, "Adressen");
		                        break;
				case Constants.EXHIBIT_MANAGER_ID:
					info = String.format(info, "Exponate");
					break;
				case Constants.MUSEUM_MANAGER_ID:
					info = String.format(info, "Museen");
					break;
				case Constants.IMAGE_MANAGER_ID:
					info = String.format(info, "Bilder");
					break;
				case Constants.ROLE_MANAGER_ID:
					info = String.format(info, "Rollen");
					break;
				case Constants.CONTACT_MANAGER_ID:
					info = String.format(info, "Personen");
					break;
				case Constants.SECTION_MANAGER_ID:
					info = String.format(info, "Sektionen");
					break;
				case Constants.OUTSOURCED_MANAGER_ID:
					info = String.format(info, "Ausstellungen");
					break;
				case Constants.CATEGORY_MANAGER_ID:
					info = String.format(info, "Kategorien");
					break;
				case Constants.LABEL_MANAGER_ID:
					info = String.format(info, "Labels");
					break;
				case Constants.HISTORY_MANAGER_ID:
					info = String.format(info, "Exponaten-Verlauf");
					break;
				default:
					info = String.format(info, "");
				}
				lblInfo.setText(info);
				pbarAnzeige.setValue(percent);
		
	}
}
