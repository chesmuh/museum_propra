package de.museum.berleburg.userInterface.panels;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class TableMainPanel extends JPanel {

	/**
	 * @author Timo Funke, Frank Hülsmann
	 * 
	 */
	private static final long serialVersionUID = -4272905396833415288L;

	/**
	 * Create the panel.
	 */
	public TableMainPanel() {
		setLayout(new BorderLayout(0, 0));
		
		TablePanel northpanel = TablePanel.getInstance();//new TablePanel();
		add(northpanel, BorderLayout.CENTER);
		
		JPanel southpanel = new TableButtonPanel();
		add(southpanel, BorderLayout.SOUTH);
		

	}

}
