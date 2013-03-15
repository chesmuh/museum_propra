package de.museum.berleburg.userInterface;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionFailedException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.dialogs.CheckExpiredOutsourcedUtil;
import de.museum.berleburg.userInterface.dialogs.CreateAddress;
import de.museum.berleburg.userInterface.dialogs.CreateCategory;
import de.museum.berleburg.userInterface.dialogs.CreateContact;
import de.museum.berleburg.userInterface.dialogs.CreateExhibition;
import de.museum.berleburg.userInterface.dialogs.CreateLoan;
import de.museum.berleburg.userInterface.dialogs.CreateMuseum;
import de.museum.berleburg.userInterface.dialogs.CreateRole;
import de.museum.berleburg.userInterface.dialogs.CreateSection;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;
import de.museum.berleburg.userInterface.dialogs.ExportDatabase;
import de.museum.berleburg.userInterface.dialogs.ImportDatabase;
import de.museum.berleburg.userInterface.dialogs.InfoDialog;
import de.museum.berleburg.userInterface.dialogs.MuseumChoose;
import de.museum.berleburg.userInterface.dialogs.ProcessDialog;
import de.museum.berleburg.userInterface.dialogs.ShowAddresses;
import de.museum.berleburg.userInterface.dialogs.ShowContacts;
import de.museum.berleburg.userInterface.dialogs.ShowExpired;
import de.museum.berleburg.userInterface.dialogs.ShowExpiredOutsourced;
import de.museum.berleburg.userInterface.dialogs.ShowRoles;
import de.museum.berleburg.userInterface.dialogs.SyncDialog;
import de.museum.berleburg.userInterface.panels.DetailPanel;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.SearchPanel;
import de.museum.berleburg.userInterface.panels.TableMainPanel;
import de.museum.berleburg.userInterface.panels.ToolbarPanel;

/**
 * This is the main Window for the GUI
 * 
 * 
 * @author Way Dat To
 * 
 */

public class MainGUI extends JFrame {

	private static ToolbarPanel toolbarPanel;
	private static MuseumMainPanel museumMainPanel;
	private static TableMainPanel tableMainPanel;
	private static DetailPanel detailPanel;
	private static InformationPanel informationPanel;
	private static SearchPanel searchPanel;
	private static MainGUI frame;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		// try {
		// InsertTestExhibits.insertTestData(1000);
		// } catch (SQLException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		while (!new File(Constants.CONFIGURATION_PATH_LOCAL).exists()) {
			try {
				new ConfigurationWindowLocal(false);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		ProcessDialog dialog = new ProcessDialog(new Point(100, 100));
		try {
			dialog.setVisible(true);
			Access.startSystem(dialog);
			frame = new MainGUI();
			frame.setVisible(true);

			if (CheckExpiredOutsourcedUtil.checkExpiredOutsourced()) {
				ShowExpiredOutsourced sEO = new ShowExpiredOutsourced();
				sEO.setVisible(true);
			}
		} catch (ConnectionException e) {
			JOptionPane.showMessageDialog(null,
					"Konnte keine Verbindung zur Datenbank herstellen!\n\n"
							+ e.getCause().getMessage(), "Datenbankfehler",
					JOptionPane.ERROR_MESSAGE);
			dialog.dispose();
			new ConfigurationWindowLocal(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dialog.dispose();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		try {
			DataAccess.getInstance().stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public MainGUI() {
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						MainGUI.class
								.getResource("/de/museum/berleburg/userInterface/logo.png")));

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {

		}

		ArrayList<Museum> allmuseums = Access.getAllMuseums();
		if (allmuseums.isEmpty()) {
			CreateMuseum dialog = new CreateMuseum();
			dialog.setVisible(true);
		} else {
			MuseumChoose musDialog = new MuseumChoose(frame);
			musDialog.setVisible(true);
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				int reply = JOptionPane.showConfirmDialog(null,
						"Wollen Sie das Programm beenden?",
						"Programm schließen", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					System.exit(0);
				} else if (reply == JOptionPane.NO_OPTION) {
				}
			}
		});

		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(1024, 768));
		setTitle("Museumsverwaltung");

		// setBounds(100, 100, 731, 563);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);

		JMenuItem mntmNeuesExhibit = new JMenuItem("Neues Exponat");
		mntmNeuesExhibit
				.setToolTipText("Öffnet ein neues Fenster zum Erstellen eines neuen Exponats");
		mnDatei.add(mntmNeuesExhibit);

