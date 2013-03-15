package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.listeners.SectionListener;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;
import de.museum.berleburg.userInterface.panels.TreeSectionPanel;

public class SectionChoose extends JDialog {

	/**
	 * @author Alexander Adema, Frank Hülsmann
	 */
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();	
	private Long sectionId;
	private Long museumId;
	
	private TreeSectionPanel sectionTree;
	private List<SectionListener> sectionListeners = new LinkedList<SectionListener>();



	/**
	 * Create the dialog.
	 */
	public SectionChoose() {
		/** @author Christian Landel */
		super();
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		init();
	}
	public SectionChoose(Dialog owner) {
		/** @author Christian Landel */
		super(owner);
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		init();
	}
	
	private void init()
	{
		setResizable(false);
		setBounds(100, 100, 304, 402);
		getContentPane().setLayout(new BorderLayout());
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		
		sectionTree = new TreeSectionPanel(false);
		sectionTree.setSize(new Dimension(320, 400));
		sectionTree.setToolTipText("Wählen sie eine Sektion");
		sectionTree.setBounds(0, 0, 298, 341);
		sectionTree.setSelection(TreeMainPanel.getInstance().getTreeSectionPanel().getTree());
		contentPanel.add(sectionTree);
		
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnChoose = new JButton("Auswählen");
				btnChoose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						
						getValuesFromSectionTree();
						
						
						Section sec = null;
						Museum mus = null;
						
						if(sectionId == null || sectionId == 0L)
						{
							try {
								mus = Access.searchMuseumID(museumId);
							} catch (MuseumNotFoundException e1) {
								JOptionPane.showConfirmDialog(SectionChoose.this, e1.getMessage());
							}
							for (SectionListener listener : sectionListeners)
								 listener.event(mus); 
						}
						else
						{
							
							try {
								sec = Access.searchSectionID(sectionId);
							} catch (SectionNotFoundException e1) {
								JOptionPane.showConfirmDialog(SectionChoose.this, e1.getMessage());
							}
							
							for (SectionListener listener : sectionListeners)
								 listener.event(sec); 
						}
						dispose();
						
					}
				});
				btnChoose.setActionCommand("OK");
				buttonPane.add(btnChoose);
				getRootPane().setDefaultButton(btnChoose);
			}
		
		}
	}
	
	public void addListener(SectionListener listener){
   		sectionListeners.add(listener);
	}
	
	/**  --------------------------------------------------------------------------------------- **/
	/**  ---------------------------------- methods -------------------------------------------- **/
	/**  --------------------------------------------------------------------------------------- **/

	/**
	 * Getting all Values from 
	 * 
	 * @param text
	 */
	public void getValuesFromSectionTree(){
		
		try {
			museumId = sectionTree.getMuseumId();
			
		} catch (MuseumNotFoundException e1) {
			JOptionPane.showConfirmDialog(SectionChoose.this, e1.getMessage());
		} 
		
		try {
			sectionId = sectionTree.getSectionId();
		} catch (SectionNotFoundException e1) {
			JOptionPane.showConfirmDialog(SectionChoose.this, e1.getMessage());
		}

		
		
	}
	


}
