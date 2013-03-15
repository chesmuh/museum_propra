package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;


public class TableModelMassChange extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3189262865729995639L;
	/**
	 * TableModel
	 * 
	 * @author Alexander Adema
	 * @author Frank Hülsmann
	 */
	

	private static final Object[] header = {"Name", "Sektion", "Kategorie"};
    private Object rowData[][]; 
    
    public TableModelMassChange(ArrayList<Exhibit> data) 
    {        
        rowData = new Object[data.size()][];
   
	    	for (int i = 0; i < data.size(); i++) 
	    	{ 	
	        	Exhibit exhibit = data.get(i);
	        	String category = "category";
	        	String section = "section";
	      
	 
	        	
	        	try {
	        		Long catId = exhibit.getCategory_id();
	        		if (catId==null || catId.equals(0L)){
	        			category = "Sonstiges";
	        		}
	        		else
					category = Access.searchCategoryID(catId).getName();
					
				} catch (CategoryNotFoundException e1) {
					InformationPanel.getInstance().setText(e1.getMessage());
					e1.printStackTrace();
				} 
	        	
	        	try {
	        		Long secId = exhibit.getSection_id();
	        		if (secId==null || secId.equals(0L)){
	        			section = "keiner Sektion zugeordnet";
	        		}
	        		else
					section = Access.searchSectionID(secId).getName();
				} catch (SectionNotFoundException e1) {
					InformationPanel.getInstance().setText(e1.getMessage());
					e1.printStackTrace();
				} 
	        	
	        	
				
	        	
	        	rowData[i] =  new Object[] { exhibit.getName(), section, category, exhibit.getId() };
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
     * @return id
     */
    public long getId(int row) {
        return (long) rowData[row][5];
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
            default:
                return String.class;
        }
    }


}


