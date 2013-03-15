package de.museum.berleburg.userInterface.table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;

public class TableModelExpiredOutsourced extends AbstractTableModel {

	/**
	 * TableModel
	 * 
	 * @author Way Dat To
	 */

	private static final long serialVersionUID = -7698587241618158995L;
	private static final Object[] header = { "Typ", "Name", "Museum", "StartDatum", "Enddatum",};
	private Object rowData[][];

	public TableModelExpiredOutsourced(ArrayList<Outsourced> arrayList) {
		rowData = new Object[arrayList.size()][];

		for (int i = 0; i < arrayList.size(); i++) {
			Outsourced outsourced = arrayList.get(i);
			String typ;
			String museum="";
			String startDateString;
			String endDateString;


			Long addId = outsourced.getAddress_id();
			if (addId == null || addId.equals(0L)) {
				typ = "Ausstellung";
			} else {
				typ = "Leihgabe";
			}

			try {
				museum = Access.searchMuseumID(outsourced.getMuseum_id()).getName();
			} catch (MuseumNotFoundException e) {
				InformationPanel.getInstance().setText(e.getMessage());
				e.printStackTrace();
			}
			
		    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");  
		      
		    Date startDate = outsourced.getStartDate();  
		    Date endDate = outsourced.getEndDate();  
		      
			startDateString = sdf.format(startDate);
			endDateString = sdf.format(endDate);

			rowData[i] = new Object[] { typ, outsourced.getName(), museum, startDateString, endDateString,
					outsourced.getId() };
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
			return Long.class;
		default:
			return String.class;
		}
	}

}
