package de.museum.berleburg.userInterface.panels;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;

/**
 * Create the main TreePanel with TabbedPane.
 * 
 * @author Maximilian Beck
 */
public class TreeMainPanel extends JPanel {

	private static final long serialVersionUID = 5263059661424445115L;

	private TreeSectionPanel sectionTreePanel = new TreeSectionPanel(true);
	private TreeCategoryPanel categoryTreePanel = new TreeCategoryPanel(true);
	private TreeExhibitionPanel exhibitionTreePanel = new TreeExhibitionPanel(true);
	private TreeLabelPanel labelTreePanel = new TreeLabelPanel(true);
	private static TreeMainPanel instance = null;
	private JTabbedPane tabbedPane;

	public TreeMainPanel() {
		instance = this;

		setAutoscrolls(true);
		setLayout(new BorderLayout(0, 0));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);

		tabbedPane.addTab("Sektionen", null, sectionTreePanel,
				"Übersicht aller Sektionen");
		tabbedPane.setEnabledAt(0, true);

		tabbedPane.addTab("Kategorien", null, categoryTreePanel,
				"Übersicht aller Kategorien");
		tabbedPane.setEnabledAt(1, true);

		tabbedPane.addTab("Ausstellungen / Leihgaben", null, exhibitionTreePanel,
				"Übersicht aller Ausstellungen");
		tabbedPane.setEnabledAt(2, true);

		tabbedPane.addTab("Label", null, labelTreePanel,
				"Übersicht aller Label");
		tabbedPane.setEnabledAt(3, true);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
	        		
