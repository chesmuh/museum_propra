package de.museum.berleburg.datastorage.backup;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/***
 * The Parser
 * 
 * @author Tim Wesener
 *
 */

public class Parser {

	String address;
	ZipFile zipFile;
	ZipEntry entry;
	Lexer Lex = null;
	
	public Parser(String address) throws IOException
	{
		this.address = address;
		zipFile = null; entry = null;
		
		zipFile = new ZipFile(address);
	}
	
	/** 
	 * @param Name - name of the table which is searched in the backup file
	 * @return the parse object which contains the head and all data
	 */
	public ParseObject getObject(String Name)
	{
		ParseObject ret = new ParseObject();		
		
		if(Name.equals("images"))
		{
			entry = new ZipEntry("imagettable.txt");
			
			try {
				Lex = new Lexer( zipFile.getInputStream(entry) );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			boolean run = true;
			int status = 1;
			
			while(run)
			{
				LexObject help = Lex.getLexObject();
				
				if( help.getType().equals("image") && status == 1)
				{
					ret.addRow(help.getZeile() );
					status = 2;
				}
				
				else if( help.getType().equals("image") && status == 2 )
				{
					ret.addRow(help.getZeile() );
				}
				
				else if( status == 2 || help.getType().equals("EOF"))
				{
					run = false;
				}
			}
			
			return ret;
		}
		
		else
		{	/**
			* Parses the tables
			*/
			entry = new ZipEntry("backup.sql");
			
			Lex = null;
			boolean run = true;
			int status = 1;
			
			try {
				Lex = new Lexer( zipFile.getInputStream(entry) );
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			while(run)
			{
				LexObject help = Lex.getLexObject();
				
				if(help.getType().equals("emptyLine") || help.getType().equals("comment") ) {}
				
				else if(help.getType().equals("info") && Name.equals("Info") )
				{
					ret.setHead( help.getZeile()  );
					run=false;
				}
				
				else if(help.getType().equals("head"))
				{
					if(status == 1 && help.getName().equals(Name))
					{
						ret.setHead( help.getZeile() );
						status = 2;
					}
					
					else if(status == 2)
					{ run = false;	}					
				}
				
				else if(help.getType().equals("tail") && status == 2)
				{
						ret.addRow( help.getZeile() );				
				}
				
				else if(help.getType().equals("EOF"))
				{ run = false; }				
			}
			
		}//End of table parsing
				
		return ret;
	}
	
	public InputStream getImage(int imgId)
	{
		entry = new ZipEntry(""+imgId + ".png");
		InputStream istr = null;
		
		try {
			istr = zipFile.getInputStream(entry);				
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return istr;
	}
}