		mntmNeuesExhibit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					EditExhibit dialog = new EditExhibit();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);

					// CreateExhibit.enableFields(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		JMenuItem mntmNeueKategorie = new JMenuItem("Neue Kategorie");
		mntmNeueKategorie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateCategory dialog = new CreateCategory();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);

			}
		});
		mnDatei.add(mntmNeueKategorie);

		JMenuItem mntmNeueSection = new JMenuItem("Neue Sektion");
		mnDatei.add(mntmNeueSection);
		mntmNeueSection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					CreateSection dialog = new CreateSection();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		JMenuItem mntmNeueAusstellung = new JMenuItem("Neue Ausstellung");
		mntmNeueAusstellung.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CreateExhibition dialog = new CreateExhibition(false);
				dialog.setVisible(true);
			}
		});
		mnDatei.add(mntmNeueAusstellung);

		JMenuItem mntmNeueLeihgabe = new JMenuItem("Neue Leihgabe");
		mntmNeueLeihgabe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateLoan createLoan = new CreateLoan(false);
				createLoan.setVisible(true);
			}
		});
		mnDatei.add(mntmNeueLeihgabe);

		JMenuItem mntmNeuesMuseum = new JMenuItem("Neues Museum");
		mnDatei.add(mntmNeuesMuseum);

		mntmNeuesMuseum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					CreateMuseum dialog = new CreateMuseum();
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(0, 1));
		mnDatei.add(separator);

		JMenuItem mntmMuseumExportieren = new JMenuItem("Museum exportieren");
		mntmMuseumExportieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ExportDatabase edb = new ExportDatabase();
					edb.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					edb.setVisible(true);
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});

		JMenuItem mntmNeueKontaktperson = new JMenuItem("Neue Kontaktperson");
		mnDatei.add(mntmNeueKontaktperson);
		mntmNeueKontaktperson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateContact createContact = new CreateContact();
				createContact.setVisible(true);
			}

		});

		JMenuItem mntmNeueRolle = new JMenuItem("Neue Rolle");
		mnDatei.add(mntmNeueRolle);

		// Rechtschreibfehler berichtigt (Ad(d)resse!!)
		JMenuItem mntmNeueAddresse = new JMenuItem("Neue Adresse");
		mntmNeueAddresse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CreateAddress createAddress = new CreateAddress();
				createAddress.setVisible(true);
			}
		});
		mnDatei.add(mntmNeueAddresse);

		JSeparator separator_4 = new JSeparator();
		mnDatei.add(separator_4);
		// mnDatei.add(mntmMuseumExportieren);
		mntmNeueRolle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CreateRole createRole = new CreateRole();
				createRole.setVisible(true);
			}

		});

		JMenuItem mntmMuseumImportieren = new JMenuItem("Museum importieren");
		mntmMuseumImportieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDatabase i = new ImportDatabase();
				i.setVisible(true);
				i.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}
		});
		// mnDatei.add(mntmMuseumImportieren);

		JMenuItem mntmBackupErstellen = new JMenuItem("Backup erstellen");
		mntmBackupErstellen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDatabase i = new ExportDatabase();
				i.setVisible(true);
				i.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			}
		});
		mnDatei.add(mntmBackupErstellen);

		JMenuItem mntmBackupLaden = new JMenuItem("Backup laden");
		mntmBackupLaden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDatabase importDatabase = new ImportDatabase();
				importDatabase.setVisible(true);

			}
		});
		mnDatei.add(mntmBackupLaden);

		JMenuItem mntmCommit = new JMenuItem("Commit");
		mntmCommit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					Configuration.getInstance().getServerConnection();
				} catch (ConnectionFailedException ex) {
					JOptionPane
							.showMessageDialog(
									null,
									"Konnte Verbindung zur Serverdatenbank nicht herstellen!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
					return;
				}

				new Thread(new Runnable() {

					@Override
					public void run() {
						SyncDialog dialog = new SyncDialog(true, MainGUI.this,
								true);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					}
				}).start();

			}
		});
		mnDatei.add(mntmCommit);

		JMenuItem mntmUpdate = new JMenuItem("Update");
		mntmUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Configuration.getInstance().getServerConnection();
				} catch (ConnectionFailedException ex) {
					JOptionPane
							.showMessageDialog(
									null,
									"Konnte Verbindung zur Serverdatenbank nicht herstellen!",
									"Datenbankfehler",
									JOptionPane.ERROR_MESSAGE);
					return;
				}
				new Thread(new Runnable() {

					@Override
					public void run() {

						new SyncDialog(false,
								MainGUI.this, true);
					}
				}).start();

			}
		});
		mnDatei.add(mntmUpdate);

		JSeparator separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(0, 1));
		mnDatei.add(separator_1);

		JMenuItem mntmSchlieen = new JMenuItem("Schlie\u00DFen");
		mntmSchlieen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (JOptionPane.showConfirmDialog(null,
						"Wollen Sie das Programm beenden?",
						"Programm schließen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					dispose();
				}
			}
		});
		mnDatei.add(mntmSchlieen);

		// JMenu mnBearbeiten = new JMenu("Bearbeiten");
		// menuBar.add(mnBearbeiten);
		//
		// JMenuItem mntmNewMenuItem_1 = new JMenuItem("Zur\u00FCck");
		// mnBearbeiten.add(mntmNewMenuItem_1);
		//
		// JMenuItem mntmNewMenuItem = new JMenuItem("Vorw\u00E4rts");
		// mnBearbeiten.add(mntmNewMenuItem);
		//
		// JSeparator separator_2 = new JSeparator();
		// separator_2.setPreferredSize(new Dimension(0, 1));
		// mnBearbeiten.add(separator_2);
		//
		// JMenuItem mntmAusschneiden = new JMenuItem("Ausschneiden");
		// mnBearbeiten.add(mntmAusschneiden);
		//
		// JMenuItem mntmKopieren = new JMenuItem("Kopieren");
		// mnBearbeiten.add(mntmKopieren);
		//
		// JMenuItem mntmEinfgen = new JMenuItem("Einfügen");
		// mnBearbeiten.add(mntmEinfgen);
		//
		// JSeparator separator_3 = new JSeparator();
		// separator_3.setPreferredSize(new Dimension(0, 1));
		// mnBearbeiten.add(separator_3);
		//
		// JMenuItem mntmVerleihen = new JMenuItem("Verleihen");
		// mnBearbeiten.add(mntmVerleihen);
		//
		// JMenuItem mntmLschen = new JMenuItem("Löschen");
		// mnBearbeiten.add(mntmLschen);

		JMenu mnAnzeigen = new JMenu("Anzeigen");
		menuBar.add(mnAnzeigen);

		JMenuItem mntmAlleKontakteAnzeigen = new JMenuItem(
				"Alle Kontakte anzeigen");
		mntmAlleKontakteAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowContacts showContacts = new ShowContacts(MainGUI.this, true);
				showContacts.setVisible(true);
			}
		});
		mnAnzeigen.add(mntmAlleKontakteAnzeigen);

		JMenuItem mntmAlleRollenAnzeigen = new JMenuItem("Alle Rollen anzeigen");
		mntmAlleRollenAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				ShowRoles showRoles = new ShowRoles(MainGUI.this, true);
				showRoles.setVisible(true);
			}
		});
		mnAnzeigen.add(mntmAlleRollenAnzeigen);

		JMenuItem mntmAlleAddressenAnzeigen = new JMenuItem(
				"Alle Adressen anzeigen");
		mntmAlleAddressenAnzeigen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowAddresses showAddresses = new ShowAddresses(MainGUI.this,
						true);
				showAddresses.setVisible(true);
			}
		});
		mnAnzeigen.add(mntmAlleAddressenAnzeigen);

		JMenuItem mntmAlleAbgelaufeneExponate = new JMenuItem(
				"Alle abgelaufenen Exponate anzeigen");
		mntmAlleAbgelaufeneExponate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ShowExpired showExpired = new ShowExpired(MainGUI.this, false);
				showExpired.setVisible(true);
			}
		});

		JMenuItem mntmAlleAbgelaufeneAusstellungenleihgabe = new JMenuItem(
				"Alle abgelaufenen Ausstellungen/Leihgabe anzeigen");
		mntmAlleAbgelaufeneAusstellungenleihgabe
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						ShowExpiredOutsourced showExpiredOutsourced = new ShowExpiredOutsourced();
						showExpiredOutsourced.setVisible(true);
					}
				});
		mnAnzeigen.add(mntmAlleAbgelaufeneAusstellungenleihgabe);
		mnAnzeigen.add(mntmAlleAbgelaufeneExponate);

		// JMenu mnSuche = new JMenu("Suche");
		// menuBar.add(mnSuche);

		// JMenuItem mntmErweiterteSuche = new JMenuItem("Erweiterte Suche");
		// mnSuche.add(mntmErweiterteSuche);

		JMenuItem mntmAbgelaufeneExponate = new JMenuItem(
				"Abgelaufene Exponate");
		mntmAbgelaufeneExponate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowExpired showExpired = new ShowExpired(MainGUI.this, false);
				showExpired.setVisible(true);
			}
		});
		// mnSuche.add(mntmAbgelaufeneExponate);

		JMenu mnFenster = new JMenu("Einstellungen");
		menuBar.add(mnFenster);

		JMenuItem mntmLocalDBConfiguration = new JMenuItem("Lokale Datenbank");
		mntmLocalDBConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConfigurationWindowLocal(true);
			}
		});
		mnFenster.add(mntmLocalDBConfiguration);

		JMenuItem mntmServerDBConfiguration = new JMenuItem("Server Datenbank");
		mntmServerDBConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					new ConfigurationWindowServer(true);
				} catch (Exception e1) {
					new ConfigurationWindowServer(false);
				}
			}
		});
		mnFenster.add(mntmServerDBConfiguration);

		JMenu mnHilfe = new JMenu("Hilfe");
		menuBar.add(mnHilfe);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[grow]"));

		JMenuItem mntmHelp = new JMenuItem("Benutzerhandbuch anzeigen");
		mnHilfe.add(mntmHelp);
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File("data/manual.pdf"));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							"Datei konnte nicht gefunden werden!", "Fehler",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		JMenuItem mntmInfo = new JMenuItem("Über");
		mnHilfe.add(mntmInfo);
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InfoDialog dialog = new InfoDialog();
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, "cell 0 0,grow");

		JPanel mainPanel = new JPanel();
		scrollPane.setViewportView(mainPanel);
		mainPanel.setLayout(new MigLayout("", "[grow,fill]",
				"[:18px:40px,grow][480:n,grow][::18px,grow,center]"));

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setDividerSize(0); //XXX hab mal die Größe auf 0 gesetzt, das Ding sieht so wie es ist scheiße aus, Timo
		splitPane_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		splitPane_2.setAlignmentY(Component.CENTER_ALIGNMENT);
		splitPane_2.setPreferredSize(new Dimension(160, 40));
		splitPane_2.setMaximumSize(new Dimension(2147483647, 40));
		splitPane_2.setMinimumSize(new Dimension(160, 40));
		mainPanel.add(splitPane_2, "cell 0 0,grow");

		searchPanel = new SearchPanel();
		searchPanel.setPreferredSize(new Dimension(400, 40));
		splitPane_2.setRightComponent(searchPanel);

		toolbarPanel = new ToolbarPanel();
		splitPane_2.setLeftComponent(toolbarPanel);
		splitPane_2.setDividerLocation(200);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setAutoscrolls(true);
		mainPanel.add(splitPane, "cell 0 1,grow");

		museumMainPanel = MuseumMainPanel.getInstance();
		museumMainPanel.setPreferredSize(new Dimension(480, 480));
		museumMainPanel.setMinimumSize(new Dimension(240, 0));
		museumMainPanel.setMaximumSize(new Dimension(480, 32767));
		museumMainPanel.setAlignmentY(0.0f);
		museumMainPanel.setAlignmentX(0.0f);
		splitPane.setLeftComponent(museumMainPanel);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setPreferredSize(new Dimension(640, 480));
		splitPane_1.setResizeWeight(1.0);
		splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane_1.setMinimumSize(new Dimension(480, 0));
		splitPane.setRightComponent(splitPane_1);

		tableMainPanel = new TableMainPanel();
		tableMainPanel.setPreferredSize(new Dimension(480, 360));
		tableMainPanel.setMinimumSize(new Dimension(480, 240));
		tableMainPanel.setAlignmentY(0.0f);
		tableMainPanel.setAlignmentX(0.0f);
		splitPane_1.setLeftComponent(tableMainPanel);

		detailPanel = new DetailPanel();
		detailPanel.setPreferredSize(new Dimension(0, 200));
		detailPanel.setMaximumSize(new Dimension(2147483647, 360));
		detailPanel.setMinimumSize(new Dimension(600, 200));
		detailPanel.setAlignmentY(0.0f);
		detailPanel.setAlignmentX(0.0f);
		splitPane_1.setRightComponent(detailPanel);
		splitPane.setDividerLocation(280);

		JPanel panel = new JPanel();
		panel.setAlignmentY(0.0f);
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(panel, "cell 0 2,grow");

		informationPanel = new InformationPanel();
		FlowLayout flowLayout_1 = (FlowLayout) informationPanel.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);

		panel.add(informationPanel);

	}

	public static MainGUI getFrame() {
		return frame;
	}

	public static MuseumMainPanel getMuseumMainPanel() {
		return museumMainPanel;
	}

	public static ToolbarPanel getToolbarPanel() {
		return toolbarPanel;
	}

	public static TableMainPanel getTableMainPanel() {
		return tableMainPanel;
	}

	public static InformationPanel getInformationPanel() {
		return informationPanel;
	}

	public static SearchPanel getSearchPanel() {
		return searchPanel;
	}

	public static DetailPanel getDetailPanel() {
		return detailPanel;
	}

}
