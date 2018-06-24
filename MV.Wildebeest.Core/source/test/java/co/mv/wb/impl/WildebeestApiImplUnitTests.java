// Wildebeest Migration Framework
// Copyright © 2013 - 2018, Matheson Ventures Pte Ltd
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

package co.mv.wb.impl;

import co.mv.wb.Assertion;
import co.mv.wb.AssertionFailedException;
import co.mv.wb.IndeterminateStateException;
import co.mv.wb.InvalidStateSpecifiedException;
import co.mv.wb.MigrationFailedException;
import co.mv.wb.MigrationInvalidStateException;
import co.mv.wb.MigrationNotPossibleException;
import co.mv.wb.MigrationPlugin;
import co.mv.wb.State;
import co.mv.wb.TargetNotSpecifiedException;
import co.mv.wb.UnknownStateSpecifiedException;
import co.mv.wb.Wildebeest;
import co.mv.wb.WildebeestApi;
import co.mv.wb.XmlValidationException;
import co.mv.wb.fixture.TestContext_SimpleFakeResource;
import co.mv.wb.fixture.TestContext_SimpleFakeResource_Builder;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.plugin.fake.FakeInstance;
import co.mv.wb.plugin.fake.SetTagMigrationPlugin;
import co.mv.wb.plugin.fake.TagAssertion;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static co.mv.wb.Asserts.assertFakeInstance;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for WildebeestApiImpl.
 *
 * @since 4.0
 */
public class WildebeestApiImplUnitTests
{
	/**
	 * A call to migrate specified a target and the resource does not have a default.  WildebeestApiImpl correctly
	 * resolves the specified target and passes it to ResourceHelperImpl.
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_targetSpecifiedNoDefault_succeeds() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.get();

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("bar")
			.getResourceWithNonExistantInitialState();

		// Execute
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.of("foo"));

		// Verify
		assertFakeInstance(
			"Foo",
			context.instance,
			"instance");
	}

	/**
	 * A call to migrate specified a target and the resource has a default.  WildebeestApiImpl correctly resolves the
	 * specified target and passes it to ResourceHelperImpl.
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_targetSpecifiedWithDefault_succeeds() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("bar")
			.getResourceWithNonExistantInitialState();

		PrintStream output = System.out;

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.get();

		// Execute
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.of("foo"));

		// Verify
		assertFakeInstance(
			"Foo",
			context.instance,
			"instance");
	}

	/**
	 * A call to migrate did not specify a target and the resource does not have a default.  WildebeestApiImpl raises
	 * the error by throwing a TargetNotSpecifiedException.
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_targetNotSpecifiedNoDefault_throws() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.get();

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.getResourceWithNonExistantInitialState();

		// Execute and Verify
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.empty());
	}

	/**
	 * A call to migrate did not specify a target but the resource has a default.  WildebeestApiImpl correctly resolves
	 * the default target and passes it to ResourceHelperImpl
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_targetNotSpecifiedWithDefault_succeeds() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.get();

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("bar")
			.getResourceWithNonExistantInitialState();

		// Execute
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.empty());

		// Verify
		assertFakeInstance(
			"Bar",
			context.instance,
			"instance");
	}

	/**
	 * A call to migrate from non existent with assertions that is expected to pass
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_withAssertionsPriorToMigration_fromNonExistent_succeeds() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("bar")
			.getResourceWithNonExistantInitialState();

		List<MigrationPlugin> migrationPlugins = new ArrayList<>();
		migrationPlugins.add(new SetTagMigrationPlugin(context.resource));

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.withCustomResourcePlugins(context.resourcePlugins)
			.withPluginManager(new PluginManagerImpl(
				Wildebeest.getPluginGroups(),
				migrationPlugins))
			.get();

		// Execute
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.empty());

		// Verify
		assertFakeInstance(
			"Bar",
			context.instance,
			"instance");
	}

	/**
	 * A call to migrate with assertions prior to migration that is expected to pass
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_withAssertionsPriorToMigration_withCurrentState_succeeds() throws
		AssertionFailedException,
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("finalState")
			.getResourceWithInitialState();

		State initialState = context.resource.getStates().get(1);
		Assertion initialStateAssertion1 = new TagAssertion(
			UUID.randomUUID(),
			0,
			"initialState");
		initialState.getAssertions().add(initialStateAssertion1);

		List<MigrationPlugin> migrationPlugins = new ArrayList<>();
		migrationPlugins.add(new SetTagMigrationPlugin(context.resource));

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.withCustomResourcePlugins(context.resourcePlugins)
			.withPluginManager(new PluginManagerImpl(
				Wildebeest.getPluginGroups(),
				migrationPlugins))
			.get();

		// Execute
		wildebeestApi.migrate(
			context.resource,
			context.instance,
			Optional.empty());

		TagAssertion initialStateAssertionResult = (TagAssertion)context.resource.getStates().get(1).getAssertions().get(0);
		assertEquals(initialStateAssertionResult.getCalledNTimes(), 1 );

		// Verify
		assertFakeInstance(
			"finalState",
			context.instance,
			"instance");
	}

	/**
	 * A call to migrate with assertions prior to migration that is expected to fail
	 *
	 * @since 4.0
	 */
	@Test
	public void migrate_withAssertionsPriorToMigration_withCurrentState_fails() throws
		IndeterminateStateException,
		InvalidStateSpecifiedException,
		MigrationFailedException,
		MigrationNotPossibleException,
		TargetNotSpecifiedException,
		UnknownStateSpecifiedException,
		MigrationInvalidStateException
	{
		// Setup
		PrintStream output = System.out;

		TestContext_SimpleFakeResource context = TestContext_SimpleFakeResource_Builder
			.create()
			.withDefaultTarget("finalState")
			.getResourceWithInitialState();

		State initialState = context.resource.getStates().get(1);
		Assertion initialStateAssertion1 = new TagAssertion(
			UUID.randomUUID(),
			0,
			"NewTag");
		initialState.getAssertions().add(initialStateAssertion1);

		List<MigrationPlugin> migrationPlugins = new ArrayList<>();
		migrationPlugins.add(new SetTagMigrationPlugin(context.resource));

		WildebeestApi wildebeestApi = Wildebeest
			.wildebeestApi(output)
			.withCustomResourcePlugins(context.resourcePlugins)
			.withPluginManager(new PluginManagerImpl(
				Wildebeest.getPluginGroups(),
				migrationPlugins))
			.get();
		try
		{
			// Execute
			wildebeestApi.migrate(
				context.resource,
				context.instance,
				Optional.empty());
			Assert.fail("The migration should not be successful. It is expected to throw an assertion failed exception.");
		}
		catch (AssertionFailedException asrFailedEx)
		{
			TagAssertion initialStateAssertionResult = (TagAssertion)context.resource.getStates().get(1).getAssertions().get(0);
			assertEquals(initialStateAssertionResult.getCalledNTimes(), 1 );

			FakeInstance fakeInstance = (FakeInstance)context.instance;
			Assert.assertTrue(
				"Assertion Failed Exception Encountered " + asrFailedEx,
				fakeInstance.getTag() != "NewTag");
		}
	}

