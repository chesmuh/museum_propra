package de.museum.berleburg.exceptions;

/**
 * @author Marco
 * 
 *         The HistoryElementNotFoundException.
 */
public class HistoryElementNotFoundException extends Exception {

	/**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HistoryElementNotFoundException() {
		super("Verlaufselement wurde nicht gefunden.");
	}

}
