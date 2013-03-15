package de.museum.berleburg.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class DuplicateEntryException extends SQLException
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateEntryException(String msg)
    {
        super(msg);
    }
}
