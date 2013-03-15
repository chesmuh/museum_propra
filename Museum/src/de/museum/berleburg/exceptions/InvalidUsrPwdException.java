package de.museum.berleburg.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class InvalidUsrPwdException extends SQLException
{
    public InvalidUsrPwdException(String msg)
    {
        super(msg);
    }
}
