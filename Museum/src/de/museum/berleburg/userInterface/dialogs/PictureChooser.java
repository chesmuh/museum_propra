package de.museum.berleburg.userInterface.dialogs; 
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/* @author Way Dat To
 * 
 */

/*
 * Creates a JFileChooser 
 */

/**
 * @author Way Dat To
 * 
 */
@SuppressWarnings("serial")
public class PictureChooser extends JFileChooser {
	File picture;
	private static PictureChooser instance = null;




	public PictureChooser() {
		instance=this;
		setBounds(100, 100, 450, 300);
		{
			JFileChooser fileChooser = new JFileChooser();
			FileFilter filter = new FileNameExtensionFilter(
					"Alle Bildformate (*.BMP;*.GIF;*.JPG;*.JPEG;*.PNG)", "gif",
					"png", "jpg", "jpeg", "bmp");
			FileFilter filterJpg = new FileNameExtensionFilter(
					"JPEG(*.JPG;*.JPEG)", "gif", "png", "jpg", "jpeg", "bmp");

			fileChooser.addChoosableFileFilter(filter);
			fileChooser.addChoosableFileFilter(filterJpg);
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			int returnVal = fileChooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				setFile(selectedFile);

			}

		}
	}
	public static PictureChooser getInstance(){
	if (instance==null){
		instance= new PictureChooser();
	}
	return instance;	
	}
	
	/*
	 * returns the path of the selected File
	 */
	public File getFile() {

		return picture;
	}
	
	public void setFile(File x){
		picture = x ;
		
	}
}