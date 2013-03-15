package de.museum.berleburg.exceptions;

/**
 *
 * @author Benedikt
 *
 */
public class ContactNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContactNotFoundException(String s)
    {
        super(s);
    }
}
