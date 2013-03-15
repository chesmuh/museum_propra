package de.museum.berleburg.userInterface.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class BooleanSyncColorRenderer extends JCheckBox implements TableCellRenderer
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 9017657309779449318L;

	private Boolean isConflict;
	private Color green = new Color(152,251,152);

	
	public BooleanSyncColorRenderer()
	 {
	     setOpaque(true);
	 }
	
	@Override 
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	     {
		 
		 TableModelSync model = (TableModelSync) table.getModel();
			this.isConflict = model.isConflict(row);
		    this.setVerticalAlignment(JLabel.CENTER);
		    this.setHorizontalAlignment(JLabel.CENTER);

			
			
			if (value != null) 
			 {
				 this.setSelected((boolean) value);
				 
				 if(isSelected)
					{
						setBackground(green);
						
					}
					else
					{
						setBackground(java.awt.Color.white);
						 
						if(isConflict)
						 {
							 setBackground(java.awt.Color.yellow);
							 
						 }
					}
			 }
	     return this;
	 }
}