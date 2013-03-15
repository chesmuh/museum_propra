package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.logicAccess.Access;



public class TableModelAdress extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3189262865729995639L;
	/**
	 * TableModelAdress
	 * 
	 * @author Frank Hülsmann
	 */
	

	private static final Object[] header = {"Straße", "PLZ", "Stadt", "Bundesland", "Land", "Museum", "Kontakt"};
    private Object rowData[][]; 
    
    public TableModelAdress(ArrayList<Address> data) 
    {        
        rowData = new Object[data.size()][];
   
	    	for (int i = 0; i < data.size(); i++) 
	    	{ 	
	    		ArrayList<Contact> contactList = new ArrayList<Contact>();
	    		ArrayList<Museum> museumList = new ArrayList<Museum>();
	    		
	    		StringBuilder contactBuilder = new StringBuilder();
	    		StringBuilder museumBuilder = new StringBuilder();
	    		
	        	Address address = data.get(i);
	        	Long addressId = address.getId();
	        	String street = address.getStreet() + " " + address.getHousenumber();
	        	String zip = address.getZipcode();
	        	String city = address.getTown();
	        	String state = address.getState();
	        	String country = address.getCountry();

	        	
	        	museumList = Access.searchMuseumByAddressId(addressId);
	        	
	        	contactList = Access.searchContactByAddressId(addressId);
	        	
	        	
	        	int countContact = 0;
	        	for (Contact actual : contactList)
	        	{
	        		countContact++;
	        		contactBuilder.append(actual.getName());
	        		contactBuilder.append(", ");
	        		contactBuilder.append(actual.getForename());
	        		if(countContact != contactList.size())
	        			contactBuilder.append(" ; ");
	        	}
	        	
	        	int countMuseum = 0;
	        	for (Museum actual : museumList)
	        	{
	        		countMuseum++;
	        		museumBuilder.append(actual.getName());
	        		if(countMuseum != museumList.size())
	        		museumBuilder.append("; ");
	        	}
	        	
	        	
			
				
	        	
	        	rowData[i] =  new Object[] { street, zip, city, state, country, museumBuilder.toString(), contactBuilder.toString(), addressId };
	        } 
	    	fireTableDataChanged();
    }
    
	public int getRowCount() {
        return rowData.length;
    }
 
    public int getColumnCount() {
        return header.length;
    }
    
    public void setValueAt(Object value, int row, int col) { 
    	rowData[row][col] = value;
    	fireTableCellUpdated(row, col);
    }
 
    public Object getValueAt(int row, int col) {
        return rowData[row][col];
    }
    /**
     * 
     * @param row
     * @return addressId
     */
    public Long getAddressId(int row) {
        return (Long) rowData[row][7];
    }
    /**
     * 
     * @param row
     * @return addressName
     */
    public String getAddressName(int row) {
        
    	String street = (String) rowData[row][0];
    	String zip = (String) rowData[row][1];
    	String city = (String) rowData[row][2];
    	String result = street + ", " + zip + " " + city;
    	
    	return result;
    }

    
    @Override
    public String getColumnName(int column) {
        return header[column].toString();
    }
    
    public boolean isCellEditable(int row, int col) {
      
            return false;
        
    }
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return String.class;
            case 5:
                return Long.class;
            default:
                return String.class;
        }
    }


}


