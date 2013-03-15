package de.museum.berleburg.exceptions;

/**
 *
 * @author Anselm
 */
public class ConnectionFailedException extends ConnectionException
{

    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionFailedException(String message)
    {
        super(message);
    }
}
