package de.museum.berleburg.exceptions;

public class NotAZipFileException extends Exception
{
    public NotAZipFileException(String msg)
    {
        super(msg);
    }
}