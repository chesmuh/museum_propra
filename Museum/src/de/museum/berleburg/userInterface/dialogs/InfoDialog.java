package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalIconFactory;

import de.museum.berleburg.userInterface.MainGUI;
/**
 * 
 * @author Maximilian Beck
 *
 */
@SuppressWarnings("serial")
public class InfoDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();


	/**
	 * Create the dialog.
	 */
	public InfoDialog() {
		setModal(true);
		setTitle("Information");
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

			{
				JButton cancelButton = new JButton("Schließen");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		
		{
			JPanel infoPane = new JPanel();
			infoPane.setLayout(new BorderLayout());
			getContentPane().add(infoPane, BorderLayout.CENTER);
			{
				Icon icon = MetalIconFactory.getFileChooserHomeFolderIcon();
				JLabel text = new JLabel("Für mehr Informationen besuchen Sie unsere",JLabel.CENTER);
				JLabel link = new JLabel("Homepage",icon, JLabel.CENTER);
				link.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {
												
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
										
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
										
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						browseUrl();
						
					}
				});
				infoPane.add(text,BorderLayout.CENTER);
				infoPane.add(link,BorderLayout.SOUTH);
			}
		}
	}
			public void browseUrl(){
				try
				{
				  Desktop.getDesktop().browse( new URI("http://www.uni-siegen.de/") );
				}
				catch ( Exception e )
				{
					JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
			}
		
		
}

