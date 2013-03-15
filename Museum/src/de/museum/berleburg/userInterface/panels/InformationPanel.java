package de.museum.berleburg.userInterface.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A JPanel for general Information
 * 
 * 
 * @author Way Dat To
 * 
 */

@SuppressWarnings("serial")
public class InformationPanel extends JPanel {


	/**
	 * Create the panel.
	 */

	private String text = " ";
	private static JLabel lblInformation;
	private static InformationPanel instance = null;

	public InformationPanel() {
		
		instance = this;
		
		setAlignmentY(Component.TOP_ALIGNMENT);
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		lblInformation = new JLabel(text);
		lblInformation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblInformation.setPreferredSize(new Dimension(720, 14));
		lblInformation.setSize(new Dimension(720, 0));
		lblInformation.setMaximumSize(new Dimension(720, 14));
		add(lblInformation);

	}

	public static InformationPanel getInstance() {
		if (instance == null) {
			instance = new InformationPanel();
		}
		return instance;
	}
	
	public void setText(String msg) {
		lblInformation.setText(msg);

	}

	/**
	 * Get the text of the InformationPanel
	 */
	public String getInformation() {
		return lblInformation.getText();

	}

}
