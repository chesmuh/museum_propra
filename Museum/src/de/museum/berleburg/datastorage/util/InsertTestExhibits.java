package de.museum.berleburg.datastorage.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;

/**
 * Inserts Test Data
 * 
 * @author Tim Wesener
 */

public class InsertTestExhibits
{
	/**
	 * Inserts exhibits in thre different museums
	 * 
	 * @param number - number of exhibits
	 */
	 public static void insertTestMuseum(int number) throws SQLException
	    {
	        long startTime = System.currentTimeMillis();
	        Configuration.getInstance().setDefault();
	        final int helper = number / 33 == 0 ? 1 : number / 33;
	        try
	        {
	            Connection conn = Configuration.getInstance().getConnection();

	            Configuration.getInstance().getConnection().setAutoCommit(false); // Should speed up inserting by a good amount of time (Anselm)


	            //insert addresses
	            DataAccess.getInstance().store(new Address("Hauptstraße", "12a", "57072", "Siegen", "Nordrhein Westfalen", "Deutschland"));
	            DataAccess.getInstance().store(new Address("Freiweg", "128", "48143", "Münster", "Nordrhein Westfalen", "Deutschland"));
	            
	            DataAccess.getInstance().store(new Address("Sensenstraße", "24", "57072", "Siegen", "Nordrhein Westfalen", "Deutschland"));
	           
	            DataAccess.getInstance().store(new Address("Kleinstraße", "87b", "57072", "Siegen", "Nordrhein Westfalen", "Deutschland"));


	            //insert museums
	            DataAccess.getInstance().store(new Museum("Heimatmuseum", "Das Heimatmuseum in Siegen mit vielen interessanten Exponaten.", 1));
	            DataAccess.getInstance().store(new Museum("Bergbaumuseum", "Das Bergbumuseum der Stadt Siegen", 3));
	            DataAccess.getInstance().store(new Museum("Handarbeitsmuseum", "Dieses Museum bietet Exponate", 4));
	            
	            //insert sections
	            DataAccess.getInstance().store(new Section("Erdgeschoss", "Das Erdgeschoss.", null, 1));
	            DataAccess.getInstance().store(new Section("Großer Raum", "Die Haupthalle mit größeren Exponaten.", new Long(1), 1));
	            DataAccess.getInstance().store(new Section("Nebenraum", "Ein kleiner Nebenraum mit wenigen Exponaten.", new Long(1), 1));
	            DataAccess.getInstance().store(new Section("Flur", "Auch auf dem Flur stehen einige Exponate.", new Long(1), 1));
	            DataAccess.getInstance().store(new Section("1.Etage", "Die erste Etage", null, 1));
	            DataAccess.getInstance().store(new Section("Treppenhaus", "Im Treppenhaus hängen vor allem Gemälde.", new Long(5), 1));
	            DataAccess.getInstance().store(new Section("Großer Saal", "Ein Saal mit vielen Exponaten.", new Long(5), 1));
	            DataAccess.getInstance().store(new Section("Außenbereich", "Der große Außenbereich.", null, 1));
	            DataAccess.getInstance().store(new Section("Scheune", "Eine Große Scheune.", new Long(8), 1));
	            DataAccess.getInstance().store(new Section("Parkplatz", "Ein großer Parkplatz.", new Long(8), 1));

	            DataAccess.getInstance().store(new Section("Haupthalle", "Der Eingangsbereich", null, 2)); // id = 11
	            DataAccess.getInstance().store(new Section("Schrank", "In dem Schrank hängen Kleider", new Long(11), 2)); // id = 12
	            
	            DataAccess.getInstance().store(new Section("Haupthaus", "Das große Haupthaus", null, 3)); //id= 13
	            DataAccess.getInstance().store(new Section("Kleiner Arbeitsraum", "Ein Raum, der zeigt wie damals gearbeitet wurde", new Long(13), 3)); // id = 14
	            DataAccess.getInstance().store(new Section("Großer Raum", "Hier stehen größere Exponate", new Long(13), 3)); // id = 15

	            //Insert Role
	            DataAccess.getInstance().store( new Role("Der Museumsleiter",1));
	            
	            DataAccess.getInstance().store( new Role("Der Vorsitzende",2));
	            
	            DataAccess.getInstance().store( new Role("Vorstand",3));
	            
	            //Insert contact			
	            DataAccess.getInstance().store(new Contact("Müller", "Hans", "02154/5447", "mueller@museum.de", "Der Derzeitige Vorsitzende des Museums", "0551/45612", 1 , new Long(1) ));
	            DataAccess.getInstance().store(new Contact("Schneider", "Peter", "045568/45112", "peter.schneider@truckerfest.de", "Unser Ansprechpartner", "012245/4544", 2, null));

	            DataAccess.getInstance().store(new Contact("Kaiser", "Michaela", "02154/4554", "Kaiser@museum.de", "Die Derzeitige Vorsitzende des Museums", "0551/455", 3 , new Long(2) ));

	            DataAccess.getInstance().store(new Contact("Müller", "Hans", "02154/5447", "mueller@museum.de", "Der Derzeitige Vorsitzende des Museums", "0551/45612", 4 , new Long(3) ));
	            
	            //Insert Category
	            DataAccess.getInstance().store(new Category("Fahrzeuge", 1, null)); // id = 1
	            DataAccess.getInstance().store(new Category("Möbel", 1, null));
	            DataAccess.getInstance().store(new Category("Technik", 1, null));
	            DataAccess.getInstance().store(new Category("Sonstiges", 1, null));
	            
	            DataAccess.getInstance().store(new Category("Sonstiges", 2, null)); //id = 5

	            DataAccess.getInstance().store(new Category("Sonstiges", 3, null)); //id = 6
	            
	            
	            conn.commit(); // Commit all insertions for now

	            //Insert exhibits		
	            Map<Integer, String> first = new HashMap<Integer, String>();
	            first.put(0, "gespendeter");
	            first.put(1, "geschenkter");
	            first.put(2, "geliehender");
	            first.put(3, "gekaufter");

	            Map<Integer, String> second = new HashMap<Integer, String>();
	            second.put(0, "roter");
	            second.put(1, "gelber");
	            second.put(2, "blauer");
	            second.put(3, "violetter");
	            second.put(4, "orangener");
	            second.put(5, "grüner");
	            second.put(6, "purpurner");
	            second.put(7, "weißer");
	            second.put(8, "grauer");
	            second.put(9, "schwarzer");

	            Map<Integer, String> third = new HashMap<Integer, String>();
	            third.put(0, "alter");
	            third.put(1, "neuer");
	            third.put(2, "aktueller");
	            third.put(3, "neuwertiger");
	            third.put(4, "kaputter");
	            third.put(5, "schöner");
	            third.put(6, "seltener");
	            third.put(7, "günstiger");
	            third.put(8, "verzierter");
	            third.put(9, "restaurierter");

	            Map<Integer, String> fourth = new HashMap<Integer, String>();
	            fourth.put(0, "deutscher");
	            fourth.put(1, "französischer");
	            fourth.put(2, "englischer");
	            fourth.put(3, "russischer");
	            fourth.put(4, "amerikanischer");
	            fourth.put(5, "irischer");
	            fourth.put(6, "spanischer");
	            fourth.put(7, "italienischer");
	            fourth.put(8, "polnischer");
	            fourth.put(9, "schwedischer");

	            Map<Integer, String> fifth = new HashMap<Integer, String>();
	            fifth.put(0, "Traktor");
	            fifth.put(1, "LKW");
	            fifth.put(2, "Tisch");
	            fifth.put(3, "Stuhl");
	            fifth.put(4, "Schrank");
	            fifth.put(5, "Computer");
	            fifth.put(6, "Radring");
	            fifth.put(7, "Reifen");
	            fifth.put(8, "Besen");
	            fifth.put(9, "Pfahl");

	            //System.out.println("Füge " + number + " Austellungsstücke ein...");
	            for (int i = 0; i < number; i++)
	            {
	                String name = "";
	                long sectionId = 0;
	                long categoryId = 0;	                
	                long museumID = (long) (Math.random()*10)%3 +1 ;
	                
	                name += first.get(getNumberOnPos(i, 5)) + " ";
	                name += second.get(getNumberOnPos(i, 4)) + " ";
	                name += third.get(getNumberOnPos(i, 3)) + " ";
	                name += fourth.get(getNumberOnPos(i, 2)) + " ";
	                name += fifth.get(getNumberOnPos(i, 1));


	                // insert section_id
	                Random rnd = new Random();

	               if(museumID == 1)
	               {
	            	  sectionId =  ( rnd.nextInt()*10)%10 + 1 ;	            	  
	            	  if(sectionId < 0)
	            		  	sectionId *= -1;
	               }
	               else if( museumID == 2)
	               {
	            	   sectionId =  ( rnd.nextInt()*10)%2 + 11 ;	            	   
	            	   if(sectionId < 0)
	            		  	sectionId *= -1;
	               }
	               else if( museumID == 3)
	               {
	            	   sectionId =  ( rnd.nextInt()*10)%3 + 13 ;
	            	   if(sectionId < 0)
	            		  	sectionId *= -1;
	               }

	                //insert category
	               if(museumID == 1)
	               {
	            	   categoryId =  ( rnd.nextInt()*10)%4 + 1 ;
	            	   if(categoryId < 0)
	            		   categoryId *= -1;
	               }
	               else if( museumID == 2)
	               {
	            	   categoryId =  5 ;
	            	   if(categoryId < 0)
	            		   categoryId *= -1;
	               }
	               else if( museumID == 3)
	               {
	            	   categoryId =  6 ;
	            	   if(categoryId < 0)
	            		   categoryId *= -1;
	               }	                           
	              	               
	                DataAccess.getInstance().store(new Exhibit(name, "Ein " + name, new Long(  sectionId ), new Long( categoryId ), 1 , null, museumID , Math.random()%300 ));

	                // Insert Image		
	                byte[] bytes = Files.readAllBytes(Paths.get("data", "testImage.png"));
	                DataAccess.getInstance().store(new Image(bytes, "Ein Testbild", i + 1));


	                if ((i % 2000) == 0)
	                {
	                    conn.commit(); // commiting all 100 Exhibits 
	                }
	                // 1 -> 273 sec
	                // 200 -> 33 sec
	                // 500 -> 31 sec
	                // 1000 -> 29 sec
	                // 2000 -> 28 sec
	                // 5000 -> 29 sec
	                // 10000 -> 28 sec
	                if ((i % helper == 0)) // Show status every 3%
	                {
	                    //System.out.println(i * 100 / number + "% Fertig");
	                }
	            }
	            conn.commit(); // commit remaining exhibits
	           // System.out.println(number + " Austellungsstücke eingefügt!");

	            //Insert Outsourced
	            DataAccess.getInstance().store(new Outsourced("Truck Ausstellung Münster", "Ein LKW geht zur Truckerausstellung nach Münster.",
	                    new Date(112, 10, 28, 16, 30, 001), null, null, new Long(2), new Long(2)));

	            //Insert Label and their exhibit label
	            Label label = new Label("teures Label");
	            label.addExhibit_id(1L);
	            DataAccess.getInstance().store(label);
	            label = new Label("anderes Label");
	            label.addExhibit_id(35L);
	            DataAccess.getInstance().store(label);

	           // System.out.println("Alle TestDaten erfolgreich eingefügt!");
	            startTime = System.currentTimeMillis() - startTime;
	            startTime /= 1000; //millis->sec
	           // System.out.println("Dauer: " + startTime + " Sekunden.");
	            conn.setAutoCommit(true);

	        }
	        catch (SQLException e)
	        {

	            e.printStackTrace();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }
	 /**
	 * Inserts exhibits one museum
	 * 
	 * @param number - number of exhibits
	 */
     public static void insertTestData(int number) throws SQLException
     {
        long startTime = System.currentTimeMillis();
        Configuration.getInstance().setDefault();
        final int helper = number / 33 == 0 ? 1 : number / 33;
        try
        {
            Connection conn = Configuration.getInstance().getConnection();

            Configuration.getInstance().getConnection().setAutoCommit(false); // Should speed up inserting by a good amount of time (Anselm)


            //insert addresses
            DataAccess.getInstance().store(new Address("Hauptstraße", "12a", "57072", "Siegen", "Nordrhein Westfalen", "Deutschland"));
            DataAccess.getInstance().store(new Address("Freiweg", "128", "48143", "Münster", "Nordrhein Westfalen", "Deutschland"));

            //insert museum
            Museum museum = new Museum("Heimatmuseum", "Das Heimatmuseum in Siegen mit vielen interessanten Exponaten.", 1);
            DataAccess.getInstance().store(museum);
            long museumId = museum.getId();
            //insert sections
            DataAccess.getInstance().store(new Section("Erdgeschoss", "Das Erdgeschoss.", null, 1));
            DataAccess.getInstance().store(new Section("Großer Raum", "Die Haupthalle mit größeren Exponaten.", new Long(1), 1));
            DataAccess.getInstance().store(new Section("Nebenraum", "Ein kleiner Nebenraum mit wenigen Exponaten.", new Long(1), 1));
            DataAccess.getInstance().store(new Section("Flur", "Auch auf dem Flur stehen einige Exponate.", new Long(1), 1));
            DataAccess.getInstance().store(new Section("1.Etage", "Die erste Etage", null, 1));
            DataAccess.getInstance().store(new Section("Treppenhaus", "Im Treppenhaus hängen vor allem Gemälde.", new Long(5), 1));
            DataAccess.getInstance().store(new Section("Großer Saal", "Ein Saal mit vielen Exponaten.", new Long(5), 1));
            DataAccess.getInstance().store(new Section("Außenbereich", "Der große Außenbereich.", null, 1));
            DataAccess.getInstance().store(new Section("Scheune", "Eine Große Scheune.", new Long(8), 1));
            DataAccess.getInstance().store(new Section("Parkplatz", "Ein großer Parkplatz.", new Long(8), 1));


            //Insert contact			
            DataAccess.getInstance().store(new Contact("Müller", "Hans", "02154/5447", "mueller@museum.de", "Der Derzeitige Vorsitzende des Museums", "0551/45612", 1 , null));
            DataAccess.getInstance().store(new Contact("Schneider", "Peter", "045568/45112", "peter.schneider@truckerfest.de", "Unser Ansprechpartner", "012245/4544", 2, null));

            //Insert Category
            DataAccess.getInstance().store(new Category("Fahrzeuge", 1, null));
            DataAccess.getInstance().store(new Category("Möbel", 1, null));
            DataAccess.getInstance().store(new Category("Technik", 1, null));
            DataAccess.getInstance().store(new Category("Sonstiges", 1, null));

            //Insert Role
            DataAccess.getInstance().store( new Role("Der Museumsleiter",1));
            
            
            conn.commit(); // Commit all insertions for now

            //Insert exhibits		
            Map<Integer, String> first = new HashMap<Integer, String>();
            first.put(0, "gespendeter");
            first.put(1, "geschenkter");
            first.put(2, "geliehender");
            first.put(3, "gekaufter");

            Map<Integer, String> second = new HashMap<Integer, String>();
            second.put(0, "roter");
            second.put(1, "gelber");
            second.put(2, "blauer");
            second.put(3, "violetter");
            second.put(4, "orangener");
            second.put(5, "grüner");
            second.put(6, "purpurner");
            second.put(7, "weißer");
            second.put(8, "grauer");
            second.put(9, "schwarzer");

            Map<Integer, String> third = new HashMap<Integer, String>();
            third.put(0, "alter");
            third.put(1, "neuer");
            third.put(2, "aktueller");
            third.put(3, "neuwertiger");
            third.put(4, "kaputter");
            third.put(5, "schöner");
            third.put(6, "seltener");
            third.put(7, "günstiger");
            third.put(8, "verzierter");
            third.put(9, "restaurierter");

            Map<Integer, String> fourth = new HashMap<Integer, String>();
            fourth.put(0, "deutscher");
            fourth.put(1, "französischer");
            fourth.put(2, "englischer");
            fourth.put(3, "russischer");
            fourth.put(4, "amerikanischer");
            fourth.put(5, "irischer");
            fourth.put(6, "spanischer");
            fourth.put(7, "italienischer");
            fourth.put(8, "polnischer");
            fourth.put(9, "schwedischer");

            Map<Integer, String> fifth = new HashMap<Integer, String>();
            fifth.put(0, "Traktor");
            fifth.put(1, "LKW");
            fifth.put(2, "Tisch");
            fifth.put(3, "Stuhl");
            fifth.put(4, "Schrank");
            fifth.put(5, "Computer");
            fifth.put(6, "Radring");
            fifth.put(7, "Reifen");
            fifth.put(8, "Besen");
            fifth.put(9, "Pfahl");

           // System.out.println("Füge " + number + " Austellungsstücke ein...");
            for (int i = 0; i < number; i++)
            {
                String name = "";
                long sectionId = 0;
                long categoryId = 0;

                name += first.get(getNumberOnPos(i, 5)) + " ";
                name += second.get(getNumberOnPos(i, 4)) + " ";
                name += third.get(getNumberOnPos(i, 3)) + " ";
                name += fourth.get(getNumberOnPos(i, 2)) + " ";
                name += fifth.get(getNumberOnPos(i, 1));


                // insert section_id
                int testVar = getNumberOnPos(i, 1);
                Random rnd = new Random();

                if (testVar == 0 || testVar == 1)
                {
                    if (rnd.nextInt(2) == 0)
                    {
                        sectionId = 9;
                    }
                    else
                    {
                        sectionId = 10;
                    }
                }
                else
                {
                    switch (rnd.nextInt(5))
                    {
                        case 0:
                            sectionId = 2;
                            break;
                        case 1:
                            sectionId = 3;
                            break;
                        case 2:
                            sectionId = 4;
                            break;
                        case 3:
                            sectionId = 6;
                            break;
                        case 4:
                            sectionId = 7;
                            break;
                    }
                }

                //insert category
                switch (testVar)
                {
                    case 0:
                    case 1:
                        categoryId = 1;
                        break;
                    case 2:
                    case 3:
                    case 4:
                        categoryId = 2;
                        break;
                    case 5:
                        categoryId = 3;
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        categoryId = 4;
                        break;
                }

                DataAccess.getInstance().store(new Exhibit(name, "Ein " + name, new Long(sectionId), new Long(categoryId), 1, null, museumId, Math.random()%300 ));

                // Insert Image		
                byte[] bytes = Files.readAllBytes(Paths.get("data", "testImage.png"));
                DataAccess.getInstance().store(new Image(bytes, "Ein Testbild", i + 1));


                if ((i % 2000) == 0)
                {
                    conn.commit(); // commiting all 100 Exhibits 
                }
                // 1 -> 273 sec
                // 200 -> 33 sec
                // 500 -> 31 sec
                // 1000 -> 29 sec
                // 2000 -> 28 sec
                // 5000 -> 29 sec
                // 10000 -> 28 sec
                if ((i % helper == 0)) // Show status every 3%
                {
                    //System.out.println(i * 100 / number + "% Fertig");
                }
            }
            conn.commit(); // commit remaining exhibits
            //System.out.println(number + " Austellungsstücke eingefügt!");

            //Insert Outsourced
            DataAccess.getInstance().store(new Outsourced("Truck Ausstellung Münster", "Ein LKW geht zur Truckerausstellung nach Münster.", 
                    new Date(112, 10, 28, 16, 30, 001), null, null, new Long(2), new Long(2)));

            //Insert Label and their exhibit label
            Label label = new Label("teures Label");
            label.addExhibit_id(1L);
            DataAccess.getInstance().store(label);
            label = new Label("anderes Label");
            label.addExhibit_id(35L);
            DataAccess.getInstance().store(label);

            //System.out.println("Alle TestDaten erfolgreich eingefügt!");
            startTime = System.currentTimeMillis() - startTime;
            startTime /= 1000; //millis->sec
            //System.out.println("Dauer: " + startTime + " Sekunden.");
            conn.setAutoCommit(true);

        }
        catch (SQLException e)
        {

            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    
     private static int getNumberOnPos(int num, int pos)
    {
        int ret = 0;

        ret = ((num % (int) Math.pow(10, pos)) - (num % (int) Math.pow(10, pos - 1))) / (int) Math.pow(10, pos - 1);

        return ret;
    }
}
