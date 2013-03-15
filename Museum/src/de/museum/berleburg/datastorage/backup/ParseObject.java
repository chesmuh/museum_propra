package de.museum.berleburg.datastorage.backup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.ConnectionException;

/**
 * This is the object given by the parser 
 * 
 * @author Tim Wesener
 * 
 */

public class ParseObject {

	private String head;	
	private ArrayList<String> rowList;
	private int actual, max;
	
	public ParseObject()
	{
		rowList = new ArrayList<String>();
		actual = -1; max = 0;
	}
		
	public String getData(int pos)
	{		
		String res; 		
		res = rowList.get(actual);
		
		String[] array = res.split( "('? , '?)|(\\('?)|('?\\))|(< )|( ; )|( >)" );	
		
		if(pos <= 0 || pos >= array.length )
		{System.out.println("Out of boud:" + pos); return "";}
		
		return array[pos]; 
	}
		
	public String getDataFromHead(int pos)
	{
		String res = head; 
		
		String[] array = res.split( "(`, `)|(\\(`)|(`\\))" );		
		
		if(pos<1 || pos>array.length-1) return "Fehler";
		
		return array[pos];
	}
	
	public String getDataString()
	{		
		return rowList.get(actual);
	}
	/*
	 * Here are the Inserts
	 */
	public Address getSQLAddress()
	{
		if( !this.getData(8).equals( "null" ) ) //deleted
			return null;
		
		return new Address(this.getData(2), this.getData(3) , this.getData(4), this.getData(5), this.getData(6), this.getData(7));	
	}
	
	public Museum getSQLMuseum(HashMap<Integer,Long>  addressMap)
	{			
		if(!this.getData(5).equals("null")) //deleted?
			return null;		
		
		return new Museum(this.getData(2), this.getData(3), addressMap.get( Integer.parseInt( this.getData(4))) );
	}

	public Role getSQLRole(HashMap<Integer,Long>  museumMap)
	{			
		if( !this.getData(4).equals("null") )
			return null;
		
		Role ret = new Role(getData(2), museumMap.get( Integer.parseInt( this.getData(3) ) ));
				
		return ret;
	}
	
	@SuppressWarnings("deprecation")
	public Outsourced getSQLOutsourced(ParseObject obj, HashMap<Integer,Long>  exhibitMap, HashMap<Integer,Long>  addressMap, HashMap<Integer,Long>  contactMap, HashMap<Integer,Long>  museumMap )
	{		
		if( !this.getData(8).equals("null") ) return null; //deleted
				
		Outsourced res = null;
		
		res = new Outsourced( getData(2), getData(3), null, null, null, null, null);
			
		//address
		if(!getData(6).equals("null"))
		{
			res.setAddress_id( addressMap.get( Integer.parseInt( getData(6) ) ) );
		}
		
		//contact
		if(!getData(7).equals("null"))
		{
			res.setContact_id( contactMap.get( Integer.parseInt( getData(7) ) ) );
		}
		
		//museum
		if(!getData(10).equals("null"))
		{
			res.setMuseum_id( museumMap.get( Integer.parseInt( getData(10) ) ) );
		}		
		
		//start date
		if(!this.getData(4).equals("null"))
		{
			String helpstr = this.getData(4);
			
			Date date = new Date(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)));			
			
