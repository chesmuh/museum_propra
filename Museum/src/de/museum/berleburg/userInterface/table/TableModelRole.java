package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;


public class TableModelRole extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3189262865729995639L;
	/**
	 * TableModelRole
	 * 
	 * @author Frank Hülsmann
	 */
	

	private static final Object[] header = {"Name", "Museum"};
    private Object rowData[][]; 
    
    public TableModelRole(ArrayList<Role> data) 
    {        
        rowData = new Object[data.size()][];
   
	    	for (int i = 0; i < data.size(); i++) 
	    	{ 	
	        	Role role = data.get(i);
	        	Long roleId = role.getId();
	        	Long museumId = role.getMuseum_id();
	        	String name = role.getName();
	        	String museumName = null;
				try {
					museumName = Access.searchMuseumID(role.getMuseum_id()).getName();
				} catch (MuseumNotFoundException e) {
					InformationPanel.getInstance().setText(e.getMessage());
					e.printStackTrace();
				}
				
	        	
	        	rowData[i] =  new Object[] { name, museumName, roleId, museumId };
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
     * @return roleId
     */
    public Long getRoleId(int row) {
        return (Long) rowData[row][2];
    }
    /**
     * 
     * @param row
     * @return museumId
     */
    public Long getMuseumId(int row){
        return (Long) rowData[row][2];
    }
    /**
     * 
     * @param row
     * @return roleName
     */
    public String getRoleName(int row) {
        return (String) rowData[row][0];
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
            default:
                return String.class;
        }
    }


}


