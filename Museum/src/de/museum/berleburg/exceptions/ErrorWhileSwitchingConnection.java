package de.museum.berleburg.exceptions;

import java.sql.SQLException;

public class ErrorWhileSwitchingConnection extends SQLException
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ErrorWhileSwitchingConnection(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
