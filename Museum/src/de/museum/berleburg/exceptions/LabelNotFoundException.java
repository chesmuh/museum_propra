package de.museum.berleburg.exceptions;

/**
 * @author Marco
 * 
 *         The LabelNotFoundException.
 */
public class LabelNotFoundException extends Exception {

	public LabelNotFoundException(String label) {
		super("Label wurde nicht gefunden: " + label);
	}

}
