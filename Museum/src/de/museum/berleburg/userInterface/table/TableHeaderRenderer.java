package de.museum.berleburg.userInterface.table;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.TableButtonPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;

public class TableHeaderRenderer implements TableCellRenderer {

	/**
	 * TableHeaderRenderer
	 * 
	 * @author Frank Hülsmann
	 */
	
	
	private final JCheckBox check = new JCheckBox();
	private TablePanel tablePanel;
	private Boolean isForwardSortName = new Boolean(null);
	private Boolean isForwardSortSection = new Boolean(null);
	private Boolean isForwardSortCategory = new Boolean(null);
	private Boolean isForwardSortState = new Boolean(null);
	
	
	public TableHeaderRenderer(JTableHeader header) {
		this.tablePanel = TablePanel.getInstance();
	    check.setOpaque(false);
	    check.setFont(header.getFont());
		       
	    header.addMouseListener(new MouseAdapter() {
	
	        @Override
	        public void mouseClicked(MouseEvent e) 
	        {
	            JTable table = ((JTableHeader) e.getSource()).getTable();           
	            TableColumnModel columnModel = table.getColumnModel();
	            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
	            int modelColumn = table.convertColumnIndexToModel(viewColumn);
	            if (modelColumn == 0) {
	            	tablePanel.checkAllCheckBoxes();
	            	TableButtonPanel.getInstance().setButtonsEnabled(true);
	                check.setSelected(!check.isSelected());
	                javax.swing.table.TableModel m = table.getModel();
	                Boolean f = check.isSelected();
	                for (int i = 0; i < m.getRowCount(); i++) {
	                    m.setValueAt(f, i, 0);
	                }
	                ((JTableHeader) e.getSource()).repaint();
	            }
	            
	           if(modelColumn == 1)
	           {
		            if (!isForwardSortName || isForwardSortName == null)
		            {      
			            TablePanel.getInstance().sortTable(Access.sortExhibitsByName(TablePanel.getInstance().getCurrentList()));
			            isForwardSortName = true;
		            }
		            else if (isForwardSortName)
		            {
		            	TablePanel.getInstance().sortTable(Access.sortReverseExhibitsByName(TablePanel.getInstance().getCurrentList()));
		                isForwardSortName = false;
		            }
	           }
	           
	           if(modelColumn == 2)
	           {
		            if (!isForwardSortSection || isForwardSortSection == null)
		            {      
			            TablePanel.getInstance().sortTable(Access.sortExhibitsBySection(TablePanel.getInstance().getCurrentList()));
			            isForwardSortSection = true;
		            }
		            else if (isForwardSortSection)
		            {
		            	TablePanel.getInstance().sortTable(Access.sortReverseExhibitsBySection(TablePanel.getInstance().getCurrentList()));
		                isForwardSortSection = false;
		            }
	           }
	           
	           if(modelColumn == 3)
	           {
		            if (!isForwardSortCategory || isForwardSortCategory == null)
		            {      
			            TablePanel.getInstance().sortTable(Access.sortExhibitsByCategory(TablePanel.getInstance().getCurrentList()));
			            isForwardSortCategory = true;
		            }
		            else if (isForwardSortCategory)
		            {
		            	TablePanel.getInstance().sortTable(Access.sortReverseExhibitsByCategory(TablePanel.getInstance().getCurrentList()));
		                isForwardSortCategory = false;
		            }
	           }
	           
	           if(modelColumn == 4)
	           {
		            if (!isForwardSortState || isForwardSortState == null)
		            {      
			            TablePanel.getInstance().sortTable(Access.sortExhibitsByState(TablePanel.getInstance().getCurrentList()));
			            isForwardSortState = true;
		            }
		            else if (isForwardSortState)
		            {
		            	TablePanel.getInstance().sortTable(Access.sortReverseExhibitsByState(TablePanel.getInstance().getCurrentList()));
		                isForwardSortState = false;
		            }
	           }
       
	      }
	    });
	}
	

	@Override
	public Component getTableCellRendererComponent(
	        JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
	    TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
	    JLabel l = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
	    l.setIcon(new CheckBoxIcon(check));
	    return l;
	}

	private static class CheckBoxIcon implements Icon {
	
	    private final JCheckBox check;
	
	    public CheckBoxIcon(JCheckBox check) {
	        this.check = check;
	    }
	
	    @Override
	    public int getIconWidth() {
	        return check.getPreferredSize().width;
	    }
	
	    @Override
	    public int getIconHeight() {
	        return check.getPreferredSize().height;
	    }
	
	    @Override
	    public void paintIcon(Component c, Graphics g, int x, int y) {
	        SwingUtilities.paintComponent(
	                g, check, (Container) c, x, y, getIconWidth(), getIconHeight());
	    }
	}
}