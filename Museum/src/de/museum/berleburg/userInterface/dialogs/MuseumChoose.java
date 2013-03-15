package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;
import de.museum.berleburg.userInterface.panels.TreeNodeObject;

/**
 * Window on startup to choose the museum.
 * 
 * 
 * @author Maximilian Beck, Caroline Bender, Jochen Saßmannshausen
 * 
 */
public class MuseumChoose extends JDialog {

	private static final long serialVersionUID = 4037029538619097150L;
	private JComboBox<TreeNodeObject> comboBoxMuseum;
	private Long museumId;
	private TreeNodeObject museumChoose = new TreeNodeObject(
			"<MUSEUM W\u00C4HLEN>");
	private Museum toSet = null;
	private JLabel choose;
	private JButton ok;
	private JButton loadbackup;
	private final JPanel contentPanel = new JPanel();

	public MuseumChoose(JFrame frame) {
		super(frame);
		setModal(true);
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setTitle("Museumsauswahl");
		setBounds(100, 100, 300, 135);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		getContentPane().add(contentPanel);
		this.setResizable(false);
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

		ok = new JButton("OK");
		loadbackup = new JButton("Backup laden");
		
		ok.setEnabled(false);
		choose = new JLabel("Bitte wählen Sie ein Museum aus:");
		comboBoxMuseum = new JComboBox<TreeNodeObject>();

		refreshComboBox();

		comboBoxMuseum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

					TreeNodeObject o = (TreeNodeObject) comboBoxMuseum
							.getSelectedItem();
					if(o!=null){
					if (o!=null&&o.getMuseumId() == null
							|| o!=null&&comboBoxMuseum.getSelectedItem().equals(
									museumChoose)) {
						setMuseumId(null);
						return;
					} else
						setMuseumId(o.getMuseumId());
					}
					if (comboBoxMuseum.getSelectedItem() != museumChoose) {
						comboBoxMuseum.removeItem(museumChoose);
					}
					if (getMuseumId() == null)
						ok.setEnabled(false);
					else
						ok.setEnabled(true);
				}
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

					MuseumMainPanel.getInstance().setMuseumTreeNode(
							MuseumChoose.this.getMuseumTreeNode());
					MuseumMainPanel.getInstance().setMuseumId(
							MuseumChoose.this.getMuseumId());
					try {
						MuseumMainPanel.getInstance().setMuseumToSelect(
								Access.searchMuseumID(museumId));
						MuseumMainPanel.getInstance().refreshComboBox();
						TreeMainPanel.getInstance().refreshTree();
						if(MainGUI.getDetailPanel()!=null)MainGUI.getDetailPanel().setDetails(
								Access.searchMuseumID(museumId));
					} catch (MuseumNotFoundException e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					}
					MuseumChoose.this.dispose();

				}
			}
		});
		loadbackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					setAlwaysOnTop(false);
					ImportDatabase i = new ImportDatabase();
					i.setVisible(true);
					i.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					setAlwaysOnTop(true);
				}
			}
		});

		
		
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);

		sl_contentPanel.putConstraint(SpringLayout.NORTH, choose, 12, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, choose, 12, SpringLayout.WEST, contentPanel);
		//choose.setFont(new Font("Arial", Font.BOLD, 13));
		contentPanel.add(choose, BorderLayout.NORTH);
		
		sl_contentPanel.putConstraint(SpringLayout.NORTH, comboBoxMuseum, 36, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, comboBoxMuseum, 12, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, comboBoxMuseum, -12, SpringLayout.EAST, contentPanel);
		contentPanel.add(comboBoxMuseum, BorderLayout.CENTER);
		
		sl_contentPanel.putConstraint(SpringLayout.NORTH, ok, 70, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, ok, 12, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, ok, -156, SpringLayout.EAST, contentPanel);
		contentPanel.add(ok, BorderLayout.SOUTH);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, loadbackup, 70, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, loadbackup, 156, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, loadbackup, -12, SpringLayout.EAST, contentPanel);
		contentPanel.add(loadbackup, BorderLayout.SOUTH);
			
		

	}

	/**
	 * Refreshes the combobox
	 */
	public void refreshComboBox() {
		ArrayList<Museum> allmuseums = Access.getAllMuseums();

		if (allmuseums.isEmpty()) {

			CreateMuseum dialog = new CreateMuseum();
			dialog.setVisible(true);

			MuseumMainPanel.getInstance().refreshComboBox();
			dispose();

			refreshComboBox();
		} else {

			long selectedMuseum = toSet != null ? toSet.getId() : 0;
			comboBoxMuseum.removeAllItems();

			for (Museum i : allmuseums) {
				TreeNodeObject o = new TreeNodeObject(i.getName());
				o.setMuseumId(i.getId());
				comboBoxMuseum.addItem(o);
				if (i.getId().equals(selectedMuseum) && toSet != null) {
					comboBoxMuseum.setSelectedItem(o);
					setMuseumId(o.getMuseumId());
				}

			}
		}
		if (toSet == null) {
			if (comboBoxMuseum.getSelectedItem() != museumChoose)
				comboBoxMuseum.addItem(museumChoose);
			comboBoxMuseum.setSelectedItem(museumChoose);
			this.museumChoose.setMuseumId(null);
		}
		toSet = null;
	}

	
	/* Getter */
	
	public Long getMuseumId() {
		return museumId;
	}

	public JComboBox<TreeNodeObject> getComboBoxMuseum() {
		return comboBoxMuseum;
	}

	public String getMuseumName() {

		TreeNodeObject o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();
		String museumName = o.getName();
		return museumName;
	}

	public TreeNodeObject getMuseumTreeNode() {
		TreeNodeObject o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();

		return o;
	}

	/* Setter */
	
	public void setMuseumTreeNode(TreeNodeObject museumTreeNode) {
		comboBoxMuseum.setSelectedItem(museumTreeNode);
	}

	public void setMuseumToSelect(Museum museum) {
		toSet = museum;
	}
	
	public void setMuseumId(Long id) {
		this.museumId = id;
	}
}
