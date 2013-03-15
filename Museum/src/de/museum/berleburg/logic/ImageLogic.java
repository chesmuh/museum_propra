package de.museum.berleburg.logic;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;

/**
 * 
 * @author Benedikt
 *
 */

public class ImageLogic {

	/**
	 * Checks the bounds of the image and scales it if it is too big. 
	 * 
	 * @param toCompress
	 * @throws IOException
	 * @throws IntegrityException
	 */
	public static byte[] checkAndScaleBounds(byte[] toCompress)
			throws IOException, IntegrityException {
		int maxBounds = 500;
		ImageIcon picture = new ImageIcon(toCompress);
		java.awt.Image image = picture.getImage();
		int newWidth = image.getWidth(null), newHeight = image.getHeight(null);
		if (image.getHeight(null) > maxBounds
				|| image.getWidth(null) > maxBounds) {

			if (image.getHeight(null) >= image.getWidth(null)) {
				newHeight = maxBounds;
				newWidth = -1;
			} else {
				newHeight = -1;
				newWidth = maxBounds;
			}
			return compressPicture(picture, image, newWidth, newHeight);
		} else {
			return compressPicture(picture, image, newWidth, newHeight);
		}

	}

	/**
	 * Compresses the picture if it is too big. 
	 * 
	 * @param toCompress the file of the picture
	 * @throws IOException
	 * @throws IntegrityException
	 */
	public static byte[] compressPicture(ImageIcon picture,
			java.awt.Image image, int newWidth, int newHeight)
			throws IOException, IntegrityException {
		image = image.getScaledInstance(newWidth, newHeight,
				java.awt.Image.SCALE_SMOOTH);
		int wait = 0;
		while (image.getWidth(null) <= 0 && image.getHeight(null) <= 0)
			try {
				java.lang.Thread.sleep(1);
				wait++;
				if (wait > 20000)
					throw new IntegrityException(image,
							"Zeitüberschreitung beim Skalieren des Bildes! ");
			} catch (InterruptedException e) {
			}
		BufferedImage bimage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics g = bimage.createGraphics();
		// paint the Icon to the BufferedImage.
		picture = new ImageIcon(image);
		picture.paintIcon(null, g, 0, 0);
		g.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bimage, "jpg", baos);
		return baos.toByteArray();
	}

	/**
	 * Updates the image. 
	 * @param toSave
	 * @throws SQLException
	 */
	public static void savePicture(Image toSave) throws ConnectionException {
		DataAccess.getInstance().update(toSave);
	}
}
