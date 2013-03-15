package de.museum.berleburg.userInterface.models;

import java.util.ArrayList;

import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.panels.TableButtonPanel;

public class TableSearchModel {


	private long museumId;
	private String exhibitName;
	private ArrayList<Long> proCategory;
	private ArrayList<Long> contraCategory;
	private ArrayList<Long> proSection;
	private ArrayList<Long> contraSection;
	private ArrayList<Long> proLabel;
	private ArrayList<Long> contraLabel;
	private ArrayList<Long> proOutsourced;
	private ArrayList<Long> contraOutsourced;
	private boolean normalSearch;
	private boolean isEmpty;
	private ArrayList<Exhibit> resultList;
	
	public TableSearchModel(long museumId,String exhName,
			ArrayList<Long> proCat, ArrayList<Long> conCat,
			ArrayList<Long> proSec, ArrayList<Long> conSec,
			ArrayList<Long> proLab, ArrayList<Long> conLab,
			ArrayList<Long> proOut, ArrayList<Long> conOut, boolean normalSearch, boolean isEmpty)
	{
		
		this.museumId = museumId;
		this.exhibitName = exhName;
		this.proCategory = proCat;
		this.contraCategory = conCat;
		this.proSection = proSec;
		this.contraSection = conSec;
		this.proLabel = proLab;
		this.contraLabel = conLab;
		this.proOutsourced = proOut;
		this.contraOutsourced = conOut;
		this.normalSearch = normalSearch;
		this.setEmpty(isEmpty);
		
		if(isEmpty)
		{
			ArrayList<Exhibit> emptyList = new ArrayList<Exhibit>();
			setResultList(emptyList);
			TableButtonPanel.getInstance().setButtonsEnabled(false);
		}
		else
		{
			ArrayList<Exhibit> result = new ArrayList<Exhibit>();
			try {
				result = Access.specialSearch(museumId, exhName, proCat, conCat, proSec, conSec, proLab, conLab, proOut, conOut, normalSearch);
				
			} catch (MuseumNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result.isEmpty())
			{
				TableButtonPanel.getInstance().setButtonsEnabled(false);
			}
			setResultList(result);
		}

		
		
	}
	

	
	/**
	 * @return the proCategory
	 */
	public ArrayList<Long> getProCategory() {
		return proCategory;
	}

	/**
	 * @param proCategory the proCategory to set
	 */
	public void setProCategory(ArrayList<Long> proCategory) {
		this.proCategory = proCategory;
	}

	/**
	 * @return the contraCategory
	 */
	public ArrayList<Long> getContraCategory() {
		return contraCategory;
	}

	/**
	 * @param contraCategory the contraCategory to set
	 */
	public void setContraCategory(ArrayList<Long> contraCategory) {
		this.contraCategory = contraCategory;
	}

	/**
	 * @return the proSection
	 */
	public ArrayList<Long> getProSection() {
		return proSection;
	}

	/**
	 * @param proSection the proSection to set
	 */
	public void setProSection(ArrayList<Long> proSection) {
		this.proSection = proSection;
	}

	/**
	 * @return the contraSection
	 */
	public ArrayList<Long> getContraSection() {
		return contraSection;
	}

	/**
	 * @param contraSection the contraSection to set
	 */
	public void setContraSection(ArrayList<Long> contraSection) {
		this.contraSection = contraSection;
	}

	/**
	 * @return the proLabel
	 */
	public ArrayList<Long> getProLabel() {
		return proLabel;
	}

	/**
	 * @param proLabel the proLabel to set
	 */
	public void setProLabel(ArrayList<Long> proLabel) {
		this.proLabel = proLabel;
	}

	/**
	 * @return the contraLabel
	 */
	public ArrayList<Long> getContraLabel() {
		return contraLabel;
	}

	/**
	 * @param contraLabel the contraLabel to set
	 */
	public void setContraLabel(ArrayList<Long> contraLabel) {
		this.contraLabel = contraLabel;
	}

	/**
	 * @return the proOutsourced
	 */
	public ArrayList<Long> getProOutsourced() {
		return proOutsourced;
	}

	/**
	 * @param proOutsourced the proOutsourced to set
	 */
	public void setProOutsourced(ArrayList<Long> proOutsourced) {
		this.proOutsourced = proOutsourced;
	}

	/**
	 * @return the contraOutsourced
	 */
	public ArrayList<Long> getContraOutsourced() {
		return contraOutsourced;
	}

	/**
	 * @param contraOutsourced the contraOutsourced to set
	 */
	public void setContraOutsourced(ArrayList<Long> contraOutsourced) {
		this.contraOutsourced = contraOutsourced;
	}

	/**
	 * @return the museumId
	 */
	public long getMuseumId() {
		return museumId;
	}

	/**
	 * @param museumId the museumId to set
	 */
	public void setMuseumId(long museumId) {
		this.museumId = museumId;
	}

	/**
	 * @return the resultList
	 */
	public ArrayList<Exhibit> getResultList() {
		return resultList;
	}

	/**
	 * @param resultList the resultList to set
	 */
	public void setResultList(ArrayList<Exhibit> resultList) {
		this.resultList = Access.sortExhibitsByName(resultList);
	}


	/**
	 * @return the normalSearch
	 */
	public boolean isNormalSearch() {
		return normalSearch;
	}


	/**
	 * @param normalSearch the normalSearch to set
	 */
	public void setNormalSearch(boolean normalSearch) {
		this.normalSearch = normalSearch;
	}


	/**
	 * @return the exhibitName
	 */
	public String getExhibitName() {
		return exhibitName;
	}


	/**
	 * @param exhibitName the exhibitName to set
	 */
	public void setExhibitName(String exhibitName) {
		this.exhibitName = exhibitName;
	}



	public boolean isEmpty() {
		return isEmpty;
	}



	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	
}


