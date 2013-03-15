package de.museum.berleburg.exceptions;


import de.museum.berleburg.datastorage.model.DatabaseElement;

/**
 *
 * @author Nils Leonhardt
 *
 */
public class ObjectAlreadyDeletedException extends Exception
{
    private DatabaseElement element;

    public ObjectAlreadyDeletedException()
    {
    }

    public ObjectAlreadyDeletedException(String msg)
    {
        super(msg);
    }

    public ObjectAlreadyDeletedException(DatabaseElement element)
    {
        this.element = element;
    }

    public DatabaseElement getElement()
    {
        return element;
    }
}
