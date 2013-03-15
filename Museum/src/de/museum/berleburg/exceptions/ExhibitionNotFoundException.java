package de.museum.berleburg.exceptions;

public class ExhibitionNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExhibitionNotFoundException(String s)
    {
        super(s);
    }
}
