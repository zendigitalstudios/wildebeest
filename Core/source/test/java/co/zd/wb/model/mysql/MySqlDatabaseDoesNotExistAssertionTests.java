package co.zd.wb.model.mysql;

import co.zd.wb.AssertExtensions;
import co.zd.wb.model.base.FakeInstance;
import co.zd.wb.model.AssertionFailedException;
import co.zd.wb.model.AssertionResponse;
import co.zd.wb.model.IndeterminateStateException;
import co.zd.wb.model.State;
import co.zd.wb.model.Migration;
import co.zd.wb.model.MigrationFailedException;
import co.zd.wb.model.MigrationNotPossibleException;
import co.zd.wb.model.base.ImmutableState;
import co.zd.wb.service.PrintStreamLogger;
import java.sql.SQLException;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;

public class MySqlDatabaseDoesNotExistAssertionTests
{
	 @Test public void applyForExistingDatabaseFails() throws
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
		 
		State created = new ImmutableState(UUID.randomUUID());
		resource.getStates().add(created);
		 
		Migration tran1 = new MySqlCreateDatabaseMigration(
			UUID.randomUUID(), null, created.getStateId());
		resource.getMigrations().add(tran1);
		 
		String databaseName = MySqlElementFixtures.databaseName("StmTest");
		
		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			databaseName);
		 
		resource.migrate(new PrintStreamLogger(System.out), instance, created.getStateId());
		
		MySqlDatabaseDoesNotExistAssertion assertion = new MySqlDatabaseDoesNotExistAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0);
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.apply(instance);

		MySqlUtil.dropDatabase(instance, databaseName);

		//
		// Assert Results
		//

		Assert.assertNotNull("response", response);
		AssertExtensions.assertAssertionResponse(false, "Database " + databaseName + " exists", response, "response");
		
	 }
	 
	 @Test public void applyForNonExistentDatabaseSucceeds() throws
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

		String databaseName = MySqlElementFixtures.databaseName("StmTest");

		MySqlDatabaseInstance instance = new MySqlDatabaseInstance(
			mySqlProperties.getHostName(),
			mySqlProperties.getPort(),
			mySqlProperties.getUsername(),
			mySqlProperties.getPassword(),
			databaseName);
		
		MySqlDatabaseDoesNotExistAssertion assertion = new MySqlDatabaseDoesNotExistAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0);
 
		//
		// Execute
		//
		
		AssertionResponse response = assertion.apply(instance);

		//
		// Assert Results
		//

		Assert.assertNotNull("response", response);
		AssertExtensions.assertAssertionResponse(
			true, "Database " + databaseName + " does not exist",
			response, "response");
		
	 }
	 
	 @Test public void applyForNullInstanceFails()
	 {
		 
		//
		// Fixture Setup
		//

		MySqlDatabaseDoesNotExistAssertion assertion = new MySqlDatabaseDoesNotExistAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0);
		
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
		 
		MySqlDatabaseDoesNotExistAssertion assertion = new MySqlDatabaseDoesNotExistAssertion(
			UUID.randomUUID(),
			"Database does not exist",
			0);
		
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