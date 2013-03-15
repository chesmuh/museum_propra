package de.museum.berleburg.exceptions;

/**
 * thrown if integrity conditions, like unique naming, are broken
 * @author Christian Landel
 * 
 */
public class IntegrityException extends Exception {
	private Object source;
	/**
	 * @param source the object where the problem arose from
	 * @param text a description of the problem
	 */
	public IntegrityException (Object source, String text) {
		super(text);
		this.source=source;
	}
	public Object getSource() {
		return source;
	}
}