	@Test
	public void validateResourceXml_invalidMySqlResource_fails()
	{
		// Execute and Verify
		this.validateResourceXml_fails("MySqlDatabase/database.wbresource.xml");
	}

	@Test
	public void validateXml_invalidSqlServerResource_fails()
	{
		// Execute and Verify
		this.validateResourceXml_fails("SqlServerDatabase/database.wbresource.xml");
	}

	@Test
	public void validateXml_validPostgreSqlResource_succeeds()
	{
		// Execute and Verify
		this.validateResourceXml_succeeds("PostgreSqlDatabase/database.wbresource.xml");
	}

	@Test
	public void validateXml_invalidResource_fails()
	{
		// Execute and Verify
		this.validateResourceXml_fails("InvalidXml/InvalidSampleResources.xml");
	}

	@Test
	public void validateXml_validMySqlInstance_succeeds()
	{
		// Execute and Verify
		this.validateInstanceXml_succeeds("MySqlDatabase/staging_db.wbinstance.xml");
	}

	@Test
	public void validateXml_invalidSqlServerInstance_fails()
	{
		// Execute and Verify
		this.validateInstanceXml_fails("SqlServerDatabase/staging_db.wbinstance.xml");
	}

	@Test
	public void validateXml_validPostgreSqlInstance_succeeds()
	{
		// Execute and Verify
		this.validateInstanceXml_succeeds("PostgreSqlDatabase/staging.wbinstance.xml");
	}

	@Test
	public void validateXml_invalidInstance_fails()
	{
		// Execute and Verify
		this.validateInstanceXml_fails("InvalidXml/InvalidInstanceSampleResources.xml");
	}

	/**
	 * A generic test that expects the supplied XML file to be valid according to the resource XSD schema.
	 *
	 * @param filename the resource XML file to validate.
	 * @since 4.0
	 */
	private void validateResourceXml_succeeds(
		String filename)
	{
		if (filename == null) throw new ArgumentNullException("filename");

		try
		{
			WildebeestApiImpl.validateResourceXml(this.readAllText(filename));
		}
		catch (XmlValidationException e)
		{
			Assert.fail(
				"the XML file was expected to be valid according to the resource schema, but was not: " +
					e.getMessage());
		}
	}

	/**
	 * A generic test that expects the supplied XML file to be INVALID according to the resource XSD schema.
	 *
	 * @param filename the resource XML file to validate.
	 * @since 4.0
	 */
	private void validateResourceXml_fails(
		String filename)
	{
		if (filename == null) throw new ArgumentNullException("filename");

		try
		{
			WildebeestApiImpl.validateResourceXml(this.readAllText(filename));
			Assert.fail("The XML file was expected to be invalid according to the resource schema, but it passed " +
				"validation");
		}
		catch (XmlValidationException e)
		{
			// Expected.
		}
	}

	/**
	 * A generic test that expects the supplied XML file to be valid according to the instance XSD schema.
	 *
	 * @param filename the instance XML file to validate.
	 * @since 4.0
	 */
	private void validateInstanceXml_succeeds(
		String filename)
	{
		if (filename == null) throw new ArgumentNullException("filename");

		try
		{
			WildebeestApiImpl.validateInstanceXml(this.readAllText(filename));
		}
		catch (XmlValidationException e)
		{
			Assert.fail(
				"the XML file was expected to be valid according to the schema, but was not: " +
					e.getMessage());
		}
	}

	/**
	 * A generic test that expects the supplied XML file to be INVALID according to the instance XSD schema.
	 *
	 * @param filename the instance XML file to validate.
	 * @since 4.0
	 */
	private void validateInstanceXml_fails(
		String filename)
	{
		if (filename == null) throw new ArgumentNullException("filename");

		try
		{
			WildebeestApiImpl.validateInstanceXml(this.readAllText(filename));
			Assert.fail("The XML file was expected to be invalid according to the instance schema, but it passed " +
				"validation");
		}
		catch (XmlValidationException e)
		{
			// Expected.
		}
	}

	private String readAllText(String filename)
	{
		if (filename == null) throw new ArgumentNullException("filename");

		final String result;
		try
		{
			result = new String(Files.readAllBytes(new File(filename).toPath()));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		return result;
	}
}
