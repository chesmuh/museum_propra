package de.museum.berleburg.exceptions;

public class CategoryNotFoundException extends Exception
{
    /**
	 * @author Chesmuh
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CategoryNotFoundException(String msg)
    {
        super(msg);
    }

    public CategoryNotFoundException()
    {
        super();
    }
}
