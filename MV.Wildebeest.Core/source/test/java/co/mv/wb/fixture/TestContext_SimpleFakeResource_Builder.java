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

package co.mv.wb.fixture;

import co.mv.wb.Assertion;
import co.mv.wb.Instance;
import co.mv.wb.Resource;
import co.mv.wb.ResourcePlugin;
import co.mv.wb.ResourceType;
import co.mv.wb.State;
import co.mv.wb.framework.ArgumentNullException;
import co.mv.wb.plugin.base.ImmutableState;
import co.mv.wb.plugin.base.ResourceImpl;
import co.mv.wb.plugin.fake.FakeConstants;
import co.mv.wb.plugin.fake.FakeInstance;
import co.mv.wb.plugin.fake.FakeResourcePlugin;
import co.mv.wb.plugin.fake.SetTagMigration;
import co.mv.wb.plugin.fake.TagAssertion;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TestContext_SimpleFakeResource_Builder
{
	private String defaultTarget;

	private TestContext_SimpleFakeResource_Builder()
	{
		defaultTarget = null;
	}

	public static TestContext_SimpleFakeResource_Builder create()
	{
		return new TestContext_SimpleFakeResource_Builder();
	}

	public TestContext_SimpleFakeResource_Builder withDefaultTarget(
		String defaultTarget)
	{
		if (defaultTarget == null) throw new ArgumentNullException("defaultTarget");

		this.defaultTarget = defaultTarget;

		return this;
	}

	public TestContext_SimpleFakeResource getResourceWithNonExistantInitialState()
	{
		Map<ResourceType, ResourcePlugin> resourcePlugins = new HashMap<>();
		resourcePlugins.put(FakeConstants.Fake, new FakeResourcePlugin());

		Resource resource = new ResourceImpl(
			UUID.randomUUID(),
			FakeConstants.Fake,
			"MyResource",
			Optional.ofNullable(defaultTarget));

		// Foo State
		UUID fooStateId = UUID.randomUUID();
		State fooState = new ImmutableState(
			fooStateId,
			Optional.of("foo"));
		resource.getStates().add(fooState);

		// Bar State
		UUID barStateId = UUID.randomUUID();
		State barState = new ImmutableState(
			barStateId,
			Optional.of("bar"));
		resource.getStates().add(barState);

		// Migrate non-existant to Foo
		resource.getMigrations().add(new SetTagMigration(
			UUID.randomUUID(),
			Optional.empty(),
			Optional.of(fooStateId.toString()),
			"Foo"));

		// Migrate non-existant to Foo
		resource.getMigrations().add(new SetTagMigration(
			UUID.randomUUID(),
			Optional.empty(),
			Optional.of(barStateId.toString()),
			"Bar"));

		Instance instance = new FakeInstance();

		return new TestContext_SimpleFakeResource(
			resourcePlugins,
			resource,
			fooStateId,
			fooState,
			barStateId,
			barState,
			instance);
	}

	public TestContext_SimpleFakeResource getResourceWithInitialState()
	{
		Map<ResourceType, ResourcePlugin> resourcePlugins = new HashMap<>();
		resourcePlugins.put(FakeConstants.Fake, new FakeResourcePlugin());

		Resource resource = new ResourceImpl(
			UUID.randomUUID(),
			FakeConstants.Fake,
			"MyResource",
			Optional.ofNullable(defaultTarget));

		UUID finalStateId = UUID.randomUUID();
		State finalState = new ImmutableState(
			finalStateId,
			Optional.of("finalState"));
		resource.getStates().add(finalState);
		Assertion finalAssertion1 = new TagAssertion(
			UUID.randomUUID(),
			0,
			"finalState");
		finalState.getAssertions().add(finalAssertion1);

		UUID initialStateId = UUID.randomUUID();
		State initialState = new ImmutableState(
			initialStateId,
			Optional.of("initialState"));
		resource.getStates().add(initialState);

		resource.getMigrations().add(new SetTagMigration(
			UUID.randomUUID(),
			Optional.of(initialStateId.toString()),
			Optional.of(finalStateId.toString()),
			"finalState"));

		Instance instance = new FakeInstance(initialStateId);
		((FakeInstance)instance).setTag("initialState");
		return new TestContext_SimpleFakeResource(
			resourcePlugins,
			resource,
			finalStateId,
			finalState,
			initialStateId,
			initialState,
			instance);
	}
}
