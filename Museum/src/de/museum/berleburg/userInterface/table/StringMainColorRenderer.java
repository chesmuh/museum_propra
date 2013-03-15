package de.museum.berleburg.userInterface.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class StringMainColorRenderer extends JLabel implements TableCellRenderer
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 9017657309779449318L;

	private Boolean isOutsourced;
	private Boolean isExpired;
	private Boolean isDeleted;
	private Color green = new Color(152,251,152);
	
	
	//Link für Farben:
	//http://cloford.com/resources/colours/500col.htm
	
	public StringMainColorRenderer()
	{
	    setOpaque(true);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
	{
			TableModelMain model = (TableModelMain) table.getModel();
			this.isOutsourced = model.isOutsourced(row);
			this.isExpired = model.isExpired(row);
			this.isDeleted = model.isDeleted(row);
			
		    this.setVerticalAlignment(JLabel.CENTER);
		    this.setHorizontalAlignment(JLabel.CENTER);
			
			
			
			if (value != null) 
			 {
				 setText(value.toString());
				 if(isSelected)
					{
						setBackground(green);
						setForeground(java.awt.Color.black);
					}
					else
					{
						setForeground(java.awt.Color.black);
						setBackground(java.awt.Color.white);
						 
						if(isOutsourced)
						 {
							 setForeground(java.awt.Color.black);
							 setBackground(java.awt.Color.lightGray);
							 
							 if(isExpired)
							 {
								 setForeground(java.awt.Color.black);
								 setBackground(java.awt.Color.red);
							 }
						 }
						if(isDeleted && col == 4)
						{
							setForeground(java.awt.Color.red);
						}
						if(isDeleted && col == 4 && isExpired)
						{
							setForeground(java.awt.Color.black);
						}
						
					}
				
			 }
				
	     return this;
	 }
}