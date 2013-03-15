package de.museum.berleburg.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class DBAccessDeniedException extends SQLException
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DBAccessDeniedException(String msg)
    {
        super(msg);
    }
}
