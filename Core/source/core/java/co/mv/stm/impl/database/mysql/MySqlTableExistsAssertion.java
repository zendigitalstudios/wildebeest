package co.mv.stm.impl.database.mysql;

import co.mv.stm.impl.BaseAssertion;
import co.mv.stm.impl.database.DatabaseHelper;
import co.mv.stm.model.AssertionFaultException;
import co.mv.stm.model.AssertionResponse;
import co.mv.stm.model.AssertionType;
import co.mv.stm.model.ModelExtensions;
import co.mv.stm.model.ResourceInstance;
import co.mv.stm.model.impl.ImmutableAssertionResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlTableExistsAssertion extends BaseAssertion
{
	public MySqlTableExistsAssertion(
		UUID assertionId,
		String name,
		int seqNum,
		String tableName)
	{
		super(assertionId, name, seqNum, AssertionType.DatabaseRowDoesNotExist);
		
		this.setTableName(tableName);
	}

	// <editor-fold desc="TableName" defaultstate="collapsed">

	private String m_tableName = null;
	private boolean m_tableName_set = false;

	public String getTableName() {
		if(!m_tableName_set) {
			throw new IllegalStateException("tableName not set.  Use the HasTableName() method to check its state before accessing it.");
		}
		return m_tableName;
	}

	private void setTableName(
		String value) {
		if(value == null) {
			throw new IllegalArgumentException("tableName cannot be null");
		}
		boolean changing = !m_tableName_set || m_tableName != value;
		if(changing) {
			m_tableName_set = true;
			m_tableName = value;
		}
	}

	private void clearTableName() {
		if(m_tableName_set) {
			m_tableName_set = true;
			m_tableName = null;
		}
	}

	private boolean hasTableName() {
		return m_tableName_set;
	}

	// </editor-fold>
	
	@Override public AssertionResponse apply(ResourceInstance instance)
	{
		if (instance == null) { throw new IllegalArgumentException("instance cannot be null"); }
		MySqlDatabaseResourceInstance db = ModelExtensions.As(instance, MySqlDatabaseResourceInstance.class);
		if (db == null) { throw new IllegalArgumentException("instance must be a MySqlDatabaseResourceInstance"); }
		
		AssertionResponse result = null;

		if (!MySqlDatabaseHelper.schemaExists(db, db.getSchemaName()))
		{
			result = new ImmutableAssertionResponse(
				false,
				String.format("Database %s does not exist", db.getSchemaName()));
		}
		
		else if (MySqlDatabaseHelper.tableExists(db, db.getSchemaName(), this.getTableName()))
		{
			result = new ImmutableAssertionResponse(true, "Table " + this.getTableName() + " exists");
		}
		
		else
		{
			result = new ImmutableAssertionResponse(false, "Table " + this.getTableName() + " does not exist");
		}
		
		return result;
	}
}