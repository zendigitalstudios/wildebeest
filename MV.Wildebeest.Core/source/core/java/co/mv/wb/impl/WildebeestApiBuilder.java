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

import co.mv.wb.PluginManager;
import co.mv.wb.ResourcePlugin;
import co.mv.wb.ResourceType;
import co.mv.wb.Wildebeest;
import co.mv.wb.WildebeestApi;
import co.mv.wb.framework.ArgumentNullException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A fluent builder for WildebeestApi implementation.
 *
 * @since 4.0
 */
public class WildebeestApiBuilder
{
	private final WildebeestApiImpl wildebeestApi;
	private final Map<ResourceType, ResourcePlugin> resourcePlugins;
	private final PluginManager pluginManager;

	/**
	 * Creates a new WildebeestApiBuilder with the specified PrintStream for Wildebeest to output to.
	 *
	 * @param output the PrintStream that Wildebeest should output to.
	 * @return a new WildebeestApiBuilder.
	 * @since 4.0
	 */
	public static WildebeestApiBuilder create(
		PrintStream output)
	{
		if (output == null) throw new ArgumentNullException("output");

		return new WildebeestApiBuilder(
			new WildebeestApiImpl(output),
			new HashMap<>(),
			new PluginManagerImpl(
				new ArrayList<>(),
				new ArrayList<>()));
	}

	private WildebeestApiBuilder(
		WildebeestApiImpl wildebeestApi,
		Map<ResourceType, ResourcePlugin> resourcePlugins,
		PluginManager pluginManager)
	{
		if (wildebeestApi == null) throw new ArgumentNullException("wildebeestApi");
		if (resourcePlugins == null) throw new ArgumentNullException("resourcePlugins");
		if (pluginManager == null) throw new ArgumentNullException("pluginManager");

		this.wildebeestApi = wildebeestApi;
		this.resourcePlugins = resourcePlugins;
		this.pluginManager = pluginManager;
	}

	/**
	 * Fluently adds the factory-preset {@link ResourcePlugin}'s to the builder.  A new builder is returned and the
	 * original builder is left unmutated.
	 *
	 * @return a new WildebeestApiBuilder with all the state of the source builder plus
	 * the factory-preset ResourcePlugin's registered.
	 */
	public WildebeestApiBuilder withFactoryResourcePlugins()
	{
		Map<ResourceType, ResourcePlugin> updatedResourcePlugins = new HashMap<>(this.resourcePlugins);
		updatedResourcePlugins.putAll(Wildebeest.getResourcePlugins());

		return new WildebeestApiBuilder(
			wildebeestApi,
			updatedResourcePlugins,
			pluginManager);
	}

	/**
	 * Fluently adds a {@link PluginManager} with the factory-preset plugin groups and migration plugins registered.
	 *
	 * @return a new WildebeestApiBuilder with a PluginManager configured with the
	 * factory-preset plugin groups and migration plugins registered.
	 */
	public WildebeestApiBuilder withFactoryPluginManager()
	{
		return this.withPluginManager(new PluginManagerImpl(
			Wildebeest.getPluginGroups(),
			Wildebeest.getMigrationPlugins(wildebeestApi)));
	}

	/**
	 * Fluently adds a {@link PluginManager} to the builder.  A new builder instance is returned with the supplied
	 * PluginManager added, and the original builder is left unmutated.
	 *
	 * @param pluginManager the PluginManager to add to the builder.
	 * @return a new WildebeestApiBuilder with the state of the source builder plus the
	 * supplied PluginManager.
	 */
	public WildebeestApiBuilder withPluginManager(PluginManager pluginManager)
	{
		if (pluginManager == null) throw new ArgumentNullException("pluginManager");

		return new WildebeestApiBuilder(
			wildebeestApi,
			resourcePlugins,
			pluginManager);
	}

	/**
	 * Builds a new {@link WildebeestApi} with the plugins that were registered to this builder.
	 *
	 * @return a new WildebeestApi instance with the plugins that were registered to
	 * this builder.
	 */
	public WildebeestApi get()
	{
		wildebeestApi.setResourcePlugins(resourcePlugins);
		wildebeestApi.setPluginManager(pluginManager);

		return wildebeestApi;
	}

	public WildebeestApiBuilder withCustomResourcePlugins(Map<ResourceType,ResourcePlugin> resourcePlugins)
	{
		return new WildebeestApiBuilder(
			wildebeestApi,
			resourcePlugins,
			pluginManager);
	}

}
