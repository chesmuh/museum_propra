package de.museum.berleburg.exceptions;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class DatabaseDriverNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseDriverNotFoundException(String msg)
    {
        super(msg);
    }
}
