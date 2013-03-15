package de.museum.berleburg.exceptions;

import java.sql.SQLException;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

import de.museum.berleburg.datastorage.Configuration;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class ExceptionHandler
{
//TODO i am incomplete!
    public static void paraphraseException(Exception exn) throws SQLException
    {
        Configuration config = Configuration.getInstance();

        String message = exn.getMessage();

        if ((exn instanceof SQLException))
        {
            if ((exn instanceof CommunicationsException))
            {
                if (message.contains("The driver has not received any packets from the server."))
                {
                    throw new ServerConnectionException(message);
                }
            }
            else
            {
                if (message.equals("Access denied for user '" + config.getLocalDatabase().getUsername()
                        + "'@'" + config.getLocalDatabase().getHost() + "' (using password: YES)"))
                {
                    throw new InvalidUsrPwdException(message);
                }
                else if (message.equals("Access denied for user '" + config.getLocalDatabase().getUsername()
                        + "'@'" + config.getServerDatabase().getHost() + "' to database '" + config.getLocalDatabase().getDatabaseName() + "'"))
                {
                    throw new DBAccessDeniedException(message);
                }
                else if (message.contains("Duplicate entry"))
                {
                    throw new DuplicateEntryException(message);
                }
                else
                {
                    exn.printStackTrace(); //TODO this does happen
                }
            }
        }
    }

    public static String generateUserMessages(Exception exn)
    {
        String message = exn.getMessage();

        return message;
    }
}
