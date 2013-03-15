package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.logicAccess.SyncModel;


public class TableModelSync extends AbstractTableModel {
	
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
	

	private static final Object[] header = {"", "Typ", "Name", "Zuletzt geändert" };
    private Object rowData[][]; 
    
    public TableModelSync(ArrayList<SyncModel> data) 
    {      
    	
        rowData = new Object[data.size()][];
   
	    	for (int i = 0; i < data.size(); i++) 
	    	{ 	
	        	SyncModel model = data.get(i);
	      
	        	String type = model.getType();
	        	String name = model.getName();
	        	String timestamp = model.getTimestamp();
	        	boolean isConflict = model.isConflict();
	        	boolean isLocal = model.isLocal();
	        	long id = model.getId();

	        	rowData[i] =  new Object[] { false, type, name, timestamp, isConflict, isLocal, i, id};
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
     * @param value
     * @param row
     */
    public void setCheckBox(boolean value, int row)
    {
    	rowData[row][0] = value;
    }
    /**
     * 
     * @param id
     * @return row
     */
    public int getRowById(long id)
    {
    	for(int i=0; i<rowData.length; i++)
    	{
    		Long iD = (Long) rowData[i][7];
    		if(iD == id)
    		{	
    			return i;
    		}	
    	}
    	return -1;
    }
    /**
     * 
     * @param row
     * @return isConflict
     */
    public boolean isConflict(int row)
    {
    	return (boolean) rowData[row][4];
    }
    
    /**
     * 
     * @param row
     * @return isLocal
     */
    public boolean isLocal(int row)
    {
    	return (boolean) rowData[row][5];
    }
    /**
     * 
     * @param row
     * @return listPosition
     */
    public int getListPosition(int row) {
        return (int) rowData[row][6];
    }
    /**
     * 
     * @param row
     * @return id
     */
    public long getId(int row) {
        return (long) rowData[row][7];
    }
    
    @Override
    public String getColumnName(int column) {
        return header[column].toString();
    }
    
    public boolean isCellEditable(int row, int col) {
      
    	if (col == 0)
    	{
    		return true;
    	}
    	else
    	{
            return false;
    	}
    }
    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            default:
                return String.class;
        }
    }


}


