package de.museum.berleburg.datastorage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;

import de.museum.berleburg.datastorage.Configuration;
import de.museum.berleburg.datastorage.interfaces.ISqlQuery;
import de.museum.berleburg.datastorage.interfaces.Model;
import de.museum.berleburg.exceptions.ConnectionException;
import de.museum.berleburg.exceptions.ConnectionTimeOutException;

/**
 *
 * @author Anselm Brehme
 */
public abstract class AbstractSQLQuery<M extends Model> implements ISqlQuery<M>
{

    protected Connection localConnection;
    protected PreparedStatement store;
    protected PreparedStatement delete;
    protected PreparedStatement update;
    protected PreparedStatement loadAll;
    protected Connection serverConnection;
    protected PreparedStatement deleteBackup;
    protected PreparedStatement updateServer;
    protected PreparedStatement loadAllBackup;
    public final String[] createTableSQLs;
    private final String tableName;
    private final String keyName;
    private final String[] valueNames;

    public AbstractSQLQuery(String[] createTableSQLs, String key, String table, String... values) throws ConnectionException
    {
        this.createTableSQLs = createTableSQLs;
        this.keyName = key;
        this.tableName = table;
        this.valueNames = values;
        try
        {
            this.localConnection = Configuration.getInstance().getConnection();
            this.createTables();
            this.init();
        }
        catch (SQLTimeoutException e)
        {
            throw new ConnectionTimeOutException("Connection timed out", e);
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Unexpected SQLError!", e);
        }

    }

    @Override
    public void updateConnections() throws ConnectionException
    {
        this.serverConnection = Configuration.getInstance().getServerConnection();
        this.localConnection = Configuration.getInstance().getConnection();
    }

    @Override
    public void createTables() throws SQLException
    {
        try
        {
            if (this.serverConnection != null)
            {
                for (String sql : createTableSQLs) // Creating backupDB
                {
                    this.serverConnection.createStatement().execute(sql);
                }
            }
        }
        catch (Exception ignored)
        {
            this.serverConnection = null;
            //System.out.println("Backup connection failed!");
        }
        //Local Connection
        for (String sql : createTableSQLs) // Creating localDB
        {
            this.localConnection.createStatement().execute(sql);
        }
    }

    @Override
    public void init() throws SQLException
    {
        this.prepareStatements();
    }

    @Override
    public void prepareStatements() throws SQLException
    {
        this.loadAll = this.localConnection.prepareStatement(this.prepareGetAll());
        this.update = this.localConnection.prepareStatement(this.prepareUpdate());
        this.store = this.localConnection.prepareStatement(this.prepareStore(), Statement.RETURN_GENERATED_KEYS);
        this.delete = this.localConnection.prepareStatement(this.prepareDelete());

        if (this.serverConnection != null)
        {
            this.deleteBackup = this.serverConnection.prepareStatement(this.prepareDelete());
            this.updateServer = this.serverConnection.prepareStatement(this.prepareUpdate());
            this.loadAllBackup = this.serverConnection.prepareStatement(this.prepareGetAll());
        }
    }

    protected String prepareDelete()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(tableName).append(" WHERE ").append(keyName).append(" = ?");
        return sb.toString();
    }

    protected String prepareUpdate()
    {
        if (valueNames.length == 0)
        {
            throw new IllegalStateException("Values to update cannot be none!");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append("(\n").
                append("\t").append(keyName);
        for (int i = 0; i < valueNames.length; ++i)
        {
            sb.append(",\n\t").append(valueNames[i]);
        }
        sb.append(")\nVALUES (?");
        for (int i = 0; i < valueNames.length; ++i)
        {
            sb.append(",?");
        }
        sb.append(") \nON DUPLICATE KEY UPDATE ").append(keyName).append("=VALUES(").append(keyName).append(")");
        for (int i = 0; i < valueNames.length; ++i)
        {
            sb.append(",\n").append(valueNames[i]).append("=VALUES(").append(valueNames[i]).append(')');
        }
        return sb.toString();
        //Previous update sql:
        /*
         sb.append("UPDATE ").append(table).append(" SET \n").
         append("\t").append(values[0]).append(" = ?");
         for (int i = 1; i < values.length; ++i)
         {
         sb.append(",\n\t").append(values[i]).append(" = ?");
         }
         sb.append("\nWHERE ").append(key).append(" = ?");
         System.out.println(sb);//DEBUGGING
         return sb.toString();
         */
    }

    protected String prepareStore()
    {
        if (valueNames.length == 0)
        {
            throw new IllegalStateException("Values to store cannot be none!");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append("(\n").
                append("\t").append(valueNames[0]);
        for (int i = 1; i < valueNames.length; ++i)
        {
            sb.append(",\n\t").append(valueNames[i]);
        }
        sb.append(")\nVALUES (?");
        for (int i = 1; i < valueNames.length; ++i)
        {
            sb.append(",?");
        }
        sb.append(") \nON DUPLICATE KEY UPDATE ").append(valueNames[0]).append("=VALUES(").append(valueNames[0]).append(")");
        for (int i = 1; i < valueNames.length; ++i)
        {
            sb.append(",\n").append(valueNames[i]).append("=VALUES(").append(valueNames[i]).append(')');
        }
        return sb.toString();
    }

    protected String prepareGetAll()
    {
        return "SELECT * FROM " + tableName;
    }

    public void bindValues(PreparedStatement statement, Object... args) throws SQLException
    {
        for (int i = 0; i < args.length; ++i)
        {
            statement.setObject(i + 1, args[i]);
        }
    }

    protected void setGeneratedKey(M model) throws SQLException
    {
        ResultSet resultSet = this.store.getGeneratedKeys();
        resultSet.next();
        model.setId(resultSet.getLong(1));
    }

    @Override
    public void delete(boolean local, M model) throws SQLException
    {
        if (local)
        {
            this.bindValues(delete, model.getId());
            this.delete.execute();
        }
        else
        {
            this.bindValues(deleteBackup, model.getId());
            this.deleteBackup.execute();
        }
    }

    @Override
    public M getById(boolean local, Long id) throws SQLException
    {
        throw new UnsupportedOperationException("Not supported.");
    }
}
