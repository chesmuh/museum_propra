package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;


public class TableModelContact extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3189262865729995639L;
	/**
	 * TableModelContact
	 * 
	 * @author Frank Hülsmann
	 * @author Way Dat To (Just added Museum & Roles)
	 */
	

	private static final Object[] header = {"Name", "Vorname", "Rolle", "Museum","Telefon", "EMail", "Fax"};
    private Object rowData[][]; 
    
    public TableModelContact(ArrayList<Contact> data) 
    {      
    	
        rowData = new Object[data.size()][];
   
	    	for (int i = 0; i < data.size(); i++) 
	    	{ 	
	        	Contact contact = data.get(i);
	        	Long contactId = contact.getId();
	        	String name = contact.getName();
	        	String forename = contact.getForename();
	        	String fon = contact.getFon();
	        	String email = contact.getEmail();
	        	String fax = contact.getFax();
	        	String role = "-";
	        	String museum= "-";
	        	Long roleId = contact.getRoleId();
	        	
	        	if (roleId == null || roleId == 0L){
	        		
	        	}
	        	else
	        	{
	        		role = Access.searchRoleId(roleId).getName();
	        		
	        		try {
						museum = Access.searchMuseumID(Access.searchRoleId(roleId).getMuseum_id()).getName();
					} catch (MuseumNotFoundException e) {
						InformationPanel.getInstance().setText(e.getMessage());
						e.printStackTrace();
					}
	        	}

	        	

	        			
	   
	          	
	        	rowData[i] =  new Object[] { name, forename, role, museum, fon, email, fax, contactId};
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
     * @return contactId
     */
    public Long getContactId(int row) {
        return (Long) rowData[row][7];
    }
    
    public String getFullName(int row) {
         String name = (String) rowData[row][0];
         String forename = (String) rowData[row][1];
         String result = name + ", " + forename;
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