			res.setStartDate( date );
		}
		
		//end date
		if(!this.getData(5).equals("null"))
		{
			String helpstr = this.getData(5);
			
			Date date = new Date(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)));			
							
			res.setEndDate( date );	
		}	
		
		/**
		 * Adds the Exhibits
		 */
		while( obj.moveCursor() )
		{
			// If the outsource id's are equal
			if( this.getData(1).equals( obj.getData(1) ) )
			{
				Long exhibit_id = null;
				Timestamp date = null;
				
				if( !obj.getData(2).equals("null") )
					exhibit_id = exhibitMap.get( Integer.parseInt( obj.getData(2)) );
							
				if( !obj.getData(3).equals("null") )
				{
					//Giveback
					String help = obj.getData(3);				
					date = new Timestamp(
							Integer.parseInt(help.substring(0, 4)) - 1900,  //Insert year
							Integer.parseInt(help.substring(5, 7)) - 1,  //Insert month	
							Integer.parseInt(help.substring(8, 10)),  //Insert day
							Integer.parseInt(help.substring(11, 13)),  //Insert hour
							Integer.parseInt(help.substring(14, 16)),  //Insert minute
							Integer.parseInt(help.substring(17, 19)),  //Insert second
							0);			
				}
				
				res.addExhibit( exhibit_id , date);	
				
			} // End of if( this.getData(1).equals( obj.getData(1) ) )
		} // Move through outsourced exhibit
				
		return res;
	}
	
	public Category getSQLCategory(HashMap<Integer,Long>  museumMap, HashMap<Integer,Long>  categoryMap)
	{		
		if( !museumMap.containsKey( Integer.parseInt(this.getData(3)) ) || !this.getData(5).equals("null") )
				return null;
		
		if(this.getData(4).equals("null"))
		{
			return new Category(this.getData(2), museumMap.get( Integer.parseInt(this.getData(3))), null );
		}	
		
		return new Category(this.getData(2), museumMap.get(Integer.parseInt( this.getData(3))) , Long.parseLong( this.getData(4) ) );
		
	}

	public Contact getSQLContact(HashMap<Integer,Long>  addressMap, HashMap<Integer,Long>  roleMap)
	{
		if( !this.getData(10).equals("null")) return null; //deleted?
		
		Contact res = null;
		
		res = new Contact( this.getData(2), this.getData(3), this.getData(4), this.getData(5), 
				this.getData(6), getData(7), 0  , null);
		
		if(addressMap.containsKey( Integer.parseInt( getData(8) )  ))
			res.setAddress_id(   addressMap.get( Integer.parseInt( getData(8) ) )  );
		
		
		if(!this.getData(9).equals("null"))
		{
			res.setRoleId( roleMap.get( Integer.parseInt(this.getData(9))) );
		}
			
		return res;
	}
	
	public Section getSQLSection(HashMap<Integer,Long>  museumMap, HashMap<Integer,Long>  sectionMap)
	{
		if( !museumMap.containsKey( Integer.parseInt(this.getData(5))) || !this.getData(6).equals("null") )
		{ return null;	}
		
		Section res = new Section(this.getData(2), this.getData(3), null , museumMap.get(Integer.parseInt(this.getData(5))) );
		
		if(!this.getData(4).equals("null"))		
		{
			res.setParent_id( Long.parseLong(this.getData(4)) );
		}
		
		return res;
	}
	
	public Exhibit getSQLExhibit(HashMap<Integer,Long>  categoryMap, HashMap<Integer,Long>  sectionMap, 
								  HashMap<Integer,Long> museumMap)
	{	
		if( !this.getData(10).equals("null") || !museumMap.containsKey(Integer.parseInt(this.getData(8)) ) ) return null; //deleted
				
		Exhibit res = null;
		
		res = new Exhibit(this.getData(2), this.getData(3), null , null , 
				Long.parseLong(this.getData(6)),null, 
				museumMap.get(Integer.parseInt(this.getData(8))), Double.parseDouble( getData(9)) );
			
		//if section not null
		if(!this.getData(4).equals("null"))
		{
			res.setSection_id( sectionMap.get(Integer.parseInt(this.getData(4))) ); 
		}
		else if(this.getData(4).equals("0"))
		{
			res.setSection_id( new Long(0) );
		}
		
		//if category not null
		if(!this.getData(5).equals("null"))
		{
			res.setCategory_id(  categoryMap.get(Integer.parseInt(this.getData(5)))  );
		}
		
		return res;
	}
	
	@SuppressWarnings("deprecation")
	public History getSQLHistory(HashMap<Integer,Long>  museumMap, HashMap<Integer,Long>  sectionMap , HashMap<Integer,Long>  exhibitMap , 
								  HashMap<Integer,Long>  categoryMap, HashMap<Integer,Long>  outsourcedMap )
	{ 		
		String helpstr;
				
		if( !exhibitMap.containsKey( Integer.parseInt( getData(2) )) || !this.getData(12).equals("null") )	
			return null;
			
		History res = new History(
				0 ,    //exhibitID
				getData(3), getData(4), null , //insert name, description, sectionID
				null, Long.parseLong( getData(7)), //categoryID, count
				null, null  ,//rfid, museumID, 
                Double.parseDouble( getData(10)), null, //wert, outsourcedid
				null, null, null , null  );   //startDate, insert, deleted , updated
						
		//If the History links to another museum
		//If Section, History, Category isn't in the Backup the id's has to be null
		if( exhibitMap.containsKey( Integer.parseInt( getData(2) )   ))
		{
			res.setExhibit_id( exhibitMap.get( Integer.parseInt( getData(2) ) ));
		}
		
		if( museumMap.containsKey( Integer.parseInt( getData(9) ) ) )
				res.setMuseum_id( museumMap.get( Integer.parseInt( getData(9))));
		
		//System.out.println( this.getDataString() );
		
		//if( res.getMuseum_id() == null)
			//System.out.println( "MuseumID ist null");
				
		if(!getData(5).equals("null")) //section
		{
			if( !sectionMap.containsKey( Integer.parseInt(  getData(5) )  )) //There's a value in another museum
				return null; //res.setSection_id( new Long(-1)  );
				
			else
				res.setSection_id( sectionMap.get( Integer.parseInt(  getData(5) ) ) ); 
		}
		
		//deleted
		if( !getData(11).equals("null") && outsourcedMap.containsKey( Integer.parseInt( getData(11) ) ) )
		{
			res.setOutsourced_id(  outsourcedMap.get( Integer.parseInt( getData(11) ) ) );
			
		}
			
		
		
		if(!getData(6).equals("null")) //category
		{
			if( !categoryMap.containsKey(  Integer.parseInt( getData(6))  ))
				return null; //res.setCategory_id(  new Long(-1 ));
			
			else
				res.setCategory_id( categoryMap.get( Integer.parseInt( getData(6))));
		}
		
		if(!getData(8).equals("null")) //rfid
		{
			res.setRfid( getData(8) );
		}		
		
		if(!getData(14).equals("null") ) //startDate
		{
			helpstr = getData(14);
			
			Timestamp date;
			date = new Timestamp(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)),  //Insert day
					Integer.parseInt(helpstr.substring(11, 13)),  //Insert hour
					Integer.parseInt(helpstr.substring(14, 16)),  //Insert minute
					Integer.parseInt(helpstr.substring(17, 19)),  //Insert second
					0);
			
			res.setStartdate( date );
			
		}
		
		
		if(!getData(12).equals("null") ) //deleted
		{
			helpstr = getData(12);
			
			Timestamp date;
			date = new Timestamp(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)),  //Insert day
					Integer.parseInt(helpstr.substring(11, 13)),  //Insert hour
					Integer.parseInt(helpstr.substring(14, 16)),  //Insert minute
					Integer.parseInt(helpstr.substring(17, 19)),  //Insert second
					0);
			
			res.setDeleted( date );	
		}
		
		
		if(!getData(13).equals("null") ) //inserted
		{
			helpstr = getData(13);
			
			Timestamp date;
			date = new Timestamp(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)),  //Insert day
					Integer.parseInt(helpstr.substring(11, 13)),  //Insert hour
					Integer.parseInt(helpstr.substring(14, 16)),  //Insert minute
					Integer.parseInt(helpstr.substring(17, 19)),  //Insert second
					0);
			
			res.setInsert( date );
		}		
		
		if(!getData(15).equals("null") ) //updated
		{
			helpstr = getData(15);
			
			Timestamp date;
			date = new Timestamp(
					Integer.parseInt(helpstr.substring(0, 4)) - 1900,  //Insert year
					Integer.parseInt(helpstr.substring(5, 7)) - 1,  //Insert month	
					Integer.parseInt(helpstr.substring(8, 10)),  //Insert day
					Integer.parseInt(helpstr.substring(11, 13)),  //Insert hour
					Integer.parseInt(helpstr.substring(14, 16)),  //Insert minute
					Integer.parseInt(helpstr.substring(17, 19)),  //Insert second
					0);
			
			res.setDeleted( date );			
		}		
		
		return res;
	}
	
	public Label getSQLLabel(ParseObject obj, HashMap<Integer,Long>  exhibitMap )
	{
		if( !this.getData(3).equals("null") ) return null;
		
		Label ret = null;
		
		// Schauen ob der Name schon vorhanden ist
		Collection<Label> LabelCollection = DataAccess.getInstance().searchLabelsByName( this.getData(2) );
				
		if( LabelCollection.isEmpty() )//Wenn Label noch nicht vorhanden ist
		{	
			ret = new Label(this.getData(2));
					
			String labelId = this.getData(1);			
			
			while( obj.moveCursor() )
			{
				if( obj.getData(2).equals( labelId ) )
				{			
					if(!exhibitMap.containsKey( Integer.parseInt( obj.getData(1) ) )) continue;
					
					ret.addExhibit_id( exhibitMap.get( Integer.parseInt( obj.getData(1) ) ) );
				}
			}			
			
			return ret;
		}
		
		// Wenn Label schon vorhanden ist
		else
		{					
			for(Label label : LabelCollection )
			{
				while( obj.moveCursor() )
				{					
					if(obj.getData(2).equals( this.getData(1) ))
					{
						if(!exhibitMap.containsKey( Integer.parseInt( obj.getData(1) ) )) continue;
						
						label.addExhibit_id( exhibitMap.get( Integer.parseInt( obj.getData(1) ) ) );	
						
						try {
							DataAccess.getInstance().update( label );
						} catch (ConnectionException e) {							
							e.printStackTrace(); }
					}					
				}
			} //End of foreach
			
			return null;
		}
		
	}
	
	public String getSQLImages( HashMap<Integer,Long>  exhibitMap )
	{
		String res = "INSERT INTO images (`exhibit_id` , `name`, `image`) VALUES ('";
		res += exhibitMap.get( Integer.parseInt(this.getData(2)) ) + "' , '"  ;
		res += this.getData(3) + "' , ? )";			
		return res;
	}
	
	public boolean moveCursor()
	{
		if(max > 0 && actual < max-1)
		{	actual++; return true; }
		
		return false;
	}
	
	public int getMax()
	{return this.max; }
	
	void setHead(String incHead)
	{	this.head = incHead;	}
	
	public void addRow(String incRow)
	{	rowList.add(incRow); 
		max++;
	}
	
	public String getHead()
	{ return head; }
	
	public String toString()
	{
		String ret = "";
		ret += head + "\n";
		
		Iterator<String> it = rowList.iterator();
		
		while( it.hasNext() )
		{
			String help = it.next();
			
			ret += help + "\n";
		}		
		
		return ret;
	}
}