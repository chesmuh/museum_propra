package de.museum.berleburg.logicAccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import de.museum.berleburg.datastorage.Constants;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.interfaces.Pair;
import de.museum.berleburg.datastorage.manager.ProcessCallBack;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.History;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Role;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.exceptions.AddressHasNoValueException;
import de.museum.berleburg.exceptions.AddressNotFoundException;
import de.museum.berleburg.exceptions.CategoryNotFoundException;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ContactNotFoundException;
import de.museum.berleburg.exceptions.DatabaseDriverNotFoundException;
import de.museum.berleburg.exceptions.DemoVersionException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.ExhibitionNotFoundException;
import de.museum.berleburg.exceptions.HistoryElementNotFoundException;
import de.museum.berleburg.exceptions.IntegrityException;
import de.museum.berleburg.exceptions.InvalidArgumentsException;
import de.museum.berleburg.exceptions.LabelNotFoundException;
import de.museum.berleburg.exceptions.ModelAlreadyDeletedException;
import de.museum.berleburg.exceptions.MuseumIDNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.NotAZipFileException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.exceptions.PictureNotFoundException;
import de.museum.berleburg.exceptions.SectionNotFoundException;
import de.museum.berleburg.logic.CategoryLogic;
import de.museum.berleburg.logic.ImageLogic;
import de.museum.berleburg.logic.LogicManager;
import de.museum.berleburg.logic.SectionLogic;

/**
 * 
 * @author FSchikowski, Benedikt, Marco, Caroline, Jochen
 * 
 */

public class Access {

	/**
	 * Starts the system. Is necessary to execute before using anything in this
	 * class.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws IOException
	 */
	public static void startSystem(ProcessCallBack callBack)
			throws ConnectionException, IOException {
		LogicManager.startSystem(callBack);
	}

	/**
	 * Exports the database to a file.
	 * 
	 * @author Benedikt
	 * @param path
	 * @param museum_id
	 * @throws NumberFormatException
	 * @throws FileNotFoundException
	 * @throws MuseumIDNotFoundException
	 * @throws DatabaseDriverNotFoundException
	 * @throws NotAZipFileException
	 * @throws AddressHasNoValueException
	 */
	public static void exportDatabase(String path, Long museum_id)
			throws SQLException, NumberFormatException, FileNotFoundException,
			MuseumIDNotFoundException, DatabaseDriverNotFoundException,
			NotAZipFileException, AddressHasNoValueException,
			ConnectionException {
		File export = new File(path);
		if (museum_id == null || museum_id.equals(0L))
			LogicManager.getInstance().exportDatabase(export, null);
		else
			LogicManager.getInstance().exportDatabase(export,
					Integer.parseInt(Long.toString(museum_id)));
	}

	/**
	 * Imports the database from a file.
	 * 
	 * @author Benedikt
	 * @param file
	 * @throws ConnectionException
	 * @throws FileNotFoundException
	 */
	public static void importDatabase(File file) throws ConnectionException,
			FileNotFoundException, SQLException {
		if (!file.exists())
			throw new FileNotFoundException(
					"Die angegebene Datei existiert nicht!");
		LogicManager.getInstance().importDatabase(file);
	}

	/**
	 * Prepares the Commit.
	 * 
	 * @author Marco
	 * @return
	 * @throws ConnectionException
	 */
	public static Updatelist prepareCommit(ProcessCallBack callBack)
			throws ConnectionException {
		return LogicManager.getInstance().prepareCommit(callBack);
	}

	/**
	 * Finalizes the Commit.
	 * 
	 * @author Marco
	 * @throws ConnectionException
	 */
	public static boolean commit(ProcessCallBack updateCallBack)
			throws ConnectionException {
		return LogicManager.getInstance().commit(updateCallBack);
	}

	/**
	 * Prepares the Update.
	 * 
	 * @author Marco
	 * @return
	 * @throws ConnectionException
	 */
	public static Updatelist prepareUpdate(ProcessCallBack callBack)
			throws ConnectionException {
		return LogicManager.getInstance().prepareUpdate(callBack);
	}

	/**
	 * Finalizes the Update.
	 * 
	 * @author Marco
	 * @throws ConnectionException
	 */
	public static boolean update(ProcessCallBack updateCallBack)
			throws ConnectionException {
		return LogicManager.getInstance().update(updateCallBack);
	}

	/**
	 * Parses the double back to two strings.
	 * 
	 * @author Benedikt
	 * @param price
	 * @return
	 */
	public static String[] reParsePrice(double price) {
		return LogicManager.getInstance().reParsePrice(price);
	}

