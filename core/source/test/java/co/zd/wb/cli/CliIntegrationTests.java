// Wildebeest Migration Framework
// Copyright 2013, Zen Digital Co Inc
//
// This file is part of Wildebeest
//
// Wildebeest is free software: you can redistribute it and/or modify it under
// the terms of the GNU General Public License v2 as published by the Free
// Software Foundation.
//
// Wildebeest is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along with
// Wildebeest.  If not, see http://www.gnu.org/licenses/gpl-2.0.html

package co.zd.wb.cli;

import co.zd.wb.Interface;
import co.zd.wb.Instance;
import co.zd.wb.plugin.mysql.MySqlDatabaseInstance;
import co.zd.wb.plugin.mysql.MySqlUtil;
import co.zd.wb.plugin.sqlserver.SqlServerDatabaseInstance;
import co.zd.wb.plugin.sqlserver.SqlServerUtil;
import co.zd.wb.service.MessagesException;
import java.io.File;
import java.sql.SQLException;
import org.junit.Test;

public class CliIntegrationTests
{
	@Test public void loadFromFilesAndMigrateMySqlResource() throws SQLException, MessagesException
	{
		
		//
		// Setup
		//

		WildebeestCommand wb = new WildebeestCommand();
		String[] args = new String[]
		{
			"migrate",
			"--resource:integration_test_fixtures/mysql_database/database.wbresource.xml",
			"--instance:integration_test_fixtures/mysql_database/staging_db.wbinstance.xml",
			"--targetState:Core Schema Loaded"
		};
		
		Instance instance = Interface.loadInstance(
			new File("integration_test_fixtures/mysql_database/staging_db.wbinstance.xml"));
		
		//
		// Execute
		//

		try
		{
			wb.run(args);
		}
		finally
		{
			MySqlDatabaseInstance instanceT = (MySqlDatabaseInstance)instance;
			MySqlUtil.dropDatabase(instanceT, instanceT.getSchemaName());
		}

	}
	
	@Test public void loadFromFilesAndMigrationSqlServerResource() throws SQLException, MessagesException
	{
		
		//
		// Setup
		//
		
		WildebeestCommand wb = new WildebeestCommand();
		String[] args = new String[]
		{
			"migrate",
			"--resource:integration_test_fixtures/sqlserver_database/database.wbresource.xml",
			"--instance:integration_test_fixtures/sqlserver_database/staging_db.wbinstance.xml",
			"--targetState:Core Schema Loaded"
		};
		
		Instance instance = Interface.loadInstance(
			new File("integration_test_fixtures/sqlserver_database/staging_db.wbinstance.xml"));
		
		//
		// Execute
		//
		
		try
		{
			wb.run(args);
		}
		finally
		{
			SqlServerDatabaseInstance instanceT = (SqlServerDatabaseInstance)instance;
			SqlServerUtil.tryDropDatabase(instanceT);
		}
	}
}