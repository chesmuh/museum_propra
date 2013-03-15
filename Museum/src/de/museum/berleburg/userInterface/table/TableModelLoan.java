package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ContactNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;



public class TableModelLoan extends AbstractTableModel{
	
	/**
	 * TableModel
	 * 
	 * @author Frank Hülsmann
	 */
	
	private static final long serialVersionUID = -7698587241618158995L;
	private static final String[] header = {"Kontaktperson", "Ausgeliehen bis"};

    private Object rowData[][];
 
    public TableModelLoan(ArrayList<Outsourced> data) {
    	
    	rowData = new Object[data.size()][];
        for (int i = 0; i < data.size(); i++) {
        	Outsourced outsourced = data.get(i);
        	String name = null;
        	String forename = null;
        	String nameResult = null;
        	
        	try {
				name = Access.searchContactID(outsourced.getContact_id()).getName();
			} catch (ContactNotFoundException e) {
				InformationPanel.getInstance().setText(e.getMessage());
				e.printStackTrace();
			}
        	try {
				forename = Access.searchContactID(outsourced.getContact_id()).getForename();
			} catch (ContactNotFoundException e) {
				InformationPanel.getInstance().setText(e.getMessage());
				e.printStackTrace();
			}
        	
        	nameResult = name + ", " + forename;
        	
        	rowData[i] =  new Object[] {nameResult , outsourced.getEndDate(), outsourced.getId() };
        } 
         
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
    
    @Override
    public String getColumnName(int column) {
        return header[column];
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
    /**
     * 
     * @param row
     * @return id
     */
    public long getId(int row) {
        return (long) rowData[row][2];
    }
    

    
    
    
    
	

}