	/**
	 * @author Caroline
	 * @param role_id
	 * @return true if role is used in a contact
	 */
	public static boolean roleIsUsed(Long role_id) {
		return LogicManager.getInstance().roleIsUsed(role_id);
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ Standard ------------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Checks if the address is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @param street
	 * @param housenumber
	 * @param zip
	 * @param city
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardAddress(String street, String housenumber,
			String zip, String city, String state, String country)
			throws InvalidArgumentsException {

		if (street.length() <= 0)
			throw new InvalidArgumentsException(
					"Der Name der Straße ist zu kurz.");
		if (street.length() > Constants.LENGTH_ADDRESS_STREET)
			throw new InvalidArgumentsException(
					"Der Name der Straße ist zu lang.");
		boolean streetMatches = street.matches("[a-zA-ZäöüßéáóÄÖÜÉÁÓ .-]*");
		if (!(city == null || city.equals("")) && streetMatches == false)
			throw new InvalidArgumentsException("Der Name der Straße " + street
					+ " enthält ungültige Zeichen.");
		boolean housenumberMatches = housenumber.matches("[0-9]+[a-zA-Z]?");
		if (housenumber.length() <= 0
				|| housenumber.length() > Constants.LENGTH_ADDRESS_HOUSENUMBER
				|| housenumberMatches == false)
			throw new InvalidArgumentsException("Die Hausnummer " + housenumber
					+ " ist ungueltig.");

		if (city.length() < 3)
			throw new InvalidArgumentsException("Name " + city
					+ "zu Kurz für eine Stadt!");
		if (city.length() > Constants.LENGTH_ADDRESS_TOWN)
			throw new InvalidArgumentsException(
					"Der Name der Stadt ist zu lang.");
		boolean cityMatches = city.matches("[a-zA-ZäöüßéáóÄÖÜÉÁÓ .-]*");
		if (!(city == null || city.equals("")) && cityMatches == false)
			throw new InvalidArgumentsException("Der Name der Stadt " + city
					+ " enthält ungültige Zeichen.");
		boolean zipMatches = zip.matches("[0-9]*");
		if (zip.length() <= 3
				|| zip.length() > Constants.LENGTH_ADDRESS_ZIPCODE
				|| zipMatches == false)
			throw new InvalidArgumentsException("Die Postleitzahl " + zip
					+ " ist ungueltig.");
		try {
			Integer.parseInt(zip);
		} catch (NumberFormatException e) {
			throw new InvalidArgumentsException("Die Postleitzahl " + zip
					+ " ist ungueltig.");
		}
		if (state.length() == 0)
			throw new InvalidArgumentsException(
					"Der Name des Bundeslandes ist zu kurz.");
		if (state.length() > Constants.LENGTH_ADDRESS_STATE)
			throw new InvalidArgumentsException(
					"Der Name des Bundeslandes ist zu lang.");
		boolean stateMatches = state.matches("[a-zA-ZäöüßéáóÄÖÜÉÁÓ .-]*");
		if (!(state == null || state.equals("")) && (stateMatches == false))
			throw new InvalidArgumentsException("Der Name des Bundelandes "
					+ state + " enthält ungültige Zeichen.");
		if (country.length() == 0)
			throw new InvalidArgumentsException(
					"Der Name des Landes ist zu kurz.");
		if (country.length() > Constants.LENGTH_ADDRESS_COUNTRY)
			throw new InvalidArgumentsException(
					"Der Name des Landes ist zu lang.");
		boolean countryMatches = country.matches("[a-zA-ZäöüßéáóÄÖÜÉÁÓ .-]*");
		if (!(country == null || country.equals(""))
				&& (countryMatches == false))
			throw new InvalidArgumentsException("Der Name des Landes "
					+ country + " enthält ungültige Zeichen.");

	}

	/**
	 * Checks if the contact is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @author FSchikowski
	 * @param partnervorname
	 * @param partnername
	 * @param telefon
	 * @param fax
	 * @param email
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardContact(String partnervorname,
			String partnername, String telefon, String fax, String email,
			Long role_id) throws InvalidArgumentsException {

		if (partnervorname.length() < 3)
			throw new InvalidArgumentsException("Vorname " + partnervorname
					+ " für Ansprechpartner zu kurz!");
		if (partnervorname.length() > Constants.LENGTH_CONTACT_FORENAME)
			throw new InvalidArgumentsException("Der Vorname ist zu lang.");
		if (partnername.length() < 3)
			throw new InvalidArgumentsException("Name " + partnername
					+ " fuer Ansprechpartner zu kurz!");
		if (partnername.length() > Constants.LENGTH_CONTACT_NAME)
			throw new InvalidArgumentsException("Der Vorname ist zu lang.");

		// optional +49
		// dann Vorwahl
		// optional -
		// dann die Rufnummer
		// Erlaubt: +49151345262 oder +49151-345262 oder 0151345262 oder
		// 0151-345262 oder 0151/123456
		boolean fonMatches = telefon.matches("[+]?[0-9]*\\/?[0-9]+$");
		if (!(telefon == null || telefon.equals("")) && (fonMatches == false))
			throw new InvalidArgumentsException(
					"Telefonnummer entspricht nicht dem gängigem Format [Bsp.:0271/12345]: "
							+ telefon);
		if (telefon.length() > Constants.LENGTH_CONTACT_FON)
			throw new InvalidArgumentsException("Telefonnummer ist zu lang.");
		boolean faxMatches = fax.matches("[+]?[0-9]*\\/?[0-9]+$");
		if (!(fax == null || fax.equals("")) && (faxMatches == false))
			throw new InvalidArgumentsException(
					"Faxnummer entspricht nicht dem gaengigem Format [Bsp.:0271/12345]: "
							+ fax);
		if (fax.length() > Constants.LENGTH_CONTACT_FON)
			throw new InvalidArgumentsException("Faxnummer ist zu lang.");
		String[] s = email.replace(" ", "").split(";");
		for (String step : s) {
			boolean emailMatches = step.matches(".+@.+\\.[a-z]+");
			if (!(email == null || email.equals("")) && emailMatches == false)
				throw new InvalidArgumentsException("Email ist nicht korrekt: "
						+ step);
			if (step.length() > Constants.LENGTH_CONTACT_EMAIL)
				throw new InvalidArgumentsException("Emailadresse \"" + step
						+ "\" ist zu lang.");
		}
		if (role_id == null)
			throw new InvalidArgumentsException(
					"Bitte legen Sie eine Rolle an.");

	}

	/**
	 * Compresses the image. Scales it too, if it has bounds bigger than 500.
	 * 
	 * @param toCheck
	 * @throws IOException
	 * @throws IntegrityException
	 */
	public static byte[] checkStandardImage(byte[] toCheck) throws IOException,
			IntegrityException {
		toCheck = ImageLogic.checkAndScaleBounds(toCheck);
		return toCheck;
	}

	/**
	 * Checks if the label is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @author Marco(updated)
	 * @param labelName
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardLabel(String labelName)
			throws InvalidArgumentsException {
		try {
			for (Label l : LogicManager.getInstance().searchLabelByName(
					labelName)) {
				if (l.getName().equals(labelName)) {
					throw new InvalidArgumentsException(
							"Es existiert bereits ein Label mit dem Namen \""
									+ labelName + "\"!");
				}
			}
		} catch (LabelNotFoundException e) {
			// All ok.
		}
		if (labelName.length() > Constants.LENGTH_LABEL_NAME)
			throw new InvalidArgumentsException("Das Label \"" + labelName
					+ "\" ist zu lang!");
		if (labelName.equals(""))
			throw new InvalidArgumentsException(
					"Bitte geben Sie einen Namen für das Label ein!");
	}

	/**
	 * Checks if the outsourced is valid.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardOutsourced(String name, Date startDate,
			Date endDate, long museum_id) throws InvalidArgumentsException {
		if (name.length() > Constants.LENGTH_DESTINATION_NAME)
			throw new InvalidArgumentsException("Der Ausstellungsname \""
					+ name + "\" ist zu lang!");
		if (name.length() < 3)
			throw new InvalidArgumentsException("Der Ausstellungsname \""
					+ name + "\" ist zu kurz!");

		for (Outsourced toTest : LogicManager.getInstance()
				.searchOutsourcedByName(name)) {
			if (toTest.getName().equals(name)
					&& toTest.getMuseum_id().longValue() == museum_id && !toTest.isDeleted())
				throw new InvalidArgumentsException(
						"Es gibt bereits eine Ausstellung oder Leihgabe mit dem Namen "
								+ name);
		}

		if (startDate == null)
			throw new InvalidArgumentsException(
					"Es wurde kein Startdatum gesetzt");
		if (endDate != null)
			if (startDate.after(endDate))
				throw new InvalidArgumentsException(
						"Das Enddatum liegt vor dem Startdatum. Bitte überprüfen Sie Ihre Eingabe.");

	}

	/**
	 * Checks if the outsourced is valid.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardOutsourcedForChangeOperation(
			String currentName, String name, Date startDate, Date endDate,
			long museum_id, long outsourced_id)
			throws InvalidArgumentsException {
		if (name.length() > Constants.LENGTH_DESTINATION_NAME)
			throw new InvalidArgumentsException("Der Ausstellungsname \""
					+ name + "\" ist zu lang!");
		if (name.length() < 3)
			throw new InvalidArgumentsException("Der Ausstellungsname \""
					+ name + "\" ist zu kurz!");

		if (!(name.equals(currentName))) {
			for (Outsourced toTest : LogicManager.getInstance()
					.searchOutsourcedByName(name)) {
				if (toTest.getName().equals(name)
						&& toTest.getMuseum_id().longValue() == museum_id)
					throw new InvalidArgumentsException(
							"Es gibt bereits eine Ausstellung oder Leihgabe mit dem Namen "
									+ name);
			}
		}

		if (startDate == null)
			throw new InvalidArgumentsException(
					"Es wurde kein Startdatum gesetzt");
		if (endDate != null)
			if (startDate.after(endDate))
				throw new InvalidArgumentsException(
						"Das Enddatum liegt vor dem Startdatum. Bitte überprüfen Sie ihre Eingabe.");

	}

	/**
	 * Checks if the loan is valid.
	 * 
	 * @author Marco
	 * @param name
	 * @param startDate
	 * @param endDate
	 * @param museum_id
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardLoan(String name, Date startDate,
			Date endDate, long museum_id) throws InvalidArgumentsException {
		checkStandardOutsourced(name, startDate, endDate, museum_id);
		if (getAllContact() == null) {
			throw new InvalidArgumentsException(
					"Bitte erstellen Sie einen Kontakt.");
		}
	}

	/**
	 * Checks if the exhibit is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @param name
	 * @param count
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardExhibit(String name, long count,
			String euro, String cent) throws InvalidArgumentsException {

		// ------ Testversion, auskommentieren für normale Version
		// int maxExhibitCount = 100;
		// if(DataAccess.getInstance().getAllExhibits().size()>=maxExhibitCount)
		// throw new InvalidArgumentsException(
		// "Sie benutzen die Demoversion des Programms.\nSie können daher nicht mehr als "+maxExhibitCount+" Exponate anlegen.\nBenutzen Sie die Vollversion, um diese Beschränkung aufzuheben.");
		// ------

		if (name.length() == 0)
			throw new InvalidArgumentsException(
					"Geben Sie bitte einen Namen ein!");
		if (name.length() > Constants.LENGTH_EXHIBIT_NAME)
			throw new InvalidArgumentsException("Der Name ist zu lang!");
		if (count <= 0)
			throw new InvalidArgumentsException(
					"Keine negative Anzahl moeglich: " + count);
		if (count == 0L)
			count = 1L;
		if (!euro.matches("[0-9]*[0-9]$"))
			throw new InvalidArgumentsException("Der Wert " + euro
					+ " ist kein gueltiger Wert. ");
		if (!cent.matches("[0-9][0-9]$"))
			if (cent.equals("")) {
				cent = "00";
			} else
				throw new InvalidArgumentsException("Der Wert " + cent
						+ " ist kein gueltiger Wert. ");
	}

	/**
	 * Checks if the Category is valid.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param museum_id
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	private static void checkStandardCategory(String name, long museum_id)
			throws InvalidArgumentsException, MuseumNotFoundException {
		if (name.length() > Constants.LENGTH_CATEGORY_NAME)
			throw new InvalidArgumentsException("Der Name ist zu lang!");
		if (name.length() == 0)
			throw new InvalidArgumentsException(
					"Geben Sie bitte einen Namen ein!");

		ArrayList<Category> categories = LogicManager
				.getAllCategoriesByMuseum(LogicManager.getInstance()
						.searchMuseumById(museum_id));
		for (Category cat : categories) {
			if (cat.getName().equals(name))
				throw new InvalidArgumentsException(
						"Der Name existiert bereits");
		}
	}

	/**
	 * Checks if the Category is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Ralf Heukäufer
	 * @param name
	 * @param museum_id
	 * @param category_id
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	private static void checkStandardCategoryForChangeOperation(String name,
			long museum_id, long category_id, Long parent_id)
			throws InvalidArgumentsException, MuseumNotFoundException {
		if (name.length() > Constants.LENGTH_CATEGORY_NAME)
			throw new InvalidArgumentsException("Der Name ist zu lang!");
		if (name.length() == 0)
			throw new InvalidArgumentsException(
					"Geben Sie bitte einen Namen ein!");

		Long pid = parent_id;
		if (pid == null)
			pid = 0L;

		// Auf child testen
		if (CategoryLogic.isChildCategory(pid, category_id))
			throw new InvalidArgumentsException(
					"Eine Kategorie kann nicht in eine ihrer Unterkategorien verschoben werden.");

		ArrayList<Category> categories = LogicManager
				.getAllCategoriesByMuseum(LogicManager.getInstance()
						.searchMuseumById(museum_id));
		for (Category cat : categories) {
			if (cat.getName().equals(name)
					&& Long.valueOf(cat.getId()) != category_id)
				throw new InvalidArgumentsException(
						"Der Name existiert bereits");
		}
	}

	/**
	 * Checks if the section is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @author Marco(updated)
	 * @param name
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	private static void checkStandardSection(String name, Long parent_id,
			long museum_id) throws InvalidArgumentsException,
			MuseumNotFoundException {
		if (name.length() > Constants.LENGTH_SECTION_NAME)
			throw new InvalidArgumentsException("Der Name ist zu lang!");
		if (name.length() == 0)
			throw new InvalidArgumentsException(
					"Geben Sie bitte einen Namen ein!");
		Long pid = parent_id;
		if (pid == null)
			pid = 0L;

		for (Section s : LogicManager.getInstance().searchSectionByName(name,
				searchMuseumID(museum_id))) {
			Long tempID = s.getParent_id();
			if (tempID == null)
				tempID = 0L;
			if (s.getName().equals(name) && (tempID.equals(pid))
					&& Long.valueOf(s.getMuseum_id()) == museum_id) {
				throw new InvalidArgumentsException(
						"Es existiert bereits eine Sektion mit diesem Namen \""
								+ name + "\"!");
			}
		}

	}

	/**
	 * Checks if the Section is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @author Marco(updated)
	 * @param name
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	private static void checkStandardSectionForChangeOperation(String name,
			Long parent_id, long section_id, long museum_id)
			throws InvalidArgumentsException, MuseumNotFoundException {
		if (name.length() > Constants.LENGTH_SECTION_NAME)
			throw new InvalidArgumentsException("Der Name ist zu lang!");
		if (name.length() == 0)
			throw new InvalidArgumentsException(
					"Geben Sie bitte einen Namen ein!");
		Long pid = parent_id;
		if (pid == null)
			pid = 0L;

		// Auf child testen
		if (SectionLogic.isChildSection(pid, section_id))
			throw new InvalidArgumentsException(
					"Eine Sektion kann nicht in eine ihrer Untersektionen verschoben werden.");

		for (Section s : LogicManager.getInstance().searchSectionByName(name,
				searchMuseumID(museum_id))) {
			Long tempID = s.getParent_id();
			if (tempID == null)
				tempID = 0L;
			if (s.getName().equals(name) && (tempID.equals(pid))
					&& Long.valueOf(s.getId()) != section_id
					&& Long.valueOf(s.getMuseum_id()) == museum_id) {
				throw new InvalidArgumentsException(
						"Es existiert bereits eine Sektion mit diesem Namen \""
								+ name + "\"!");
			}
		}

	}

	/**
	 * Checks if the museum is valid.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * @param name
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardMuseum(String name)
			throws InvalidArgumentsException {
		for (Museum m : LogicManager.getInstance().searchMuseumByName(name)) {
			if (m.getName().equals(name))
				throw new InvalidArgumentsException(
						"Es existiert bereits ein Museum mit dem Namen \""
								+ name + "\"!");
		}
		if (name.length() < 3)
			throw new InvalidArgumentsException("Name " + name
					+ " fuer Museum zu kurz!");
		if (name.length() > Constants.LENGTH_MUSEUM_NAME)
			throw new InvalidArgumentsException("Name " + name
					+ " fuer Museum zu lang!");

	}

	/**
	 * Checks if the Museum is valid, when it should be changed.
	 * 
	 * @author Jochen Saßmannshausen
	 * @param name
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandardMuseumForChangeOperation(String name,
			long museum_id) throws InvalidArgumentsException {
		for (Museum m : LogicManager.getInstance().searchMuseumByName(name)) {
			if (m.getName().equals(name) && !(m.getId().equals(museum_id)))
				throw new InvalidArgumentsException(
						"Es existiert bereits ein Museum mit dem Namen \""
								+ name + "\"!");
		}
		if (name.length() < 3)
			throw new InvalidArgumentsException("Name " + name
					+ " fuer Museum zu kurz!");
		if (name.length() > Constants.LENGTH_MUSEUM_NAME)
			throw new InvalidArgumentsException("Name " + name
					+ " fuer Museum zu lang!");

	}

	/**
	 * Checks if the Role is valid.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @throws InvalidArgumentsException
	 */
	private static void checkStandartRole(String name)
			throws InvalidArgumentsException {
		if (name.length() < 3)
			throw new InvalidArgumentsException("Name " + name
					+ " fuer Rolle zu kurz!");
		if (name.length() > Constants.LENGTH_ROLE_NAME)
			throw new InvalidArgumentsException("Der Name \"" + name
					+ "\" ist zu lang!");
	}

	/**
	 * Checks if the Date is valid.
	 * 
	 * @author Caroline Bender
	 * 
	 * @param dateName
	 * @param valid
	 * @throws InvalidArgumentsException
	 */
	public static void checkStandardDate(String dateName, boolean valid, Date date)
			throws InvalidArgumentsException {

//		Date test = (new GregorianCalendar(2038, 01, 19)).getTime();
		Date test=null;
		try {
			test = new SimpleDateFormat("yyyyMMdd").parse("20380119");
		} catch (ParseException e) {
			// should never happen
			e.printStackTrace();
		}
		
		if (date!=null && test.before(date)) {
			throw new InvalidArgumentsException(
					"Ein Datum nach dem 19.01.2038 ist leider nicht möglich.");
		}

		if (valid == false)
			throw new InvalidArgumentsException("Das eingegebene " + dateName
					+ " ist ungültig.");
	}

	/**
	 * Checks if date is valid.
	 * 
	 * @author Caroline Bender
	 * @author Marco
	 * @param day
	 * @param month
	 * @param year
	 * @return true if date is valid
	 * @throws InvalidArgumentsException
	 */
	public static boolean checkDate(String day, String month, String year)
			throws InvalidArgumentsException {

		if (day.equals("") || month.equals("") || year.equals(""))
			return false;
		if (day.length() != 2 || month.length() != 2 || year.length() != 4)
			return false;

		int dayNumber;
		int monthNumber;
		int yearNumber;

		try {
			dayNumber = Integer.parseInt(day);
			monthNumber = Integer.parseInt(month);
			yearNumber = Integer.parseInt(year);
		} catch (NumberFormatException e) {
			return false;
		}

		int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (yearNumber % 4 == 0)
			monthDays[1] = 29;

		if (monthNumber > 12 || monthNumber < 0 || dayNumber < 0
				|| yearNumber < 1970)
			return false;
		if (dayNumber > monthDays[monthNumber - 1])
			return false;

		

		return true;

	}

	/**
	 * Checks if date is valid.
	 * 
	 * @author Caroline Bender
	 * @author Marco
	 * @param day
	 * @param month
	 * @param year
	 * @return true if date is valid
	 * @throws InvalidArgumentsException
	 * @throws EndDateBeforNowException 
	 */
//	public static boolean checkEndDate(String day, String month, String year)
//			throws InvalidArgumentsException, EndDateBeforNowException {
//
//		if (day.equals("") || month.equals("") || year.equals(""))
//			return false;
//		if (day.length() != 2 || month.length() != 2 || year.length() != 4)
//			return false;
//
//		int dayNumber;
//		int monthNumber;
//		int yearNumber;
//
//		try {
//			dayNumber = Integer.parseInt(day);
//			monthNumber = Integer.parseInt(month);
//			yearNumber = Integer.parseInt(year);
//		} catch (NumberFormatException e) {
//			return false;
//		}
//
//		int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
//		if (yearNumber % 4 == 0)
//			monthDays[1] = 29;
//
//		if (monthNumber > 12 || monthNumber < 0 || dayNumber < 0
//				|| yearNumber < 1970)
//			return false;
//		if (dayNumber > monthDays[monthNumber - 1])
//			return false;
//
//		Date currentDate = new Date(System.currentTimeMillis());
//		if(yearNumber < currentDate.getYear()) {
//			throw new EndDateBeforNowException();
//		} else if(monthNumber < currentDate.getMonth()) {
//			throw new EndDateBeforNowException();
//		} else if(dayNumber < currentDate.getDay()) {
//			throw new EndDateBeforNowException();
//		}
//
//		return true;
//	}
	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ INSERT --------------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Insert museum with a contact partner and an address.
	 * 
	 * @author FSchikowski
	 * @author Benedikt
	 * 
	 * @param name
	 * @param museumbeschreibung
	 * @param street
	 * @param housenumber
	 * @param zip
	 * @param city
	 * @param state
	 * @param country
	 * @return
	 * @throws InvalidArgumentsException
	 * @throws ConnectionException
	 * @throws MuseumNotFoundException
	 */
	public static long insertAllMuseum(String name, String museumbeschreibung,
			String street, String housenumber, String zip, String city,
			String state, String country) throws InvalidArgumentsException,
			ConnectionException, MuseumNotFoundException {
		checkStandardMuseum(name);
		checkStandardAddress(street, housenumber, zip, city, state, country);
		Address adr = new Address(street, housenumber, zip, city, state,
				country);
		LogicManager.getInstance().insertAddress(adr);
		Museum m = new Museum(name, museumbeschreibung, adr.getId());
		long m_id = LogicManager.getInstance().insertMuseum(m);
		Access.insertCategory("Sonstiges", null, m_id);
		return m_id;
	}

	/**
	 * Inserts a section.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param description
	 * @param parent_id
	 * @param museum_id
	 * @return
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	public static long insertSection(String name, String description,
			Long parent_id, long museum_id) throws ConnectionException,
			InvalidArgumentsException, MuseumNotFoundException {

		checkStandardSection(name, parent_id, museum_id);
		return LogicManager.getInstance().insertSection(
				new Section(name, description, parent_id, museum_id));
	}

	/**
	 * Insert an exhibit.
	 * 
	 * @author Benedikt
	 * 
	 * @throws InvalidArgumentsException
	 * @throws ConnectionException
	 * @throws MuseumNotFoundException
	 * @throws CategoryNotFoundException
	 * @throws DemoVersionException
	 *             If Exhibit-Count > 100
	 */
	public static long insertExhibit(String name, String description,
			long museum_id, Long section_id, Long category_id, long count,
			String rfid, String euro, String cent)
			throws InvalidArgumentsException, ConnectionException,
			CategoryNotFoundException, MuseumNotFoundException,
			DemoVersionException {
		checkStandardExhibit(name, count, euro, cent);
		if (Constants.DEMO_VERSION
				&& DataAccess.getInstance().getExhibitCount() >= 100) {
			throw new DemoVersionException();
		}
		if (category_id.equals(null) || category_id.equals(0L))
			category_id = LogicManager
					.getInstance()
					.getMiscellaneousCategory(
							LogicManager.getInstance().searchMuseumById(
									museum_id)).getId();
		return LogicManager.getInstance().insertExhibit(
				new Exhibit(name, description, section_id, category_id, count,
						rfid, museum_id, LogicManager
								.getInstance().parsePrice(euro, cent)));
	}

	/**
	 * Inserts a picture of an exhibit.
	 * 
	 * @author Benedikt
	 * 
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws IntegrityException
	 */
	public static long insertPicture(long exhibitId, byte[] toAdd)
			throws IOException, ConnectionException, IntegrityException {
		toAdd = checkStandardImage(toAdd);
		Image toSave = new Image(toAdd, "", exhibitId);
		return ((LogicManager) LogicManager.getInstance())
				.insertPicture(toSave);

	}

	/**
	 * Inserts a new exhibition.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws InvalidArgumentsException
	 * @throws ConnectionException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static long insertExhibition(String name, String description,
			Date startDate, Date endDate, long museum_id, boolean validStart,
			boolean validEnd) throws InvalidArgumentsException,
			ConnectionException {
		checkStandardDate("Startdatum", validStart, startDate);
		checkStandardDate("Enddatum", validEnd, endDate);
		checkStandardOutsourced(name, startDate, endDate, museum_id);
		return LogicManager.getInstance().insertOutsourced(
				new Outsourced(name, description, startDate, endDate, null,
						null, museum_id));
	}

	/**
	 * Inserts a new loan.
	 * 
	 * @author Benedikt
	 * 
	 * @param startDate
	 * @param endDate
	 * @param address_id
	 * @param contact_id
	 * @return
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static long insertLoan(String name, String description,
			Date startDate, Date endDate, Long address_id, Long contact_id,
			long museum_id, boolean validStart, boolean validEnd)
			throws ConnectionException, InvalidArgumentsException {
		checkStandardDate("Startdatum", validStart, startDate);
		checkStandardDate("Enddatum", validEnd, endDate);
		checkStandardLoan(name, startDate, endDate, museum_id);
		return LogicManager.getInstance().insertOutsourced(
				new Outsourced(name, description, startDate, endDate,
						address_id, contact_id, museum_id));
	}

	/**
	 * Adds an exhibit to an outsourced.
	 * 
	 * @author Benedikt
	 * 
	 * @throws OutsourcedNotFoundException
	 * @throws ConnectionException
	 */
	public static long addToExhibition(long exhibit_id, long outsourced_id)
			throws ConnectionException, OutsourcedNotFoundException {
		return LogicManager.getInstance().addToOutsourced(
				DataAccess.getInstance().getExhibitById(exhibit_id),
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Inserts a category.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws MuseumNotFoundException
	 * @throws InvalidArgumentsException
	 */
	public static long insertCategory(String name, Long parent_id,
			long museum_id) throws ConnectionException,
			InvalidArgumentsException, MuseumNotFoundException {
		checkStandardCategory(name, museum_id);
		// parent_id==null if museum is parent
		try {
			Category parent = LogicManager.getInstance().searchCategoryById(
					parent_id);
			if (parent != null
					&& parent.getName().equals("Sonstiges")
					&& (parent.getParent_id() == null || parent.getParent_id()
							.equals(0L)))
				throw new InvalidArgumentsException(
						"Unter der Kategorie \"Sonstiges\" kann keine Unterkategorie erstellt werden. ");
		} catch (CategoryNotFoundException e) {
			// Do nothing
		}
		return LogicManager.getInstance().insertCategory(
				new Category(name, museum_id, parent_id));
	}

	/**
	 * Adds an exhibit to a category.
	 * 
	 * @author Benedikt
	 * 
	 * @throws CategoryNotFoundException
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 * 
	 */
	public static long addToCategory(long exhibit_id, long category_id)
			throws ConnectionException, ExhibitNotFoundException,
			CategoryNotFoundException {
		LogicManager.getInstance().moveToCategory(
				LogicManager.getInstance().searchExhibitById(exhibit_id),
				LogicManager.getInstance().searchCategoryById(category_id));
		return exhibit_id;
	}

	/**
	 * Adds an exhibit to a loan.
	 * 
	 * @author Benedikt
	 * 
	 * @throws OutsourcedNotFoundException
	 * @throws ConnectionException
	 */
	public static long addToLoan(long exhibit_id, long outsourced_id)
			throws ConnectionException, OutsourcedNotFoundException {
		return LogicManager.getInstance().addToOutsourced(
				DataAccess.getInstance().getExhibitById(exhibit_id),
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Inserts a contact.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param forename
	 * @param fon
	 * @param email
	 * @param museum_id
	 * @param description
	 * @param fax
	 * @return
	 * @throws ConnectionException
	 * @throws MuseumNotFoundException
	 * @throws InvalidArgumentsException
	 */
	public static long insertContact(String name, String forename, String fon,
			String email, long address_id, String description, String fax,
			Long role_id) throws ConnectionException, MuseumNotFoundException,
			InvalidArgumentsException {
		checkStandardContact(forename, name, fon, fax, email, role_id);
		return LogicManager.getInstance().insertContact(
				new Contact(name, forename, fon, email, description, fax,
						address_id, role_id));
	}

	/**
	 * Inserts an address.
	 * 
	 * @author Marco
	 * 
	 * @param street
	 * @param housenumber
	 * @param zipcode
	 * @param town
	 * @param state
	 * @param country
	 * @return
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static long insertAddress(String street, String housenumber,
			String zipcode, String town, String state, String country)
			throws ConnectionException, InvalidArgumentsException {
		checkStandardAddress(street, housenumber, zipcode, town, state, country);
		return LogicManager.getInstance()
				.insertAddress(
						new Address(street, housenumber, zipcode, town, state,
								country));
	}

	/**
	 * Inserts a new label.
	 * 
	 * @author Marco
	 * 
	 * @param name
	 * @param exhibit_id
	 * @return
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 * @throws LabelNotFoundException
	 */
	public static long insertLabel(String name) throws ConnectionException,
			InvalidArgumentsException {
		checkStandardLabel(name);
		return LogicManager.getInstance().insertLabel(new Label(name));
	}

	/**
	 * Adds an exhibit to a label.
	 * 
	 * @author Marco
	 * @param toAdd
	 * @param label
	 * @return
	 * @throws InvalidArgumentsException
	 * @throws LabelNotFoundException
	 * @throws ConnectionException
	 */
	public static long addToLabel(Exhibit toAdd, Label label)
			throws InvalidArgumentsException, LabelNotFoundException,
			ConnectionException {
		return LogicManager.getInstance().addToLabel(toAdd, label);
	}

	/**
	 * Inserts a role.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param museum_id
	 * @return
	 * @throws InvalidArgumentsException
	 * @throws ConnectionException
	 */
	public static long insertRole(String name, long museum_id)
			throws InvalidArgumentsException, ConnectionException {
		checkStandartRole(name);
		return LogicManager.getInstance().insertRole(new Role(name, museum_id));
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ update functions ----------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Copies a categoryTree.
	 * 
	 * @author Benedikt
	 * @author Fschikowski
	 * @author Ralf Heukäufer
	 * @param category_id
	 * @param museum_id
	 * @return
	 * @throws CategoryNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 */
	public static void copyCategory(long category_id, long museum_id)
			throws ConnectionException, MuseumNotFoundException,
			InvalidArgumentsException {
		Category cat = DataAccess.getInstance().getCategoryById(category_id);
		if (!CategoryLogic.hasParent(cat) && cat.getName().equals("Sonstiges")) {

		} else {
			ArrayList<Category> toCopy = new ArrayList<Category>(DataAccess
					.getInstance().getAllSubCategories(category_id));
			ArrayList<Category> remove = new ArrayList<>();
			for (Category step : toCopy) {
				try {
					checkStandardCategory(step.getName(), museum_id);
				} catch (InvalidArgumentsException e) {
					remove.addAll(DataAccess.getInstance().getAllSubCategories(
							step.getId()));
					e.printStackTrace();
				} catch (MuseumNotFoundException e) {
					e.printStackTrace();
				}
			}
			toCopy.removeAll(remove);
			LogicManager.getInstance().copyCategories(toCopy, museum_id);
		}
	}

	/**
	 * @author Ralf Heukäufer
	 * @return
	 * @throws CategoryNotFoundException
	 */
	public static HashSet<Category> getallChildCategorys(long category_id)
			throws CategoryNotFoundException {
		Category category = LogicManager.getInstance().searchCategoryById(
				category_id);
		HashSet<Category> toCopy = new HashSet<Category>();
		HashSet<Category> cList = new HashSet<Category>();
		HashSet<Category> returnList = new HashSet<Category>();
		cList = toCopy;
		toCopy.add(category);
		toCopy.addAll(LogicManager.getAllCategoriesByCategory(category));

		toCopy.removeAll(cList);

		for (Category c : toCopy)
			returnList.add(c);
		for (Category c : toCopy)
			getallChildCategorys(c.getId());
		return returnList;
	}

	/**
	 * Changes the fields of a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @param name
	 * @param description
	 * @throws MuseumNotFoundException
	 * @throws ConnectionException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidArgumentsException
	 */
	public static void changeMuseum(long museum_id, String name,
			String description) throws FileNotFoundException, IOException,
			ConnectionException, MuseumNotFoundException,
			InvalidArgumentsException {
		checkStandardMuseumForChangeOperation(name, museum_id);
		LogicManager.getInstance().changeMuseum(
				LogicManager.getInstance().searchMuseumById(museum_id), name,
				description);
	}

	/**
	 * Changes the fields of a section.
	 * 
	 * @author Benedikt
	 * 
	 * @param section_id
	 * @param name
	 * @param description
	 * @param parent_id
	 * @param museum_id
	 * @throws SectionNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 * @throws MuseumNotFoundException
	 * @throws CategoryNotFoundException
	 */
	public static void changeSection(long section_id, String name,
			String description, Long parent_id, long museum_id)
			throws ConnectionException, SectionNotFoundException,
			InvalidArgumentsException, MuseumNotFoundException,
			CategoryNotFoundException {
		checkStandardSectionForChangeOperation(name, parent_id, section_id,
				museum_id);
		LogicManager.getInstance().changeSection(
				LogicManager.getInstance().searchSectionById(section_id), name,
				description, parent_id, museum_id);
	}

	/**
	 * Changes the fields of an exhibit.
	 * 
	 * @author Benedikt
	 * 
	 * @param exhibit_id
	 * @param name
	 * @param description
	 * @param section_id
	 * @param category_id
	 * @param count
	 * @param rfid
	 * @param museum_id
	 * @param outsourced_id
	 * @param euro
	 * @param cent
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static void changeExhibit(long exhibit_id, String name,
			String description, Long section_id, Long category_id, long count,
			String rfid, long museum_id, String euro,
			String cent) throws ConnectionException, ExhibitNotFoundException,
			InvalidArgumentsException {
		checkStandardExhibit(name, count, euro, cent);
		Double price = LogicManager.getInstance().parsePrice(euro, cent);
		LogicManager.getInstance().changeExhibit(
				LogicManager.getInstance().searchExhibitById(exhibit_id), name,
				description, section_id, category_id, count, rfid, museum_id,
				price);
	}

	/**
	 * Changes the fields of a category.
	 * 
	 * @author Benedikt
	 * @author Marco
	 * 
	 * @param category_id
	 * @param name
	 * @param museum_id
	 * @param parent_id
	 * @throws ConnectionException
	 * @throws CategoryNotFoundException
	 * @throws MuseumNotFoundException
	 * @throws InvalidArgumentsException
	 */
	public static void changeCategory(long category_id, String name,
			long museum_id, Long parent_id) throws ConnectionException,
			CategoryNotFoundException, InvalidArgumentsException,
			MuseumNotFoundException {
		checkStandardCategoryForChangeOperation(name, museum_id, category_id,
				parent_id);
		LogicManager.getInstance().changeCategory(
				LogicManager.getInstance().searchCategoryById(category_id),
				name, museum_id, parent_id);
	}

	/**
	 * Changes the fields of a picture.
	 * 
	 * @author Benedikt
	 * 
	 * @param picture_id
	 * @param image
	 * @param name
	 * @param exhibit_id
	 * @throws Exception
	 * @throws ConnectionException
	 * @throws PictureNotFoundException
	 * @throws IOException
	 * @throws IntegrityException
	 */
	public static void changePicture(long picture_id, byte[] image,
			String name, long exhibit_id) throws ConnectionException,
			IOException, PictureNotFoundException, IntegrityException {
		checkStandardImage(image);
		LogicManager.getInstance().changePicture(
				LogicManager.getInstance().searchPictureById(picture_id),
				image, name, exhibit_id);
	}

	/**
	 * Changes the fields of an address.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @param street
	 * @param housenumber
	 * @param zipcode
	 * @param town
	 * @param state
	 * @param country
	 * @throws AddressNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static void changeAddress(long address_id, String street,
			String housenumber, String zipcode, String town, String state,
			String country) throws ConnectionException,
			AddressNotFoundException, InvalidArgumentsException {
		checkStandardAddress(street, housenumber, zipcode, town, state, country);
		LogicManager.getInstance().changeAddress(
				LogicManager.getInstance().searchAddressById(address_id),
				street, housenumber, zipcode, town, state, country);
	}

	/**
	 * Changes the fields of a contact.
	 * 
	 * @author Benedikt
	 * 
	 * @param contact_id
	 * @param name
	 * @param forename
	 * @param fon
	 * @param email
	 * @param museum_id
	 * @param description
	 * @param fax
	 * @throws ContactDetailNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static void changeContact(long contact_id, String name,
			String forename, String fon, String email, long address_id,
			String description, String fax, Long role_id)
			throws ConnectionException, ContactNotFoundException,
			InvalidArgumentsException {
		checkStandardContact(forename, name, fon, fax, email, role_id);
		LogicManager.getInstance().changeContact(
				LogicManager.getInstance().searchContactDetailById(contact_id),
				name, forename, fon, email, description, fax, role_id,
				address_id);
	}

	/**
	 * Changes the fields of a label.
	 * 
	 * @author Marco
	 * 
	 * @param label_id
	 * @param name
	 * @throws ConnectionException
	 * @throws LabelNotFoundException
	 * @throws InvalidArgumentsException
	 */
	public static void changeLabel(long label_id, String name)
			throws ConnectionException, LabelNotFoundException,
			InvalidArgumentsException {
		checkStandardLabel(name);
		LogicManager.getInstance().changeLabel(
				LogicManager.getInstance().searchLabelById(label_id), name);

	}

	/**
	 * Changes the fields of an outsourced.
	 * 
	 * @author Caroline
	 * 
	 * @param outsourced_id
	 * @param name
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param Address_id
	 * @param contact_id
	 * @throws OutsourcedNotFoundException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 */
	public static void changeOutsourced(long outsourced_id, String name,
			String description, Date startDate, Date endDate, Long Address_id,
			Long contact_id, boolean validStart, boolean validEnd)
			throws ConnectionException, OutsourcedNotFoundException,
			InvalidArgumentsException {
		checkStandardDate("Startdatum", validStart, startDate);
		checkStandardDate("Enddatum", validEnd, endDate);
		checkStandardOutsourcedForChangeOperation(LogicManager.getInstance()
				.searchOutsourcedById(outsourced_id).getName(), name,
				startDate, endDate, LogicManager.getInstance()
						.searchOutsourcedById(outsourced_id).getMuseum_id(),
				outsourced_id);
		LogicManager.getInstance().changeOutsourced(
				LogicManager.getInstance().searchOutsourcedById(outsourced_id),
				name, description, startDate, endDate, Address_id, contact_id);
	}

	/**
	 * Changes the fields of a role.
	 * 
	 * @author Benedikt
	 * 
	 * @param role_id
	 * @param name
	 * @param museum_id
	 * @throws ConnectionException
	 */
	public static void changeRole(long role_id, String name, long museum_id)
			throws ConnectionException {
		LogicManager.changeRole(
				LogicManager.getInstance().searchRoleById(role_id), name,
				museum_id);
	}

	/**
	 * Checks if no exhibit is in the outsourced.
	 * 
	 * @author Benedikt
	 * 
	 * @param toCheck
	 * @return
	 * @throws OutsourcedNotFoundException
	 */
	public static boolean isEveryThingBack(long outsourced_id)
			throws OutsourcedNotFoundException {
		return LogicManager.getInstance().isEveryThingBack(
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Checks if an outsourced is expired.
	 * 
	 * @author Benedikt
	 * 
	 * @param toCheck
	 * @return
	 * @throws OutsourcedNotFoundException
	 */
	public static boolean isExpired(long outsourced_id) throws OutsourcedNotFoundException {
		return LogicManager.getInstance().isExpired(
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Checks if an outsourced is permanent oursourced.
	 * 
	 * @author Benedikt
	 * 
	 * @param toCheck
	 * @return
	 * @throws OutsourcedNotFoundException
	 */
	public static boolean isPermanentOutsourced(long outsourced_id)
			throws OutsourcedNotFoundException {
		return LogicManager.getInstance().isPermanentOutsourced(
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ move functions ------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Moves a section to another section or museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param section_id
	 * @param newParentSection_id
	 * @param newMuseum_id
	 * @throws MuseumNotFoundException
	 * @throws SectionNotFoundException
	 * @throws TargetIsChildException
	 * @throws IOException
	 * @throws ConnectionException
	 * @throws IntegrityException
	 * @throws FileNotFoundException
	 */
	public static void moveSection(long section_id, Long newParentSection_id,
			long newMuseum_id) throws FileNotFoundException,
			IntegrityException, ConnectionException, IOException,
			SectionNotFoundException, MuseumNotFoundException {
		if (newParentSection_id == null || newParentSection_id.equals(0L)) {
			LogicManager.getInstance().moveSection(
					LogicManager.getInstance().searchSectionById(section_id),
					LogicManager.getInstance().searchMuseumById(newMuseum_id));
		} else {
			LogicManager.getInstance().moveSection(
					LogicManager.getInstance().searchSectionById(section_id),
					LogicManager.getInstance().searchSectionById(
							newParentSection_id));
		}
	}

	/**
	 * Moves an exhibit into another section.
	 * 
	 * @author Benedikt
	 * 
	 * @param exhibit_id
	 * @param newParentSection_id
	 * @param newMuseum_id
	 * @throws MuseumNotFoundException
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 * @throws IntegrityException
	 * @throws SectionNotFoundException
	 *             <<<<<<< Access.java
	 * @throws CategoryNotFoundException
	 *             =======
	 * @throws CategoryNotFoundException
	 *             >>>>>>> 1.136
	 */
	public static void moveExhibit(long exhibit_id, Long newParentSection_id,
			long newMuseum_id) throws IntegrityException, ConnectionException,
			ExhibitNotFoundException, MuseumNotFoundException,
			SectionNotFoundException, CategoryNotFoundException {
		if (newParentSection_id == null || newParentSection_id.equals(0L)) {
			LogicManager.getInstance().moveExhibit(
					LogicManager.getInstance().searchExhibitById(exhibit_id),
					LogicManager.getInstance().searchMuseumById(newMuseum_id));
		} else {
			LogicManager.getInstance().moveExhibit(
					LogicManager.getInstance().searchExhibitById(exhibit_id),
					LogicManager.getInstance().searchSectionById(
							newParentSection_id));
		}
	}

	/**
	 * Moves a category under another one.
	 * 
	 * @author Marco
	 * 
	 * @param category_id
	 * @param parentCategory_id
	 * @throws CategoryNotFoundException
	 * @throws IntegrityException
	 * @throws ConnectionException
	 */
	public static void moveCategory(long category_id, Long parentCategory_id)
			throws ConnectionException, IntegrityException,
			CategoryNotFoundException {
		LogicManager.getInstance().moveCategory(
				LogicManager.getInstance().searchCategoryById(category_id),
				LogicManager.getInstance()
						.searchCategoryById(parentCategory_id));
	}

	/**
	 * Moves an exhibit to another outsourced.
	 * 
	 * @author Marco
	 * 
	 * @param exhibit_id
	 * @param outsourced_id
	 * @throws OutsourcedNotFoundException
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 */
	public static void moveExhibitToOutsourced(long exhibit_id,
			long outsourced_id) throws ConnectionException,
			ExhibitNotFoundException, OutsourcedNotFoundException {
		LogicManager.getInstance().moveExhibitToOutsourced(
				LogicManager.getInstance().searchExhibitById(exhibit_id),
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Moves exhibit to another category.
	 * 
	 * @author Marco
	 * 
	 * @param exhibit_id
	 * @param category_id
	 * @throws CategoryNotFoundException
	 * @throws ExhibitNotFoundException
	 * @throws ConnectionException
	 */
	public static void moveExhibitToCategory(long exhibit_id, long category_id)
			throws ConnectionException, ExhibitNotFoundException,
			CategoryNotFoundException {
		LogicManager.getInstance().moveToCategory(
				LogicManager.getInstance().searchExhibitById(exhibit_id),
				LogicManager.getInstance().searchCategoryById(category_id));
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ GET_ALL -------------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Gets all Museums.
	 * 
	 * @author Benedikt
	 * 
	 * @return all museums
	 */
	public static ArrayList<Museum> getAllMuseums() {
		return LogicManager.getAllMuseums();
	}

	/**
	 * Get all sections by a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @return all subsections
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Section> getAllSubSectionsFromMuseum(long museum_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllSectionsByMuseum(LogicManager.getInstance()
				.searchMuseumById(museum_id));

	}

	/**
	 * Gets all exhibits which are directly contained by a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @return all exhibits which are directly in the museum
	 */
	public static ArrayList<Exhibit> getAllExhibitsByMuseumSectionIsNull(
			long museum_id) {
		return LogicManager.getAllExhibitsByMuseumSectionIsNull(museum_id);
	}

	/**
	 * Gets all subsections by a section.
	 * 
	 * @author Benedikt
	 * 
	 * @return all subsections from a section
	 * @throws SectionNotFoundException
	 */
	public static ArrayList<Section> getAllSubSectionsFromSection(
			long section_id) throws SectionNotFoundException {
		return LogicManager.getAllSectionsBySection(LogicManager.getInstance()
				.searchSectionById(section_id));

	}

	/**
	 * Gets all exhibits from a section.
	 * 
	 * @author Benedikt
	 * 
	 * @return all exhibits by a section
	 * @throws SectionNotFoundException
	 */
	public static ArrayList<Exhibit> getAllExhibitsBySection(long section_id)
			throws SectionNotFoundException {
		return LogicManager.getAllExhibitsBySection(LogicManager.getInstance()
				.searchSectionById(section_id));

	}

	/**
	 * Gets all exhibits from a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @return all exhibits by a museum
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Exhibit> getAllExhibitsByMuseum(long section_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllExhibitsByMuseum(LogicManager.getInstance()
				.searchMuseumById(section_id));

	}

	/**
	 * Gets all exhibits from a category.
	 * 
	 * @author Benedikt
	 * 
	 * @param category_id
	 * @return all exhibits by category
	 */
	public static ArrayList<Exhibit> getAllExhibitsByCategory(long category_id) {
		return LogicManager.getAllExhibitsByCategory(category_id);
	}

	/**
	 * Gets all exhibits from an outsourced.
	 * 
	 * @author Benedikt
	 * 
	 * @param outsourced_id
	 * @return all ehibits by outsourced
	 */
	public static ArrayList<Exhibit> getAllExhibitsByOutsourced(
			long outsourced_id) {
		return LogicManager.getAllExhibitsByOutsourced(outsourced_id);
	}

	/**
	 * Gets all outsourced by a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @return all outsourced
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Outsourced> getAllOutsourced(long museum_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllOutsourced(LogicManager.getInstance()
				.searchMuseumById(museum_id));
	}

	/**
	 * Gets all exhibitions.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * 
	 * @return all exhibitions
	 */
	public static ArrayList<Outsourced> getAllExhibitions() {
		return LogicManager.getAllExhibitions();
	}

	/**
	 * Gets all loans.
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * 
	 * @return all loans
	 */
	public static ArrayList<Outsourced> getAllLoans() {
		return LogicManager.getAllLoans();
	}

	/**
	 * Gets all exhibitions from a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @return all exhibitions by a museum
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Outsourced> getAllExhibitionsByMuseum(long museum_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllExhibitions(LogicManager.getInstance()
				.searchMuseumById(museum_id));
	}

	/**
	 * Gets all loans from a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @return all loans from a museum
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Outsourced> getAllLoansByMuseum(long museum_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllLoans(LogicManager.getInstance()
				.searchMuseumById(museum_id));
	}

	/**
	 * Gets all categories by a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @param museum_id
	 * @return all direct child categories from a museum
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Category> getAllCategoriesByMuseum(long museum_id)
			throws MuseumNotFoundException {
		return LogicManager.getAllCategoriesByMuseum(LogicManager.getInstance()
				.searchMuseumById(museum_id));
	}

	/**
	 * Gets all categories by a category.
	 * 
	 * @author Benedikt
	 * 
	 * @param category_id
	 * @return all direct child categories from a category
	 * @throws CategoryNotFoundException
	 */
	public static ArrayList<Category> getAllCategoriesByCategory(
			long category_id) throws CategoryNotFoundException {
		return LogicManager.getAllCategoriesByCategory(LogicManager
				.getInstance().searchCategoryById(category_id));
	}

	/**
	 * Get all address.
	 * 
	 * @author Benedikt
	 * 
	 * @return all address
	 */
	public static ArrayList<Address> getAllAddress() {
		return LogicManager.getAllAddress();
	}

	/**
	 * Gets all contact.
	 * 
	 * @author Benedikt
	 * 
	 * @return all contact
	 */
	public static ArrayList<Contact> getAllContact() {
		return LogicManager.getAllContact();
	}

	/**
	 * Gets all roles.
	 * 
	 * @author Benedikt
	 * 
	 * @return list of Roles
	 */
	public static ArrayList<Role> getAllRole() {
		return LogicManager.getAllRole();
	}

	/**
	 * Gets all labels.
	 * 
	 * @author Marco
	 * 
	 * @return list of all Labels
	 */
	public static ArrayList<Label> getAllLabels() {
		return LogicManager.getAllLabels();
	}

	/**
	 * Gets all labels by an exhibit Id.
	 * 
	 * @author Marco
	 * 
	 * @param exhibitId
	 * @return list of all labels by an exhibit Id
	 */
	public static ArrayList<Label> getAllLabelsByExhibitId(long exhibitId) {
		return LogicManager.getAllLabelsByExhibitId(exhibitId);
	}

	/**
	 * Gets all history.
	 * 
	 * @author Marco
	 * 
	 * @return list of all History
	 */
	public static ArrayList<History> getAllHistory() {
		return LogicManager.getAllHistory();
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ DELETE --------------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Removes an exhibit form an outsourced.
	 * 
	 * @author Benedikt
	 * 
	 * @param exhibit_id
	 * @return outsourced_id
	 * @throws ConnectionException
	 */
	public static void removeFromOutsourced(long exhibit_id) throws ConnectionException 
        {
            for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
            {
                if (outsourced.getExhibitIds().containsKey(exhibit_id))
                {
                    LogicManager.getInstance().removeFromOutsourced(exhibit_id, outsourced);
                }
            }
	}

	/**
	 * Deletes a museum.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteMuseum(long id)
			throws ModelAlreadyDeletedException, ConnectionException {
		LogicManager.getInstance().deleteMuseum(id);
	}

	/**
	 * Deletes a section.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteSection(long id)
			throws ModelAlreadyDeletedException, ConnectionException {
		LogicManager.getInstance().deleteSection(id);
	}

	/**
	 * Deletes an exhibit.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteExhibit(long id)
			throws ModelAlreadyDeletedException, ConnectionException {
		LogicManager.getInstance().deleteExhibit(id);
	}

	/**
	 * Deletes an exhibition.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 * @throws IntegrityException
	 */
	public static void deleteExhibition(long id)
			throws ModelAlreadyDeletedException, ConnectionException,
			IntegrityException {
		LogicManager.getInstance().deleteOutsourced(id);
	}

	/**
	 * Deletes a category.
	 * 
	 * @throws ModelAlreadyDeletedException
	 * @throws ConnectionException
	 * @throws InvalidArgumentsException
	 * @throws CategoryNotFoundException
	 */
	public static void deleteCategory(long id) throws ConnectionException,
			ModelAlreadyDeletedException, InvalidArgumentsException,
			CategoryNotFoundException {
		LogicManager.getInstance().deleteCategory(id);
	}

	/**
	 * Deletes an Address.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 * @throws IntegrityException
	 */
	public static void deleteAddress(long id)
			throws ModelAlreadyDeletedException, ConnectionException,
			IntegrityException {
		LogicManager.getInstance().deleteAddress(id);
	}

	/**
	 * Deletes a contact.
	 * 
	 * @throws ContactNotFoundException
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteContact(long id)
			throws ModelAlreadyDeletedException, ConnectionException,
			ContactNotFoundException, IntegrityException {
		LogicManager.getInstance().deleteContactDetail(id);
	}

	/**
	 * Deletes a label.
	 * 
	 * @author Marco
	 * 
	 * @param id
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void deleteLabel(long id)
			throws ModelAlreadyDeletedException, ConnectionException {
		LogicManager.getInstance().deleteLabel(id);
	}

	/**
	 * Deletes a role.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @throws ModelAlreadyDeletedException
	 * @throws ConnectionException
	 * @throws IntegrityException
	 */
	public static void deleteRole(long id) throws ModelAlreadyDeletedException,
			ConnectionException, IntegrityException {
		LogicManager.getInstance().deleteRole(id);
	}

	/**
	 * Removes an exhibit from a label.
	 * 
	 * @author Marco
	 * 
	 * @param exhibitId
	 * @param label
	 * @throws ConnectionException
	 */
	public static void removeFromLabel(Long exhibitId, Label label)
			throws ConnectionException {
		LogicManager.getInstance().removeFromLabel(exhibitId, label);
	}

	/**
	 * Deletes a picture.
	 * 
	 * @author Marco
	 * 
	 * @param id
	 * @throws ModelAlreadyDeletedException
	 * @throws ConnectionException
	 * @throws Exception
	 */
	public static void deletePicture(long id)
			throws ModelAlreadyDeletedException, ConnectionException, Exception {
		LogicManager.getInstance().deletePicture(id);
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ MASS CHANGE ---------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Removes all exhibits from an outsourced.
	 * 
	 * @author Benedikt
	 * 
	 * @param exhibit_ids
	 * @throws ConnectionException
	 */
	public static void massRemoveFromOutosurced(ArrayList<Long> exhibit_ids)
			throws ConnectionException {
		LogicManager.getInstance().massRemoveFromOutsourced(exhibit_ids);
	}

	/**
	 * Deletes exhibits.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param dlist
	 * @throws ConnectionException
	 * @throws ModelAlreadyDeletedException
	 */
	public static void massExhibitDelete(ArrayList<Long> dlist)
			throws ModelAlreadyDeletedException, ConnectionException {
		LogicManager.getInstance().massExhibitDelete(dlist);
	}

	/**
	 * Changes section of the exhibits.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param list
	 */
	public static void massExhibitSectionChange(ArrayList<Long> list,
			Long sectionID) {
		LogicManager.getInstance().massExhibitSectionChange(list, sectionID);
	}

	/**
	 * Changes category of the exhibits.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param list
	 */
	public static void massExhibitCategoryChange(ArrayList<Long> list,
			Long categoryID) {
		LogicManager.getInstance().massExhibitCategoryChange(list, categoryID);
	}

	/**
	 * Changes the section from exhibits.
	 * 
	 * @author Benedikt
	 * 
	 * @param toChange
	 * @param section_id
	 * @param museum_id
	 * @throws MuseumNotFoundException
	 * @throws ConnectionException
	 * @throws IntegrityException
	 * @throws SectionNotFoundException
	 */
	public static void massChangeSection(ArrayList<Exhibit> toChange,
			Long section_id, long museum_id) throws IntegrityException,
			ConnectionException, MuseumNotFoundException,
			SectionNotFoundException {
		// section_id==null if target is museum
		if (section_id == null || section_id.equals(0L)) {
			LogicManager.getInstance().massSectionChange(toChange,
					LogicManager.getInstance().searchMuseumById(museum_id));
		} else {
			LogicManager.getInstance().massSectionChange(toChange,
					LogicManager.getInstance().searchSectionById(section_id));
		}
	}

	/**
	 * Changes the category of all elements from toChange.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param toChange
	 * @param category_id
	 * @throws CategoryNotFoundException
	 * @throws ConnectionException
	 */
	public static void massChangeCategory(ArrayList<Exhibit> toChange,
			long category_id) throws ConnectionException,
			CategoryNotFoundException {
		LogicManager.getInstance().massChangeCategory(toChange,
				LogicManager.getInstance().searchCategoryById(category_id));
	}

	/**
	 * Changes the exhibition of all elements from toChange.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param toChange
	 * @param outsourced_id
	 * @throws OutsourcedNotFoundException
	 * @throws ConnectionException
	 */
	public static void massChangeExhibition(ArrayList<Exhibit> toChange,
			long outsourced_id) throws ConnectionException,
			OutsourcedNotFoundException {
		LogicManager.getInstance().massAddOutsourced(toChange,
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/**
	 * Changes the loan of all toChange elements.
	 * 
	 * @author Ralf Heukäufer
	 * 
	 * @param toChange
	 * @param outsourced_id
	 * @throws OutsourcedNotFoundException
	 * @throws ConnectionException
	 */
	public static void massChangeLoan(ArrayList<Exhibit> toChange,
			long outsourced_id) throws ConnectionException,
			OutsourcedNotFoundException {
		LogicManager.getInstance().massAddOutsourced(toChange,
				LogicManager.getInstance().searchOutsourcedById(outsourced_id));
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ SEARCH --------------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Get outsourced by id.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @return Outsourced
	 * @throws OutsourcedNotFoundException
	 */
	public static Outsourced getOutsourcedByID(Long id)
			throws OutsourcedNotFoundException {
		return LogicManager.getInstance().searchOutsourcedById(id);
	}

	/**
	 * Search museum by name.
	 * 
	 * @author Benedikt
	 */
	public static ArrayList<Museum> searchMuseumName(String name) {
		return LogicManager.getInstance().searchMuseumByName(name);

	}

	/**
	 * Search museum by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws MuseumNotFoundException
	 */
	public static Museum searchMuseumID(long id) throws MuseumNotFoundException {
		return LogicManager.getInstance().searchMuseumById(id);

	}

	/**
	 * Search museum by address id.
	 * 
	 * @author Marco
	 * 
	 * @param addressId
	 * @return
	 */
	public static ArrayList<Museum> searchMuseumByAddressId(long addressId) {
		return LogicManager.getInstance().searchMuseumByAddressId(addressId);
	}

	/**
	 * Get miscellaneous catgeory by museum id
	 * 
	 * @author Benedikt.
	 * 
	 * @param museum_id
	 * @return Miscellaneous Category
	 * @throws CategoryNotFoundException
	 * @throws MuseumNotFoundException
	 */
	public static Category getMiscellaneousCategory(long museum_id)
			throws CategoryNotFoundException, MuseumNotFoundException {
		return LogicManager.getInstance().getMiscellaneousCategory(
				LogicManager.getInstance().searchMuseumById(museum_id));
	}

	/**
	 * Search section by name.
	 * 
	 * @author Benedikt
	 */
	public static ArrayList<Section> searchSectionName(String name,
			Museum museum) {
		return LogicManager.getInstance().searchSectionByName(name, museum);

	}

	/**
	 * Search section by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws SectionNotFoundException
	 */
	public static Section searchSectionID(long id)
			throws SectionNotFoundException {
		return LogicManager.getInstance().searchSectionById(id);
	}

	/**
	 * Search subSection by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws SectionNotFoundException
	 */
	public static ArrayList<Section> searchSubSectionID(long parent_id)
			throws SectionNotFoundException {
		return LogicManager.getAllSectionsBySection(LogicManager.getInstance()
				.searchSectionById(parent_id));
	}

	/**
	 * Search exhibit by name.
	 * 
	 * @author Benedikt
	 * 
	 * @deprecated use specialSearch instead with value normalsearch=true
	 */
	public static ArrayList<Exhibit> searchExhibitName(String name,
			long museum_id) {
		return LogicManager.getInstance().searchExhibitByName(name, museum_id);
	}

	/**
	 * Search exhibits by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ExhibitNotFoundException
	 */
	public static Exhibit searchExhibitID(long id)
			throws ExhibitNotFoundException {
		return LogicManager.getInstance().searchExhibitById(id);
	}

	/**
	 * Search exhibits by label and museum.
	 * 
	 * @author Marco
	 * 
	 * @param labelId
	 * @return
	 * @throws LabelNotFoundException
	 * @throws ExhibitNotFoundException
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Exhibit> searchExhibitsByLabelAndMuseum(
			long labelId, long museum_id) throws LabelNotFoundException,
			ExhibitNotFoundException, MuseumNotFoundException {
		return LogicManager.getInstance().searchExhibitsByLabelAndMuseum(
				LogicManager.getInstance().searchLabelById(labelId),
				LogicManager.getInstance().searchMuseumById(museum_id));
	}

	/**
	 * Search exhibition by name.
	 * 
	 * @author Benedikt
	 * 
	 * @throws ExhibitionNotFoundException
	 */
	public static ArrayList<Outsourced> searchOutsourcedByName(String name) {
		return LogicManager.getInstance().searchOutsourcedByName(name);
	}

	/**
	 * Search exhibition by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws OutsourcedNotFoundException
	 */
	public static Outsourced searchExhibitonID(long id)
			throws OutsourcedNotFoundException {
		return LogicManager.getInstance().searchOutsourcedById(id);
	}

	/**
	 * Search category by name.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @return
	 */
	public static ArrayList<Category> searchCategoryByName(String name) {
		return LogicManager.getInstance().searchCategoryByName(name);
	}

	/**
	 * Search category by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws CategoryNotFoundException
	 */
	public static Category searchCategoryID(long id)
			throws CategoryNotFoundException {
		return LogicManager.getInstance().searchCategoryById(id);

	}

	/**
	 * Search loan by id.
	 * 
	 * @author Benedikt
	 * 
	 * @throws OutsourcedNotFoundException
	 */
	public static Outsourced searchLoanID(long id)
			throws OutsourcedNotFoundException {
		return LogicManager.getInstance().searchOutsourcedById(id);
	}

	/**
	 * Search address by id.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @return
	 * @throws AddressNotFoundException
	 */
	public static Address searchAddressID(long id)
			throws AddressNotFoundException {
		return LogicManager.getInstance().searchAddressById(id);
	}

	/**
	 * SearchAddressByMuseumName
	 * 
	 * @author FShikowski
	 * 
	 * @param museumName
	 * @return
	 */
	public static ArrayList<Address> searchAddressByMuseumName(String museumName) {
		return LogicManager.getInstance().searchAddressByMuseumName(museumName);
	}

	/**
	 * SearchRoleByMuseumName
	 * 
	 * @author FSchikowski
	 * 
	 * @param museumName
	 * @return
	 */
	public static ArrayList<Role> searchRoleByMuseumId(long id) {
		return LogicManager.getInstance().getAllRolesByMuseumId(id);
	}

	/**
	 * Search contact by id.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @return
	 * @throws ContactDetailNotFoundException
	 */
	public static Contact searchContactID(long id)
			throws ContactNotFoundException {
		return LogicManager.getInstance().searchContactDetailById(id);
	}

	/**
	 * Search contact by name and forename.
	 * 
	 * @author Benedikt
	 * 
	 * @param name
	 * @param forename
	 * @return
	 */
	public static ArrayList<Contact> searchContactName(String name,
			String forename) {
		return LogicManager.getInstance().searchContactDetailByName(name,
				forename);
	}

	/**
	 * Search contact by address id.
	 * 
	 * @author Marco
	 * 
	 * @param addressId
	 * @return
	 */
	public static ArrayList<Contact> searchContactByAddressId(long addressId) {
		return LogicManager.getInstance().searchContactByAddressId(addressId);
	}

	/**
	 * Search contact by museum id.
	 * 
	 * @author Benedikt
	 * @author Jochen
	 * 
	 * @param museumId
	 * @return
	 */
	public static ArrayList<Contact> searchContactByMuseumId(long museumId) {
		ArrayList<Contact> temp = LogicManager.getInstance()
				.searchContactByMuseumId(museumId);
		ArrayList<Contact> finallist = new ArrayList<Contact>();
		for (Contact c : temp)
			if (c.isDeleted() == false)
				finallist.add(c);
		return finallist;
	}

	/**
	 * Search image by exhibit id.
	 * 
	 * @author Benedikt
	 * 
	 * @param exhibit_id
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<Image> searchPictureByExhibitId(long exhibit_id)
			throws Exception {
		return LogicManager.getInstance().searchPictureByExhibitId(exhibit_id);
	}

	/**
	 * Search role by role_id.
	 * 
	 * @author Benedikt
	 * 
	 * @param role_id
	 * @return
	 */
	public static Role searchRoleId(long role_id) {
		return LogicManager.getInstance().searchRoleById(role_id);
	}

	/**
	 * Search ricture by id.
	 * 
	 * @author Benedikt
	 * 
	 * @param id
	 * @return Image
	 * @throws ConnectionException
	 * @throws PictureNotFoundException
	 */
	public Image searchPictureById(Long id) throws ConnectionException,
			PictureNotFoundException {
		return LogicManager.getInstance().searchPictureById(id);
	}

	/**
	 * Search label by id.
	 * 
	 * @author Marco
	 * 
	 * @param labelId
	 * @return
	 * @throws LabelNotFoundException
	 */
	public static Label searchLabelById(long labelId)
			throws LabelNotFoundException {
		return LogicManager.getInstance().searchLabelById(labelId);
	}

	/**
	 * Search label by name.
	 * 
	 * @author Marco
	 * 
	 * @param labelName
	 * @return
	 * @throws LabelNotFoundException
	 */
	public static ArrayList<Label> searchLabelByName(String labelName)
			throws LabelNotFoundException {
		return LogicManager.getInstance().searchLabelByName(labelName);
	}

	/**
	 * Search history by id.
	 * 
	 * @author Marco
	 * 
	 * @param historyId
	 * @return
	 * @throws HistoryElementNotFoundException
	 */
	public static History searchHistoryElementById(Long historyId)
			throws HistoryElementNotFoundException {
		return LogicManager.getInstance().searchHistoryElementById(historyId);
	}

	/**
	 * Search history by name.
	 * 
	 * @author Marco
	 * 
	 * @param historyName
	 * @return
	 * @throws HistoryElementNotFoundException
	 */
	public static ArrayList<History> searchHistoryElementByName(
			String historyName) throws HistoryElementNotFoundException {
		return LogicManager.getInstance().searchHistoryElementByName(
				historyName);
	}

	/**
	 * Search history by exhibit id.
	 * 
	 * @author Marco
	 * 
	 * @param exhibitId
	 * @return list of histories sorted by date
	 * @throws HistoryElementNotFoundException
	 */
	public static ArrayList<History> searchHistoryElementsByExhibitId(
			long exhibitId) throws HistoryElementNotFoundException {
		return LogicManager.getInstance().searchHistoryElementsByExhibitId(
				exhibitId);
	}

	/**
	 * 
	 * Special search. Searches exhibits with special input.
	 * 
	 * @author Ralf
	 * 
	 * @param MuseumID
	 * @param proCategory
	 * @param contraCategory
	 * @param proSection
	 * @param contraSection
	 * @param proLabel
	 * @param contraLabel
	 * @param proOutsourced
	 * @param contraOutsourced
	 * @return List of Exhibits
	 * @throws MuseumNotFoundException
	 */
	public static ArrayList<Exhibit> specialSearch(long MuseumID,
			String Exhibitname, ArrayList<Long> proCategory,
			ArrayList<Long> contraCategory, ArrayList<Long> proSection,
			ArrayList<Long> contraSection, ArrayList<Long> proLabel,
			ArrayList<Long> contraLabel, ArrayList<Long> proOutsourced,
			ArrayList<Long> contraOutsourced, boolean normalsearch)
			throws MuseumNotFoundException {
		return LogicManager.getInstance().specialSearch(MuseumID, Exhibitname,
				proCategory, contraCategory, proSection, contraSection,
				proLabel, contraLabel, proOutsourced, contraOutsourced,
				normalsearch);
	}

	/*
	 * ----------------------------------------------------------------------
	 * ------------------------------ SORT EXHIBITS--------------------------
	 * ----------------------------------------------------------------------
	 */

	/**
	 * Sorts list oh exhibits by name, by section, by category or by state
	 * 
	 * @author Jochen Saßmannshausen
	 * @author Caroline Bender
	 * 
	 * @param exhibits
	 *            list of exhibits to sort
	 * @return sorted ArrayList
	 */
	public static ArrayList<Exhibit> sortExhibitsByName(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortExhibitsByName(exhibits);
	}

	public static ArrayList<Exhibit> sortExhibitsBySection(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortExhibitsBySection(exhibits);
	}

	public static ArrayList<Exhibit> sortExhibitsByCategory(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortExhibitsByCategory(exhibits);
	}

	public static ArrayList<Exhibit> sortExhibitsByState(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortExhibitsByState(exhibits);
	}

	public static ArrayList<Exhibit> sortReverseExhibitsByName(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortReverseExhibitsByName(exhibits);
	}

	public static ArrayList<Exhibit> sortReverseExhibitsBySection(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance()
				.sortReverseExhibitsBySection(exhibits);
	}

	public static ArrayList<Exhibit> sortReverseExhibitsByCategory(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortReverseExhibitsByCategory(
				exhibits);
	}

	public static ArrayList<Exhibit> sortReverseExhibitsByState(
			ArrayList<Exhibit> exhibits) {
		return LogicManager.getInstance().sortReverseExhibitsByState(exhibits);
	}

	public static ArrayList<SyncModel> sortSyncModel(ArrayList<SyncModel> models) {
		return LogicManager.getInstance().sortSyncModel(models);
	}

	public static ArrayList<Pair<SyncModel, SyncModel>> sortSyncPair(
			ArrayList<Pair<SyncModel, SyncModel>> models) {
		return LogicManager.getInstance().sortSyncPair(models);
	}

	/**
	 * @author FSchikowski
	 * @param label
	 * @return
	 */
	public static ArrayList<Label> sortLabelsByName(ArrayList<Label> label) {
		return LogicManager.getInstance().sortLabelsByName(label);
	}

}
