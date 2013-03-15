package de.museum.berleburg.userInterface.panels;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Address;
import de.museum.berleburg.datastorage.model.Category;
import de.museum.berleburg.datastorage.model.Contact;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Image;
import de.museum.berleburg.datastorage.model.Label;
import de.museum.berleburg.datastorage.model.Museum;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.datastorage.model.Section;
import de.museum.berleburg.logic.OutsourcedLogic;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.dialogs.EditCategory;
import de.museum.berleburg.userInterface.dialogs.EditContact;
import de.museum.berleburg.userInterface.dialogs.EditExhibit;
import de.museum.berleburg.userInterface.dialogs.EditExhibition;
import de.museum.berleburg.userInterface.dialogs.EditLoan;
import de.museum.berleburg.userInterface.dialogs.EditMuseum;
import de.museum.berleburg.userInterface.dialogs.EditSection;
import de.museum.berleburg.userInterface.listeners.ExhibitListener;

/**
 * shows the details of a model (either exhibit, museum, section, category or
 * outsourced) as an HTML text
 *
 * @author Christian Landel
 */
@SuppressWarnings("serial")
public class DetailPanel extends JPanel
{

    /**
     * the text that will be displayed when no item is selected
     */
    private static final String defaultText = "<div>(Klicken Sie auf ein Element, um hier eine Zusammenfassung anzuzeigen)</div>";
    /**
     * characters that can are displayed in html without modification
     */
    private static final char[] htmlExtraChars =
    {
        'ä', 'Ä', 'ö', 'Ö', 'ü',
        'Ü', '^', 'ß', '!', '\"', '§', '$', '/', '(', ')', '=', '?', '°',
        '{', '[', ']', '}', '\\', '@', '€', '+', '*', '~', '\'', '#', '|',
        ',', '.', '-', ';', ':', '_', ' '
    };
    /**
     * the pane that contains the html code and the pictures
     */
    private JTextPane pane;
    private JScrollPane scrollPane;
    /**
     * @author Jochen Saßmannshausen
     */
    Object lastDisplayed = null;
    LinkHistory history = new LinkHistory();
    private JButton btnBack;
    private JButton btnForth;

