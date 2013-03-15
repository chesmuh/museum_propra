package de.museum.berleburg.userInterface.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.dialogs.DetailSearch;

/**
 * SearchPanel
 * 
 * @author Timo Funke
 */

public class SearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField txtSearch;
	private JButton btnOpenDetailSearch;
	private JComboBox<String> comboBoxChooseType;
	private String searchText;
	private long museumId;
	private ArrayList<Long> emptyList = new ArrayList<Long>();

	/**
	 * Initialize the panel
	 */
	public SearchPanel() {

		setLayout(new MigLayout("", "[][grow,fill][][grow][][]", "[18px]"));

		setMinimumSize(new Dimension(400, 40));
		setLayout(new MigLayout("", "[grow][][][][]", "[fill]"));

		txtSearch = new JTextField();
		txtSearch.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					startSearch();
				}
			}
		});

		add(txtSearch, "cell 2 0");
		txtSearch.setColumns(20);

		comboBoxChooseType = new JComboBox<String>();
		comboBoxChooseType.setModel(new DefaultComboBoxModel<String>(
				new String[] { "Exponatsname", "Sektionsname",
						"Sektionsname mit Untersektionen", "Kategoriename",
						"Labelname", "Ausstellung/Leihgabe" }));
		add(comboBoxChooseType, "cell 3 0");

		JButton btnStartSearch = new JButton(new ImageIcon(getClass()
				.getResource("Search.png")));
		btnStartSearch.setToolTipText("Suche starten");
		btnStartSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				startSearch();

			}
		});

		btnOpenDetailSearch = new JButton("erweiterte Suche");
		add(btnStartSearch, "cell 5 0");

		btnOpenDetailSearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {

				String selectedItem = (String) comboBoxChooseType
						.getSelectedItem();
				if (selectedItem == "Exponatsname") {
					DetailSearch dialog = new DetailSearch(txtSearch.getText(),
							btnOpenDetailSearch.getLocationOnScreen());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} else {
					DetailSearch dialog = new DetailSearch("",
							btnOpenDetailSearch.getLocationOnScreen());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				}

			}
		});
		add(btnOpenDetailSearch, "cell 6 0");
		setVisible(true);
	}

	/**
	 * Start the normal search
	 */
	public void startSearch() {

		setSearchText(txtSearch.getText());

		setMuseumId(MuseumMainPanel.getInstance().getMuseumId());

		String selectedItem = (String) comboBoxChooseType.getSelectedItem();

		if (selectedItem == "Exponatsname") {
			searchByName(getSearchText());
		} else if (selectedItem == "Sektionsname") {
			searchBySection(getSearchText());
		} else if (selectedItem == "Sektionsname mit Untersektionen") {
			searchBySectionAndSubSection(getSearchText());
		} else if (selectedItem == "Kategoriename") {
			searchByCategory(getSearchText());
		} else if (selectedItem == "Labelname") {
			searchByLabel(getSearchText());
		} else if (selectedItem == "Ausstellung/Leihgabe") {
			searchByOutsourced(getSearchText());
		}
	}

	/**
	 * Search exhibit by Name and shows it on the table
	 * 
	 * @param text
	 */
	public void searchByName(String text) {
		ArrayList<Exhibit> exList = new ArrayList<Exhibit>();
		try {
			exList = Access.specialSearch(getMuseumId(), text, emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, emptyList, true);
		} catch (MuseumNotFoundException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}

		if (exList.isEmpty()) {
			InformationPanel.getInstance().setText(
					"Kein Exponat mit dem Namen " + getSearchText()
							+ " gefunden");
		} else {
			TablePanel.getInstance().updateTable(getMuseumId(), text,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, emptyList, emptyList, true, false);
			InformationPanel.getInstance().setText(
					"Exponat/e mit dem Namen " + getSearchText() + " gefunden");
		}

	}

	/**
	 * Shows all exhibits from the searched section
	 * 
	 * @param text
	 */
	public void searchBySectionAndSubSection(String text) {

		ArrayList<Section> secList = Access.searchSectionName(text, DataAccess
				.getInstance().getMuseumById(museumId));
		ArrayList<Long> secIDList = new ArrayList<Long>();

		for (Section actual : secList) {
			secIDList.add(actual.getId());
		}

		for (Section actual : secList) {
			try {
				for (Section add : Access.getAllSubSectionsFromSection(actual
						.getId()))
					secIDList.add(add.getId());
			} catch (SectionNotFoundException e) {
				InformationPanel.getInstance().setText(e.getMessage());
			}
		}
		if (secList.isEmpty()) {
			InformationPanel.getInstance().setText(
					"Keine Sektion mit dem Namen " + getSearchText()
							+ " gefunden");
		} else {
			TablePanel.getInstance().updateTable(getMuseumId(), "", emptyList,
					emptyList, secIDList, emptyList, emptyList, emptyList,
					emptyList, emptyList, false, false);
			InformationPanel.getInstance().setText(
					"Sektion " + getSearchText() + " gefunden");
		}
	}

	/**
	 * Shows all exhibits from the searched Section
	 * 
	 * @param text
	 */
	public void searchBySection(String text) {

		ArrayList<Section> secList = Access.searchSectionName(text, DataAccess
				.getInstance().getMuseumById(museumId));
		ArrayList<Long> secIDList = new ArrayList<Long>();

		for (Section actual : secList) {
			secIDList.add(actual.getId());
		}

		if (secList.isEmpty()) {
			InformationPanel.getInstance().setText(
					"Keine Sektion mit dem Namen " + getSearchText()
							+ " gefunden");
			TablePanel.getInstance().updateTable(0, "", emptyList, emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, true, false);
		} else {
			TablePanel.getInstance().updateTable(getMuseumId(), "", emptyList,
					emptyList, secIDList, emptyList, emptyList, emptyList,
					emptyList, emptyList, false, false);
			InformationPanel.getInstance().setText(
					"Sektion " + getSearchText() + " gefunden");
		}
	}

	/**
	 * Shows all exhibits from the searched Category
	 * 
	 * @param text
	 */
	protected void searchByCategory(String text) {

		ArrayList<Category> catList = Access.searchCategoryByName(text);
		ArrayList<Long> catIDList = new ArrayList<Long>();

		for (Category actual : catList) {
			catIDList.add(actual.getId());
		}

		if (catList.isEmpty()) {
			InformationPanel.getInstance().setText(
					"Keine Kategorie mit dem Namen " + getSearchText()
							+ " gefunden");
			TablePanel.getInstance().updateTable(0, "", emptyList, emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, true, false);
		} else {
			TablePanel.getInstance().updateTable(getMuseumId(), "", catIDList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, emptyList, false, false);
			InformationPanel.getInstance().setText(
					"Kategorie " + getSearchText() + " gefunden");
		}
	}

	/**
	 * Shows all exhibits from the searched Label
	 * 
	 * @param text
	 */
	protected void searchByLabel(String text) {

		ArrayList<Label> labList = new ArrayList<Label>();
		ArrayList<Long> labIDList = new ArrayList<Long>();

		try {
			labList = Access.searchLabelByName(text);
		} catch (LabelNotFoundException e) {
			InformationPanel.getInstance().setText(e.getMessage());
		}

		for (Label actual : labList) {
			labIDList.add(actual.getId());
		}

		if (labList.isEmpty()) {
			InformationPanel.getInstance()
					.setText(
							"Kein Label mit dem Namen " + getSearchText()
									+ " gefunden");
			TablePanel.getInstance().updateTable(0, "", emptyList, emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, true, false);
		} else {
			TablePanel.getInstance().updateTable(getMuseumId(), "", emptyList,
					emptyList, emptyList, emptyList, labIDList, emptyList,
					emptyList, emptyList, false, false);
			InformationPanel.getInstance().setText(
					"Label " + getSearchText() + " gefunden");
		}
	}

	/**
	 * Shows all exhibits from the searched Outsourced
	 * 
	 * @param text
	 */
	protected void searchByOutsourced(String text) {
		ArrayList<Long> outIDList = new ArrayList<Long>();
		ArrayList<Outsourced> outList = new ArrayList<Outsourced>();
		outList = Access.searchOutsourcedByName(text);

		for (Outsourced actual : outList) {
			outIDList.add(actual.getId());
		}

		if (outList.isEmpty()) {
			InformationPanel.getInstance().setText(
					"Keine Ausstellung/Leihgabe mit dem Namen "
							+ getSearchText() + " gefunden");
			TablePanel.getInstance().updateTable(0, "", emptyList, emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					emptyList, true, false);
		} else {
			TablePanel.getInstance().updateTable(
					MuseumMainPanel.getInstance().getMuseumId(), "", emptyList,
					emptyList, emptyList, emptyList, emptyList, emptyList,
					outIDList, emptyList, false, false);
			InformationPanel.getInstance().setText(
					"Ausstellung/Leihgabe " + getSearchText() + " gefunden");
		}
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * @param searchText
	 *            the searchText to set
	 */
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	/**
	 * @return the museumId
	 */
	public long getMuseumId() {
		return museumId;
	}

	/**
	 * @param museumId
	 *            the museumId to set
	 */
	public void setMuseumId(long museumId) {
		this.museumId = museumId;
	}

}