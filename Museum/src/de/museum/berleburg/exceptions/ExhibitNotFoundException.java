package de.museum.berleburg.exceptions;

/**
 *
 * @author FSchikowski
 *
 */
public class ExhibitNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExhibitNotFoundException(String s)
    {
        super(s);
    }
}
