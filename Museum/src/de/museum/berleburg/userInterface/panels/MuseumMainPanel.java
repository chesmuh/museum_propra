package de.museum.berleburg.userInterface.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.CreateMuseum;

/**
 * Create the ComboBox and TreeMainPanel.
 *
 * @author Maximilian Beck
 *
 */
public class MuseumMainPanel extends JPanel {


  private static final long serialVersionUID = 4913986989231819192L;
  private JComboBox<TreeNodeObject> comboBoxMuseum;
  private static MuseumMainPanel instance = null;
  private Long museumId;
  private TreeNodeObject noMuseum = new TreeNodeObject("<KEIN MUSEUM VORHANDEN>");
  private Museum toSet=null;
  
  
  public MuseumMainPanel() {
    instance=this;
    
    setLayout(new BorderLayout(0, 0));
    
    
    JPanel northpanel = new JPanel();
    comboBoxMuseum = new JComboBox<TreeNodeObject>();
    this.noMuseum.setMuseumId(null);
 
        
    
    refreshComboBox();
    
    comboBoxMuseum.addActionListener (new ActionListener () {
        public void actionPerformed(ActionEvent e) {
        	if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
        		
        		TreeNodeObject o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();
        		if(o==null)return;
        		setMuseumId(o.getMuseumId());
        		TreeMainPanel.getInstance().refreshTree();
        		try {
					MainGUI.getDetailPanel().setDetails(Access.searchMuseumID(o.getMuseumId()==null?0:o.getMuseumId()));
					ArrayList<Long> empty = new ArrayList<>();
					TablePanel.getInstance().updateTable(o.getMuseumId(), empty, empty, empty, empty, empty, empty, empty, empty);
				} catch (MuseumNotFoundException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler",
							JOptionPane.ERROR_MESSAGE);
				}
        		
        	}
        }
    });
    
    northpanel.add(comboBoxMuseum);
    add(northpanel, BorderLayout.NORTH);
    
    
//    JSeparator separator = new JSeparator();
//    separator.setMaximumSize(new Dimension(0, 1));
//    northpanel.add(separator);
    
    
    TreeMainPanel southpanel = TreeMainPanel.getInstance();
    add(southpanel, BorderLayout.CENTER);

  }
  
public void refreshComboBox(){
  
    ArrayList<Museum> allmuseums = Access.getAllMuseums();    
    
    if (allmuseums.isEmpty()){
      comboBoxMuseum.addItem(noMuseum);
      comboBoxMuseum.setSelectedItem(noMuseum);
      this.noMuseum.setMuseumId(null);
      TreeMainPanel.getInstance().refreshTree();
      
      CreateMuseum dialog = new CreateMuseum();
      dialog.setVisible(true);
      
      refreshComboBox();
      return;
    }
    else {
    	long selectedMuseum = toSet!=null?toSet.getId():0;
    	comboBoxMuseum.removeAllItems();
    	ArrayList<Museum> allmuseumsNew = Access.getAllMuseums();
      for (Museum i : allmuseumsNew) {
        TreeNodeObject o = new TreeNodeObject(i.getName());
        o.setMuseumId(i.getId());
        comboBoxMuseum.addItem(o);
        if(i.getId().equals(selectedMuseum)&&toSet!=null){
        	comboBoxMuseum.setSelectedItem(o);
        	setMuseumId(i.getId());
        }
        
      }
   }
      
      toSet = null;
    }

  
  /* Getter */
  
/**
 * 
 * @return MuseumMainPanel actual instance
 */
public static MuseumMainPanel getInstance(){
    
    if (instance == null) {
      
      instance = new MuseumMainPanel();
    }
    return instance;
    
}
  
/**
 * 
 * @return Long the museum id
 */
  public Long getMuseumId()
  {
    return museumId;
  }
  
  /**
   * 
   * @return JComboBox actual comboBox
   */
  public JComboBox<TreeNodeObject> getComboBoxMuseum(){
	  return comboBoxMuseum;
  }
  
  /**
   * 
   * @return String museum name
   */
  public String getMuseumName()
  {
    
     TreeNodeObject o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();
     String museumName = o.getName();
    return museumName;
  }
  
  /**
   * 
   * @return TreeNodeObject museum node
   */
  public TreeNodeObject getMuseumTreeNode()
  {
	  for(int i=0; i< comboBoxMuseum.getItemCount(); i++){
			if(comboBoxMuseum.getItemAt(i).getMuseumId() == museumId)
				return comboBoxMuseum.getItemAt(i);
		}
     TreeNodeObject o = (TreeNodeObject) comboBoxMuseum.getSelectedItem();
    
    return o;
  }
  
  /**
   * 
   * @return A Combo clone
   */
  public JComboBox<TreeNodeObject> getComboClone(){
	  JComboBox<TreeNodeObject> result = new JComboBox<TreeNodeObject>();
	  for(int i=0; i<comboBoxMuseum.getItemCount(); i++){
		  result.addItem(comboBoxMuseum.getItemAt(i));
	  }
	  return result;
  }
 
  /* Setter */
  
  /**
   * 
   * @param museumid
   */
  public void setMuseumId(Long id)
  {
    this.museumId = id;
  }
  
  /**
   * 
   * @param museumTreeNode
   */
  public void setMuseumTreeNode(TreeNodeObject museumTreeNode){
	  comboBoxMuseum.setSelectedItem(museumTreeNode);
  }
  
  /**
   * 
   * @param museum
   */
  public void setMuseumToSelect(Museum museum)
  {
	  toSet = museum;
  }
  
  
  
}