	        		refreshOtherTrees();
	        		TableButtonPanel.getInstance().setRetoureVisible(false);
			}
		}); 

	}
	
	/**
	 * Refreshes all the trees
	 */

	public void refreshTree() {
		this.sectionTreePanel.refreshTree();
		this.categoryTreePanel.refreshTree();
		this.exhibitionTreePanel.refreshTree();
		this.labelTreePanel.refreshTree();
	}
	
	public void refreshOtherTrees(){
		long index = tabbedPane.getSelectedIndex();
		ArrayList<Long> emptyList = new ArrayList<>();

		if (index == 0){
			
			if(sectionTreePanel.getLastSelected() != null)
			{
				if(sectionTreePanel.isRoot())
				{
					ArrayList<Section> sections = new ArrayList<>();
					try {
						sections = Access
								.getAllSubSectionsFromMuseum(MainGUI.getMuseumMainPanel().getMuseumId());
					} catch (MuseumNotFoundException e) {
						JOptionPane.showMessageDialog(null,
								e.getMessage(), "Fehler",
								JOptionPane.ERROR_MESSAGE);
					}
					ArrayList<Long> sectionId = new ArrayList<Long>();
					for (Section s : sections) {
						sectionId.add(s.getId());
					}
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), emptyList, emptyList, emptyList, sectionId, emptyList, emptyList, emptyList, emptyList);
				}
				else
				{
					long id = sectionTreePanel.getLastSelected().getSectionId();
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), 0, id, 0, 0);
				}
			}
			else
			{
				TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, null, null, null, null, null, null, null, null, false, true);
			}
			this.categoryTreePanel.refreshTree();
			this.exhibitionTreePanel.refreshTree();
			this.labelTreePanel.refreshTree();
			
		}
		else if (index == 1){
			if(categoryTreePanel.getLastSelected() != null)
			{
				if(categoryTreePanel.isRoot())
				{
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, false, true);
				}
				else
				{
					long id = categoryTreePanel.getLastSelected().getCategoryId();
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), id, 0, 0, 0);
				}
			}
			else
			{
				TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, null, null, null, null, null, null, null, null, false, true);
			}
			this.sectionTreePanel.refreshTree();
			this.exhibitionTreePanel.refreshTree();
			this.labelTreePanel.refreshTree();
			
		}
		else if (index == 2){
			if(exhibitionTreePanel.getLastSelected() != null)
			{
				
					if(exhibitionTreePanel.isRoot())
					{
						ArrayList<Outsourced> allOut = new ArrayList<>();
						try {
							allOut = Access
									.getAllOutsourced(MainGUI.getMuseumMainPanel().getMuseumId());
						} catch (MuseumNotFoundException e) {
							JOptionPane.showMessageDialog(null,
									e.getMessage(), "Fehler",
									JOptionPane.ERROR_MESSAGE);
						}
						ArrayList<Long> outId = new ArrayList<Long>();
						
						for (Outsourced out : allOut) {
							outId.add(out.getId());
						}
						if (outId.isEmpty()) {
							TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), "", emptyList, emptyList,
									emptyList, emptyList, emptyList, emptyList,
									outId, emptyList, false, true);
						} else {
							TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), emptyList, emptyList,
									emptyList, emptyList, emptyList, emptyList,
									outId, emptyList);
						}
					}
					else
					{
						
						Long id = exhibitionTreePanel.getLastSelected().getExhibitionId();
						if (id != null)
						{
							TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), 0, 0, 0, id);
						}
						else
						{
							TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, null, null, null, null, null, null, null, null, false, true);
						}
						
						
						
					}
				
			}
			else
			{
				TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, null, null, null, null, null, null, null, null, false, true);
			}
			this.categoryTreePanel.refreshTree();
			this.sectionTreePanel.refreshTree();
			this.labelTreePanel.refreshTree();
			
		}
		else if (index == 3){
			if(labelTreePanel.getLastSelected() != null)
			{
				if(labelTreePanel.isRoot())
				{
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, emptyList, false, true);
				}
				else
				{
					long id = labelTreePanel.getLastSelected().getLabelId();
					TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), 0, 0, id, 0);
				}
			}
			else
			{
				TablePanel.getInstance().updateTable(MainGUI.getMuseumMainPanel().getMuseumId(), null, null, null, null, null, null, null, null, null, false, true);
			}
			this.categoryTreePanel.refreshTree();
			this.exhibitionTreePanel.refreshTree();
			this.sectionTreePanel.refreshTree();
			
		}
		
	}

	/* Getter */

	/**
	 * 
	 * @return TreeMainPanel actual instance
	 */
	public static TreeMainPanel getInstance() {

		if (instance == null) {

			instance = new TreeMainPanel();
		}
		return instance;

	}

	/**
	 * 
	 * @return Long the museum id of selected tab
	 * @throws MuseumNotFoundException
	 */
	public Long getMuseumId() throws MuseumNotFoundException {
		long index = tabbedPane.getSelectedIndex();

		if (index == 0)
			return this.sectionTreePanel.getMuseumId();
		else if (index == 1)
			return this.categoryTreePanel.getMuseumId();
		else if (index == 2)
			return this.exhibitionTreePanel.getMuseumId();
		else if (index == 3)
			return this.labelTreePanel.getMuseumId();
		else {
			throw new MuseumNotFoundException("Museum wurde nicht gefunden!");
		}

	}


	/**
	 * 
	 * @return TreeSectionPanel
	 */
	public TreeSectionPanel getTreeSectionPanel() {
		return sectionTreePanel;
	}

	/**
	 * 
	 * @return TreeCategoryPanel
	 */
	public TreeCategoryPanel getTreeCategoryPanel() {
		return categoryTreePanel;
	}

	/**
	 * 
	 * @return TreeExhibitionPanel
	 */
	public TreeExhibitionPanel getTreeExhibitionPanel() {
		return exhibitionTreePanel;
	}

	/**
	 * 
	 * @return TreeLabelPanel
	 */
	public TreeLabelPanel getTreeLabelPanel() {
		return labelTreePanel;
	}

	/**
	 * 
	 * @return JTabbedPane selected tab
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

}
