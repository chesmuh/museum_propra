package de.museum.berleburg.exceptions;

/**
 * 
 * @author Nils Leonhardt
 *
 */
public class DemoVersionException extends Exception {
	private static final long serialVersionUID = -830012699235690569L;

	public DemoVersionException() {
		super("Es dürfen in der Demoversion nicht mehr als 100 Exponate angelegt werden.");
	}
}
