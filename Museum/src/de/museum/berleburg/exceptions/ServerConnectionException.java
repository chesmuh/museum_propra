package de.museum.berleburg.exceptions;

import java.sql.SQLException;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class ServerConnectionException extends SQLException
{

    public ServerConnectionException(String msg)
    {
        super(msg);
    }
}
