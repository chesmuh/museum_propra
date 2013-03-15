package de.museum.berleburg.exceptions;

public class SectionNotFoundException extends Exception
{
    public SectionNotFoundException()
    {
        super();
    }

    public SectionNotFoundException(String msg)
    {
        super(msg);
    }
}
