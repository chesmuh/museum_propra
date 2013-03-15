package de.museum.berleburg.userInterface.dialogs;

//internal imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.listeners.CategoryListener;
import de.museum.berleburg.userInterface.listeners.ExhibitListener;
import de.museum.berleburg.userInterface.listeners.LabelListener;
import de.museum.berleburg.userInterface.listeners.SectionListener;
import de.museum.berleburg.userInterface.listeners.SimpleListener;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;

/**
 * A Dialog with all options that are needed to create, edit or view an exhibit,
 * including viewing, deleting and adding pictures and viewing its history data.
 * 
 * @author Christian Landel
 */
@SuppressWarnings("serial")
public class EditExhibit extends JDialog {
	/* test this dialog without the rest of the application */
	/*
	 * public static void main (String args[]) { noThrow=true; new
	 * EditExhibit(null,null,null).setVisible(true); }
	 */
	/** null if a new exhibit is to be created,
	 *  otherwise the item that is to be edited */
	private Exhibit currentItem = null;
	/** a list of all currently open instances */
	private static Collection<EditExhibit> openedItems = new LinkedList<EditExhibit>();
	/** the list of listeners that will be notified when "ok"
	 *  or "ok&amp;new" was clicked */
	private LinkedList<ExhibitListener> listeners = new LinkedList<ExhibitListener>();
	/** if editing is disabled, don't ask for confirmation when closed */
	private boolean editingDisabled = false;
	/** sets the section for subsequent dialogs, if null is given to the
	 * constructor or the parameter does not exist in the constructor */
	private static Section nextSection = null;
	/** sets the category for subsequent dialogs, if null is given to the
	 * constructor or the parameter does not exist in the constructor */
	private static Category nextCategory = null;
	/** the museum that the current item belongs to */
	private Museum museum = null;
	/** the section that is set in the dialog */
	private Section section = null;
	/** the category that is set in the dialog */
	private Category category = null;
	/** the labels that are assigned to this exhibit */
	private Collection<Label> labels = new LinkedList<Label>();
	/** The images that will be added (i.e., inserted into the database) after
	 *  the insert or update operation. This list is placed before the other list. */
	private Collection<byte[]> imagesToInsert = new LinkedList<byte[]>();
	/** The images that were loaded from the DB upon window creation. This list
	 *  is placed after the other list. */
	private Collection<Image> imagesExisting = new LinkedList<Image>();
	/** the images that are to be deleted (must all be in imagesExisting, otherwise...) */
	private Collection<Image> imagesToDelete = new LinkedList<Image>();
	/** The currently selected and displayed image. Null if both collections of the
	 * images that are being added and those that exist from an opened exhibit are empty. */
	private Object imageSelected = null;
	/** This variable is only true if this window was correctly initialized,
	 *  preventing actions from happening otherwise */
	private boolean initialized = false;
	/** This variable is true after the first change in the exhibit,
	 *  so the user will be asked if he wants to discard his changes. */
	private boolean changed = false;
	/** the panel that will contain a preview of the currently selected image */
	private JPanel panelImage;
	private JButton btnPreviousImage;
	private JButton btnNextImage;
	private JButton btnImageDelete;
	private JLabel lblImageInsert;
	private JLabel lblImageDelete;
	private JScrollPane scrollPaneHistory;
	/** the image that is generated from a byte array; null if both images lists are empty */
	private java.awt.Image imageToDisplay = null;
	// fields that will contain the input data
	private JTextField textFieldName;
	private JTextField textFieldRfid;
	private JTextField textFieldSection;
	private JTextField textFieldCategory;
	private JTextField textFieldCount;
	private JTextArea textAreaDescription;
	private JTextField textFieldLabels;
	private JTextField textFieldEuro;
	private JTextField textFieldCent;
	// buttons that will be greyed out if editing is disabled
	private JButton btnEdit;
	private JButton btnSection;
	private JButton btnCategory;
	private JButton btnLabels;
	private JButton btnImageAdd;
	private JButton btnSave;
    private JButton btnSaveAndNew;
    private JButton btnLt;
    private JButton btnGt;
    private JButton btnAmp;
    private JButton btnCopy;
    // other buttons
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnEnable;
	
	/**
	 * This creates a dialog that edits an existing exhibit.
	 * <div>If an exhibit that is already opened is opened again, nothing will
	 * happen, except the corresponding dialog will request focus.</div> 
	 * @param exhibit the exhibit to edit.
	 */
	public EditExhibit(Exhibit exhibit)
	{
		super(MainGUI.getFrame());
		for (EditExhibit instance : openedItems)
			// true if the corresponding exhibit is already open
			if (instance.getExhibit().getId().equals(exhibit.getId())) {
				super.dispose();
				initialized=false; //should be the case, anyway
				instance.requestFocus();
				return;
			}
		openedItems.add(this);
		currentItem = exhibit;
		try {
			museum = Access.searchMuseumID(exhibit.getMuseum_id());
			for (Image image : Access.searchPictureByExhibitId(exhibit.getId())) {
				if (imageSelected == null)
					imageSelected = image;
				imagesExisting.add(image);
			}
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
//			showMessageDialog(
//					"Fehler beim Laden aus der Datenbank: " + e.getMessage(),
//					"DB-Fehler", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, "Verbindungsfehler zur Datenbank!", "Datenbankfehler", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			openedItems.remove(exhibit);
			return;
		}

		if (exhibit.getSection_id() == null)
			section = null;
		else
			section = exhibit.getSection();
		if (exhibit.getCategory_id() == null)
			category = null;
		else
			category = exhibit.getCategory();
		labels = exhibit.getLabels();

		init();
		
		//update miscellaneous fields
		textFieldName.setText(exhibit.getName());
		textFieldRfid.setText(exhibit.getRfid());
		textFieldCount.setText("" + exhibit.getCount());
		textAreaDescription.setText(exhibit.getDescription());
		String[] price = Access.reParsePrice(exhibit.getWert());
		textFieldEuro.setText(price[0]);
		textFieldCent.setText(price[1]);
		
		//update the history table
		scrollPaneHistory.setViewportView(
				historyTable()
		);
	}

