package de.museum.berleburg.userInterface.dialogs;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.exceptions.MuseumNotFoundException;
import de.museum.berleburg.exceptions.OutsourcedNotFoundException;
import de.museum.berleburg.logic.OutsourcedLogic;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.InformationPanel;
import de.museum.berleburg.userInterface.panels.TablePanel;
import de.museum.berleburg.userInterface.panels.TreeMainPanel;

/**
 *
 * @author Way Dat To
 *
 */
@SuppressWarnings("serial")
public class AddToLoan extends JDialog
{

    TablePanel tablePanel;
    private ArrayList<Long> ids;
    private JComboBox<String> comboBox;
    private ComboBoxModel<String> comboBoxModel;
    private JLabel exhibitCount;
    private static AddToLoan instance;
    Vector<Long> vectorLong = new Vector<Long>();
    Vector<String> vectorString = new Vector<String>();
    ArrayList<Outsourced> list = new ArrayList<Outsourced>();

    /**
     * Create the dialog.
     */
    /**
     *
     */
    public AddToLoan()
    {
        instance = this;

        tablePanel = TablePanel.getInstance();

        setModal(true);
        setTitle("Exponat verleihen");
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(
                new MigLayout("", "[][434px,grow]", "[][][][140:n:140][33px]"));
        {
            JLabel lblAnzahlDerExponate = new JLabel("Anzahl der Exponate");
            getContentPane().add(lblAnzahlDerExponate, "cell 0 0");
        }
        {
            if (tablePanel.isChecked())
            {
                exhibitCount = new JLabel(""
                        + tablePanel.getCheckedIds().size());
            }
            else
            {
                exhibitCount = new JLabel("" + 1);
            }

            getContentPane().add(exhibitCount, "cell 1 0");
        }
        {
            JLabel lblErstelleEineNeue = new JLabel(
                    "Erstelle eine neue Leihgabe");
            getContentPane().add(lblErstelleEineNeue, "cell 0 2");
        }
        {
            JLabel lblNewLabel = new JLabel("Wähle eine Leihgabe");
            getContentPane().add(lblNewLabel, "cell 0 1,alignx left");
        }
        {
            {
                comboBox = new JComboBox<String>();
                comboBoxModel = new DefaultComboBoxModel<String>(getVectors());
                comboBoxModel.addListDataListener(comboBox);
                comboBox.setModel(comboBoxModel);
                comboBox.setBounds(158, 207, 320, 20);
                getContentPane().add(comboBox, "cell 1 1,growx");
            }

        }
        {
            JButton btnNeueLeihgabeErstellen = new JButton(
                    "Neue Leihgabe erstellen");
            btnNeueLeihgabeErstellen.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    CreateLoan createLoan = new CreateLoan(true);
                    createLoan.setVisible(true);
                }
            });
            getContentPane().add(btnNeueLeihgabeErstellen, "cell 1 2");
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, "cell 0 4 2 1,growx,aligny top");
            {
                JButton okButton = new JButton("Verleihen");
                okButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        if (comboBox.getItemCount() == 0)
                        {
                            JOptionPane
                                    .showMessageDialog(null,
                                    "Keine Leihgaben vorhanden!",
                                    "Keine Leihgabe",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            try
                            {
                                checkList(list);
                            }
                            catch (emptyList e2)
                            {
                                JOptionPane.showMessageDialog(null,
                                        e2.getMessage(), "Fehler",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            if (tablePanel.isChecked())
                            {
                                ids = tablePanel.getCheckedIds();

                                for (Long actual : ids)
                                {

                                    try
                                    {
                                        Exhibit exh = null;
                                        if (actual != null)
                                        {
                                            try
                                            {
                                                exh = Access
                                                        .searchExhibitID(actual);
                                            }
                                            catch (ExhibitNotFoundException e1)
                                            {

                                                continue;
                                            }
                                        }
                                        Outsourced outsourcedFound = null;
                                        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
                                        {
                                            if (outsourced.getExhibitIds().containsKey(exh.getId()))
                                            {
                                            	if(outsourced.givenBack(exh.getId())==null)
                                            		outsourcedFound = outsourced;
                                                break;
                                            }
                                        }
                                        if (outsourcedFound != null)
                                        {
                                            int reply = JOptionPane
                                                    .showConfirmDialog(
                                                    null,
                                                    "Das Exponat "
                                                    + exh.getName()
                                                    + " ist bereits der Ausstellung/Leihgabe "
                                                    + outsourcedFound.getName()
                                                    + " zugeordnet.\nMöchten Sie es dennoch verleihen?",
                                                    "Exponat verleihen",
                                                    JOptionPane.YES_NO_OPTION);
                                            if (reply == JOptionPane.NO_OPTION)
                                            {
                                                continue;
                                            }
                                        }
                                        Access.addToLoan(actual, vectorLong
                                                .get(comboBox
                                                .getSelectedIndex()));
                                    }
                                    catch (ConnectionException e1)
                                    {
                                        JOptionPane
                                                .showMessageDialog(
                                                null,
                                                e1.getMessage(),
                                                "Datenbankfehler",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                    catch (OutsourcedNotFoundException e1)
                                    {
                                        JOptionPane.showMessageDialog(null,
                                                e1.getMessage(), "Fehler",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }

                                InformationPanel
                                        .getInstance()
                                        .setText(
                                        "Exponat(e) erfolgreich zur Leihgabe hinzugefügt");
                                TablePanel.getInstance().refreshTable();
                                MainGUI.getDetailPanel().refresh();
                                dispose();

                            }
                            else
                            {

                                try
                                {
                                    boolean add = true;
                                    Exhibit exh = null;

                                    try
                                    {
                                        exh = Access.searchExhibitID(tablePanel
                                                .getSelectedRowId());
                                    }
                                    catch (ExhibitNotFoundException e1)
                                    {

                                        add = false;
                                    }
                                    Outsourced outsourcedFound = null;
                                    for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
                                    {
                                        if (outsourced.getExhibitIds().containsKey(exh.getId()))
                                        {
                                        	if(outsourced.givenBack(exh.getId())==null)
                                        		outsourcedFound = outsourced;
                                            break;
                                        }
                                    }
                                    if (outsourcedFound != null)
                                    {
                                        int reply = JOptionPane
                                                .showConfirmDialog(
                                                null,
                                                "Das Exponat "
                                                + exh.getName()
                                                + " ist bereits der Ausstellung/Leihgabe "
                                                + outsourcedFound.getName()
                                                + " zugeordnet.\nMöchten Sie es dennoch verleihen?",
                                                "Exponat verleihen",
                                                JOptionPane.YES_NO_OPTION);
                                        if (reply == JOptionPane.NO_OPTION)
                                        {
                                            add = false;
                                        }
                                    }
                                    if (add)
                                    {
                                        Access.addToLoan(tablePanel
                                                .getSelectedRowId(), vectorLong
                                                .get(comboBox
                                                .getSelectedIndex()));
                                        InformationPanel
                                                .getInstance()
                                                .setText(
                                                "Exponat(e) erfolgreich zur Leihgabe hinzugefügt");
                                        TablePanel.getInstance().refreshTable();
                                        MainGUI.getDetailPanel().refresh();
                                        dispose();
                                    }
                                }
                                catch (ConnectionException e1)
                                {
                                    JOptionPane.showMessageDialog(null,
                                            e1.getMessage(),
                                            "Datenbankfehler",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                                catch (OutsourcedNotFoundException e1)
                                {
                                    JOptionPane.showMessageDialog(null,
                                            e1.getMessage(), "Fehler",
                                            JOptionPane.ERROR_MESSAGE);
                                }

                            }

                        }

                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Abbrechen");
                cancelButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    /**
     * Insert data of Loans to two vectors
     *
     * @return
     */
    public Vector<String> getVectors()
    {

        try
        {
            list = Access.getAllLoansByMuseum(TreeMainPanel.getInstance()
                    .getMuseumId());
        }
        catch (MuseumNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
        for (Outsourced actual : list)
        {
            if (!actual.isDeleted() && !OutsourcedLogic.isExpired(actual))
            {
                vectorLong.add(actual.getId());
                vectorString.add(actual.getName());
            }
        }
        return vectorString;
    }

    /**
     *
     */
    public void refresh()
    {
        vectorLong.clear();
        vectorString.clear();
        comboBox.setModel(new DefaultComboBoxModel<String>(getVectors()));

    }

    /**
     * Returns an instance of this class
     *
     * @return
     */
    public static AddToLoan getInstance()
    {

        if (instance == null)
        {
            instance = new AddToLoan();
        }
        return instance;
    }

    class emptyList extends Exception
    {

        public emptyList(String msg)
        {
            super(msg);
        }
    }

    private void checkList(ArrayList<Outsourced> l) throws emptyList
    {
        if (l.isEmpty())
        {
            throw new emptyList("Keine Leihgabe vorhanden!");
        }

    }
}
