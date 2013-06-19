package co.mv.stm.model.mysql;

import co.mv.stm.AssertExtensions;
import co.mv.stm.model.base.FakeInstance;
import co.mv.stm.model.database.SqlScriptMigration;
import co.mv.stm.model.AssertionFailedException;
import co.mv.stm.model.AssertionResponse;
import co.mv.stm.model.IndeterminateStateException;
import co.mv.stm.model.State;
import co.mv.stm.model.Migration;
import co.mv.stm.model.MigrationFailedException;
import co.mv.stm.model.MigrationNotPossibleException;
import co.mv.stm.model.base.ImmutableState;
import co.mv.stm.service.PrintStreamLogger;
import java.sql.SQLException;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;

public class MySqlTableExistsAssertionTests
{
	 @Test public void applyForExistingTableSucceeds() throws
		 IndeterminateStateException,
		 AssertionFailedException,
		 MigrationNotPossibleException,
		 MigrationFailedException,
		 SQLException
	 {
		 
		//
		// Fixture Setup
		//
		 
		MySqlProperties mySqlProperties = MySqlProperties.get();

		MySqlDatabaseResource resource = new MySqlDatabaseResource(UUID.randomUUID(), "Database");
		 
		// Created
		State created = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(created);

		// Schema Loaded
		State schemaLoaded = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(schemaLoaded);
		 
		// Migrate -> created
		Migration migration1 = new MySqlCreateDatabaseMigration(
			UUID.randomUUID(), null, created.getStateId());
		resource.getMigrations().add(migration1);
		 
		// Migrate created -> schemaLoaded
		Migration migration2 = new SqlScriptMigration(
			UUID.randomUUID(),
			created.getStateId(),
			schemaLoaded.getStateId(),
			MySqlElementFixtures.realmTypeRefCreateTableStatement());
		resource.getMigrations().add(migration2);

		String databaseName = MySqlElementFixtures.databaseName("StmTest");
		
		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			databaseName);
		 
		resource.migrate(new PrintStreamLogger(System.out), instance, schemaLoaded.getStateId());
		
		MySqlTableExistsAssertion assertion = new MySqlTableExistsAssertion(
			UUID.randomUUID(),
			"RealmTypeRef Exists",
			0,
			"RealmTypeRef");
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.apply(instance);
		
		MySqlUtil.dropDatabase(instance, databaseName);

		//
		// Assert Results
		//

		Assert.assertNotNull("response", response);
		AssertExtensions.assertAssertionResponse(true, "Table RealmTypeRef exists", response, "response");
		
	 }
	 
	 @Test public void applyForNonExistentTableFails() throws
		 IndeterminateStateException,
		 AssertionFailedException,
		 MigrationNotPossibleException,
		 MigrationFailedException,
		 SQLException
	 {
		 
		//
		// Fixture Setup
		//

		MySqlProperties mySqlProperties = MySqlProperties.get();
		 
		MySqlDatabaseResource resource = new MySqlDatabaseResource(UUID.randomUUID(), "Database");
		 
		// Created
		State created = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(created);
		 
		// Migrate -> created
		Migration migration1 = new MySqlCreateDatabaseMigration(
			UUID.randomUUID(), null, created.getStateId());
		resource.getMigrations().add(migration1);

		String databaseName = MySqlElementFixtures.databaseName("StmTest");

		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			databaseName);
		 
		resource.migrate(new PrintStreamLogger(System.out), instance, created.getStateId());
		
		MySqlTableExistsAssertion assertion = new MySqlTableExistsAssertion(
			UUID.randomUUID(),
			"RealmTypeRef Exists",
			0,
			"RealmTypeRef");
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.apply(instance);

		MySqlUtil.dropDatabase(instance, databaseName);

		//
		// Assert Results
		//

		Assert.assertNotNull("response", response);
		AssertExtensions.assertAssertionResponse(false, "Table RealmTypeRef does not exist", response, "response");
		
	 }
	 
	 @Test public void applyForNonExistentDatabaseFails()
	 {
		 
		//
		// Fixture Setup
		//

		MySqlProperties mySqlProperties = MySqlProperties.get();

		String databaseName = MySqlElementFixtures.databaseName("StmTest");
		
		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			databaseName);
		 
		MySqlTableExistsAssertion assertion = new MySqlTableExistsAssertion(
			UUID.randomUUID(),
			"RealmTypeRef Exists",
			0,
			"RealmTypeRef");
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.apply(instance);

		//
		// Assert Results
		//

		Assert.assertNotNull("response", response);
		AssertExtensions.assertAssertionResponse(
			false, "Database " + databaseName + " does not exist",
			response, "response");

	 }
	 
	 @Test public void applyForNullInstanceFails()
	 {
		 
		//
		// Fixture Setup
		//
		 
		MySqlTableExistsAssertion assertion = new MySqlTableExistsAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0,
			"TableName");
		
		//
		// Execute
		//
		
		IllegalArgumentException caught = null;
		
		try
		{
			AssertionResponse response = assertion.apply(null);
			
			Assert.fail("IllegalArgumentException expected");
		}
		catch(IllegalArgumentException e)
		{
			caught = e;
		}

		//
		// Assert Results
		//

		Assert.assertEquals("caught.message", "instance cannot be null", caught.getMessage());
		
	 }
	 
	 @Test public void applyForIncorrectInstanceTypeFails()
	 {
		 
		//
		// Fixture Setup
		//
		 
		MySqlTableExistsAssertion assertion = new MySqlTableExistsAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0,
			"TableName");
		
		FakeInstance instance = new FakeInstance();
		
		//
		// Execute
		//
		
		IllegalArgumentException caught = null;
		
		try
		{
			AssertionResponse response = assertion.apply(instance);
			
			Assert.fail("IllegalArgumentException expected");
		}
		catch(IllegalArgumentException e)
		{
			caught = e;
		}

		//
		// Assert Results
		//

		Assert.assertEquals("caught.message", "instance must be a MySqlDatabaseInstance", caught.getMessage());
		
	}
}