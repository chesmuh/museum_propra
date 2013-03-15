package de.museum.berleburg.datastorage.backup;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import de.museum.berleburg.datastorage.MuseumDB;

/**
 * 
 * @author Tim Wesener
 *
 */

public class EraseDatabase {

	public static void eraseDB(Connection con)
	{		
		try {
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();
			
			// Erase history
			stmt.executeUpdate("DROP TABLE history");
			stmt.executeUpdate( MuseumDB.History.getCreateTableSQL()[0] );
			
			// Erase exhibit_label
			stmt.executeUpdate("DROP TABLE exhibit_label");
			stmt.executeUpdate( MuseumDB.ExhibitLabel.getCreateTableSQL() );
			
			// Erase exhibit_label
			stmt.executeUpdate("DROP TABLE label");
			stmt.executeUpdate( MuseumDB.Label.getCreateTableSQL()[0] );
			
			// Erase images
			stmt.executeUpdate("DROP TABLE images");
			stmt.executeUpdate( MuseumDB.Images.getCreateTableSQL()[0] );
			
			// Erase outsourced
			stmt.executeUpdate("DROP TABLE outsourced");
			stmt.executeUpdate( MuseumDB.Outsourced.getCreateTableSQL()[0] );
			
			/*
			String test = MuseumDB.Outsourced.getCreateTableSQL();
			test = test.substring(0, test.length()-2) + ",\n`deleted` timestamp NULL DEFAULT NULL);";
			
			stmt.executeUpdate( test );			*/
			
			// Erase exhibit
			stmt.executeUpdate("DROP TABLE exhibit");
			stmt.executeUpdate( MuseumDB.Exhibit.getCreateTableSQL()[0] );
			
			// Erase category
			stmt.executeUpdate("DROP TABLE category");
			stmt.executeUpdate( MuseumDB.Category.getCreateTableSQL()[0] );	
		
			// Erase section
			stmt.executeUpdate("DROP TABLE section");
			stmt.executeUpdate( MuseumDB.Section.getCreateTableSQL()[0] );
			
			// Erase contact
			stmt.executeUpdate("DROP TABLE contact");
			stmt.executeUpdate( MuseumDB.Contact.getCreateTableSQL()[0] );
						
			// Erase museum
			stmt.executeUpdate("DROP TABLE museum");
			stmt.executeUpdate( MuseumDB.Museum.getCreateTableSQL()[0] );
			
			// Erase address
			stmt.executeUpdate("DROP TABLE address");
			stmt.executeUpdate( MuseumDB.Address.getCreateTableSQL()[0] );
					
			// Erase role
			stmt.executeUpdate("DROP TABLE role");
			stmt.executeUpdate( MuseumDB.Role.getCreateTableSQL()[0] );
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException e) {			
			e.printStackTrace();
		} 
	}
		
	public static void eraseDB(Connection con, int musID)
	{		
		System.out.println("Museum wird gelöscht: " + musID);
		
		try
		{
			con.setAutoCommit(false);
			Statement stmt = con.createStatement();			
						
			//delete address
			stmt.executeUpdate("DELETE FROM address WHERE id " + 
								"IN (SELECT address_id FROM museum WHERE id = '"+musID+"' AND deleted IS NULL)"+
								"AND deleted IS NULL");
					
					//addresses of addresses linked by contact contacts
			stmt.executeUpdate("DELETE FROM address WHERE id " + 
					"IN (SELECT contact.address_id FROM contact JOIN role ON contact.role_id = role.id " +
						"WHERE role.museum_id = "+ musID +" " +
						"AND contact.deleted IS null AND role.deleted IS null ) "+
					"AND deleted IS NULL"); 
			
			//Delete Images
			stmt.executeUpdate("DELETE FROM images WHERE `exhibit_id` "+
								"IN (SELECT id FROM exhibit WHERE museum_id = "+ musID +" AND deleted IS NULL) " + 
								"AND deleted IS NULL");			
			
			//Delete first contact
			stmt.executeUpdate("DELETE FROM contact WHERE role_id " +
								"IN (SELECT id FROM role WHERE museum_id = "+ musID+
								" AND deleted IS NULL)"+
								" AND deleted IS NULL"); 			
			
			//delete Role
			stmt.executeUpdate("DELETE FROM role WHERE museum_id = "+ musID +" AND deleted IS NULL");
					
			
			//Delete Labels
			stmt.executeUpdate("DELETE FROM label WHERE label.id " +
									"IN ( SELECT label_id FROM exhibit_label exla JOIN exhibit ex " +
									"ON exla.exhibit_id = ex.id " +
									"WHERE ex.museum_id = " + musID + " ) " +
								"AND label.id NOT IN " +
									"( SELECT label_id FROM exhibit_label exla JOIN exhibit ex " +
									"ON exla.exhibit_id = ex.id " +
									"WHERE ex.museum_id != "+ musID +" ) ");	
			
			//Delete exhibit_label
			stmt.executeUpdate("DELETE FROM exhibit_label WHERE exhibit_id " +
									"IN ( SELECT exhibit_id FROM exhibit " +
									"WHERE museum_id = " + musID + " ) " +
								"AND label_id NOT IN " +
									"( SELECT exhibit_id FROM exhibit " +
									"WHERE museum_id != "+ musID +" ) ");
						
			///delete outsourced_exhibits
			stmt.executeUpdate( "DELETE FROM outsourced_exhibits WHERE outsourced_id " +
								"IN (SELECT id FROM outsourced WHERE museum_id = "+ musID +" )" );
			
			///delete outsourced
			stmt.executeUpdate( "DELETE FROM outsourced WHERE museum_id = "+ musID  );			
			
			//Delete history
			stmt.executeUpdate("DELETE FROM history WHERE `exhibit_id` " +
                              "IN (SELECT id FROM exhibit WHERE museum_id = "+musID+" AND deleted IS NULL)" +
                              "AND deleted IS NULL");
			
			stmt.executeUpdate("DELETE FROM history WHERE `section_id` " +
                    "IN (SELECT id FROM section WHERE museum_id = "+musID+" AND deleted IS NULL)" +
					"AND deleted IS NULL");	
			
			//Delete exhibit
			stmt.executeUpdate("DELETE FROM exhibit WHERE museum_id = '"+musID+"' AND deleted IS NULL"); 
						
			//Delete category
			stmt.executeUpdate("DELETE FROM category WHERE museum_id = '"+musID+"' AND deleted IS NULL"); 
			
			//Delete sections			
			stmt.executeUpdate("DELETE FROM section WHERE museum_id = '"+musID+"' AND deleted IS NULL"); 
			
			//delete Museum
			stmt.executeUpdate("DELETE FROM museum WHERE id = '"+musID+"' AND deleted IS NULL");			
			
			con.commit();
			con.setAutoCommit(true);
			
		} catch (SQLException e) {			
			e.printStackTrace();
		}
				
	}
	
}
