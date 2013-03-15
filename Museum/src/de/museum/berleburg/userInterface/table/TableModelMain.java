package de.museum.berleburg.userInterface.table;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logic.OutsourcedLogic;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.InformationPanel;

public class TableModelMain extends AbstractTableModel {

	/**
	 * TableModel
	 * 
	 * @author Frank Hülsmann
	 */
	private static final long serialVersionUID = -7698587241618158995L;
	private static final Object[] header = { "", "Name", "Sektion",
			"Kategorie", "Status" };
	private Object rowData[][];

	public TableModelMain(ArrayList<Exhibit> data) {
		rowData = new Object[data.size()][];

		for (int i = 0; i < data.size(); i++) {
			Exhibit exhibit = data.get(i);
			String category = "category";
			String section = "section";
			String status = "Status";
			boolean isExpired = false;
			boolean isOutsourced = false;
			boolean isDeleted = false;
			status = "im Museum";

			if (exhibit.isDeleted()) {
				status = "gelöscht";
				isDeleted = true;
			} else {
				for (Outsourced outsourced : exhibit.getOutsourced()) {
					if (null == outsourced.givenBack(exhibit.getId())) {
						isOutsourced = true;
						if (OutsourcedLogic.isExpired(outsourced)) {
							status = "überfällig";
							isExpired = true;
						} else {
							if (outsourced.isLoan()) {
								status = "ausgeliehen";
							} else {
								status = "ausgestellt";
							}
						}
					}
				}

			}

			try {
				Long catId = exhibit.getCategory_id();
				if (catId == null || catId.equals(0L)) {
					category = "Sonstiges";
				} else {
					category = Access.searchCategoryID(catId).getName();
				}

			} catch (CategoryNotFoundException e1) {
				InformationPanel.getInstance().setText(e1.getMessage());
				e1.printStackTrace();
			}

			try {
				Long secId = exhibit.getSection_id();
				if (secId == null || secId.equals(0L)) {
					section = "keiner Sektion zugeordnet";
				} else {
					section = Access.searchSectionID(secId).getName();
				}
			} catch (SectionNotFoundException e1) {
				InformationPanel.getInstance().setText(e1.getMessage());
				e1.printStackTrace();
			}

			rowData[i] = new Object[] { false, exhibit.getName(), section,
					category, status, exhibit.getId(), isOutsourced, isExpired,
					isDeleted };
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

	/**
	 * 
	 * @param row
	 * @return isExpired
	 */
	public Boolean isExpired(int row) {
		return (Boolean) rowData[row][7];
	}

	/**
	 * 
	 * @param row
	 * @return isOutsourced
	 */
	public Boolean isOutsourced(int row) {
		return (Boolean) rowData[row][6];
	}

	/**
	 * 
	 * @param row
	 * @return isDeleted
	 */
	public Boolean isDeleted(int row) {
		return (Boolean) rowData[row][8];
	}

	@Override
	public String getColumnName(int column) {
		return header[column].toString();
	}

	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return true;
		} else {
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
		case 4:
			return String.class;
		default:
			return String.class;
		}
	}
}
