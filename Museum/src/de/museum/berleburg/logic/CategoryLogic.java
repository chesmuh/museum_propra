package de.museum.berleburg.logic;

import java.util.ArrayList;
import java.util.Collection;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.logicAccess.Access;

/**
 * 
 * This class organizes {@link Category} objects which are provided by our data
 * storage team in de.museum.berleburg.model.
 * <p>
 * It only contains static methods which accept the objects that contain the
 * actual data.
 * <p>
 * Categories can be organized in tree structures. A category can (but does not
 * need to) have a parent category and can (but does not need to) have as many
 * children categories as desired. The children then reference that category as
 * their parent. Children and grand children in this way are called
 * subcategories. The same way grand parents and direct parent are called
 * supercategories.
 * 
 * @author Christian Landel
 * 
 */
public/* static */class CategoryLogic {
	/**
	 * Remove a category from the museum (it will stay in the DB with the
	 * "deleted" time stamp set). 
	 * 
	 * @throws ModelAlreadyDeletedException
	 * @throws ConnectionException
	 * @throws CategoryNotFoundException
	 */
	public static void delete(Category category) throws ConnectionException,
			ModelAlreadyDeletedException, CategoryNotFoundException {
		// move exhibits to miscellaneous category
		Collection<Exhibit> exhibits = DataAccess.getInstance()
				.getAllExhibitsByCategory(category.getId());
		for (Exhibit e : exhibits) {
			e.setCategory_id(LogicManager.getInstance()
					.getMiscellaneousCategory(category.getMuseum()).getId());
			DataAccess.getInstance().update(e);
		}

		deleteCategoryAndChildren(category);

	}

	/**
	 * Deletes all subCategories and move Exhibits to Default-Category.
	 * 
	 * @param cat
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 * @throws CategoryNotFoundException
	 */
	private static void deleteCategoryAndChildren(Category cat)
			throws ConnectionException, ModelAlreadyDeletedException,
			CategoryNotFoundException {
		Collection<Category> categories = DataAccess.getInstance()
				.getChildCategories(cat.getId());

		for (Category c : categories) {
			Collection<Exhibit> exhibits = DataAccess.getInstance()
					.getAllExhibitsByCategory(cat.getId());

			for (Exhibit e : exhibits) {
				e.setCategory_id(LogicManager.getInstance()
						.getMiscellaneousCategory(cat.getMuseum()).getId());
				DataAccess.getInstance().update(e);
			}
			deleteCategoryAndChildren(c);
		}

		DataAccess.getInstance().delete(cat);
	}

	/**
	 * Remove a list of categories from the museum (it will stay in the DB with
	 * the "deleted" time stamp set). 
	 */
	public static void delete(Collection<Category> categories) throws Exception {
		for (Category step : categories)
			delete(step);
	}

	/**
	 * Get exhibits by Category.
	 * 
	 * @param category
	 * @return list of exhibits
	 */
	public static ArrayList<Exhibit> getExhibits(Category category) {
		try {
			return new ArrayList<Exhibit>(DataAccess.getInstance()
					.getAllExhibitsByCategory(category.getId()));
		} catch (Exception e) {
			return new ArrayList<Exhibit>();
		}
	}

	/**
	 * Gets the parent category which the given category is a child of
	 * 
	 * @param category
	 *            the child category
	 * @return
	 * @throws CategoryNotFoundException
	 */
	public static Category getParent(Category category)
			throws CategoryNotFoundException {
		if (!hasParent(category))
			throw new CategoryNotFoundException(
					"Die angegebene Kategorie hat keine Oberkategorie");
		return DataAccess.getInstance()
				.getCategoryById(category.getParent_id());
	}

	/**
	 * Get a list of the immediate subcategories (the children) of the given
	 * category. 
	 */
	public static ArrayList<Category> getChildren(Category category) {
		ArrayList<Category> result = new ArrayList<Category>();
		for (Category step : DataAccess.getInstance()
				.getAllCategoriesByMuseumId(category.getMuseum_id()))
			if (step.getParent_id().equals(category.getId()))
				result.add(step);
		return result;
	}

	/**
	 * Get a list of all subCategories (children, grand children, grand grand
	 * children, ... , grand^n children). 
	 */
	public static ArrayList<Category> getSubcategories(Category category) {
		ArrayList<Category> result = new ArrayList<Category>();
		for (Category step : DataAccess.getInstance()
				.getAllCategoriesByMuseumId(category.getMuseum_id()))
			if (isSubcategory(step, category))
				result.add(step);
		return result;
	}

	/**
	 * Get a list of all superCategories (parent, grand parent, ... , grand^n
	 * parent). 
	 */
	public static ArrayList<Category> getSupercategories(Category category) {
		ArrayList<Category> result = new ArrayList<Category>();
		Category step = category;
		while (hasParent(step)) {
			Category parent = DataAccess.getInstance().getCategoryById(
					step.getParent_id());
			result.add(parent);
			step = parent;
		}
		return result;
	}

	/**
	 * Moves a category under another category, so it becomes a subCategory. 
	 * 
	 * @param category
	 *            the category that will become a child category
	 * @param destination
	 *            the category that will become its parent category
	 * @throws IntegrityException
	 *             if you try to move a category under itself
	 */
	public static void move(Category category, Category destination)
			throws IntegrityException {
		if (destination.getId().equals(category.getId()))
			throw new IntegrityException(category,
					"Eine Kategorie kann nicht unter sich selbst verschoben werden!\n"
							+ "(Die beiden Kategorien sind die selben)");
		if (isSubcategory(destination, category))
			throw new IntegrityException(category,
					"Eine Kategorie kann nicht unter sich selbst verschoben werden!\n"
							+ "(\"" + destination.getName()
							+ "\" ist Unterkategorie von \""
							+ category.getName() + "\")");
		category.setParent_id(destination.getId());
	}

	/**
	 * Makes a subCategory to a root category, meaning it will have no parent, thus
	 * its parent category will not have it as a child anymore. THIS DOES NOT
	 * DELETE THE PARENT CATEGORY
	 */
	public static void removeParent(Category category) {
		category.setParent_id(null);
	}

	/**
	 * Tests if a category has a parent category, i.e. it is a subCategory. 
	 */
	public static boolean hasParent(Category category) {
		if(category.getParent_id() != null && category.getParent_id() != 0 ){
			return  true;
		}
		else
			return false;
	}

	/**
	 * Tests if a category is a subCategory of another one. 
	 * 
	 * @return true, if "subcategory" is a subcategory of "category"
	 */
	public static boolean isSubcategory(Category category,
			Category supercategory) {
		// iterate the given category up to the root
		Category step = category;
		while (hasParent(step)) {
			// hit
			if (supercategory.getId().equals(step.getParent_id()))
				return true;
			// next step is the parent category
			try {
				step = DataAccess.getInstance().getCategoryById(
						step.getParent_id());
			} catch (Exception e) {
				break;
			}
		}
		// no hit
		return false;
	}

	/**
	 * Tests if a category is a superCategory of another one. 
	 * 
	 * @return true, if "category" is a supercategory of "subcategory"
	 */
	public static boolean isSupercategory(Category category,
			Category subcategory) {
		return isSubcategory(subcategory, category);
	}

	/**
	 * Saves the Category.
	 * 
	 * @param toSave
	 * @throws ConnectionException
	 */
	public static void saveCategory(Category toSave) throws ConnectionException {
		DataAccess.getInstance().update(toSave);
	}
	
	/**
	 * Checks if category a is a childcategory of category b. 
	 * 
	 * @author Ralf Heukäufer
	 * @return true if a is childcategory of b
	 * 
	 */
	public static boolean isChildCategory(long a, long b) {
		Category s;
		try {
			s = Access.searchCategoryID(a);
		} catch (CategoryNotFoundException e) {
			return false;
		}
		
		while (s != null && s.getId() != 0) {
			if (s.getParent_id() != null && s.getParent_id().equals(b))
				return true;
			s = s.getParent();
		}
		return false;
	}
	
}
