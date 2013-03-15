package de.museum.berleburg.datastorage.backup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.MuseumDB;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
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
 *  Here is the funktion to insert a backup
 *  
 *  @author Tim Wesener
 */

public class InsertBackup
{
	public static void insertBackup(String fileaddress, ProcessCallBack callback) throws SQLException, ConnectionException
	{		
		long wholeTime = System.currentTimeMillis();
		long exhibitTime , imageTime;
		
		Parser parser = null;
		ParseObject obj = null;
		HashMap<Integer,Long> map1 = new HashMap<Integer,Long>();
		HashMap<Integer,Long> map2 = new HashMap<Integer,Long>();
		HashMap<Integer,Long> map3 = new HashMap<Integer,Long>();
		HashMap<Integer,Long> map4 = new HashMap<Integer,Long>();
		HashMap<Integer,Long> map5 = new HashMap<Integer,Long>();
		HashMap<Integer,Long> map6 = new HashMap<Integer,Long>();
		
		Configuration.getInstance().setDefault();
								
		Connection connection = null ;
		Statement statement = null;
				
		try {			
			parser = new Parser( fileaddress );
			
			connection = Configuration.getInstance().getConnection();
			statement = connection.createStatement();
			
		} catch (IOException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		}
						
		/**
		 * Erases Data in Database
		 */		
		obj = parser.getObject("Info");		
		
		if(obj.getDataFromHead(1).equals("WholeSystemBackup"))
		{				
			System.out.println("Whole System Backup");
			EraseDatabase.eraseDB(connection);		
		}
			
		else if(obj.getDataFromHead(1).equals("MuseumBackup"))
		{
			System.out.println("Museum Backup");
			obj = parser.getObject("museum");
			obj.moveCursor();
			String MuseumName =  obj.getData(2);
			ResultSet test;
			
			try {
				test = statement.executeQuery("SELECT id FROM museum WHERE name = '" + MuseumName +"' AND deleted IS NULL" );
				
				if(test.next() )
				{//there is a museum with the same name
					EraseDatabase.eraseDB(connection, test.getInt(1) );				
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}
		
		connection.setAutoCommit(true);
				
		/**
		 *  Inserts address
		 */
		System.out.println("Insert address");
		
		String tablename = "address";		
		
		obj = parser.getObject(tablename);
		
		while(obj.moveCursor())
		{	
			Address insert = obj.getSQLAddress();
			
			if(insert != null)
			{
				DataAccess.getInstance().store(insert);
						
				map1.put( Integer.parseInt(obj.getData(1))  , insert.getId());
			}
		}		
		
		/* Until here:
		 * map1:address
		 * map2:not in use
		 * map3:not in use
		 * map4:contact
		 * map5:not in use
		 * map6:not in use
		 */		
				
		/**
		 *  Inserts museum
		 */
		System.out.println("Insert museum");

		tablename = "museum";		
		
		obj = parser.getObject(tablename);
		
		while(obj.moveCursor())
		{	
			Museum insert = obj.getSQLMuseum(map1);
			
			if( insert != null)
			{
				DataAccess.getInstance().store(insert);
						
				map3.put( Integer.parseInt(obj.getData(1)), insert.getId() );
			}
		}
		
		
		/* Until here:
		 * map1:address
		 * map2:not in use
		 * map3:museum
		 * map4:contact
		 * map5:not in use
		 * map6:not in use
		 */			
		
		/**
		 *  Inserts role
		 */
		System.out.println("Insert role");
		
		tablename = MuseumDB.Role.TABLE_NAME;
		
		obj = parser.getObject(tablename);	
		
		while(obj.moveCursor())
		{
			Role insert = obj.getSQLRole(map3);
						
			if(insert != null)
			{
				DataAccess.getInstance().store( insert );
			
				map6.put( Integer.parseInt( obj.getData(1) ) , insert.getId() );
			}
		}
		
		/* Until here:
		 * map1:address
		 * map2:not in use
		 * map3:museum
		 * map4:contact
		 * map5:not in use
		 * map6:role
		 */			
		
		/**
		 *  Inserts category
		 */
		System.out.println("Insert category");
		
		tablename = "category";		
		
		obj = parser.getObject(tablename);		
			
		while(obj.moveCursor())
		{		
			Category insert = obj.getSQLCategory(map3 , map2);
			
			if(insert != null)
			{
				DataAccess.getInstance().store(insert);
			
				map2.put( Integer.parseInt(obj.getData(1)) , insert.getId() );
			}
		} 
			
		// We haven't stored the parent id's now, we do that now
		Collection<Long> CategoryIds = map2.values();		
		Iterator<Long> CategoryIter = CategoryIds.iterator();
		Category category = null;
				
			while( CategoryIter.hasNext() )
			{
				Long lauf = (Long) CategoryIter.next();
					
				category = DataAccess.getInstance().getCategoryById( lauf );
					
				if( category.getParent_id() != null )
				{
					category.setParent_id( map5.get( category.getParent_id().intValue()) );									
					DataAccess.getInstance().update( category );
				}
			}
		
		/* Until here:
		 * map1: address
		 * map2: category
		 * map3: museum
		 * map4: contact
		 * map5: not in use
		 * map6: role
		 */			
		
		/**
		 *  Inserts contact
		 */
		System.out.println("Insert contact");
		
		tablename = "contact";		
		
		obj = parser.getObject(tablename);		
		
		while(obj.moveCursor())
		{	
			Contact insert = obj.getSQLContact(map1, map6);
						
			if( insert != null )
			{
				DataAccess.getInstance().store(insert);
			
				map4.put(Integer.parseInt( obj.getData(1)) , insert.getId() );
			}
		}
		
		map6 = new HashMap<>();
		
		/* Until here:
		 * map1: address
		 * map2: category
		 * map3: museum
		 * map4: contact
		 * map5: section
		 * map6:not in use
		 */			
		
		/**
		 *  Inserts section
		 */
		System.out.println("Insert section");
		
		tablename = "section";		
		
		obj = parser.getObject(tablename);		
				
		while(obj.moveCursor())
		{	
			Section insert = obj.getSQLSection(map3, map5);
			
			if(insert != null)
			{
				DataAccess.getInstance().store(insert);
			
				map5.put(Integer.parseInt(obj.getData(1)), insert.getId());
			}
		}
		
		// We haven't stored the parent id's now, we do that now
		Collection<Long> SectionIds = map5.values();		
		Iterator<Long> SectionIter = SectionIds.iterator();
		Section section = null;
		
		while( SectionIter.hasNext() )
		{
			Long lauf = (Long) SectionIter.next();
			
			section = DataAccess.getInstance().getSectionById( lauf );				
			
			if( section.getParent_id() != null )
			{
				section.setParent_id( map5.get( section.getParent_id().intValue()) );
				DataAccess.getInstance().update( section );
			}
		}
			
		/* Until here:
		 * map1: address
		 * map2: category
		 * map3: museum
		 * map4: not in use
		 * map5: section
		 * map6: not in use
		 */		
		connection.setAutoCommit(false);
		
		/**
		 *  Inserts exhibit
		 */	
		System.out.println("Insert exhibit");
				
		exhibitTime = System.currentTimeMillis();
		
		tablename = "exhibit";		
		
		obj = parser.getObject(tablename);			
		
		int i = 0;
		
		while(obj.moveCursor())
		{					
			Exhibit insert = obj.getSQLExhibit(map2, map5, map3);
			
			if( insert != null )
			{			
				DataAccess.getInstance().store(insert);
				
				map6.put(Integer.parseInt(obj.getData(1)), insert.getId() );
				i++;
			}
			
			if(i % 500 == 0)
				connection.commit();
		}
		
		exhibitTime = System.currentTimeMillis() - exhibitTime;
		
		connection.setAutoCommit(true);
		
		/* Until here:
		 * map1: address
		 * map2: category
		 * map3: museum
		 * map4: contact
		 * map5: section
		 * map6: exhibit
		 */			
		
		HashMap<Integer, Long> outsourcedTable = new HashMap<Integer, Long>();
		
		/**
		 * Insert Outsourced
		 */	
		System.out.println("Insert outsourced");

		tablename = "outsourced";		
		
		obj = parser.getObject(tablename);			
				
		while(obj.moveCursor())
		{		
			Outsourced insert = obj.getSQLOutsourced( parser.getObject( "outsourced_exhibits"), map6, map1, map4, map3);
					 
			if( insert != null )
			{
				DataAccess.getInstance().store(insert);
					
				outsourcedTable.put(Integer.parseInt(obj.getData(1)), insert.getId());
			}
		}
		
		
		/* Until here:
		 * map1: not in use
		 * map2: category
		 * map3: museum
		 * map4: contact
		 * map5: section
		 * map6: exhibit
		 */		
			
		/**
		 * insert history
		 */
		System.out.println("Insert history");
		
		tablename = MuseumDB.History.TABLE_NAME;
		
		obj = parser.getObject(tablename);
	
		while(obj.moveCursor())
		{
			History insert = obj.getSQLHistory(map3, map5, map6, map2, outsourcedTable);
						
			if(insert != null)			
				DataAccess.getInstance().store( insert );
			
		}
		
		/* Until here:
		 * map1: not in use
		 * map2: not in use
		 * map3: museum
		 * map4: contact
		 * map5: section
		 * map6: exhibit
		 */
		
		map1 = new HashMap<>();
		map2 = new HashMap<>();
		
		/** 
		 *  Insert Label
		 */	
		System.out.println("Insert Label");
		
		tablename = "label";		
		
		obj = parser.getObject(tablename);		
				
		while(obj.moveCursor())
		{
			Label insert = obj.getSQLLabel(parser.getObject("exhibit_label") , map6 );
					
			if( insert != null )
				DataAccess.getInstance().store(insert);
		}
		
		connection.setAutoCommit(false);
		
		/**
		 * Inserts Images
		 */	
		System.out.println("Insert Images");

		imageTime = System.currentTimeMillis();		
		
		obj = parser.getObject("images");
		
		PreparedStatement prepstmt = null;
		
		i = 0;
		
		while( obj.moveCursor() )
		{
			int id = Integer.parseInt( obj.getData(1) );		
			
			try {
				prepstmt = connection.prepareStatement( obj.getSQLImages(map6) );
							
				prepstmt.setBinaryStream(1 , parser.getImage(id) );					
							
				System.out.println("Speichere Bild :" + id);
				
				prepstmt.executeUpdate();				
				
				prepstmt.close();				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			i++;
			if(i % 100 == 0)
				connection.commit();
			
			imageTime = System.currentTimeMillis() - imageTime;			
		}
		
		//Whole Time
		wholeTime = System.currentTimeMillis() - wholeTime;
		
		System.out.println("Laufzeit des Gesamten Programms: " + wholeTime/1000 + " Sekunden");
		System.out.println("Laufzeit der Exhibits: " + exhibitTime/1000 + " Sekunden");
		System.out.println("Laufzeit der Images: " + imageTime/1000 + " Sekunden");
		
		connection.setAutoCommit(true);
		
		/**
		 * Do some clean up work
		 */
		try {
			connection.setAutoCommit(true);
			statement.close();			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} 
}