package de.museum.berleburg.logic;

import java.sql.Timestamp;
import java.util.Date;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;

/**
 *
 * @author David
 *
 */
public class OutsourcedLogic
{

    /**
     * Deletes an outsourced.
     *
     * @param toDelete
     * @throws ConnectionException
     * @throws ModelAlreadyDeletedException
     * @throws IntegrityException
     */
    public static void deleteOutsourced(Outsourced toDelete)
            throws ModelAlreadyDeletedException, ConnectionException,
            IntegrityException
    {
        if (!isEveryThingBack(toDelete))
        {
            throw new IntegrityException(toDelete,
                    "Es sind noch nicht alle Exponate zurückgekommen. Löschen ist nicht möglich. ");
        }
        DataAccess.getInstance().delete(toDelete);
    }

    /**
     * Checks, if every Exhibit is back.
     *
     * @param toCheck
     * @return true, if every Exhibit is back
     */
    public static boolean isEveryThingBack(Outsourced toCheck)
    {
        for (Timestamp givenBack : toCheck.getExhibitIds().values())
        {
            if (givenBack == null)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks, if an Outsourced is expired.
     *
     * @param toCheck
     * @return true, if Outsourced is expired
     */
    public static boolean isExpired(Outsourced toCheck)
    {
        Date today = new Date();
        if (toCheck.getEndDate() == null)
        {
            return false;
        }
        if (toCheck.getEndDate().before(today))
        {
            return true;
        }
        return false;
    }

    /**
     * Checks, if an Outsourced is permanent.
     *
     * @param toCheck
     * @return true, if an Outsourced is permanent
     */
    public static boolean isPermanentOutsourced(Outsourced toCheck)
    {
        if (toCheck.getEndDate() == null)
        {
            return true;
        }
        return false;
    }

    /**
     * Updates the outsourced.
     *
     * @param toSave
     * @throws ConnectionException
     */
    public static void saveOutsourced(Outsourced toSave)
            throws ConnectionException
    {
        DataAccess.getInstance().update(toSave);
    }
}
