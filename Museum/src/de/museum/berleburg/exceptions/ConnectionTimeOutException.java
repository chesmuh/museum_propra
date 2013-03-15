package de.museum.berleburg.exceptions;

import java.sql.SQLTimeoutException;

/**
 * Gets thrown on SQLTimeoutException
 *
 * @author Anselm
 */
public class ConnectionTimeOutException extends ConnectionException
{

    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionTimeOutException(String message, SQLTimeoutException e)
    {
        super(message, e);
    }
}
