package de.museum.berleburg.userInterface.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.museum.berleburg.datastorage.DataAccess;
import de.museum.berleburg.datastorage.model.Exhibit;
import de.museum.berleburg.datastorage.model.Outsourced;
import de.museum.berleburg.exceptions.ExhibitNotFoundException;
import de.museum.berleburg.logic.OutsourcedLogic;
import de.museum.berleburg.logicAccess.Access;
import de.museum.berleburg.userInterface.MainGUI;
import de.museum.berleburg.userInterface.panels.MuseumMainPanel;
import de.museum.berleburg.userInterface.table.TableModelExpired;

public class ShowExpired extends JDialog
{
    /**
     * @author Frank Hülsmann
     */
    private static final long serialVersionUID = -5432048624927094918L;
    private final JPanel tablePanel = new JPanel();
    private JPanel buttonPanel = new JPanel();
    private JButton cancelButton = new JButton("Abbrechen");
    private JButton showExhButton = new JButton("Exponat anzeigen");
    private JTable table;
    private TableModelExpired model;
    private Long currentId = null;
    private static ShowExpired instance = null;

    /**
     * Create the dialog.
     */
    public ShowExpired(JFrame frame, boolean modal)
    {
        super(frame, modal);
        instance = this;
        setBounds(100, 100, 550, 400);
        setTitle("Abgelaufene Exponate");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainGUI.class.getResource("/de/museum/berleburg/userInterface/logo.png")));

        getContentPane().setLayout(new BorderLayout());

        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        tablePanel.setLayout(new GridBagLayout());

        tablePanel.setBorder(null);

        getContentPane().add(tablePanel, BorderLayout.CENTER);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        table = new JTable();



        table.addMouseListener(new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {

                showExhButton.setEnabled(true);
                int row = table.rowAtPoint(e.getPoint());
                long id = (long) model.getId(row);
                currentId = id;

                int rowIndex = table.getSelectedRow();
                if (rowIndex < 0)
                {
                    return;
                }

                if (e.getClickCount() == 2 && e.getModifiers() == InputEvent.BUTTON1_MASK)
                {
                    try
                    {
                        Exhibit exhibit = Access.searchExhibitID(id);

                        EditExhibit dialog = new EditExhibit(exhibit);
                        dialog.enableEditing(false);
                        dialog.setVisible(true);
                    }
                    catch (ExhibitNotFoundException e1)
                    {
                        JOptionPane.showMessageDialog(null, e1.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 0);

        tablePanel.add(new JScrollPane(table), c);

        showExhButton.setActionCommand("Exponat anzeigen");
        showExhButton.setEnabled(false);
        showExhButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {

                Exhibit exhibit = null;
                try
                {
                    exhibit = Access.searchExhibitID(currentId);
                }
                catch (ExhibitNotFoundException e)
                {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                }

                EditExhibit dialog = new EditExhibit(exhibit);
                dialog.enableEditing(false);
                dialog.setVisible(true);

            }
        });
        showExhButton.setActionCommand("OK");
        cancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent arg0)
            {
                dispose();
            }
        });
        buttonPanel.add(showExhButton);
        buttonPanel.add(cancelButton);

        getRootPane().setDefaultButton(cancelButton);

        updateTable(MuseumMainPanel.getInstance().getMuseumId());

    }

    public ShowExpired()
    {
    }

    /**
     *
     * @return instance
     */
    public static ShowExpired getInstance()
    {
        if (instance == null)
        {
            instance = new ShowExpired();
        }
        return instance;
    }

    /**
     * Table Update
     *
     * @param museumId
     */
    public void updateTable(Long museumId)
    {
        model = new TableModelExpired(getAllExpired(museumId));
        table.setModel(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        model.fireTableDataChanged();
    }

    /**
     *
     * @param museumId
     * @return resultList (all expired Exhibits)
     */
    public ArrayList<Exhibit> getAllExpired(Long museumId)
    {
        ArrayList<Exhibit> resultList = new ArrayList<Exhibit>();
        for (Outsourced outsourced : DataAccess.getInstance().getAllOutsourced())
        {
            if (OutsourcedLogic.isExpired(outsourced))
            {
                for (Map.Entry<Long,Timestamp> entry : outsourced.getExhibitIds().entrySet())
                {
                    if (entry.getValue() == null)
                    {
                        resultList.add(DataAccess.getInstance().getExhibitById(entry.getKey()));
                    }
                }
            }
        }
        return resultList;
    }
}
