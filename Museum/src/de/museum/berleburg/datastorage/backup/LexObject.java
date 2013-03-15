package de.museum.berleburg.datastorage.backup;

/***
 * Here is the object which is given back by the Lexer
 * 
 * @author Tim Wesener
 */
public class LexObject {

	private String name;
	private String type;
	private String zeile;
	
	public LexObject(String zeile)
	{
		this.zeile = zeile;		
		
		if(zeile.equals(""))
		{
			name = null;
			type = "emptyLine";
		}
		
		else if(zeile.substring(0, 2).equals("--") )
		{
			name = null;
			type = "comment";
		}
		
		else if(zeile.substring(0, 1).equals("(") )
		{
			name = null;
			type = "tail";
		}
		
		else if(zeile.substring(0, 2).equals("-?") )
		{
			name = null;
			type = "info";
		}
		
		else if(zeile.substring(0, 11).equals("INSERT INTO") )
		{
			String[] test = zeile.split("`", 3);
			
			name = test[1] ;
			type = "head";
		}
		
		else if(zeile.substring(0, 2).equals("op") )
		{
			name = null;
			type = "EOF";
		}
		
		else if(zeile.substring(0, 2).equals("< ") )
		{
			type = "image";
			name = null;
		}
		
	}
	
	public String getType()
	{
		return type;
	}

	public void setType(String inType)
	{
		this.type = inType;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		this.name = inName;
	}
	
	public String getZeile()
	{
		return zeile;
	}

	public void setZeile(String inZeile)
	{
		this.zeile = inZeile;
	}

}
