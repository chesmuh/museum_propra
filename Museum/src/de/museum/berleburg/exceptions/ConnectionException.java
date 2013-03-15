package de.museum.berleburg.exceptions;

/**
 *
 * @author Anselm
 */
public abstract class ConnectionException extends Exception
{

    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionException(String message)
    {
        super(message);
    }
}
