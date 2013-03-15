package de.museum.berleburg.datastorage.backup;

import java.io.IOException;
import java.io.InputStream;

/**
 * The Lexer used to read the backup - file
 *  
 * @author Tim Wesener
 *
 */
public class Lexer {
	
	InputStream inputStream;
	
	public Lexer(InputStream incInputStream)
	{
		this.inputStream = incInputStream;
	}
	
	public LexObject getLexObject()
	{
		byte[] buffer = new byte[1];
		String res = "";
		boolean special = false;
		boolean apostrophe = false; //IS true, when the file pointer is inside two apostrophes
		
		try {
			
			while(true)
			{
				int returned = inputStream.read( buffer );
				if(returned == -1) 
				{
					return new LexObject("op EndOfFile");
				}
				
				//Sonderzeichen
				if( buffer[0] == -61 )
				{ special = true;
					continue;
				}
		
				char erg = (char)buffer[0];
				if(erg == '\n' && !apostrophe)
					break;
				
				if(special)
				{ 
					special = false;
					res += getUTF8(buffer[0]);					
				}
				
				else if(erg == '\'')
				{
					if(apostrophe)	apostrophe=false;
					else			apostrophe=true;
					
					res += erg;
				}
				
				else
					res += erg;
			}		
			
			return new LexObject(res);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	/**
	 * Convert the last byte of a UTF8 character in a character
	 * 
	 * @param inc - the second part of the special UTF8 Character
	 * 
	 * @return the special UTF8 in char
	 */
	private char getUTF8(byte inc)
	{
		char res = 'd';
		
		switch(inc)
		{
		case -68: res = 'ü'; break;
		case -74: res = 'ö'; break;
		case -92: res = 'ä'; break;
		case -97: res = 'ß'; break;
		case -100:res = 'Ü'; break;
		case -106:res = 'Ö'; break;
		case -124:res = 'Ä'; break;		
		}
		
		return res;
	}
}