    /**
     * common operations for all constructors
     */
    private void init()
    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        scrollPane = new JScrollPane();
        scrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);
        pane = new JTextPane();
        scrollPane.setViewportView(pane);
        pane.setContentType("text/html");
        pane.setEditable(false);
        pane.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    openLink(event.getDescription(), event.getURL());
                }
            }
        });
        JPanel panelHistory = new JPanel();
        panelHistory.setLayout(new BoxLayout(panelHistory, BoxLayout.Y_AXIS));
        btnBack = new JButton("<");
        btnBack.setToolTipText("Wenn Sie in diesem Abschnitt mehrere Elemente hintereinander"
                + " zur Anzeige ausgewählt haben, können sie mit diesem Knopf zum vorher gewählten"
                + " Element zurückgelangen.");
        btnBack.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                goBack();
            }
        });
        btnForth = new JButton(">");
        btnForth.setToolTipText("Wenn Sie mit dem obigen Knopf (\"<\") zu einem vorher"
                + " gewählten Element gewechselt haben, können Sie mit diesem Knopf"
                + " wieder zum anschließend gewählten Element zurückgelangen.");
        btnForth.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                goForth();
            }
        });
        panelHistory.add(btnBack);
        panelHistory.add(btnForth);
        updateButtons();
        add(panelHistory);
    }

    /**
     * go back in the history of displayed items, if possible
     */
    private void goBack()
    {
        openLink(history.goBack(), null, false);
        updateButtons();
    }

    /**
     * go forth again in the history of displayed items, if goBack() was used
     * previously
     */
    private void goForth()
    {
        openLink(history.goForth(), null, false);
        updateButtons();
    }

    private void updateButtons()
    {
        btnBack.setEnabled(history.hasPrev());
        btnForth.setEnabled(history.hasNext());
    }

    /**
     * open a link (can be internal, like "exh/5", or external, like
     * "http://example.com" or "mailto:test@example.com")
     *
     * @param link a string that will be parsed
     * @param url optional - will be parsed from
     * @param link if null
     */
    public void openLink(String link, URL url)
    {
        openLink(link, url, true);
    }

    /**
     * open a link (can be internal, like "exh/5", or external, like
     * "http://example.com" or "mailto:test@example.com")
     *
     * @param link a string that will be parsed
     * @param url optional - will be parsed from
     * @param link if null
     * @param recordHistory usually true, except if going through history itself
     */
    private void openLink(String link, URL url, final boolean recordHistory)
    {
        if (link == null)
        {
            link = "";
        }
        if (link.isEmpty() && url == null)
        {
            return;
        }
        try
        {
            // parse a link in the form of type/id
            String type = "";
            long id = 0L;
            int slashIndex = 0; // the index in 'link' where '/' appears
            for (int i = 0; i < link.length(); i++)
            {
                if (link.charAt(i) == '/')
                {
                    slashIndex = i;
                    break;
                }
            }
            try
            {
                // yes, this extracts the left part /without/ the slash
                type = link.substring(0, slashIndex);
                // if the link has the form type/id
                // then the part after the type (add 1 char for the slash)
                // will be parsed as the id
                id = Long.parseLong(link.substring(slashIndex + 1));
            } // whatever went wrong, just make it so the uri
            // will be opened in a browser (see below)
            catch (Exception e)
            {
                type = "";
                id = 0;
            }
            /**
             * dialogs for editing are not allowed if they are from a different
             * museum than is currently opened
             */
            boolean editingAllowed = true;
            if (lastDisplayed instanceof Exhibit)
            {
                if (((Exhibit) lastDisplayed).getMuseum_id() != MuseumMainPanel
                        .getInstance().getMuseumId())
                {
                    editingAllowed = false;
                }
            }
            if (lastDisplayed instanceof Museum)
            {
                if (((Museum) lastDisplayed).getId() != MuseumMainPanel
                        .getInstance().getMuseumId())
                {
                    editingAllowed = false;
                }
            }
            if (lastDisplayed instanceof Section)
            {
                if (((Section) lastDisplayed).getMuseum_id() != MuseumMainPanel
                        .getInstance().getMuseumId())
                {
                    editingAllowed = false;
                }
            }
            if (lastDisplayed instanceof Category)
            {
                if (((Category) lastDisplayed).getMuseum_id() != MuseumMainPanel
                        .getInstance().getMuseumId())
                {
                    editingAllowed = false;
                }
            }
            // if (lastDisplayed instanceof Contact)
            // if ( ((Contact)lastDisplayed).getMuseum_id()
            // !=MuseumMainPanel.getInstance().getMuseumId() )
            // editingAllowed=false;
            if (lastDisplayed instanceof Outsourced)
            {
                if (((Outsourced) lastDisplayed).getMuseum_id() != MuseumMainPanel
                        .getInstance().getMuseumId())
                {
                    editingAllowed = false;
                }
            }
            /**
             * if an error message for museum misuse should be shown
             */
            boolean showWrongMuseum = false;
            // start a long if...type...else chain
            // edit types open edit dialogs
            if (type.equals("EditExh"))
            {
                if (editingAllowed)
                {
                    EditExhibit ee = new EditExhibit(Access.searchExhibitID(id));
                    ee.addListener(new ExhibitListener()
                    {
                        public void event(Collection<Exhibit> exhibits)
                        {
                            for (Exhibit e : exhibits)
                            {
                                setDetails(e);
                            }
                        }
                    });
                    ee.enableEditing(false);
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            else if (type.equals("EditMus"))
            {
                if (editingAllowed)
                {
                    EditMuseum em = new EditMuseum(id);
                    em.setVisible(true);
                    setDetails(Access.searchMuseumID(id));
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            else if (type.equals("EditSec"))
            {
                if (editingAllowed)
                {
                    EditSection es = new EditSection(id);
                    es.setVisible(true);
                    setDetails(Access.searchSectionID(id));
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            else if (type.equals("EditCat"))
            {
                if (editingAllowed)
                {
                    EditCategory ec = new EditCategory(id);
                    ec.setVisible(true);
                    setDetails(Access.searchCategoryID(id));
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            else if (type.equals("EditOut"))
            {
                if (editingAllowed)
                {
                    Outsourced out = Access.getOutsourcedByID(id);
                    if (out.getContact_id() == null
                            || out.getContact_id() == 0L)
                    {
                        EditExhibition ee = new EditExhibition(id);
                        ee.setVisible(true);
                    }
                    else
                    {
                        EditLoan el = new EditLoan(id);
                        el.setVisible(true);
                    }
                    setDetails(Access.getOutsourcedByID(id));
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            else if (type.equals("EditCon"))
            {
                if (editingAllowed)
                {
                    EditContact ec = new EditContact(id, true, null, true);
                    ec.setVisible(true);
                }
                else
                {
                    showWrongMuseum = true;
                }
            }
            // "link" types copy links to the clipboard so the user can retrieve
            // them
            else if (type.equals("LinkExh"))
            {
                String name = Access.searchExhibitID(id).getName();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                        new StringSelection("<a href=exh/" + id + ">"
                        + DetailPanel.textToHTML(name) + "</a>"),
                        null);
                MainGUI.getInformationPanel()
                        .setText(
                        "Den Link-Code für das Exponat \""
                        + name
                        + "\" mit Strg+V in Beschreibungstexte einfügen.");
            }
            else if (type.equals("LinkMus"))
            {
                String name = Access.searchMuseumID(id).getName();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                        new StringSelection("<a href=mus/" + id + ">"
                        + DetailPanel.textToHTML(name) + "</a>"),
                        null);
                MainGUI.getInformationPanel()
                        .setText(
                        "Den Link-Code für das Museum \""
                        + name
                        + "\" mit Strg+V in Beschreibungstexte einfügen.");
            }
            else if (type.equals("LinkSec"))
            {
                String name = Access.searchSectionID(id).getName();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                        new StringSelection("<a href=sec/" + id + ">"
                        + DetailPanel.textToHTML(name) + "</a>"),
                        null);
                MainGUI.getInformationPanel()
                        .setText(
                        "Den Link-Code für die Sektion \""
                        + name
                        + "\" mit Strg+V in Beschreibungstexte einfügen.");
            }
            else if (type.equals("LinkCat"))
            {
                String name = Access.searchSectionID(id).getName();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                        new StringSelection("<a href=cat/" + id + ">"
                        + DetailPanel.textToHTML(name) + "</a>"),
                        null);
                MainGUI.getInformationPanel()
                        .setText(
                        "Den Link-Code für die Kategorie \""
                        + name
                        + "\" mit Strg+V in Beschreibungstexte einfügen.");
            }
            else if (type.equals("LinkOut"))
            {
                String name = Access.getOutsourcedByID(id).getName();
                Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(
                        new StringSelection("<a href=out/" + id + ">"
                        + DetailPanel.textToHTML(name) + "</a>"),
                        null);
                MainGUI.getInformationPanel()
                        .setText(
                        "Den Link-Code für \""
                        + name
                        + "\" mit Strg+V in Beschreibungstexte einfügen.");
            }
            // these types open the supported models in this panel
            // visited pages are recorded if the user clicks on links
            else if (type.toUpperCase().equals("EXH"))
            {
                setDetails(Access.searchExhibitID(id), recordHistory);
            }
            else if (type.toUpperCase().equals("OUT"))
            {
                setDetails(Access.getOutsourcedByID(id), recordHistory);
            }
            else if (type.toUpperCase().equals("CAT"))
            {
                setDetails(Access.searchCategoryID(id), recordHistory);
            }
            else if (type.toUpperCase().equals("SEC"))
            {
                setDetails(Access.searchSectionID(id), recordHistory);
            }
            else if (type.toUpperCase().equals("MUS"))
            {
                setDetails(Access.searchMuseumID(id), recordHistory);
            }
            else if (type.toUpperCase().equals("CON"))
            {
                setDetails(Access.searchContactID(id), recordHistory);
            }
            // unknown type => most probably http, just delegate it to some
            // browser
            else
            {
                if (url == null)
                {
                    url = new URL(link);
                }
                Desktop.getDesktop().browse(url.toURI());
            } // end of if...type...else chain

            if (showWrongMuseum)
            {
                JOptionPane
                        .showMessageDialog(
                        this,
                        "Kann kein Element eines nicht gewählten Museums bearbeiten.",
                        "Abbruch", JOptionPane.WARNING_MESSAGE);
            }
        }
        catch (Exception ex)
        {
            if (!(ex instanceof URISyntaxException))
            {
                ex.printStackTrace();
            }
            if (ex instanceof NullPointerException)
            {
                JOptionPane.showMessageDialog(null, "Null Pointer", "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "HTML-Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * scroll back to the top, so the user will see the details instead of parts
     * of the images
     */
    private void scrollTop()
    {
        javax.swing.SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                scrollPane.getHorizontalScrollBar().setValue(0);
                scrollPane.getVerticalScrollBar().setValue(0);
            }
        });
    }

    /**
     * create the panel with the default text set
     */
    public DetailPanel()
    {
        init();
        resetDetails();
    }

    /**
     * set the details manually
     */
    public void setDetails(String html)
    {
        pane.setText(html + "<div></div>");
        scrollTop();
    }

    /**
     * revert the details to the default text
     */
    public void resetDetails()
    {
        setDetails(defaultText);
        append(new File("data/1.png"));
    }

    /**
     * set the details according to a model
     */
    public void setDetails(Exhibit exhibit)
    {
        setDetails(exhibit, true);
    }

    public void setDetails(Exhibit exhibit, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("exh/" + exhibit.getId());
            updateButtons();
        }
        setDetails(DetailPanel.toHTML(exhibit));
        lastDisplayed = exhibit;
        try
        {
            Collection<Image> images = Access.searchPictureByExhibitId(exhibit
                    .getId());
            for (Image step : images)
            {
                try
                {
                    append(step);
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(
                            this,
                            "Fehler beim Anzeigen des Bildes: "
                            + e.getMessage(), "E/A-Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden von Bildern: " + e.getMessage(),
                    "E/A-Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * set the details according to a model
     */
    public void setDetails(Museum museum)
    {
        setDetails(museum, true);
    }

    private void setDetails(Museum museum, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("mus/" + museum.getId());
            updateButtons();
        }
        lastDisplayed = museum;
        setDetails(DetailPanel.toHTML(museum));
    }

    /**
     * set the details according to a model
     */
    public void setDetails(Section section)
    {
        setDetails(section, true);
    }

    private void setDetails(Section section, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("sec/" + section.getId());
            updateButtons();
        }
        lastDisplayed = section;
        setDetails(DetailPanel.toHTML(section));
    }

    /**
     * set the details according to a model
     */
    public void setDetails(Category category)
    {
        setDetails(category, true);
    }

    private void setDetails(Category category, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("cat/" + category.getId());
            updateButtons();
        }
        lastDisplayed = category;
        setDetails(DetailPanel.toHTML(category));
    }

    /**
     * set the details according to a model
     */
    public void setDetails(Outsourced outsourced)
    {
        setDetails(outsourced, true);
    }

    private void setDetails(Outsourced outsourced, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("out/" + outsourced.getId());
            updateButtons();
        }
        lastDisplayed = outsourced;
        setDetails(DetailPanel.toHTML(outsourced));
    }

    public void setDetails(Contact contact)
    {
        setDetails(contact, true);
        lastDisplayed = contact;
    }

    public void setDetails(Contact contact, boolean recordHistory)
    {
        if (recordHistory)
        {
            history.record("con/" + contact.getId());
            updateButtons();
        }
        lastDisplayed = contact;
        setDetails(DetailPanel.toHTML(contact, true));
    }

    /**
     * show the current content again, updating it
     */
    public void refresh()
    {
        if (lastDisplayed instanceof Exhibit)
        {
            setDetails((Exhibit) lastDisplayed);
        }
        if (lastDisplayed instanceof Museum)
        {
            setDetails((Museum) lastDisplayed);
        }
        if (lastDisplayed instanceof Section)
        {
            setDetails((Section) lastDisplayed);
        }
        if (lastDisplayed instanceof Category)
        {
            setDetails((Category) lastDisplayed);
        }
        if (lastDisplayed instanceof Outsourced)
        {
            setDetails((Outsourced) lastDisplayed);
        }
    }

    /**
     * append an image to the text
     */
    public void append(byte[] image)
    {
        pane.insertIcon(new ImageIcon(image));
        scrollTop();
    }

    /**
     * append an image to the text
     */
    public void append(Image image)
    {
        append(image.getRawImage());
    }

    /**
     * append an image to the text
     */
    public void append(File image)
    {
        try
        {
            append(Files.readAllBytes(image.toPath()));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Fehler beim Laden von Bildern: " + e.getMessage(),
                    "E/A-Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @author Jochen Saßmannshausen
     */
    public Object getLastDisplayed()
    {
        return lastDisplayed;
    }

    /**
     * summerize an Exhibit as an HTML text
     */
    public static String toHTML(Exhibit exhibit)
    {
        try
        {
            Collection<Label> labels = Access.getAllLabelsByExhibitId(exhibit
                    .getId());
            String labelsText = "";
            String price[] = Access.reParsePrice(exhibit.getWert());
            for (Label label : labels)
            {
                if (labelsText.equals(""))
                {
                    labelsText = label.getName();
                }
                else
                {
                    labelsText += ", " + label.getName();
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<div><a href=EditExh/")
                    .append(exhibit.getId())
                    .append("><h2>")
                    .append(DetailPanel.textToHTML(exhibit.getName()))
                    .append("</h2></a></div>\n");
            if (exhibit.isDeleted())
            {
                sb.append("(<i>gelöscht</i>)");
            }
            // a table for better appearance
            sb.append("<table border=0>\n")
                .append("<tr>\n")// 1st row: count (left) and section (right)
                .append("<td><u>Anzahl</u>: ")
                .append(exhibit.getCount())
                .append("</td>\n")
                     // the section can only be shown if there is one
                .append("<td><u>Sektion</u>: ");
            if (exhibit.getSection_id() != null)
            {
                sb.append("<a href=Sec/")
                .append(exhibit.getSection_id())
                .append(">")
                .append(exhibit.getSection().getName())
                .append("</a>");
            }
            else
            {
                sb.append("<i>(keine)</i>");
            }
            sb.append("</td></tr>\n")
                .append("<tr>\n")// 2nd row: value (left) and category (right)
                .append("<td><u>Wert</u>: ")
                .append(price[0])
                .append(",")
                .append(price[1])
                .append("€</td>\n");
            // the category has a default if not set
                    // (we changed this, but in case the category is still
                    // null... just normal paranoia
            sb.append("<td><u>Kategorie</u>: ");
            if (exhibit.getCategory() != null)
            {
                sb.append("<a href=Cat/");
                sb.append(exhibit.getCategory_id())
                        .append(">")
                        .append(exhibit.getCategory().getName())
                        .append("</a>");
            }
            else
            {
                sb.append("<i>(keine)</i>");
            }
            sb.append("</td>\n")
              .append("</tr></table>");
            Outsourced outsourcedFound = null;
            for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
            {
                if (outsourced.getExhibitIds().containsKey(exhibit.getId()))
                {
                    outsourcedFound = outsourced;
                    break;
                }
            }
            if (outsourcedFound != null)
            {
                if (outsourcedFound.getContact_id() == null || outsourcedFound.getContact_id() == 0L)
                {
                    sb.append("<div><u>Ausstellung</u>: ");
                }
                else
                {
                    sb.append("<div><u>Leihgabe</u>: ");
                }
                sb.append("<a href=Out/")
                        .append(outsourcedFound.getId())
                        .append(">")
                        .append(outsourcedFound.getName())
                        .append("</a>");
                if (outsourcedFound.getEndDate() != null)
                {
                    sb.append(", läuft bis: ");
                    if (OutsourcedLogic.isExpired(outsourcedFound))
                    {
                        sb.append("<font color=800000><b>");
                    }
                    sb.append(new SimpleDateFormat("EE dd.MM.yyyy")
                        .format(outsourcedFound.getEndDate()));
                    if (OutsourcedLogic.isExpired(outsourcedFound))
                    {
                        sb.append("</font></b>");
                    }
                }
                if (!labels.isEmpty())
                {
                    sb.append("<div><u>Labels</u>: ")
                            .append(labelsText)
                            .append("</div>\n");
                }
            }
            sb.append("<div><font size=\"-2\"><a href=LinkExh/")
            	.append(exhibit.getId())
            	.append(">Verweis kopieren</a></font></div>\n")
            	.append("<hr></hr>")
            	.append(DetailPanel.insertDiv(exhibit.getDescription()))
            	.append("<hr></hr>");
            return sb.toString();
        }
        catch (Exception e)
        {
            return DetailPanel.toHTML(e);
        }
    }

    /**
     * summerize a Museum as an HTML text
     */
    public static String toHTML(Museum museum)
    {
        if (museum == null)
        {
            return "(Museum: Null-Pointer)";
        }
        try
        {
            String contacts = "";
            for (Contact contact : Access.searchContactByMuseumId(museum
                    .getId()))
            {
                contacts += "<td>" + DetailPanel.toHTML(contact, false)
                        + "</td>";
            }
            return "<div><h2><a href=EditMus/"
                    + museum.getId()
                    + ">"
                    + museum.getName()
                    + "</a></h2></div>"
                    + (museum.isDeleted() ? "(<i>gelöscht</i>)" : "")
                    + "<table border=0>"
                    // left: address, additional columns on the right: contacts
                    + "<tr>"
                    + "<th><u>Adresse</u>:</th> <th><u>Verantwortliche(r)</u>:</th>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>"
                    + DetailPanel.toHTML(museum.getAddress())
                    + "</td>"
                    + contacts // already enclosed in td tags
                    + "</tr>" + "</table>"
                    + "<div><font size=\"-2\"><a href=LinkMus/"
                    + museum.getId() + ">Verweis kopieren</a></font></div>"
                    + "<hr></hr>"
                    + DetailPanel.insertDiv(museum.getDescription());
        }
        catch (Exception e)
        {
            return DetailPanel.toHTML(e);
        }
    }

    /**
     * summerize a Section as an HTML text
     */
    public static String toHTML(Section section)
    {
        if (section == null)
        {
            return "(Section: Null-Pointer)";
        }
        try
        {
            return "<div><h2><a href=EditSec/" + section.getId() + ">"
                    + section.getName() + "</a></h2></div>"
                    + (section.isDeleted() ? "(<i>gelöscht</i>)" : "")
                    + "<div><font size=\"-2\"><a href=LinkSec/"
                    + section.getId() + ">Verweis kopieren</a></font></div>"
                    + "<hr></hr>"
                    + DetailPanel.insertDiv(section.getDescription());
        }
        catch (Exception e)
        {
            return DetailPanel.toHTML(e);
        }
    }

    /**
     * summerize a Category as an HTML text
     */
    public static String toHTML(Category category)
    {
        if (category == null)
        {
            return "(Category: Null-Pointer)";
        }
        try
        {
            return "<div><h2><a href=EditCat/" + category.getId() + ">"
                    + category.getName() + "</a></h2></div>"
                    + (category.isDeleted() ? "(<i>gelöscht</i>)" : "");
            // I decide this would be bad for the user (no descriptions possible
            // in categories,
            // thus no HTML linking in between category descriptions)
			/*
             * + "<div><font size=\"-2\"><a href=LinkCat/" + category.getId() +
             * ">Verweis kopieren</a></font></div>";
             */
        }
        catch (Exception e)
        {
            return DetailPanel.toHTML(e);
        }
    }

    /**
     * summerize an Outsourced as an HTML text
     */
    public static String toHTML(Outsourced outsourced)
    {
        if (outsourced == null)
        {
            return "(Outsourced: Null-Pointer)";
        }
        try
        {
            return "<div><h2>"
                    + (OutsourcedLogic.isExpired(outsourced) ? "<font color=#800000><b>"
                    : "")
                    + "<a href=EditOut/"
                    + outsourced.getId()
                    + ">"
                    + outsourced.getName()
                    + "</a>"
                    + (outsourced.isDeleted() ? "(<i>gelöscht</i>)" : "")
                    + (OutsourcedLogic.isExpired(outsourced) ? "</b></font>"
                    : "")
                    + "</h2></div>"
                    + "<table border=0><tr>"
                    + (outsourced.getAddress() != null ? "<td><div><u>Adresse</u>:</div>"
                    + DetailPanel.toHTML(outsourced.getAddress())
                    + "</td>"
                    : "")
                    + (outsourced.getContact_id() != null
                    && !outsourced.getContact_id().equals(0L) ? "<td><div><u>Verantwortlicher</u>:</div>"
                    + DetailPanel.toHTML(
                    Access.searchContactID(outsourced
                    .getContact_id()), false) + "</td>"
                    : "")
                    + "</tr></table>"
                    + "<div><u>Zeitraum</u>: "
                    + (outsourced.getStartDate() != null ? new SimpleDateFormat(
                    "EE dd.MM.yyyy").format(outsourced.getStartDate())
                    : "...")
                    + " - "
                    + (OutsourcedLogic.isExpired(outsourced) ? "<font color=#800000><b>"
                    : "")
                    + (outsourced.getEndDate() != null ? new SimpleDateFormat(
                    "EE dd.MM.yyyy").format(outsourced.getEndDate())
                    : "...")
                    + (OutsourcedLogic.isExpired(outsourced) ? "</b></font>"
                    : "") + "</div>"
                    + "<div><font size=\"-2\"><a href=LinkOut/"
                    + outsourced.getId() + ">Verweis kopieren</a></font></div>"
                    + "<hr></hr>"
                    + DetailPanel.insertDiv(outsourced.getDescription());
        }
        catch (Exception e)
        {
            return DetailPanel.toHTML(e);
        }
    }

    /**
     * get an adress in a readable form
     */
    public static String toHTML(Address address)
    {
        if (address == null)
        {
            return "";
        }
        else
        {
            return (address.isDeleted() ? "(<i>gelöscht</i>)" : "") + "<div>"
                    + address.getStreet() + " " + address.getHousenumber()
                    + ", " + address.getZipcode() + " " + address.getTown()
                    + "</div><div>" + address.getState() + ", "
                    + address.getCountry() + "</div>";
        }
    }

    /**
     * get a contact in a readable form
     */
    public static String toHTML(Contact contact, boolean full)
    {
        if (contact == null)
        {
            return "";
        }
        return (full ? "<div><h2><a href=EditCon/" + contact.getId() + ">"
                + contact.getForename() + " " + contact.getName()
                + "</a></h2></div>" : "<div><i><a href=con/" + contact.getId()
                + ">" + contact.getForename() + " " + contact.getName()
                + "</a></i></div>")
                + (contact.isDeleted() ? "(<i>gelöscht</i>)" : "")
                + "<div>Tel.: "
                + contact.getFon()
                + "</div>"
                + "<div>Fax: "
                + contact.getFax()
                + "</div>"
                + "<div>E-Mail: <a href=mailto:"
                + contact.getEmail()
                + ">"
                + contact.getEmail()
                + "</a></div>"
                + (contact.getRoleId() != null
                && !contact.getRoleId().equals(0L) ? "<div>Rolle: "
                + Access.searchRoleId(contact.getRoleId()).getName()
                + "</div>" : "")
                + (full ? "<hr></hr><div><i>"
                + DetailPanel.insertDiv(contact.getDescription())
                + "</i></div>" : "");
    }

    /**
     * wrap each line of the given source into &lt;div&gt;-tags
     */
    public static String insertDiv(String source)
    {
        if (source == null)
        {
            return "";
        }
        String result = "<div>";
        for (int i = 0; i < source.length(); i++)
        {
            String character = source.substring(i, i + 1);
            if (character.equals("\n"))
            {
                result += "</div>\n<div>";
            }
            else
            {
                result += character;
            }
        }
        result += "</div>";
        return result;
    }

    /**
     * reduce the given source to characters that don't interfere with the HTML
     */
    public static String textToHTML(String source)
    {
        if (source == null)
        {
            return "link";
        }
        String result = "";
        for (int i = 0; i < source.length(); i++)
        {
            char c = source.charAt(i);
            if (c == '&')
            {
                result += "&amp;";
            }
            if (c == '<')
            {
                result += "&lt;";
            }
            if (c == '>')
            {
                result += "&gt;";
            }
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
                    || (c >= '0' && c <= '9')
                    || DetailPanel.linkExtraCharsContains(c))
            {
                result += c;
            }
        }
        if (result.equals(""))
        {
            return "link";
        }
        return result;
    }

    /**
     * convenience function that checks if a given character is in
     * "htmlExtraChars"
     */
    public static boolean linkExtraCharsContains(char c)
    {
        for (char test : htmlExtraChars)
        {
            if (test == c)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * get an exception message as a red HTML text
     */
    public static String toHTML(Exception e)
    {
        return "<a style=\"color:#800000\"><i><b>" + e.getMessage()
                + "</b></i></a>";
    }

    /**
     * keep track of opened links and go back and forth as desired
     */
    private class LinkHistory
    {

        private LinkHistoryElement curr = null;
        /**
         * the first element in the resulting list; needed to cut off elements
         * at the beginning if too many pages were visited
         */
        private LinkHistoryElement start;
        private int count = 0;

        private LinkHistory()
        {
        }

        /**
         * call this when opening a new link
         */
        private void record(String link)
        {
            if (curr == null)
            {
                curr = new LinkHistoryElement(link);
                start = curr;
            }
            else
            {
                // don't record 2 same pages in a row
                if (curr != null)
                {
                    if (curr.cont.toUpperCase().equals(link.toUpperCase()))
                    {
                        return;
                    }
                }
                LinkHistoryElement newElem = new LinkHistoryElement(link);
                // cut off elements that are "walked over"
                if (hasNext())
                {
                    curr.next.prev = null;
                }
                curr.next = newElem;
                newElem.prev = curr;
                curr = newElem;
            }
            // allow going back 100 pages at max
            count++;
            while (count >= 100)
            {
                start = start.next;
                start.prev = null;
                // count again, the list could have been shortened
                count = 0;
                LinkHistoryElement test = curr;
                while (test.prev != null && test.prev != start)
                {
                    count++;
                    test = test.prev;
                }
            }
        }

        /**
         * go forth a step if possible, then return the new current link
         */
        private String goForth()
        {
            if (curr == null)
            {
                return "";
            }
            if (hasNext())
            {
                curr = curr.next;
            }
            return curr.cont;
        }

        /**
         * go back a step if possible, then return the new current link
         */
        private String goBack()
        {
            if (curr == null)
            {
                return "";
            }
            if (hasPrev())
            {
                curr = curr.prev;
            }
            return curr.cont;
        }

        /**
         * @return true if there is a next item in the history (after going
         * back)
         */
        private boolean hasNext()
        {
            if (curr == null)
            {
                return false;
            }
            return curr.next != null;
        }

        /**
         * @return true if there is a previous item in the history (if more than
         * 1 item was recorded subsequently)
         */
        private boolean hasPrev()
        {
            if (curr == null)
            {
                return false;
            }
            return curr.prev != null;
        }

        /**
         * a data structure that represents a record in the history
         */
        private class LinkHistoryElement
        {

            /**
             * link this element with the item following it; null if there is
             * none
             */
            private LinkHistoryElement next = null;
            /**
             * link this element with the item preceding it; null if there is
             * none
             */
            private LinkHistoryElement prev = null;
            /**
             * the actual content
             */
            private String cont;

            /**
             * This creates a new record, containing a string. The variables
             * "next" and "previous" must be set manually!
             */
            private LinkHistoryElement(String cont)
            {
                this.cont = cont;
            }
        }
    }
}