	/**
	 * This creates a new Exhibit when the save button is pressed. Also sets a
	 * chosen section and category (can be changed by the user); they can be
	 * null. Null pointers are overridden by the "next" variables (see
	 * the functions setNext...).
	 * 
	 * @wbp.parser.constructor
	 */
	public EditExhibit(Museum museum, Section section, Category category) {
		super(MainGUI.getFrame());
		this.museum = museum;
		this.section = section;
		this.category = category;
		if (section == null)
			this.section = nextSection;
		if (category == null)
			this.category = nextCategory;
		if (this.museum==null &&
			MuseumMainPanel.getInstance().getMuseumId()!=0)
			try {
				this.museum=Access.searchMuseumID(MuseumMainPanel.getInstance().getMuseumId());
			} catch (MuseumNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		if (this.category == null)
			try {
				this.category = Access.getMiscellaneousCategory(this.museum.getId());
			} catch (CategoryNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (MuseumNotFoundException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
//		
//		catch (Exception e) {
//				e.printStackTrace();
//			}
		init();
	}

	/**
	 * This creates a new Exhibit when the save button is pressed. The three
	 * parameters for the museum, the section and the category can be set beforehand
	 * with the "setNext..." functions.
	 */
	public EditExhibit() {
		super(MainGUI.getFrame());
		if (MuseumMainPanel.getInstance().getMuseumId()!=0)
			try {
				this.museum=Access.searchMuseumID(MuseumMainPanel.getInstance().getMuseumId());
			} catch (MuseumNotFoundException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			}
		this.section = nextSection;
		this.category = nextCategory;
		init();
	}

	/**
	 * Add a listener that will be notified when the dialog changes or creates
	 * an exhibit. <div>The function "event(...)" of all the added listeners
	 * will be called with a list that contains the corresponding item</div>
	 * example:
	 * 
	 * <pre>
	 * {@code}
	 * EditExhibit dialog = new EditExhibit();
	 * dialog.addListener( new ExhibitListener() {
	 * 	public void event (Collection&ltExhibit&gt exhibits) {
	 * 		for (Exhibit item : exhibits)
	 * 			System.out.println ("Changed Exhibit: " + item.getName());
	 * 	}
	 * });
	 * </pre>
	 */
	public void addListener(ExhibitListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Creates a table with the contents of the history elements of an exhibit.
	 * <div>If an error occurs, it will be shown therein.</div>
	 */
	private JTable historyTable ()
	{
		Collection<History> history = null;
		try {
			history = Access.searchHistoryElementsByExhibitId(currentItem.getId());
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Fehler beim Laden der Historie: "+e.getMessage(),
							              "DB-Fehler", JOptionPane.ERROR_MESSAGE);
//			JOptionPane.showMessageDialog(null, "Historieelement wurde nicht gefunden!", "Fehler", JOptionPane.ERROR_MESSAGE);
			Object[] st = e.getStackTrace();
			ArrayList<Object[]> show = new ArrayList<Object[]>(st.length+1);
			show.add(new Object[]{e});
			for (Object o : st)
				show.add(new Object[]{o});
			return new JTable(
					show.toArray(new Object[show.size()][]),
					new String[]{"<fehler>"});
		}
		//in each row, indices 0 to 3 (4 columns) are visible, if there are more items, they are not shown
		//more specifically, index 4 will be used to reference the selected History element
		final String[] columns = {"Änderungsdatum","Name","Sektion","Kategorie"};
		LinkedList<Object[]> collect = new LinkedList<Object[]>();
		/** iterate the history elements and add text to the table */
		for (History elem : history)
		{
			/** check if the museum was deleted due to being separated during the backup */
			boolean museumDeleted = (Long)elem.getMuseum_id()==null || elem.getMuseum_id()<=0L;
			ArrayList<Object> row = new ArrayList<Object>(5);
			//index 0
			row.add(new SimpleDateFormat("dd.MM.yyyy, HH:mm").format(elem.getInsert()));
			//index 1
			row.add(elem.getName());
			//index 2
			if (museumDeleted)
				row.add("(nicht verfügbar)");
			else if (elem.getSection_id()==null || elem.getSection_id().equals(0L))
				row.add("");
			else
				try {
					row.add(Access.searchSectionID(elem.getSection_id()).getName());
				} catch (Exception e) {
					row.add("err: "+e.getMessage());
					JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			//index 3
			if (museumDeleted)
				row.add("(nicht verfügbar)");
			else if (elem.getCategory_id()==null || elem.getCategory_id().equals(0L))
				row.add("");
			else
				try {
					row.add(Access.searchCategoryID(elem.getCategory_id()).getName());
				} catch (Exception e) {
					row.add("err: "+e.getMessage());
					JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
				}
			//index 4 has an invisibility super power
			row.add(elem);
			collect.add(row.toArray());
		}
		final Object[][] data = collect.toArray(new Object[collect.size()][]);
		final JTable result = new JTable (data,columns);
		result.setEnabled(true);
		result.setModel( new TableModel() {
			public int getRowCount()
				{return data.length;}
			public int getColumnCount()
				{return columns.length;}
			public String getColumnName(int columnIndex)
				{return columns[columnIndex];}
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex==4)
					return History.class;
				return String.class;
			}
			public boolean isCellEditable(int rowIndex, int columnIndex)
				{return false;}
			public Object getValueAt(int rowIndex, int columnIndex)
				{return data[rowIndex][columnIndex];}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
			public void addTableModelListener(TableModelListener l) {}
			public void removeTableModelListener(TableModelListener l) {}
			
		});
		/** anonymous classes need this; this is NOT a singleton */
		final EditExhibit instance = this;
		result.setRowSelectionAllowed(true);
		result.addMouseListener(new MouseAdapter() {
			public void mouseReleased (MouseEvent e) {
				//double click on an item ("selected row")
				if (e.getClickCount()==2 && ((e.getModifiers()&InputEvent.BUTTON1_MASK)!=0))
					// open a dialog with the selected item
					new HistoryDetails( instance,
							            (History) data [result.getSelectedRow()][4] );
			}
		});
		
		result.setToolTipText("Klicken Sie doppelt auf ein Element, um die Details zu einem Zeitpunkt anzuzeigen.");
		return result;
	}

	/**
	 * save the current changes in the database (either updates or inserts the
	 * exhibit, also deletes and inserts images)
	 * 
	 * @return true if successful
	 * @throws NumberFormatException
	 *             if the "count" text field does not contain a valid number
	 * @throws Exception
	 *             from {@link Access}.insertExhibit(...)
	 */
	private boolean save() throws NumberFormatException, InvalidArgumentsException, Exception
	{
		Long museum_id=null;

		if (editingDisabled) // just in case...
			return false;
		
		if (category!=null)
			museum=category.getMuseum();
		if (section!=null)
			museum=section.getMuseum();

		if (museum != null)
			museum_id = museum.getId();
		else {
			JOptionPane.showMessageDialog(this,
							"Fehler beim Speichern: Es konnte kein zugehöriges Museum ausgemacht werden",
							"Kein Museum angegeben", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if (category == null) {
			JOptionPane.showMessageDialog(this,
							"Fehler beim Speichern: Es muss eine Kategorie ausgewählt werden!",
							"Kein Museum angegeben", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		String name = textFieldName.getText();
		String description = textAreaDescription.getText();
		int count = Integer.parseInt(textFieldCount.getText());
		// if the current objects are null, insert with null as id,
		// otherwise with their id's
		Long section_id = section == null ? null : section.getId();
		Long category_id = category == null ? null : category.getId();
		String rfid = textFieldRfid.getText();
		String euro = textFieldEuro.getText();
		String cent = textFieldCent.getText();
		Long currentId;
		if (currentItem == null) {
			// the object we are editing does not yet exist in the DB
			currentId = Access.insertExhibit(name, description, museum_id,
					section_id, category_id, count, rfid, euro,
					cent);
			currentItem = Access.searchExhibitID(currentId);
		} else {
			currentId = currentItem.getId();
			Access.changeExhibit(currentId, name, description, section_id,
					category_id, count, rfid, museum_id, euro,
					cent);
		}
		// update the images
		for (byte[] image : imagesToInsert)
			Access.insertPicture(currentId, image);
		for (Image image : imagesToDelete)
			Access.deletePicture(image.getId());
		// update the labels
		for (Label label : Access.getAllLabelsByExhibitId(currentId))
			Access.removeFromLabel(currentId, label);
		for (Label label : labels)
			Access.addToLabel(currentItem, label);

		LinkedList<Exhibit> item = new LinkedList<Exhibit>();
		item.add(Access.searchExhibitID(currentId));
		for (ExhibitListener listener : listeners)
			listener.event(item);
		// hack if files are needed
		/*
		 try {
		 	File temp = File.createTempFile("museum", "image");
		 	FileOutputStream fos = new FileOutputStream(temp);
		 	fos.write(image);
		 	fos.close();
		 	Access.insertPicture(currentId, temp);
		 	temp.delete();
		 }
		 catch (Exception e) {
		 	e.printStackTrace();
		 	JOptionPane.showMessageDialog ( this, "Fehler beim Speichern einer temporären Datei: "+e.getMessage(),
		 									"E/A-Fehler", JOptionPane.ERROR_MESSAGE );
		 	return false;
		 	}
		 */
		MainGUI.getDetailPanel().setDetails(Access.searchExhibitID(currentId));
		TablePanel.getInstance().refreshTable();
		if (section!=null)
			nextSection=section;
		if (category!=null)
			nextCategory=category;
		return true;
	}

	/** set the texts in the fields according to the current values */
	private void updateFields() {
		if (category==null)
			try {
				category=Access.getMiscellaneousCategory(
							museum!=null
							? museum.getId()
							: MainGUI.getMuseumMainPanel().getMuseumId());
			} catch (CategoryNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (MuseumNotFoundException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (section != null)
			textFieldSection.setText(section.getName());
		else if (museum != null)
			textFieldSection.setText(museum.getName());
		if (category != null)
			textFieldCategory.setText(category.getName());
		String labelsText = "";
		for (Label label : labels)
			if (labelsText.equals(""))
				labelsText = label.getName();
			else
				labelsText += ", " + label.getName();
		textFieldLabels.setText(labelsText);
	}

	/**
	 * show the image (again), when resizing or switching it
	 */
	private void refreshImage()
	{
		// if the image was created successfully...
		if (imageToDisplay == null)
			return;
		// the image is shown in a text pane
		panelImage.removeAll();
		JTextPane pane = new JTextPane();
		panelImage.add(pane);
		pane.setEditable(false);
		// variables for the given to imageToDisplay.getScaledInstance(...)
		// one of them will be set to its dimension, the other one will stay -1
		int resizeW = -1,
			resizeH = -1;
		// maximum dimensions for the scaled image are:
		int maxW = panelImage.getWidth(),
			maxH = panelImage.getHeight();
		// the given dimensions
		int imgW = imageToDisplay.getWidth(null),
			imgH = imageToDisplay.getHeight(null);
		// the height the image would have if scaled by the available width
		int scaledH = maxW * imgH / imgW;
		// most probably (but not necessarily!) this would be too tall:
		if (scaledH > maxH)
			resizeH = maxH;
		else
			resizeW = maxW;
		// if the panel is collapsed or something:
		if (resizeW<=0 && resizeH<=0)
			return;
		pane.insertIcon( new ImageIcon(
						imageToDisplay.getScaledInstance(
								resizeW, resizeH,
								java.awt.Image.SCALE_DEFAULT)
		));
		//this keeps fragments of overdrawn images from retaining in the panel
		panelImage.setVisible(false);
		panelImage.setVisible(true);
	}

	/**
	 * This displays either an {@link Image} from imagesExisting or a byte array
	 * from imagesToInsert, defined by imageSelected. This function should be
	 * called after the nextImage, previousImage and deleteImage functions. This
	 * also updates the left ("&lt;") and right ("&gt;") buttons, and displays or
	 * hides the label that indicates deletion, depending on the list
	 * "imagesToDelete".
	 * 
	 * @param imageToDisplay
	 */
	private void updateImage()
	{
		btnPreviousImage.setEnabled(hasPreviousImage());
		btnNextImage.setEnabled(hasNextImage());
		lblImageDelete.setVisible(false);
		lblImageInsert.setVisible(false);
		imageToDisplay = null; // if none of the below creates an image, this
								// will be null
		if (imageSelected instanceof byte[]) {
			imageToDisplay = Toolkit.getDefaultToolkit().createImage(
					(byte[]) imageSelected);
			lblImageInsert.setVisible(true);
		}
		if (imageSelected instanceof Image) {
			imageToDisplay = Toolkit.getDefaultToolkit().createImage(
					((Image) imageSelected).getRawImage());
			for (Image test : imagesToDelete)
				if (test.getId().equals(((Image)imageSelected).getId()))
					lblImageDelete.setVisible(true);
		}
		refreshImage();
	}

	/**
	 * Check if there is an image placed "before" the current one in either of the lists.
	 */
	private boolean hasPreviousImage()
	{
		// it is sufficient to test if any one image is "placed" somewhere
		// before the currently shown one
		// for example, on the first entry
		for (byte[] test : imagesToInsert) {
			if (imageSelected == test)
				return false; // the first entry in both lists
			else
				return true;
		}
		// if the above list is empty, only then the other list will be checked
		// (again, only the first entry):
		if (imageSelected instanceof Image)
		for (Image test : imagesExisting) {
			if (((Image)imageSelected).getId().equals(test.getId()))
				return false;
			else
				return true;
		}
		// both lists are empty:
		return false;
	}

	/**
	 * replaces the content of the imagePanel with the previous image in the lists, if existing
	 */
	private void previousImage()
	{
		if (!hasPreviousImage())
			return;
		// if the image that is "placed" after the test object is the current image
		// then the test object is the image "placed" before the current image
		Object prev = null;
		for (byte[] test : imagesToInsert) {
			if (test == imageSelected) {
				imageSelected = prev;
				break; // nothing to do here
			}
			prev = test;
		}
		if (imageSelected instanceof Image)
		for (Image test : imagesExisting) {
			if (test.getId().equals(((Image)imageSelected).getId())) {
				imageSelected = prev;
				break;
			}
			prev = test;
		}
		updateImage();
	}

	/**
	 * Check if there is an image placed "after" the current one in either of the lists.
	 */
	private boolean hasNextImage()
	{
		// Iterate both lists; if the current image is found and there is still
		// a next image, then return true, otherwise false.
		boolean found = false;
		Iterator<byte[]> test1 = imagesToInsert.iterator();
		while (test1.hasNext()) {
			byte[] test = test1.next();
			if (test == imageSelected)
				// hit, now if either in this or the other list
				// there is another next item, then return true
				found = true;
			if (found && test1.hasNext())
				return true;
		}
		Iterator<Image> test2 = imagesExisting.iterator();
		while (test2.hasNext()) {
			// note that "found" could still be set to true from iterating the first list
			if (found && test2.hasNext())
				return true;
			Image test = test2.next();
			if (imageSelected instanceof Image &&
				test.getId().equals(((Image)imageSelected).getId()))
				found = true;
		}
		// no next image to the currently selected one found:
		return false;
	}

	/**
	 * replaces the content of the imagePanel with the next image in the lists, if existing
	 */
	private void nextImage()
	{
		if (!hasNextImage())
			return;
		boolean found = false;
		Iterator<byte[]> test1 = imagesToInsert.iterator();
		while (test1.hasNext()) {
			byte[] test = test1.next();
			if (test == imageSelected)
				// hit, now if either in this or the other list there is another next item,
				// then the next one is to be selected (and there must be a next one)
				found = true;
			if (found && test1.hasNext()) {
				imageSelected = test1.next();
				updateImage();
				return;
			}
		}
		Iterator<Image> test2 = imagesExisting.iterator();
		while (test2.hasNext()) {
			if (found) {
				// it is asserted that there is a next entry
				imageSelected = test2.next();
				updateImage();
				return;
			}
			Image test = test2.next();
			if (imageSelected instanceof Image &&
				test.getId().equals(((Image)imageSelected).getId()))
				found = true;
		}
		// this should never be reached
	}

	/**
	 * Deletes the currently selected image from either to list of images to
	 * insert or the list of images that were loaded when the exhibit was
	 * loaded. If no image is selected (i.e., there is none) then nothing will
	 * happen. This function will confirm the deletion with a popup window, if
	 * the deleted image is from an existing exhibit.
	 */
	private void deleteImage()
	{
		if (imageSelected == null)
			return;
		setChanged();
		// don't iterate the byte[] list if the selection isn't in it
		if (imageSelected instanceof byte[])
		for (byte[] test : imagesToInsert)
			// the image that is selected for deletion is one that would be inserted
			if (test == imageSelected) {
				// select another image (if none available, reset it)
				if (hasNextImage()) {
					nextImage();
				} else if (hasPreviousImage()) {
					previousImage();
				} else {
					imageSelected = null;
				}
				imagesToInsert.remove(test);
				updateImage();
				return; // nothing to do here
			}
		if (imageSelected instanceof Image)
		for (Image test : imagesExisting)
			// the image that is selected for deletion is one that was loaded
			// from the DB
			if (test.getId().equals(((Image)imageSelected).getId())) {
				for (Image testd : imagesToDelete)
					// test if this image is already to be deleted
					if (testd.getId().equals(test.getId())) {
						int answer = JOptionPane.showConfirmDialog(this,
										"Das Bild doch beim Exponat lassen?", "Frage",
										JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
						if (answer==JOptionPane.YES_OPTION) {
							imagesToDelete.remove(testd);
							updateImage();
						}
						return;
					}
				int answer = JOptionPane.showConfirmDialog(this,
								"Das zum Exponat gehörige Bild löschen?", "Frage",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (answer == JOptionPane.YES_OPTION) {
					imagesToDelete.add(test);
					updateImage();
				}
				return;
			}
		// this should never be reached
	}

	/**
	 * shows a file chooser dialog and loads the selected file, adding it to imagesToInsert
	 */
	private void addImages()
	{
		LinkedList<byte[]> toAdd = new LinkedList<byte[]>();
		try {
			// it seems this bug prevents us from opening files from
			// "recently used files"
			// https://bugzilla.redhat.com/show_bug.cgi?id=881425
			FileDialog fd = new FileDialog(this, "Bilddatei(en) auswählen...",
									FileDialog.LOAD);
			fd.setMultipleMode(true);
			fd.setModal(true);
			fd.setVisible(true);
			for (File file : fd.getFiles()) {
				String ending = EditExhibit.parseFileEnding(file.getAbsolutePath());
				if (     ( ! ending.toUpperCase().equals("PNG")  )
					 &&  ( ! ending.toUpperCase().equals("JPG")  )
					 &&  ( ! ending.toUpperCase().equals("JPEG") )
					 &&  ( ! ending.toUpperCase().equals("GIF")  )
				   )
					throw new Exception ( "Datei \"" + file.getAbsolutePath() + "\" "
							            + "hat keine der unterstützten Dateiendungen (jpg,jpeg,png,gif)");
				else {
					byte[] item = Files.readAllBytes(file.toPath());
					toAdd.add(item);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Fehler beim Einlesen von Bildern:\n\t" + e.getMessage(),
					"E/A-Fehler", JOptionPane.ERROR_MESSAGE);
			toAdd = new LinkedList<byte[]>(); //reset so nothing will happen
		}
		for (byte[] item : toAdd) {
			imageSelected = item;
			imagesToInsert.add(item);
		}
		setChanged();
		updateImage();
	}
	
	public static String parseFileEnding (String path) {
		if (path==null)
			return "";
		int dotIndex = 0;
		for (int i=path.length()-1; i>=0; i--)
			if (path.charAt(i)=='.') {
				dotIndex = i;
				break;
			}
		//if the path ends with a dot
		if (dotIndex==path.length()-1)
			return "";
		return path.substring(dotIndex+1);
	}

	/**
	 * shows the label chooser dialog and configures the exhibit and the dialog accordingly
	 */
	private void editLabels()
	{
		SelectLabels sl = new SelectLabels(this,labels);
		sl.addListener(new LabelListener() {
			public void event(Collection<Label> labels) {
				setLabels(labels);
				setChanged();
			}
		});
		sl.addDisposeListener(new SimpleListener() {
			public void event() {
			}
		});
	}

	/** somewhat hacky: a variable that prevents new confirm dialogs from popping up */
	private boolean confirmDialogIsUp = false;

	/** override the default dispose operation so the user has to confirm closing the window
	 *  if changes were registered */
	@Override
	public void dispose() {
		if (!initialized || confirmDialogIsUp)
			return;
		confirmDialogIsUp = true;
		if (!changed) {
			close();
			return;
		}
		int answer = JOptionPane.showConfirmDialog(this,
				"Das Bearbeiten des Exponats \""
					+ ( currentItem!=null ? currentItem.getName() : textFieldName.getText() )
					+ "\" abbrechen?",
				"Sicherheitsabfrage", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (answer == JOptionPane.YES_OPTION)
			close(); // if the exhibit dialog is closed, leave the boolean stay true
		else
			confirmDialogIsUp = false;
	}
	
	/** override the default setVisible operation so it will have control over being opened */
	@Override
	public void setVisible (boolean b) {
		if (!initialized)
			return;
		else
			super.setVisible(b);
	}

	/** close the window without confirmation */
	private void close() {
		if (!initialized)
			return;
		confirmDialogIsUp = true;
		// if in editing mode (not creating a new exhibit)
		if (currentItem != null)
			openedItems.remove(this);
		initialized=false;
		super.dispose();
	}

	/**
	 * this greys out the fields so the user won't edit them by accident
	 * <p>deprecated: use enableEditing(false) instead
	 * @deprecated
	 */
	public void disableEditing() {
		enableEditing(false);
	}
	
	/** If there is a selection in the table panel, the user has to confirm that
	 *  (s)he wants to edit the exhibit because the selection will be reset. If
	 *  editing was confirmed once, there is no point in asking again. */
	private boolean editingConfirmed = false;
	
	/** this enables or disables the buttons and text inputs
	 *  @param enabled if true, the properties of the exhibit can be edited,
	 *  	else editing will be disabled */
	public void enableEditing(boolean enabled)
	{
		if (!initialized)
			return;
		//prohibit the editing of deleted exhibits
		if (enabled && currentItem!=null && currentItem.isDeleted()) {
			JOptionPane.showMessageDialog(this, "Das Exponat kann nicht bearbeitet werden: Es wurde bereits gelöscht!",
			                              "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (enabled && !editingConfirmed && TablePanel.getInstance().isChecked())
		{
			int answer = JOptionPane.showConfirmDialog(this,
							"Nach dem Bearbeiten wird die Auswahl in der Liste zurückgesetzt. Fortfahren?",
							"Frage", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (answer != JOptionPane.YES_OPTION)
				return;
			else
				editingConfirmed=true;
		}
		textFieldName.setEditable(enabled);
		btnEdit.setEnabled(enabled);
		btnSection.setEnabled(enabled);
		btnCategory.setEnabled(enabled);
		textFieldCount.setEditable(enabled);
		btnLabels.setEnabled(enabled);
		textAreaDescription.setEditable(enabled);
		btnImageDelete.setEnabled(enabled);
		btnImageAdd.setEnabled(enabled);
		btnSave.setEnabled(enabled);
		textFieldEuro.setEditable(enabled);
		textFieldCent.setEditable(enabled);
		//as demanded, grey out the "save&new" button if editing an existing item
		btnSaveAndNew.setEnabled(enabled && currentItem==null);
		btnLt.setEnabled(enabled);
		btnGt.setEnabled(enabled);
		btnAmp.setEnabled(enabled);
		btnCopy.setEnabled(enabled);
		editingDisabled=!enabled;
		if (editingDisabled)
			btnEnable.setText("Bearbeiten aktivieren");
		else
			btnEnable.setText("Bearbeiten deaktivieren");
	}
	/** if editing is disable, enable it; if it's enabled, disable it */
	private void toggleEditing() {
		enableEditing(editingDisabled);
	}
	
	/**
	 * common operations for both constructors (builds up the window and creates listeners)
	 */
	private void init()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
		getContentPane().setLayout(gridBagLayout);

		JPanel panelFields = new JPanel();
		GridBagConstraints gbc_panelFields = new GridBagConstraints();
		gbc_panelFields.insets = new Insets(0, 16, 8, 16);
		gbc_panelFields.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelFields.gridx = 0;
		gbc_panelFields.gridy = 0;
		getContentPane().add(panelFields, gbc_panelFields);
		GridBagLayout gbl_panelFields = new GridBagLayout();
		gbl_panelFields.columnWeights = new double[] { 0.0, 1.0 };
		gbl_panelFields.rowWeights = new double[] { 0.0, 1.0, 1.0, 1.0, 0.0,
				1.0, 0.0 };
		panelFields.setLayout(gbl_panelFields);

		textFieldName = new JTextField();
		GridBagConstraints gbc_textFieldName = new GridBagConstraints();
		gbc_textFieldName.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldName.gridx = 1;
		gbc_textFieldName.gridy = 0;
		panelFields.add(textFieldName, gbc_textFieldName);
		textFieldName.setColumns(10);

		JLabel lblName = new JLabel("Name des Exponats");
		GridBagConstraints gbc_lblName = new GridBagConstraints();
		gbc_lblName.anchor = GridBagConstraints.WEST;
		gbc_lblName.gridx = 0;
		gbc_lblName.gridy = 0;
		panelFields.add(lblName, gbc_lblName);

		JLabel lblRfid = new JLabel("RFID-Tag(*)");
		GridBagConstraints gbc_lblRfid = new GridBagConstraints();
		gbc_lblRfid.anchor = GridBagConstraints.WEST;
		gbc_lblRfid.gridx = 0;
		gbc_lblRfid.gridy = 1;
		panelFields.add(lblRfid, gbc_lblRfid);

		JPanel panelRfid = new JPanel();
		GridBagConstraints gbc_panelRfid = new GridBagConstraints();
		gbc_panelRfid.fill = GridBagConstraints.BOTH;
		gbc_panelRfid.gridx = 1;
		gbc_panelRfid.gridy = 1;
		panelFields.add(panelRfid, gbc_panelRfid);
		panelRfid.setLayout(new BoxLayout(panelRfid, BoxLayout.X_AXIS));

		textFieldRfid = new JTextField();
		textFieldRfid.setEditable(false);
		panelRfid.add(textFieldRfid);
		textFieldRfid.setColumns(10);

		btnEdit = new JButton("editieren");
		panelRfid.add(btnEdit);

		JLabel lblSection = new JLabel("Sektion");
		GridBagConstraints gbc_lblSection = new GridBagConstraints();
		gbc_lblSection.anchor = GridBagConstraints.WEST;
		gbc_lblSection.gridx = 0;
		gbc_lblSection.gridy = 2;
		panelFields.add(lblSection, gbc_lblSection);

		JPanel panelSection = new JPanel();
		GridBagConstraints gbc_panelSection = new GridBagConstraints();
		gbc_panelSection.fill = GridBagConstraints.BOTH;
		gbc_panelSection.gridx = 1;
		gbc_panelSection.gridy = 2;
		panelFields.add(panelSection, gbc_panelSection);
		panelSection.setLayout(new BoxLayout(panelSection, BoxLayout.X_AXIS));

		textFieldSection = new JTextField();
		textFieldSection.setEditable(false);
		panelSection.add(textFieldSection);
		textFieldSection.setColumns(10);

		btnSection = new JButton("wählen");
		panelSection.add(btnSection);

		JLabel lblCategory = new JLabel("Kategorie");
		GridBagConstraints gbc_lblCategory = new GridBagConstraints();
		gbc_lblCategory.anchor = GridBagConstraints.WEST;
		gbc_lblCategory.gridx = 0;
		gbc_lblCategory.gridy = 3;
		panelFields.add(lblCategory, gbc_lblCategory);

		JPanel panelCategory = new JPanel();
		GridBagConstraints gbc_panelCategory = new GridBagConstraints();
		gbc_panelCategory.fill = GridBagConstraints.BOTH;
		gbc_panelCategory.gridx = 1;
		gbc_panelCategory.gridy = 3;
		panelFields.add(panelCategory, gbc_panelCategory);
		panelCategory.setLayout(new BoxLayout(panelCategory, BoxLayout.X_AXIS));

		textFieldCategory = new JTextField();
		textFieldCategory.setEditable(false);
		panelCategory.add(textFieldCategory);
		textFieldCategory.setColumns(10);

		btnCategory = new JButton("wählen");
		panelCategory.add(btnCategory);

		JLabel lblAnzahl = new JLabel("Anzahl");
		GridBagConstraints gbc_lblAnzahl = new GridBagConstraints();
		gbc_lblAnzahl.anchor = GridBagConstraints.WEST;
		gbc_lblAnzahl.gridx = 0;
		gbc_lblAnzahl.gridy = 4;
		panelFields.add(lblAnzahl, gbc_lblAnzahl);

		textFieldCount = new JTextField("1");
		textFieldCount.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_textFieldCount = new GridBagConstraints();
		gbc_textFieldCount.anchor = GridBagConstraints.WEST;
		gbc_textFieldCount.gridx = 1;
		gbc_textFieldCount.gridy = 4;
		panelFields.add(textFieldCount, gbc_textFieldCount);
		textFieldCount.setColumns(10);
		textFieldCount.setText("1");

		JPanel panelEuroCent = new JPanel();
		GridBagConstraints gbc_panelEuroCent = new GridBagConstraints();
		gbc_panelEuroCent.anchor = GridBagConstraints.WEST;
		gbc_panelEuroCent.fill = GridBagConstraints.VERTICAL;
		gbc_panelEuroCent.gridx = 1;
		gbc_panelEuroCent.gridy = 5;
		panelFields.add(panelEuroCent, gbc_panelEuroCent);
		panelEuroCent.setLayout(new BoxLayout(panelEuroCent, BoxLayout.X_AXIS));

		textFieldEuro = new JTextField();
		panelEuroCent.add(textFieldEuro);
		textFieldEuro.setColumns(10);
		textFieldEuro.setText("0");

		JLabel lblComma = new JLabel(",");
		panelEuroCent.add(lblComma);

		textFieldCent = new JTextField();
		panelEuroCent.add(textFieldCent);
		textFieldCent.setColumns(2);
		textFieldCent.setText("00");

		JLabel lblEuro = new JLabel("€");
		panelEuroCent.add(lblEuro);

		JLabel lblLabels = new JLabel("Labels(*)");
		GridBagConstraints gbc_lblLabels = new GridBagConstraints();
		gbc_lblLabels.anchor = GridBagConstraints.WEST;
		gbc_lblLabels.gridx = 0;
		gbc_lblLabels.gridy = 6;
		panelFields.add(lblLabels, gbc_lblLabels);

		JPanel panelLabels = new JPanel();
		GridBagConstraints gbc_panelLabels = new GridBagConstraints();
		gbc_panelLabels.fill = GridBagConstraints.BOTH;
		gbc_panelLabels.gridx = 1;
		gbc_panelLabels.gridy = 6;
		panelFields.add(panelLabels, gbc_panelLabels);
		panelLabels.setLayout(new BoxLayout(panelLabels, BoxLayout.X_AXIS));

		textFieldLabels = new JTextField();
		textFieldLabels.setEditable(false);
		panelLabels.add(textFieldLabels);
		textFieldLabels.setColumns(10);

		btnLabels = new JButton("wählen");
		panelLabels.add(btnLabels);

		JLabel lblValue = new JLabel("Wert");
		GridBagConstraints gbc_lblValue = new GridBagConstraints();
		gbc_lblValue.anchor = GridBagConstraints.WEST;
		gbc_lblValue.gridx = 0;
		gbc_lblValue.gridy = 5;
		panelFields.add(lblValue, gbc_lblValue);
		
		//add another field if the current item already exists and is exhibited or lent
		if (currentItem != null)
                {
                    Outsourced searchingOutsourced = null;
                    for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
                    {
                        if (outsourced.getExhibitIds().containsKey(currentItem.getId()))
                        {
                            searchingOutsourced = outsourced;
                        }
                    }
                    if (searchingOutsourced != null)
                    {
                        final Outsourced foundOutsourced = searchingOutsourced;
                        GridBagConstraints gbc_lblOutsourced = new GridBagConstraints();
			gbc_lblOutsourced.anchor = GridBagConstraints.WEST;
			gbc_lblOutsourced.gridx = 0;
			gbc_lblOutsourced.gridy = 7;
			if ( foundOutsourced.getAddress_id()==null ||
				 foundOutsourced.getAddress_id()==0 )
				panelFields.add(new JLabel("ausgestellt:"), gbc_lblOutsourced);
			else
				panelFields.add(new JLabel("verliehen:"), gbc_lblOutsourced);

			JButton btnOutsourced = new JButton (foundOutsourced.getName());
			GridBagConstraints gbc_btnOutsourced = new GridBagConstraints();
			gbc_btnOutsourced.anchor = GridBagConstraints.WEST;
			gbc_btnOutsourced.gridx = 1;
			gbc_btnOutsourced.gridy = 7;
			panelFields.add(btnOutsourced, gbc_btnOutsourced);
			btnOutsourced.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0)
				{
					if ( foundOutsourced.getAddress_id()==null ||
						 foundOutsourced.getAddress_id()==0L )
					{
						// no address => exhibition
						EditExhibition ee = new EditExhibition(foundOutsourced.getId());
						ee.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						ee.setVisible(true);
					}
					else
					{
						// there is an address => loan
						EditLoan el = new EditLoan(foundOutsourced.getId());
						el.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						el.setVisible(true);
					}
					
				}
			});
                    }
                }
		final JPanel panelBody = new JPanel();
		GridBagLayout gbl_panelBody = new GridBagLayout();
		gbl_panelBody.columnWeights = new double[]{1.0};
		gbl_panelBody.rowWeights = new double[]{1.0};
		panelBody.setLayout(gbl_panelBody);
		
		GridBagConstraints gbc_panelBody = new GridBagConstraints();
		gbc_panelBody.fill = GridBagConstraints.BOTH;
		gbc_panelBody.gridx = 0;
		gbc_panelBody.gridy = 2;
		getContentPane().add(panelBody, gbc_panelBody);

		JSeparator separatorAction = new JSeparator();
		GridBagConstraints gbc_separatorAction = new GridBagConstraints();
		gbc_separatorAction.insets = new Insets(0, 6, 6, 0);
		gbc_separatorAction.gridx = 0;
		gbc_separatorAction.gridy = 3;
		getContentPane().add(separatorAction, gbc_separatorAction);
		
		final JPanel panelHistory = new JPanel();
		GridBagLayout gbl_panelHistory = new GridBagLayout();
		gbl_panelHistory.columnWidths = new int[]{0, 0};
		gbl_panelHistory.rowHeights = new int[]{0, 0, 0};
		gbl_panelHistory.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelHistory.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelHistory.setLayout(gbl_panelHistory);

		JLabel lblHistory = new JLabel("Historie");
		GridBagConstraints gbc_lblHistory = new GridBagConstraints();
		gbc_lblHistory.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblHistory.gridx = 0;
		gbc_lblHistory.gridy = 0;
		panelHistory.add(lblHistory, gbc_lblHistory);
		
		scrollPaneHistory = new JScrollPane();
		GridBagConstraints gbc_scrollPaneHistory = new GridBagConstraints();
		gbc_scrollPaneHistory.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneHistory.gridx = 0;
		gbc_scrollPaneHistory.gridy = 1;
		panelHistory.add(scrollPaneHistory, gbc_scrollPaneHistory);
		scrollPaneHistory.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		final JPanel panelImages = new JPanel();
		GridBagLayout gbl_panelImages = new GridBagLayout();
		gbl_panelImages.columnWidths = new int[]{0, 0};
		gbl_panelImages.rowHeights = new int[]{0, 0, 0, 0};
		gbl_panelImages.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelImages.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		panelImages.setLayout(gbl_panelImages);

		JPanel panelImagesLabelButtons = new JPanel();
		GridBagConstraints gbc_panelImagesLabelButtons = new GridBagConstraints();
		gbc_panelImagesLabelButtons.anchor = GridBagConstraints.SOUTH;
		gbc_panelImagesLabelButtons.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelImagesLabelButtons.gridx = 0;
		gbc_panelImagesLabelButtons.gridy = 0;
		panelImages.add(panelImagesLabelButtons, gbc_panelImagesLabelButtons);
		GridBagLayout gbl_panelImagesLabelButtons = new GridBagLayout();
		gbl_panelImagesLabelButtons.columnWidths = new int[] { 41, 0, 117, 0 };
		gbl_panelImagesLabelButtons.rowHeights = new int[] { 25, 0 };
		gbl_panelImagesLabelButtons.columnWeights = new double[] { 1.0, 0.0,
				0.0, Double.MIN_VALUE };
		gbl_panelImagesLabelButtons.rowWeights = new double[] { 0.0,
				Double.MIN_VALUE };
		panelImagesLabelButtons.setLayout(gbl_panelImagesLabelButtons);

		JLabel lblImages = new JLabel("Bilder");
		GridBagConstraints gbc_lblImages = new GridBagConstraints();
		gbc_lblImages.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblImages.anchor = GridBagConstraints.WEST;
		gbc_lblImages.gridx = 0;
		gbc_lblImages.gridy = 0;
		panelImagesLabelButtons.add(lblImages, gbc_lblImages);

		btnImageDelete = new JButton("Bild löschen");
		GridBagConstraints gbc_btnDeleteImage = new GridBagConstraints();
		gbc_btnDeleteImage.gridx = 1;
		gbc_btnDeleteImage.gridy = 0;
		panelImagesLabelButtons.add(btnImageDelete, gbc_btnDeleteImage);

		btnImageAdd = new JButton("Bild hinzufügen...");
		GridBagConstraints gbc_btnImageAdd = new GridBagConstraints();
		gbc_btnImageAdd.anchor = GridBagConstraints.EAST;
		gbc_btnImageAdd.gridx = 2;
		gbc_btnImageAdd.gridy = 0;
		panelImagesLabelButtons.add(btnImageAdd, gbc_btnImageAdd);

		JPanel panelImageStatusLabel = new JPanel();
		GridBagConstraints gbc_panelImageStatusLabel = new GridBagConstraints();
		gbc_panelImageStatusLabel.fill = GridBagConstraints.BOTH;
		gbc_panelImageStatusLabel.gridx = 0;
		gbc_panelImageStatusLabel.gridy = 1;
		panelImages.add(panelImageStatusLabel, gbc_panelImageStatusLabel);
		panelImageStatusLabel.setLayout(new BoxLayout(panelImageStatusLabel,
				BoxLayout.X_AXIS));

		lblImageDelete = new JLabel("Bild wird beim Speichern gelöscht!!");
		panelImageStatusLabel.add(lblImageDelete);
		lblImageDelete.setForeground(Color.RED);

		lblImageInsert = new JLabel("Bild wird beim Speichern hinzugefügt.");
		lblImageInsert.setForeground(Color.GREEN);
		panelImageStatusLabel.add(lblImageInsert);

		JPanel panelImagesBody = new JPanel();
		GridBagConstraints gbc_panelImagesBody = new GridBagConstraints();
		gbc_panelImagesBody.fill = GridBagConstraints.BOTH;
		gbc_panelImagesBody.gridx = 0;
		gbc_panelImagesBody.gridy = 2;
		panelImages.add(panelImagesBody, gbc_panelImagesBody);
		GridBagLayout gbl_panelImagesBody = new GridBagLayout();
		gbl_panelImagesBody.columnWeights = new double[] { 0.0, 1.0, 0.0 };
		gbl_panelImagesBody.rowWeights = new double[] { 1.0 };
		panelImagesBody.setLayout(gbl_panelImagesBody);

		btnPreviousImage = new JButton("  <-  ");
		btnPreviousImage.setEnabled(false);
		GridBagConstraints gbc_btnPreviousImage = new GridBagConstraints();
		gbc_btnPreviousImage.fill = GridBagConstraints.BOTH;
		gbc_btnPreviousImage.gridx = 0;
		gbc_btnPreviousImage.gridy = 0;
		panelImagesBody.add(btnPreviousImage, gbc_btnPreviousImage);

		panelImage = new JPanel();
		GridBagConstraints gbc_panelImage = new GridBagConstraints();
		gbc_panelImage.fill = GridBagConstraints.BOTH;
		gbc_panelImage.gridx = 1;
		gbc_panelImage.gridy = 0;
		panelImagesBody.add(panelImage, gbc_panelImage);
		GridBagLayout gbl_panelImage = new GridBagLayout();
		gbl_panelImage.columnWidths = new int[] { 0 };
		gbl_panelImage.rowHeights = new int[] { 0 };
		gbl_panelImage.columnWeights = new double[] {};
		gbl_panelImage.rowWeights = new double[] {};
		panelImage.setLayout(gbl_panelImage);
		
		btnNextImage = new JButton("  ->  ");
		btnNextImage.setEnabled(false);
		GridBagConstraints gbc_btnNextImage = new GridBagConstraints();
		gbc_btnNextImage.fill = GridBagConstraints.BOTH;
		gbc_btnNextImage.gridx = 2;
		gbc_btnNextImage.gridy = 0;
		panelImagesBody.add(btnNextImage, gbc_btnNextImage);
		
		JPanel panelRadioButtons = new JPanel();
		GridBagConstraints gbc_panelRadioButtons = new GridBagConstraints();
		gbc_panelRadioButtons.insets = new Insets(0, 0, 5, 0);
		gbc_panelRadioButtons.fill = GridBagConstraints.BOTH;
		gbc_panelRadioButtons.gridx = 0;
		gbc_panelRadioButtons.gridy = 1;
		getContentPane().add(panelRadioButtons, gbc_panelRadioButtons);
		
		btnEnable = new JButton("Bearbeiten deaktivieren");
		panelRadioButtons.add(btnEnable);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		panelRadioButtons.add(separator);
		
		JSeparator separator_1 = new JSeparator();
		panelRadioButtons.add(separator_1);
		
		JSeparator separator_2 = new JSeparator();
		panelRadioButtons.add(separator_2);
		
		JToggleButton tglbtnDescription = new JToggleButton("Beschreibung");
		tglbtnDescription.setSelected(true);
		buttonGroup.add(tglbtnDescription);
		panelRadioButtons.add(tglbtnDescription);
		
		JToggleButton tglbtnImages = new JToggleButton("Bilder");
		buttonGroup.add(tglbtnImages);
		panelRadioButtons.add(tglbtnImages);

		JPanel panelAction = new JPanel();
		GridBagConstraints gbc_panelAction = new GridBagConstraints();
		gbc_panelAction.gridx = 0;
		gbc_panelAction.gridy = 4;
		getContentPane().add(panelAction, gbc_panelAction);
		panelAction.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblOptional = new JLabel("(*) Optional");
		panelAction.add(lblOptional);
		
		JButton btnCancel = new JButton("Abbrechen");
		panelAction.add(btnCancel);

		btnSave = new JButton("Exponat speichern");
		panelAction.add(btnSave);

		btnSaveAndNew = new JButton("speichern & neu");
		panelAction.add(btnSaveAndNew);

		final JPanel panelDescription = new JPanel();
		GridBagLayout gbl_panelDescription = new GridBagLayout();
		gbl_panelDescription.columnWidths = new int[]{0, 0};
		gbl_panelDescription.rowHeights = new int[]{0, 0, 0};
		gbl_panelDescription.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panelDescription.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		panelDescription.setLayout(gbl_panelDescription);

		JPanel panelDescriptionHead = new JPanel();
		GridBagConstraints gbc_panelDescriptionHead = new GridBagConstraints();
		gbc_panelDescriptionHead.fill = GridBagConstraints.BOTH;
		gbc_panelDescriptionHead.gridx = 0;
		gbc_panelDescriptionHead.gridy = 0;
		panelDescription.add(panelDescriptionHead, gbc_panelDescriptionHead);
		GridBagLayout gbl_panelDescriptionHead = new GridBagLayout();
		gbl_panelDescriptionHead.columnWeights = new double[]{1.0, 0.0};
		panelDescriptionHead.setLayout(gbl_panelDescriptionHead);

		JLabel lblDescription = new JLabel("Beschreibung(*)");
		GridBagConstraints gbc_lblDescription = new GridBagConstraints();
		gbc_lblDescription.anchor = GridBagConstraints.WEST;
		gbc_lblDescription.gridx = 0;
		gbc_lblDescription.gridy = 0;
		panelDescriptionHead.add(lblDescription, gbc_lblDescription);

		JLabel lblEinfgen = new JLabel("Einfügen:");
		GridBagConstraints gbc_lblEinfgen = new GridBagConstraints();
		gbc_lblEinfgen.gridx = 1;
		gbc_lblEinfgen.gridy = 0;
		panelDescriptionHead.add(lblEinfgen, gbc_lblEinfgen);

		btnLt = new JButton("<");
		btnLt.setToolTipText("Füge den Code für das Zeichen \"<\" in die Beschreibung ein," +
				" sodass dieses in der Seitendarstellung angezeigt wird.");
		GridBagConstraints gbc_btnLt = new GridBagConstraints();
		gbc_btnLt.gridx=2;
		gbc_btnLt.gridy=0;
		panelDescriptionHead.add(btnLt, gbc_btnLt);

		btnGt = new JButton(">");
		btnGt.setToolTipText("Füge den Code für das Zeichen \">\" in die Beschreibung ein," +
				" sodass dieses in der Seitendarstellung angezeigt wird.");
		GridBagConstraints gbc_btnGt = new GridBagConstraints();
		gbc_btnGt.gridx=3;
		gbc_btnGt.gridy=0;
		panelDescriptionHead.add(btnGt, gbc_btnGt);

		btnAmp = new JButton("&");
		btnAmp.setToolTipText("Füge den Code für das Zeichen \"&\" in die Beschreibung ein," +
				" sodass dieses in der Seitendarstellung angezeigt wird.");
		GridBagConstraints gbc_btnAmp = new GridBagConstraints();
		gbc_btnAmp.gridx=4;
		gbc_btnAmp.gridy=0;
		panelDescriptionHead.add(btnAmp, gbc_btnAmp);

		btnCopy = new JButton("Kopie");
		btnCopy.setToolTipText("Füge kopierten Text ein; kann verwendet werden, um" +
				" einen kopierten Verweis in der Seitendarstellung anzuzeigen.");
		GridBagConstraints gbc_btnCopy = new GridBagConstraints();
		gbc_btnCopy.gridx=5;
		gbc_btnCopy.gridy=0;
		panelDescriptionHead.add(btnCopy, gbc_btnCopy);

		JScrollPane scrollPaneDescription = new JScrollPane();
		GridBagConstraints gbc_scrollPaneDescription = new GridBagConstraints();
		gbc_scrollPaneDescription.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneDescription.gridx = 0;
		gbc_scrollPaneDescription.gridy = 1;
		panelDescription.add(scrollPaneDescription, gbc_scrollPaneDescription);

		textAreaDescription = new JTextArea();
		scrollPaneDescription.setViewportView(textAreaDescription);
		
		/** the listeners need this; this is NOT a singleton */
		final EditExhibit instance = this;
		
		// set the "changed" variable whenever anything is being edited
		textFieldName.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				if (textFieldName.isEditable())
					setChanged();
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		textFieldCount.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				if (textFieldCount.isEditable())
					setChanged();
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		textFieldEuro.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				
				setChanged();
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		textFieldCent.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				setChanged();
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		textAreaDescription.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent arg0) {
				setChanged();
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyPressed(KeyEvent arg0) {}
		});
		
		// stuff that messes in the "body" panel
		
		panelImages.setVisible(false);
		panelHistory.setVisible(false);
		
		GridBagConstraints gbc_panelDescription = new GridBagConstraints();
		gbc_panelDescription.fill = GridBagConstraints.BOTH;
		gbc_panelDescription.gridx = 0;
		gbc_panelDescription.gridy = 0;
		panelBody.add(panelDescription, gbc_panelDescription);
		
		
		tglbtnDescription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = 0; gbc.gridy = 0;
				panelBody.removeAll();
				panelBody.add(panelDescription, gbc);
				panelDescription.setVisible(true);
				panelImages.setVisible(false);
				panelHistory.setVisible(false);
				panelBody.invalidate();
			}
		});
		
		tglbtnImages.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.gridx = 0; gbc.gridy = 0;
				panelBody.removeAll();
				panelBody.add(panelImages, gbc);
				panelDescription.setVisible(false);
				panelImages.setVisible(true);
				panelHistory.setVisible(false);
				panelBody.invalidate();
			}
		});
		
		if (currentItem!=null) {
			JToggleButton tglbtnHistory = new JToggleButton("Historie");
			buttonGroup.add(tglbtnHistory);
			panelRadioButtons.add(tglbtnHistory);
			tglbtnHistory.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					GridBagConstraints gbc = new GridBagConstraints();
					gbc.fill = GridBagConstraints.BOTH;
					gbc.gridx = 0; gbc.gridy = 0;
					panelBody.removeAll();
					panelBody.add(panelHistory, gbc);
					panelDescription.setVisible(false);
					panelImages.setVisible(false);
					panelHistory.setVisible(true);
					panelBody.invalidate();
				}
			});
		}
		
		
		
		btnLt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaDescription.insert("&lt;", textAreaDescription.getCaretPosition());
				setChanged();
			}
		});
		btnGt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaDescription.insert("&gt;", textAreaDescription.getCaretPosition());
				setChanged();
			}
		});
		btnAmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textAreaDescription.insert("&amp;", textAreaDescription.getCaretPosition());
				setChanged();
			}
		});
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
				if ((contents != null) &&
						contents.isDataFlavorSupported(DataFlavor.stringFlavor))
					try {
						textAreaDescription.insert(
								(String) contents.getTransferData(DataFlavor.stringFlavor) ,
								textAreaDescription.getCaretPosition());
						setChanged();
					} catch (UnsupportedFlavorException | IOException e1) {
						e1.printStackTrace();
					}
			}
		});
		
		btnSection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SectionChoose sc = new SectionChoose(instance);
				sc.addListener(new SectionListener() {
					public void event(Section s) {
						section = s;
						updateFields();
						setChanged();
					}
					public void event(Museum m) {
						museum = m;
						//if a museum is chosen in the section dialog,
						//reset the section
						section = null;
						updateFields();
						setChanged();
					}
				});
				sc.setModal(true);
				sc.setVisible(true);
			}
		});
		btnCategory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CategoryChoose cc = new CategoryChoose(instance);
				cc.addListener(new CategoryListener() {
					public void event(Category c) {
						category = c;
						updateFields();
						setChanged();
					}
				});
				cc.setModal(true);
				cc.setVisible(true);
			}
		});
		btnLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editLabels();
			}
		});
		btnPreviousImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				previousImage();
			}
		});
		btnNextImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextImage();
			}
		});
		btnImageDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteImage();
			}
		});
		btnImageAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addImages();
			}
		});
		btnEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleEditing();
			}
		});
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (save())
						close();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog (null,
							"Keine gültige Zahl: \"" + textFieldCount.getText() + "\"",
							"Fehler bei der Eingabe", JOptionPane.WARNING_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog (null,
							"Fehler beim " + (currentItem==null ? "Anlegen" : "Ändern")
						  + " des Exponats: " + ex.getMessage(), "Fehler",
							JOptionPane.ERROR_MESSAGE);
					if (! (ex instanceof InvalidArgumentsException) )
						ex.printStackTrace();
				}
			}
		});
		btnSaveAndNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (save()) {
						close();
						EditExhibit ee = new EditExhibit();
						ee.setCategory(category);
						ee.setSection(section);
						ee.setDescription(textAreaDescription.getText());
						ee.setEuro(textFieldEuro.getText());
						ee.setCent(textFieldCent.getText());
						ee.setName(textFieldName.getText());
						ee.setChanged();
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog (null,
							"Keine gültige Zahl: \"" + textFieldCount.getText() + "\"",
							"Fehler bei der Eingabe", JOptionPane.WARNING_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog (null,
							"Fehler beim Anlegen des Exponats:" + ex.getMessage(),
							"Fehler", JOptionPane.ERROR_MESSAGE);
					if (! (ex instanceof InvalidArgumentsException) )
						ex.printStackTrace();
				}
			}
		});
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MainGUI.getDetailPanel().refresh();
				dispose();
			}
		});
		panelImage.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				refreshImage();
			}
			public void componentShown(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
		});
		
		initialized=true;

		setTitle("Detailansicht - Exponat");
		setMinimumSize(new Dimension(500, 450));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		toFront();
		setSize(600, 580);
		setVisible(true);
		updateFields();
		updateImage();
	}
	
	/** if an existing item is opened, get it with this function */
	public Exhibit getExhibit() {
		return currentItem;
	}

	/** use this to set the labels of a freshly generated dialog */
	public void setLabels(Collection<Label> labels) {
		if (!initialized)
			return;
		this.labels = labels;
		updateFields();
	}

	/** use this to set the museum of a freshly generated dialog */
	public void setMuseum(Museum museum) {
		if (!initialized)
			return;
		this.museum = museum;
		updateFields();
	}

	/** use this to set the section of a freshly generated dialog */
	public void setSection(Section section) {
		if (!initialized)
			return;
		this.section = section;
		updateFields();
	}

	/** use this to set the category of a freshly generated dialog */
	public void setCategory(Category category) {
		if (!initialized)
			return;
		this.category = category;
		updateFields();
	}

	/** use this to set the description of a freshly generated dialog */
	public void setDescription(String description) {
		if (!initialized)
			return;
		this.textAreaDescription.setText(description);
		updateFields();
	}

	/** use this to set the euro value of a freshly generated dialog */
	public void setEuro(String euro) {
		if (!initialized)
			return;
		this.textFieldEuro.setText(euro);
		updateFields();
	}
	/** use this to set the cent value of a freshly generated dialog */
	public void setCent(String cent) {
		if (!initialized)
			return;
		this.textFieldCent.setText(cent);
		updateFields();
	}

	/** use this to set the name of a freshly generated dialog */
	public void setName(String value) {
		if (!initialized)
			return;
		this.textFieldName.setText(value);
		updateFields();
	}

	/**
	 * sets the museum for subsequent dialogs, if null is given to the
	 * constructor or the parameter does not exist in the constructor
	 * @deprecated
	 */
//	public static void setNextMuseum(Museum nextMuseum) {
//	}

	/**
	 * sets the section for subsequent dialogs, if null is given to the
	 * constructor or the parameter does not exist in the constructor
	 */
	public static void setNextSection(Section nextSection) {
		EditExhibit.nextSection = nextSection;
	}

	/**
	 * sets the category for subsequent dialogs, if null is given to the
	 * constructor or the parameter does not exist in the constructor
	 */
	public static void setNextCategory(Category nextCategory) {
		EditExhibit.nextCategory = nextCategory;
	}
	
	/**
	 * sets the internal "changed" variable to true
	 * so a confirmation dialog will be shown before closing
	 */
	public void setChanged() {
		if (!editingDisabled)
			changed=true;
	}
}
