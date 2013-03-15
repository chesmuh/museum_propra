package de.museum.berleburg.exceptions;

/**
 * Gets thrown when trying to delete a model that already got deleted.
 *
 * @author Anselm Brehme
 */
public class ModelAlreadyDeletedException extends Exception
{
    public ModelAlreadyDeletedException(String msg)
    {
        super(msg);
    }
}
