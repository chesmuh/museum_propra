package de.museum.berleburg.exceptions;

public class AddressNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddressNotFoundException(String s)
    {
        super(s);
    }
}
