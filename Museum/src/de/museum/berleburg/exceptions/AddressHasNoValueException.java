package de.museum.berleburg.exceptions;

public class AddressHasNoValueException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddressHasNoValueException(String msg)
    {
        super(msg);
    }
}
