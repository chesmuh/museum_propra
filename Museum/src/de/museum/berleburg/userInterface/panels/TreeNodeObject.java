package de.museum.berleburg.userInterface.panels;

public class TreeNodeObject {

	/**
	 * Create the Objects in Trees.
	 * 
	 * @author Maximilian Beck
	 */

	private Long museumId;
	private Long sectionId;
	private Long categoryId;
	private Long exhibitionId;
	private Long loanId;
	private Long roleid;
	private Long labelId;

	public Long getLabelId() {
		return labelId;
	}

	public void setLabelId(Long labelId) {
		this.labelId = labelId;
	}

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Long getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Long exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	private String name;

	public TreeNodeObject(String name) {
		this.name = name;

	}

	public Long getMuseumId() {
		return museumId;
	}

	public void setMuseumId(Long museumId) {
		this.museumId = museumId;
	}

	public Long getSectionId() {
		return sectionId;
	}

	public void setSectionId(Long sectionId) {
		this.sectionId = sectionId;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public Long getRoleid() {
		return roleid;
	}

	public void setRoleid(Long roleid) {
		this.roleid = roleid;
	}

}
